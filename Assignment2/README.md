# Assignment 2 — Data Wrangling II + Linear Regression

This folder contains two notebooks covering two different topics:

- `Data_Wrangling_II.ipynb` — **Data Wrangling II** on a synthetic Academic Performance dataset (50 students). Focus: missing values, inconsistencies, outliers, transformations.
- `Assignment_2_Complete.ipynb` — **Linear Regression** on a Coffee Shop Revenue dataset. Focus: simple LR, multiple LR, gradient descent.

---

## Part A — Data Wrangling II (`Data_Wrangling_II.ipynb`)

### Objective
1. Detect and handle **missing values and inconsistencies**.
2. Detect and treat **outliers** in numeric variables.
3. Apply **data transformations** to reduce skewness or rescale.

### Libraries
| Library | Purpose |
|---|---|
| `pandas`, `numpy` | DataFrame + numeric ops; `np.nan`, `np.log1p`, `np.where` |
| `matplotlib.pyplot`, `seaborn` | Boxplots, histograms, missing-value heatmaps |
| `scipy.stats` | `skew()`, `zscore()`, statistical tests |

### Concepts Covered

#### Missing Values
- `df.isnull().sum()` — count NaN per column.
- `sns.heatmap(df.isnull())` — visualize missing pattern as yellow stripes.
- `df['col'].fillna(df['col'].median())` — median fill (robust to outliers).

#### Inconsistencies
- `df['col'].unique()` / `.value_counts()` — list distinct values + their counts.
- `df['col'].str.lower().str.strip()` — string normalization.
- `df['col'].map({'m': 'Male', ...})` — collapse multiple spellings into clean categories.
- `df.loc[condition, 'col'] = np.nan` — flag out-of-range values as missing, then re-impute.

#### Outlier Detection & Treatment
- **Boxplots** with `sns.boxplot` — visual whisker-based detection.
- **IQR Rule**:
  - `Q1 = df['col'].quantile(0.25)`, `Q3 = df['col'].quantile(0.75)`
  - `IQR = Q3 - Q1`; bounds = `[Q1 − 1.5·IQR, Q3 + 1.5·IQR]`.
- **Z-Score Rule**: `|z| > 3` → outlier (works best on roughly normal data).
- **Capping (Winsorization)**: replace values outside the IQR bounds with the bound itself, using `np.where()` or `Series.clip()`. Preserves all rows — important for small datasets.

#### Data Transformation
- `Series.skew()` — measures asymmetry (`>0` right-skew, `<0` left-skew, `0` symmetric).
- `np.log1p(x)` — `log(1+x)`, safely handles zeros; compresses right tail.
- `np.sqrt(x)` — milder transformation for mild right skew.
- **Min-Max Scaling**: `(x − min) / (max − min)` → `[0, 1]`.
- **Standardization (Z-score)**: `(x − mean) / std` → mean 0, std 1.

| Transformation | When to use |
|---|---|
| `np.log1p(x)` | Right-skewed positive data |
| `np.sqrt(x)` | Mild right skew, count data |
| `1 / x` | Strong right skew |
| `x ** 2` | Left-skewed data |
| Min-Max | Rescale to `[0, 1]` |
| Standardization | Center on 0 with std 1 |

---

## Part B — Linear Regression (`Assignment_2_Complete.ipynb`)

### Objective
Predict `Daily_Revenue` for a coffee shop using its operational features. Compare **Simple LR**, **Multiple LR**, and **SGDRegressor**.

### Libraries
| Library | Purpose |
|---|---|
| `sklearn.model_selection.train_test_split` | 80/20 train/test split |
| `sklearn.linear_model.LinearRegression` | Closed-form linear regression |
| `sklearn.linear_model.SGDRegressor` | Linear regression via stochastic gradient descent |
| `sklearn.preprocessing.StandardScaler` | Mean-0, std-1 feature scaling |
| `sklearn.metrics` | `mean_squared_error`, `mean_absolute_error`, `r2_score` |

### Concepts Covered

#### Regression Basics
- **Regression** predicts a continuous numeric value (vs. classification → discrete label).
- **Simple LR**: one predictor → `y = b₀ + b₁·x`.
- **Multiple LR**: many predictors → `y = b₀ + b₁·x₁ + b₂·x₂ + … + bₙ·xₙ`.

#### EDA Before Modeling
- Scatter plots — check whether each predictor's relationship with the target looks linear.
- `df.corr()['target']` — quantify each predictor's linear relationship with the target.

#### Train/Test Split
- `train_test_split(X, y, test_size=0.2, random_state=42)` — hold back 20% for unbiased evaluation.

#### Model API
- `LinearRegression().fit(X_train, y_train)` — train.
- `model.coef_` / `model.intercept_` — learned weights and bias.
- `model.predict(X_test)` — make predictions.

#### Feature Scaling for Gradient Descent
- `StandardScaler().fit_transform(X_train)` — fit on train, then `.transform(X_test)` (no re-fit, prevents leakage).
- `SGDRegressor(max_iter=1000, learning_rate='constant', eta0=0.01)` — iterative LR; **requires** scaled features.

#### Evaluation Metrics
| Metric | Formula | Meaning |
|---|---|---|
| **MSE** | `mean((y − ŷ)²)` | Penalizes large errors heavily |
| **RMSE** | `√MSE` | Same units as the target |
| **MAE** | `mean(|y − ŷ|)` | Average absolute error |
| **R²** | `1 − SS_res/SS_tot` | Proportion of variance explained (closer to 1 is better) |

#### Key Insight
Multiple LR's MSE is much lower than Simple LR's because revenue depends on multiple factors (customers, marketing, foot traffic). SGD-trained LR ≈ closed-form LR after proper scaling.
