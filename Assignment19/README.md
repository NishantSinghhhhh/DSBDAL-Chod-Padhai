# Assignment 19 — Linear Regression on Boston Housing Dataset

Notebook: `Assignment_19_Complete.ipynb`
Dataset: Boston Housing — 506 samples, 14 features.
Target: `MEDV` — median home value in $1000s.

---

## 1. The Big Picture

This assignment uses **Linear Regression** to predict the median home price (`MEDV`) of Boston neighborhoods based on 13 features. The Boston Housing dataset has been a teaching staple since the 1970s — it's small enough to work with interactively, has rich features (some numeric, some near-binary), exhibits real correlations between features and target, and includes the classic challenges of multicollinearity and outliers.

### 1.1 What Is Regression?
**Regression** is a supervised machine-learning task that predicts a *continuous numeric value* from input features. Unlike classification, which outputs a discrete category, regression outputs a real number — house prices, temperatures, time-to-failure, demand forecasts.

### 1.2 What Is Linear Regression?
**Linear Regression** assumes the target is a linear combination of features:

> y = b₀ + b₁·x₁ + b₂·x₂ + … + bₙ·xₙ + ε

- `b₀` is the **intercept** — predicted value when all features are zero.
- `bⱼ` are **coefficients** — change in `y` per unit change in `xⱼ`, holding others constant.
- `ε` is irreducible noise.

Geometrically, the model is a hyperplane through the data; the coefficients define its orientation.

### 1.3 What Linear Means
The "linear" in linear regression refers to linearity in the **parameters**, not the features. You can include `x²`, `log(x)`, or interaction terms `x₁·x₂` as features and the model is still called linear regression — because each parameter still contributes additively.

---

## 2. The Boston Dataset

### 2.1 Features

| Column | Meaning |
|---|---|
| CRIM | Per-capita crime rate by town |
| ZN | Proportion of residential land zoned for lots over 25 000 sq.ft |
| INDUS | Proportion of non-retail business acres |
| CHAS | Charles River dummy (1 if tract bounds the river, else 0) |
| NOX | Nitric-oxide concentration (parts per 10 million) |
| RM | Average number of rooms per dwelling |
| AGE | Proportion of owner-occupied units built before 1940 |
| DIS | Weighted distance to five Boston employment centers |
| RAD | Index of accessibility to radial highways |
| TAX | Property-tax rate per $10 000 |
| PTRATIO | Pupil-teacher ratio by town |
| B | `1000(Bk − 0.63)²` where `Bk` is the proportion of African-American residents |
| LSTAT | Percentage of lower-status population |
| **MEDV** | **Median home value in $1000s — TARGET** |

### 2.2 Known Caveats
The Boston Housing dataset has well-documented **ethical problems**, particularly the `B` column, which encodes a racially-coded function under the assumption that segregation has a positive effect on housing prices. Modern teaching materials often use it with caveats; some have moved to alternative housing datasets (e.g., California Housing) that don't carry the same baggage. We use it here because the focus is on linear regression, not on the specific data ethics.

---

## 3. Cost Function — Mean Squared Error

The model picks coefficients to minimize:

> J(b) = (1/n) · Σ (yᵢ − ŷᵢ)²

Squaring serves three purposes:

- Prevents positive and negative errors from cancelling.
- Penalizes larger errors more (squared > linear in magnitude).
- Makes the cost function differentiable everywhere — important for gradient-based optimization.

Minimizing MSE is equivalent to **maximum likelihood estimation** under the assumption that errors are normally distributed.

---

## 4. Two Ways to Solve It

### 4.1 Closed-Form (Normal Equation / OLS)

> b = (XᵀX)⁻¹ · Xᵀy

Computed via matrix inversion. Exact answer, but `O(n³)` in the number of features — slow for very high dimensions. Used by `LinearRegression` in scikit-learn.

### 4.2 Gradient Descent
Iterative: nudge weights against the gradient of the cost. Scales to millions of rows. Requires:

- A **learning rate** (step size).
- **Feature scaling** (otherwise the optimization landscape is poorly conditioned).
- Multiple passes through the data.

Variants: batch GD (one update per pass), SGD (one sample per update), mini-batch GD (a small batch per update).

---

## 5. Assumptions of Linear Regression

For the model's statistical guarantees to hold:

| # | Assumption | What It Means |
|---|---|---|
| 1 | Linearity | Each predictor relates linearly to the target |
| 2 | Independence | Observations are uncorrelated with each other |
| 3 | Homoscedasticity | Residual variance is constant across all predictions |
| 4 | Normality of residuals | Errors are normally distributed |
| 5 | No (perfect) multicollinearity | Predictors aren't perfectly correlated |

Mild violations are tolerable; severe violations make coefficients and confidence intervals unreliable.

---

## 6. Common Issues

### 6.1 Overfitting
Model fits training data so closely it captures noise. Diagnostic: high training accuracy, low test accuracy. Mitigations: regularization (Ridge, Lasso), more data, fewer features.

### 6.2 Underfitting
Model is too simple to capture the pattern. Both train and test errors are high. Mitigations: more features, more flexible model.

### 6.3 Multicollinearity
Predictors are highly correlated, providing redundant information. Coefficients become unstable; small data changes produce big coefficient swings. Detected via correlation matrix or **Variance Inflation Factor (VIF > 5)**. Fixed by dropping a feature, combining features, or using Ridge regularization.

### 6.4 Outliers
Squared error is very sensitive to outliers — one extreme observation can pull the coefficients dramatically. Robust regression methods (Huber, RANSAC) mitigate this.

---

## 7. Evaluation Metrics

| Metric | Formula | Meaning |
|---|---|---|
| **MAE** | `mean(|y − ŷ|)` | Mean absolute error; robust |
| **MSE** | `mean((y − ŷ)²)` | Cost we minimize; penalizes large errors |
| **RMSE** | `√MSE` | Same units as target; easier to interpret |
| **R²** | `1 − SS_res/SS_tot` | Proportion of variance explained |
| **Adjusted R²** | Penalized R² for feature count | Compare models with different feature counts |

R² of 1.0 = perfect fit; 0 = no better than predicting the mean; negative = worse than the mean (bad sign).

---

## 8. Diagnostic Plots

### 8.1 Actual vs Predicted
A scatter plot of `y_test` vs `y_pred`. A reference 45° line shows where perfect predictions would lie. Points scattered around the line = good; systematic deviation = model issue.

### 8.2 Residual Plot
A scatter plot of residuals (`y − ŷ`) vs predicted values. Should look like a flat random cloud around zero. Patterns:

- **Funnel shape** → heteroscedasticity.
- **Curve** → non-linearity not captured by the model.
- **Outliers** → individual observations the model can't fit.

### 8.3 Q-Q Plot of Residuals
Tests normality. Points should fall on a 45° line if residuals are normal. Heavy tails or curvature suggests non-normal errors.

---

## 9. Regularization (Beyond Plain LR)

### 9.1 Ridge Regression (L2)
Adds a penalty proportional to the **sum of squared coefficients** to the cost:

> J = MSE + λ · Σ bⱼ²

Shrinks coefficients toward zero. Handles multicollinearity gracefully. Doesn't produce sparse models.

### 9.2 Lasso Regression (L1)
Adds a penalty proportional to the **sum of absolute coefficients**:

> J = MSE + λ · Σ |bⱼ|

Can drive coefficients exactly to zero — produces a sparse, automatically-feature-selected model.

### 9.3 Elastic Net
A weighted combination of L1 and L2 penalties. Often the best of both worlds when features are correlated.

---

## 10. Viva Questions (40)

### A. Regression Fundamentals

**Q1. What is regression?**
A supervised learning task that predicts a continuous numeric value from input features. Distinguished from classification (predicts a discrete category) by the output type. Linear regression is the simplest and most foundational regression algorithm.

**Q2. What is linear regression?**
An algorithm that fits a straight line (or hyperplane) through data, expressed as `y = b₀ + b₁·x₁ + … + bₙ·xₙ`. Assumes a linear relationship between features and target. Trained by minimizing the mean squared error between predictions and actual values.

**Q3. What is the difference between simple and multiple linear regression?**
- **Simple LR**: one predictor — `y = b₀ + b₁·x`.
- **Multiple LR**: two or more predictors.
The math is identical; multiple LR just generalizes to higher dimensions. Most real-world problems have multiple features.

**Q4. What does the coefficient `bⱼ` represent?**
The expected change in the target `y` for a one-unit increase in feature `xⱼ`, *holding all other features constant*. The "holding others constant" is critical — it's why we can have a positive bivariate correlation but a negative coefficient when other features are added.

**Q5. What does the intercept `b₀` represent?**
The predicted target when all features are zero. Sometimes a meaningful baseline; sometimes nonsensical (a 0-room house has 0 price?). Interpretability depends on whether all-zero features are physically plausible.

**Q6. What is the cost function used in linear regression?**
**Mean Squared Error (MSE)**: `(1/n) Σ (yᵢ − ŷᵢ)²`. Squaring penalizes large errors more, prevents positive/negative cancellation, and makes the cost differentiable for gradient-based optimization. Equivalent to maximum likelihood estimation under Gaussian noise.

**Q7. Why MSE rather than MAE?**
MSE is differentiable everywhere; MAE has a kink at zero. MSE has a closed-form solution (the Normal Equation); MAE doesn't. MSE connects to Gaussian-noise maximum likelihood. MAE is more robust to outliers but harder to optimize and provides no closed-form.

### B. Solving the Regression

**Q8. What are the two ways to solve linear regression?**
- **Closed-form (Normal Equation)**: `b = (XᵀX)⁻¹ Xᵀy`. One matrix inversion. Exact, but `O(n³)` in features.
- **Gradient Descent**: iterative, scales to large datasets but needs feature scaling and learning-rate tuning.

**Q9. What is the Normal Equation?**
The closed-form analytical solution to linear regression: `b = (XᵀX)⁻¹ Xᵀy`. Comes from setting the gradient of MSE to zero and solving. Works perfectly when `XᵀX` is invertible (no perfect multicollinearity).

**Q10. When does the Normal Equation fail?**
When `XᵀX` is **singular** (not invertible) — caused by perfect multicollinearity (features are exact linear combinations of others) or having more features than samples (`n_features > n_samples`). In practice, scikit-learn uses a pseudo-inverse to handle these gracefully.

**Q11. What is gradient descent?**
An iterative optimization algorithm that updates parameters in the direction opposite to the cost-function gradient: `b ← b − α·∇J(b)`. The learning rate `α` controls step size. Convergence depends on convexity (linear regression is convex, so it converges to the global minimum).

**Q12. What's the role of the learning rate?**
Step size in gradient descent. Too large → overshoots, may diverge. Too small → very slow convergence. Typical values: 0.001 to 0.1. Modern adaptive methods (Adam, RMSProp) auto-tune it.

**Q13. Why does gradient descent need feature scaling?**
Without scaling, features with larger magnitudes dominate the cost surface, creating a long narrow valley. Gradient descent zigzags slowly. Standardization (mean 0, std 1) makes the cost contours roughly circular so GD walks straight to the minimum.

### C. Assumptions

**Q14. What are the five classical assumptions of linear regression?**
Linearity (relationship is actually linear), independence (observations are uncorrelated), homoscedasticity (constant residual variance), normality of residuals (errors are Gaussian), and no perfect multicollinearity.

**Q15. What is homoscedasticity?**
The variance of residuals is constant across all predictor values. The opposite — heteroscedasticity — means residual spread changes with predicted value. Diagnostic: residual plot. Fix: log-transform target, weighted least squares, or use a robust regression.

**Q16. How do you check if residuals are normally distributed?**
- **Q-Q plot** of residuals against a normal distribution.
- **Histogram** of residuals — should look bell-shaped.
- **Statistical tests** like Shapiro-Wilk, Jarque-Bera.
Mild violations are usually tolerable; severe non-normality affects confidence intervals more than point predictions.

**Q17. What is multicollinearity?**
When two or more predictors are highly correlated. Coefficients become unstable — small data changes produce large coefficient swings. Doesn't hurt prediction accuracy, only the interpretation of individual coefficients.

**Q18. How do you detect multicollinearity?**
- Correlation matrix (`|r| > 0.8` is suspicious).
- **Variance Inflation Factor (VIF)**: `VIF > 5` is concerning, `> 10` is severe.
- Condition number of the design matrix.
Fixes: drop a feature, combine features, use Ridge regularization.

### D. Evaluation

**Q19. Define MAE, MSE, RMSE, and R².**
- **MAE** = `mean(|y − ŷ|)` — robust, in target units.
- **MSE** = `mean((y − ŷ)²)` — penalizes large errors, in squared units.
- **RMSE** = `√MSE` — in target units, more interpretable than MSE.
- **R²** = `1 − SS_res/SS_tot` — proportion of variance explained.

**Q20. What does R² = 0.7 mean intuitively?**
The model explains 70% of the variance in the target; 30% remains unexplained (noise + missing features). R² alone isn't enough — combine it with RMSE so you know absolute error magnitude too.

**Q21. Can R² be negative?**
Yes — on the test set, when the model is worse than predicting the mean. A negative R² is a strong red flag of overfitting, distribution shift, or a buggy model.

**Q22. What is adjusted R²?**
A penalized R² that accounts for the number of features: `1 − (1 − R²)·(n − 1)/(n − p − 1)`. Plain R² always increases when you add features (even useless ones); adjusted R² only goes up if the new feature actually improves the fit beyond the penalty.

**Q23. Why is RMSE more interpretable than MSE?**
RMSE is in the same units as the target. If predicting house prices in thousands of dollars, RMSE = $5K means "typical prediction error is about five thousand dollars". MSE in $K² is meaningless intuitively.

### E. Train/Test Split & Generalization

**Q24. Why split into train and test sets?**
To get an unbiased estimate of generalization to unseen data. Evaluating on training data is biased upward; the model has already seen those answers. The test set is held out and used only for final evaluation.

**Q25. What is overfitting?**
When the model fits training data so closely it memorizes noise. Training error is low but test error is high. Caused by too-complex models, too many features, or too little data. Mitigations: regularization, cross-validation, more data, simpler models.

**Q26. What is underfitting?**
When the model is too simple to capture the pattern. Both training and test errors are high. Mitigations: add features, more flexible model, reduce regularization.

**Q27. What is data leakage?**
When information from outside the training set sneaks into the model. Common forms: scaling using statistics from the full dataset, using a feature derived from the target, using future data in time-series. Produces optimistic test results that don't hold in production.

**Q28. What is k-fold cross-validation?**
Split the data into `k` folds. Train on `k−1` and test on the remaining fold; rotate so every fold is the test set once. Average the `k` test scores. More reliable than a single train-test split, especially on small datasets.

### F. Diagnostics

**Q29. What is a residual?**
The difference between the actual and predicted value: `residual = y − ŷ`. Positive residual means the model under-predicted; negative means over-predicted. Residuals should be small and random.

**Q30. What does a residual plot look like for a well-fit model?**
A flat random cloud of points around zero, with no visible pattern. The variance should be roughly constant across all predicted values.

**Q31. What does a "funnel-shaped" residual plot indicate?**
Heteroscedasticity — residual variance changes with the predicted value. The residuals fan out as predictions grow (or shrink). Confidence intervals become unreliable. Fix by log-transforming the target or using weighted least squares.

**Q32. What does a curve in the residual plot indicate?**
The model failed to capture a non-linear relationship. Add polynomial features, try a non-linear model (Random Forest, Gradient Boosting), or transform features.

### G. Beyond Plain LR

**Q33. What is Ridge regression?**
Linear regression with **L2 regularization**: cost = MSE + λ·Σbⱼ². The penalty shrinks coefficients toward zero, handling multicollinearity gracefully and reducing overfitting. Doesn't produce sparse models — coefficients shrink but rarely reach zero.

**Q34. What is Lasso regression?**
Linear regression with **L1 regularization**: cost = MSE + λ·Σ|bⱼ|. Can drive coefficients exactly to zero, producing a sparse, automatically-feature-selected model. Useful when you suspect many features are irrelevant.

**Q35. What is Elastic Net?**
A weighted combination of L1 and L2 penalties: cost = MSE + λ₁·Σ|bⱼ| + λ₂·Σbⱼ². Often the best of both worlds — handles multicollinearity (L2) and produces sparsity (L1). Good default when features are correlated and you want feature selection.

**Q36. When would you use Ridge vs Lasso?**
- **Ridge**: when most features matter and you want stable coefficients despite correlation.
- **Lasso**: when you suspect many features are irrelevant and want automatic feature selection.
- **Elastic Net**: when both apply.

### H. Boston-Specific & Code

**Q37. Which features in Boston Housing are most predictive of MEDV?**
- **LSTAT** (% lower-status population) — strongly negative correlation.
- **RM** (average rooms) — strongly positive correlation.
- **PTRATIO**, **NOX**, **CRIM** — moderately negative.
These are usually the top features in any Boston-Housing model.

**Q38. Why would you log-transform the target (MEDV)?**
The MEDV distribution has a heavy right tail and is capped at $50K (top-coded). Log-transforming produces a more symmetric distribution and reduces the influence of extreme values. Models on `log(MEDV)` often outperform those on raw MEDV.

**Q39. What ethical issues exist with the Boston Housing dataset?**
The `B` feature encodes racial composition under a problematic assumption. The dataset reflects 1970s redlining and segregation patterns. Modern courses often use it with explicit caveats or substitute alternative datasets (California Housing, Ames Housing).

**Q40. How would you improve a linear regression model for Boston?**
- Try **regularized variants** (Ridge, Lasso, Elastic Net).
- **Engineer features**: log-transform skewed features, create interaction terms (RM × LSTAT).
- **Handle outliers**: identify and either cap or use robust regression (Huber).
- **Try non-linear models** — Random Forest or Gradient Boosting often outperform linear regression on tabular data.
- **Cross-validate** to choose hyperparameters more robustly than a single train-test split.
- **Use ensemble methods** — stack a linear model with a non-linear one.
