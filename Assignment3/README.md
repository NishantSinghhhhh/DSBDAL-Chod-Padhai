# Assignment 3 — Descriptive Statistics + Logistic Regression

This folder contains two notebooks covering two different topics:

- `Descriptive_Statistics_ I I I.ipynb` — **Descriptive Statistics** on the Iris dataset. Focus: groupby aggregations, measures of central tendency and variability.
- `Assignment_3_Complete.ipynb` — **Logistic Regression** on a Wine Quality dataset. Focus: binary classification, confusion matrix, classification metrics.

---

## Part A — Descriptive Statistics (`Descriptive_Statistics_ I I I.ipynb`)

### Objective
1. Compute summary statistics (mean, median, min, max, std) for numeric variables grouped by a categorical variable.
2. Display percentile, mean, and standard deviation for each Iris species (`Iris-setosa`, `Iris-versicolor`, `Iris-virginica`).

### Libraries
| Library | Purpose |
|---|---|
| `pandas` | `read_csv()`, `groupby()`, `describe()`, `agg()` |
| `numpy` | Numerical operations (used internally by pandas) |

### Concepts Covered

#### Categorical vs Numeric Variables
- **Categorical (qualitative)** — labeled groups, here `species`.
- **Numeric (quantitative)** — measurements: `sepal_length`, `sepal_width`, `petal_length`, `petal_width`.

#### GroupBy Aggregations
`df.groupby('species')` splits the DataFrame into one sub-DataFrame per category. Calling an aggregation then computes that statistic per group.

| Function | What it returns |
|---|---|
| `.mean(numeric_only=True)` | Arithmetic average per group |
| `.median(numeric_only=True)` | 50th percentile (robust to outliers) |
| `.min()` / `.max()` | Extremes per group |
| `.std(numeric_only=True)` | Spread around the mean per group |

#### Combined Aggregation with `agg()`
- `df.groupby('species').agg(['mean', 'median', 'min', 'max', 'std'])` — all stats at once in a tidy MultiIndex table.

#### Building Lists / Dicts of Per-Category Values
- `.tolist()` — convert a Series to a Python list.
- `.to_dict()` — convert a Series to `{category: value}`.
- Example: `df.groupby('species')['sepal_length'].mean().tolist()` → one mean per species.

#### Per-Category Statistical Summaries
- Boolean filtering: `df[df['species'] == 'Iris-setosa']` selects rows of one category.
- `.describe()` — count, mean, std, min, 25%, 50% (median), 75%, max — all percentile/spread metrics in one call.
- One-liner alternative: `df.groupby('species').describe()` returns the same for every species at once.

#### Measures of Central Tendency vs Variability
- **Central tendency**: mean, median (where the data is centered).
- **Variability**: standard deviation, IQR (how spread out the data is).
- **Percentiles**: 25%, 50%, 75% — divide sorted data into quarters.

---

## Part B — Logistic Regression (`Assignment_3_Complete.ipynb`)

### Objective
Predict whether a wine is **good** (`quality ≥ 6`) or **bad** from 11 chemical features using Logistic Regression.

### Libraries
| Library | Purpose |
|---|---|
| `sklearn.model_selection.train_test_split` | Stratified train/test split |
| `sklearn.preprocessing.StandardScaler` | Feature scaling (essential for logistic regression) |
| `sklearn.linear_model.LogisticRegression` | The classifier |
| `sklearn.metrics` | `confusion_matrix`, `accuracy_score`, `precision_score`, `recall_score`, `classification_report` |

### Concepts Covered

#### Classification vs Regression
- **Classification** predicts a discrete category (spam/not-spam, good/bad).
- **Regression** predicts a continuous number.

#### Logistic Regression
- Linear classifier that wraps a linear combination through the **sigmoid function** `σ(z) = 1 / (1 + e⁻ᶻ)` to output a probability in `[0, 1]`.
- Threshold (default 0.5) converts probability → class label.
- "Regression" in the name is historical; in practice it's a classification algorithm.

#### Building a Binary Target
- `df['good'] = (df['quality'] >= 6).astype(int)` — collapse a multi-valued ordinal into 0/1.

#### Stratified Split
- `train_test_split(..., stratify=y)` — preserves class balance in both train and test sets. Important when classes are imbalanced.

#### Feature Scaling
- `StandardScaler` → mean 0, std 1 per feature.
- **Fit on train only**, then `.transform(test)` — never call `fit` on test data (prevents leakage).

#### Model API
- `LogisticRegression(max_iter=1000).fit(X_train_scaled, y_train)`.
- `model.predict(X)` — predicted class label.
- `model.predict_proba(X)[:, 1]` — probability of class 1.
- `model.coef_` / `model.intercept_` — learned weights (positive coef → pushes toward class 1).

#### Confusion Matrix
A 2×2 table of `(actual, predicted)`:

|                       | Predicted Bad (0) | Predicted Good (1) |
|---|---|---|
| **Actual Bad (0)**    | TN (correct)      | FP (false alarm)   |
| **Actual Good (1)**   | FN (missed)       | TP (correct)       |

- `confusion_matrix(y_test, y_pred)` returns the 2×2 array.
- `cm.ravel()` flattens to `(TN, FP, FN, TP)`.

#### Performance Metrics
| Metric | Formula | Interpretation |
|---|---|---|
| **Accuracy** | `(TP + TN) / Total` | Overall correctness |
| **Error Rate** | `(FP + FN) / Total` | `1 − Accuracy` |
| **Precision** | `TP / (TP + FP)` | Of positives predicted, how many are right |
| **Recall (Sensitivity)** | `TP / (TP + FN)` | Of true positives, how many were caught |
| **F1** | `2·P·R / (P+R)` | Harmonic mean of precision and recall |

- `classification_report` prints precision, recall, F1, support for every class.
