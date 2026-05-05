# Assignment 24 — Descriptive Statistics on Food Delivery + Iris Datasets

Notebook: `Assignment_24_Complete.ipynb`
Datasets: `food_delivery_times.csv`, `iris.csv`
Focus: measures of central tendency, variability, distribution shape (skewness, kurtosis), grouping, percentiles, and per-class summary statistics on real datasets.

---

## 1. What Is Descriptive Statistics?

**Descriptive statistics** is the branch of statistics that *summarizes* and *describes* the main features of a dataset using numbers — without making predictions about a larger population. It answers questions like:

- What's the typical value? (central tendency)
- How spread out are the values? (variability)
- What is the shape of the distribution? (skewness, kurtosis)
- How are values grouped together?

It contrasts with **inferential statistics**, which uses samples to make probabilistic claims about populations (hypothesis tests, confidence intervals, p-values). Descriptive statistics is the *first thing* any analyst does after loading data — it surfaces patterns, outliers, and quality issues before any model is fit.

---

## 2. Variable Types

Different statistics apply to different variable types.

### 2.1 Numeric (Quantitative)

- **Continuous** — any value in a range (delivery time, distance).
- **Discrete** — countable, integer-valued (number of stops, number of orders).

### 2.2 Categorical (Qualitative)

- **Nominal** — unordered categories (vehicle type, courier name).
- **Ordinal** — ordered categories without uniform spacing (traffic level: low/medium/high).

---

## 3. Measures of Central Tendency

These describe where the center of the data lies.

### 3.1 Mean (Arithmetic Average)

> mean = sum(x) / n

Uses every value. Pulled by outliers — best when data is symmetric.

### 3.2 Median

The middle value when sorted. Robust to outliers — preferred for skewed data (delivery times are right-skewed: most are 20–40 min, but a few take hours).

### 3.3 Mode

Most frequent value. Only meaningful central-tendency measure for nominal data. Distributions can be unimodal, bimodal, or multimodal.

### 3.4 Mean vs Median in Skewed Distributions

- **Right-skewed**: mean > median (Iris petal length when classes pooled, food delivery times).
- **Left-skewed**: mean < median.
- **Symmetric**: mean ≈ median.

### 3.5 Trimmed Mean
A compromise between mean and median: drop the top and bottom α% before averaging. Robust to outliers without losing as much information as the median.

---

## 4. Measures of Variability

### 4.1 Range

> range = max − min

Sensitive to outliers; rarely the best summary.

### 4.2 Variance

> variance = mean of (x − mean)²

Average squared deviation. Units are squared, hard to interpret directly.

### 4.3 Standard Deviation

> std = √variance

Same units as the data. Roughly: "the average distance of a value from the mean".

### 4.4 Sample vs Population Variance
- **Population**: divides by `n`.
- **Sample**: divides by `n − 1` (Bessel's correction). Pandas defaults to sample.

### 4.5 Interquartile Range (IQR)

> IQR = Q3 − Q1

Spread of the middle 50%. Robust to outliers. Foundation of the boxplot whiskers and the standard outlier rule (`Q1 − 1.5·IQR`, `Q3 + 1.5·IQR`).

### 4.6 Coefficient of Variation (CV)

> CV = std / mean

Standard deviation as a fraction of the mean. Lets you compare variability across datasets with different scales.

---

## 5. Percentiles and Quartiles

### 5.1 Definitions

- **Percentile** — value below which a given % of observations fall.
- **Quartiles** — 25th, 50th, 75th percentiles (Q1, Q2 = median, Q3).
- **Five-number summary** — min, Q1, median, Q3, max — basis of the boxplot.

### 5.2 Why Percentiles Matter
Robust (immune to outliers), distribution-free, directly interpretable. Standard in performance engineering (p99 latency), education (percentile ranks), and finance.

---

## 6. Distribution Shape

### 6.1 Skewness
Measure of asymmetry. Positive (right-skewed) has a long right tail; negative (left-skewed) a long left tail; zero is symmetric.

### 6.2 Kurtosis
Measure of "tailedness".
- **Leptokurtic** (high kurtosis) — heavy tails, sharp peak (financial returns).
- **Mesokurtic** (≈ normal).
- **Platykurtic** (low kurtosis) — light tails, flat peak.

### 6.3 The Empirical Rule (68-95-99.7)
For a normal distribution: 68% within ±1 std, 95% within ±2 std, 99.7% within ±3 std. Foundation of the Z-score outlier rule.

---

## 7. Grouping and Stratified Statistics

A single global summary often hides important sub-group differences.

- **`groupby`** in pandas splits a DataFrame by a categorical variable and applies a statistic per group.
- For Iris, statistics computed per species reveal that *setosa* has visibly smaller petals than *virginica*, even though the global mean is somewhere in between.
- Per-group summaries are the foundation of stratified analysis.

### 7.1 Why Group?
- Identify class differences before classification.
- Detect subpopulations with different behaviors.
- Avoid Simpson's Paradox (a trend in groups that reverses when groups are combined).

---

## 8. The Two Datasets

### 8.1 Food Delivery Times
- Numeric features: distance, prep time, courier experience.
- Categorical features: weather, traffic, time of day, vehicle type.
- Target: delivery time in minutes.
- Delivery time is **right-skewed** — most are 20–40 minutes, but a few extreme cases stretch to 100+.

### 8.2 Iris
- 150 flower samples, 50 from each of 3 species.
- 4 numeric features.
- Globally bimodal in petal features (because of the species mixture).
- Per-class unimodal and approximately normal.

---

## 9. Visualizations That Pair With Descriptive Statistics

| Visualization | What It Shows |
|---|---|
| Histogram | Distribution shape — center, spread, skew |
| Boxplot | Median, quartiles, outliers; group comparison |
| Violin plot | Full distribution + summary stats |
| Pair plot | Pairwise relationships across all numeric features |
| Heatmap (corr) | Pairwise correlations |
| Bar chart | Counts/means per category |
| KDE plot | Smooth density estimate |

---

## 10. Viva Questions (40)

### A. Fundamentals

**Q1. What is descriptive statistics?**
A branch of statistics that summarizes and describes the main features of a dataset using numbers — central tendency, variability, distribution shape, and counts. It does not make probabilistic claims about a larger population (that's inferential statistics). Descriptive statistics is the first thing analysts do after loading data; it surfaces patterns, anomalies, and data-quality issues.

**Q2. What's the difference between descriptive and inferential statistics?**
- **Descriptive** — summarizes and describes data you already have.
- **Inferential** — uses a sample to make probabilistic claims about a population (hypothesis tests, confidence intervals, p-values).

**Q3. What's the difference between population and sample?**
- **Population** — the entire group of interest.
- **Sample** — a subset drawn from the population.
Population statistics use Greek letters (μ, σ); sample statistics use Latin letters (x̄, s).

**Q4. What's the difference between continuous and discrete variables?**
- **Continuous** — can take any value in a range (delivery time).
- **Discrete** — countable (number of orders).
Different statistics and visualizations apply to each.

**Q5. What's the difference between nominal and ordinal data?**
- **Nominal** — unordered categories (vehicle type).
- **Ordinal** — ordered categories without uniform spacing (traffic = low/med/high).
For nominal you can compute the mode but not mean or median; for ordinal you can compute the median but not the mean.

### B. Central Tendency

**Q6. What is the mean?**
The arithmetic average — sum of values divided by count. Uses every observation but is sensitive to outliers. Best for symmetric data without extreme values.

**Q7. What is the median?**
The middle value when sorted. With even sample size, average of the two middle values. **Robust** to outliers. Preferred for skewed data like delivery times or income.

**Q8. What is the mode?**
The most frequent value. Only useful central-tendency measure for nominal categorical data. Distributions can be unimodal, bimodal, or multimodal.

**Q9. When is mean misleading and median preferred?**
When the data has outliers or is skewed. Delivery times are skewed by occasional very long deliveries — the median is more representative of "typical".

**Q10. How are mean, median, and mode related in a normal distribution?**
In a perfectly symmetric distribution, all three are equal. As skew increases, they separate: in right-skewed data, mean > median > mode; in left-skewed, mean < median < mode.

**Q11. What is a trimmed mean?**
The mean computed after dropping the most extreme α% of values from each tail. Robust to outliers without losing as much information as the median.

### C. Variability

**Q12. What is variance?**
The average squared deviation from the mean. Units are squared, making variance hard to interpret directly. We usually report its square root (std).

**Q13. What is standard deviation?**
The square root of variance. Same units as the data. Roughly: "the typical distance of a value from the mean". Used everywhere in statistics.

**Q14. Why does sample variance divide by `n − 1`?**
Because using the sample mean (instead of the unknown population mean) systematically underestimates variance. Dividing by `n − 1` corrects for this — Bessel's correction.

**Q15. What is the range?**
`max − min`. Easy to compute but extremely sensitive to outliers — one extreme blows it up.

**Q16. What is the IQR?**
**Interquartile Range** — Q3 − Q1, the spread of the middle 50%. Robust to outliers. Foundation of the boxplot whiskers and the standard outlier rule.

**Q17. What is the coefficient of variation?**
`CV = σ / μ`. Standard deviation as a fraction of the mean. Useful for comparing variability across datasets with different units or scales.

### D. Distribution Shape

**Q18. What is skewness?**
A measure of asymmetry. Positive (right-skewed) has a long right tail; negative (left-skewed) a long left tail; zero is symmetric. Rule of thumb: `|skew| < 0.5` is roughly symmetric.

**Q19. What is a right-skewed distribution? Give a real example.**
Long tail on the right; mean > median. Common when values are bounded below by zero but unbounded above. Examples: income, fares, **delivery times**, file sizes.

**Q20. What is kurtosis?**
A measure of "tailedness". Leptokurtic (high kurtosis) → heavy tails, sharp peak. Mesokurtic ≈ normal. Platykurtic → light tails, flat peak.

**Q21. What is the empirical rule (68-95-99.7)?**
For a normal distribution: 68% of data within ±1 std, 95% within ±2 std, 99.7% within ±3 std. Foundation of the Z-score outlier rule.

### E. Percentiles

**Q22. What are percentiles?**
A value below which a given percentage of observations fall. The 80th percentile is the value below which 80% of data lies.

**Q23. What are quartiles?**
The 25th, 50th, 75th percentiles — Q1, Q2 (median), Q3. Split data into four equal parts. The IQR is `Q3 − Q1`.

**Q24. What is the five-number summary?**
Min, Q1, median, Q3, max. Captures the essential shape of a distribution. Basis of the boxplot.

**Q25. Why use percentiles in performance engineering?**
Mean response time hides the tail. p50 (median) tells you the typical experience; p99 tells you the worst 1%. SLAs are usually defined on p99 or p99.9 because that's what determines whether users notice slowness.

### F. Grouping

**Q26. What is `groupby` and why is it useful?**
A pandas operation that splits a DataFrame by a categorical column, applies a function per group, and combines results. The foundation of stratified analysis — comparing statistics across groups.

**Q27. What is Simpson's Paradox?**
A counterintuitive phenomenon where a trend appears in groups but disappears or reverses when the groups are combined. Always look at the data both pooled and stratified.

**Q28. Why compute statistics per class on Iris?**
Because the global mean of petal length is meaningless — the three species have very different petal lengths. Per-class statistics reveal that *setosa* has tiny petals (~1.5 cm), *versicolor* medium (~4.3 cm), *virginica* large (~5.5 cm).

**Q29. Why compute statistics per traffic level on food delivery?**
Because traffic clearly affects delivery time. Per-traffic-level summaries show how strongly traffic correlates with delivery time and whether the relationship is linear.

### G. Visualizations

**Q30. What is a boxplot?**
A compact summary showing five numbers — min, Q1, median, Q3, max — plus outliers as dots. Excellent for group comparison.

**Q31. What is a violin plot?**
Combines a boxplot's summary with a kernel density curve showing full distribution shape. Useful when distributions are bimodal.

**Q32. What is a pair plot?**
A grid of scatter plots showing every pair of numeric features, with histograms or KDEs on the diagonal. Excellent for first-look multivariate EDA.

### H. Practical & Code

**Q33. What does `df.describe()` return?**
A summary table with count, mean, std, min, 25%, 50% (median), 75%, max for every numeric column. With `include='all'` it also reports unique-count, top, freq for categorical columns.

**Q34. What does `df.groupby('col').describe()` return?**
The full `describe()` summary, computed per group. Returns a multi-level table where rows are groups and columns are (feature, statistic) pairs.

**Q35. What does `df.agg(['mean', 'median', 'std'])` do?**
Applies multiple aggregation functions in one call and stacks results. More concise than calling each separately.

**Q36. What's the difference between `mean()`, `median()`, `mode()` in pandas?**
- `mean()` — arithmetic mean. Returns one number per column.
- `median()` — middle value. Returns one number per column.
- `mode()` — most frequent value. Returns a Series per column (may be multiple).

**Q37. Why does `mode()` return a Series instead of a single value?**
Because a distribution can have multiple modes. Bimodal data has two modes. Pandas returns all of them; you typically take `.mode()[0]`.

**Q38. How do you check normality?**
- Visual: histogram, KDE, Q-Q plot.
- Numeric: skewness near 0, excess kurtosis near 0.
- Statistical tests: Shapiro-Wilk, D'Agostino's K², Anderson-Darling, Kolmogorov-Smirnov.
With large samples, even tiny deviations become statistically "significant", so visual inspection often matters more.

**Q39. What is z-score and how is it useful?**
`z = (x − mean) / std`. Number of standard deviations from the mean. Used for outlier detection (`|z| > 3`), feature standardization, and comparing values from different distributions.

**Q40. How would you summarize a new dataset in 5 minutes?**
1. `df.shape`, `df.head()`, `df.info()` — structure.
2. `df.isnull().sum()` — missing values.
3. `df.describe(include='all')` — central tendency, spread, percentiles.
4. `df.hist()` — distribution shapes.
5. `df.corr()` heatmap — pairwise correlations.
6. `df.groupby('class').describe()` — group differences.
7. Inspect a few extreme rows.
This 7-step routine surfaces most data-quality issues and analytical opportunities.
