# Assignment 12 — Descriptive Statistics on Wine + Iris Datasets

Notebook: `Assignment_12_Complete.ipynb`
Focus: measures of central tendency, measures of variability, distribution shape (skewness, kurtosis), grouping, percentiles, and per-class summary statistics on real datasets.

---

## 1. What Is Descriptive Statistics?

**Descriptive statistics** is the branch of statistics that *summarizes* and *describes* the main features of a dataset using numbers — without making any predictions or generalizations to a larger population. It answers questions like:

- What's the typical value? (central tendency)
- How spread out are the values? (variability / dispersion)
- What is the shape of the distribution? (skewness, kurtosis)
- How are values grouped together?

It contrasts with **inferential statistics**, which uses samples to make probabilistic claims about populations (hypothesis tests, confidence intervals, p-values). Descriptive statistics is the *first thing* any analyst does after loading data — it surfaces patterns, outliers, and quality issues before any model is fit.

---

## 2. Types of Variables

Different statistics apply to different variable types.

### 2.1 Numeric (Quantitative)

- **Continuous** — can take any value in a range (height, weight, temperature, price). Has infinitely many possible values within an interval.
- **Discrete** — countable, integer-valued (number of children, count of clicks, score 1–5).

### 2.2 Categorical (Qualitative)

- **Nominal** — categories without inherent order (colors, gender, country).
- **Ordinal** — ordered categories without uniform spacing (grade A/B/C/D, Likert scales).

### 2.3 Why It Matters
- Mean and standard deviation only make sense for numeric data.
- Mode is the only central-tendency measure for nominal data.
- Median works for numeric and ordinal but not nominal.
- Histograms work for numeric; bar charts for categorical.

---

## 3. Measures of Central Tendency

These describe where the center of the data lies.

### 3.1 Mean (Arithmetic Average)

> mean = sum(x) / n

- Uses every value.
- Pulled by outliers — one billionaire in a sample of 100 people pulls the mean income up massively.
- Best when the data is roughly symmetric and free of extreme outliers.

### 3.2 Median

The middle value when data is sorted. If `n` is even, it's the average of the two middle values.

- **Robust** to outliers — the middle doesn't move whether the highest value is 100 or 10 million.
- Best summary for skewed data (income, response times, fares).

### 3.3 Mode

The most frequent value.

- Only meaningful measure of central tendency for **nominal** categorical data.
- A distribution may have zero modes (uniform), one mode (unimodal), two modes (bimodal), or more.
- For continuous data the mode is rarely useful; report central tendency via mean/median instead.

### 3.4 Mean vs Median in Skewed Distributions

- **Right-skewed**: mean > median (the long tail pulls the mean up).
- **Left-skewed**: mean < median.
- **Symmetric**: mean ≈ median.

This relationship is so reliable it's used as a quick skewness check.

### 3.5 Trimmed Mean
A compromise between mean and median: drop the top and bottom α% of values, then take the mean of what remains. A 10% trimmed mean drops the top 10% and bottom 10% before averaging — robust to outliers without losing as much information as the median.

---

## 4. Measures of Variability (Dispersion)

These describe how spread out the data is around the center.

### 4.1 Range

> range = max − min

- Extremely sensitive to outliers (one extreme value blows it up).
- Quick to compute but rarely the best summary.

### 4.2 Variance

> variance = mean of (x − mean)²

- Average squared distance from the mean.
- Units are squared (e.g., square dollars), which makes it hard to interpret directly.

### 4.3 Standard Deviation

> std = √variance

- Same units as the original data.
- Roughly: "the average distance of a value from the mean".
- Used everywhere — confidence intervals, z-scores, hypothesis tests.

### 4.4 Sample vs Population Variance
- **Population variance** divides by `n`.
- **Sample variance** divides by `n − 1` (Bessel's correction) — corrects for the bias introduced by using the sample mean instead of the true population mean.

`pandas.var()` and `numpy.var(ddof=1)` use sample variance by default.

### 4.5 Interquartile Range (IQR)

> IQR = Q3 − Q1

- Spread of the middle 50% of the data.
- **Robust** — unaffected by outliers.
- Foundation of the standard outlier rule (`Q1 − 1.5·IQR`, `Q3 + 1.5·IQR`).

### 4.6 Coefficient of Variation (CV)

> CV = std / mean

- Standard deviation as a fraction of the mean.
- Lets you compare variability across datasets with different units or scales.

---

## 5. Percentiles and Quartiles

### 5.1 Definitions

- **Percentile** — the value below which a given percentage of observations fall. The 80th percentile is the value below which 80% of the data lies.
- **Quartiles** — the 25th, 50th, and 75th percentiles (Q1, Q2, Q3). Q2 is the median.
- **Quintiles** — five equal parts (20th, 40th, 60th, 80th).
- **Deciles** — ten equal parts.

### 5.2 The Five-Number Summary
- Min
- Q1 (25th percentile)
- Median (50th percentile)
- Q3 (75th percentile)
- Max

These five numbers capture the essential shape of a distribution and form the basis of the boxplot.

### 5.3 Why Percentiles Matter
- Robust to outliers (unlike mean/std).
- Work on any distribution — no normality assumption.
- Direct interpretation (90th percentile latency, 99th percentile response time).
- Standard in performance engineering, education, finance.

---

## 6. Distribution Shape

### 6.1 Skewness
A measure of asymmetry.

- `skew = 0` — symmetric (normal).
- `skew > 0` — **right-skewed** — long tail on the right; mean > median.
- `skew < 0` — **left-skewed** — long tail on the left; mean < median.

Rule of thumb: `|skew| < 0.5` is fairly symmetric; `0.5–1` is moderate; `> 1` is highly skewed.

### 6.2 Kurtosis
A measure of "tailedness" — how heavy the tails are compared to a normal distribution.

- **Mesokurtic** (kurtosis ≈ 3 or excess kurtosis ≈ 0) — normal.
- **Leptokurtic** (kurtosis > 3) — heavy tails, sharp peak (financial returns, extreme events).
- **Platykurtic** (kurtosis < 3) — light tails, flat peak (uniform-like).

Excess kurtosis = kurtosis − 3, so a normal distribution has excess kurtosis = 0.

### 6.3 The Empirical Rule (68-95-99.7)
For a normal distribution:
- 68% of data falls within ±1 std of the mean.
- 95% within ±2 std.
- 99.7% within ±3 std.

This is the basis of the Z-score outlier rule (`|z| > 3` → outlier).

---

## 7. Grouping and Stratified Statistics

A single global summary often hides important sub-group differences.

- **`groupby`** in pandas splits a DataFrame by a categorical variable and applies a statistic per group.
- For Iris, statistics computed per species reveal that *Iris-setosa* has visibly smaller petals than *virginica*, even though the overall mean petal length is somewhere in between.
- Per-group summaries are the foundation of **stratified analysis** — comparing populations.

### 7.1 Why Group?
- Identify class differences before classification.
- Detect subpopulations with different behaviors.
- Avoid Simpson's Paradox (a trend in groups that reverses when groups are combined).

---

## 8. The Two Datasets in This Assignment

### 8.1 Wine Dataset
- Samples of wine with chemical features: alcohol, malic acid, ash, alkalinity, magnesium, phenols, flavanoids, etc.
- Classes: 3 cultivars (varieties of grape).
- A classic teaching dataset for descriptive statistics, classification, and clustering.

### 8.2 Iris Dataset
- 150 flower samples, 50 from each of 3 species (setosa, versicolor, virginica).
- 4 numeric features: sepal length, sepal width, petal length, petal width.
- The most famous teaching dataset in statistics; introduced by Fisher in 1936.

---

## 9. Visualizations That Pair With Descriptive Statistics

| Visualization | What It Shows |
|---|---|
| Histogram | Distribution shape — center, spread, skew |
| Boxplot | Median, quartiles, outliers (excellent for group comparison) |
| Violin plot | Full distribution shape + summary statistics |
| Pair plot | Pairwise relationships across all numeric features |
| Heatmap (corr) | Correlation between every pair of numeric features |
| Bar chart | Counts/means per category |
| KDE plot | Smooth density estimate |

---

## 10. Viva Questions (40)

### A. Fundamentals

**Q1. What is descriptive statistics?**
A branch of statistics that summarizes and describes the main features of a dataset using numbers — central tendency, variability, distribution shape, and counts. It does *not* make probabilistic claims about a larger population; that's the job of inferential statistics. Descriptive statistics is the first thing analysts do after loading data; it surfaces patterns, anomalies, and data-quality issues before any model is fit.

**Q2. What is the difference between descriptive and inferential statistics?**
- **Descriptive** — summarizes and describes data you already have (mean, std, percentiles).
- **Inferential** — uses a sample to make probabilistic claims about a population (hypothesis tests, confidence intervals, p-values).
Descriptive comes first; inferential builds on top of it.

**Q3. What's the difference between population and sample?**
- **Population** — the entire group of interest (every adult in a country).
- **Sample** — a subset drawn from the population (1000 adults surveyed).
Population statistics use Greek letters (μ, σ); sample statistics use Latin letters (x̄, s).

**Q4. What is the difference between continuous and discrete variables?**
- **Continuous** — can take any value in a range (height, weight, temperature). Infinitely many possible values within an interval.
- **Discrete** — countable, integer-valued (number of clicks, count of children, age in years).
Different statistics and visualizations apply to each.

**Q5. What is the difference between nominal and ordinal data?**
- **Nominal** — categories without order (colors, country, gender).
- **Ordinal** — ordered categories without uniform spacing (grade A/B/C/D, satisfaction Likert scale).
For nominal you can compute the mode but not mean or median; for ordinal you can compute the median but not the mean.

### B. Central Tendency

**Q6. What is the mean?**
The arithmetic average — sum of all values divided by count. Uses every observation, but is very sensitive to outliers. A single extreme value can shift the mean dramatically. The mean is the best summary for symmetric data without extreme values.

**Q7. What is the median?**
The middle value when the data is sorted. With even sample size, the average of the two middle values. The median is **robust** — outliers don't shift it. It's the preferred central-tendency measure for skewed data, like income, where the mean is dragged toward extreme values.

**Q8. What is the mode?**
The most frequent value. The only central-tendency measure that works for nominal categorical data ("most common color"). Distributions can be unimodal (one peak), bimodal (two peaks), or multimodal. For continuous data the mode is rarely useful.

**Q9. When is mean misleading and median preferred?**
When the data has outliers or is skewed. Income is the textbook example: a few billionaires pull the mean up so far that it doesn't reflect the experience of most people. The median is the typical income — half above, half below — and is far more representative.

**Q10. How are mean, median, and mode related in a normal distribution?**
In a perfectly symmetric (normal) distribution, all three are equal. As skew increases, they separate: in right-skewed data, mean > median > mode; in left-skewed, mean < median < mode. The relationship `mean ≈ median + 3·(median − mode)` holds approximately for moderately skewed distributions.

**Q11. What is a trimmed mean?**
The mean computed after dropping the most extreme α% of values from each tail. A 10% trimmed mean drops the top 10% and bottom 10% before averaging. It's a compromise between mean (which uses all values) and median (which uses only the middle one) — robust to outliers but more informative than the median.

### C. Variability

**Q12. What is variance?**
The average squared deviation from the mean: `var = mean((x − mean)²)`. Units are squared (square dollars, square seconds), making variance hard to interpret directly. We usually report its square root — the standard deviation — instead.

**Q13. What is standard deviation?**
The square root of the variance. Same units as the original data, so it's directly interpretable as "the typical distance of a value from the mean". Used everywhere — z-scores, confidence intervals, hypothesis tests.

**Q14. Why does sample variance divide by `n − 1` instead of `n`?**
Because using the sample mean (instead of the unknown population mean) systematically underestimates variance. Dividing by `n − 1` instead of `n` corrects for this — known as **Bessel's correction**. Pandas and NumPy default to `ddof=1` (sample variance) for this reason.

**Q15. What is the range?**
The difference between maximum and minimum. Easy to compute but extremely sensitive to outliers — one extreme value blows it up. Rarely the best summary; use IQR for a robust alternative.

**Q16. What is the IQR?**
**Interquartile Range** — Q3 − Q1, the spread of the middle 50% of the data. Robust to outliers because it ignores the tails. It's the basis of the standard outlier rule (`Q1 − 1.5·IQR`, `Q3 + 1.5·IQR`) and is what defines the box of a boxplot.

**Q17. What is the coefficient of variation?**
The standard deviation expressed as a fraction of the mean: `CV = σ / μ`. Useful for comparing variability across datasets with different units or scales — a CV of 0.1 means the std is 10% of the mean, regardless of whether the data is in dollars, seconds, or kilograms.

### D. Distribution Shape

**Q18. What is skewness?**
A measure of asymmetry. Positive skew (right-skewed) has a long right tail; negative skew (left-skewed) has a long left tail; zero skew is symmetric. Computed from the third standardized moment. As a rule of thumb, `|skew| < 0.5` is roughly symmetric, `0.5–1` is moderate, `> 1` is highly skewed.

**Q19. What is a right-skewed distribution? Give a real example.**
A distribution where most values are concentrated on the left and a long thin tail extends to the right. The mean is greater than the median because the tail pulls the mean upward. Examples: income, house prices, response times, web-site visit duration. Right-skew is common whenever values are bounded below (by zero, say) but unbounded above.

**Q20. What is kurtosis?**
A measure of "tailedness" — how heavy the tails are compared to a normal distribution. **Mesokurtic** (≈ normal), **leptokurtic** (heavier tails, sharper peak — characteristic of financial returns), **platykurtic** (lighter tails, flatter peak — like uniform distributions). High kurtosis means extreme events are more probable than a normal distribution would suggest.

**Q21. What is the empirical rule (68-95-99.7)?**
For a normal distribution: 68% of data lies within ±1 std of the mean, 95% within ±2 std, 99.7% within ±3 std. This is the foundation of the Z-score outlier rule (`|z| > 3` → outlier) and the standard "bell curve" intuition.

### E. Percentiles and Quartiles

**Q22. What are percentiles?**
A value below which a given percentage of observations fall. The 80th percentile is the value below which 80% of the data lies. Percentiles are robust (immune to outliers) and work on any distribution.

**Q23. What are quartiles?**
The 25th, 50th, and 75th percentiles — Q1, Q2 (the median), Q3. They split the data into four equal parts. The middle 50% lies between Q1 and Q3 — the **interquartile range**.

**Q24. What is the five-number summary?**
Min, Q1, median, Q3, max. Captures the essential shape of a distribution in five numbers and is the basis of the boxplot.

**Q25. Why use percentiles in performance engineering?**
Mean response time hides the tail. p50 (median) tells you the typical experience; p99 tells you the experience of the worst 1%. SLAs are usually defined on p99 or p99.9 because that's what determines whether users are noticeably slower.

### F. Grouping and Comparison

**Q26. What is `groupby` and why is it useful?**
A pandas operation that splits a DataFrame into groups based on a categorical column, applies a function (mean, std, count) per group, and combines the results. It's the foundation of stratified analysis — comparing statistics across groups (e.g., mean petal length per Iris species).

**Q27. What is Simpson's Paradox?**
A counterintuitive phenomenon where a trend appears in groups but disappears or reverses when the groups are combined. The classic example: a hospital with higher mortality overall has lower mortality *for every patient subgroup* — because it admits sicker patients. Always look at the data both pooled and stratified.

**Q28. Why compute statistics per class on Iris?**
Because the global mean of petal length is meaningless — the three species have very different petal lengths. Per-class statistics reveal that *setosa* has tiny petals (mean ~1.5 cm), *versicolor* medium (~4.3 cm), and *virginica* large (~5.5 cm). This separation is what makes Iris classification easy.

### G. Visualization Pairings

**Q29. What is the relationship between a boxplot and the five-number summary?**
A boxplot is a visual rendering of the five-number summary plus outliers. The box spans Q1–Q3, the line is the median, the whiskers extend 1.5·IQR, and dots beyond are outliers. It's the most compact, information-dense visual for distribution comparison.

**Q30. When would you prefer a histogram over a boxplot?**
When you care about the **shape** of one distribution in detail — peaks, modes, gaps. Boxplots compress the shape into summary stats; histograms show the full picture. Use a boxplot to compare distributions across groups; use a histogram to examine one in depth.

**Q31. What is a violin plot and why use it?**
A violin plot combines a boxplot's summary statistics with a kernel-density curve showing the full distribution shape. Useful when distributions are bimodal or non-standard — a boxplot would hide that.

**Q32. What is a pair plot?**
A grid of scatter plots showing every pair of numeric features against each other, with histograms or KDEs on the diagonal. Excellent for spotting correlations, clusters, and outliers across many features at once. Standard EDA tool for the Iris dataset.

### H. Practical & Code-Specific

**Q33. What does `df.describe()` return?**
A summary table with count, mean, standard deviation, min, 25%, 50% (median), 75%, and max for every numeric column. With `include='all'` it also reports unique-count, top, and frequency for categorical columns. The fastest one-liner for a first look at a dataset.

**Q34. What does `df.groupby('col').describe()` return?**
The full `describe()` summary, computed independently for each group. Returns a multi-level index where rows are groups and columns are (feature, statistic) pairs. The fastest way to compare distributions across categories.

**Q35. What does `df.agg(['mean', 'median', 'std'])` do?**
Applies multiple aggregation functions in a single call and stacks the results vertically. More concise than calling each separately, and lets you mix built-in and custom functions.

**Q36. What's the difference between `mean()`, `median()`, and `mode()` in pandas?**
- `mean()` — arithmetic mean. Returns one number per column.
- `median()` — middle value. Returns one number per column.
- `mode()` — most frequent value(s). Returns a *Series* per column, because there may be multiple modes.

**Q37. Why does `mode()` return a Series instead of a single value?**
Because a distribution can have multiple modes. Bimodal data (two peaks of equal height) has two modes. Pandas returns all of them; you typically take `.mode()[0]` to grab the first.

**Q38. How do you check normality of a feature?**
- Visual: histogram, KDE, Q-Q plot.
- Numeric: skewness near 0 and excess kurtosis near 0.
- Statistical tests: Shapiro-Wilk (for n < 5000), D'Agostino's K², Anderson-Darling, Kolmogorov-Smirnov. They give a p-value; a small p-value rejects normality. With large samples, even tiny deviations become statistically "significant", so visual inspection often matters more than tests.

**Q39. What is z-score and how is it useful?**
`z = (x − mean) / std`. The number of standard deviations an observation is from the mean. Used for outlier detection (`|z| > 3`), feature standardization, and comparing values from different distributions. Negative z means below the mean; positive z above.

**Q40. How would you summarize a new dataset in 5 minutes?**
1. `df.shape`, `df.head()`, `df.info()` — structure and dtypes.
2. `df.isnull().sum()` — missing values.
3. `df.describe(include='all')` — central tendency, spread, percentiles.
4. `df.hist()` — distribution shapes for every numeric column.
5. `df.corr()` (heatmap) — pairwise correlations.
6. `df.groupby('class').describe()` — group differences if you have a target/category.
7. Inspect a few rows with extreme values to understand outliers.
This 7-step routine surfaces the vast majority of data-quality issues and analytical opportunities.
