# Assignment 20 — Logistic Regression on Social Network Ads

Notebook: `Assignment_20_Complete.ipynb`
Dataset: `social_network_ads.csv`
Target: `Purchased` (binary: 0 = didn't buy, 1 = bought)

> Note: This problem statement matches Assignment 9 in the exam paper. Same dataset, same task, same theory — implemented as a fresh standalone notebook.

---

## 1. Classification — A Quick Recap

Classification predicts a discrete category from a fixed set. Examples:

- Spam vs not-spam (binary).
- Fraudulent vs legitimate transaction (binary).
- Tumor benign vs malignant vs uncertain (multi-class).
- **User purchases vs doesn't purchase** (this assignment — binary).

Two key flavors:

- **Binary** — exactly two classes (0/1, yes/no).
- **Multi-class** — three or more classes.

This assignment is binary, so we use **logistic regression** — the simplest and most foundational binary classifier.

---

## 2. Logistic Regression in Detail

### 2.1 The Three-Stage Architecture

**Stage 1 — Linear part:**
> z = b₀ + b₁·x₁ + b₂·x₂ + … + bₙ·xₙ

A weighted sum of features. `z` can be any real number — positive, negative, large, small.

**Stage 2 — Sigmoid squashing:**
> σ(z) = 1 / (1 + e⁻ᶻ)

The sigmoid (logistic function) maps any real number to `[0, 1]`, producing a probability. As `z → +∞`, σ → 1; as `z → −∞`, σ → 0; at `z = 0`, σ = 0.5.

**Stage 3 — Threshold:**
> ŷ = 1 if σ(z) ≥ 0.5, else 0

The threshold converts probability to class label. Default is 0.5, but it can be tuned for application-specific cost ratios.

### 2.2 Why Not Use Linear Regression for Classification?
- Linear regression's output is unbounded — can be negative or exceed 1, making no sense as a probability.
- The MSE-based cost combined with thresholding is non-convex; gradient descent gets stuck.
- Calibration is bad — linear regression fits a line, not a probability.

### 2.3 The Logit / Log-Odds View
Logistic regression models the **log-odds** as a linear function of features:

> log(p / (1 − p)) = b₀ + b₁·x₁ + … + bₙ·xₙ

`p / (1 − p)` is the **odds**: how much more likely class 1 is than class 0. Taking the log gives the **logit**, which spans all real numbers — matching the range of the linear combination. Exponentiating a coefficient `bⱼ` gives the **odds ratio**: how much the odds multiply when `xⱼ` increases by one unit.

---

## 3. Cost Function — Binary Cross-Entropy

We don't use MSE for logistic regression. Instead:

> J(b) = − (1/n) · Σ [yᵢ · log(p̂ᵢ) + (1 − yᵢ) · log(1 − p̂ᵢ)]

Why this form?

- It's **convex** in the parameters, so gradient descent always finds the global minimum.
- It comes from **maximum likelihood** under a Bernoulli noise model.
- It heavily penalizes confidently-wrong predictions: when the true label is 1 and the predicted probability is near 0, `log(p̂)` blows up to `−∞`.

This sharp penalty for confident wrong predictions is exactly what we want — a model that says "I'm 99% sure this is class 1" and then turns out to be wrong should pay a huge cost.

---

## 4. The Decision Boundary

### 4.1 Where σ(z) = 0.5
By definition, the decision boundary is the set of points where the model is indifferent — `σ(z) = 0.5`, equivalently `z = 0`, which is `b₀ + b₁·x₁ + … + bₙ·xₙ = 0`. In two dimensions that's a straight line; in three, a plane; in higher dimensions, a hyperplane.

### 4.2 Why Linear?
Logistic regression is a **linear classifier** — its decision boundary is linear in the original feature space. To handle non-linear boundaries, engineer non-linear features (`x²`, `x₁·x₂`, polynomial features, splines) before training. Or switch to a non-linear classifier (Random Forest, SVM with RBF kernel, neural network).

---

## 5. Class Imbalance — A Frequent Real-World Challenge

When the classes are not roughly 50/50, several problems arise:

- **Accuracy becomes misleading**. With 95% class-0 / 5% class-1, predicting "always 0" gives 95% accuracy and zero usefulness.
- **The classifier learns to favor the majority** because the loss is dominated by it.
- **Test sets may have very few minority instances** if you don't stratify.

Mitigations:

- **`stratify=y`** in `train_test_split` — preserves class proportions.
- **`class_weight='balanced'`** — automatically weighs classes inversely to frequency.
- **Resampling** — SMOTE (oversample minority), random undersample (majority).
- **Threshold tuning** — choose a threshold that maximizes F1 or business utility, not just 0.5.
- **Use precision, recall, F1, ROC-AUC** rather than just accuracy.

---

## 6. Why Feature Scaling Matters

The Social Network Ads dataset has features at very different scales:

- `Age` — roughly 18–60.
- `EstimatedSalary` — 15 000–150 000.

Without scaling:

- Gradient-based solvers converge slowly because the cost surface is elongated.
- Some solvers (`lbfgs`, `saga`) become numerically unstable.
- Coefficient magnitudes are not directly comparable as importance proxies.

`StandardScaler` rescales each feature to mean 0 and standard deviation 1. The cardinal rule: **fit on train only**, **transform on test**. Fitting on test contaminates the procedure with test-set information — that's **data leakage**.

---

## 7. Confusion Matrix and Metrics

### 7.1 The Four Outcomes

|                       | Predicted 0      | Predicted 1     |
|---|---|---|
| **Actual 0**          | TN (correct)     | FP (false alarm) |
| **Actual 1**          | FN (missed)      | TP (correct)     |

- **TN** — correctly predicted no purchase.
- **FP** — predicted purchase, but didn't (false alarm).
- **FN** — predicted no purchase, but did (missed customer).
- **TP** — correctly predicted purchase.

### 7.2 Metrics

| Metric | Formula | Best for |
|---|---|---|
| Accuracy | `(TP + TN) / Total` | Balanced data |
| Precision | `TP / (TP + FP)` | Cost of false alarm is high |
| Recall (Sensitivity) | `TP / (TP + FN)` | Cost of missing a positive is high |
| F1 | `2·P·R / (P + R)` | Balance, especially imbalanced data |
| Specificity | `TN / (TN + FP)` | Recall for negatives |
| ROC-AUC | Area under TPR-vs-FPR curve | Threshold-independent |

### 7.3 Precision–Recall Trade-off
Lowering the threshold catches more positives (recall ↑) but more false alarms (precision ↓). Raising it does the opposite. The right balance is application-dependent.

- **Cancer screening**: maximize recall — missing cancer is far costlier than a false alarm.
- **Spam filter**: maximize precision — quarantining a real email is costly.
- **Ad targeting**: balance F1 to maximize conversion-cost trade-off.

---

## 8. Comparison with Other Classifiers

### Logistic Regression vs Linear Regression

|  | Linear Regression | Logistic Regression |
|---|---|---|
| Output | Continuous | Probability → class |
| Cost | MSE | Log Loss |
| Equation | `y = b₀ + b₁·x` | `p = 1/(1+e^−z)` |
| Use | Regression | Classification |

### Logistic Regression vs Naïve Bayes

|  | Logistic Regression | Naïve Bayes |
|---|---|---|
| Approach | Discriminative — models `P(c|x)` directly | Generative — models `P(x|c)·P(c)` |
| Independence assumption | None | Conditional independence |
| Need for data | More | Less |
| Calibration | Generally better | Often pushed to 0/1 |

### Logistic Regression vs Decision Trees

|  | Logistic Regression | Decision Tree |
|---|---|---|
| Boundary | Linear | Axis-aligned step-functions |
| Interpretable? | Coefficients & odds ratios | If/else rules |
| Captures interactions | Only if engineered | Naturally |
| Sensitive to scaling | Yes | No |

---

## 9. Strengths & Limitations

### Strengths
- **Fast** to train and predict.
- **Interpretable** — coefficients translate directly to odds ratios.
- **Probabilistic output** — well-calibrated by default.
- **Strong baseline** — often within a few percent of more complex models.
- **Handles high-dimensional data** well, especially with regularization.

### Limitations
- Linear decision boundary — can't capture complex non-linear patterns natively.
- Sensitive to multicollinearity — coefficients become unstable.
- Requires feature scaling for fast convergence.
- Doesn't model interactions automatically.
- Struggles with imbalanced data without weighting / resampling.

---

## 10. Viva Questions (40)

### A. Classification & Logistic Regression Fundamentals

**Q1. What is classification?**
A supervised learning task that predicts a discrete category from a fixed, finite set. Distinguished from regression (continuous output) by its output type. Binary classification has exactly two classes; multi-class has three or more.

**Q2. What is logistic regression?**
A linear classification algorithm that predicts the probability of belonging to a class using the **sigmoid function** and assigns a class label by thresholding. Despite the name, it's a classifier, not a regressor — the "regression" comes from its log-odds linear-regression-on-a-transformed-scale formulation.

**Q3. Why is it called "regression" if it does classification?**
Historical naming. The model's *math* fits a linear regression on the **log-odds** of the class. The output is a class, but the underlying mechanism regresses the logit. So "regression" stays in the name even though the algorithm is used for classification.

**Q4. What is the sigmoid function?**
`σ(z) = 1 / (1 + e⁻ᶻ)`. Maps any real number to `[0, 1]`, making the output a valid probability. Smooth, differentiable everywhere, and its derivative has the elegant form `σ(z)·(1 − σ(z))` that simplifies gradient computation.

**Q5. What is `z` in logistic regression?**
The linear combination `z = b₀ + b₁·x₁ + … + bₙ·xₙ`. Often called the **logit** or **raw score**. Positive `z` pushes the predicted probability toward 1; negative pushes toward 0; `z = 0` gives 0.5 — the decision boundary.

**Q6. What is the default decision threshold and when would you change it?**
0.5 by default. Lower it (e.g., 0.3) to predict the positive class more eagerly — useful when the cost of missing a positive is high (medical screening). Raise it (e.g., 0.8) to predict positives only when confident — useful when false positives are costly (spam filter).

**Q7. What is the cost function for logistic regression?**
**Binary Cross-Entropy** / **Log Loss**: `J = − (1/n) Σ [y·log(p̂) + (1−y)·log(1−p̂)]`. Convex in the parameters (so gradient descent finds the global minimum) and equivalent to maximum-likelihood estimation under a Bernoulli noise model. Confidently wrong predictions are penalized severely.

**Q8. Why log loss instead of MSE?**
MSE composed with the sigmoid is non-convex — gradient descent can get stuck in local minima. Log loss is convex, has a probabilistic interpretation, and produces well-calibrated probabilities. It also matches maximum-likelihood theory for binary outcomes.

**Q9. What is the decision boundary in logistic regression?**
The set of points where `σ(z) = 0.5`, equivalently `z = 0`, which is `b₀ + b₁x₁ + … + bₙxₙ = 0` — a hyperplane. In 2D it's a line; in 3D a plane; in higher D a hyperplane. Logistic regression is therefore a **linear classifier**.

**Q10. Can logistic regression handle non-linear boundaries?**
Not directly — but you can engineer non-linear features (`x²`, `x₁·x₂`, polynomial terms, splines) and feed them in. The boundary is then non-linear in the original space but linear in the augmented feature space. For complex non-linear problems, use a non-linear classifier instead (Random Forest, kernel SVM, neural network).

**Q11. What's the difference between linear and logistic regression?**
| | Linear | Logistic |
|---|---|---|
| Output | Continuous | Probability → class |
| Cost | MSE | Log Loss |
| Equation | `y = b₀ + b₁x` | `p = 1/(1 + e^−z)` |
| Use | Regression | Classification |

**Q12. What are odds, log-odds, and odds ratios?**
- **Odds** = `p / (1 − p)`. If `p = 0.8`, odds = 4 (event 4× as likely as not).
- **Log-odds** (logit) = `log(odds)` — the linear function in logistic regression.
- **Odds ratio** = `e^bⱼ` — the multiplicative effect on odds for a one-unit increase in `xⱼ`.

### B. The Math Inside

**Q13. How are coefficients interpreted?**
A unit increase in `xⱼ` changes the **log-odds** by `bⱼ`. Equivalently, it multiplies the **odds** by `e^bⱼ`. Positive `bⱼ` pushes the prediction toward class 1; negative pushes toward class 0. After standardization, coefficient magnitudes also reflect feature importance.

**Q14. How is logistic regression trained?**
By **maximum-likelihood estimation** (equivalent to minimizing log loss). The optimizer is iterative — `lbfgs`, `liblinear`, `saga`, `newton-cg` in scikit-learn. There is no closed-form solution analogous to linear regression's Normal Equation.

**Q15. What is maximum likelihood estimation?**
A principle for estimating model parameters: choose the parameters that maximize the probability of observing the actual training labels. For logistic regression, MLE is mathematically equivalent to minimizing binary cross-entropy.

**Q16. What is regularization in logistic regression?**
A penalty added to the cost to discourage overfitting. **L2 (Ridge)** penalizes squared coefficients, shrinking them. **L1 (Lasso)** penalizes absolute coefficients, possibly driving them to exactly zero (feature selection). Strength is controlled by the inverse parameter `C` in scikit-learn.

### C. Preprocessing

**Q17. Why scale features for logistic regression?**
Without scaling, gradient-based solvers converge slowly because features with different magnitudes elongate the cost surface. Coefficient magnitudes also become incomparable as importance proxies — a tiny coefficient on a large-scale feature can be just as influential as a huge coefficient on a small-scale feature.

**Q18. What does `StandardScaler` do?**
Rescales each feature to mean 0 and standard deviation 1: `(x − μ) / σ`. Preserves the distribution shape; only changes location and scale. Essential preprocessing for logistic regression and most other models that use gradient descent.

**Q19. Why fit only on training data?**
Fitting on test data leaks test-set statistics back into training, making test-set evaluation overly optimistic. The split between training and test is an information barrier — anything learned from data must come from the training set only.

**Q20. What does `stratify=y` do in `train_test_split`?**
Preserves class proportions in both training and test sets. If your data is 95% class 0 / 5% class 1, both splits will have the same 95/5 ratio. Without it, especially with small datasets, you can get a test set with very few positives — making metrics noisy and unreliable.

### D. Confusion Matrix and Metrics

**Q21. What is a confusion matrix?**
A 2×2 table for binary classification (or `n×n` for `n` classes) that breaks predictions into four cells: TN, FP, FN, TP. Every classification metric — accuracy, precision, recall, F1 — is derived from these four numbers.

**Q22. Define TP, TN, FP, FN.**
- **TP** — actual 1, predicted 1 (correct hit).
- **TN** — actual 0, predicted 0 (correct rejection).
- **FP** — actual 0, predicted 1 (false alarm; Type I error).
- **FN** — actual 1, predicted 0 (miss; Type II error).

**Q23. What's the difference between Type I and Type II errors?**
- **Type I (FP)** — false positive; rejecting a true null hypothesis. "Crying wolf."
- **Type II (FN)** — false negative; failing to reject a false null. "Missing the wolf."
The trade-off between them depends on the application's cost structure.

**Q24. What is accuracy and when is it misleading?**
`(TP + TN) / Total`. Misleading on imbalanced data — predicting the majority class always gives high accuracy but is useless. Use precision, recall, F1, or ROC-AUC for imbalanced problems.

**Q25. What is precision?**
`TP / (TP + FP)`. Of the cases the model predicted as positive, how many actually were. High precision = few false alarms. Important when acting on a false alarm is costly — quarantining a real email, prosecuting an innocent person.

**Q26. What is recall (sensitivity)?**
`TP / (TP + FN)`. Of all actual positives, how many the model caught. High recall = few misses. Important when missing a positive is costly — missing a cancer diagnosis, missing a fraud, missing a security threat.

**Q27. What is the precision-recall trade-off?**
Lowering the decision threshold raises recall and lowers precision, and vice versa. You can't max both simultaneously without a perfect classifier. Choose the balance based on the relative costs of FP vs FN.

**Q28. What is the F1 score?**
The harmonic mean of precision and recall: `F1 = 2·P·R / (P + R)`. The harmonic mean drops sharply if either is small, so F1 is high only when both are high. Standard metric for imbalanced data.

**Q29. Why harmonic mean for F1, not arithmetic?**
The harmonic mean punishes models that are great at one but terrible at the other. A model with `P = 1.0, R = 0.0` gets F1 = 0 (correct — useless), while the arithmetic mean would give 0.5 (misleadingly suggesting it's adequate).

**Q30. What is the ROC curve and AUC?**
**ROC**: plot of TPR (recall) vs FPR (1 − specificity) as the threshold varies from 0 to 1. **AUC**: area under the curve. AUC = 1 → perfect; 0.5 → random; < 0.5 → worse than random. Threshold-independent measure of classifier quality.

**Q31. What does an AUC of 0.85 mean?**
The probability that a randomly selected positive is ranked higher than a randomly selected negative is 0.85. Equivalently, the model has a strong ability to discriminate the two classes — better than random (0.5) but not perfect (1.0).

**Q32. What's the difference between sensitivity and specificity?**
- **Sensitivity (recall)** = `TP / (TP + FN)` — recall for the positive class.
- **Specificity** = `TN / (TN + FP)` — recall for the negative class.
ROC plots sensitivity vs (1 − specificity).

### E. Comparisons

**Q33. How does logistic regression compare with Naïve Bayes?**
- **Logistic regression** is **discriminative** — models `P(c|x)` directly.
- **Naïve Bayes** is **generative** — models `P(x|c)·P(c)`.
- LR makes no independence assumption; NB does.
- LR usually achieves higher accuracy with enough data; NB needs less data.
- LR is generally better calibrated.

**Q34. How does logistic regression compare with SVM?**
- **Logistic regression** outputs probabilities.
- **SVM** finds the maximum-margin separating hyperplane and outputs distance to the hyperplane (not a probability without calibration).
- SVM with kernels handles non-linearity natively; LR doesn't without feature engineering.
- LR is often easier to interpret.

**Q35. How does logistic regression compare with Decision Trees?**
- **LR** has a linear decision boundary; **trees** have axis-aligned step functions.
- LR coefficients give odds ratios; trees give if-else rules.
- Trees handle interactions automatically; LR doesn't.
- LR is sensitive to feature scaling; trees aren't.
- LR is parametric (fixed parameters); trees are non-parametric (grow with data).

### F. Practical & Code-Specific

**Q36. What does `predict_proba` return?**
A 2D array with one column per class, containing the predicted probability for each. Columns sum to 1 across each row. Use it when you need to rank predictions, threshold-tune, or report calibrated probabilities rather than just hard labels.

**Q37. Why might `max_iter=100` (the default) not be enough?**
For complex or unscaled data, the optimizer may not converge in 100 iterations. scikit-learn issues a `ConvergenceWarning`. The fix is to scale features (which dramatically speeds convergence) or increase `max_iter` to 1000 or 5000.

**Q38. What does `class_weight='balanced'` do?**
Automatically weighs each class inversely to its frequency, so the loss is balanced even when classes are imbalanced. Equivalent to oversampling the minority class without changing the data.

**Q39. How is the Social Network Ads model interpreted?**
Each coefficient tells you the change in log-odds of purchasing per unit change in the feature. Positive `Age` coefficient → older users are more likely to buy. Positive `EstimatedSalary` coefficient → higher-income users are more likely to buy. Magnitudes (after scaling) reflect relative importance.

**Q40. How would you improve this classifier?**
- Tune **regularization strength** (`C` in scikit-learn) via cross-validation.
- Engineer **interaction or polynomial features** for non-linear boundaries.
- Try `class_weight='balanced'` if classes are imbalanced.
- **Tune the decision threshold** to maximize F1 or business utility.
- Try **non-linear classifiers** — Random Forest, Gradient Boosting (XGBoost, LightGBM), kernel SVM.
- Use **cross-validation** rather than a single train-test split.
- For production: monitor **calibration** (predicted vs actual probabilities) and retrain periodically.
