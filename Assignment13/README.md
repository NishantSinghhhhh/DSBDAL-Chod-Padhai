# Assignment 13 — Hadoop MapReduce WordCount in Java

Notebook: `Assignment_13_Complete.ipynb`
Files: `WordCount.java`, `sample_text.txt`
Focus: distributed computing fundamentals, Hadoop ecosystem, HDFS, MapReduce paradigm, and the canonical WordCount example.

---

## 1. The Big Data Problem

### 1.1 What Is Big Data?
**Big Data** refers to datasets too large or too complex to process on a single machine. It's commonly characterized by the "V's":

- **Volume** — terabytes to petabytes of data.
- **Velocity** — data arrives at high speed (streaming, real-time).
- **Variety** — structured (tables), semi-structured (JSON, XML), unstructured (text, images, video).
- **Veracity** — quality and trustworthiness of data.
- **Value** — the insights extractable from the data.

### 1.2 Why Single Machines Fail
- **Disk capacity** — a single server can hold only a few terabytes.
- **Memory** — RAM is even more limited.
- **CPU** — sequential processing on one machine takes too long.
- **Reliability** — one machine = one point of failure.

The solution: distribute the data and the computation across many machines.

---

## 2. The Hadoop Ecosystem

Hadoop is an open-source framework for storing and processing big data on clusters of commodity machines. The three foundational components:

### 2.1 HDFS (Hadoop Distributed File System)
A distributed storage system designed for very large files.

- Splits files into **blocks** (default 128 MB or 256 MB).
- Replicates each block to **multiple nodes** (default 3) for fault tolerance.
- **NameNode** — master process that holds file metadata (where each block lives).
- **DataNodes** — worker processes that actually store the block data.
- Optimized for **sequential reads** of large files, not random small reads.

### 2.2 MapReduce
A programming model for processing data stored in HDFS in parallel across the cluster.

- Inspired by functional programming's `map` and `reduce`.
- **Mapper** processes each record independently — embarrassingly parallel.
- **Reducer** aggregates results from mappers.
- The framework handles all the distribution, scheduling, fault tolerance.

### 2.3 YARN (Yet Another Resource Negotiator)
Hadoop's cluster resource manager.

- Allocates CPU and memory across jobs.
- Schedules tasks on available nodes.
- Replaced the original JobTracker in Hadoop 2.x.

### 2.4 The Wider Ecosystem
- **Hive** — SQL-like queries over HDFS.
- **Pig** — high-level data-flow language.
- **HBase** — NoSQL database on HDFS.
- **Sqoop** — import/export between HDFS and relational databases.
- **Spark** — modern in-memory alternative to MapReduce, much faster.

---

## 3. The MapReduce Paradigm

### 3.1 The Map Phase
Each input record (line of a file) is passed to a **mapper** function. The mapper emits zero or more `(key, value)` pairs.

For WordCount, the input is a line of text and the output is one `(word, 1)` pair per word in the line.

### 3.2 The Shuffle and Sort Phase
Hadoop automatically:
- Groups all `(key, value)` pairs by key.
- Sorts the keys.
- Routes pairs with the same key to the same reducer.

This is the most expensive phase — moves data across the network.

### 3.3 The Reduce Phase
For each unique key, the **reducer** receives the key and an iterator over all the values associated with it. It typically aggregates them — sum, count, average, max — and emits a final `(key, result)` pair.

For WordCount, the reducer sums all the 1s for each word and emits `(word, total_count)`.

### 3.4 Why It Works
- **Map** is embarrassingly parallel — each input record can be processed independently.
- **Shuffle** is the only synchronization point.
- **Reduce** is also parallel across keys.
- **Fault tolerance**: if a worker dies, only that worker's tasks need to be redone — not the whole job.

---

## 4. The Anatomy of a MapReduce Job

A typical Hadoop MapReduce program in Java consists of three classes:

### 4.1 The Mapper Class
- Extends `Mapper<KeyIn, ValueIn, KeyOut, ValueOut>`.
- Overrides the `map(key, value, context)` method.
- For each input pair, emits zero or more output pairs via `context.write(...)`.

### 4.2 The Reducer Class
- Extends `Reducer<KeyIn, ValueIn, KeyOut, ValueOut>`.
- Overrides the `reduce(key, values, context)` method.
- Receives all values for a single key; emits aggregated output.

### 4.3 The Driver / Main Class
- Configures the job: input/output paths, mapper/reducer classes, key/value types.
- Submits the job to the cluster.
- Waits for completion.

---

## 5. Hadoop Data Types

Hadoop has its own writable types (analogous to Java's primitives) for efficient serialization across the network:

| Hadoop Type | Java Equivalent |
|---|---|
| `IntWritable` | `int` |
| `LongWritable` | `long` |
| `FloatWritable` | `float` |
| `DoubleWritable` | `double` |
| `Text` | `String` |
| `BooleanWritable` | `boolean` |
| `NullWritable` | `null` (no value) |

These types implement the `Writable` interface, allowing efficient binary serialization. You don't use Java's `String` directly because `Text` is more efficient for Hadoop's network shuffling.

---

## 6. The Combiner — A Mini-Reducer

A **Combiner** is an optional optimization that runs *after* the mapper on the same node, *before* the data is sent to the reducer.

- Acts as a "mini-reducer" on each mapper's output.
- Reduces network traffic by aggregating locally before shuffling.
- Must be **commutative and associative** (sum, count, max — yes; average — no, unless redesigned).

For WordCount, the combiner sums local counts per word so each mapper sends one `(word, partial_count)` pair instead of many `(word, 1)` pairs.

---

## 7. The Partitioner

Determines which reducer a `(key, value)` pair goes to.

- Default: **HashPartitioner** — `partition = hash(key) % numReducers`.
- Custom partitioners are useful when you want all related keys to land on the same reducer (e.g., all keys starting with the same letter).

The partitioner ensures that all values for a given key end up on the same reducer — critical for correctness.

---

## 8. WordCount — The "Hello World" of MapReduce

The canonical example that taught a generation of engineers MapReduce.

### 8.1 The Problem
Given a (potentially huge) text corpus, count how many times each unique word appears.

### 8.2 The MapReduce Solution

**Mapper:**
- Input: `(line_offset, line_text)`
- For each word in the line, emit `(word, 1)`.

**Reducer:**
- Input: `(word, [1, 1, 1, …])`
- Output: `(word, sum)`.

That's the whole algorithm. The framework handles the rest.

### 8.3 Why It's a Classic

- Simple enough to grasp in 5 minutes.
- Scales from a single text file to web-scale corpora.
- Demonstrates every core concept: parallelism, key-value, shuffle, aggregation.
- Real-world variants drive search-engine ranking, log analysis, recommendation systems.

---

## 9. Strengths and Weaknesses of MapReduce

### Strengths
- **Massive scalability** — proven on petabyte-scale data.
- **Fault tolerance** — failed tasks are retried automatically.
- **Cheap hardware** — commodity machines, no specialized servers.
- **Locality-aware** — schedules tasks close to where the data lives ("data locality").

### Weaknesses
- **Disk-based** — every map and reduce stage hits disk. Slow for iterative algorithms.
- **No native iteration** — machine-learning algorithms that need to loop over data 100 times are very inefficient.
- **High latency** — minutes-to-hours for small jobs because of cluster startup.
- **Verbose API** — Java code for even simple operations.

These limitations motivated **Apache Spark**, which keeps data in memory between stages and is 10–100× faster for iterative workloads.

---

## 10. Modern Alternatives

- **Apache Spark** — in-memory distributed processing. RDDs and DataFrames. Much faster for iterative ML.
- **Apache Flink** — true streaming + batch.
- **Apache Beam** — unified programming model that runs on multiple engines (Flink, Spark, Dataflow).
- **Cloud equivalents**:
  - AWS — EMR (Hadoop/Spark), Athena (Presto), Glue.
  - GCP — Dataproc, BigQuery, Dataflow.
  - Azure — HDInsight, Synapse, Databricks.

---

## 11. Viva Questions (40)

### A. Big Data Fundamentals

**Q1. What is Big Data?**
Datasets too large or too complex to process with traditional tools on a single machine. Characterized by the V's — Volume (size), Velocity (speed), Variety (formats), Veracity (quality), Value (insights). The defining feature isn't a single number but the requirement to distribute storage and computation across many machines.

**Q2. What are the 5 V's of Big Data?**
- **Volume** — sheer size.
- **Velocity** — speed of arrival.
- **Variety** — different formats and sources.
- **Veracity** — uncertainty and quality.
- **Value** — business insights extractable from the data.

**Q3. Why can't traditional databases handle Big Data?**
- Single-server disk and memory limits.
- Vertical scaling has hardware ceilings; horizontal scaling is hard for ACID transactions.
- Data variety doesn't fit relational schemas.
- Query performance degrades sharply on very large tables.
- Too expensive at scale.

**Q4. What is horizontal vs vertical scaling?**
- **Vertical scaling (scale up)** — buy a bigger machine. Limited by hardware.
- **Horizontal scaling (scale out)** — add more machines. Theoretically unlimited but requires distributed software.
Hadoop is built for horizontal scaling on commodity hardware.

### B. Hadoop Architecture

**Q5. What is Hadoop?**
An open-source framework for distributed storage and processing of large datasets across clusters of commodity hardware. The three core components are HDFS (storage), MapReduce (processing), and YARN (resource management).

**Q6. What is HDFS?**
**Hadoop Distributed File System** — splits large files into blocks, replicates them across nodes, and presents them as a single logical filesystem. Optimized for sequential reads of large files; not for random small reads. Default block size is 128 MB; default replication factor is 3.

**Q7. What is the role of the NameNode?**
The master daemon that holds **HDFS metadata** — file-to-block mapping, block-to-DataNode mapping, permissions. It does not store the actual data, only the directory of where everything lives. A single NameNode is a Single Point of Failure (mitigated by Standby NameNodes in HA configurations).

**Q8. What is the role of a DataNode?**
A worker daemon that stores the actual data blocks on its local disk. Each DataNode periodically reports its block list to the NameNode (heartbeat) so the NameNode knows which DataNodes have which blocks.

**Q9. What is the default block size in HDFS?**
128 MB in modern Hadoop (was 64 MB in early versions). Configurable per file. Larger blocks reduce metadata overhead at the NameNode and improve throughput; smaller blocks add more parallelism but more overhead.

**Q10. What is the default replication factor and why?**
3 — each block is stored on 3 different DataNodes. Provides fault tolerance: even if 2 of the 3 nodes fail, the data is still available. Replication factor is configurable per file.

**Q11. What is YARN?**
**Yet Another Resource Negotiator** — Hadoop's cluster resource manager. Allocates CPU and memory across jobs and schedules tasks on available DataNodes. Replaced the legacy JobTracker in Hadoop 2.x.

### C. MapReduce Concepts

**Q12. What is MapReduce?**
A programming model for processing large datasets in parallel across a distributed cluster. The work is split into two phases — **Map** (process each record independently) and **Reduce** (aggregate results). Inspired by functional programming's `map` and `reduce` operations and popularized by Google's 2004 paper.

**Q13. What does the Mapper do?**
Reads input records (typically lines of a file) one at a time and emits zero or more `(key, value)` pairs. Each mapper runs independently on a portion of the input — embarrassingly parallel.

**Q14. What does the Reducer do?**
Receives all values associated with a single key and emits aggregated output — typically a single `(key, summary)` pair. Each reducer runs independently on a different group of keys.

**Q15. What is the Shuffle and Sort phase?**
The phase between Map and Reduce where Hadoop:
- Groups all `(key, value)` pairs by key.
- Sorts them.
- Routes pairs to the appropriate reducer based on the partitioner.
This is the most network-intensive part of any MapReduce job.

**Q16. What is the Combiner?**
An optional **mini-reducer** that runs on the mapper's output before it's sent across the network. It pre-aggregates locally to reduce network traffic. Must be commutative and associative — works for sum, count, max, min; doesn't work directly for average.

**Q17. What is the Partitioner?**
A function that decides which reducer a `(key, value)` pair goes to. The default `HashPartitioner` uses `hash(key) % numReducers`. You can write a custom partitioner when you need related keys to land on the same reducer.

**Q18. How does fault tolerance work in MapReduce?**
Tasks (map or reduce) are independent units of work. If a task fails — node crashes, hardware failure, OOM — the framework detects it and re-runs only that task on another node. Speculative execution also runs slow tasks redundantly to mitigate stragglers.

**Q19. What is data locality?**
A scheduler optimization: when possible, run map tasks on the DataNode that already holds the input block, so data doesn't move across the network. Reduces network I/O and dramatically speeds up jobs with large inputs.

### D. Java MapReduce Programming

**Q20. What are the three classes in a typical MapReduce job?**
- **Mapper** — extends `Mapper<KIn, VIn, KOut, VOut>`, overrides `map(key, value, context)`.
- **Reducer** — extends `Reducer<KIn, VIn, KOut, VOut>`, overrides `reduce(key, values, context)`.
- **Driver** — main class that configures the job (input/output paths, mapper/reducer classes, key/value types) and submits it to the cluster.

**Q21. What is the `Writable` interface?**
Hadoop's serialization interface. Every type passed between mappers and reducers must implement `Writable` to allow efficient binary serialization across the network. Built-in implementations: `IntWritable`, `LongWritable`, `Text`, `DoubleWritable`, etc.

**Q22. Why does Hadoop have its own types instead of Java's String/int?**
Hadoop's `Writable` types are **more compact and faster to serialize** than Java's default serialization. For petabyte-scale shuffles, every byte saved matters. They also avoid Java's boxing/unboxing overhead for primitives.

**Q23. What is the `Text` class?**
Hadoop's equivalent of `String`, optimized for UTF-8 byte storage. Used as the key or value type when working with text data.

**Q24. What is the `IntWritable` class?**
Hadoop's wrapper for `int` that implements the `Writable` interface. Used to send integers between mappers and reducers efficiently.

### E. WordCount Specifically

**Q25. Describe the WordCount mapper.**
For each line of input, split it into words. For each word, emit `(word, 1)`. The key is the word (Text), the value is the count contribution (IntWritable, value 1).

**Q26. Describe the WordCount reducer.**
For each unique word, receive all the 1s emitted by mappers. Sum them. Emit `(word, total_count)`. The framework guarantees that every 1 for a given word has been routed to the same reducer.

**Q27. How does the combiner help WordCount?**
Without it, a mapper emits `(the, 1)` thousands of times. The combiner aggregates these locally to `(the, 5000)` so only one pair per unique word is shuffled across the network. Often makes the job 10× faster on text-heavy datasets.

**Q28. Can the WordCount reducer be reused as the combiner?**
Yes — because summation is both commutative and associative. WordCount is a textbook case where the same class can serve as both reducer and combiner.

### F. Strengths, Weaknesses, Alternatives

**Q29. What are the strengths of MapReduce?**
- Massive scalability — proven on petabyte data.
- Fault tolerance — automatic task retry.
- Runs on commodity hardware.
- Simple programming model — `map` and `reduce` are easy to reason about.
- Strong ecosystem — Hive, Pig, HBase build on top of it.

**Q30. What are the weaknesses of MapReduce?**
- Disk-based between stages → slow for iterative algorithms.
- High setup/teardown latency.
- Verbose Java API.
- Inefficient for short jobs (cluster startup dominates).
- Not suited to streaming or low-latency interactive queries.

**Q31. Why is Spark faster than MapReduce?**
Spark keeps intermediate results **in memory** between stages instead of writing to disk. For iterative ML algorithms (gradient descent, K-Means) that loop over the data many times, in-memory access is 10–100× faster than disk reads. Spark also has a richer API (DataFrames, SQL, MLlib) and supports streaming.

**Q32. When would you still use MapReduce instead of Spark?**
- Very large one-pass batch jobs where memory isn't enough to hold intermediate state.
- Legacy pipelines already written in MapReduce.
- Cost-sensitive environments where memory is expensive.
- Educational contexts — MapReduce is conceptually simpler.

**Q33. What is Apache Spark?**
A modern distributed computing framework that addresses MapReduce's limitations. Key abstractions: **RDD** (resilient distributed dataset) and **DataFrame**. Supports SQL, streaming, machine learning, and graph processing in one unified API. Runs on Hadoop YARN, Kubernetes, or standalone.

**Q34. What is Apache Hive?**
A data warehouse layer on top of Hadoop. Lets you write **HiveQL** (SQL-like) queries that get translated into MapReduce or Spark jobs. Makes Hadoop accessible to analysts who know SQL but don't want to write Java.

**Q35. What is Apache Pig?**
A high-level data-flow language (called Pig Latin) that compiles to MapReduce. More expressive than raw MapReduce but less popular than Hive in modern deployments.

### G. Practical & Code-Specific

**Q36. How do you submit a MapReduce job?**
Compile the Java code into a JAR. Then run `hadoop jar wordcount.jar WordCount /input/path /output/path`. Hadoop submits the job to YARN, which schedules it across the cluster. Progress can be monitored from the YARN UI.

**Q37. What happens if the output directory already exists?**
The job fails immediately. Hadoop refuses to overwrite outputs to prevent accidental data loss. Either delete the output directory first or use a different name.

**Q38. What format is the WordCount output?**
By default, plain text — one `(key, value)` pair per line, separated by a tab. Files like `part-r-00000`, `part-r-00001`, one per reducer. The number of files = number of reducers.

**Q39. What is speculative execution?**
A scheduler optimization for stragglers. If one task is running noticeably slower than its peers, Hadoop launches a duplicate on another node. Whichever finishes first wins; the other is killed. Mitigates the impact of slow nodes on overall job time.

**Q40. How do you debug a MapReduce job?**
- Run on a small sample first in **local mode** (no cluster needed).
- Use **counters** for diagnostic logging without printing to stdout.
- Check **task logs** in the YARN UI for stack traces.
- Use **MRUnit** — a unit-testing library specifically for MapReduce.
- For complex pipelines, profile a single mapper/reducer locally with a debugger before submitting to the cluster.
