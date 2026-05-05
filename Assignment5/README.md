# Assignment 5 — Linear Regression on Food Delivery Times

Notebook: `Assignment_5_Complete.ipynb`
Dataset: `food_delivery_times.csv`
Target: `Delivery_Time_min` (continuous)

---

## 1. Topic Deep-Dive

### 1.1 Supervised Learning
A model is **supervised** when training data has both inputs `X` and known outputs `y`. The two main supervised tasks are:

| Task | Output | Example |
|---|---|---|
| **Regression** | Continuous number | House price, delivery time, temperature |
| **Classification** | Discrete category | Spam/not-spam, click/no-click |

This assignment is a regression task — predicting *how many minutes* a delivery takes.

### 1.2 What Linear Regression Actually Does
Linear regression assumes a **linear relationship** between features and target:

> **y = b₀ + b₁·x₁ + b₂·x₂ + … + bₙ·xₙ + ε**

- `b₀` = **intercept** (bias) — predicted `y` when all features are 0.
- `b₁ … bₙ` = **coefficients (weights)** — how much `y` changes per unit change in that feature, holding others constant.
- `ε` = **error term** — irreducible noise.

Geometrically, the model is a straight **line** in 2D, a **plane** in 3D, and a **hyperplane** in higher dimensions.

### 1.3 Cost Function — Mean Squared Error (MSE)
The model picks `b₀ … bₙ` to minimize the average squared distance between predicted and actual values:

> **J(b) = (1/n) · Σ (yᵢ − ŷᵢ)²**

We square the error so positive and negative errors don't cancel, and so larger errors are penalized more heavily.

### 1.4 Two Ways to Solve Linear Regression
| Method | Library Class | How it works | Notes |
|---|---|---|---|
| **Closed-Form (OLS / Normal Equation)** | `LinearRegression` | One-shot matrix formula `b = (XᵀX)⁻¹ Xᵀy` | Exact answer, slow on huge datasets |
| **Gradient Descent (SGD)** | `SGDRegressor` | Iteratively nudges weights down the cost-function slope | Scales to millions of rows; needs scaled features and a learning rate |

### 1.5 The 5 Classical Assumptions of Linear Regression
| # | Assumption | What it means | How to check |
|---|---|---|---|
| 1 | **Linearity** | Relationship between X and y is linear | Scatter plots, residual plot |
| 2 | **Independence** | Observations are not correlated with each other | Domain reasoning, Durbin-Watson |
| 3 | **Homoscedasticity** | Errors have constant variance across all X | Residuals-vs-predicted plot should be a flat cloud |
| 4 | **Normality of errors** | Residuals follow a normal distribution | Q-Q plot, histogram of residuals |
| 5 | **No (perfect) multicollinearity** | Predictors aren't perfectly correlated | Correlation matrix, VIF |

### 1.6 Common Issues
- **Overfitting** — model memorizes training data, fails on test data. Mitigated with more data, fewer features, or regularization (Ridge / Lasso).
- **Underfitting** — model is too simple to capture the pattern. Add features, use a more complex model.
- **Multicollinearity** — two features are highly correlated, making coefficients unstable. Detected via the correlation matrix or **Variance Inflation Factor (VIF)**.
- **Dummy variable trap** — including all dummy columns from a one-hot encoding creates perfect multicollinearity. Always use `drop_first=True`.

### 1.7 Encoding Categorical Variables
ML models can't read text — categories must become numbers.

| Method | When | Function |
|---|---|---|
| **One-Hot Encoding** | Nominal data, no order (Weather, Vehicle_Type) | `pd.get_dummies(df, drop_first=True)` |
| **Label Encoding** | Binary or ordinal data (Low/Med/High) | `LabelEncoder().fit_transform(...)` |
| **Manual Mapping** | Custom ordering | `df['col'].map({'Low': 0, ...})` |

`drop_first=True` drops one dummy per variable as the **reference category** to avoid the dummy variable trap.

### 1.8 Why Train/Test Split
Evaluating on training data inflates performance — the model has already seen those answers. Holding back 20% as a **test set** gives an honest estimate of how it will generalize. The split should be **random** and **reproducible** (`random_state=42`).

### 1.9 Why Scale Features for Gradient Descent
Gradient descent takes steps proportional to the gradient. If `Income` ranges 0–80 000 and `Age` ranges 0–80, the cost surface is a long narrow valley — GD zigzags and converges slowly. **Standardization** (`StandardScaler`) reshapes each feature to mean 0, std 1 so all axes have the same scale.

`fit_transform` only on training data; `transform` only on test data — never fit on test (information leakage).

---

## 2. Libraries Used

| Library | Purpose |
|---|---|
| `pandas` | DataFrames, `read_csv`, `get_dummies`, `select_dtypes` |
| `numpy` | Numerical ops, `np.sqrt` for RMSE |
| `matplotlib.pyplot`, `seaborn` | Scatter plots, boxplots, residual plots |
| `sklearn.model_selection.train_test_split` | 80/20 split |
| `sklearn.linear_model.LinearRegression` | Closed-form OLS |
| `sklearn.linear_model.SGDRegressor` | Gradient descent LR |
| `sklearn.preprocessing.StandardScaler` | Mean-0, std-1 scaling |
| `sklearn.metrics` | `mean_squared_error`, `mean_absolute_error`, `r2_score` |

---

## 3. Functions & Methods Reference

### Loading & Inspection
- `pd.read_csv(path)` → DataFrame
- `df.shape`, `df.head()`, `df.info()`, `df.describe()`
- `df.select_dtypes(include='number')` / `include='object'` — partition by type

### Visualization
- `plt.scatter(x, y)` — scatter plot for numeric predictors vs target
- `sns.boxplot(x=cat_col, y='target')` — distribution of target across categories
- `sns.heatmap(df.corr(), annot=True)` — correlation heatmap

### Encoding
- `pd.get_dummies(df, drop_first=True)` — one-hot encode all object columns

### Modeling
- `LinearRegression().fit(X, y)` — closed-form fit
- `model.coef_` — array of learned weights
- `model.intercept_` — bias term
- `model.predict(X)` — predictions
- `SGDRegressor(max_iter=1000, learning_rate='constant', eta0=0.01)` — gradient descent variant

### Scaling
- `StandardScaler().fit_transform(X_train)` — `(x − μ)/σ`
- `scaler.transform(X_test)` — apply train-learned stats

### Metrics
| Metric | Formula | Best |
|---|---|---|
| MSE | `mean((y − ŷ)²)` | 0 |
| RMSE | `√MSE` | 0 |
| MAE | `mean(|y − ŷ|)` | 0 |
| R² | `1 − SS_res/SS_tot` | 1 |

---

## 4. Pipeline Summary
1. Load CSV → inspect shape, dtypes, summary stats.
2. Identify predictors (drop `Order_ID`) and target (`Delivery_Time_min`).
3. EDA — scatter plots for numeric, boxplots for categorical, correlation with target.
4. One-hot encode categoricals with `drop_first=True`.
5. 80/20 train-test split with `random_state=42`.
6. Train **Simple LR** on `Distance_km` only → high MSE.
7. Train **Multiple LR** on all features → much lower MSE.
8. Train **SGDRegressor** (gradient descent) on scaled features → matches multiple LR.
9. Evaluate all three with MSE, RMSE, MAE, R².
10. Plot Actual vs Predicted; residuals around zero indicate a well-fit model.

---

## 5. Viva Questions (40)

### A. Regression Fundamentals
**Q1. What is regression?**
A supervised learning task that predicts a continuous numeric value from input features.

**Q2. What is linear regression?**
An algorithm that fits a straight line/hyperplane through data: `y = b₀ + b₁·x₁ + … + bₙ·xₙ`. It assumes a linear relationship between features and target.

**Q3. What is the difference between simple and multiple linear regression?**
Simple LR uses one predictor (`y = b₀ + b₁x`). Multiple LR uses two or more.

**Q4. What is the difference between regression and classification?**
Regression outputs a continuous number; classification outputs a discrete category.

**Q5. What does the coefficient `b₁` represent?**
The expected change in `y` for a one-unit increase in `x₁`, holding all other features constant.

**Q6. What does the intercept `b₀` represent?**
The predicted `y` when all features are zero. It's a baseline.

**Q7. What is the cost function in linear regression?**
**Mean Squared Error (MSE)**: the average squared difference between predicted and actual values. The model picks coefficients that minimize this.

**Q8. Why do we square the errors instead of taking absolute value?**
Squaring (a) prevents positive and negative errors cancelling, (b) penalizes larger errors more, (c) makes the cost function differentiable everywhere — needed for gradient-based optimization.

### B. Solving the Regression
**Q9. What are the two ways to solve linear regression?**
1. **Closed-form (Normal Equation / OLS)**: `b = (XᵀX)⁻¹ Xᵀy`.
2. **Gradient Descent**: iteratively update weights to reduce the cost.

**Q10. When would you prefer gradient descent over the closed form?**
When the dataset is huge — inverting `XᵀX` becomes too slow / memory-heavy. GD scales linearly with rows.

**Q11. What is gradient descent?**
An iterative optimization algorithm that updates parameters in the direction opposite to the gradient of the cost function: `b ← b − α · ∂J/∂b`.

**Q12. What is the learning rate (`α` / `eta0`)?**
The step size in gradient descent. Too large → overshoots / diverges. Too small → very slow convergence.

**Q13. What is the difference between batch GD, SGD, and mini-batch GD?**
- **Batch GD** uses the entire dataset for each step (stable, slow).
- **SGD** uses one sample per step (noisy, fast).
- **Mini-batch GD** uses a small batch (a balance, used in practice).

**Q14. Why is feature scaling important for gradient descent?**
Without scaling, features with large ranges dominate the cost surface; GD zigzags and converges slowly. Scaling makes the cost contours roughly circular so GD goes straight to the minimum.

### C. Assumptions & Diagnostics
**Q15. What are the key assumptions of linear regression?**
Linearity, independence of errors, homoscedasticity (constant error variance), normality of residuals, no perfect multicollinearity.

**Q16. What is homoscedasticity?**
The variance of residuals is constant across all values of the predictors. The opposite (heteroscedasticity) makes coefficient errors unreliable.

**Q17. How do you check residual assumptions?**
- **Residuals-vs-predicted plot** for homoscedasticity (should be a flat cloud).
- **Q-Q plot / histogram** for normality.
- **Durbin-Watson** for independence.

**Q18. What is multicollinearity?**
When two or more predictors are highly correlated. It makes coefficients unstable (small data changes cause big coefficient changes) but does not hurt prediction.

**Q19. How do you detect multicollinearity?**
Correlation matrix, or **Variance Inflation Factor (VIF)**: `VIF > 5` is suspicious; `> 10` is severe.

### D. Encoding & Preprocessing
**Q20. Why must categorical variables be encoded?**
Linear regression operates on numbers; it cannot interpret text labels.

**Q21. What is one-hot encoding?**
Creating a separate 0/1 column for each category of a categorical variable.

**Q22. What is the dummy variable trap?**
Keeping all `k` dummies of a `k`-category variable creates perfect multicollinearity (they sum to 1). Drop one with `drop_first=True`.

**Q23. When would you use label encoding instead of one-hot?**
For ordinal variables where the order matters (Low/Medium/High) or for binary variables (Male/Female).

**Q24. What does `pd.get_dummies(drop_first=True)` do?**
One-hot encodes every object column and drops the first dummy of each variable as the reference category.

### E. Model Evaluation
**Q25. What is MAE?**
**Mean Absolute Error** = `mean(|y − ŷ|)`. Same units as the target. Less sensitive to outliers than MSE.

**Q26. What is MSE?**
**Mean Squared Error** = `mean((y − ŷ)²)`. Penalizes large errors heavily.

**Q27. What is RMSE?**
**Root Mean Squared Error** = `√MSE`. Same units as the target — easier to interpret than MSE.

**Q28. What is R² (coefficient of determination)?**
The proportion of variance in `y` explained by the model. `R² = 1 − SS_res/SS_tot`. `1` = perfect, `0` = no better than predicting the mean, negative = worse than the mean.

**Q29. Can R² be negative?**
Yes — if the model is worse than predicting the mean. Common when evaluating on test data with a poor model.

**Q30. What is adjusted R²?**
A version of R² that penalizes adding useless features. Ordinary R² always increases when you add features; adjusted R² only goes up if the new feature actually helps.

### F. Train/Test Split & Generalization
**Q31. Why split into train and test sets?**
Evaluating on training data is biased — the model has already seen the answers. The test set estimates real-world generalization.

**Q32. What does `random_state=42` do?**
Seeds the random number generator so the train/test split is reproducible.

**Q33. What is overfitting?**
Model fits training data so closely it captures noise. High train accuracy but low test accuracy.

**Q34. What is underfitting?**
Model is too simple to capture the underlying pattern — both train and test errors are high.

**Q35. How do you detect overfitting?**
Big gap between training error and test error (training error much lower).

### G. Comparison & Code-Specific
**Q36. Why is multiple LR's MSE lower than simple LR's in this notebook?**
Delivery time depends on multiple factors (distance, prep time, weather, traffic, vehicle). One predictor only captures part of the variation.

**Q37. Why do `LinearRegression` and `SGDRegressor` give nearly identical results?**
They optimize the same MSE objective. Closed-form finds the exact minimum; SGD with enough iterations converges to (almost) the same point.

**Q38. What is the difference between `fit_transform` and `transform`?**
`fit_transform` learns parameters (e.g., mean, std) AND applies them. `transform` only applies previously learned parameters. Always `fit_transform` on train, `transform` on test — to avoid leakage.

**Q39. What is data leakage?**
When information from outside the training set sneaks in (e.g., scaling using stats computed on the whole dataset). Causes optimistic test results that won't hold in production.

**Q40. How would you improve the model further?**
- Try regularization (Ridge / Lasso) for stability.
- Engineer features (e.g., distance × traffic interaction).
- Try non-linear models (Random Forest, Gradient Boosting).
- Collect more data or more relevant predictors.
