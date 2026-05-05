# Assignment 23 — Data Wrangling on Academic Performance Dataset

Notebook: `Assignment_23_Complete.ipynb`
Dataset: `academic_performance.csv`
Focus: end-to-end wrangling — missing values, inconsistencies, outlier detection (boxplot, IQR), capping, transformation, normalization.

---

## 1. What Is Data Wrangling?

**Data wrangling** (also called *data cleaning* or *data munging*) is the process of taking raw, messy, real-world data and transforming it into a clean, structured form ready for analysis or modeling. In any real-world data-science project, this step typically takes **60–80% of total project time** — far more than the modeling itself. The famous saying captures why: **"garbage in, garbage out"** — even the best ML algorithm fails on dirty data.

Wrangling encompasses several distinct sub-tasks:

1. **Discovery** — understanding what columns exist, their types, and valid ranges.
2. **Structuring** — reshaping the data (long ↔ wide, splitting columns, joining tables).
3. **Cleaning** — handling missing values, duplicates, and inconsistencies.
4. **Enriching** — adding derived columns or external data.
5. **Validating** — sanity-checking against domain rules.
6. **Publishing** — writing the cleaned dataset back out for downstream use.

---

## 2. Missing Values

### 2.1 Why Data Is Missing
Missing-value patterns:

- **MCAR (Missing Completely At Random)** — independent of all data. Easiest to handle.
- **MAR (Missing At Random)** — depends on observed variables but not on the missing value itself.
- **MNAR (Missing Not At Random)** — depends on the missing value itself. Hardest case; biases inference.

### 2.2 Strategies

- **Listwise deletion** — drop rows with any NaN. Simple but wastes data.
- **Mean imputation** — fill with the column mean. Best for symmetric data.
- **Median imputation** — fill with the median. Robust to outliers; preferred default for numeric data.
- **Mode imputation** — fill with the most frequent value. Used for categorical columns.
- **Constant imputation** — fill with a placeholder ("Unknown", 0).
- **Forward-fill / backward-fill** — propagate previous/next value. Used in time-series.
- **Model-based** — KNN imputation, MICE, IterativeImputer.

### 2.3 When Mean vs Median?
- **Mean** — symmetric distributions without extreme outliers.
- **Median** — skewed data or outliers, since the mean is dragged by extreme tails.

---

## 3. Inconsistencies

Inconsistencies are values that exist but are **invalid** — wrong format, out-of-range, or inconsistent spellings.

### 3.1 Common Inconsistencies in Educational Data
- **Mixed-case categories** — "Male", "male", "M", "MALE" should all be one.
- **Out-of-range scores** — math score = 150 (max should be 100).
- **Negative ages** — clearly typo errors.
- **Date format mismatches** — "01/02/2024" vs "2024-01-02".

### 3.2 Strategies
- **String normalization** — lowercase, strip whitespace, regex match.
- **Categorical mapping** — map all variants to canonical form.
- **Range validation** — flag values outside `[0, 100]` for percentages.
- **Replace invalid with NaN** — then re-impute with median.

---

## 4. Outliers

### 4.1 What Is an Outlier?
A value unusually far from the rest of the data. May be a measurement error, data-entry mistake, or a genuine extreme observation.

### 4.2 Why They Matter
- Distort the mean and standard deviation.
- Mislead distance-based models (KNN, K-Means).
- Dominate squared-error losses in linear regression.
- Sometimes are the most important points (fraud, anomalies).

### 4.3 Detection

**Visual — Boxplot.** A box from Q1 to Q3 with whiskers at 1.5·IQR; dots beyond are outliers.

**Numeric — IQR Rule.**
- `Q1 = 25th percentile`, `Q3 = 75th percentile`
- `IQR = Q3 − Q1`
- Lower bound = `Q1 − 1.5·IQR`
- Upper bound = `Q3 + 1.5·IQR`
- Anything outside is an outlier.

**Numeric — Z-Score.** `z = (x − mean) / std`; `|z| > 3` is an outlier (~99.7% rule for normal data).

**Modified Z-Score (using MAD).** More robust on skewed data.

### 4.4 Treatment

| Strategy | When | Trade-off |
|---|---|---|
| **Remove** | Confirmed errors | Loses information |
| **Cap (Winsorize)** | Real but extreme | Compresses tails |
| **Transform** | Skewed distribution | Changes scale |
| **Bin** | Non-numeric goal | Loses precision |
| **Keep** | Outlier IS the signal | Use robust models |

**Capping (Winsorization)** — replace values outside the IQR bounds with the bounds. Preserves rows; biases tails toward the bound.

---

## 5. Data Transformation

### 5.1 Skewness
A measure of asymmetry. Right-skewed (positive skew) has a long right tail; left-skewed (negative skew) has a long left tail; symmetric is zero. As a rule of thumb, `|skew| < 0.5` is fairly symmetric.

### 5.2 Transformations

| Transformation | Best for |
|---|---|
| `log(x)` / `log1p(x)` | Right-skewed positive data |
| `sqrt(x)` | Mild right skew |
| `1/x` | Strong right skew |
| `x²`, `x³` | Left-skewed data |
| Box-Cox | Automatic λ for positive data |
| Yeo-Johnson | Automatic for any sign |

### 5.3 Why Normality Matters
- Linear regression assumes normally-distributed errors.
- Many statistical tests assume normality.
- Distance-based methods are sensitive to skew.
- Tree-based models are largely invariant to monotonic transformations.

---

## 6. Scaling vs Transformation

| | Changes Range | Changes Shape |
|---|---|---|
| **Min-Max scaling** | Yes (to `[0, 1]`) | No |
| **Standardization (Z-score)** | Yes (mean 0, std 1) | No |
| **Log / sqrt** | Yes | Yes (toward normal) |
| **Robust scaling** | Yes (using median, IQR) | No |

Scaling rescales magnitude; transformation reshapes the distribution. They're different tools and often used together.

---

## 7. The Five Wrangling Operations in This Assignment

1. **Detect missing values** with `isnull().sum()` and visualize with a heatmap.
2. **Impute** numeric NaNs with the median (robust).
3. **Detect outliers** visually (boxplot) and numerically (IQR rule).
4. **Cap outliers** using `Series.clip(lower, upper)`.
5. **Transform** a skewed numeric column (e.g., grade or attendance) with `np.log1p` and compare distributions before/after.

---

## 8. Why Wrangling Education Data Is Tricky

- **Privacy** — student records carry sensitive info; care with anonymization.
- **Domain semantics** — score = 150 for "math out of 100" is clearly invalid.
- **Class size effects** — small class sizes have unstable summary statistics.
- **Demographic variables** — gender, ethnicity must be handled with sensitivity to avoid bias.
- **Time variability** — students change over years; longitudinal alignment is hard.

---

## 9. Viva Questions (40)

### A. Data Wrangling Fundamentals

**Q1. What is data wrangling?**
The process of transforming raw, messy data into a clean, structured form suitable for analysis or modeling. It includes handling missing values, fixing inconsistencies, detecting and treating outliers, encoding categorical variables, scaling numeric ones, and applying transformations to reduce skewness. Consumes 60–80% of total project time in real-world projects.

**Q2. Why is data wrangling important?**
"Garbage in, garbage out" — even the most sophisticated algorithm produces wrong predictions when fed bad data. Wrangling ensures the model trains on data that actually represents reality. It also surfaces hidden data-quality issues that would otherwise show up as confusing model behavior much later.

**Q3. What are the main steps in a wrangling pipeline?**
1. Load and inspect (shape, dtypes, summary stats).
2. Detect and handle missing values.
3. Detect and resolve inconsistencies (wrong format, duplicates, illegal values).
4. Detect and treat outliers.
5. Convert data types where needed.
6. Encode categorical variables.
7. Scale or transform numeric features.
8. Validate and save the cleaned dataset.

**Q4. What's the difference between data cleaning and data preprocessing?**
**Cleaning** specifically targets quality issues (missing values, duplicates, errors). **Preprocessing** is a broader umbrella that also includes scaling, encoding, and transformation — anything done to prepare data for a downstream task.

**Q5. What is GIGO?**
"Garbage In, Garbage Out" — a model can only be as good as the data it's trained on. Cleaning the input is non-negotiable.

### B. Missing Values

**Q6. What are the three patterns of missingness?**
- **MCAR** (Missing Completely At Random): independent of all data.
- **MAR** (Missing At Random): depends on observed variables but not on the missing value itself.
- **MNAR** (Missing Not At Random): depends on the missing value itself. Hardest because it biases inference no matter what you do.

**Q7. How do you detect missing values in pandas?**
`df.isnull()` returns a boolean DataFrame; `.sum()` counts NaN per column; `.sum().sum()` gives the total. A heatmap of `df.isnull()` shows missing-value patterns visually.

**Q8. When should you drop rows with missing values?**
Only when the percentage missing is small (< 5%) and the dataset is large enough to spare them. Dropping is risky because it can introduce selection bias, especially if missingness isn't random.

**Q9. When should you drop columns with missing values?**
When more than ~50–70% of a column is missing and the column isn't critical. Imputing such columns introduces too much fabricated data.

**Q10. What's the difference between mean and median imputation?**
- **Mean imputation** fills NaNs with the column's average. Best for symmetric, outlier-free data.
- **Median imputation** uses the middle value. Robust to outliers and skewed data — generally the safer default.
The mean is dragged by extreme values; the median ignores them.

**Q11. What is mode imputation?**
Filling NaNs with the most frequent value. Used for **categorical** columns where mean and median don't apply — e.g., filling missing port of embarkation with the most common port.

**Q12. What is forward-fill?**
`fillna(method='ffill')` propagates the last valid value forward. Used in time-series data when the value is assumed to persist until something changes.

**Q13. What is KNN imputation?**
A model-based method that finds the *k* nearest rows (by distance over the non-missing features) and uses their average for the missing value. More accurate than simple imputation when correlations between features are strong, but computationally heavier.

**Q14. Why might mean imputation distort the data?**
It reduces variance (every imputed value is exactly the mean), which artificially shrinks confidence intervals and standard errors. It also weakens correlations because imputed values introduce a constant in place of the relationship.

### C. Inconsistencies

**Q15. What are common categorical inconsistencies in academic data?**
- Multiple spellings of the same value ("Male", "male", "M", "MALE").
- Misspelled categories ("Femaile" instead of "Female").
- Different code conventions (1/0 vs Yes/No vs True/False).
Treat by standardizing to a canonical form via `.str.lower().str.strip().map({...})`.

**Q16. What are out-of-range values?**
Values that are technically present but obviously invalid — e.g., age = 200, score = -50 in a 0–100 range, attendance = 150% in a 0–100% range. Replace with NaN, then re-impute.

**Q17. How do you detect duplicates?**
`df.duplicated()` returns a boolean per row; `df.duplicated().sum()` counts. `df.drop_duplicates()` removes them. Sometimes "duplicates" are legitimate repeated observations; check before deleting.

### D. Outliers

**Q18. What is an outlier?**
A data point unusually far from the rest. May be measurement error, data-entry mistake, or genuine extreme observation. Outliers distort summary statistics and mislead distance-based models, but sometimes are the most important points (fraud, anomalies).

**Q19. How does the IQR method work?**
Compute Q1 and Q3 (25th and 75th percentiles). The interquartile range is `IQR = Q3 − Q1`. Any value below `Q1 − 1.5·IQR` or above `Q3 + 1.5·IQR` is flagged as an outlier. The 1.5 multiplier covers ~99.3% of a normal distribution.

**Q20. How does the Z-score method work?**
For each value compute `z = (x − mean) / std`. Values with `|z| > 3` are outliers. Best for roughly normal data; less reliable on skewed distributions.

**Q21. IQR vs Z-score — which is better?**
**IQR** is robust because percentiles are unaffected by extreme values. Works on any distribution. **Z-score** assumes normality, and the very outliers it tries to detect inflate the std. For skewed or unknown distributions, IQR is the safer default.

**Q22. What is winsorization?**
Replacing extreme values with the nearest non-outlier boundary (e.g., the IQR bounds). Preserves rows; trims tails. Preferable to deletion when the dataset is small or when the extreme values are real but disproportionate.

**Q23. When should you NOT remove outliers?**
- When they ARE the signal (fraud, anomalies, rare diseases).
- When the dataset is small enough that losing rows shrinks statistical power.
- When you suspect MNAR-style missingness.

**Q24. Are outlier rules sensitive to sample size?**
Yes. Small samples make IQR unstable. Large samples flag too many "normal" extremes. Adjust the multiplier or use a different method for very large or very small datasets.

### E. Data Transformation

**Q25. What is skewness?**
A measure of asymmetry. Positive skew (right-skewed) has a long right tail; negative (left-skewed) has a long left tail; zero is symmetric. Computed from the third standardized moment.

**Q26. What is a right-skewed distribution? Give an example.**
A distribution where most values cluster on the left and a long thin tail extends right. Mean > median because the tail pulls the mean up. Examples: income, fares, response times. Common when values are bounded below (often by zero) but unbounded above.

**Q27. Why apply a log transformation?**
To compress long right tails and pull the distribution toward symmetry. Often turns a right-skewed distribution into something close to normal, satisfying the assumptions of many statistical methods.

**Q28. What is `log1p` and why use it instead of `log`?**
`log1p(x) = log(1 + x)`. Avoids `log(0) = −∞` by adding 1 first. Use it whenever your data could include zeros (counts, durations, distances).

**Q29. When use square-root vs log?**
- **Sqrt** — mild right skew, especially count data. Less aggressive.
- **Log** — strong right skew. More aggressive compression of the tail.

**Q30. What is Box-Cox transformation?**
A power-transformation family parameterized by `λ`: covers `log` (λ=0), `sqrt` (λ=0.5), squaring (λ=2), etc. Optimal λ is chosen automatically to make the transformed data as close to normal as possible. Limitation: requires strictly positive input.

**Q31. What is Yeo-Johnson transformation?**
A generalization of Box-Cox that works with zero and negative values. Useful when your data spans a real range and you still want automatic skewness reduction.

### F. Scaling

**Q32. What is Min-Max scaling?**
Rescaling values to `[0, 1]`: `x_scaled = (x − min) / (max − min)`. Preserves the *shape* of the distribution; only rescales magnitude. Useful when you need bounded inputs.

**Q33. What is Standardization?**
Centering and scaling to mean 0, std 1: `x_scaled = (x − mean) / std`. Preserves the distribution shape. Used by algorithms that assume zero-centered data.

**Q34. Min-Max vs Standardization — when each?**
- **Min-Max** when you need a bounded range or when the algorithm doesn't assume normality (e.g., neural networks).
- **Standardization** when the algorithm benefits from zero-centered, unit-variance features (linear models, distance-based methods, PCA).
For tree-based models, neither is strictly necessary.

**Q35. What is the difference between scaling and transformation?**
Scaling changes the *range* without changing the *shape*. Transformation changes the *shape* (e.g., reshaping right-skewed data into something normal). The two are complementary — transform first to remove skew, then scale to control range.

### G. Practical & Code-Specific

**Q36. What's the difference between `inplace=True` and reassignment?**
- `df['col'].fillna(value, inplace=True)` is now discouraged in modern pandas due to chained-assignment / Copy-on-Write issues; can silently fail.
- `df['col'] = df['col'].fillna(value)` is the recommended idiom.

**Q37. Why copy the DataFrame before transforming?**
To preserve the original for comparison ("before" vs "after") and to avoid accidentally modifying it in place. `df.copy()` returns an independent DataFrame.

**Q38. Should you remove outliers before or after train/test split?**
**After** splitting — and only using statistics computed on the *training* set. Otherwise information from the test set leaks into the bounds. Same rule for imputation, scaling, and any parameter learned from data.

**Q39. How do you decide between dropping a column and imputing it?**
Look at percent missing, importance, and pattern. < 5% missing → impute. > 70% missing → consider dropping. In between → think about whether the column is a strong predictor and whether the missingness pattern is informative.

**Q40. How would you validate that wrangling worked?**
- `df.isnull().sum()` returns 0.
- `df.describe()` reveals no out-of-range values.
- Boxplots show no extreme outliers.
- Histograms show desired distribution shape.
- A small sample is spot-checked against domain rules.
- Downstream model performance is more stable across train/test re-splits than before wrangling.
