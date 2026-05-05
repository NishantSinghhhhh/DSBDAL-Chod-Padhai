import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * MapReduce program to compute average temperature, dew point, and wind speed
 * from a weather data file.
 *
 * Sample input format (one record per line):
 *   2024-01-01 25.3 15.2 10.5
 *   2024-01-02 27.1 16.0  8.3
 *
 * Each line: date  temperature  dew_point  wind_speed
 *
 * Sample output:
 *   dew_point     15.61
 *   temperature   24.78
 *   wind_speed    12.34
 */
public class Weather {

    /* =========================================================
       MAPPER
       Input  : (LineNumber, Text record)
       Output : (FieldName, FieldValue) for each numeric field
       ========================================================= */
    public static class WeatherMapper
            extends Mapper<Object, Text, Text, DoubleWritable> {

        private Text fieldName = new Text();
        private DoubleWritable fieldValue = new DoubleWritable();

        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.isEmpty()) return;             // skip empty lines

            // Each line: DATE TEMP DEW_POINT WIND_SPEED
            String[] tokens = line.split("\\s+");
            if (tokens.length < 4) return;          // malformed line, skip

            try {
                double temp  = Double.parseDouble(tokens[1]);
                double dew   = Double.parseDouble(tokens[2]);
                double wind  = Double.parseDouble(tokens[3]);

                // Emit (field_name, value) for each numeric measurement
                fieldName.set("temperature");
                fieldValue.set(temp);
                context.write(fieldName, fieldValue);

                fieldName.set("dew_point");
                fieldValue.set(dew);
                context.write(fieldName, fieldValue);

                fieldName.set("wind_speed");
                fieldValue.set(wind);
                context.write(fieldName, fieldValue);

            } catch (NumberFormatException e) {
                // Skip lines with non-numeric values
                return;
            }
        }
    }

    /* =========================================================
       REDUCER
       Input  : (FieldName, [v1, v2, v3, ...])
       Output : (FieldName, average)
       ========================================================= */
    public static class AvgReducer
            extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();

        @Override
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                throws IOException, InterruptedException {

            double sum = 0.0;
            int count = 0;
            for (DoubleWritable v : values) {
                sum += v.get();
                count++;
            }

            if (count > 0) {
                double avg = sum / count;
                result.set(avg);
                context.write(key, result);         // emit (field, average)
            }
        }
    }

    /* =========================================================
       DRIVER
       ========================================================= */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: Weather <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "weather averages");

        job.setJarByClass(Weather.class);
        job.setMapperClass(WeatherMapper.class);
        // NOTE: We intentionally do NOT use the AvgReducer as a Combiner.
        // Average is NOT associative -- average of averages != true average.
        job.setReducerClass(AvgReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
