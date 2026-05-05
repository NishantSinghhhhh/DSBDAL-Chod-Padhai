# Assignment 6 — Logistic Regression on Advertising Dataset

Notebook: `Assignment_6_Complete.ipynb`
Dataset: `advertising.csv`
Target: `Clicked_on_Ad` (binary: 0 = no click, 1 = click)

---

## 1. Topic Deep-Dive

### 1.1 Classification vs Regression
| Task | Output | Examples |
|---|---|---|
| **Regression** | Continuous number | House price, delivery time |
| **Classification** | Discrete category | Spam/not-spam, click/no-click, malignant/benign |

This assignment is **binary classification** — two possible labels (0 / 1).

### 1.2 What Logistic Regression Actually Does
Despite the name, logistic regression is a **classifier**, not a regressor. It builds a probability that an input belongs to class 1 in three stages:

**Stage 1 — Linear part (the "regression"):**
> z = b₀ + b₁·x₁ + b₂·x₂ + … + bₙ·xₙ

`z` can be any real number — large positive, large negative, anything.

**Stage 2 — Sigmoid squash:**
> σ(z) = 1 / (1 + e⁻ᶻ)

| z | σ(z) |
|---|---|
| `−∞` | 0 |
| `0` | 0.5 |
| `+∞` | 1 |

The sigmoid (logistic function) compresses any real number into `[0, 1]`, giving a usable probability.

**Stage 3 — Threshold:**
> ŷ = 1 if σ(z) ≥ 0.5, else 0

This converts probability → class label.

### 1.3 Why "Logistic Regression"
The model regresses the **log-odds** (logit) on a linear function of the features:

> log(p / (1 − p)) = b₀ + b₁·x₁ + … + bₙ·xₙ

So even though the *output* is a class, the *math* fits a linear model on the log-odds — hence "regression" stays in the name historically.

### 1.4 Cost Function — Log Loss (Cross-Entropy)
Linear regression's MSE is non-convex with the sigmoid, so we use **binary cross-entropy** instead:

> J(b) = − (1/n) · Σ [ yᵢ · log(p̂ᵢ) + (1 − yᵢ) · log(1 − p̂ᵢ) ]

This is **convex** in `b`, so gradient descent always finds the global minimum.

### 1.5 Decision Boundary
The set of points where `σ(z) = 0.5` is the **decision boundary**. Mathematically that's `z = 0`, which is a **straight line** in 2D, a **plane** in 3D, a **hyperplane** in higher dimensions. Logistic regression is therefore a **linear classifier** — it can only separate classes that are linearly separable (with a margin).

### 1.6 Linear vs Logistic Regression Side-by-Side
|  | Linear Regression | Logistic Regression |
|---|---|---|
| Output | Continuous number | Probability (0–1) → class label |
| Use case | Regression | Classification |
| Cost function | MSE | Log Loss / Cross-Entropy |
| Equation | `y = b₀ + b₁·x` | `p = 1 / (1 + e^−(b₀ + b₁·x))` |
| Boundary | A line/plane through points | A line/plane separating classes |
| Algorithm | OLS or Gradient Descent | MLE via Gradient Descent |

### 1.7 Class Imbalance
If 95% of users don't click and 5% do, a model that always predicts "no click" gets 95% accuracy but is useless. Mitigations:
- **`stratify=y`** in train/test split — preserves class ratio.
- **Class weights** (`class_weight='balanced'`) — penalizes mistakes on the minority class more.
- **Oversampling / Undersampling / SMOTE** — change the data distribution.
- Use **precision / recall / F1 / ROC-AUC** instead of accuracy alone.

### 1.8 Why Scale Features
The features have wildly different ranges (Age ~30, Income ~50 000). Without scaling:
- Gradient descent converges slowly.
- Coefficients are not directly comparable in size.
- Some solvers (`lbfgs`, `saga`) become numerically unstable.

`StandardScaler` rescales each feature to mean 0, std 1. **Always** `fit_transform` on train, `transform` only on test (no leakage).

### 1.9 Confusion Matrix — The Foundation of Classification Metrics
For binary classification, every prediction falls into one of four buckets:

|                       | Predicted: 0 (No)   | Predicted: 1 (Yes)   |
|---|---|---|
| **Actual: 0 (No)**    | **TN** True Negative — correct rejection | **FP** False Positive — Type I error (false alarm) |
| **Actual: 1 (Yes)**   | **FN** False Negative — Type II error (miss) | **TP** True Positive — correct hit |

### 1.10 Classification Metrics

| Metric | Formula | When it matters |
|---|---|---|
| **Accuracy** | `(TP + TN) / Total` | Balanced classes; misleading when imbalanced |
| **Error Rate** | `(FP + FN) / Total` | Complement of accuracy |
| **Precision** | `TP / (TP + FP)` | False alarms are costly (e.g., flagging good customer as fraud) |
| **Recall (Sensitivity / TPR)** | `TP / (TP + FN)` | Misses are costly (e.g., missing a tumor) |
| **Specificity (TNR)** | `TN / (TN + FP)` | Correctly identifying negatives |
| **F1 Score** | `2 · P · R / (P + R)` | Harmonic mean — balance of precision & recall |
| **ROC-AUC** | Area under TPR-vs-FPR curve | Threshold-independent overall quality |

### 1.11 Precision/Recall Trade-off
Lowering the decision threshold catches more positives (higher recall) but flags more false alarms (lower precision), and vice versa. The right balance depends on the cost of each error type in the application.

---

## 2. Libraries Used

| Library | Purpose |
|---|---|
| `pandas`, `numpy` | DataFrame ops, numerical operations |
| `matplotlib`, `seaborn` | Class distribution plots, confusion-matrix heatmap |
| `sklearn.model_selection.train_test_split` | Stratified 80/20 split |
| `sklearn.preprocessing.StandardScaler` | Mean-0, std-1 scaling |
| `sklearn.linear_model.LogisticRegression` | The classifier |
| `sklearn.metrics` | `confusion_matrix`, `accuracy_score`, `precision_score`, `recall_score`, `classification_report` |

---

## 3. Functions & Methods Reference

### Loading & Inspection
- `pd.read_csv(path)` → DataFrame
- `df.shape`, `df.head()`, `df.info()`, `df.describe()`
- `df['target'].value_counts()` — class balance counts
- `df['target'].mean()` — fraction of class 1 (since target is 0/1)
- `sns.countplot(x='target', data=df)` — visualize class balance

### Train/Test Split
- `train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)` — `stratify=y` preserves class ratio.

### Scaling
- `StandardScaler().fit_transform(X_train)`
- `scaler.transform(X_test)`

### Model
- `LogisticRegression(max_iter=1000, random_state=42)` — `max_iter` controls solver iterations.
- `model.fit(X_train_scaled, y_train)`
- `model.predict(X_test_scaled)` → class labels (0 or 1)
- `model.predict_proba(X_test_scaled)[:, 1]` → probability of class 1
- `model.coef_` — learned weights (positive coef pushes prediction toward class 1)
- `model.intercept_` — bias term

### Evaluation
- `confusion_matrix(y_test, y_pred)` → 2×2 array
- `cm.ravel()` → `(TN, FP, FN, TP)`
- `accuracy_score`, `precision_score`, `recall_score`
- `classification_report(y_test, y_pred, target_names=[...])` — per-class precision, recall, F1, support
- `sns.heatmap(cm, annot=True, fmt='d')` — visualize the confusion matrix

---

## 4. Pipeline Summary
1. Load CSV → check shape, dtypes, summary stats, class balance.
2. Identify predictors (5 features) and target (`Clicked_on_Ad`).
3. **Stratified** 80/20 train-test split (preserves class proportions).
4. Scale features with `StandardScaler` (fit on train, transform on test).
5. Train `LogisticRegression(max_iter=1000)` on scaled features.
6. Inspect coefficients — direction reveals which features push toward each class.
7. Predict labels (`predict`) and probabilities (`predict_proba`).
8. Build the confusion matrix → extract TN, FP, FN, TP.
9. Compute accuracy, error rate, precision, recall manually and cross-check with sklearn.
10. View the full `classification_report` for per-class metrics including F1.

---

## 5. Viva Questions (40)

### A. Classification & Logistic Regression Fundamentals
**Q1. What is classification?**
A supervised learning task that predicts a discrete category for each input.

**Q2. What is logistic regression?**
A linear classification algorithm that predicts the probability of belonging to a class using the **sigmoid function**, then assigns the class label by thresholding the probability.

**Q3. Why is it called "regression" if it does classification?**
Historical naming — the math regresses the **log-odds** of the class on a linear combination of features. The output is a class, but the underlying model is linear in the log-odds.

**Q4. What is the sigmoid (logistic) function?**
`σ(z) = 1 / (1 + e⁻ᶻ)`. It maps any real number to `[0, 1]`, giving a probability.

**Q5. What is `z` in logistic regression?**
The linear combination `z = b₀ + b₁·x₁ + … + bₙ·xₙ`. It's the input to the sigmoid.

**Q6. What is the default decision threshold?**
0.5. If `σ(z) ≥ 0.5`, predict class 1; otherwise predict class 0.

**Q7. What's the difference between linear and logistic regression?**
| Linear | Logistic |
|---|---|
| Predicts continuous value | Predicts probability → class |
| Cost: MSE | Cost: Log Loss |
| Output range: any real number | Output range: [0, 1] |
| Use: regression | Use: classification |

**Q8. Why can't we use linear regression for classification?**
Linear regression's output is unbounded — can be negative or > 1, which makes no sense as a probability. MSE also gives a non-convex cost when combined with thresholding.

**Q9. What is the cost function for logistic regression?**
**Binary Cross-Entropy / Log Loss**: `J = −(1/n) Σ [y·log(p̂) + (1−y)·log(1−p̂)]`. Convex, so gradient descent finds the global optimum.

**Q10. What is the decision boundary in logistic regression?**
The set of points where `σ(z) = 0.5`, which is `z = 0`. In 2D it's a straight line, in 3D a plane, in higher dims a hyperplane. Logistic regression is therefore a **linear classifier**.

**Q11. Can logistic regression handle non-linear boundaries?**
Not directly — but you can engineer non-linear features (`x²`, `x₁·x₂`, polynomial features, splines) and feed them in.

**Q12. Is logistic regression parametric or non-parametric?**
Parametric — it has a fixed number of parameters (`b₀ … bₙ`), regardless of dataset size.

### B. The Math Inside
**Q13. What are odds?**
The ratio `p / (1 − p)`. Tells you how much more likely class 1 is than class 0.

**Q14. What is the logit / log-odds?**
`log(p / (1 − p))`. Logistic regression models this as a linear function of features.

**Q15. What is Maximum Likelihood Estimation (MLE)?**
The optimization principle behind logistic regression: pick parameters that maximize the probability of observing the actual training labels. Mathematically equivalent to minimizing log loss.

**Q16. How are coefficients interpreted?**
Each coefficient `bⱼ` is the change in **log-odds** for a one-unit increase in feature `xⱼ`. `exp(bⱼ)` gives the **odds ratio** — multiplicative factor on the odds.

### C. Preprocessing
**Q17. Why scale features for logistic regression?**
Features with very different magnitudes slow gradient-based solvers and produce coefficients that aren't directly comparable. Scaling makes the cost surface well-conditioned.

**Q18. What does `StandardScaler` do?**
Rescales each feature to mean 0 and standard deviation 1: `(x − μ) / σ`.

**Q19. Why `fit_transform` only on training data?**
Fitting on test data leaks test-set statistics into training, giving optimistic and unrealistic test-set scores.

**Q20. What is `stratify=y` in `train_test_split`?**
It preserves the class ratio in both train and test sets — important when classes are imbalanced.

### D. Confusion Matrix & Metrics
**Q21. What is a confusion matrix?**
A 2×2 table (for binary) breaking predictions into True Negatives, False Positives, False Negatives, True Positives.

**Q22. Define TP, TN, FP, FN.**
- **TP** — actual 1, predicted 1 (correct hit)
- **TN** — actual 0, predicted 0 (correct rejection)
- **FP** — actual 0, predicted 1 (false alarm; Type I error)
- **FN** — actual 1, predicted 0 (miss; Type II error)

**Q23. What is accuracy?**
`(TP + TN) / Total`. Fraction of correct predictions.

**Q24. Why is accuracy misleading on imbalanced data?**
On a 95/5 dataset, "always predict majority" gives 95% accuracy but is useless.

**Q25. What is precision?**
`TP / (TP + FP)`. When the model says "positive", how often it is right.

**Q26. What is recall (sensitivity)?**
`TP / (TP + FN)`. Of all actual positives, how many we caught.

**Q27. What is the precision-recall trade-off?**
Lowering the threshold raises recall but lowers precision; raising it does the opposite. Choose the balance based on the cost of each error type.

**Q28. What is specificity?**
`TN / (TN + FP)`. Of all actual negatives, how many we correctly identified.

**Q29. What is the F1 score?**
Harmonic mean of precision and recall: `F1 = 2·P·R / (P + R)`. Punishes models that are great at one but terrible at the other.

**Q30. Why harmonic mean for F1 instead of arithmetic mean?**
The harmonic mean drops sharply if either value is small, so a model with `P=1, R=0` gets F1=0 (correct), whereas the arithmetic mean would be 0.5 (misleading).

**Q31. What is ROC-AUC?**
ROC plots **True Positive Rate** vs **False Positive Rate** at different thresholds. AUC is the area under that curve. AUC = 1 → perfect; 0.5 → random; < 0.5 → worse than random.

**Q32. When would you optimize for recall over precision?**
Medical screening (don't miss a disease), fraud detection (don't miss a fraud), security (don't miss a threat) — anywhere a missed positive is much costlier than a false alarm.

**Q33. When would you optimize for precision?**
Spam filters (don't quarantine legitimate email), recommendation systems (don't push irrelevant items), arrest decisions — anywhere a false positive is very costly.

**Q34. What does `classification_report` show?**
Per-class precision, recall, F1 score, and support (number of true instances), plus aggregated macro and weighted averages.

### E. Practical & Code-Specific
**Q35. What does `max_iter=1000` do?**
Caps the number of solver iterations. Bigger `max_iter` gives more time to converge — useful when training reports a `ConvergenceWarning`.

**Q36. Difference between `predict` and `predict_proba`?**
`predict` returns the class label (0 or 1). `predict_proba` returns the probability of each class — useful when you want to threshold differently or rank predictions.

**Q37. How are positive/negative coefficients interpreted in this notebook?**
Positive coefficient → feature pushes prediction toward "click" (class 1). Negative → toward "no click" (class 0). After scaling, magnitude shows relative importance.

**Q38. Why is logistic regression often a strong baseline?**
- Fast to train and predict
- Coefficients are interpretable (and odds ratios are exact)
- Works well when the decision boundary is roughly linear
- Outputs calibrated probabilities (well-suited to ranking and threshold tuning)

**Q39. What are the limitations of logistic regression?**
- Assumes a linear decision boundary
- Sensitive to multicollinearity
- Can't natively model interactions (must engineer them)
- Struggles with highly imbalanced data without weighting/resampling

**Q40. How would you improve this model?**
- Engineer interaction / non-linear features
- Try regularization (`L1` for feature selection, `L2` for stability)
- Tune the decision threshold to maximize F1 or business metric
- Try non-linear models (Random Forest, Gradient Boosting, SVM with RBF kernel)
- Use `class_weight='balanced'` if classes are imbalanced
