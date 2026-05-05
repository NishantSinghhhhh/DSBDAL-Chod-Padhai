# Assignment 14 — MapReduce Log File Processing

Notebook: `Assignment_14_Complete.ipynb`
Files: `LogProcessor.java`, `system.log`
Focus: applying Hadoop MapReduce to a real Big Data use case — system log analysis. Counting log levels (ERROR, WARN, INFO), identifying frequent error patterns, summarizing application behavior at scale.

---

## 1. Why Logs Are a Big Data Use Case

Production systems generate **enormous** quantities of log data:

- Web servers log every request — millions per day.
- Application servers log every operation, exception, and slow query.
- Security systems log every authentication attempt.
- Distributed systems log inter-service communication, replication events, and failovers.

A medium-traffic site can produce gigabytes of log data per hour. Analyzing all of this on a single machine with `grep` and `awk` works for prototypes, but breaks down at production scale. MapReduce is purpose-built for this:

- Logs are **line-oriented** — each line is independent → embarrassingly parallel.
- Logs are **append-only** — perfect for HDFS's sequential-write model.
- Common queries are **aggregations** — counts, sums, top-K — natural fits for `reduce`.
- Logs grow continuously — must be processable as a stream of new files.

---

## 2. Anatomy of a Log Line

A typical log line:

```
2024-04-12 10:23:45 ERROR [com.acme.payment.PaymentService] - Connection refused: localhost:5432
```

Fields:

- **Timestamp** — when the event happened.
- **Log level** — DEBUG, INFO, WARN, ERROR, FATAL.
- **Logger / Component** — which class or module produced the log.
- **Message** — human-readable description, sometimes with structured fields.
- Optional: thread name, request ID, correlation ID, MDC fields.

Variations are huge — each language, framework, and team has its own conventions. Real-world log analysis often starts with a **regex parser** that extracts structured fields from raw lines.

---

## 3. Common Log Levels

Standardized severity tiers:

| Level | When |
|---|---|
| **TRACE** | Extremely detailed — function entry/exit, used for tracing flow |
| **DEBUG** | Diagnostic info useful during development |
| **INFO** | Normal operational events ("user logged in") |
| **WARN** | Something unexpected but the application is still working |
| **ERROR** | Operation failed; user-visible impact possible |
| **FATAL** | Application or component cannot continue; usually triggers shutdown |

Production typically logs INFO and above; DEBUG/TRACE are enabled selectively for troubleshooting.

---

## 4. Common Log-Analysis Questions

The kinds of questions analysts ask of log data:

1. **How many ERROR messages per hour?** — health monitoring.
2. **Which exception types appear most often?** — top-K error analysis.
3. **What is the request rate over time?** — capacity planning.
4. **How many unique users hit the API yesterday?** — usage metrics.
5. **What is the p99 response time?** — performance SLA tracking.
6. **Are there ERROR spikes correlated with deploys?** — change correlation.
7. **Which IPs make the most requests?** — security / abuse detection.
8. **What is the geographic distribution of errors?** — regional incidents.

Each of these is a `MapReduce` workload: parse each line in the map phase, aggregate by key in the reduce phase.

---

## 5. The MapReduce Pattern for Log Analysis

### 5.1 Counting by Log Level

**Mapper:**
- Parse the line.
- Extract the log level (e.g., "ERROR").
- Emit `(log_level, 1)`.

**Reducer:**
- Receive all 1s for each level.
- Sum them.
- Emit `(level, total_count)`.

This is structurally identical to WordCount — the only change is *what* the mapper extracts as the key.

### 5.2 Counting by Hour or Component

Just change what the mapper emits as the key:

- **Hourly** — extract `YYYY-MM-DD HH` from the timestamp.
- **Per component** — extract the logger / class name.
- **Per error type** — extract the exception class from the message.

The reducer logic stays the same.

### 5.3 Top-K Errors

A two-stage MapReduce:

- **Stage 1**: count each error type (same as level counting).
- **Stage 2**: a single reducer that maintains a top-K heap and emits the K most frequent errors.

This pattern is widely used for "top users", "top URLs", "top errors", etc.

### 5.4 Joining Logs with Reference Data

Sometimes log analysis requires joining log records with reference data (e.g., user_id → user_name mapping).

- **Map-side join** (broadcast): if the reference data is small, distribute it to all mappers.
- **Reduce-side join**: emit `(key, log_record)` and `(key, reference_record)` from two mappers, join on the same key in the reducer.

---

## 6. Counters in MapReduce

Hadoop **Counters** let mapper and reducer code increment named counters during the job. After the job, counters are reported in the job summary.

Used for:

- **Diagnostic logging** — count malformed lines, parse errors, skipped records.
- **Sanity checks** — total input records seen, total output emitted.
- **Custom metrics** — number of ERROR records, number of WARN records.

Counters are aggregated across all tasks automatically. They're better than `System.out.println` for distributed jobs because logs from many machines are scattered across the cluster.

---

## 7. Patterns Specific to Log Processing

### 7.1 Parsing Robustness
Real logs are messy. The mapper must:

- Skip empty lines.
- Skip malformed lines (logged via a counter).
- Handle multi-line stack traces (one log "event" can span dozens of lines).
- Be defensive about regex failures — never throw an unhandled exception.

### 7.2 Multi-Line Records
Stack traces are multi-line. A naïve mapper that processes one line at a time will see each stack-trace line as a separate event. Solutions:

- **Pre-process** logs to merge multi-line records into single lines.
- **Custom InputFormat** that knows how to identify record boundaries.
- **Use a line-prefix heuristic** (a line that doesn't start with a timestamp is a continuation).

### 7.3 Time Windows
Logs are time-series data. Common patterns:

- **Tumbling windows** — non-overlapping fixed periods (5-minute buckets).
- **Sliding windows** — overlapping windows that slide forward.
- **Session windows** — gap-based, used for user-activity analytics.

For a one-shot batch job, you typically use tumbling windows: emit `(timestamp_truncated_to_5_minutes, 1)` from the mapper.

---

## 8. Modern Log-Analysis Stacks

While MapReduce works for log analysis, modern teams typically use:

- **ELK / Elastic Stack** — Elasticsearch for storage, Logstash/Beats for ingestion, Kibana for visualization. Real-time search, dashboards, alerts.
- **Splunk** — commercial competitor. Powerful query language (SPL).
- **Loki + Grafana** — Grafana Labs' open-source competitor; lightweight.
- **Datadog Logs / New Relic / Sumo Logic** — managed SaaS options.
- **OpenTelemetry** — modern open standard for logs, traces, and metrics.

These tools provide near-real-time querying, alerting, and visualization that batch MapReduce can't match. But MapReduce/Spark remains the right tool for **historical batch analysis** of years of archived logs.

---

## 9. Strengths & Limitations of MapReduce for Logs

### Strengths
- Linear scalability — twice as much hardware processes data twice as fast.
- Resilient — node failures don't crash the job.
- Cheap to store years of logs in HDFS.
- Familiar paradigm — map → reduce maps directly to "extract → aggregate".

### Limitations
- **Latency** — minutes to hours; not suitable for real-time alerting.
- **No complex event processing** — multi-event correlation requires careful Reducer design.
- **No interactive querying** — you commit to a job, wait, and read results.
- **Verbose Java** — Hive and Spark SQL provide far more concise APIs.

---

## 10. Viva Questions (40)

### A. Log Analysis Concepts

**Q1. What is log analysis and why is it a Big Data problem?**
Log analysis is the process of inspecting application, system, and security logs to extract operational insights, debug issues, monitor performance, and detect security anomalies. It's a Big Data problem because production systems generate gigabytes per day, far beyond what `grep` on a single machine can handle interactively. The volume requires distributed storage (HDFS) and distributed processing (MapReduce, Spark).

**Q2. What are common log levels and what do they mean?**
- **TRACE** — extremely detailed flow information.
- **DEBUG** — diagnostic info for developers.
- **INFO** — normal operational events.
- **WARN** — something unexpected but recoverable.
- **ERROR** — operation failed; possible user impact.
- **FATAL** — cannot continue; usually triggers shutdown.
Production typically logs INFO and above.

**Q3. What information does a typical log line contain?**
Timestamp, log level, source component (logger or class name), message, and optionally thread name, request ID, correlation ID, user ID. Each framework has its own format, so robust analyzers parse with regex.

**Q4. What kinds of questions do people ask of log data?**
- Error rates over time (health monitoring).
- Top-K most frequent errors (triage).
- Slowest endpoints (performance).
- Unique users / IPs (analytics).
- Suspicious patterns (security).
- Pre-vs-post deploy behavior (change correlation).

### B. Hadoop Fundamentals

**Q5. What is Hadoop?**
An open-source framework for distributed storage and processing of large datasets across clusters of commodity machines. Built around three components: HDFS for storage, MapReduce for processing, and YARN for resource management.

**Q6. What is HDFS?**
**Hadoop Distributed File System** — a distributed filesystem that stores files split into blocks (default 128 MB) and replicated across nodes (default 3×) for fault tolerance. Optimized for large files and sequential reads.

**Q7. Why is HDFS suitable for log data?**
- Logs are written sequentially → matches HDFS's append-only model.
- Logs are large in aggregate → HDFS scales horizontally.
- Once written, logs are read many times for analysis → HDFS optimizes for high read throughput.
- Log retention spans months/years → HDFS's cost-per-TB is low.

**Q8. What is MapReduce?**
A programming model for distributed parallel processing. Splits work into a **Map** phase (process each record independently) and a **Reduce** phase (aggregate by key). Hadoop handles distribution, fault tolerance, and shuffle automatically.

**Q9. What is YARN?**
**Yet Another Resource Negotiator** — Hadoop's cluster resource manager. Allocates CPU/memory and schedules tasks on worker nodes. Replaced the legacy JobTracker in Hadoop 2.

**Q10. What is the role of the NameNode?**
The master that holds HDFS metadata — file-to-block mapping, block locations, permissions. It does *not* store data, only the directory of where everything lives.

### C. The MapReduce Algorithm

**Q11. Describe the three phases of a MapReduce job.**
1. **Map** — each input record is processed independently; the mapper emits `(key, value)` pairs.
2. **Shuffle and Sort** — Hadoop groups all values by key and routes them to reducers.
3. **Reduce** — each reducer receives a key and all its values, aggregates, and emits the result.

**Q12. What does the Mapper do in log analysis?**
Reads each log line, parses out fields (timestamp, level, message), and emits a `(key, value)` pair where the key is whatever you want to count by (level, hour, error type) and the value is typically `1` (or some metric to aggregate).

**Q13. What does the Reducer do in log analysis?**
Receives all values for a single key (e.g., all the 1s for "ERROR"). Aggregates — sum, count, average, top-K — and emits a final `(key, summary)`. The reducer is the source of the final answer.

**Q14. What is the Shuffle and Sort phase?**
The phase between Map and Reduce where Hadoop groups all map outputs by key, sorts them, and routes them across the network to the appropriate reducer. It's the most expensive phase because it moves data across nodes.

**Q15. What is the Combiner?**
A "mini-reducer" that runs on the mapper's output before shuffling, locally aggregating data to reduce network traffic. Must be commutative and associative — sums, counts, max, min are fine; average is not (unless redesigned).

**Q16. What is a Counter in Hadoop?**
A named integer that mapper/reducer code can increment during the job. Hadoop aggregates them across all tasks and reports them in the job summary. Used for diagnostic logging — counting malformed lines, parse errors, total input records — without polluting stdout.

### D. Patterns for Log Analysis

**Q17. How would you count log lines by level?**
- Mapper: parse each line, extract the level (ERROR, WARN, INFO), emit `(level, 1)`.
- Reducer: sum the 1s per level, emit `(level, total)`.

**Q18. How would you count errors per hour?**
- Mapper: parse the timestamp, truncate to the hour (e.g., "2024-04-12 10"), filter only ERROR lines, emit `(hour, 1)`.
- Reducer: sum per hour, emit `(hour, error_count)`.

**Q19. How would you find the top 10 most frequent errors?**
Two-stage MapReduce. **Stage 1**: count each error message (or signature). **Stage 2**: a single reducer maintains a min-heap of size 10 over the (count, error) pairs and emits the top 10 at the end. Setting `setNumReduceTasks(1)` ensures only one reducer sees all the data.

**Q20. How would you handle multi-line stack traces?**
- Pre-process logs into single-line records before submitting to MapReduce.
- Or write a **custom InputFormat** that recognizes record boundaries by timestamp prefix.
- Or use a heuristic in the mapper: lines without a timestamp prefix are continuations of the previous record.

**Q21. How would you compute the request rate over time?**
- Mapper: parse timestamp, truncate to a window (e.g., 5 minutes), filter to relevant requests, emit `(window, 1)`.
- Reducer: sum per window, emit `(window, request_count)`.
- Reading the output gives you a time series ready to plot.

### E. Robustness & Real-World Issues

**Q22. How do you handle malformed log lines?**
- Wrap parsing in try/catch in the mapper.
- Log the malformed line via a counter (e.g., `MALFORMED_LINES`).
- Skip it without crashing the job.
- After the job, check the counter to verify a small fraction of bad lines.

**Q23. What is data locality and why does it matter for logs?**
The scheduler tries to run a map task on the DataNode that already holds the block being processed. For terabytes of logs, avoiding network transfer dramatically speeds up the job. Modern Hadoop achieves >90% local task placement.

**Q24. What is fault tolerance in MapReduce?**
If a task fails (node crash, OOM, hardware error), Hadoop detects it and re-runs that task on another node. Only the failed task is retried — not the whole job. Speculative execution also runs duplicates of slow tasks (stragglers) to mitigate node-level slowness.

**Q25. What is speculative execution?**
A scheduler optimization for stragglers: if a task is running noticeably slower than its peers, Hadoop launches a duplicate on another node. Whichever finishes first wins; the other is killed. Without speculation, one slow disk could double a job's runtime.

### F. Modern Alternatives

**Q26. What is the ELK stack and why is it popular for log analysis?**
**Elasticsearch** (search engine + storage), **Logstash** (ingestion pipeline), **Kibana** (visualization). Provides near-real-time querying, dashboards, and alerting on logs. The de-facto standard for operational log analysis. Strengths: speed, interactive querying, beautiful UI. Weaknesses: more expensive than HDFS at very large scale, harder to do batch ML on.

**Q27. What is Splunk?**
A commercial competitor to ELK. Strong query language (SPL), excellent dashboards, robust alerting. Expensive at scale (per-GB-ingested licensing). Common in enterprise security and IT operations.

**Q28. When is MapReduce still appropriate for logs over ELK or Splunk?**
- Historical batch analysis on years of archived logs.
- Computing ML features over large historical windows.
- Complex multi-pass pipelines that don't fit a search engine.
- Cost — HDFS is far cheaper than Elasticsearch at petabyte scale.

**Q29. What is Apache Spark and why is it faster than MapReduce for logs?**
Spark keeps intermediate data in memory between stages. For multi-stage log pipelines (parse → enrich → aggregate → top-K), Spark avoids the disk round-trip that MapReduce pays at every stage. Often 10–100× faster on the same hardware.

**Q30. What is Spark Streaming?**
Spark's streaming API for processing data continuously (micro-batches every few seconds). Perfect for near-real-time log analysis — error rates, anomaly alerts — without the complexity of a full streaming engine.

### G. Practical & Code-Specific

**Q31. What `Writable` types would you use for log analysis?**
- `Text` for keys (level name, hour string, error type) and for the line itself.
- `IntWritable` or `LongWritable` for counts.
- `NullWritable` if you don't need a value (e.g., emitting unique IPs).

**Q32. How do you submit a MapReduce job that reads logs from a directory of files?**
Pass the directory path as the input. Hadoop reads all files in it (recursively, with appropriate config). Each file is split into blocks; each block becomes one or more map tasks. The directory can hold thousands of log files; Hadoop handles the parallelism.

**Q33. How do you handle compressed log files (`.gz`, `.bz2`)?**
Hadoop transparently decompresses them based on file extension. `.bz2` files are splittable (a single file can be split across mappers); `.gz` files are not splittable (one file = one mapper) — so prefer bzip2 or LZO for HDFS log archives.

**Q34. What is the output format of a log-analysis MapReduce job?**
By default, plain text — one `(key, value)` per line, tab-separated. Files are named `part-r-00000`, `part-r-00001`, etc., one per reducer. You can also configure SequenceFile output for binary, more compact storage.

**Q35. How do you visualize the output of a log-processing job?**
Read the part files into a tool like pandas or Excel and plot. For real production pipelines, output to a database (HDFS → Hive → Tableau) or a dashboard tool (Grafana, Superset, Looker).

**Q36. How would you analyze logs that are still being written to (live streams)?**
Don't use plain MapReduce — its batch nature isn't suitable. Use:
- **Spark Streaming** or **Structured Streaming** for micro-batch.
- **Apache Flink** for true streaming.
- **Kafka + Kafka Streams** for event-driven processing.
- The ELK / Splunk stack ingests in near-real time and lets you query as data arrives.

**Q37. How do you parse logs with regex in a Hadoop mapper?**
Compile the `Pattern` once as a static field (don't recompile per record). In `map()`, run `pattern.matcher(line)`, check `matches()`, then `group(N)` to extract fields. Catch `Exception` to handle malformed lines without crashing.

**Q38. What if your logs have inconsistent formats from different services?**
- Maintain multiple regex patterns and try each.
- Use a structured logging format (JSON) to begin with — services emit logs as JSON, the mapper does `JSONObject json = new JSONObject(line)`. No regex parsing.
- Use a format-detection library (Grok in Logstash, Fluentd parsers) to identify and route per format.

**Q39. How would you correlate ERROR logs with the user request that caused them?**
Logs need a **correlation ID** (request ID, trace ID) propagated through every service that handles the request. The mapper extracts the correlation ID; the reducer groups by it to reconstruct the full per-request log timeline. This is the foundation of **distributed tracing** systems (Jaeger, Zipkin, OpenTelemetry).

**Q40. How would you build a real-time dashboard from log analysis?**
A typical pipeline:
1. **Ingest** — Filebeat or Fluentd ships logs from servers to a queue.
2. **Queue** — Kafka buffers and decouples producers from consumers.
3. **Process** — Spark Streaming, Flink, or a custom consumer parses, enriches, and aggregates.
4. **Store** — Elasticsearch (for search) + Prometheus (for time-series metrics).
5. **Visualize** — Grafana or Kibana dashboards with auto-refresh.
6. **Alert** — PagerDuty / OpsGenie when error rates spike.
End-to-end latency: seconds to minutes.
