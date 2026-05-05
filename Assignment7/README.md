# Assignment 7 — Data Wrangling on Laptops Dataset

Notebook: `Assignment_7_Complete.ipynb`
Focus: five core wrangling operations — mean imputation, outlier detection (boxplot + IQR), capping (winsorization), data transformation (log, sqrt, Min-Max, Z-score), and skewness reduction.

---

## 1. What Is Data Wrangling?

**Data Wrangling** (also called *data cleaning*, *data munging*, or *data preprocessing*) is the process of transforming raw, messy, real-world data into a clean, structured form ready for analysis or modeling. In any real-world data-science project, this step typically takes **60–80% of total project time** — far more than the modeling itself. The famous saying captures why: **"garbage in, garbage out"** — even the best machine-learning algorithm fails if it is trained on dirty data.

Wrangling encompasses several distinct sub-tasks:

1. **Discovery** — understanding what columns exist, their types, and their valid ranges.
2. **Structuring** — reshaping the data (long ↔ wide, splitting columns, joining tables).
3. **Cleaning** — handling missing values, duplicates, and inconsistencies.
4. **Enriching** — adding derived columns or external data.
5. **Validating** — sanity-checking the result against domain rules.
6. **Publishing** — writing the cleaned dataset back out for downstream use.

---

## 2. Missing Values — Causes, Patterns, and Treatment

### 2.1 Why Data Is Missing
Missing data has several patterns, and the right treatment depends on which pattern applies:

- **MCAR (Missing Completely At Random)** — the missingness is independent of any variable. Example: a sensor randomly failing. Statistically the safest case; almost any imputation works.
- **MAR (Missing At Random)** — missingness depends on observed variables but not on the missing value itself. Example: men less likely to fill in "weight". You can model the missingness using observed variables.
- **MNAR (Missing Not At Random)** — missingness depends on the missing value itself. Example: people with high incomes refusing to disclose income. The hardest case; biases inference.

### 2.2 Strategies
- **Listwise deletion** (drop rows): simple but loses data, only safe with very few NaNs.
- **Mean imputation**: replace NaN with the column mean. Works best on roughly symmetric distributions; biased by outliers.
- **Median imputation**: replace NaN with the median. Robust to outliers — preferred for skewed data.
- **Mode imputation**: replace NaN with the most frequent value. Used for categorical columns.
- **Constant imputation**: replace with a placeholder like `'Unknown'` or `0`.
- **Forward-fill / backward-fill**: propagate the previous/next value. Used in time series.
- **Model-based imputation (KNN, Iterative)**: predict the missing value from the other features; more accurate but more complex.

### 2.3 When Mean vs Median?
- **Mean** — if the distribution is roughly normal/symmetric and free of extreme outliers.
- **Median** — if the distribution is skewed or has outliers, since the mean gets "dragged" by the tails.

---

## 3. Outliers — What They Are and Why They Matter

An **outlier** is a data point that is unusually far from the rest of the data. Outliers can:
- Distort summary statistics (mean and standard deviation are very sensitive).
- Mislead machine-learning models, especially distance-based ones (KNN, K-Means) and linear regression.
- Sometimes represent the most interesting parts of the data (fraud, anomalies, breakthroughs) — so don't just delete them blindly.

### 3.1 Detection Techniques

**Visual — Boxplot**
A boxplot draws a box from Q1 (25th percentile) to Q3 (75th percentile), with a line at the median. The "whiskers" extend by 1.5·IQR on each side. Anything past the whiskers is flagged as an outlier (drawn as dots).

**Numerical — IQR Rule**
- `Q1 = 25th percentile`, `Q3 = 75th percentile`
- `IQR = Q3 − Q1`
- Lower bound = `Q1 − 1.5·IQR`
- Upper bound = `Q3 + 1.5·IQR`
- Any value outside `[lower, upper]` is an outlier.

The 1.5 multiplier is the standard convention; it covers about 99.3% of a normal distribution.

**Numerical — Z-Score**
- `z = (x − mean) / std_dev`
- A value with `|z| > 3` is typically considered an outlier (covers 99.7% of a normal distribution by the empirical rule).
- Best for roughly normal data; less reliable on skewed distributions.

**Numerical — Modified Z-Score (using MAD)**
For robustness on skewed data: `0.6745 · (x − median) / MAD` where MAD is the median absolute deviation. Threshold around `|3.5|`. Used when the data has heavy tails.

### 3.2 Treatment

| Strategy | When | Trade-off |
|---|---|---|
| **Remove** | Confirmed errors, large dataset | Loses information |
| **Cap (Winsorize)** | Real but extreme values, small dataset | Preserves rows |
| **Transform** | Skewed distribution | Changes scale |
| **Bin** | Non-numeric analysis goal | Loses precision |
| **Keep** | Outlier is the signal (fraud) | Use robust models |

**Capping (Winsorization)** replaces values above the upper bound with the upper bound, and values below the lower bound with the lower bound. It preserves all rows — important for small datasets — at the cost of compressing the tails.

---

## 4. Data Transformation

Many statistical techniques (linear regression, t-tests, ANOVA) and some machine-learning models assume features are roughly **normally distributed**. Real-world data is often **skewed** — long-tailed in one direction. Transformations reshape the distribution toward normality.

### 4.1 Skewness
Skewness measures the asymmetry of a distribution:
- `skew == 0` — symmetric (normal).
- `skew > 0` — **right-skewed** (long tail on the right): mean > median.
- `skew < 0` — **left-skewed** (long tail on the left): mean < median.

Rule of thumb: `|skew| < 0.5` is fairly symmetric, `0.5–1` is moderate, `> 1` is highly skewed.

### 4.2 Common Transformations

| Transformation | Best for | Notes |
|---|---|---|
| **`log(x)`** | Right-skewed positive data | Undefined at 0; use `log1p(x) = log(1+x)` for safety |
| **`sqrt(x)`** | Mild right skew, count data | Less aggressive than log |
| **`1 / x`** | Strong right skew | Most aggressive; reverses ordering |
| **`x²` / `x³`** | Left-skewed data | Stretches the right side |
| **Box-Cox** | Positive data, automatic λ selection | Family that subsumes log, sqrt, square |
| **Yeo-Johnson** | Real data including zero/negative | Generalization of Box-Cox |

### 4.3 Scaling vs Transformation

| | What it does | Changes shape? |
|---|---|---|
| **Min-Max scaling** | Rescales to `[0, 1]` | No |
| **Standardization (Z-score)** | Mean 0, std 1 | No |
| **Log / sqrt** | Compresses long tails | Yes — pushes toward normal |
| **Robust scaling** | Uses median and IQR | No, but handles outliers gracefully |

Scaling rescales magnitude; transformation reshapes the *distribution*. They are different tools and often used together.

### 4.4 Why Normality Matters
- **Linear regression** assumes the residuals are normally distributed (for valid p-values and confidence intervals).
- **Principal Component Analysis** is sensitive to scale and roughly normal data.
- **K-Means** computes Euclidean distances and is biased by skewed or unscaled features.
- Tree-based models (Random Forest, Gradient Boosting, XGBoost) are largely **invariant** to monotonic transformations, so transformations matter less for them.

---

## 5. The 5 Operations Used in This Assignment

1. **Detect missing values** with `isnull().sum()` and visualize with a heatmap.
2. **Impute** numeric NaNs with the mean (used here because the data is roughly symmetric).
3. **Detect outliers** visually (boxplot) and numerically (IQR rule).
4. **Cap outliers** using `Series.clip(lower, upper)` to compress the tails.
5. **Transform** a skewed numeric column (e.g., price) with `np.log1p` and compare distributions before/after.

---

## 6. Viva Questions (40)

### A. Data Wrangling Fundamentals

**Q1. What is data wrangling?**
Data wrangling is the process of transforming raw, messy data into a clean, well-structured form suitable for analysis or modeling. It includes handling missing values, fixing inconsistencies, detecting and treating outliers, encoding categorical variables, scaling numeric ones, and applying transformations to reduce skewness. In real projects it consumes 60–80% of total time because real-world data is full of errors, gaps, and inconsistencies that no model can compensate for.

**Q2. Why is data wrangling important?**
Even the most sophisticated algorithm produces wrong predictions when fed bad data — the "garbage in, garbage out" principle. Models silently learn from whatever is in the training data, including errors and biases. Wrangling ensures the model trains on data that actually represents reality. It also surfaces hidden data-quality issues that would otherwise show up as confusing model behavior much later in the pipeline.

**Q3. What does GIGO stand for?**
"Garbage In, Garbage Out" — a model can only ever be as good as the data it's trained on. Cleaning the input is therefore non-negotiable.

**Q4. What are the main steps in a data-wrangling pipeline?**
1. Load and inspect (shape, dtypes, summary statistics).
2. Detect and handle missing values.
3. Detect and resolve inconsistencies (wrong format, duplicates, illegal values).
4. Detect and treat outliers.
5. Convert data types where needed.
6. Encode categorical variables.
7. Scale or transform numeric features.
8. Validate and save the cleaned dataset.

**Q5. What's the difference between data cleaning and data preprocessing?**
Cleaning specifically targets quality issues (missing values, duplicates, errors). Preprocessing is a broader umbrella that also includes scaling, encoding, and transformation — anything done to prepare data for a downstream task.

### B. Missing Values

**Q6. What are the three patterns of missingness?**
- **MCAR** (Missing Completely At Random): missingness independent of all data.
- **MAR** (Missing At Random): missingness depends on observed variables.
- **MNAR** (Missing Not At Random): missingness depends on the unobserved value itself. The hardest case because it biases inference no matter what you do.

**Q7. How do you detect missing values in pandas?**
`df.isnull()` returns a boolean DataFrame; `.sum()` counts NaN per column; `.sum().sum()` gives the total. A heatmap of `df.isnull()` with seaborn shows missing-value patterns visually — sometimes a column is missing in clusters, which hints at the cause.

**Q8. When should you drop rows with missing values?**
Only when the percentage missing is small (< 5%) and the dataset is large enough to spare them. Dropping is risky because it can introduce selection bias, especially if missingness isn't random.

**Q9. When should you drop columns with missing values?**
When more than ~50–70% of a column is missing and the column isn't critical. Imputing such columns introduces too much fabricated data; the imputed column ends up reflecting the imputation method more than reality.

**Q10. What's the difference between mean and median imputation?**
- **Mean imputation** fills NaNs with the column's average. Best for symmetric, outlier-free data.
- **Median imputation** uses the middle value. Robust to outliers and skewed data — generally the safer default for numeric columns in real-world data.
The mean is dragged toward extreme values (e.g., one billionaire in an income column); the median ignores them.

**Q11. What is mode imputation and when is it used?**
Mode imputation fills NaNs with the most frequent value of the column. Used for **categorical** columns where mean/median don't make sense — e.g., filling a missing port of embarkation with the most common port.

**Q12. What is forward-fill?**
`fillna(method='ffill')` propagates the last valid value forward. It's used in time-series data when the value is assumed to persist until something changes — e.g., a sensor reading that didn't update.

**Q13. What is KNN imputation?**
A model-based method that finds the *k* nearest rows (by distance over the non-missing features) and uses their average for the missing value. More accurate than simple imputation when correlations between features are strong, but computationally heavier.

**Q14. Why might mean imputation distort the data?**
It reduces the variance (every imputed value is exactly the mean), which artificially shrinks confidence intervals and standard errors of downstream models. It also weakens correlations because imputed values introduce a constant in place of the relationship.

### C. Outliers

**Q15. What is an outlier?**
A data point that lies an unusually large distance from the rest. Outliers may be measurement errors, data-entry mistakes, or genuine extreme observations (a billionaire in an income column, a fraud in a transaction column).

**Q16. Why are outliers a problem?**
They distort the mean and standard deviation, mislead distance-based models (KNN, K-Means), and can dominate the loss in linear regression because squared errors blow up at extreme values. They can hurt model generalization or — in the wrong context — be the signal you actually want to detect.

**Q17. How does the IQR method work?**
Compute Q1 (25th percentile) and Q3 (75th percentile). The interquartile range is `IQR = Q3 − Q1`. Any value below `Q1 − 1.5·IQR` or above `Q3 + 1.5·IQR` is flagged as an outlier. The 1.5 multiplier was chosen by Tukey to cover ~99.3% of a normal distribution — values past the whiskers are statistically unusual.

**Q18. How does the Z-score method work?**
For each value compute `z = (x − mean) / std_dev`. Values with `|z| > 3` are outliers (since a normal distribution has 99.7% of its mass within 3 standard deviations). Best applied to roughly normal data; less reliable on skewed distributions because the mean and std are themselves distorted.

**Q19. IQR vs Z-score — which is better?**
- **IQR** is robust because it uses percentiles, which are immune to extreme values. Works on any distribution.
- **Z-score** assumes (approximate) normality. The very outliers it tries to detect inflate the std and pull the mean, masking themselves.
For skewed or unknown distributions, IQR is the safer default.

**Q20. What is winsorization?**
Replacing extreme values with the nearest "non-outlier" boundary (e.g., the IQR bounds). All rows are kept, just trimmed at the tails. It's preferable to deletion when the dataset is small or when the extreme values are real but disproportionate.

**Q21. What's the difference between trimming and winsorizing?**
- **Trimming** removes the extreme rows entirely (loses data).
- **Winsorizing** caps them at a boundary (preserves rows but biases the tails toward the bound).

**Q22. When should you NOT remove outliers?**
When the outliers ARE the signal — fraud detection, anomaly detection, rare-disease prediction. Also when the dataset is small enough that losing rows materially shrinks statistical power. And when you suspect MNAR-style missingness in disguise.

### D. Data Transformation

**Q23. What is skewness?**
A statistical measure of asymmetry. Positive skew (right-skewed) has a long right tail; negative skew (left-skewed) has a long left tail; zero skew is symmetric. Skewness is computed from the third standardized moment and provides a single number to summarize asymmetry.

**Q24. What is a right-skewed distribution? Give a real example.**
A distribution where most values are concentrated on the left and a long thin tail extends to the right. The mean is greater than the median because the tail pulls the mean upward. Examples: income, house prices, time-to-event data, web-site visit duration.

**Q25. Why apply a log transformation?**
Log compresses large values more than small ones, pulling in long right tails. It often turns a right-skewed distribution into something close to normal, which satisfies the assumptions of linear regression, t-tests, ANOVA, and other classical statistics. It also stabilizes variance when variance grows with the mean.

**Q26. What is `log1p` and why use it instead of `log`?**
`log1p(x) = log(1 + x)`. It avoids `log(0) = −∞` by adding 1 before taking the log. Use it whenever your data could include zeros (counts, durations, distances) — common in real-world datasets.

**Q27. When would you use a square-root transformation?**
For mild right skew, especially with **count data** like number-of-orders or number-of-clicks. It's gentler than log; it shrinks tails less aggressively, so it's the right choice when the distribution is only mildly skewed.

**Q28. What is Min-Max scaling?**
Rescaling values to `[0, 1]`: `x_scaled = (x − min) / (max − min)`. It preserves the *shape* of the distribution; it only rescales magnitude. Useful when you need bounded inputs (e.g., neural network inputs, image pixel values).

**Q29. What is Standardization (Z-score scaling)?**
Centering and scaling to mean 0, std 1: `x_scaled = (x − mean) / std`. Preserves the distribution shape; doesn't reshape it. Used by algorithms that assume zero-centered data (logistic regression, SVM, PCA, K-Means).

**Q30. Min-Max vs Standardization — when do you use each?**
- **Min-Max** if you need a bounded range, or when the algorithm doesn't assume normality (e.g., neural networks).
- **Standardization** if the algorithm benefits from zero-centered, unit-variance features (most linear models, distance-based methods, PCA).
- For tree-based models, neither is strictly necessary because they're invariant to monotonic transformations of individual features.

**Q31. What is the difference between scaling and transformation?**
**Scaling** changes the *range* without changing the *shape*. **Transformation** changes the *shape* of the distribution (e.g., reshaping a right-skewed feature into something normal). The two are complementary — you may transform first to remove skew, then scale to control range.

**Q32. What is Box-Cox transformation?**
A power-transformation family parameterized by `λ`: covers `log` (`λ = 0`), `sqrt` (`λ = 0.5`), squaring (`λ = 2`), and others. The optimal `λ` is chosen automatically to make the transformed data as close to normal as possible. Limitation: requires strictly positive input.

**Q33. What is Yeo-Johnson transformation?**
A generalization of Box-Cox that works with zero and negative values. Useful when your data spans a real range — temperature, returns, residuals — and you still want an automatic skewness-reducing transformation.

### E. Conceptual Edge Cases

**Q34. What is the difference between `inplace=True` and reassignment in pandas?**
- `df['col'].fillna(value, inplace=True)` *attempts* to modify in place but is now discouraged in modern pandas due to chained assignment / Copy-on-Write issues; it can silently fail.
- `df['col'] = df['col'].fillna(value)` is the recommended idiom — it reassigns the cleaned column back to the DataFrame and avoids the warning.

**Q35. Why do we copy the DataFrame before transforming?**
To preserve the original for comparison ("before" vs "after") and to avoid accidentally modifying it in place. `df.copy()` returns an independent DataFrame so changes don't propagate back.

**Q36. Should you remove outliers before or after splitting train/test?**
After splitting — and only using statistics computed on the *training* set. Otherwise, information from the test set leaks into the bounds. The same rule applies to imputation, scaling, and any other parameter learned from data.

**Q37. Are outlier rules sensitive to sample size?**
Yes. With very small samples, IQR bounds are unstable; with very large samples, the IQR rule will flag many "normal" extreme values just because there are more of them. Adjust the multiplier (e.g., 3.0 instead of 1.5) for very large datasets.

**Q38. How do you decide between dropping a column and imputing it?**
Look at percent missing, importance, and pattern. < 5% missing → impute. > 70% missing → consider dropping. In between → think about whether the column is a strong predictor and whether the missingness pattern tells you something itself ("missing" can be a useful category).

**Q39. What does it mean for a model to be "robust"?**
A model is robust if its predictions don't change drastically because of outliers, missing data, or small perturbations in the input. Robust models include median-based estimators, tree-based models, and ridge/lasso regression. Linear regression with MSE is *not* robust because squared errors amplify outliers.

**Q40. How would you validate that wrangling worked?**
- `df.isnull().sum()` returns 0 across all columns.
- `df.describe()` reveals no out-of-range values (e.g., negative ages, scores > 100).
- Boxplots of every numeric column show no extreme dots beyond the whiskers (or only intentional ones).
- Histograms show the desired distribution shape (e.g., a previously skewed feature now looks roughly normal).
- A small sample of rows is spot-checked manually against domain rules.
- Downstream model performance is more stable across train/test re-splits than before wrangling.

---

## 11. Quick Reference — Key Concepts

### Decision Tree for Missing-Value Strategy

| % Missing | Action |
|---|---|
| < 1% | Drop rows or simple impute |
| 1–10% | Median (numeric) / mode (categorical) |
| 10–50% | Model-based imputation (KNN, MICE) |
| 50–70% | Consider dropping; impute carefully |
| > 70% | Almost always drop the column |

### Decision Tree for Outlier Strategy

| Situation | Action |
|---|---|
| Confirmed error | Remove or replace |
| Real but extreme | Cap (winsorize) |
| Real, distribution skewed | Transform (log) |
| Real, the signal you want | Keep, use robust models |
| Small sample | Cap rather than remove |

### Decision Tree for Transformation

| Distribution | Transformation |
|---|---|
| Strong right skew | `np.log1p(x)` |
| Mild right skew | `np.sqrt(x)` |
| Strong skew, automatic | Box-Cox / Yeo-Johnson |
| Left-skewed | `x²`, `x³` |
| Need range `[0, 1]` | Min-Max scaling |
| Need mean 0, std 1 | Standardization |

### Common Wrangling Mistakes

- Imputing with global statistics on the whole dataset (data leakage).
- Removing outliers based on test-set statistics.
- Using mean for skewed data.
- Not handling MNAR missingness (the missingness itself encodes information).
- Forgetting to handle duplicates.
- Dropping columns without checking domain importance.
- Applying log to data that contains zeros (use `log1p` instead).
- Treating placeholder codes (-9999, "N/A") as real values.
- Combining datasets without verifying schema and unit alignment.
- Failing to validate that wrangling worked (`isnull().sum() == 0`).

### Useful pandas Idioms

- `df.isnull().sum().sum()` — total missing cells.
- `df['col'] = df['col'].fillna(df['col'].median())` — recommended idiom (no `inplace=True`).
- `df['col'] = df['col'].clip(lower, upper)` — winsorize.
- `df['col_log'] = np.log1p(df['col'])` — safe log transform.
- `(df.isnull().sum() / len(df) * 100).round(2)` — percent missing.
- `df.select_dtypes(include='number')` — pick numeric columns only.

### Real-World Applications

- **Healthcare**: cleaning electronic health records before survival analysis.
- **Finance**: detecting and handling bad transactions, missing trades.
- **E-commerce**: cleaning product catalogs, customer profiles.
- **IoT**: handling sensor failures, dropouts, calibration drift.
- **Government**: cleaning census data, accident reports, tax records.
- **Marketing**: deduplicating customers across systems, aligning campaign data.

In every domain, the wrangling techniques are the same — only the domain-specific validation rules differ.
