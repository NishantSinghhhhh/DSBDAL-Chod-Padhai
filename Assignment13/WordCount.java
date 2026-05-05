import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        // Reuse one IntWritable("1") for every emission - saves memory
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            // Split the line into tokens (whitespace by default)
            StringTokenizer itr = new StringTokenizer(value.toString());

            while (itr.hasMoreTokens()) {
                // Lowercase + clean punctuation so "Hadoop" and "hadoop." count together
                String token = itr.nextToken().toLowerCase().replaceAll("[^a-zA-Z]", "");
                if (!token.isEmpty()) {
                    word.set(token);
                    context.write(word, one);   // emit (word, 1)
                }
            }
        }
    }

    /* =========================================================
       REDUCER
       Input  : (Word, [1, 1, 1, ...])  - all 1s for each word
       Output : (Word, totalCount)
       ========================================================= */
    public static class IntSumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();           // accumulate all 1s
            }
            result.set(sum);
            context.write(key, result);     // emit (word, total_count)
        }
    }


    public static void main(String[] args) throws Exception {
        // Sanity check
        if (args.length != 2) {
            System.err.println("Usage: WordCount <input path> <output path>");
            System.exit(-1);
        }

        // Standard Hadoop configuration object
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");

        // Tell Hadoop which JAR contains our code
        job.setJarByClass(WordCount.class);

        // Wire up the Mapper/Combiner/Reducer
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);   // local pre-aggregation
        job.setReducerClass(IntSumReducer.class);

        // Output type declaration
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Input/output paths from command-line args
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // Submit and wait
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}