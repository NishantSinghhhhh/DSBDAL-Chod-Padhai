# Assignment 10 — Naïve Bayes Classification on Iris Dataset

Notebook: `Assignment_10_Complete.ipynb`
Dataset: Iris (`iris.csv`) — 150 flowers, 4 numeric features (sepal length, sepal width, petal length, petal width), 3 species classes.

---

## 1. The Foundation: Probability Theory

### 1.1 Conditional Probability
Conditional probability answers the question: *given that some event A has occurred, what is the probability that event B also occurs?*

> P(B | A) = P(A ∩ B) / P(A)

In ML, we read it as: given that we observed certain features, what is the probability the input belongs to a particular class?

### 1.2 Independence
Two events A and B are **independent** if the occurrence of one does not affect the probability of the other.

- Mathematically: `P(A ∩ B) = P(A) · P(B)`.
- Equivalently: `P(B | A) = P(B)`.
- Intuitively: knowing A tells you nothing about B.

### 1.3 Joint Probability
The probability that two (or more) events both occur. For two features:

> P(X₁, X₂) = P(X₁) · P(X₂ | X₁)

When X₁ and X₂ are independent, this simplifies to `P(X₁) · P(X₂)`.

---

## 2. Bayes' Theorem

### 2.1 The Theorem
The mathematical heart of Naïve Bayes:

> P(Class | Features) = ( P(Features | Class) · P(Class) ) / P(Features)

Each term has a name:

- **P(Class)** — the **prior**: how likely is this class *before* seeing any features.
- **P(Features | Class)** — the **likelihood**: how likely are these features if the class were true.
- **P(Class | Features)** — the **posterior**: how likely is the class *after* seeing the features. The model's actual prediction.
- **P(Features)** — the **evidence** (or marginal): a normalizing constant that ensures probabilities sum to 1.

### 2.2 Why It's Useful
We rarely know `P(Class | Features)` directly. But we can estimate `P(Features | Class)` from training data — it's just "how often did each feature value appear within each class?". Bayes' theorem flips this around to give us what we actually want.

### 2.3 The "Naïve" Assumption
The full likelihood `P(x₁, x₂, …, xₙ | Class)` is hard to estimate — it's a high-dimensional joint distribution. **Naïve Bayes** makes a strong simplifying assumption: **all features are conditionally independent given the class**. So:

> P(x₁, x₂, …, xₙ | Class) = P(x₁ | Class) · P(x₂ | Class) · … · P(xₙ | Class)

This assumption is rarely literally true in real data, but the resulting classifier still performs surprisingly well — often competitively with much more complex methods, especially in text classification.

### 2.4 Why It Works Despite the Naïve Assumption
- Even if the absolute probabilities are wrong, the *relative ordering* of class probabilities is often correct.
- Errors from the independence assumption tend to cancel rather than compound.
- The decision boundary depends on which class has the highest posterior, not on the exact probability values.
- Naïve Bayes converges very fast and needs little data — even 50 samples per class gives reasonable results.

---

## 3. Variants of Naïve Bayes

### 3.1 Gaussian Naïve Bayes
- **For**: continuous numeric features.
- **Assumption**: each feature, given the class, is normally distributed.
- **Estimate**: per class and feature, learn `μ` (mean) and `σ²` (variance) from training data.
- **Likelihood**: plug a feature value into the normal-distribution PDF for each class and multiply (with the others, naïvely).
- Used for the Iris dataset because all four features are continuous measurements.

### 3.2 Multinomial Naïve Bayes
- **For**: discrete count data, especially text.
- **Assumption**: features are word/term counts following a multinomial distribution.
- **Estimate**: per class and term, the smoothed term frequency.
- The standard model for **text classification** (spam, sentiment, topic).

### 3.3 Bernoulli Naïve Bayes
- **For**: binary features (present/absent).
- **Assumption**: each feature is either 0 or 1 — Bernoulli distributed.
- Used in text classification when features are word presence (0/1) rather than counts.

### 3.4 Complement Naïve Bayes
- A modification that handles **class imbalance** better by computing the complement of each class.
- Recommended for imbalanced text classification (spam detection where ham >> spam).

---

## 4. The Iris Dataset

- 150 flower samples, 50 from each of 3 species: *Iris-setosa*, *Iris-versicolor*, *Iris-virginica*.
- 4 features per flower: sepal length, sepal width, petal length, petal width — all in centimeters.
- Introduced by Ronald Fisher in 1936; one of the most famous datasets in statistics and ML.
- Setosa is **linearly separable** from the other two; versicolor and virginica overlap somewhat.
- Tiny, balanced, and clean — perfect for teaching classification.

---

## 5. Multi-Class Classification

The Iris problem has **3 classes**, not 2. Naïve Bayes handles this naturally — for each input, compute the posterior probability for *every* class and pick the largest:

> ŷ = argmax_c P(c) · ∏ᵢ P(xᵢ | c)

Other approaches:
- **Logistic regression** uses **softmax** for multi-class.
- **One-vs-Rest (OvR)** trains *k* binary classifiers, one per class.
- **One-vs-One (OvO)** trains *k(k−1)/2* classifiers, one per class pair.

---

## 6. Confusion Matrix in Multi-Class
For 3 classes, the confusion matrix is **3×3** instead of 2×2. The diagonal cells `(i, i)` count correct predictions of class `i`. Off-diagonal cells `(i, j)` count examples truly of class `i` that the model predicted as class `j`. Per-class precision, recall, and F1 are computed by treating each class as the "positive" class in turn (one-vs-rest).

Aggregations:

- **Macro-average** — unweighted mean across classes (treats all classes equally).
- **Weighted-average** — weighted by class support (favors common classes).
- **Micro-average** — pooled TP/FP/FN across classes (equals accuracy in multi-class).

---

## 7. Strengths and Weaknesses of Naïve Bayes

### Strengths
- **Fast** to train and predict — no iterative optimization required.
- **Works with little data** — only needs frequency counts.
- **Naturally handles multi-class** problems.
- **Probabilistic output** that is interpretable.
- **Robust to irrelevant features** (their likelihood is similar across classes).
- Good baseline, often competitive in **text classification**.

### Weaknesses
- The independence assumption is rarely literally true.
- Probabilities are often poorly **calibrated** (push toward 0 or 1).
- Continuous features must satisfy a parametric assumption (normality for Gaussian NB).
- **Zero-frequency problem**: if a feature value never appeared with a class in training, the posterior collapses to 0. Solved by **Laplace (add-one) smoothing**.

---

## 8. Laplace Smoothing
The zero-frequency problem occurs in discrete Naïve Bayes (Multinomial, Bernoulli) when a feature value never co-occurs with a class in training. Multiplying the likelihood by 0 wipes out the entire posterior.

**Laplace smoothing** adds a small constant `α` (commonly 1) to every count:

> P̂(xᵢ | c) = (count(xᵢ, c) + α) / (count(c) + α · |V|)

`|V|` is the vocabulary / feature-value cardinality. With α = 1, no probability is ever exactly 0; with α → 0, no smoothing is applied. The hyperparameter trades bias (more smoothing) against variance (less smoothing).

---

## 9. Viva Questions (40)

### A. Probability Foundations

**Q1. What is conditional probability?**
The probability of one event occurring given that another has already happened. Written `P(B | A) = P(A ∩ B) / P(A)`. In machine learning we use it to ask: given that we observed certain features, how likely is each class? It's the foundation of all probabilistic classification.

**Q2. What is Bayes' theorem and why is it important?**
A formula relating conditional probabilities: `P(A | B) = P(B | A) · P(A) / P(B)`. It lets us "flip" a conditional we don't know directly into one we *can* estimate from data. In classification, we estimate `P(features | class)` from training data and use Bayes' theorem to compute the desired `P(class | features)`.

**Q3. What is a prior probability?**
The probability of a class *before* seeing any features. In a balanced 3-class problem, each prior is 1/3; in spam detection where 60% of email is spam, the prior `P(spam) = 0.6`. Priors encode our background knowledge about class frequencies.

**Q4. What is a likelihood?**
`P(features | class)` — given that the class is true, how probable are the observed features? For Gaussian NB this comes from a normal-distribution PDF; for Multinomial NB from counts of feature values within each class.

**Q5. What is a posterior probability?**
`P(class | features)` — the probability of a class *after* observing the features. The model's actual answer. We choose the class with the highest posterior (MAP — Maximum A Posteriori — decision rule).

**Q6. What is the evidence in Bayes' theorem?**
`P(features)` — the marginal probability of the features, summed across all classes. Acts as a normalizing constant ensuring that posteriors sum to 1. Often skipped in classification because it cancels when we compare class posteriors.

### B. Naïve Bayes Specifics

**Q7. What is Naïve Bayes?**
A probabilistic classifier built on Bayes' theorem with the simplifying assumption that all features are conditionally independent given the class. It picks the class that maximizes the posterior probability, computed as the prior times the product of per-feature likelihoods.

**Q8. Why is Naïve Bayes called "naïve"?**
Because it assumes all features are conditionally independent given the class — a simplification that's almost never literally true. Despite this naïveté, the classifier often works very well, especially when data is small or the dimensionality is high.

**Q9. What is the conditional independence assumption?**
The assumption that, given the class label, knowing one feature tells you nothing about the others. Mathematically: `P(x₁, x₂ | c) = P(x₁ | c) · P(x₂ | c)`. It's the simplification that makes Naïve Bayes computationally tractable.

**Q10. Why does Naïve Bayes still work despite a wrong assumption?**
Even if the absolute probabilities are biased, the *relative ordering* of class posteriors is often correct. The decision rule (argmax) only needs the right *order*, not the right *values*. Errors from the independence assumption also tend to cancel rather than compound across features.

**Q11. What are the three main variants of Naïve Bayes?**
- **Gaussian NB** — for continuous features assumed normally distributed (Iris features).
- **Multinomial NB** — for discrete counts, especially word frequencies (text classification).
- **Bernoulli NB** — for binary features (word presence/absence).

**Q12. Which Naïve Bayes variant is appropriate for the Iris dataset and why?**
Gaussian Naïve Bayes — all four features (sepal/petal length and width) are continuous numeric measurements that look approximately normal within each species. We estimate per-class means and variances and use the normal-distribution PDF as the likelihood.

**Q13. What is the zero-frequency problem?**
In discrete Naïve Bayes, if a feature value never co-occurs with a class in training, its likelihood is exactly zero. When you multiply zero into the posterior product, the entire posterior becomes zero — wiping out information from other features. The fix is Laplace smoothing.

**Q14. What is Laplace (additive) smoothing?**
Adding a small constant `α` (usually 1) to every count when estimating likelihoods. Ensures no probability is ever exactly zero. The trade-off: more smoothing reduces variance but biases toward uniform; less smoothing has the opposite effect.

**Q15. What is MAP estimation in the context of Naïve Bayes?**
Maximum A Posteriori — choose the class that maximizes the posterior `P(c | x)`, equivalently `P(c) · ∏ P(xᵢ | c)`. The standard decision rule for Naïve Bayes (and many other probabilistic classifiers).

### C. Strengths and Weaknesses

**Q16. What are the main strengths of Naïve Bayes?**
- Very fast — no iterative optimization, just frequency counts.
- Works well with small datasets — needs only a few examples per class.
- Handles multi-class problems natively.
- Output is a probability, which is interpretable.
- Robust to irrelevant features (their likelihoods are similar across classes).
- Excellent baseline for text classification.

**Q17. What are the main weaknesses of Naïve Bayes?**
- Assumes feature independence, which is rarely true.
- Probabilities are often poorly calibrated — pushed toward 0 or 1.
- Gaussian NB assumes feature normality, which can be violated.
- Suffers from the zero-frequency problem without smoothing.
- Cannot model feature interactions.

**Q18. When would you NOT use Naïve Bayes?**
When features are strongly correlated (the independence assumption breaks down badly), when feature interactions matter for the decision, or when you need well-calibrated probabilities for downstream decisions (use logistic regression with calibration instead).

**Q19. Compare Naïve Bayes with Logistic Regression.**
- **Generative vs Discriminative**: NB models `P(x | c)` and `P(c)` (joint distribution); logistic regression models `P(c | x)` directly (decision boundary).
- **Speed**: NB is faster to train.
- **Data efficiency**: NB needs less data; logistic regression needs more.
- **Calibration**: logistic regression generally has better-calibrated probabilities.
- **Asymptotic accuracy**: with infinite data, logistic regression typically wins; with little data, NB wins.

**Q20. Compare Naïve Bayes with Decision Trees.**
- NB is a probabilistic linear classifier; trees are non-linear and rule-based.
- NB needs less data; trees are more flexible but easier to overfit.
- Trees handle feature interactions; NB cannot.
- NB outputs probabilities natively; trees produce class labels (probabilities require calibration).

### D. Multi-Class & Iris Specifics

**Q21. How does Naïve Bayes handle multi-class problems?**
Naturally — for each class `c`, compute the posterior `P(c) · ∏ P(xᵢ | c)`, then pick the class with the largest value. No special trick needed. This is one reason NB is popular in problems with many classes.

**Q22. What is the Iris dataset?**
A classic ML dataset of 150 iris flowers (50 each of three species — setosa, versicolor, virginica), each with 4 measurements (sepal length, sepal width, petal length, petal width). Introduced by Ronald Fisher in 1936; clean, balanced, and tiny — ideal for teaching classification.

**Q23. Are the Iris classes linearly separable?**
*Iris-setosa* is linearly separable from the other two on petal-length alone. *Versicolor* and *virginica* overlap somewhat in feature space — they require a non-linear or probabilistic decision boundary for perfect separation, and even then misclassifications occur.

**Q24. Why does Gaussian NB work so well on Iris?**
Within each species, the four features are approximately normally distributed and roughly independent. The classes are well-separated in feature space, especially on petal measurements. So NB's modeling assumptions are close enough to reality to give >95% accuracy on this dataset.

### E. Confusion Matrix and Multi-Class Metrics

**Q25. What does a confusion matrix look like for 3 classes?**
A 3×3 matrix. Rows are actual classes, columns are predicted. The diagonal counts correct predictions; off-diagonal cells count specific kinds of errors. For example, cell (versicolor, virginica) counts versicolor flowers misclassified as virginica.

**Q26. How do you compute accuracy in multi-class classification?**
Sum of diagonal entries divided by total number of predictions: `accuracy = trace(CM) / sum(CM)`. Same intuition as the binary case.

**Q27. How is precision computed for a class in multi-class?**
For class `c`: `precision(c) = TP(c) / (TP(c) + FP(c))` where TP(c) is the diagonal cell for class c and FP(c) is the sum of the column for c (excluding the diagonal). Reads as "of the predictions for class c, how many were correct".

**Q28. How is recall computed for a class in multi-class?**
For class `c`: `recall(c) = TP(c) / (TP(c) + FN(c))` where FN(c) is the sum of the row for c (excluding the diagonal). Reads as "of the actual class-c examples, how many we correctly identified".

**Q29. What's the difference between macro and weighted average?**
- **Macro-average**: unweighted mean across classes — treats every class equally regardless of size. Good for imbalanced data when each class matters equally.
- **Weighted-average**: each class's metric is weighted by its support (number of true instances). Closer to overall accuracy on imbalanced data.

**Q30. What is micro-averaging?**
Compute TP, FP, FN globally across all classes, then compute the metric. In multi-class settings, micro-precision = micro-recall = micro-F1 = accuracy. Used when every individual prediction matters equally.

### F. Practical & Code-Specific

**Q31. How is Gaussian NB trained?**
For each class `c` and each feature `x`, compute the mean `μ(x | c)` and variance `σ²(x | c)` from training data. Also compute the prior `P(c)` as the class proportion. At prediction time, plug feature values into the normal-distribution PDF for each class, multiply with the others (naïvely), multiply by the prior, and pick the largest.

**Q32. Is feature scaling needed for Naïve Bayes?**
Generally no. Gaussian NB is invariant to monotonic transformations of individual features (the mean/variance scale together). Multinomial and Bernoulli NB use counts, which aren't scaled. Scaling is harmless but unnecessary — unlike for distance-based or gradient-descent methods.

**Q33. Do you need a train/test split for Naïve Bayes?**
Yes — for honest evaluation of any supervised model. NB is so simple it tends not to overfit, but you still need the split to estimate generalization performance, compare to other models, and tune hyperparameters like the smoothing constant.

**Q34. Can Naïve Bayes overfit?**
Less than most models, but yes — especially with high-dimensional discrete features and no smoothing, where rare feature values memorize training peculiarities. Smoothing and feature selection address this.

**Q35. How does Naïve Bayes compare to KNN?**
- **NB** is parametric (fixed number of parameters), trains in one pass.
- **KNN** is non-parametric; "training" stores the data, and prediction does the work (slow at inference).
- **NB** scales to huge data; **KNN** struggles past tens of thousands of rows.
- **NB** outputs calibrated-ish probabilities; **KNN** doesn't natively.

**Q36. Why is Naïve Bayes popular for text classification?**
- High-dimensional text data plays to NB's strength of handling many features.
- The independence assumption is roughly OK for words after stop-word removal.
- It's fast — training and predicting a million documents is trivial.
- It's interpretable — per-word likelihoods can be inspected.
- It's a strong baseline that often beats more complex models on small text datasets.

**Q37. What does `predict_proba` return for a multi-class NB model?**
An array with one column per class containing the posterior probability of each class. The columns sum to 1 across each row. Use it when you need rankings or calibrated thresholds rather than just hard class labels.

**Q38. What does `class_prior` mean?**
The prior probability of each class — usually estimated from training-data class frequencies but can be set manually if you have domain knowledge about real-world class proportions different from those in your training sample.

**Q39. What is generative vs discriminative learning?**
- **Generative** models — like Naïve Bayes — learn `P(x, c)` (or `P(x | c)` and `P(c)`), so they model how data is generated. They can also generate synthetic data.
- **Discriminative** models — like logistic regression — learn `P(c | x)` directly, focusing only on the decision boundary.
Generative methods often need less data; discriminative methods often achieve higher asymptotic accuracy.

**Q40. How would you improve a Naïve Bayes classifier?**
- Try other variants (Multinomial / Bernoulli) if features fit better.
- Apply feature selection to remove uninformative features.
- Tune the smoothing constant via cross-validation.
- Combine with calibration (e.g., Platt scaling, isotonic regression) for better probability estimates.
- Use class weights or resampling for imbalanced data.
- Use it as a fast baseline, then move to logistic regression or gradient boosting if accuracy plateaus.
