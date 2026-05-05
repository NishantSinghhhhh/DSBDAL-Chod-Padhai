# Assignment 17 — Data Visualization II: Age × Sex × Survival Boxplot (Titanic)

Notebook: `Assignment_17_Complete.ipynb`
Dataset: `titanic.csv`
Focus: a deep dive into the **boxplot** — its anatomy, what it reveals, and how to use grouped boxplots to compare distributions across multiple categorical dimensions.

---

## 1. The Boxplot

### 1.1 Origin
The **boxplot** (also called a box-and-whisker plot) was popularized by John Tukey in his 1977 book *Exploratory Data Analysis*. It was designed as a compact, robust way to summarize a distribution that would not be misled by outliers — a deliberate contrast to the mean-and-standard-deviation summaries that dominated statistics at the time.

### 1.2 Anatomy

| Component | Meaning |
|---|---|
| **Bottom of box** | Q1 (25th percentile) |
| **Middle line** | Median (50th percentile) |
| **Top of box** | Q3 (75th percentile) |
| **Lower whisker** | Smallest value above `Q1 − 1.5·IQR` |
| **Upper whisker** | Largest value below `Q3 + 1.5·IQR` |
| **Dots beyond whiskers** | Outliers |

### 1.3 The Five-Number Summary
A boxplot is a visual rendering of the **five-number summary**: minimum, Q1, median, Q3, maximum (plus outliers). With this single shape you can read the typical value (median), the spread (box height = IQR), the asymmetry (where the median sits in the box, where the whiskers extend), and the outliers — all at a glance.

### 1.4 What It Hides
Boxplots are dense but they hide:

- **Modality** — a bimodal distribution and a unimodal distribution can produce identical boxplots.
- **Sample size** — a 50-row group and a 5000-row group look the same.
- **Exact distribution shape** — only summary statistics are shown.

For these you need a histogram, KDE, or violin plot. The boxplot is best at **comparing distributions across groups** quickly; it's not the right tool for examining one distribution in detail.

---

## 2. The IQR (Interquartile Range)

### 2.1 Definition

> IQR = Q3 − Q1

The spread of the middle 50% of the data. It's a robust measure of dispersion — outliers don't shift Q1 or Q3, so the IQR is unaffected by them.

### 2.2 The 1.5·IQR Rule
The whiskers extend to `Q1 − 1.5·IQR` and `Q3 + 1.5·IQR`. Anything beyond is flagged as an outlier. The 1.5 multiplier was chosen by Tukey to capture about 99.3% of a normal distribution — values past the whiskers are unusual.

For **strict outlier detection**, sometimes 3·IQR is used instead, capturing only the most extreme observations.

### 2.3 IQR vs Standard Deviation

| Metric | Robust to Outliers? | Distribution Assumption |
|---|---|---|
| **IQR** | Yes | None |
| **Standard Deviation** | No | Roughly normal |

For skewed or heavy-tailed data, IQR-based summaries are far more reliable than standard-deviation-based ones.

---

## 3. Reading a Boxplot

### 3.1 Position of the Median Inside the Box
- Centered → distribution is roughly symmetric.
- Pushed toward Q1 (bottom) → right-skewed.
- Pushed toward Q3 (top) → left-skewed.

### 3.2 Whisker Length
- Both whiskers similar → symmetric.
- Upper whisker much longer → right-skewed.
- Lower whisker much longer → left-skewed.

### 3.3 Outlier Density
- Many outliers above → heavy right tail or extreme values.
- Many outliers below → heavy left tail.
- Outliers on both sides → heavy-tailed distribution overall.

### 3.4 Box Height (IQR)
- Tall box → wide spread of central values.
- Short box → tight clustering of central values.

---

## 4. Grouped Boxplots

### 4.1 What They Are
A **grouped boxplot** draws side-by-side boxes for each level of a categorical variable, optionally further split by a second category (with a `hue` parameter). It lets you compare distributions across groups at a glance.

### 4.2 Why They're Powerful
- They **align** boxes on a common axis, making direct comparison effortless.
- They show the median, spread, skew, and outliers of every group simultaneously.
- They surface **interactions** — does the relationship between A and Y depend on category B?

### 4.3 The Titanic Age × Sex × Survival Plot
- **x = sex** (male, female).
- **y = age**.
- **hue = survived** (0 = no, 1 = yes).

Reading the plot reveals:

- **Male non-survivors are older** than male survivors — boys survived more than men (children-first effect).
- **Female age** doesn't correlate as strongly with survival — women survived at high rates across all ages.
- **The age distribution of women is broader** than that of men.
- **Young children** (visible as low outliers among male non-survivors) are disproportionately represented among survivors.

These insights would be hard to read from a table of summary statistics; a single boxplot makes them obvious.

---

## 5. Boxplots vs Other Plots

### 5.1 Boxplot vs Histogram

| Aspect | Boxplot | Histogram |
|---|---|---|
| Shows | Summary statistics | Full distribution |
| Best for | Group comparison | Examining one distribution |
| Hides | Multi-modality, sample size | Quartiles, outliers (visible only by inspection) |
| Compactness | Very compact | More space-hungry |

### 5.2 Boxplot vs Violin Plot
Violin plots show summary statistics **and** the full kernel density curve. Better for bimodal or otherwise non-standard distributions where a boxplot would hide the structure. Boxplots are simpler and more familiar to non-technical audiences.

### 5.3 Boxplot vs Strip / Swarm Plot
A **strip plot** shows every individual data point. A **swarm plot** does the same but jitters them so they don't overlap. These show all the data but become unreadable past a few hundred points. Useful in small datasets when you want to see every observation.

### 5.4 Boxplot + Strip Overlay
Boxplots overlaid with strip plots (`sns.boxplot` + `sns.stripplot`) combine the structural clarity of the box with the per-observation transparency of the points. The best of both worlds for medium-sized data.

---

## 6. The Titanic Dataset

### 6.1 Variables Relevant Here

| Variable | Type | Description |
|---|---|---|
| `age` | Numeric | Passenger age in years |
| `sex` | Nominal | male / female |
| `survived` | Binary | 0 = died, 1 = survived |

### 6.2 Notable Facts
- Median age was 28; most passengers were in their 20s and 30s.
- 19% of male passengers survived vs 74% of female passengers.
- Children under ~10 survived at high rates regardless of sex.
- Some `age` values are missing (about 20%).

---

## 7. Key Insights from the Boxplot

A well-constructed grouped boxplot of age × sex × survival reveals patterns that match the historical reality of the disaster:

- **"Women and children first."** Women of all ages had high survival rates; among men, children survived noticeably more than adults.
- **Class correlated with age.** Younger passengers were more likely in second/third class; this hides in a simple age plot but resolves with class as another grouping.
- **The age distribution of female non-survivors is shifted slightly toward older ages.** This is consistent with elderly women being slower to evacuate.
- **The IQR for men is roughly similar across survival statuses,** but the median is younger for survivors — survival favored the young.

---

## 8. Best Practices for Boxplots

### 8.1 Order Categories Meaningfully
Default ordering is often alphabetical or insertion order. Reorder by domain (Pclass = 1, 2, 3) or by value (ascending/descending median) to make patterns easier to read.

### 8.2 Include Sample Size
Add the count of observations per category in the label or annotation. A box with 5 observations and a box with 5000 look identical, but the 5-observation box is far less reliable.

### 8.3 Use Consistent Color
Use colors meaningfully — e.g., red for "died", green for "survived" — not for decoration. Pick colorblind-friendly palettes.

### 8.4 Annotate Outliers When Few
If only a handful of outliers exist, label them with their actual values. If there are many, leave them as dots and don't try to label them all.

### 8.5 Combine with Other Views
A boxplot alone hides modality; a histogram alone hides the IQR. Show both side by side, or use a violin plot, when distribution shape matters.

---

## 9. Viva Questions (40)

### A. The Boxplot

**Q1. What is a boxplot?**
A compact graphical summary of a numeric distribution, showing five statistics — minimum (whisker end), Q1 (box bottom), median (middle line), Q3 (box top), maximum (whisker end) — plus outliers as individual dots beyond the whiskers. Designed by John Tukey in the 1970s as a robust alternative to mean-and-std summaries.

**Q2. Who invented the boxplot?**
**John Tukey**, in his 1977 book *Exploratory Data Analysis*. Tukey is one of the founding figures of modern statistics; the boxplot, the FFT (with Cooley), and the term "bit" all trace back to him. He championed a style of statistics that began with looking — visualizing data — before testing.

**Q3. What is the five-number summary?**
The five statistics that summarize a distribution: minimum, Q1 (25th percentile), median (50th percentile), Q3 (75th percentile), maximum. The boxplot is a visual rendering of this summary plus outliers.

**Q4. What does the box represent?**
The middle 50% of the data — from Q1 to Q3. Its height is the **interquartile range** (IQR). The line inside the box is the median.

**Q5. What do the whiskers represent?**
The whiskers extend from the box to the most extreme observations within `1.5 × IQR` from Q1 and Q3. Specifically: lower whisker = smallest value ≥ `Q1 − 1.5·IQR`; upper whisker = largest value ≤ `Q3 + 1.5·IQR`. Anything beyond is an outlier.

**Q6. Why are outliers shown as dots?**
To draw attention to them — they would distort the box if included in the main shape, and they often deserve individual scrutiny (real anomaly? data error? the most interesting points in the dataset?). Tukey's design ensures outliers are visible without dominating the plot.

**Q7. What is the IQR?**
**Interquartile Range** — Q3 minus Q1, the spread of the middle 50% of the data. Robust to outliers because it ignores the tails. The IQR is the foundation of the boxplot's outlier rule.

**Q8. Why use 1.5·IQR for the whiskers?**
The 1.5 multiplier was chosen by Tukey so that, for a normal distribution, only about 0.7% of values fall beyond the whiskers — making "beyond the whiskers" a reasonable threshold for "unusual". For tighter outlier detection some use 3·IQR.

### B. Reading a Boxplot

**Q9. How do you tell if a distribution is right-skewed from a boxplot?**
- Median pushed toward the bottom of the box.
- Upper whisker much longer than the lower whisker.
- More outliers on the upper side than the lower.
All three together strongly indicate right-skew.

**Q10. How do you tell if a distribution is left-skewed?**
- Median pushed toward the top of the box.
- Lower whisker much longer than the upper.
- More outliers on the lower side.

**Q11. How do you read variability from a boxplot?**
The height of the box (IQR) tells you the spread of the central 50%. A tall box means values are spread out; a short box means they're tightly clustered. For comparison, the whisker spans show the broader range, but the box is the most reliable.

**Q12. What does a very small box mean?**
The middle 50% of the data is tightly clustered — low variability around the median. If accompanied by long whiskers and many outliers, you have a tight central cluster with a long tail.

**Q13. What does a long whisker on one side mean?**
A skew in that direction. Long upper whisker = right-skewed; long lower whisker = left-skewed.

**Q14. Can you read sample size from a boxplot?**
Generally **no** — boxplots don't show count by default. A box drawn from 10 points and one drawn from 10 000 look identical. Always annotate counts when comparing groups, or pair with a violin plot showing density.

### C. Grouped Boxplots

**Q15. What is a grouped boxplot?**
A boxplot where multiple boxes are drawn side by side, one per level of a categorical variable. Optionally further split by a secondary category (the `hue` parameter in seaborn). Excellent for comparing distributions across groups in one chart.

**Q16. Why are grouped boxplots particularly informative?**
They align distributions on a common scale, making direct visual comparison effortless. They surface group differences and interactions — patterns that would be invisible in a global summary. They're the standard tool for the question "does this numeric variable differ across groups?".

**Q17. What is the `hue` parameter in seaborn?**
A second categorical variable used to color sub-groups within each main category. For Titanic, `x=sex, y=age, hue=survived` produces two boxes per sex — one for survivors, one for non-survivors — making the survival × age × sex relationship visible in a single chart.

**Q18. How would you compare the age distribution of survivors and non-survivors by sex?**
Use a grouped boxplot: x = sex, y = age, hue = survived. The plot shows four boxes — male/non-survivor, male/survivor, female/non-survivor, female/survivor. By comparing positions and shapes you immediately see how age, sex, and survival interact.

### D. Titanic Insights

**Q19. What does the Titanic age × sex × survival boxplot reveal?**
- Among **men**, survivors are noticeably younger than non-survivors — children survived more than adult men.
- Among **women**, age has weaker association with survival — women of all ages survived at high rates.
- The **female age distribution** is broader than the male distribution.
- The "women and children first" policy is visible in the plot.

**Q20. Why do men show a stronger age-survival relationship than women?**
Because the rescue priority was women and children. Among women, sex alone was enough to grant access to lifeboats regardless of age. Among men, age was the deciding factor — children were prioritized, adult men weren't. So among men, survival selects for youth; among women, age matters less.

**Q21. What is "missing not at random" in the Titanic context?**
About 20% of `age` values are missing. The pattern correlates with class — third-class passengers had less complete record-keeping. So the missingness depends on a variable (class) that itself relates to survival. This makes naïve mean imputation slightly biased; better to impute using both sex and class.

**Q22. What's the survival rate breakdown by sex?**
Roughly 74% of female passengers and 19% of male passengers survived. Sex is the single strongest predictor of survival — far stronger than age, fare, or class on its own.

### E. Boxplots vs Other Plots

**Q23. When would you prefer a boxplot over a histogram?**
When comparing distributions across multiple groups. Boxplots align cleanly side-by-side; histograms become hard to compare when stacked. Use a histogram for examining one distribution in detail; use a boxplot for comparing many.

**Q24. When would you prefer a violin plot over a boxplot?**
When the distribution is bimodal or otherwise non-standard. Boxplots can hide bimodality completely — two peaks of equal height and a single peak produce identical boxplots. Violin plots show the full density curve and reveal modality.

**Q25. When would you use a strip plot or swarm plot?**
For small to medium datasets where you want to see every individual observation. Strip plots show every point with horizontal jitter; swarm plots arrange points to avoid overlap. Combined with a boxplot overlay, they show structure and individual data.

**Q26. What's the difference between a boxplot and an error bar chart?**
Error bars show mean ± std (or ± SEM, ± confidence interval). Boxplots show median + IQR + outliers. Error bars assume normality; boxplots make no assumption. For non-normal data, boxplots are far more honest.

### F. Outliers

**Q27. What is an outlier in the boxplot context?**
A value beyond `Q1 − 1.5·IQR` or `Q3 + 1.5·IQR`. These are values unusually far from the rest of the data and are drawn as individual dots beyond the whiskers.

**Q28. Are outliers always bad?**
No. Outliers may be:
- **Errors** — measurement mistakes, data-entry typos. Should be corrected or removed.
- **Genuine extreme values** — a billionaire in an income column, a record-breaking athlete. Real and important.
- **The signal** — fraud detection, anomaly detection, rare-disease prediction. The whole goal is to find these.
Always investigate before deleting.

**Q29. Are outlier rules sensitive to sample size?**
Yes. With very small samples (n < 30), the IQR is unstable and the outlier rule may flag normal variation. With very large samples (n > 100 000), the rule will flag many "normal" extreme values just because there are more of them. Adjust the multiplier or use a different method (z-score, MAD-based) for very large or very small data.

**Q30. How would you treat outliers detected by a boxplot?**
- **Investigate first** — are they errors or real?
- **Cap** (winsorize) at the IQR bounds if real but extreme.
- **Transform** the variable (log, sqrt) to compress tails.
- **Remove** if confirmed errors.
- **Keep** if they're the signal you care about.

### G. Practical & Code-Specific

**Q31. How do you make a basic boxplot in seaborn?**
`sns.boxplot(x='category', y='value', data=df)`. For grouped: `sns.boxplot(x='cat1', y='value', hue='cat2', data=df)`. seaborn handles axes, colors, and labels automatically with sensible defaults.

**Q32. How do you order the boxes in a meaningful way?**
Pass `order=['low', 'medium', 'high']` to `sns.boxplot` to enforce a specific order. Without it, boxes appear in alphabetical or insertion order, which often isn't the most informative.

**Q33. How do you customize the colors of a boxplot?**
Use `palette` for predefined palettes (`Set1`, `Set2`, `viridis`, `RdBu`) or pass a list of colors. For categorical data use Set palettes; for ordered data use sequential; for diverging data use diverging.

**Q34. How do you reduce the alpha of outliers to de-emphasize them?**
`sns.boxplot(... fliersize=2)` reduces outlier marker size. Or `flierprops=dict(alpha=0.3)` for transparency. For dense data with many outliers this reduces visual clutter.

**Q35. How do you overlay a strip plot on a boxplot?**
```python
sns.boxplot(x='cat', y='val', data=df)
sns.stripplot(x='cat', y='val', data=df, color='black', alpha=0.4, jitter=True)
```
Best for medium-sized data — shows the box structure plus every data point.

**Q36. What's the difference between `sns.boxplot` and `df.boxplot()`?**
seaborn's `boxplot` produces nicer-looking, statistically-aware plots with built-in support for grouping by hue. pandas's `df.boxplot()` is bare-bones matplotlib — works but less polished.

### H. Conceptual

**Q37. What does it mean for a metric to be "robust"?**
Resistant to outliers and distributional assumptions. The median is robust; the mean is not. The IQR is robust; the standard deviation is not. Robust metrics give similar answers whether or not extreme values are present, which makes them safer for real-world data.

**Q38. Why is the median often preferred over the mean for skewed data?**
Because the mean is pulled by extreme values. In the Titanic fare distribution, a few first-class passengers paid £500+; the mean fare is around £32, but the median is around £14 — far closer to the typical experience. The median is the better summary of "what most passengers paid".

**Q39. What is Simpson's Paradox and could it appear in Titanic analysis?**
A phenomenon where a trend reverses when groups are combined or split. Yes — for instance, looking at fare and survival overall might show a positive correlation, but within each class, the correlation could disappear or even invert. Always look at the data both pooled and stratified by relevant categories.

**Q40. What's the goal of EDA before modeling?**
To understand the data well enough to make good modeling decisions: which features are likely to matter, what distribution shapes need transforming, where outliers are, what missing-value strategies fit, what target classes look like, and which interactions to consider. Skipping EDA is the most common cause of wasted modeling effort — you end up training on data you don't actually understand.
