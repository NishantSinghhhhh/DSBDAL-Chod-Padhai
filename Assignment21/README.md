# Assignment 21 — Naïve Bayes Classification on Iris Dataset

Notebook: `Assignment_21_Complete.ipynb`
Dataset: Iris (`iris.csv`) — 150 flowers, 4 numeric features, 3 species classes.

---

## 1. Classification — A Quick Recap

**Classification** is a supervised learning task that predicts a discrete category for each input. This assignment is a **multi-class** problem — three species: *Iris-setosa*, *Iris-versicolor*, *Iris-virginica*. Multi-class classification is a generalization of binary, where the algorithm must distinguish between three or more classes simultaneously.

We use **Naïve Bayes** — specifically **Gaussian Naïve Bayes** since the features are continuous numeric measurements.

---

## 2. Probability Foundations

### 2.1 Conditional Probability
The probability that event B occurs given that event A has occurred:

> P(B | A) = P(A ∩ B) / P(A)

In ML: given the features we observed, what is the probability the input belongs to a particular class?

### 2.2 Independence
Two events are **independent** if the occurrence of one tells you nothing about the other:

> P(A ∩ B) = P(A) · P(B), or equivalently P(B | A) = P(B)

### 2.3 Joint Probability
The probability of multiple events occurring together. For two features:

> P(X₁, X₂) = P(X₁) · P(X₂ | X₁)

If they're independent, this simplifies to `P(X₁) · P(X₂)`.

---

## 3. Bayes' Theorem

### 3.1 The Theorem

> P(Class | Features) = ( P(Features | Class) · P(Class) ) / P(Features)

| Term | Name | Meaning |
|---|---|---|
| `P(Class)` | **Prior** | Probability of the class before seeing features |
| `P(Features \| Class)` | **Likelihood** | Probability of features given the class |
| `P(Class \| Features)` | **Posterior** | Updated probability after seeing features |
| `P(Features)` | **Evidence** | Marginal probability — normalizing constant |

### 3.2 Why It Matters
Direct estimation of `P(Class | Features)` is hard. But `P(Features | Class)` is easy — count how often each feature value appears within each class. Bayes' theorem flips the conditional, giving us what we actually want from what we can easily estimate.

### 3.3 The Naïve Assumption
Computing the full likelihood `P(x₁, x₂, …, xₙ | Class)` is hard — it's a high-dimensional joint distribution. **Naïve Bayes** assumes all features are **conditionally independent given the class**:

> P(x₁, x₂, …, xₙ | Class) = P(x₁ | Class) · P(x₂ | Class) · … · P(xₙ | Class)

This is rarely literally true, but the resulting classifier still works surprisingly well — often competitively with much more complex methods.

### 3.4 Why It Works Despite a Wrong Assumption
- Even if absolute probabilities are wrong, the *relative ordering* of class posteriors is often correct.
- Errors from the independence assumption tend to cancel rather than compound.
- The decision boundary depends on which class has the highest posterior, not on the exact values.
- It converges fast and needs little data.

---

## 4. Variants of Naïve Bayes

### 4.1 Gaussian Naïve Bayes
- For **continuous numeric** features.
- Assumes each feature, given the class, is **normally distributed**.
- Estimates per-class mean `μ` and variance `σ²` from training data.
- Uses the normal-distribution PDF as the likelihood.
- **Used here for Iris** because all 4 features are continuous measurements.

### 4.2 Multinomial Naïve Bayes
- For **discrete count** features, especially text.
- Assumes features follow a multinomial distribution.
- The standard model for text classification (spam, sentiment).

### 4.3 Bernoulli Naïve Bayes
- For **binary** features (presence / absence).
- Each feature is 0 or 1 — Bernoulli distributed.
- Used in text classification when features are word presence rather than counts.

### 4.4 Complement Naïve Bayes
- Variation of Multinomial NB designed for **imbalanced** text data.
- Recommended for spam detection where ham >> spam.

---

## 5. The Iris Dataset

- 150 flowers, 50 from each of 3 species.
- 4 numeric features: sepal length, sepal width, petal length, petal width — all in cm.
- Introduced by Ronald Fisher in 1936.
- Setosa is **linearly separable** from the others; versicolor and virginica overlap mildly.
- Tiny, balanced, and clean — ideal for teaching classification.

---

## 6. Multi-Class Classification with Naïve Bayes

NB handles multi-class natively — for each input, compute the posterior probability for **every** class and pick the largest:

> ŷ = argmax_c P(c) · ∏ᵢ P(xᵢ | c)

No special trick needed. This is one reason NB is popular in problems with many classes.

Other multi-class strategies (used by other algorithms):

- **One-vs-Rest (OvR)** — train *k* binary classifiers, one per class.
- **One-vs-One (OvO)** — train *k(k−1)/2* classifiers, one per class pair.
- **Softmax** — multinomial logistic regression natively handles multi-class.

---

## 7. The Confusion Matrix in Multi-Class

For 3 classes, the confusion matrix is **3×3** instead of 2×2.

- Rows = actual classes; columns = predicted.
- Diagonal cells `(i, i)` = correct predictions.
- Off-diagonal cells `(i, j)` = examples truly of class `i` predicted as `j`.

Per-class metrics use **one-vs-rest**: treat the class of interest as positive, the others as negative.

---

## 8. Strengths and Weaknesses

### Strengths
- **Fast** to train — no iterative optimization, just frequency counts.
- **Works with little data** — needs only basic statistics per class.
- **Naturally handles multi-class** problems.
- **Probabilistic output** — interpretable.
- **Robust to irrelevant features** — their likelihoods are similar across classes.
- **Strong baseline**, especially for text classification.

### Weaknesses
- The independence assumption is rarely literally true.
- Probabilities are often **poorly calibrated** (pushed toward 0 or 1).
- Continuous features must satisfy a parametric assumption (normality for Gaussian NB).
- Suffers from the **zero-frequency problem** without smoothing (Multinomial / Bernoulli only).

---

## 9. Laplace Smoothing

In Multinomial / Bernoulli NB, if a feature value never co-occurs with a class in training, its likelihood is exactly zero — multiplying by zero wipes out the entire posterior. **Laplace (additive) smoothing** adds a small constant `α` (usually 1) to every count:

> P̂(xᵢ | c) = (count(xᵢ, c) + α) / (count(c) + α · |V|)

Ensures no probability is exactly zero. The hyperparameter trades bias (more smoothing) against variance (less smoothing).

Gaussian NB doesn't suffer this problem directly because its likelihood is a continuous PDF.

---

## 10. Generative vs Discriminative Models

| | Generative (NB) | Discriminative (Logistic Regression) |
|---|---|---|
| Models | `P(x, c)` (or `P(x|c)·P(c)`) | `P(c|x)` directly |
| Learns | How data is generated | The decision boundary only |
| Can | Generate new data | Classify only |
| Data efficiency | Better with little data | Better with lots |
| Asymptotic accuracy | Lower | Higher |

NB is the canonical generative classifier; logistic regression is the canonical discriminative one. They typically perform similarly when the NB independence assumption roughly holds.

---

## 11. Viva Questions (40)

### A. Probability Foundations

**Q1. What is conditional probability?**
The probability of one event given another has occurred. Written `P(B | A) = P(A ∩ B) / P(A)`. In ML, it's the foundation of all probabilistic classification — given the features we observed, how likely is each class?

**Q2. What is Bayes' theorem and why is it important?**
A formula relating conditional probabilities: `P(A | B) = P(B | A) · P(A) / P(B)`. It lets us flip a conditional we don't know directly into one we *can* estimate from data. In classification, we estimate `P(features | class)` from training data and use Bayes' theorem to compute `P(class | features)`.

**Q3. What is a prior probability?**
The probability of a class **before** seeing any features. In a balanced 3-class problem, each prior is 1/3; in spam detection where 60% of email is spam, the prior `P(spam) = 0.6`. Priors encode background knowledge about class frequencies.

**Q4. What is a likelihood?**
`P(features | class)` — given that the class is true, how probable are the observed features? For Gaussian NB this comes from a normal-distribution PDF; for Multinomial NB from counts of feature values within each class.

**Q5. What is a posterior probability?**
`P(class | features)` — the probability of a class **after** observing the features. The model's actual answer. We pick the class with the highest posterior (MAP — Maximum A Posteriori).

**Q6. What is the evidence in Bayes' theorem?**
`P(features)` — the marginal probability of the features, summed across all classes. Acts as a normalizing constant. Often skipped because it cancels when we compare class posteriors.

### B. Naïve Bayes Specifics

**Q7. What is Naïve Bayes?**
A probabilistic classifier built on Bayes' theorem with the simplifying assumption that all features are conditionally independent given the class. It picks the class with the highest posterior, computed as the prior times the product of per-feature likelihoods.

**Q8. Why is it called "naïve"?**
Because it assumes features are conditionally independent given the class — a simplification almost never literally true. Despite this naïveté, it often works very well, especially with high-dimensional data or small samples.

**Q9. What is the conditional independence assumption?**
The assumption that, given the class label, knowing one feature tells you nothing about the others: `P(x₁, x₂ | c) = P(x₁ | c) · P(x₂ | c)`. The simplification that makes Naïve Bayes computationally tractable.

**Q10. Why does Naïve Bayes still work despite a wrong assumption?**
Even if absolute probabilities are biased, the relative ordering of class posteriors is often correct. The decision rule (argmax) only needs the right *order*, not the right *values*. Errors from independence violations also tend to cancel rather than compound.

**Q11. What are the three main variants of Naïve Bayes?**
- **Gaussian NB** — for continuous features assumed normally distributed (Iris features).
- **Multinomial NB** — for discrete counts, especially word frequencies (text classification).
- **Bernoulli NB** — for binary features (word presence/absence).

**Q12. Which Naïve Bayes variant fits Iris and why?**
**Gaussian NB** — all four features are continuous numeric measurements that look approximately normal within each species. We estimate per-class means and variances and use the normal-distribution PDF as the likelihood.

**Q13. What is the zero-frequency problem?**
In discrete Naïve Bayes, if a feature value never co-occurs with a class in training, its likelihood is exactly zero. Multiplying by zero wipes out the entire posterior product. Solved by Laplace smoothing.

**Q14. What is Laplace (additive) smoothing?**
Adding a small constant `α` (usually 1) to every count when estimating likelihoods. Ensures no probability is ever exactly zero. The trade-off: more smoothing reduces variance but biases toward uniform.

**Q15. What is MAP estimation?**
**Maximum A Posteriori** — choose the class that maximizes the posterior `P(c | x)`, equivalently `P(c) · ∏ P(xᵢ | c)`. The standard decision rule for Naïve Bayes and many other probabilistic classifiers.

### C. Strengths & Weaknesses

**Q16. What are the strengths of Naïve Bayes?**
- Very fast — no iterative optimization, just frequency counts.
- Works with small datasets — needs only a few examples per class.
- Handles multi-class problems natively.
- Output is a probability — interpretable.
- Robust to irrelevant features.
- Excellent baseline for text classification.

**Q17. What are the weaknesses of Naïve Bayes?**
- Assumes feature independence, which is rarely true.
- Probabilities are often poorly calibrated.
- Gaussian NB requires feature normality, which is often violated.
- Suffers from the zero-frequency problem without smoothing.
- Cannot model feature interactions.

**Q18. When would you NOT use Naïve Bayes?**
When features are strongly correlated (the independence assumption breaks down badly), when feature interactions matter for the decision, or when you need well-calibrated probabilities for downstream decisions.

**Q19. Compare Naïve Bayes with Logistic Regression.**
- **Generative vs Discriminative**: NB models `P(x | c)·P(c)`; LR models `P(c | x)` directly.
- **Speed**: NB is faster.
- **Data efficiency**: NB needs less data.
- **Calibration**: LR is generally better-calibrated.
- **Asymptotic accuracy**: with infinite data, LR typically wins; with little data, NB often wins.

**Q20. Compare Naïve Bayes with Decision Trees.**
- NB is a probabilistic linear classifier; trees are non-linear and rule-based.
- NB needs less data; trees are more flexible but easier to overfit.
- Trees handle feature interactions; NB cannot.
- NB outputs probabilities natively; trees produce class labels.

### D. Multi-Class & Iris

**Q21. How does Naïve Bayes handle multi-class problems?**
Naturally — for each class `c`, compute the posterior `P(c) · ∏ P(xᵢ | c)`, then pick the class with the largest value. No special trick needed. This is one reason NB is popular for problems with many classes.

**Q22. What is the Iris dataset?**
A classic ML dataset of 150 iris flowers (50 each of three species), each with 4 measurements (sepal length, sepal width, petal length, petal width). Introduced by Ronald Fisher in 1936; clean, balanced, and tiny — ideal for teaching classification.

**Q23. Are the Iris classes linearly separable?**
*Iris-setosa* is linearly separable from the other two on petal length alone. *Versicolor* and *virginica* overlap mildly — they require a non-linear or probabilistic decision boundary for perfect separation.

**Q24. Why does Gaussian NB work well on Iris?**
Within each species, the four features are approximately normally distributed and roughly independent. Classes are well-separated in feature space, especially on petal measurements. NB's modeling assumptions are close enough to reality to give >95% accuracy.

**Q25. What are the most discriminative Iris features?**
Petal length and petal width. Setosa has tiny petals; virginica has large; versicolor sits in between. The separation on these two features is so clean that even simple classifiers perform near-perfectly.

### E. Confusion Matrix and Multi-Class Metrics

**Q26. What does a confusion matrix look like for 3 classes?**
A 3×3 matrix. Rows are actual classes, columns predicted. Diagonal cells count correct predictions; off-diagonal cells count specific kinds of errors.

**Q27. How do you compute accuracy in multi-class?**
Sum of diagonal entries divided by total: `accuracy = trace(CM) / sum(CM)`. Same intuition as the binary case.

**Q28. How is precision computed for a class in multi-class?**
For class `c`: `precision(c) = TP(c) / (TP(c) + FP(c))` where `TP(c)` is the diagonal cell and `FP(c)` is the sum of the column excluding the diagonal. Reads as "of the predictions for class c, how many were correct".

**Q29. How is recall computed for a class in multi-class?**
For class `c`: `recall(c) = TP(c) / (TP(c) + FN(c))` where `FN(c)` is the sum of the row excluding the diagonal. Reads as "of the actual class-c examples, how many we correctly identified".

**Q30. What's the difference between macro and weighted average?**
- **Macro** — unweighted mean across classes; treats every class equally regardless of size.
- **Weighted** — each class's metric weighted by its support; reflects overall performance more closely.
For balanced datasets like Iris, macro and weighted are essentially the same.

**Q31. What is micro-averaging?**
Compute TP, FP, FN globally across all classes, then compute the metric. In multi-class settings, micro-precision = micro-recall = micro-F1 = accuracy.

### F. Practical & Code

**Q32. How is Gaussian NB trained?**
For each class `c` and each feature `x`, compute the per-class mean `μ` and variance `σ²` from training data. Also compute the prior `P(c)` as the class proportion. At prediction time, plug feature values into the normal-distribution PDF for each class, multiply (naïvely), multiply by the prior, and pick the largest.

**Q33. Is feature scaling needed for Naïve Bayes?**
Generally no. Gaussian NB is invariant to monotonic per-feature transformations (the mean/variance scale together). Multinomial and Bernoulli NB use counts, which aren't scaled. Scaling is harmless but unnecessary.

**Q34. Do you need a train/test split for Naïve Bayes?**
Yes — for honest evaluation. NB is so simple it tends not to overfit, but you still need the split to estimate generalization performance, compare to other models, and tune hyperparameters like the smoothing constant.

**Q35. Can Naïve Bayes overfit?**
Less than most models, but yes — especially with high-dimensional discrete features and no smoothing, where rare feature values memorize training peculiarities. Smoothing and feature selection address this.

**Q36. How does NB compare with KNN?**
- **NB** is parametric (fixed parameters), trains in one pass.
- **KNN** is non-parametric; "training" stores the data, prediction does the work (slow inference).
- NB scales to huge data; KNN struggles past tens of thousands of rows.
- NB outputs calibrated-ish probabilities; KNN doesn't natively.

**Q37. Why is NB popular for text classification?**
- High-dimensional text plays to NB's strength of handling many features.
- The independence assumption is roughly OK for words after stop-word removal.
- It's fast — training and predicting a million documents is trivial.
- Interpretable — per-word likelihoods can be inspected.
- A strong baseline that often beats more complex models on small text datasets.

**Q38. What does `predict_proba` return for a multi-class NB model?**
An array with one column per class containing the posterior probability of each class. Columns sum to 1 across each row. Use it when you need rankings or threshold-tuned predictions.

**Q39. What is generative vs discriminative learning?**
- **Generative** models (NB) learn `P(x, c)` — they model how data is generated and can also synthesize new data.
- **Discriminative** models (LR, SVM) learn `P(c | x)` directly, focusing only on the decision boundary.
Generative methods often need less data; discriminative methods often achieve higher asymptotic accuracy.

**Q40. How would you improve a Naïve Bayes classifier?**
- Try other variants (Multinomial / Bernoulli) if features fit better.
- Apply feature selection to remove uninformative features.
- Tune the smoothing constant via cross-validation.
- Combine with calibration (Platt scaling, isotonic regression) for better probability estimates.
- Use class weights or resampling for imbalanced data.
- Use NB as a fast baseline, then move to logistic regression or gradient boosting if accuracy plateaus.
