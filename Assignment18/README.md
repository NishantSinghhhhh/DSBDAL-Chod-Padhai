# Assignment 18 — Data Visualization III: Iris Feature Distributions

Notebook: `Assignment_18_Complete.ipynb`
Dataset: `iris.csv`
Focus: visualizing distributions of multiple features across multiple classes — histograms, boxplots, KDE plots, pair plots, and what each reveals about the underlying data.

---

## 1. The Iris Dataset

### 1.1 Background
The **Iris dataset** is the most famous dataset in statistics and machine learning. Introduced by **Ronald A. Fisher** in his 1936 paper *The Use of Multiple Measurements in Taxonomic Problems*, it has been the standard teaching example for over 80 years. Its enduring popularity comes from being:

- **Tiny** — 150 samples, processable in any tool.
- **Balanced** — 50 examples per class.
- **Clean** — no missing values, no inconsistencies.
- **Multi-class** — 3 species, slightly more interesting than binary problems.
- **Geometric** — features are physical measurements with intuitive meaning.

### 1.2 Variables

| Feature | Type | Range (cm) |
|---|---|---|
| sepal length | Continuous numeric | ~4–8 |
| sepal width | Continuous numeric | ~2–4.5 |
| petal length | Continuous numeric | ~1–7 |
| petal width | Continuous numeric | ~0.1–2.5 |
| species | Categorical (3 classes) | setosa / versicolor / virginica |

### 1.3 Why Iris Is Used So Often
- It demonstrates **multi-class classification** — three classes is more general than binary.
- Petal-based features cleanly separate **setosa** from the others; **versicolor** and **virginica** overlap somewhat. So the dataset has both an "easy" and a "hard" sub-problem.
- Visualizations on Iris are uncommonly clear — pairs of features form clean clusters that classical algorithms can separate visibly.

---

## 2. Visualizing Distributions Per Class

When data has natural groupings (here, species), visualizing the distribution of each feature **per group** is far more informative than a global summary. The key insight: the global histogram of "petal length" is roughly bimodal, but it splits cleanly into three unimodal distributions when stratified by species.

### 2.1 Per-Class Histograms
Stacked or overlaid histograms — one color per class — show how each class's values distribute. Useful for spotting:

- Class separability (do the histograms overlap much?).
- Class-specific spread (is one species more variable than another?).
- Outliers within a class.
- Skew within a class.

For Iris:
- **Petal length** is the most discriminating feature — setosa has tiny petals, virginica has large, versicolor sits in between with little overlap.
- **Sepal width** has the most overlap across classes — least useful for classification.

### 2.2 Per-Class KDE
Smooth density curves overlaid by class. Often clearer than histograms because:

- No bin-boundary artifacts.
- Easier to compare shapes.
- Can be filled with transparency to see overlap.

`sns.kdeplot(x='petal_length', hue='species', data=df, fill=True, alpha=0.4)`.

### 2.3 Per-Class Boxplot
Side-by-side boxplots, one per class. Compact and excellent for comparing:

- Medians.
- Spread (IQR).
- Outliers.
- Skew (median position in box).

For Iris, the box for petal length is highest for virginica, lowest for setosa — and they barely overlap, indicating a clean split.

### 2.4 Per-Class Violin Plot
Combines KDE and boxplot. Best of both: shows full distribution shape and summary statistics. Useful when you suspect bimodality within a group.

---

## 3. Multivariate Visualizations

The three plots above visualize one feature at a time. For multivariate exploration:

### 3.1 Pair Plot
A grid of scatter plots showing every pair of features, with histograms or KDEs on the diagonal. seaborn produces it in one call: `sns.pairplot(df, hue='species')`. Excellent first look at multi-feature data — spots clusters, correlations, and class separability across all dimensions at once.

For Iris, a pair plot reveals:
- **petal_length vs petal_width** is the cleanest separator — setosa is far from the others, versicolor and virginica form distinct clusters with light overlap.
- **sepal_length vs sepal_width** is much messier; classes are jumbled.
- The diagonal histograms (or KDEs) show within-feature distribution shapes per class.

### 3.2 Scatter Plot with Color
A single scatter plot of two features, with points colored by class. Cleaner than a pair plot when you've already identified the most discriminating pair.

### 3.3 Heatmap of Correlations
`sns.heatmap(df.corr(), annot=True, cmap='coolwarm')`. Shows pairwise linear correlations among numeric features. For Iris, petal length and petal width have the strongest correlation (~0.96).

### 3.4 Joint Plot
A scatter plot of two variables with marginal distributions on the top and right axes. seaborn's `sns.jointplot()` does this in one call. Combines bivariate and univariate views.

---

## 4. Reading Class Separability

A central question in supervised classification: how separable are the classes in the feature space?

### 4.1 Visually Separable
Classes form distinct clusters in feature space. A classifier should achieve very high accuracy. **Setosa vs others** in Iris is the textbook visually-separable case.

### 4.2 Linearly Separable
A straight line (or hyperplane in high-D) can perfectly separate classes. Setosa is linearly separable from versicolor and virginica using petal-length alone — even a simple linear classifier achieves perfect accuracy.

### 4.3 Non-Linearly Separable
Classes can be separated only by curves or complex boundaries. **Versicolor vs virginica** is mildly non-linear; the classes overlap slightly and a linear classifier makes a few errors.

### 4.4 Inseparable
The features simply don't carry enough information to distinguish classes. Add more features or expect low accuracy.

---

## 5. Distribution Shapes Per Feature

Inspecting individual feature distributions reveals modeling decisions:

### 5.1 Sepal Length
Roughly normal. Mean ~5.84, std ~0.83. No transformation needed.

### 5.2 Sepal Width
Roughly normal. Mean ~3.06, std ~0.43. No transformation needed.

### 5.3 Petal Length
**Bimodal** in the global histogram — a peak around 1.5 (setosa) and a broader peak around 5 (versicolor + virginica). When split by species, each group is unimodal. The bimodality globally is a sign that there's a strong class structure in the data.

### 5.4 Petal Width
Similar to petal length — globally bimodal, per-class unimodal.

### 5.5 Implication
The bimodality of the global petal distributions tells us, before any modeling, that petal features are the most discriminative. Sepal features, with smoothly unimodal global distributions, are less informative.

---

## 6. Plot Selection Decision Tree

| Goal | Best Plot |
|---|---|
| Examine one numeric distribution | Histogram / KDE |
| Compare a numeric distribution across categories | Grouped boxplot or violin plot |
| Examine relationship between two numeric variables | Scatter plot |
| Examine relationships across many features | Pair plot |
| Show pairwise correlations | Heatmap |
| Show counts of one categorical variable | Bar chart |
| Show data over time | Line chart |
| Highlight a few outlier observations | Boxplot or strip plot |
| Detect class separability | Pair plot with hue |

---

## 7. Color and Style for Class Visualizations

### 7.1 Categorical Palettes
For nominal class labels, use a categorical palette where colors are equally spaced and not perceptually ordered: `Set1`, `Set2`, `Paired`, `tab10`. Each class gets a distinct, easy-to-distinguish hue.

### 7.2 Consistent Class Colors
If the same class appears in multiple plots, use the **same color** every time. This builds visual consistency — "purple = setosa" should hold across the entire report.

### 7.3 Colorblind Safety
About 8% of men and 0.5% of women have some form of red-green color blindness. Use palettes designed to be colorblind-friendly: viridis (sequential), cubehelix, or seaborn's `colorblind` palette.

---

## 8. Interactivity

For exploratory work, interactive plots add a lot of value:

- **plotly** — interactive scatter, line, 3D plots. Click on a class to hide/show it.
- **bokeh** — similar interactive output, server-rendered.
- **altair** — declarative, based on Vega-Lite.
- **Jupyter widgets** — slide a parameter and see plots update.

Interactive plots are great for EDA but not for static reports/papers. For final reporting, return to static matplotlib/seaborn output.

---

## 9. The Iris Story Through Visualization

Putting all the visualizations together, the Iris dataset tells a clear story:

1. **Histograms by species** show petal features cleanly separating setosa, with versicolor and virginica overlapping slightly.
2. **Boxplots by species** confirm the medians are far apart on petal features and close on sepal features.
3. **A pair plot** reveals that the optimal 2D representation is petal length vs petal width — three distinct clusters with minimal overlap.
4. **A correlation heatmap** shows that petal length and petal width are highly correlated (each predicting the other), so a single petal feature carries most of the information.
5. **The conclusion**: a classifier trained on petal length alone achieves >95% accuracy; adding the other features pushes it past 97%.

---

## 10. Viva Questions (40)

### A. Iris Dataset

**Q1. What is the Iris dataset?**
A famous dataset of 150 iris flowers introduced by Ronald Fisher in 1936. Contains 50 samples each of three species (setosa, versicolor, virginica), with four numeric features per sample: sepal length, sepal width, petal length, and petal width — all measured in centimeters. It's the canonical teaching dataset for classification and visualization in statistics and machine learning.

**Q2. Why is the Iris dataset used so widely?**
- It's tiny (150 rows) and clean (no missing values, no errors).
- It's balanced (50 per class).
- It demonstrates multi-class problems naturally.
- One class (setosa) is linearly separable; the others are mildly non-linear — so it covers easy and hard sub-problems.
- It's geometric — feature meanings are intuitive.
- It produces beautifully clean visualizations.

**Q3. Which Iris feature is most discriminating between species?**
**Petal length** (and almost equivalently, petal width). Setosa has tiny petals (~1.5 cm), virginica has large petals (~5.5 cm), and versicolor sits in between (~4.3 cm). The classes barely overlap on this feature, making it nearly perfect for classification.

**Q4. Which Iris feature is least discriminating?**
**Sepal width**. The three species have very similar sepal widths with substantial overlap, so this feature alone provides little classification signal.

**Q5. Are the Iris classes linearly separable?**
Setosa is linearly separable from the other two. Versicolor and virginica are *mildly* non-linearly separable — they overlap slightly along the boundary, and a linear classifier makes a small number of errors. With petal length and petal width, even a simple logistic regression achieves over 95% accuracy.

### B. Distribution Visualization

**Q6. What is a histogram and what does it reveal?**
A chart that splits a numeric variable into equal-width bins and shows the count in each bin. Reveals the **center**, **spread**, **shape** (symmetric, skewed, bimodal), and **outliers** of a distribution. The first plot to make for any new numeric variable.

**Q7. What is a Kernel Density Estimate (KDE)?**
A smoothing technique that places a small bell curve at each data point and sums them, producing a continuous density curve. Avoids the bin-boundary artifacts of histograms and is often easier to compare across groups when overlaid with transparency.

**Q8. What is a violin plot and when do you prefer it?**
A combination of a boxplot and KDE — shows the full distribution shape plus median and quartiles. Prefer it over a boxplot when distributions are bimodal or otherwise non-standard, where the boxplot's summary statistics would hide the structure.

**Q9. What does a bimodal distribution suggest?**
That the data is a mixture of two populations. A bimodal histogram of petal lengths in Iris (without splitting by class) reveals the two sub-populations: setosa (small petals) and the combined versicolor/virginica (larger petals). When you see bimodality, look for the underlying grouping variable.

**Q10. How do mean and median differ in skewed data?**
- Right-skewed: mean > median.
- Left-skewed: mean < median.
- Symmetric: mean ≈ median.
The mean is pulled by extreme values; the median is robust. For skewed data, the median is a better summary of the "typical" value.

### C. Per-Class Visualization

**Q11. Why visualize distributions per class?**
A global summary often hides class-specific structure. The global histogram of petal length is bimodal, but each species individually is unimodal — a fact that's invisible in the pooled view. Per-class visualization reveals which features discriminate which classes, suggesting good predictors for classification.

**Q12. What's the easiest way to make per-class histograms in seaborn?**
`sns.histplot(data=df, x='petal_length', hue='species', kde=True)`. This produces overlaid histograms (one per species) plus KDE curves, with sensible defaults.

**Q13. What's the easiest way to make per-class boxplots in seaborn?**
`sns.boxplot(data=df, x='species', y='petal_length')`. One box per species, side by side. Excellent for comparing medians and IQRs at a glance.

**Q14. When would you use a violin plot instead of a boxplot for per-class comparison?**
When you suspect any class has a non-standard distribution shape — bimodality, heavy skew, multiple modes. Violin plots show the full density; boxplots only show summary statistics.

**Q15. How can you tell from a per-class plot whether classes are separable?**
- Histograms / KDEs: do the curves barely overlap? → very separable.
- Boxplots: do the boxes barely overlap? → very separable.
- Pair plot: do points form distinct clusters? → very separable.
For setosa vs others on petal length, the curves don't overlap at all — perfect separability.

### D. Multivariate Visualization

**Q16. What is a pair plot?**
A grid of scatter plots showing every pair of numeric features, with histograms or KDEs on the diagonal. seaborn produces it via `sns.pairplot(df, hue='species')`. The standard tool for first-look multivariate EDA — spots clusters, correlations, and class separability across all dimensions in one chart.

**Q17. What does a pair plot of Iris reveal?**
- The clearest 2D separation is **petal_length vs petal_width** — three distinct clusters with minimal overlap.
- Sepal-based pairs are messier; classes overlap substantially.
- The diagonal KDEs show that petal features are bimodal globally but unimodal per class.
- Setosa is far from the others in every plot involving a petal feature.

**Q18. What is a correlation heatmap?**
A matrix where each cell is colored by the correlation coefficient between two variables. Reds for positive, blues for negative; intensity scaled by magnitude. For Iris, petal length and petal width are very strongly correlated (~0.96), so they carry largely the same information.

**Q19. What does Pearson's correlation coefficient measure?**
The strength of a **linear** relationship between two numeric variables, ranging from −1 (perfect negative) to +1 (perfect positive), with 0 meaning no linear relationship. Note that 0 doesn't mean "unrelated" — it means "no *linear* relationship". Two variables with a strong curved relationship can have correlation near 0.

**Q20. What is Spearman correlation?**
A correlation coefficient based on **ranks** rather than raw values. Captures monotonic relationships (whether increasing in one variable implies increasing in another), even non-linear ones. Robust to outliers. Use it when the relationship is non-linear but monotonic.

### E. Plot Selection

**Q21. How do you decide which plot to use?**
- One numeric: histogram / KDE.
- One numeric, multiple groups: boxplot / violin plot.
- Two numerics: scatter plot.
- Many numerics: pair plot.
- Two numerics + groups: scatter plot with hue.
- Pairwise correlations: heatmap.
- One categorical: bar chart of counts.
- Time-series: line chart.
The choice depends on your question and the data types involved.

**Q22. What are the principles of a good plot?**
- **Maximize the data-ink ratio** (most ink should encode data).
- **Avoid chartjunk** (no 3D, no decoration).
- **Use clear titles and axis labels**.
- **Choose colors meaningfully**.
- **Show the data clearly** — let it speak.
Tufte's classic principles, still the gold standard.

**Q23. What is the difference between exploratory and explanatory plots?**
- **Exploratory** — for the analyst. Quick, ugly, many plots, full of detail. The goal is to find patterns.
- **Explanatory** — for an audience. Polished, simplified, focused on a single message. The goal is to communicate findings.

**Q24. Why are pie charts often discouraged?**
Humans are bad at comparing angles and areas. Bar charts encode the same information using length, which we compare accurately. Pie charts are tolerable only with very few slices and when proportions of a whole are the key message.

### F. Color and Style

**Q25. What types of color palettes are there?**
- **Categorical / qualitative** — distinct hues for unordered groups (Set1, Set2, tab10).
- **Sequential** — light to dark in a single hue family for ordered data (Blues, viridis).
- **Diverging** — two hues meeting at a neutral midpoint for data with a meaningful zero (RdBu, coolwarm).

**Q26. What is a colorblind-friendly palette and why does it matter?**
A palette designed so people with color blindness (about 8% of men) can still distinguish all categories. Examples: viridis, cubehelix, seaborn's `colorblind` palette. Avoid red-green pairings, which are problematic for the most common form of color blindness.

**Q27. Why use the same color for the same class across plots?**
For visual consistency across a report. If "setosa" is purple in one plot, it should be purple in every other plot. The reader builds an association; switching colors creates cognitive load and confusion.

### G. Distribution Shapes

**Q28. What is skewness?**
A measure of asymmetry. Positive (right-skewed) has a long right tail; negative (left-skewed) a long left tail; zero is symmetric. As a rule of thumb, `|skew| < 0.5` is roughly symmetric, `0.5–1` is moderate, `> 1` is highly skewed.

**Q29. What is kurtosis?**
A measure of "tailedness" — how heavy the tails are compared to a normal distribution. Leptokurtic = heavy tails (extreme values common); platykurtic = light tails (extremes rare); mesokurtic = normal-like.

**Q30. What does it mean to "transform" a feature?**
To apply a function (log, sqrt, square) that reshapes the distribution — typically to reduce skew and bring it closer to normal. Linear regression and other classical methods perform better on roughly normal features.

**Q31. When would you log-transform a feature?**
When it's right-skewed and positive — income, fares, file sizes, response times. The log compresses the long tail and pulls the distribution toward symmetry, making mean-based statistics more meaningful and improving the fit of linear models.

### H. Practical & Code-Specific

**Q32. What's the difference between matplotlib and seaborn?**
**matplotlib** is the foundational, low-level library — verbose but flexible. **seaborn** sits on top and provides high-level statistical plots with better defaults and direct pandas DataFrame support. They're often used together: seaborn for the plot, matplotlib for fine-tuning.

**Q33. How do you create a multi-panel figure?**
`fig, axes = plt.subplots(rows, cols)` returns a figure and a grid of axes. You then plot into each axis individually (`axes[0,0].scatter(...)`, etc.) and call `plt.tight_layout()` to space them properly.

**Q34. Why would you use `plt.tight_layout()`?**
To automatically adjust subplot spacing so axis labels and titles don't overlap. Without it, dense subplot grids look cluttered and labels can collide between subplots.

**Q35. How do you save a publication-quality plot?**
`plt.savefig('out.png', dpi=300, bbox_inches='tight')`. Use `dpi=300` for raster (PNG, JPG) for clear print rendering. Use SVG or PDF for vector output that scales without pixelation.

**Q36. How do you change the default seaborn style?**
`sns.set_style('whitegrid')` for a white background with light gridlines; `'darkgrid'` for darker; `'ticks'` for minimal. Set it once at the top of a notebook to apply consistently.

**Q37. How would you visualize the entire Iris dataset in one figure?**
A pair plot — `sns.pairplot(df, hue='species')`. In a single call you get a 4×4 grid of scatter plots showing all feature pairs, with KDEs on the diagonal showing per-class distributions. The most informative single chart for Iris.

**Q38. How would you handle missing values before plotting?**
- `dropna()` — exclude rows with missing values.
- Impute (mean, median, mode) — fill them in.
- Visualize the missing pattern with a heatmap of `df.isnull()`.
- For a small fraction missing, `dropna()` is usually fine for plots.

**Q39. How can you add interactivity to a plot?**
- **Plotly** — `import plotly.express as px; px.scatter(df, ...)` produces interactive HTML.
- **Bokeh** — similar, built for web embedding.
- **ipywidgets** in Jupyter — slide a parameter and see the plot update.
Interactive plots are great for EDA but not for static papers; export to a static format for final reporting.

**Q40. How do you communicate a finding to a non-technical audience using a chart?**
- Choose **one** key chart that tells the central story.
- Use a **descriptive title** that states the finding ("Female passengers survived 4× more than male").
- Annotate key values directly on the chart.
- Strip everything that doesn't reinforce the message — gridlines, decoration, secondary axes.
- Use color sparingly and meaningfully.
- Test the chart with a non-technical reader before finalizing.
The goal of an explanatory chart is to be understood in 5 seconds.
