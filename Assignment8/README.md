# Assignment 8 — Linear Regression on Taxi Trip Pricing + Data Visualization I (Titanic)

This folder contains two notebooks:

- `Assignment_8_Complete.ipynb` — **Linear Regression** to predict taxi trip prices.
- `Data_Visualization_I _VIII.ipynb` — **Data Visualization** on the Titanic dataset, including a fare histogram.

---

## Part A — Linear Regression on Taxi Trip Pricing

### 1. The Big Picture
**Regression** is a supervised machine-learning task that predicts a *continuous numeric value* from input features. Unlike classification — which outputs a discrete label like spam/not-spam — regression outputs a real number such as a temperature, a house price, or, in this assignment, the **price of a taxi trip in dollars**.

The simplest and most fundamental regression algorithm is **Linear Regression**. Its central assumption is that the target is a *linear* function of the features. Geometrically that means a straight line in two dimensions, a flat plane in three, and a hyperplane in higher-dimensional feature spaces. Mathematically, with `n` features, the model is:

> y = b₀ + b₁·x₁ + b₂·x₂ + … + bₙ·xₙ + ε

`b₀` is the **intercept**, the predicted value when all features are zero. `b₁ … bₙ` are the **coefficients** (also called weights). Each coefficient `bⱼ` represents the change in `y` for a one-unit increase in feature `xⱼ`, holding everything else fixed. The term `ε` represents irreducible noise — sources of variation the model cannot explain.

### 2. Cost Function — Mean Squared Error
Linear regression learns the coefficients by minimizing a **cost function** — the **Mean Squared Error**:

> J(b) = (1/n) · Σ (yᵢ − ŷᵢ)²

Errors are squared so positive and negative deviations don't cancel, so larger errors are penalized more heavily, and so the cost surface is differentiable everywhere (which matters for gradient-based optimization). The squaring is what makes linear regression sensitive to outliers — a single very-far-off prediction contributes 100× more cost than ten predictions that are off by 10%.

### 3. Two Ways to Solve It

| Method | Class | Idea | Trade-off |
|---|---|---|---|
| **Closed-form (Normal Equation, OLS)** | `LinearRegression` | One matrix formula: `b = (XᵀX)⁻¹ Xᵀy` | Exact answer, slow on huge datasets (`O(n³)` matrix inversion) |
| **Gradient Descent (SGD)** | `SGDRegressor` | Iterative: nudge weights down the cost surface | Scales to millions of rows, requires scaled features and a learning rate |

Closed-form is preferred for moderate-sized datasets because it's exact. Gradient descent becomes attractive when you have millions of rows or hundreds of thousands of features.

### 4. Assumptions of Linear Regression
For its statistical guarantees (valid p-values, narrow confidence intervals) to hold, linear regression requires:

1. **Linearity** — the relationship between each predictor and the target is linear.
2. **Independence** — observations are not correlated with each other.
3. **Homoscedasticity** — residual variance is constant across all predicted values.
4. **Normality of residuals** — the errors follow a normal distribution.
5. **No (perfect) multicollinearity** — predictors aren't perfectly correlated with each other.

Mild violations are usually tolerable, but severe violations can produce misleading coefficients and bad predictions on unseen data.

### 5. Encoding Categorical Variables
Linear regression needs numeric inputs. Categorical columns (Weather, Vehicle_Type) must be encoded:

- **One-Hot Encoding** with `pd.get_dummies(drop_first=True)` for nominal data with no inherent order.
- **Label Encoding** for binary or ordinal variables.
- **Target / Mean Encoding** for high-cardinality categoricals (advanced).

`drop_first=True` drops one dummy per variable as the **reference category**. Without it, the dummies sum to 1 across each row, which causes **perfect multicollinearity** — the so-called **dummy variable trap**.

### 6. Train/Test Split and Generalization
We split the dataset 80/20 so we can evaluate on data the model never saw during training. This is essential because evaluating on training data is biased — the model has already seen those answers. The split should be random (`random_state=42` ensures reproducibility) and should preserve any structure that matters for the task (e.g., balance, time order).

### 7. Evaluation Metrics

| Metric | Formula | Meaning |
|---|---|---|
| **MAE** | `mean(|y − ŷ|)` | Avg absolute error in target units; less sensitive to outliers |
| **MSE** | `mean((y − ŷ)²)` | Penalizes large errors; matches the cost we minimize |
| **RMSE** | `√MSE` | Same units as the target, easier to interpret |
| **R²** | `1 − SS_res / SS_tot` | Fraction of variance explained (1.0 perfect, 0 no better than mean) |

R² can be negative on the test set if the model is worse than always predicting the mean — a useful red flag.

### 8. Gradient Descent and Feature Scaling
`SGDRegressor` performs the same optimization but iteratively. Each step it nudges weights against the gradient of the cost. The step size (`eta0` or learning rate `α`) is a hyperparameter:
- Too large → the algorithm bounces around or diverges.
- Too small → training takes forever to converge.

Gradient descent is **very sensitive to feature scale**. If `Distance` ranges 0–50 and `Income` ranges 0–80 000, the cost surface is a long narrow valley and SGD zigzags. **Standardization** (`StandardScaler`) reshapes each feature to mean 0 and std 1, making the cost surface roughly circular so SGD walks straight to the minimum.

The cardinal rule: `fit_transform` only on training data; `transform` only on test data. Fitting on test leaks information and produces unrealistically optimistic test scores — this is **data leakage**.

---

## Part B — Data Visualization I (Titanic Fare Histogram)

### 1. What Is Data Visualization?
**Data Visualization** is the practice of representing data graphically — through charts, plots, and graphs — to reveal patterns, relationships, and outliers that are difficult to spot in raw numbers. Visualization is a core part of **Exploratory Data Analysis (EDA)** and is typically the first thing a data scientist does after loading a dataset. Anscombe's quartet famously shows four datasets with identical means, variances, and correlations but completely different visual shapes — a reminder that summary statistics alone can mislead.

### 2. The Histogram
A **histogram** divides the range of a numeric variable into equal-width "bins" and shows the count (or frequency) of values in each bin as a vertical bar. It reveals:

- **Center** — where the data clusters.
- **Spread** — how wide the values range.
- **Shape** — symmetric, skewed, bimodal, uniform.
- **Outliers** — bars far away from the rest of the distribution.

The choice of **bin count** matters: too few hides structure, too many becomes noisy. Common rules of thumb: `√n` (square root), Sturges (`1 + log₂(n)`), Freedman-Diaconis (uses IQR for robustness on skewed data).

### 3. Distribution Shapes

| Shape | What it means | Example |
|---|---|---|
| **Symmetric / Normal** | Tails balanced around the center | Heights, IQ scores |
| **Right-skewed** | Long tail on the right; mean > median | Income, fares, response times |
| **Left-skewed** | Long tail on the left; mean < median | Test scores ceiling-bound |
| **Bimodal** | Two peaks | A mix of two populations |
| **Uniform** | Roughly flat | Random IDs |

The Titanic fare distribution is famously **right-skewed**: most passengers paid modest fares, but a few first-class passengers paid extreme amounts.

### 4. The Titanic Dataset
891 passengers; columns include `pclass`, `sex`, `age`, `fare`, `embarked`, `survived`, and more. It's a classic teaching dataset for both descriptive analysis and binary classification (predicting survival).

---

## 3. Viva Questions (40)

### A. Regression Concepts

**Q1. What is supervised learning, and where does linear regression fit in?**
Supervised learning is the branch of machine learning where the training data includes both inputs (features) and known correct outputs (labels). The model learns a function from features to labels by minimizing some error on the training data. The two main supervised tasks are regression (predicting a continuous number) and classification (predicting a discrete category). Linear regression is the simplest and most foundational regression algorithm — it assumes the target is a linear combination of features and fits the unique line/hyperplane that minimizes mean squared error.

**Q2. What is linear regression, mathematically?**
A model that predicts the target as a linear combination of features: `y = b₀ + b₁·x₁ + b₂·x₂ + … + bₙ·xₙ + ε`. `b₀` is the intercept, `b₁ … bₙ` are weights, and `ε` is irreducible noise. The "linear" in linear regression means linear in the *parameters*, not necessarily in the features — you can include `x²` or `log(x)` as features and still call it linear regression.

**Q3. What is the cost function used in linear regression?**
The **Mean Squared Error** (MSE): `(1/n) Σ (yᵢ − ŷᵢ)²`. We minimize MSE because squaring (a) prevents positive and negative errors from cancelling, (b) penalizes large errors more than small ones, and (c) makes the cost function differentiable everywhere — necessary for gradient-based optimization. Minimizing MSE is also equivalent to maximum-likelihood estimation under the assumption that errors are normally distributed.

**Q4. Why is MSE preferred over Mean Absolute Error in linear regression?**
MSE is differentiable everywhere; MAE has a kink at zero where the gradient is undefined. For linear regression, MSE has a closed-form solution (the Normal Equation) and connects nicely to maximum-likelihood theory under Gaussian noise. MAE is more robust to outliers but harder to optimize — it's used in median regression / quantile regression instead.

**Q5. What's the difference between simple and multiple linear regression?**
Simple linear regression uses one predictor: `y = b₀ + b₁·x`. Multiple linear regression uses two or more: `y = b₀ + b₁·x₁ + … + bₙ·xₙ`. The training procedure is identical — it's still ordinary least squares — but with more features the model can capture more variance, at the cost of higher risk of overfitting and multicollinearity issues.

**Q6. What does the coefficient `bⱼ` represent?**
The expected change in the target `y` for a one-unit increase in feature `xⱼ`, *holding all other features constant*. Sign and magnitude tell you direction and strength; for unscaled features the magnitudes aren't directly comparable across features.

**Q7. What does the intercept `b₀` represent?**
The predicted target when all features are zero. Sometimes that's a meaningful baseline (the price of a 0-distance taxi trip is the base fare); sometimes it's nonsensical (a 0-room house). The intercept's interpretability depends on whether all-zero features are physically plausible.

**Q8. What are the two methods to solve linear regression?**
**Closed-form / OLS / Normal Equation**: `b = (XᵀX)⁻¹ Xᵀy`. One-shot calculation, exact, but `O(n³)` to invert the matrix.
**Gradient Descent**: iterative — start with random weights, repeatedly nudge them in the direction that decreases cost. Scales to huge datasets but needs hyperparameter tuning and feature scaling.

**Q9. What is gradient descent in detail?**
An iterative optimization algorithm. At each step we compute the gradient of the cost function with respect to the parameters and update them in the opposite direction: `b ← b − α · ∂J/∂b`. The learning rate `α` controls the step size. Variants — batch, stochastic (one sample at a time), mini-batch (a small batch) — trade between stability and speed.

**Q10. What's the role of the learning rate?**
The learning rate is the step size in gradient descent. Too large and the algorithm overshoots the minimum and may diverge. Too small and convergence is painfully slow. Typical values lie between 0.001 and 0.1; advanced schemes like adaptive learning rates (Adam, RMSProp) auto-adjust it.

**Q11. Why does gradient descent need feature scaling?**
The gradient direction is sensitive to feature magnitudes. If one feature ranges 0–1 and another 0–100 000, the cost surface becomes a long narrow valley; gradient descent zigzags and converges slowly. Standardization (mean 0, std 1) makes the cost contours roughly circular so gradient descent walks straight toward the minimum.

### B. Assumptions and Diagnostics

**Q12. What are the five classical assumptions of linear regression?**
Linearity (the relationship is actually linear), independence (observations are independent), homoscedasticity (residual variance is constant), normality of residuals (errors are normally distributed), and no perfect multicollinearity (predictors aren't perfectly correlated).

**Q13. What is homoscedasticity and why does it matter?**
Homoscedasticity means the variance of the residuals is constant across all predicted values. The opposite — heteroscedasticity — means the spread of errors changes with the predicted value. When residuals fan out as predictions grow, classical confidence intervals and p-values become unreliable. You diagnose it by plotting residuals against predicted values; the cloud should be flat. Fixes include log-transforming the target or using weighted least squares.

**Q14. How do you check if residuals are normally distributed?**
Plot a Q-Q plot of the residuals against a normal distribution — points should fall along the 45° line. Also useful: a histogram of residuals (should look bell-shaped) or formal tests like Shapiro-Wilk or Jarque-Bera. Mild violations are usually tolerable; severe violations make confidence intervals unreliable.

**Q15. What is multicollinearity?**
When two or more predictors are highly correlated, providing redundant information. Coefficients become unstable — small changes in the data produce large swings in the coefficients — and individual coefficient interpretations become unreliable. It does *not* hurt predictive accuracy, only the interpretability of individual weights.

**Q16. How do you detect multicollinearity?**
Look at the correlation matrix (a pair with `|r| > 0.8` is a red flag) or compute the **Variance Inflation Factor** (VIF) for each feature. `VIF > 5` is suspicious; `VIF > 10` is severe. Fix by dropping one of the correlated features, combining them (e.g., total = a + b), or using regularization (Ridge handles multicollinearity gracefully).

**Q17. What is the dummy variable trap?**
If you keep all `k` dummy variables for a `k`-category column, they sum to 1 across each row, which means one is a perfect linear combination of the others. This produces perfect multicollinearity and breaks `(XᵀX)⁻¹`. The standard fix: drop one dummy as the reference category (`pd.get_dummies(drop_first=True)`).

### C. Evaluation

**Q18. Define MSE, RMSE, MAE, and R².**
- **MSE** = `mean((y − ŷ)²)` — the cost we minimize; penalizes large errors heavily.
- **RMSE** = `√MSE` — same units as the target, easier to interpret.
- **MAE** = `mean(|y − ŷ|)` — average absolute error; robust to outliers.
- **R²** = `1 − SS_res / SS_tot` — fraction of variance explained; 1.0 perfect, 0 means no better than the mean baseline, negative means worse.

**Q19. What does an R² of 0.85 mean intuitively?**
The model explains 85% of the variance in the target. The remaining 15% comes from noise, missing features, and model misspecification. R² should always be reported alongside RMSE/MAE because R² alone says nothing about absolute error magnitude.

**Q20. Can R² be negative?**
Yes — on the test set. It happens when the model is worse than predicting the mean of the test target. A negative R² is a strong signal of overfitting, distribution shift, or a buggy model.

**Q21. What is adjusted R²?**
A penalized version of R² that accounts for the number of features: `1 − (1 − R²)·(n − 1) / (n − p − 1)`. Plain R² always increases when you add features (even useless ones); adjusted R² only increases if the new feature actually improves the fit beyond the penalty. Use adjusted R² to compare models with different feature counts.

### D. Train/Test and Generalization

**Q22. Why split data into train and test sets?**
To get an unbiased estimate of how well the model generalizes to unseen data. Evaluating on training data is biased upward — the model has already seen those answers. The test set is held out and used only for final evaluation; the training set is used to fit parameters.

**Q23. What is overfitting?**
A model fits the training data so closely that it captures noise specific to that sample. It has low training error but high test error. Caused by too-complex models, too-many features, or too-little data. Mitigations: regularization, cross-validation, more data, simpler models.

**Q24. What is underfitting?**
The model is too simple to capture the underlying pattern. Both training and test errors are high. Mitigations: add features, use a more flexible model, reduce regularization.

**Q25. What is data leakage?**
When information from outside the training set sneaks into training. Common forms: scaling using statistics computed on the full dataset, using a feature that was computed using the target, or using future information in time-series data. It produces optimistic test results that don't hold in production.

### E. Visualization Concepts (Part B)

**Q26. What is data visualization and why is it important?**
Data visualization translates numerical data into visual form so the human visual system — by far the most powerful pattern-recognition machine we have — can detect shapes, trends, clusters, and outliers. It is a core part of EDA. Anscombe's quartet (four datasets with identical means, variances, and correlations but very different shapes) demonstrates that summary statistics alone can be deeply misleading without visuals.

**Q27. What is a histogram?**
A chart that splits a numeric variable's range into equal-width bins and shows the count of values in each bin as bars. It reveals the distribution's center, spread, shape, and outliers.

**Q28. What is the difference between a histogram and a bar chart?**
A **bar chart** plots categorical data — bars correspond to discrete labels, gaps between them reflect the categorical nature. A **histogram** plots a continuous numeric variable — bars touch each other because the underlying scale is continuous and bins are adjacent intervals.

**Q29. How do you choose the right number of bins?**
Common rules of thumb: `√n`, Sturges (`1 + log₂(n)`), or Freedman-Diaconis (`2·IQR / n^(1/3)`, robust to outliers). The goal is enough resolution to see structure without becoming noisy. In practice you experiment with 10–50 bins and pick the one that tells the clearest story.

**Q30. What is skewness as visible in a histogram?**
Asymmetry. A **right-skewed** histogram has a long thin tail on the right; the mean is greater than the median. **Left-skewed** has a tail on the left; the mean is less than the median. **Symmetric** distributions look like a bell. The Titanic fare histogram is strongly right-skewed because a small number of first-class passengers paid very high fares.

**Q31. What is kernel density estimation (KDE)?**
A smooth alternative to a histogram. KDE places a small bell curve at each data point and sums them, producing a smooth density curve. It avoids the bin-boundary artifacts of histograms and shows the underlying distribution more clearly. seaborn's `histplot(kde=True)` overlays both.

**Q32. What is a boxplot?**
A compact summary of a distribution showing the median, quartiles, and outliers. The box spans Q1 to Q3 (the middle 50% of data); the line inside is the median; the whiskers extend 1.5·IQR; dots beyond the whiskers are flagged outliers.

**Q33. When would you prefer a boxplot over a histogram?**
When comparing distributions across categories — boxplots side-by-side make distributions easy to compare at a glance. Histograms are better for examining a single distribution's *shape* in detail.

**Q34. What is a scatter plot used for?**
To visualize the relationship between two numeric variables. Patterns reveal correlation (positive, negative, or none), curvature (linear vs non-linear), clustering, and outliers. It's the standard EDA tool before fitting any regression.

**Q35. What is a heatmap?**
A grid of colored cells representing a matrix of values. In data science, heatmaps are often used to display correlation matrices: red (or warm) for positive correlation, blue (or cool) for negative, with intensity scaled by magnitude. They make patterns in dozens of pairwise relationships visible at a glance.

**Q36. What is the difference between matplotlib and seaborn?**
**matplotlib** is the foundational plotting library — low-level and verbose, but extremely flexible. **seaborn** sits on top of matplotlib and provides higher-level statistical plotting — boxplots, violinplots, KDEs, distribution plots — with nicer default styling and direct support for pandas DataFrames. In practice you use them together: seaborn for the plot, matplotlib for fine-tuning.

**Q37. What does a long-tailed distribution suggest about your data?**
A long tail signals that a small number of observations are very different from the majority. This often points to a need for transformation (`log1p`), special outlier handling, or a robust model. It also flags that mean-based statistics will be biased; report the median instead.

### F. Code-Specific

**Q38. Why does multiple LR almost always outperform simple LR on real datasets?**
Real-world targets depend on multiple factors. With one predictor, the model captures only the variance explained by that single feature. Adding more relevant predictors lets the model account for additional variance, lowering MSE. Of course, adding *irrelevant* predictors doesn't help and can cause overfitting — the right features matter more than the count.

**Q39. Why do `LinearRegression` and `SGDRegressor` give nearly identical results?**
They optimize the same MSE objective. `LinearRegression` finds the exact closed-form minimum; `SGDRegressor` converges to (almost) the same point through gradient descent, given enough iterations and a reasonable learning rate. Differences come from numerical precision and SGD's stochasticity.

**Q40. How would you go beyond linear regression to improve a price-prediction model?**
Try regularized variants (Ridge for general stability, Lasso for feature selection). Engineer interaction or polynomial features. Try non-linear models — Random Forests, Gradient Boosting (XGBoost, LightGBM, CatBoost) often dominate tabular data. Use cross-validation to pick hyperparameters, and check for distribution shift between train and production.
