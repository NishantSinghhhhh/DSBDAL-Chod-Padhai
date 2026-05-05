# Assignment 16 — Data Visualization I: Titanic Patterns + Fare Histogram

Notebook: `Assignment_16_Complete.ipynb`
Dataset: `titanic (1).csv`
Focus: exploratory data visualization on the Titanic dataset, with a deep dive into the histogram and how to interpret distribution shapes.

---

## 1. What Is Data Visualization?

**Data Visualization** is the practice of representing data graphically — through charts, plots, and graphs — to reveal patterns, trends, relationships, and outliers that are often impossible to see in raw numbers. It's the bridge between raw data and human understanding, exploiting the fact that the human visual system is by far the most powerful pattern-recognition machine ever built. We can scan thousands of points on a scatter plot in seconds; reading the same numbers in a table would take hours.

### 1.1 Where Visualization Fits in the Workflow
Visualization is a core part of **Exploratory Data Analysis (EDA)** — the loose, iterative process of getting acquainted with a dataset before formal modeling. Done early, it surfaces:

- The shape of distributions (skew, modality, outliers).
- Correlations and dependencies between variables.
- Missing-value patterns.
- Group differences (do men and women differ on this metric?).
- Time-based trends and seasonality.

Done late (in a final report or dashboard), visualization communicates findings to non-technical audiences who can't read code or statistics.

### 1.2 Anscombe's Quartet — Why Summary Statistics Aren't Enough
Anscombe's quartet is four datasets with **identical** means, variances, and correlation coefficients but completely different visual shapes. One is linear, one is curved, one has an outlier, one is a vertical line plus an outlier. The lesson: summary statistics can hide everything that matters about your data — always plot before you analyse.

---

## 2. The Titanic Dataset

891 passengers on the RMS Titanic's maiden voyage in 1912. The dataset is classic for two reasons: it's small enough to inspect by eye, and it has rich variety — numeric, categorical, ordinal, missing values, and a clear binary target (survival).

### 2.1 Variables

| Variable | Type | Description |
|---|---|---|
| PassengerId | Integer | Unique ID |
| Survived | Binary | 0 = No, 1 = Yes (target) |
| Pclass | Ordinal | Ticket class (1, 2, 3) |
| Name | Text | Full name |
| Sex | Nominal | male / female |
| Age | Numeric | In years |
| SibSp | Integer | Number of siblings/spouses aboard |
| Parch | Integer | Number of parents/children aboard |
| Ticket | Text | Ticket number |
| Fare | Numeric | British pounds, continuous |
| Cabin | Text | Cabin number (mostly missing) |
| Embarked | Nominal | Port — C / Q / S |

### 2.2 Patterns Visualization Reveals
- **Sex** is the single biggest predictor of survival ("women and children first").
- **Class** matters: first-class passengers survived at much higher rates than third-class.
- **Age** matters but non-linearly: very young children survived more; the elderly didn't.
- **Fare** is right-skewed: most paid little, a few paid extreme amounts.
- **Cabin** is mostly missing — the missingness itself is informative (lower-class passengers didn't have a recorded cabin).

---

## 3. The Histogram in Depth

### 3.1 What It Is
A histogram divides the range of a numeric variable into equal-width **bins** and shows the count (or frequency) of values in each bin as bars. Bars touch each other because the underlying scale is continuous — gaps would imply discrete values.

### 3.2 What It Reveals

- **Center** — where the data clusters (peak of the histogram).
- **Spread** — how wide the values range (width of the distribution).
- **Shape** — symmetric, skewed, bimodal, uniform.
- **Outliers** — bars far from the rest, or single tall isolated bars.
- **Modality** — the number of peaks (modes).

### 3.3 Bin Count Selection
The number of bins matters more than people realize. Too few and you hide structure; too many and you get a noisy histogram of one-or-two-point bars. Common heuristics:

| Rule | Formula | When |
|---|---|---|
| Square root | `√n` | Quick default |
| Sturges | `1 + log₂(n)` | Small to medium n |
| Rice | `2·n^(1/3)` | Larger samples |
| Freedman-Diaconis | `bin_width = 2·IQR / n^(1/3)` | Robust to skew |
| Scott | `bin_width = 3.5·σ / n^(1/3)` | Roughly normal data |

In practice you experiment — try 10, 20, 50 bins — and pick the one that tells the cleanest story.

### 3.4 Histogram vs Bar Chart

| Feature | Histogram | Bar Chart |
|---|---|---|
| Data type | Continuous numeric | Categorical |
| Bars | Touch (continuous scale) | Have gaps (discrete categories) |
| Order | Imposed by the numeric scale | Arbitrary or by frequency |
| Bin width | Choosable | N/A |

### 3.5 Histogram with Density (KDE)
Adding a **kernel density estimate** smooths the bin edges into a continuous curve. The KDE is robust to bin-boundary artifacts and shows the underlying density more clearly. seaborn's `histplot(kde=True)` overlays both.

---

## 4. Distribution Shapes

### 4.1 Symmetric / Normal
Bell curve. Mean ≈ median ≈ mode. Central in classical statistics; arises whenever many independent random factors combine (Central Limit Theorem). Examples: heights, IQ scores, measurement errors.

### 4.2 Right-Skewed (Positive Skew)
Long tail on the right. Mean > median. Common when values are bounded below (often by 0) but unbounded above. Examples: income, fares, response times, web visit duration. **The Titanic fare is famously right-skewed** — a few first-class passengers paid extreme fares.

### 4.3 Left-Skewed (Negative Skew)
Long tail on the left. Mean < median. Common when values are bounded above. Examples: test scores when most students score near the top (ceiling effect), retirement ages.

### 4.4 Bimodal
Two distinct peaks. Often indicates a mixture of two underlying populations (e.g., heights of adults — bimodal because of male/female mix). Always investigate; a single summary statistic on bimodal data is meaningless.

### 4.5 Uniform
Roughly flat — every value equally likely. Random IDs, dice rolls in theory, certain hash distributions.

### 4.6 Heavy-Tailed
Long, fat tails — extreme values occur much more often than a normal distribution would suggest. Financial returns, network latencies, earthquake magnitudes. Standard deviation is misleading; use percentiles.

---

## 5. Other EDA Plots

### 5.1 Boxplot
Shows the five-number summary (min, Q1, median, Q3, max) plus outliers. Excellent for **comparing distributions across groups** (e.g., fare by passenger class).

### 5.2 Violin Plot
Combines a boxplot with a kernel density estimate. Shows distribution shape *and* summary statistics. Useful when distributions are bimodal.

### 5.3 Scatter Plot
Two numeric variables plotted against each other. Reveals correlation, curvature, clustering, outliers. Workhorse of bivariate EDA.

### 5.4 Pair Plot
A grid of scatter plots for every pair of features, with diagonal histograms or KDEs. Excellent for spotting relationships across many features at once.

### 5.5 Heatmap
A matrix of color-coded values. Most commonly used to visualize a correlation matrix — red for positive correlations, blue for negative, color intensity scaled by magnitude.

### 5.6 Bar Chart
Counts or means per category. The standard for comparing summary statistics across discrete groups.

### 5.7 Line Chart
Continuous data over an ordered axis (typically time). Shows trends, seasonality, and change.

### 5.8 Pie Chart
Parts of a whole. Generally avoided in serious analysis — humans are bad at comparing angle/area, and bar charts convey the same info more accurately. Use sparingly, only with very few slices.

---

## 6. seaborn vs matplotlib

**matplotlib** is the foundational Python plotting library — low-level, verbose, but extremely flexible. Every other Python plotting library is built on top of it.

**seaborn** sits on top of matplotlib. It provides:

- Higher-level API for statistical plots (boxplot, violin, pair, joint).
- Better default styling (cleaner colors, fonts, gridlines).
- Direct support for pandas DataFrames (`x='col1', y='col2', data=df`).
- Built-in datasets including Titanic.

In practice, you use them together: seaborn for the plot, matplotlib for fine-tuning (titles, axis limits, annotations).

---

## 7. Principles of Good Visualization

### 7.1 Tufte's Principles
Edward Tufte, the godfather of data visualization, argued for:

- **Maximize data-ink ratio** — minimize ink that doesn't convey data.
- **Avoid chartjunk** — gradients, 3D effects, drop shadows distract without informing.
- **Show the data clearly** — let the data speak.

### 7.2 Common Mistakes

- **Truncated y-axis** that exaggerates differences.
- **Pie charts with too many slices**.
- **3D bar charts** — angles distort perception.
- **Inconsistent color scales**.
- **Overplotting** — too many points hide structure; use transparency or 2D density.

### 7.3 Color
- Use **categorical** palettes for nominal data (Set1, Set2, Set3 in seaborn).
- Use **sequential** palettes for ordered data (Blues, Greens, viridis).
- Use **diverging** palettes for data with a meaningful midpoint (RdBu, coolwarm).
- Avoid red/green for the colorblind — about 8% of men can't distinguish them.

---

## 8. Viva Questions (40)

### A. Visualization Fundamentals

**Q1. What is data visualization and why is it important?**
The graphical representation of data, designed to make patterns, trends, and outliers visible to the human eye. It's important because the visual cortex is the most powerful pattern-recognition system humans have — we can scan thousands of points on a scatter plot in seconds, but reading the same numbers from a table takes hours and yields little intuition. Visualization makes hidden structure obvious.

**Q2. What is Exploratory Data Analysis (EDA)?**
The iterative process of getting to know a dataset before formal modeling. Coined by John Tukey, it emphasizes plots, summary statistics, and looking — not testing hypotheses. EDA surfaces data-quality issues, suggests features, and informs modeling decisions. It's the most under-rated step in any data project.

**Q3. What is Anscombe's quartet and why is it important?**
Four datasets with identical means, variances, and correlation coefficients but completely different visual shapes — one linear, one curved, one with an outlier, one with two outliers. It demonstrates that summary statistics alone can deeply mislead, and that you must always plot your data before drawing conclusions.

**Q4. What's the difference between exploratory and explanatory visualization?**
- **Exploratory** — for the analyst, to find patterns. Quick, ugly, many plots, full of detail.
- **Explanatory** — for an audience, to communicate findings. Polished, simplified, often one chart that tells a single story.
The two have very different design priorities.

### B. Histograms

**Q5. What is a histogram?**
A chart that splits a numeric variable into equal-width bins and shows the count of values in each bin as bars. Bars touch each other because the underlying numeric scale is continuous. The histogram reveals the center, spread, shape, and outliers of a distribution.

**Q6. What's the difference between a histogram and a bar chart?**
- **Histogram** — for continuous numeric data; bars touch; bin width is chosen.
- **Bar chart** — for categorical data; bars have gaps; one bar per category.
The visual difference (gaps vs no gaps) signals whether the underlying scale is continuous or discrete.

**Q7. How do you choose the right number of bins?**
Common heuristics: square root (√n), Sturges (1 + log₂ n), Rice (2·n^(1/3)), Freedman-Diaconis (uses IQR), Scott (uses std). In practice you try a few and pick the cleanest. Too few hides structure; too many becomes noisy.

**Q8. What is a kernel density estimate (KDE)?**
A smoothing technique that places a small bell curve at each data point and sums them, producing a smooth density curve. KDE avoids the bin-boundary artifacts of histograms and shows the underlying distribution more clearly. Often overlaid on a histogram (`sns.histplot(kde=True)`).

**Q9. How do you read distribution shape from a histogram?**
- One peak, symmetric → roughly normal.
- One peak, long right tail → right-skewed.
- One peak, long left tail → left-skewed.
- Two peaks → bimodal (likely mixture of two populations).
- Roughly flat → uniform.
- Long heavy tails → heavy-tailed (extreme values are common).

### C. Distribution Shapes

**Q10. What is a right-skewed distribution? Give a Titanic example.**
A distribution with a long thin tail on the right; the mean is greater than the median because the tail pulls the mean upward. The **Titanic fare distribution** is the textbook example: most passengers paid modest fares, but a small number of first-class passengers paid extreme amounts (Cardeza family paid £512 — the maximum). Median fare is around £14; mean is around £32.

**Q11. What is a left-skewed distribution?**
A distribution with a long tail on the left. Mean < median. Often arises from ceiling effects — when many values cluster near the upper bound. Example: an easy exam where most students score 90+ but a few do badly.

**Q12. What is a bimodal distribution and what does it imply?**
A distribution with two peaks. It typically signals a **mixture of two populations** — e.g., the height distribution of adults is bimodal because of the male/female mix. When you see bimodality, look for the underlying grouping variable; analyzing the two groups separately is usually more informative than a single global summary.

**Q13. How do mean and median relate in skewed distributions?**
- Right-skewed: mean > median.
- Left-skewed: mean < median.
- Symmetric: mean ≈ median.
The relationship is so reliable that comparing mean vs median is a quick skewness check.

**Q14. What does it mean for a distribution to be "heavy-tailed"?**
Extreme values are much more probable than a normal distribution would predict. Examples: financial returns, network latencies, file sizes, earthquake magnitudes. Standard deviation is misleading on heavy-tailed data; report percentiles (p50, p99) instead.

### D. Other EDA Plots

**Q15. What is a boxplot?**
A compact summary of a distribution showing five numbers — min, Q1, median, Q3, max — plus outliers as dots. The box spans Q1–Q3 (the middle 50%), the line is the median, the whiskers extend 1.5·IQR. Excellent for comparing distributions across groups.

**Q16. What is the IQR?**
**Interquartile Range** — Q3 minus Q1, the spread of the middle 50% of the data. Robust to outliers because it ignores the tails. The IQR is the basis of the standard outlier rule (`Q1 − 1.5·IQR`, `Q3 + 1.5·IQR`).

**Q17. What does the position of the median in a box tell you?**
- Centered → roughly symmetric.
- Pushed toward Q1 (the bottom of the box) → right-skewed.
- Pushed toward Q3 (the top) → left-skewed.

**Q18. What is a violin plot?**
A combination of a boxplot and a KDE. It shows summary statistics (median, quartiles) plus the full distribution shape. Useful when distributions are bimodal — a boxplot would hide that, but a violin plot makes it obvious.

**Q19. What is a scatter plot?**
A plot of two numeric variables against each other, one point per observation. Reveals correlation, curvature, clustering, and outliers. The standard tool for examining relationships between two continuous variables.

**Q20. What is a pair plot?**
A grid of scatter plots showing every pair of numeric features, with histograms or KDEs on the diagonal. seaborn's `pairplot()` produces it in one line. Excellent for spotting relationships across many features at once during EDA.

**Q21. What is a heatmap?**
A matrix of colored cells where each color encodes a numeric value. Most commonly used to visualize a correlation matrix — red for positive correlations, blue for negative, intensity scaled by magnitude. Lets you spot patterns in dozens of pairwise relationships at a glance.

**Q22. What is a bar chart used for?**
Comparing a numeric summary (count, mean, median) across categories. Each bar is one category; height is the value. Standard for descriptive comparisons across groups.

**Q23. What is a line chart used for?**
Continuous data over an ordered axis, typically time. Shows trends, seasonality, and change. Default for time-series data.

**Q24. Why are pie charts often discouraged?**
Humans are bad at comparing angles and areas accurately. Bar charts encode the same information using length, which we can compare precisely. Pie charts are acceptable only with very few slices and when proportions of a whole are the primary message.

### E. Titanic-Specific Insights

**Q25. What patterns does Titanic visualization reveal?**
- Women survived at much higher rates than men ("women and children first").
- First-class passengers survived more than second; second more than third.
- Children survived more than adults; the elderly survived less.
- Embarkation port matters slightly (Cherbourg passengers survived more — many were upper-class).
- Cabin is mostly missing for third-class passengers — the missingness itself signals the group.

**Q26. Why is the Titanic fare distribution right-skewed?**
Because most passengers were in third class with cheap tickets, but a small number of first-class passengers paid extreme amounts. The fare structure was extremely unequal — the median is around £14, the maximum is £512.

**Q27. What's interesting about the Cabin column on Titanic?**
77% of cabin values are missing. This wasn't random — third-class passengers didn't have recorded cabin numbers. So the missingness itself is a feature: "Cabin missing" essentially means "third class". This is a beautiful real-world example of MNAR (missing not at random) being informative.

**Q28. What's the survival rate by sex on Titanic?**
Roughly 74% of women survived vs 19% of men. Sex is the single strongest predictor of survival in the dataset, dwarfing age, class, and fare on its own.

### F. seaborn and matplotlib

**Q29. What's the difference between matplotlib and seaborn?**
**matplotlib** is the foundational, low-level plotting library; you control every element manually, which is verbose but flexible. **seaborn** sits on top and provides high-level statistical plots (boxplot, violin, pair, joint, heatmap) with better default styling and direct pandas DataFrame support. In practice you use them together.

**Q30. How do you save a plot to a file?**
`plt.savefig('out.png', dpi=300, bbox_inches='tight')`. Common formats: PNG (raster), SVG/PDF (vector). Use vectors for publication; PNG for web/email. Always set `dpi` for raster output (300 is publication quality).

**Q31. What is the difference between `plt.figure()` and `plt.subplots()`?**
`plt.figure()` creates a single figure; `plt.subplots(rows, cols)` creates a figure with a grid of axes and returns them. Use subplots whenever you want multiple plots side by side, with explicit access to each axis.

**Q32. Why use `plt.tight_layout()`?**
To automatically adjust subplot spacing so labels don't overlap. Without it, axis labels and titles can collide between subplots, especially in dense grids.

### G. Principles of Good Visualization

**Q33. Who is Edward Tufte and what are his principles?**
Edward Tufte is the godfather of modern data visualization. His core principles: **maximize the data-ink ratio** (most ink should encode data, not decoration), **avoid chartjunk** (no 3D, no drop shadows, no gradients without purpose), **show the data clearly**.

**Q34. What is "chartjunk"?**
Visual elements that don't carry information: 3D bars, drop shadows, ornamental gradients, decorative icons, gridlines that aren't needed. They distract from the data and can mislead. Tufte's classic critique was of corporate charts buried in chartjunk.

**Q35. What are common mistakes in visualization?**
- Truncated y-axis that visually exaggerates differences.
- Pie charts with too many slices.
- 3D effects on simple data.
- Overplotting that hides density.
- Inconsistent or perceptually misleading color scales.
- Encoding too many dimensions in one plot.

**Q36. What color palette would you use for a sequential variable like temperature?**
A **sequential** palette — viridis, magma, Blues, or Greens. They go from light to dark in a single hue family, making higher values clearly distinguishable. Avoid rainbow palettes (perceptually nonlinear) and red/green together (problematic for colorblindness).

**Q37. What about a diverging variable like z-score?**
A **diverging** palette — RdBu, coolwarm. Two hues meeting at a neutral midpoint. Lets the viewer see both direction (above or below the midpoint) and magnitude.

### H. Practical & Code-Specific

**Q38. How do you visualize categorical data alongside numeric data?**
Use a grouped boxplot or violin plot — one box per category — to compare numeric distributions across groups. Or a bar chart of summary statistics (mean, median) per category. seaborn's `boxplot(x=cat, y=num, data=df)` is the standard.

**Q39. How would you handle overplotting in a scatter plot of 100 000 points?**
- Set `alpha` (transparency) so dense regions appear darker.
- Use a **2D density plot** (`sns.kdeplot(x, y)`) instead.
- Use a **hexbin plot** that bins points spatially.
- Sample randomly to a manageable size.
- Use a small marker size.

**Q40. What's your go-to EDA workflow for a new dataset?**
1. `df.shape`, `df.head()`, `df.info()` — structural check.
2. `df.isnull().sum()` and a missing-value heatmap — quality check.
3. `df.describe(include='all')` — summary statistics.
4. Histogram of every numeric column — distribution shapes.
5. Boxplot of every numeric column — outliers.
6. Bar chart of every categorical column — class counts.
7. Correlation heatmap — relationships among numeric features.
8. Pair plot or pair plots of selected features — bivariate relationships.
9. Group-by-target plots — does anything relate to the target?
This 9-step routine surfaces the vast majority of data-quality issues and analytic opportunities.
