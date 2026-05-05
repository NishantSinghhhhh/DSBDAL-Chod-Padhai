# Assignment 9 — Logistic Regression on Social Network Ads + Data Visualization II (Titanic)

This folder contains two notebooks:

- `Assignment_9_Complete.ipynb` — **Logistic Regression** to predict whether a social-network user will purchase a product based on age and salary.
- `Data_Visualization_II_IX.ipynb` — A grouped **boxplot** showing how age varies across `sex × survival` on the Titanic.

---

## Part A — Logistic Regression

### 1. Classification vs Regression
**Classification** is a supervised-learning task that predicts a *discrete category* — a label drawn from a fixed, finite set. Spam filters classify email as spam/not-spam; medical models classify a tumor as malignant/benign; recommendation systems classify a user as likely-to-purchase or not. **Regression**, by contrast, predicts a continuous numeric value (price, temperature, time). Both are supervised because the training data includes the ground-truth label, and the model learns a function from features to label.

When the label is binary (two classes) the problem is **binary classification**; when there are three or more, it's **multi-class classification**. This assignment is binary: did the user purchase (1) or not (0)?

### 2. Why Logistic Regression Despite the Name
Logistic regression is, despite its name, a **classifier**. The "regression" comes from the math: the model fits a linear regression on the *log-odds* of the class probability, not on the class itself. So we still write `z = b₀ + b₁·x₁ + … + bₙ·xₙ`, but we don't predict `z` directly — we transform it through the **sigmoid (logistic)** function:

> σ(z) = 1 / (1 + e⁻ᶻ)

The sigmoid takes any real number and squashes it into `[0, 1]`. So whatever value `z` takes, the output of the sigmoid is interpretable as a probability — specifically, the probability that the input belongs to class 1.

To produce a class label we apply a **decision threshold**, typically 0.5: predict class 1 if `σ(z) ≥ 0.5`, else class 0. Lowering the threshold makes the model more eager to predict class 1 (more recall, less precision); raising it makes it more conservative.

### 3. The Logit and Log-Odds
The model is linear in the **log-odds**:

> log(p / (1 − p)) = b₀ + b₁·x₁ + … + bₙ·xₙ

`p / (1 − p)` is the **odds**: how much more likely class 1 is than class 0. Taking the log gives the **logit**, which can range over all real numbers, matching the range of a linear combination. When you exponentiate a coefficient `bⱼ`, you get an **odds ratio**: `e^bⱼ` is the multiplicative effect on the odds for a one-unit increase in feature `xⱼ`.

### 4. Cost Function — Binary Cross-Entropy
For linear regression the cost was MSE. For logistic regression we use **Log Loss** (also called **Binary Cross-Entropy**):

> J(b) = − (1/n) · Σ [ yᵢ · log(p̂ᵢ) + (1 − yᵢ) · log(1 − p̂ᵢ) ]

Why not MSE? Because MSE composed with the sigmoid produces a **non-convex** cost — gradient descent can get stuck in local minima. Log loss is convex in the parameters, so gradient descent always finds the global minimum. Log loss also matches **maximum likelihood estimation** under the Bernoulli noise assumption, giving the procedure a solid statistical foundation.

The shape of log loss matters intuitively: when the true label is 1 and the predicted probability is near 1, the loss is small; when the prediction is near 0 (very confidently wrong), the loss explodes. Log loss heavily penalizes confident wrong predictions, which is what we want in a probabilistic model.

### 5. Decision Boundary
The decision boundary is the set of points where the model is exactly indifferent between the two classes — `σ(z) = 0.5`, equivalently `z = 0`, which is `b₀ + b₁·x₁ + … + bₙ·xₙ = 0`. In two dimensions that's a straight line; in three, a plane; in higher dimensions, a hyperplane. Logistic regression is therefore a **linear classifier** — it can only separate classes that are linearly separable (or close to it).

To handle non-linear boundaries with logistic regression, you engineer features (`x²`, `x₁·x₂`, polynomial terms, splines, kernel transforms) before training. Otherwise switch to a non-linear classifier.

### 6. Class Imbalance
Real-world classification problems are rarely 50/50. Fraud detection might be 0.1% positive; medical screening 5%. With an imbalanced dataset:
- Accuracy becomes misleading — predicting "no fraud" always gives 99.9% accuracy and zero usefulness.
- The classifier learns to favor the majority class because the loss is dominated by it.

Mitigations include:
- **Stratified split** (`stratify=y`) to keep class proportions consistent in train and test.
- **Class weights** (`class_weight='balanced'`) so the model pays proportionally more attention to the minority class.
- **Resampling** — oversampling the minority class (SMOTE) or undersampling the majority.
- **Threshold tuning** — instead of 0.5, choose a threshold that maximizes F1 or business utility.

### 7. Why Scale Features
Features in this dataset have very different magnitudes — Age in roughly 18–60, EstimatedSalary in 15 000–150 000. Without scaling:
- Gradient-based solvers converge slowly because the cost surface is elongated.
- Some optimizers (`lbfgs`, `saga`) become numerically unstable.
- Coefficients on the larger-magnitude features look small, but only because of scale, not unimportance.

`StandardScaler` rescales each feature to mean 0, std 1. The cardinal rule: `fit_transform` only on training data; `transform` only on test data. Fitting on test contaminates the training procedure with test-set information — that's **data leakage**.

### 8. Confusion Matrix and Metrics

|                       | Predicted 0 | Predicted 1 |
|---|---|---|
| **Actual 0**          | TN | FP |
| **Actual 1**          | FN | TP |

- **TN (True Negative)** — correctly predicted no purchase.
- **FP (False Positive)** — predicted purchase, but didn't (false alarm). Type I error.
- **FN (False Negative)** — predicted no purchase, but did (missed customer). Type II error.
- **TP (True Positive)** — correctly predicted purchase.

From these four numbers we derive every standard binary-classification metric:

| Metric | Formula | Meaning |
|---|---|---|
| Accuracy | `(TP + TN) / Total` | Overall correctness — misleading on imbalanced data |
| Error Rate | `(FP + FN) / Total` | `1 − Accuracy` |
| Precision | `TP / (TP + FP)` | When we say "positive", how often we're right |
| Recall (Sensitivity / TPR) | `TP / (TP + FN)` | Of true positives, how many we caught |
| Specificity (TNR) | `TN / (TN + FP)` | Of true negatives, how many we caught |
| F1 Score | `2·P·R / (P + R)` | Harmonic mean of precision and recall |
| ROC-AUC | Area under TPR-vs-FPR curve | Threshold-independent quality |

The **precision–recall trade-off**: lowering the threshold catches more positives (recall up) but flags more false alarms (precision down). The right balance is application-dependent — in spam filtering, we favor precision (don't quarantine real email); in cancer screening, we favor recall (don't miss a disease).

---

## Part B — Data Visualization II (Titanic Boxplot)

### 1. The Boxplot
The **boxplot** (or box-and-whisker plot), invented by John Tukey in 1977, summarizes a distribution with five numbers:

| Component | Meaning |
|---|---|
| Bottom of box | Q1 (25th percentile) |
| Middle line | Median (50th percentile) |
| Top of box | Q3 (75th percentile) |
| Whiskers | Extend to `Q1 − 1.5·IQR` and `Q3 + 1.5·IQR` |
| Dots beyond whiskers | Outliers |

The **interquartile range** (IQR = Q3 − Q1) measures spread of the middle 50% of the data — a robust alternative to standard deviation. The position of the median inside the box hints at skew: if it's pushed toward the bottom, the distribution is right-skewed; pushed toward the top, left-skewed; centered, symmetric.

### 2. Grouped Boxplots
A grouped boxplot draws side-by-side boxes for each level of a categorical variable, optionally further split by a second category (with `hue`). It lets you compare distributions across groups at a glance — invaluable for EDA when you want to see how a numeric variable differs across categories.

In this assignment we plot **age × sex × survival** on the Titanic. The grouped boxplot reveals patterns like:
- Female survivors tend to be slightly older than female non-survivors.
- Male survivors are notably younger than male non-survivors (children-first effect).
- The age distribution of female passengers is broader than that of male passengers.

These insights are hard to extract from a table of summary statistics but jump out of a well-constructed boxplot.

### 3. Boxplot vs Other Plots

| Plot | Best For |
|---|---|
| Histogram | Examining the shape of one distribution |
| Boxplot | Comparing distributions across groups |
| Violin plot | Distribution shape *and* group comparison combined |
| Strip / swarm plot | Showing every individual data point |
| KDE plot | Smooth density estimate |

---

## 3. Viva Questions (40)

### A. Classification & Logistic Regression Fundamentals

**Q1. What is classification, and how does it differ from regression?**
Classification is a supervised-learning task that predicts a discrete category from a fixed, finite set. Regression predicts a continuous number. Spam/not-spam, malignant/benign, click/no-click are classification; price prediction, temperature forecasting, time-to-completion are regression. Both are supervised — they need labeled training data — but they use different loss functions and produce different output types.

**Q2. What is logistic regression?**
A linear classification algorithm that predicts the probability of belonging to a class using the sigmoid function and assigns the class label by thresholding. It models the log-odds of the class as a linear combination of features, so even though it produces a class label, the underlying machinery is a linear regression on a transformed scale.

**Q3. Why is it called "regression" if it does classification?**
Historical naming. The *math* fits a linear regression on the log-odds (the logit). The original work by Cox in the 1950s framed it as a regression problem on a transformed scale. The output is a class label, but the linear regression part of the name reflects the model's internal structure.

**Q4. What is the sigmoid (logistic) function and why is it used?**
`σ(z) = 1 / (1 + e⁻ᶻ)`. It maps any real number to `[0, 1]`, making the output interpretable as a probability. It's smooth and differentiable everywhere, and its derivative has a particularly nice form `σ(z)·(1 − σ(z))` that makes gradient computation efficient.

**Q5. What is `z` in logistic regression?**
The linear combination `z = b₀ + b₁·x₁ + … + bₙ·xₙ`. Often called the logit or the "raw score". Positive `z` pushes the probability toward 1; negative `z` pushes it toward 0; `z = 0` gives probability 0.5 — the decision boundary.

**Q6. What is the default decision threshold and when would you change it?**
Default is 0.5: if the predicted probability is ≥ 0.5, predict class 1, else class 0. You'd change it to optimize a specific business metric. For cancer screening, lowering to 0.3 catches more positives at the cost of more false alarms. For spam filtering, raising to 0.8 reduces false quarantines.

**Q7. Why can't we use linear regression directly for classification?**
Linear regression's output is unbounded and can fall outside [0, 1], so it isn't a valid probability. Combining linear regression with a 0.5 threshold gives a non-convex objective, has poor calibration, and is heavily influenced by extreme inputs. Logistic regression solves all three issues by squashing the output through the sigmoid and using log loss.

**Q8. What is the cost function used by logistic regression?**
**Binary Cross-Entropy** (Log Loss): `J = − (1/n) Σ [y·log(p̂) + (1−y)·log(1−p̂)]`. It's convex in the parameters (so gradient descent finds the global minimum) and corresponds to maximum-likelihood estimation under a Bernoulli noise model. Confidently wrong predictions are penalized severely because `log(0)` blows up.

**Q9. Why log loss instead of MSE?**
MSE composed with the sigmoid is non-convex — gradient descent can get stuck. Log loss is convex, has a probabilistic interpretation (negative log-likelihood under Bernoulli), and produces well-calibrated probabilities when the model is well-specified.

**Q10. What does the decision boundary look like?**
A hyperplane: `b₀ + b₁·x₁ + … + bₙ·xₙ = 0`. In two dimensions, a straight line. In three dimensions, a flat plane. Logistic regression therefore requires the classes to be (approximately) linearly separable. For non-linear boundaries, engineer non-linear features or switch to a non-linear classifier.

**Q11. Is logistic regression a linear classifier?**
Yes — its decision boundary is linear in the *original* feature space. Once you add polynomial or interaction features, the boundary becomes non-linear in the original space, but it's still linear in the augmented feature space.

**Q12. What are odds and how do they relate to probabilities?**
Odds are the ratio `p / (1 − p)`. If `p = 0.8`, odds = 4 (event is 4× as likely as its complement). Probabilities are bounded in `[0, 1]`; odds are bounded in `[0, ∞)`; log-odds are bounded in `(−∞, ∞)`, which matches the range of a linear function of features.

**Q13. What is the logit function?**
`logit(p) = log(p / (1 − p))`. The inverse of the sigmoid. It maps probabilities in `(0, 1)` to all real numbers. Logistic regression models the logit as a linear function of features.

**Q14. How do you interpret a logistic regression coefficient?**
A unit increase in `xⱼ` changes the *log-odds* by `bⱼ`. Equivalently, it multiplies the *odds* by `e^bⱼ`. Positive `bⱼ` means the feature pushes the prediction toward class 1; negative pushes toward class 0; magnitude (after standardization) reflects feature importance.

### B. Optimization

**Q15. How is logistic regression trained?**
By **maximum-likelihood estimation** (equivalent to minimizing log loss). The optimizer is typically gradient-based — `lbfgs`, `liblinear`, `saga`, or `newton-cg` in scikit-learn. There's no closed-form solution like linear regression's Normal Equation; logistic regression must be solved iteratively.

**Q16. What is maximum likelihood estimation?**
A principle for estimating model parameters: choose the parameters that maximize the probability of observing the actual training data. For logistic regression, MLE is mathematically identical to minimizing log loss, so the two terms are interchangeable in practice.

**Q17. What is regularization in logistic regression?**
A technique that adds a penalty on large coefficients to the loss function, discouraging overfitting. **L2 regularization** (Ridge) penalizes the sum of squared coefficients and shrinks them toward zero. **L1 regularization** (Lasso) penalizes absolute values and can drive coefficients exactly to zero, producing a sparse model. Scikit-learn's `LogisticRegression` applies L2 by default with strength controlled by the inverse parameter `C`.

### C. Preprocessing

**Q18. Why scale features for logistic regression?**
Without scaling, gradient-based solvers converge slowly when features have very different magnitudes. Coefficient magnitudes also become incomparable — a tiny coefficient on a large-magnitude feature can be just as influential as a huge coefficient on a small-magnitude feature. Scaling makes optimization fast, stable, and interpretable.

**Q19. What does `StandardScaler` do?**
Rescales each feature to have mean 0 and standard deviation 1: `(x − μ) / σ`. Preserves the distribution shape; only changes location and scale.

**Q20. Why `fit_transform` only on training data?**
Fitting on test data leaks test-set statistics back into training, making test-set evaluation overly optimistic. The split between training and test must be a strict information barrier — anything learned from data must come from the training set only.

**Q21. What does `stratify=y` do in `train_test_split`?**
Preserves class proportions in both training and test sets. If your data is 95% class 0 / 5% class 1, both splits will have the same 95/5 split. Without stratification, especially with small datasets, you can get a test set with very few positives, making metrics noisy and unreliable.

### D. Confusion Matrix and Metrics

**Q22. What is a confusion matrix?**
A 2×2 table for binary classification (or `n×n` for `n` classes) that breaks predictions into four cells: True Negatives, False Positives, False Negatives, True Positives. Every classification metric — accuracy, precision, recall, F1 — is derived from these four numbers.

**Q23. Define TP, TN, FP, FN with an example.**
For an ad-click model:
- **TP**: user clicked, model predicted click — correct hit.
- **TN**: user didn't click, model predicted no click — correct rejection.
- **FP**: user didn't click, model predicted click — false alarm (Type I error).
- **FN**: user clicked, model predicted no click — missed opportunity (Type II error).

**Q24. What is accuracy and when is it misleading?**
`(TP + TN) / Total`. It's intuitive but breaks down on imbalanced data. With 99% class 0, predicting "always 0" gives 99% accuracy and zero usefulness for the rare class. Use precision/recall/F1/AUC instead when classes are imbalanced.

**Q25. What is precision?**
`TP / (TP + FP)`. Of the cases the model predicted as positive, how many actually were. High precision = few false alarms. Important when the cost of acting on a false alarm is high — quarantining legitimate email, prosecuting an innocent person, recommending a low-quality product.

**Q26. What is recall (sensitivity)?**
`TP / (TP + FN)`. Of all actual positives, how many the model caught. High recall = few misses. Important when missing a positive is very costly — missing a cancer diagnosis, missing a fraud, missing a security threat.

**Q27. What is the precision–recall trade-off?**
Lowering the decision threshold catches more positives (recall ↑) but flags more false alarms (precision ↓). Raising it does the opposite. You can't maximize both simultaneously without a perfect classifier; you choose the balance based on the relative cost of FP vs FN in the application.

**Q28. What is the F1 score?**
The harmonic mean of precision and recall: `F1 = 2·P·R / (P + R)`. The harmonic mean drops sharply if either component is small, so F1 is high only when *both* precision and recall are high. F1 is the standard metric when both error types matter and the data is imbalanced.

**Q29. Why harmonic mean for F1 instead of arithmetic mean?**
The harmonic mean is biased toward the lower of the two values, so a model with `precision = 1.0` but `recall = 0.0` gets F1 = 0 (correctly reflecting that it's useless), whereas the arithmetic mean would give 0.5 (misleadingly suggesting it's adequate).

**Q30. What is specificity and how does it differ from recall?**
**Specificity** = `TN / (TN + FP)` — of all actual negatives, how many the model correctly identified. **Recall (sensitivity)** = `TP / (TP + FN)` — of all actual positives, how many we caught. Specificity is recall for the negative class. They're related by mirror symmetry: precision and recall focus on the positive class; specificity focuses on the negative class.

**Q31. What is the ROC curve and AUC?**
**ROC** (Receiver Operating Characteristic): plot of true-positive rate (recall) against false-positive rate (1 − specificity) as the threshold varies from 0 to 1. **AUC** (Area Under the Curve): the probability that a randomly chosen positive is ranked higher than a randomly chosen negative. AUC = 1 → perfect; AUC = 0.5 → random; AUC < 0.5 → worse than random (a sign of inverted predictions).

### E. Visualization (Part B)

**Q32. What is a boxplot and what five statistics does it summarize?**
A compact distribution summary showing five-number summary: min, Q1, median, Q3, max, plus outliers as dots beyond the whiskers. The box spans Q1 to Q3 (the middle 50%), the line is the median, and the whiskers extend 1.5·IQR. Designed by John Tukey for quick distribution comparison.

**Q33. What is the IQR and why is it useful?**
**Interquartile Range** = `Q3 − Q1`. The spread of the middle 50% of the data. It's a robust measure of spread — unaffected by extreme values, unlike the standard deviation. The IQR is the foundation of the standard outlier rule (`Q1 − 1.5·IQR`, `Q3 + 1.5·IQR`).

**Q34. What does the position of the median inside the box tell you?**
If the median is closer to Q1 (bottom), the distribution is **right-skewed** — values pile up at the lower end with a tail extending up. If closer to Q3 (top), the distribution is **left-skewed**. Centered → roughly symmetric.

**Q35. What's the difference between a boxplot and a violin plot?**
A boxplot shows summary statistics. A **violin plot** shows the full distribution shape using a kernel density estimate, while still highlighting the quartiles internally. Violin plots are more informative when distributions are bimodal or otherwise non-standard — boxplots can hide that.

**Q36. Why is a grouped boxplot useful?**
It lets you compare distributions across categories (and sub-categories with `hue`) side-by-side. This makes group differences visible at a glance — far easier than reading a table of summary statistics.

**Q37. Compare boxplot and histogram.**
- **Histogram** is best for examining the *shape* of one distribution in detail (binned counts).
- **Boxplot** is best for comparing distributions across groups, with a focus on summary statistics rather than shape.

### F. Code-Specific

**Q38. Why does scaling matter more for logistic regression than for tree-based models?**
Tree-based models split on individual feature values and are invariant to monotonic transformations of any single feature. Logistic regression, like all linear models that use gradient descent, is sensitive to feature scale because the optimization landscape and coefficient magnitudes depend on it.

**Q39. What does `predict_proba` return and how does it differ from `predict`?**
`predict_proba` returns the probabilities of each class — a 2-D array with one column per class. `predict` returns the class label by thresholding (default 0.5). When you need to rank predictions, choose a custom threshold, or report calibrated probabilities, use `predict_proba`.

**Q40. How would you improve this classifier?**
- Tune the regularization strength (`C` in scikit-learn).
- Engineer interaction or polynomial features for non-linear boundaries.
- Try `class_weight='balanced'` if classes are imbalanced.
- Tune the decision threshold to maximize F1 or business utility.
- Try non-linear classifiers — Random Forests, Gradient Boosting (XGBoost, LightGBM), or SVMs with non-linear kernels.
- Use cross-validation to choose hyperparameters more robustly than a single train/test split.
