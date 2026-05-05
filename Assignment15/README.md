# Assignment 15 — MapReduce on Weather Data

Notebook: `Assignment_15_Complete.ipynb`
Files: `Weather.java`, `sample_weather.txt`
Focus: applying Hadoop MapReduce to numeric time-series data — computing averages, min/max, and per-day statistics from weather observations.

---

## 1. The Problem

Each line of the weather dataset is a daily observation:

```
2024-01-01 25.3 15.2 10.5
2024-01-02 27.1 16.0  8.3
2024-01-03 22.8 14.5 12.1
```

Format: `date  temperature  dew_point  wind_speed`

**Goal:** compute the **average** of temperature, dew point, and wind speed across the entire dataset (or per month, per year, etc.). With years of daily data from thousands of stations, the dataset can easily be terabytes — out of reach for a single-machine pandas pipeline. MapReduce is the right tool.

---

## 2. Why Weather Data Is a Big Data Use Case

- **Volume**: thousands of weather stations × decades of history × multiple sensors per station = billions of records.
- **Sequential structure**: each observation is independent → embarrassingly parallel.
- **Repeated analysis**: scientists ask different questions of the same data over and over.
- **Long retention**: historical weather data is precious; once collected, it's never thrown away.

This kind of dataset was the historical motivation for projects like the **NOAA Integrated Surface Database** and was the original driving use case for the Tom White Hadoop book.

---

## 3. Numeric Aggregation in MapReduce

### 3.1 Computing a Sum
**Mapper:**
- Parse the line, extract the value of interest (e.g., temperature).
- Emit `(constant_key, temperature)`.

**Reducer:**
- Receive all temperatures.
- Sum them.
- Emit `(key, total)`.

### 3.2 Computing a Count
- Mapper emits `(key, 1)` for every record.
- Reducer sums.

### 3.3 Computing an Average
The clean way: emit a custom `(sum, count)` pair from the mapper, then sum both in the reducer and divide at the end.

This is more robust than emitting `(key, value)` and computing `sum / count` in the reducer because:

- It allows the same code to work as a **combiner** (sums of `(sum, count)` are still `(sum, count)`).
- Naïvely emitting raw values would prevent combiner optimization, since average is *not* commutative or associative.

### 3.4 Min / Max
- Mapper emits `(key, value)`.
- Reducer iterates and tracks the running min or max.
- Combiner can be the same code — min and max are both commutative and associative.

### 3.5 Variance / Standard Deviation
Use the **online algorithm** (Welford's) or the simpler **two-pass** approach. For combiner-friendly computation, emit triples `(count, sum, sum_of_squares)`:

- Variance = `(sum_of_squares / n) − (sum / n)²`.
- Tuples are addable across mappers, so a combiner is straightforward.

---

## 4. Per-Day / Per-Month Aggregation

By changing the *key* the mapper emits, we change the granularity of the aggregation:

| Key emitted | Result |
|---|---|
| Constant ("global") | One overall average |
| Date (`2024-01-01`) | One average per day (no-op for daily data) |
| Year-Month (`2024-01`) | Monthly averages |
| Year (`2024`) | Yearly averages |
| Station ID | Per-station averages |

The reducer logic doesn't change — only the key choice in the mapper.

---

## 5. The General Aggregation Pattern

Counting words. Counting log levels. Averaging temperatures. Summing sales. Top-K errors. They're all the same MapReduce shape:

1. **Mapper extracts** what to group by (the key) and what to summarize (the value).
2. **Combiner pre-aggregates** locally if possible.
3. **Shuffle** routes by key.
4. **Reducer aggregates** all values for each key.
5. **Output** is `(group, summary)` pairs.

This pattern is so dominant that frameworks like Hive and Spark SQL collapse it into a single SQL `GROUP BY ... AGGREGATE` clause.

---

## 6. Custom `Writable` Types

For weather analysis we need to emit composite values like `(sum, count)` or `(sum, sum_sq, count)`. Java doesn't have native tuples, so we write a **custom Writable**:

```java
public class WeatherStats implements Writable {
    double sum;
    long count;

    public void write(DataOutput out) throws IOException {
        out.writeDouble(sum);
        out.writeLong(count);
    }

    public void readFields(DataInput in) throws IOException {
        sum = in.readDouble();
        count = in.readLong();
    }
}
```

Hadoop calls `write` to serialize the object before sending it across the network and `readFields` to deserialize it on the other side. Implementing `WritableComparable` (instead of just `Writable`) lets Hadoop use the type as a *key*, which requires defining `compareTo` and `equals`/`hashCode`.

---

## 7. Combiner Pitfalls for Averages

Naïvely setting the reducer as the combiner doesn't always work:

```java
// WRONG for averages
public void reduce(Text key, Iterable<DoubleWritable> values, Context ctx) {
    double sum = 0; int count = 0;
    for (DoubleWritable v : values) { sum += v.get(); count++; }
    ctx.write(key, new DoubleWritable(sum / count));
}
```

If this runs as a combiner, the reducer receives **already-averaged values**, then averages those — the result is wrong unless every combiner saw the same number of records (rare).

**Right approach**: emit `(sum, count)` from both mapper and combiner; the reducer divides only at the end.

---

## 8. Time-Series Considerations

Weather data is **time-series**, which raises questions MapReduce alone doesn't address well:

- **Order matters** for some operations (rolling averages, anomaly detection).
- **Missing dates** distort averages — handle gaps explicitly.
- **Seasonality** (annual cycles) is real signal — don't average it away.
- **Spatial correlation** — nearby stations have correlated weather; modelling can exploit this.

For pure aggregation MapReduce is fine; for sequence-aware analysis (rolling windows, ARIMA, Kalman filters), use Spark Streaming or specialized time-series databases (InfluxDB, TimescaleDB).

---

## 9. The Reduce-Side vs Map-Side Distinction

**Map-side aggregation** uses combiners to reduce data before shuffling. Faster but requires commutative, associative operations.

**Reduce-side aggregation** does all the work in the reducer. Always correct but moves more data.

For sums, counts, min, max — use map-side (combiners). For averages — use map-side with `(sum, count)` tuples. For percentiles, exact distinct counts, sorted operations — reduce-side only.

---

## 10. Modern Alternatives

| Tool | Notes |
|---|---|
| **Spark** | Same paradigm but in-memory; 10–100× faster for iterative jobs |
| **Hive** | SQL `GROUP BY` over Hadoop — much less code |
| **Spark SQL** | SQL on Spark; widely used in production |
| **Pandas / Dask** | Single-machine or distributed Python; great for medium data |
| **InfluxDB / TimescaleDB** | Purpose-built for time-series; native rollups and downsampling |
| **BigQuery / Snowflake** | Cloud SQL warehouses with petabyte scale |

For pure educational MapReduce, the WeatherCount-style problem remains a great example of how the paradigm handles numeric aggregation cleanly and at scale.

---

## 11. Viva Questions (40)

### A. Big Data and Hadoop Fundamentals

**Q1. What is Big Data and why is weather data an example?**
Big Data refers to datasets too large or complex for a single machine. Weather data qualifies because of its **volume** (thousands of stations × decades × many sensors), **velocity** (real-time observations), and **veracity** issues (missing observations, instrument errors). A multi-decade global dataset is easily terabytes — out of reach for pandas, perfect for distributed processing.

**Q2. What is Hadoop and why was it created?**
An open-source framework for distributed storage and processing of large datasets. Created at Yahoo in 2006 (inspired by Google's GFS and MapReduce papers) to solve the problem of indexing the web at scale on commodity hardware.

**Q3. What is HDFS and what makes it suitable for weather data?**
HDFS is Hadoop's distributed file system, splitting files into 128 MB blocks replicated 3× across nodes. Suitable for weather data because it's optimized for **sequential reads of large files**, weather observations are **append-only** (matches HDFS's write model), and historical archives benefit from HDFS's low cost-per-TB.

**Q4. What is YARN's role?**
**Yet Another Resource Negotiator** — Hadoop's cluster resource manager. Schedules tasks on worker nodes, allocates CPU/memory, and handles failover. Decouples resource management from MapReduce so other engines (Spark, Tez) can share the same cluster.

**Q5. What is MapReduce?**
A programming model for parallel batch processing. Two phases — **Map** (extract and transform records independently) and **Reduce** (aggregate by key). The framework handles distribution, fault tolerance, and shuffle automatically. The user writes only the per-record logic.

### B. The Weather MapReduce Job

**Q6. How would you compute the average temperature using MapReduce?**
Mapper: parse each line, extract the temperature, emit a fixed key (or any chosen group key) with a `(sum, count)` value of `(temp, 1)`. Reducer: sum sums and counts separately, then emit `(group, sum/count)` at the end. This decomposition is essential to make the operation **combiner-safe**.

**Q7. Why can't you just emit raw temperatures and average in the reducer?**
Because then no combiner is possible — averages aren't commutative or associative. Without a combiner, every observation crosses the network. With the `(sum, count)` decomposition, combiners drastically reduce shuffle traffic.

**Q8. How would you compute per-month averages?**
Change the mapper key from a constant to the year-month substring of the date (e.g., `"2024-01"`). The reducer logic stays identical. Output is one average per month.

**Q9. How would you compute min and max?**
Mapper emits `(key, value)`. Reducer scans the iterator and tracks the running min or max, emitting it at the end. Min and max are both commutative and associative, so the reducer can be reused as the combiner.

**Q10. How would you compute standard deviation?**
Emit triples `(count, sum, sum_of_squares)` from the mapper. The combiner / reducer adds these triples element-wise. At the end, compute `variance = sum_sq/n − (sum/n)²` and `std = sqrt(variance)`. The triple is combiner-safe.

**Q11. How do you handle missing values in the mapper?**
Filter them out — if a value is `NaN`, missing, or `-9999` (a common placeholder in weather data), don't emit anything. Optionally, increment a Counter to track how many missing observations were seen.

### C. Custom Writables

**Q12. What is a custom Writable and why might you need one?**
A user-defined class implementing the `Writable` interface so it can be serialized between mapper and reducer. You need one when your value (or key) isn't a single primitive — for example, emitting `(sum, count)` pairs requires a `SumCountWritable`.

**Q13. What two methods must a Writable implement?**
- `write(DataOutput)` — serializes the object to a binary stream.
- `readFields(DataInput)` — deserializes from a binary stream.
The methods must be exact mirrors of each other and read/write fields in the same order.

**Q14. What's the difference between `Writable` and `WritableComparable`?**
`Writable` is for values that just need to be serialized. `WritableComparable` extends `Writable` and adds `compareTo`, required for any type used as a **key** because Hadoop sorts keys during the shuffle phase.

**Q15. Why use `Writable` types instead of `java.io.Serializable`?**
Hadoop's `Writable` is **much faster and more compact** than Java's standard serialization, which is critical when shuffling petabytes between mappers and reducers. Java serialization includes class metadata in every record; Writable doesn't.

### D. Combiners and Aggregation Correctness

**Q16. What is a Combiner and when does it help?**
A "mini-reducer" that runs on each mapper's output before shuffling. It pre-aggregates locally, dramatically reducing the volume of data sent over the network. Helps for any commutative, associative aggregation (sum, count, min, max). For weather averages, the combiner reduces gigabytes of values to a small number of `(sum, count)` pairs per mapper.

**Q17. Can a combiner change the result of a job?**
For commutative/associative operations: no. For others (like averaging raw values): yes — the result becomes wrong. Always design your value type to make the operation combiner-safe (e.g., `(sum, count)` for averages).

**Q18. Why must combiner operations be commutative and associative?**
Because Hadoop may run the combiner zero, one, or many times in any order. The output must be identical regardless. Sum, count, min, max satisfy this; raw average doesn't (averaging averages of different-sized groups gives the wrong answer).

**Q19. Can the same class serve as both reducer and combiner?**
Often yes — for sum, count, min, max with consistent value types. For averages with `(sum, count)` tuples, the combiner aggregates tuples and emits a tuple, while the reducer aggregates tuples and emits a single number. They differ only in the final step, so usually they're different classes.

### E. The Bigger Picture

**Q20. What is the general MapReduce pattern for aggregation?**
1. Mapper extracts the **group key** and the **value to aggregate**.
2. Combiner pre-aggregates locally (when valid).
3. Shuffle sorts and routes by key.
4. Reducer combines all values for each key into a single summary.
5. Output is one record per group.
This pattern underlies word count, log analysis, weather averages, sales totals, top-K — anywhere SQL would say `GROUP BY`.

**Q21. How does MapReduce relate to SQL `GROUP BY`?**
A `GROUP BY` in SQL is essentially a MapReduce in disguise. Hive and Spark SQL compile `GROUP BY` directly into the map-shuffle-reduce pattern. Hadoop and SQL engines are different surface APIs over the same underlying paradigm.

**Q22. What is data locality and why does it matter for weather data?**
Weather datasets are big — terabytes of observations. Without locality, every map task pulls its block over the network. Hadoop's scheduler tries to run each map on the DataNode that already has the block locally — saving most of the network I/O. Without it, MapReduce wouldn't scale.

**Q23. What is the role of partitioning?**
The partitioner decides which reducer each key goes to. Default `HashPartitioner` distributes keys uniformly. For weather data, you could partition by station ID so all observations from a single station land in one reducer — useful if you want per-station summaries without shuffling.

### F. Time-Series Specifics

**Q24. What special properties does time-series data have?**
- **Order matters** for some computations (running averages, change detection).
- **Equally spaced or not?** — daily observations are evenly spaced; sensor readings often aren't.
- **Missing data** is common — sensors fail, observations are dropped.
- **Seasonality** — annual, weekly, daily cycles must be modelled, not averaged away.
- **Trend** — long-term direction may matter more than short-term fluctuation.

**Q25. How would you compute a 7-day rolling average in MapReduce?**
Tricky because it requires knowing neighboring records, which mappers process independently. Approach:
1. Map each record to multiple `(7 windows containing this date, value)` pairs.
2. Reduce by window, computing the average.
This generates 7× the data but parallelizes well. In Spark or Flink, the streaming windowing API does this directly.

**Q26. How would you detect anomalies in weather observations?**
Compute mean and std per `(station, month)` group. Then for each observation, compute `z = (value − mean) / std`. Flag `|z| > 3` as anomalous. Two MapReduce passes — first to compute group statistics, second to score each observation against them.

**Q27. Can MapReduce process streaming weather data?**
Not natively — MapReduce is batch. For streaming use Apache Flink or Spark Structured Streaming. They process micro-batches of new observations every few seconds and update aggregates in near-real time.

### G. Handling Real-World Issues

**Q28. How do you handle gaps (missing days) in weather data?**
- Treat the missing date as missing in the mapper — don't emit a record for it.
- For interpolation, post-process the output to fill gaps with the average of neighbors.
- For more sophisticated handling, use a streaming engine that can reason about expected vs received records.

**Q29. How do you handle different units across stations?**
Pre-process to normalize units (Celsius vs Fahrenheit, m/s vs mph). The mapper checks the source format and converts before emitting. Or include the unit in the key/value and let the reducer handle conversion.

**Q30. What if a sensor's clock is wrong by hours?**
- Detect via a sanity check (e.g., daily observation at 4 AM is suspicious).
- Correct with a mapping table of known offsets.
- Flag and skip if uncorrectable.
Distributed time-series analysis is often as much about data quality as about computation.

### H. Comparisons & Modern Approaches

**Q31. How would you do this analysis in Apache Spark?**
```scala
val rdd = sc.textFile("weather.txt")
  .map(line => line.split("\\s+"))
  .map(arr => (arr(0).take(7), arr(1).toDouble))
val avg = rdd.aggregateByKey((0.0, 0))(
  (acc, v) => (acc._1 + v, acc._2 + 1),
  (a, b) => (a._1 + b._1, a._2 + b._2)
).mapValues { case (sum, n) => sum / n }
```
Same algorithm, in-memory, far less boilerplate than the Java version.

**Q32. How would you do this with Hive?**
```sql
SELECT SUBSTR(date, 1, 7) AS month, AVG(temperature)
FROM weather
GROUP BY SUBSTR(date, 1, 7);
```
Hive compiles this into a MapReduce or Spark job. Far easier to write and maintain than raw Java MapReduce.

**Q33. How would you do this in pandas (single machine)?**
```python
df['month'] = df['date'].str[:7]
df.groupby('month')['temperature'].mean()
```
Works perfectly for datasets that fit in RAM. Beyond that you need Spark, Dask, or Hadoop.

**Q34. When is MapReduce still preferred over Spark for this workload?**
- Truly massive datasets where memory isn't enough for any practical Spark partition.
- Existing Hadoop infrastructure that already runs MapReduce jobs in production.
- Cost: Spark's memory-heavy model is expensive at petabyte scale.
- Educational settings — MapReduce is conceptually simpler.

### I. Practical & Code-Specific

**Q35. Can a MapReduce program have multiple reducers?**
Yes. Set `job.setNumReduceTasks(n)`. The partitioner distributes keys across the n reducers; each writes its own output file. For per-month averages with a small number of months, fewer reducers is fine. For a per-station job over thousands of stations, many reducers parallelize the work.

**Q36. What is the output format of the weather job?**
By default, plain text — one `(key, value)` pair per line, tab-separated, with the value formatted via `toString()` of the Writable. For numeric output, the default is fine. For complex outputs, override the OutputFormat or `toString()` of your Writable.

**Q37. How do you debug a weather MapReduce job?**
- Test the mapper and reducer logic in unit tests with **MRUnit**.
- Run on a small sample in **local mode** before submitting to the cluster.
- Use **Counters** to track parse failures and skipped records.
- Inspect **task logs** in the YARN UI for stack traces.

**Q38. How do you deal with very wide rows (many sensors per observation)?**
- Either flatten to one record per sensor per day (long form).
- Or keep as wide form and emit one record per sensor of interest.
- The choice depends on the analysis: long form is more flexible; wide form is more compact.

**Q39. How do you partition weather data effectively?**
Common partitioning schemes:
- By **time** (year-month-day directories) — speeds up time-bounded queries.
- By **station** — speeds up per-station analysis.
- By **region** — speeds up regional analysis.
Partitioning is invisible to MapReduce (it just reads the files) but dramatically speeds up Hive/Spark queries that filter on the partition key.

**Q40. How would you build a production weather-analytics pipeline?**
1. **Ingest**: Kafka or HDFS direct uploads from stations.
2. **Storage**: Parquet files on HDFS or S3, partitioned by year/month.
3. **Compute**: Spark for batch, Spark Streaming for real-time.
4. **Catalog**: Hive metastore so SQL tools can query.
5. **Serve**: pre-aggregated rollups in a time-series DB (InfluxDB) or warehouse (BigQuery).
6. **Visualize**: Grafana or Superset dashboards.
7. **Alert**: trigger PagerDuty when extreme weather is detected.
End-to-end, this can ingest gigabytes per minute and answer queries in seconds.
