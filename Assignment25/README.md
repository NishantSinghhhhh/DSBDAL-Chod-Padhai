# Assignment 25 — Naïve Bayes Classification on Iris Dataset

Notebook: `Assignment_25_Complete.ipynb`
Dataset: Iris (`iris.csv`) — 150 flowers, 4 numeric features, 3 species classes.

> Note: This problem statement is identical to Assignment 21. This notebook is a fresh, standalone implementation of the same Naïve Bayes classification task.

---

## 1. Classification Overview

**Classification** is a supervised learning task that predicts a discrete category. The Iris problem has **three** classes — *setosa*, *versicolor*, *virginica* — making it a **multi-class** classification problem. Multi-class is a generalization of binary classification where the algorithm must distinguish three or more classes simultaneously.

We use **Gaussian Naïve Bayes** because the four Iris features are continuous numeric measurements that are roughly normally distributed within each species.

---

## 2. Probability Foundations

### 2.1 Conditional Probability
The probability of one event given another:

> P(B | A) = P(A ∩ B) / P(A)

In ML: given the features we observed, what's the probability of each class?

### 2.2 Independence
Two events are independent if knowing one tells you nothing about the other:

> P(A ∩ B) = P(A) · P(B)

### 2.3 Joint Probability
Probability of multiple events occurring together. For two features:

> P(X₁, X₂) = P(X₁) · P(X₂ | X₁)

If independent, simplifies to `P(X₁) · P(X₂)`.

---

## 3. Bayes' Theorem

### 3.1 The Theorem

> P(Class | Features) = ( P(Features | Class) · P(Class) ) / P(Features)

| Term | Name | Meaning |
|---|---|---|
| `P(Class)` | **Prior** | Class probability before seeing features |
| `P(Features \| Class)` | **Likelihood** | Probability of features given the class |
| `P(Class \| Features)` | **Posterior** | Updated probability after seeing features |
| `P(Features)` | **Evidence** | Normalizing constant |

### 3.2 The Naïve Assumption

> P(x₁, x₂, …, xₙ | Class) = P(x₁ | Class) · P(x₂ | Class) · … · P(xₙ | Class)

All features are conditionally independent given the class. Rarely literally true, but usually good enough.

### 3.3 Why It Works Despite a Wrong Assumption
- The decision rule (argmax) needs the right *order*, not the right *values*.
- Errors from independence violations tend to cancel.
- Converges fast and works with little data.

---

## 4. Variants of Naïve Bayes

| Variant | For | Distribution Assumed |
|---|---|---|
| **Gaussian** | Continuous numeric | Normal per class |
| **Multinomial** | Discrete counts (text) | Multinomial |
| **Bernoulli** | Binary features | Bernoulli |
| **Complement** | Imbalanced text | Complement multinomial |

For Iris (4 continuous features) → **Gaussian Naïve Bayes**.

---

## 5. Gaussian Naïve Bayes — How It Works

For each class `c` and each feature `x`:
1. Estimate per-class mean `μ` and variance `σ²` from training data.
2. At prediction time, plug feature values into the normal PDF for each class.
3. Multiply per-feature likelihoods (naïvely) and the prior.
4. Pick the class with the highest posterior.

The likelihood for one feature given a class is:

> P(x | c) = (1 / √(2πσ²_c)) · exp( − (x − μ_c)² / 2σ²_c )

---

## 6. The Iris Dataset

- 150 flowers, 50 from each of 3 species.
- 4 numeric features: sepal length, sepal width, petal length, petal width.
- Introduced by Ronald Fisher in 1936.
- Setosa is **linearly separable** from the other two on petal-length alone.
- Versicolor and virginica overlap somewhat — they require a non-linear or probabilistic boundary for perfect separation.

---

## 7. Multi-Class Classification

### 7.1 NB Handles It Natively
For each class, compute the posterior; pick the largest. No special treatment needed.

### 7.2 Other Multi-Class Strategies
- **One-vs-Rest (OvR)** — train *k* binary classifiers.
- **One-vs-One (OvO)** — train *k(k−1)/2* classifiers.
- **Softmax** — multinomial logistic regression natively handles multi-class.

---

## 8. The Confusion Matrix in Multi-Class

For 3 classes, a 3×3 matrix:
- Rows = actual classes; columns = predicted.
- Diagonal cells = correct predictions.
- Off-diagonal cells = specific kinds of errors.

### 8.1 Per-Class Metrics (One-vs-Rest)
- **Precision(c)** = `TP(c) / (TP(c) + FP(c))`.
- **Recall(c)** = `TP(c) / (TP(c) + FN(c))`.
- **F1(c)** = harmonic mean.

### 8.2 Aggregations
- **Macro** — unweighted mean across classes (treats all classes equally).
- **Weighted** — by class support.
- **Micro** — pooled over all classes (= accuracy in multi-class).

---

## 9. Strengths and Weaknesses

### Strengths
- Fast to train (just frequency counts).
- Works with little data.
- Handles multi-class natively.
- Probabilistic output.
- Robust to irrelevant features.
- Strong baseline for text.

### Weaknesses
- Independence assumption rarely true.
- Probabilities often poorly calibrated.
- Continuous features must satisfy parametric assumptions.
- Zero-frequency problem (in Multinomial / Bernoulli).
- Cannot model feature interactions.

---

## 10. Generative vs Discriminative

| | Generative (NB) | Discriminative (LR) |
|---|---|---|
| Models | `P(x, c)` | `P(c | x)` directly |
| Learns | How data is generated | Decision boundary only |
| Can | Generate new data | Classify only |
| Data efficiency | Better with little data | Better with lots |
| Asymptotic accuracy | Lower | Higher |

---

## 11. Viva Questions (40)

### A. Probability Foundations

**Q1. What is conditional probability?**
The probability of one event given another has occurred. `P(B | A) = P(A ∩ B) / P(A)`. Foundation of all probabilistic classification — given the features we observed, how likely is each class?

**Q2. What is Bayes' theorem and why is it important?**
A formula: `P(A | B) = P(B | A) · P(A) / P(B)`. Lets us flip a conditional we don't know directly into one we *can* estimate from data. In classification, we estimate `P(features | class)` from training data and compute `P(class | features)`.

**Q3. What is a prior probability?**
Probability of a class **before** seeing features. In a balanced 3-class problem, each prior is 1/3.

**Q4. What is a likelihood?**
`P(features | class)` — given that the class is true, how probable are the observed features? For Gaussian NB it comes from a normal PDF.

**Q5. What is a posterior probability?**
`P(class | features)` — probability of a class **after** observing features. The model's actual answer.

**Q6. What is the evidence in Bayes' theorem?**
`P(features)` — the marginal probability, summed across all classes. Acts as a normalizing constant. Often skipped because it cancels when comparing class posteriors.

### B. Naïve Bayes Specifics

**Q7. What is Naïve Bayes?**
A probabilistic classifier based on Bayes' theorem with the simplifying assumption that features are conditionally independent given the class. It picks the class with the highest posterior.

**Q8. Why is it called "naïve"?**
Because it assumes features are conditionally independent given the class — a simplification almost never literally true. Despite this naïveté, it often works very well.

**Q9. What is the conditional independence assumption?**
The assumption that, given the class label, knowing one feature tells you nothing about the others: `P(x₁, x₂ | c) = P(x₁ | c) · P(x₂ | c)`. The simplification that makes Naïve Bayes computationally tractable.

**Q10. Why does Naïve Bayes still work despite a wrong assumption?**
Even if absolute probabilities are biased, the relative ordering of posteriors is often correct. The argmax decision rule needs the right *order*, not exact values. Errors from independence violations tend to cancel rather than compound.

**Q11. What are the three main variants of Naïve Bayes?**
- **Gaussian NB** — continuous features assumed normal (Iris).
- **Multinomial NB** — discrete counts (text).
- **Bernoulli NB** — binary features (word presence).

**Q12. Which variant fits Iris?**
**Gaussian NB** — all four features are continuous and roughly normal within each species.

**Q13. What is the zero-frequency problem?**
In discrete NB, if a feature value never co-occurs with a class in training, its likelihood is exactly zero, wiping out the entire posterior. Fixed by Laplace smoothing.

**Q14. What is Laplace (additive) smoothing?**
Adding a small constant `α` (usually 1) to every count, ensuring no probability is exactly zero.

**Q15. What is MAP estimation?**
**Maximum A Posteriori** — choose the class that maximizes the posterior. The standard NB decision rule.

### C. Strengths & Weaknesses

**Q16. What are NB's strengths?**
Very fast (just counts), works with little data, multi-class natively, probabilistic output, robust to irrelevant features, strong baseline for text.

**Q17. What are NB's weaknesses?**
Independence assumption rarely true, poor calibration, parametric assumptions for continuous features, zero-frequency problem (mitigated by smoothing), no feature-interaction modeling.

**Q18. When would you NOT use NB?**
When features are strongly correlated, interactions matter, or you need calibrated probabilities for downstream decisions.

**Q19. Compare NB with Logistic Regression.**
- **Generative vs Discriminative**: NB models `P(x|c)·P(c)`; LR models `P(c|x)` directly.
- NB is faster.
- NB needs less data.
- LR is generally better-calibrated.
- LR usually wins with lots of data; NB often wins with little.

**Q20. Compare NB with Decision Trees.**
- NB is a probabilistic linear classifier; trees are non-linear and rule-based.
- NB needs less data.
- Trees handle interactions; NB doesn't.
- NB outputs probabilities natively.

### D. Multi-Class & Iris

**Q21. How does NB handle multi-class?**
Naturally — for each class, compute the posterior; pick the largest. No special trick.

**Q22. What is the Iris dataset?**
A classic ML dataset: 150 iris flowers, 50 per species (setosa, versicolor, virginica), 4 numeric features per flower. Introduced by Fisher in 1936.

**Q23. Are Iris classes linearly separable?**
*Setosa* is linearly separable from the other two on petal length alone. *Versicolor* and *virginica* overlap mildly.

**Q24. Why does Gaussian NB work well on Iris?**
Within each species, the features are approximately normal and roughly independent. Classes are well-separated, especially on petal features. NB's assumptions are close enough to give >95% accuracy.

**Q25. What are the most discriminative Iris features?**
Petal length and petal width. Setosa has tiny petals; virginica has large; versicolor sits between with little overlap.

### E. Confusion Matrix and Multi-Class Metrics

**Q26. What does a 3-class confusion matrix look like?**
A 3×3 matrix. Diagonal cells = correct predictions. Off-diagonal cells = errors of specific types.

**Q27. How do you compute multi-class accuracy?**
`accuracy = trace(CM) / sum(CM)` — sum of diagonal divided by total.

**Q28. How is precision computed for a class in multi-class?**
For class `c`: `TP(c) / (TP(c) + FP(c))` where TP is the diagonal cell and FP is the column sum (excluding diagonal). Reads as "of predictions for class c, how many were correct".

**Q29. How is recall computed for a class in multi-class?**
For class `c`: `TP(c) / (TP(c) + FN(c))` where FN is the row sum (excluding diagonal). Reads as "of actual class-c examples, how many we caught".

**Q30. What's the difference between macro and weighted average?**
- **Macro** — unweighted mean (each class counts equally).
- **Weighted** — by class support (favors common classes).
For balanced datasets like Iris, they're essentially the same.

**Q31. What is micro-averaging?**
Compute TP/FP/FN globally across all classes, then compute the metric. In multi-class, micro-precision = micro-recall = micro-F1 = accuracy.

### F. Practical & Code

**Q32. How is Gaussian NB trained?**
For each class and feature, compute per-class mean and variance from training data. Also compute the prior as the class proportion. At prediction time, plug feature values into the normal PDF for each class, multiply naïvely, multiply by prior, and pick the largest.

**Q33. Is feature scaling needed for NB?**
Generally no. Gaussian NB is invariant to monotonic per-feature transformations. Multinomial and Bernoulli use counts. Scaling is harmless but unnecessary.

**Q34. Do you need a train/test split for NB?**
Yes — for honest evaluation. NB is so simple it tends not to overfit, but you still need the split to estimate generalization.

**Q35. Can NB overfit?**
Less than most models, but yes — especially with high-dimensional discrete features and no smoothing, where rare feature values memorize training peculiarities.

**Q36. How does NB compare with KNN?**
- NB is parametric; KNN is non-parametric (stores all data).
- NB is fast at inference; KNN is slow at inference (distance calculations).
- NB scales to huge data; KNN doesn't.

**Q37. Why is NB popular for text?**
- High dimensionality plays to NB's strength.
- Independence is roughly OK after stop-word removal.
- Fast — handles millions of docs trivially.
- Interpretable — per-word likelihoods inspectable.
- A strong baseline.

**Q38. What does `predict_proba` return for multi-class NB?**
An array with one column per class, each row a posterior. Columns sum to 1.

**Q39. What is generative vs discriminative learning?**
- **Generative** — models `P(x, c)`. Can synthesize new data. Often needs less training data.
- **Discriminative** — models `P(c|x)` directly. Often higher asymptotic accuracy.

**Q40. How would you improve a Naïve Bayes classifier?**
- Try other variants if features fit better.
- Apply feature selection.
- Tune smoothing constant.
- Combine with calibration (Platt, isotonic).
- Use class weights for imbalanced data.
- Use NB as baseline, then move to LR or gradient boosting if accuracy plateaus.
