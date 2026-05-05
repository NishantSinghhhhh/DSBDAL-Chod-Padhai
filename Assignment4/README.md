# Assignment 4 — Linear Regression (Boston Housing) + Data Wrangling (Heart Disease)

This folder contains two notebooks covering two different topics:

- `Linear_Regression_Boston_IV.ipynb` — **Linear Regression** on the Boston Housing dataset. Focus: predicting house prices, EDA, regression metrics.
- `Assignment_4_Complete.ipynb` — **Data Wrangling** on a Heart Disease dataset. Focus: missing values, outliers, transformations.

---

## Part A — Linear Regression on Boston Housing (`Linear_Regression_Boston_IV.ipynb`)

### Objective
Predict the median home price (`MEDV`) using Linear Regression on the Boston Housing dataset (506 rows × 14 features).

### Libraries
| Library | Purpose |
|---|---|
| `pandas`, `numpy` | DataFrame + numerical operations |
| `matplotlib`, `seaborn` | Heatmaps, scatter plots, residual plots |
| `sklearn.model_selection.train_test_split` | 80/20 split |
| `sklearn.linear_model.LinearRegression` | The regression model |
| `sklearn.metrics` | `mean_absolute_error`, `mean_squared_error`, `r2_score` |

### Dataset Variables (Selected)
| Column | Meaning |
|---|---|
| `CRIM` | Per-capita crime rate by town |
| `RM` | Average rooms per dwelling |
| `AGE` | Proportion of owner-occupied units built pre-1940 |
| `DIS` | Distance to employment centers |
| `LSTAT` | % lower-status population |
| `MEDV` | **Target** — median home value (in $1000s) |

### Concepts Covered

#### Exploratory Data Analysis
- `df.isnull().sum()` — confirm no missing values.
- `df.describe()` — feature ranges and summary stats.
- `df.corr()` — correlation matrix; values in `[−1, 1]`.
- `sns.heatmap(df.corr(), annot=True, cmap='coolwarm')` — visualize feature-to-feature correlations.
- `df.corr()['MEDV'].sort_values()` — pick the strongest predictors of the target.

#### Modeling
- `X = df.drop('MEDV', axis=1)` / `y = df['MEDV']` — split features from target.
- `train_test_split(X, y, test_size=0.2, random_state=42)` — reproducible 80/20 split.
- `LinearRegression().fit(X_train, y_train)` — fits one coefficient per feature plus an intercept.
- `model.coef_` — coefficients (positive → feature pushes price up; negative → pushes price down).
- `model.intercept_` — the bias term `b₀`.
- `model.predict(X_test)` — returns predicted `MEDV` values.

#### Evaluation Metrics
| Metric | Formula | Interpretation |
|---|---|---|
| **MAE** | `mean(|y − ŷ|)` | Avg absolute error, same units as target |
| **MSE** | `mean((y − ŷ)²)` | Penalizes large errors more |
| **RMSE** | `√MSE` | Same units as target, easier to interpret |
| **R²** | `1 − SS_res/SS_tot` | Proportion of variance explained (1.0 = perfect) |

#### Diagnostic Plots
- **Actual vs Predicted scatter** + 45° reference line — points on the diagonal = perfect predictions.
- **Residual plot** (`y_test − y_pred` vs `y_pred`) — should be a random cloud around 0. A pattern would indicate the linearity assumption is violated.

---

## Part B — Data Wrangling on Heart Disease (`Assignment_4_Complete.ipynb`)

### Objective
Apply a complete wrangling pipeline to `heart_disease.csv`:
1. Handle missing values and biologically impossible entries.
2. Detect and treat outliers.
3. Apply transformations to reduce skewness.

### Libraries
| Library | Purpose |
|---|---|
| `pandas`, `numpy` | DataFrame + `np.nan`, `np.log1p`, `np.sqrt` |
| `matplotlib`, `seaborn` | Boxplots, histograms, missing-value heatmaps |

### Concepts Covered

#### Missing Values & Inconsistencies
- `df.isnull().sum()` — count NaN per column.
- `sns.heatmap(df.isnull(), cbar=False, cmap='viridis')` — visualize missing cells.
- Domain-based validation: medical ranges (age 0–120, cholesterol 0–600, resting BP 0–250).
- `df.loc[condition, 'col'] = np.nan` — flag invalid values as missing.
- `df[col] = df[col].fillna(df[col].median())` — robust median imputation (avoids the chained-assignment / Copy-on-Write warning).

#### Outlier Detection & Treatment

**Visual** — Boxplots before vs after capping:
```python
sns.boxplot(y=df[col])
```

**Numeric — IQR Rule:**
- `Q1 = series.quantile(0.25)`, `Q3 = series.quantile(0.75)`
- `IQR = Q3 − Q1`
- Bounds = `[Q1 − 1.5·IQR, Q3 + 1.5·IQR]`

**Treatment — Capping (Winsorization):**
- `series.clip(lower, upper)` — replace values outside the IQR bounds with the bounds. Preserves all rows.

#### Data Transformation
- `Series.skew()` — quantifies asymmetry. Positive = right tail; negative = left tail.
- `np.log1p(x)` — `log(1+x)`, safely handles zeros; compresses right-skewed distributions.
- `np.sqrt(x)` — milder transform than log.
- **Min-Max scaling**: `(x − min)/(max − min)` → `[0, 1]`.
- **Z-score (standardization)**: `(x − mean)/std` → mean 0, std 1.

| Technique | Best for |
|---|---|
| `np.log1p` | Strong right skew, positive data with possible zeros |
| `np.sqrt` | Mild right skew, count data |
| Min-Max | Bounded `[0, 1]` rescaling |
| Z-score | Centering + unit variance for ML algorithms |

### Pipeline Summary
1. Load CSV → check shape, dtypes, summary stats.
2. Detect missing values + biologically invalid entries → flag as `NaN` → impute with median.
3. Detect outliers visually (boxplot) and numerically (IQR) → cap with `Series.clip()`.
4. Measure skewness → log-transform skewed features (e.g., cholesterol).
5. Final clean DataFrame ready for analysis or modeling.
