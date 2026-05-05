import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * MapReduce program to process a system log file.
 * Counts the number of occurrences of each log level (INFO, WARN, ERROR, DEBUG).
 *
 * Sample input format (one log entry per line):
 *   2024-01-01 10:00:30 INFO  User login successful
 *   2024-01-01 10:01:00 ERROR Database connection failed
 *   2024-01-01 10:01:30 WARN  Cache miss for key user_123
 *
 * Sample output (one (level, count) pair per line):
 *   DEBUG  20
 *   ERROR  20
 *   INFO   120
 *   WARN   40
 */
public class LogProcessor {

    /* =========================================================
       MAPPER
       Input  : (LineNumber, Text logLine)
       Output : (LogLevel, 1)
       ========================================================= */
    public static class LogMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable ONE = new IntWritable(1);
        private Text level = new Text();

        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.isEmpty()) return;             // skip empty lines

            // Each log line: "DATE TIME LEVEL MESSAGE..."
            // Splitting by whitespace gives at least 3 tokens
            String[] tokens = line.split("\\s+");
            if (tokens.length < 3) return;          // malformed line, skip

            String logLevel = tokens[2].toUpperCase();

            // Only count standard log levels
            if (logLevel.equals("INFO")  ||
                logLevel.equals("WARN")  ||
                logLevel.equals("ERROR") ||
                logLevel.equals("DEBUG")) {
                level.set(logLevel);
                context.write(level, ONE);          // emit (LEVEL, 1)
            }
        }
    }

    /* =========================================================
       REDUCER
       Input  : (LogLevel, [1, 1, 1, ...])
       Output : (LogLevel, totalCount)
       ========================================================= */
    public static class SumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);             // emit (LEVEL, totalCount)
        }
    }

    /* =========================================================
       DRIVER
       ========================================================= */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: LogProcessor <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "log level count");

        job.setJarByClass(LogProcessor.class);
        job.setMapperClass(LogMapper.class);
        job.setCombinerClass(SumReducer.class);     // local pre-aggregation
        job.setReducerClass(SumReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
