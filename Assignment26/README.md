# Assignment 26 — Data Visualization on Death Rate / Accident Dataset

Notebook: `Assignment_26_Complete.ipynb`
Dataset: `death_rate.csv`
Focus: visualizing public-safety / accident data — line charts for time trends, bar charts for category comparisons, heatmaps for spatial-temporal patterns, and the unique ethical considerations of mortality-related visualization.

---

## 1. Why Visualize Accident Data?

Government accident statistics — road traffic deaths, occupational injuries, drowning incidents, fall-related deaths — are some of the most actionable public-safety datasets that exist. Effective visualization serves several purposes:

1. **Identify high-risk groups** — which age, gender, location, or vehicle type has elevated risk?
2. **Detect time trends** — are deaths increasing or decreasing? Are there seasonal patterns?
3. **Inform policy** — visualizing where and how accidents happen helps prioritize interventions.
4. **Communicate to the public** — clear visualizations drive awareness and behavior change.
5. **Compare across regions** — spot geographic patterns suggesting infrastructure, climate, or cultural drivers.

Done well, accident-data visualizations save lives. Done poorly — exaggerating, cherry-picking, or distorting — they can mislead policy and erode public trust.

---

## 2. Data Visualization Fundamentals

### 2.1 What Is Data Visualization?
The graphical representation of data, designed to make patterns, trends, and outliers visible to the human eye. Exploits the human visual system — by far the most powerful pattern-recognition machine we have. We can scan thousands of points on a chart in seconds; reading the same numbers from a table takes hours.

### 2.2 EDA — Exploratory Data Analysis
The iterative process of getting to know a dataset before formal modeling. Visualization is its core activity. EDA surfaces:
- Distribution shapes.
- Correlations and dependencies.
- Missing-value patterns.
- Group differences.
- Time trends and seasonality.

### 2.3 Anscombe's Quartet
Four datasets with identical mean, variance, and correlation but completely different visual shapes — a reminder that summary statistics alone can mislead. Always plot before drawing conclusions.

---

## 3. Plot Types for Accident Data

### 3.1 Line Charts — for Time Trends
Plot deaths or accidents over time. Reveals:
- **Long-term trend** (improving, worsening, flat).
- **Seasonality** (winter vs summer, weekday vs weekend).
- **Sudden changes** (after policy interventions).

Use consistent y-axis scaling — never truncate the y-axis to exaggerate change.

### 3.2 Bar Charts — for Category Comparisons
Compare death counts across:
- Age groups.
- Sex / gender.
- Cause (motor vehicle, fall, poisoning, drowning).
- State / region.
- Year.

Order categories meaningfully — by frequency or domain order, not alphabetically by default.

### 3.3 Heatmaps — for Two-Way Comparisons
A matrix of colored cells where each color encodes a value. For accident data:
- **Year × Age group** — see how each age cohort's death rate changed over time.
- **State × Cause** — find geographic patterns.
- **Hour × Day-of-week** — when do most accidents happen?

### 3.4 Pie Charts — for Composition
Acceptable for showing the breakdown of total deaths into 3–5 cause categories. Avoid for 10+ slices; use a horizontal bar chart instead.

### 3.5 Scatter Plots — for Two Numeric Variables
Plot speed vs fatality rate, age vs survival probability, etc. Reveals correlations, clusters, outliers.

### 3.6 Boxplots — for Distribution Comparison
Compare distributions of injury severity across vehicle types, ages, or regions.

### 3.7 Geographic Maps — for Spatial Patterns
Choropleth maps (states colored by death rate) instantly show geographic clustering. Vital for accident data, which is intrinsically spatial.

---

## 4. Distribution Shapes for Accident Data

### 4.1 Right-Skewed (Common)
Death counts per region, hospital stay durations, ages-at-death-from-falls — usually right-skewed. The mean overstates the typical case; report the median.

### 4.2 Bimodal (Sometimes)
Age-at-death from motor vehicle accidents is often bimodal — peaks among young drivers and elderly pedestrians.

### 4.3 Heavy-Tailed
Mass-casualty events. Most days have a moderate number of accidents; rare events have far more. Power-law-like distributions appear.

---

## 5. Time-Series Considerations

### 5.1 Stationarity
Are the statistics constant over time? Mortality rates often have **trend** (long-term direction) and **seasonality** (annual cycle). Visualize both.

### 5.2 Smoothing
Rolling averages (7-day, 30-day) smooth out noise and reveal trend. Use rolling means for daily data; longer windows for noisier data.

### 5.3 Anomalies
Sudden spikes (a mass-casualty event) or drops (a successful policy) deserve special attention. Annotate them on the chart.

### 5.4 Comparing Periods
Year-over-year comparisons are often more informative than absolute values. Plot each year as a separate line; or facet by year.

---

## 6. Color and Style for Mortality Data

### 6.1 Color Choice
- **Avoid red for safety topics** unless intentional — red signals danger and can sensationalize.
- **Use sequential palettes** for ordinal data (severity scale).
- **Use diverging palettes** for change-from-baseline comparisons.
- **Use categorical palettes** for cause categories.
- Always test for **colorblind safety** (about 8% of men have red-green colorblindness).

### 6.2 Tone
Mortality data deserves restraint. Avoid:
- 3D effects.
- Decorative gradients.
- Sensationalist titles ("DEATHS SOAR!").
- Truncated y-axes that exaggerate change.
- Cherry-picked time windows.

---

## 7. Ethical Considerations

Accident and death data carry unique ethical weight. Considerations:

### 7.1 Privacy
Don't identify individuals. Aggregate to regions or age groups. Even rare events can de-anonymize individuals if cohorts are too small.

### 7.2 Honesty
Don't truncate axes or cherry-pick periods to support a predetermined narrative. Show enough context for the viewer to judge fairly.

### 7.3 Sensitivity
Phrase categorical labels carefully. "Male" and "female" rather than gendered metaphors. "Pedestrian" rather than "victim". Avoid blaming the deceased.

### 7.4 Impact
Visualizations of mortality can drive policy. They have real consequences. Get the analysis right; don't sensationalize; don't bury caveats.

---

## 8. Common Pitfalls

- **Truncated y-axis** that exaggerates a small change.
- **Cherry-picked time window** that supports a narrative.
- **Misleading color scales** (e.g., implying ordering on nominal data).
- **Confusing aggregation** (mixing per-100K death rates with raw counts).
- **Ignoring population** — high *count* in California doesn't mean high *rate*.
- **Mixing different cause definitions** across years (data definitions change).

---

## 9. Best Practices

### 9.1 Start with the Question
What are you trying to learn or communicate? The chart should answer one clear question.

### 9.2 Choose the Right Plot
- One number over time → line chart.
- One number per category → bar chart.
- Two numbers per record → scatter plot.
- Composition of a whole → bar chart (or pie if few categories).
- Two-way comparison → heatmap.

### 9.3 Annotate Clearly
- Title states the finding.
- Axis labels with units.
- Legend if multiple series.
- Source and date.
- Annotations on key events.

### 9.4 Iterate
First plots are exploratory and ugly; final plots for an audience should be polished. Don't ship the first version.

---

## 10. Viva Questions (40)

### A. Visualization Fundamentals

**Q1. What is data visualization?**
The graphical representation of data, designed to make patterns, trends, relationships, and outliers visible to the human eye. It exploits the visual cortex's pattern-recognition power — we can scan thousands of points on a chart in seconds, but reading the same numbers in a table takes hours.

**Q2. Why is visualization especially important for public-safety data?**
Mortality and injury data inform policy decisions that save lives. Clear visualizations help policymakers, the public, and researchers identify high-risk groups, time trends, and effective interventions. Misleading visualizations can mislead policy with real human consequences.

**Q3. What is Exploratory Data Analysis?**
The iterative, informal process of getting to know a dataset before formal modeling. Coined by John Tukey. Visualization is its core activity — it surfaces data-quality issues, suggests features, and informs modeling decisions before any algorithms are run.

**Q4. What is Anscombe's quartet?**
Four datasets with identical mean, variance, correlation, and regression line — but completely different visual shapes. A reminder that summary statistics alone can deeply mislead, and that you must always plot data before drawing conclusions.

### B. Plot Selection

**Q5. When would you use a line chart for accident data?**
For time trends — deaths or accidents over time. Reveals long-term direction (rising, falling, flat), seasonality (winter spikes, summer lulls), and sudden changes after policy interventions.

**Q6. When would you use a bar chart for accident data?**
To compare a numeric summary across discrete categories — death counts by age group, by gender, by cause, by region. Orders should be meaningful (by value or domain), not alphabetical by default.

**Q7. When would you use a heatmap?**
For two-way comparisons — year × age group, state × cause, hour × day-of-week. Color encodes magnitude. Reveals patterns hidden in long tables.

**Q8. When would you use a choropleth map?**
For geographic comparisons — state-level death rates, district-level accident frequencies. The map encodes magnitude with color, immediately showing geographic clusters.

**Q9. When would you use a boxplot for accident data?**
To compare distributions of severity or duration across categories — injury severity by vehicle type, hospital-stay length by age group. Reveals medians, spread, and outliers.

**Q10. When would you use a histogram?**
For examining the distribution of one continuous variable — age at death, severity score, response time. Reveals center, spread, shape, and outliers.

### C. Distribution Shapes

**Q11. What does a right-skewed distribution look like?**
Long tail on the right. Most values cluster on the left; a few extreme values stretch right. Mean > median because the tail pulls the mean upward. Common in accident data — e.g., severity scores, hospital stays.

**Q12. Why are accident counts per region usually right-skewed?**
Population varies widely — a few large regions account for the bulk of accidents. Reporting raw counts makes them look concentrated; **death rates per 100 000** correct for this.

**Q13. What is bimodality and what might cause it in accident data?**
Two distinct peaks. In motor-vehicle deaths, age is bimodal: peaks among young drivers (inexperience) and elderly pedestrians (vulnerability). The two peaks reflect two different causal mechanisms.

**Q14. What's the empirical rule (68-95-99.7)?**
For a normal distribution: 68% of data within ±1 std, 95% within ±2 std, 99.7% within ±3 std. Foundation of the Z-score outlier rule.

### D. Time-Series

**Q15. What is a stationary time series?**
One whose statistical properties (mean, variance, autocorrelation) are constant over time. Real mortality data is usually **non-stationary** — there are trends (long-term direction) and seasonality (annual cycles).

**Q16. What is seasonality?**
Cyclical pattern that repeats at a fixed period — annual (winter spike for falls), weekly (weekend spikes for traffic deaths), daily (rush-hour spikes). Important to model when forecasting.

**Q17. Why use rolling averages?**
To smooth noise and reveal trend. Daily mortality counts are noisy; a 7-day rolling average smooths weekly variation; a 30-day rolling average smooths monthly noise. The trade-off: more smoothing = clearer trend but slower response to changes.

**Q18. What's the difference between count and rate?**
- **Count** — raw number of events (e.g., 1500 traffic deaths in California).
- **Rate** — count per population unit (e.g., 12.5 per 100K population).
California's high count just reflects its large population; the rate is what reveals risk.

**Q19. Why normalize by population when comparing regions?**
Otherwise large regions always look "bad" because they have more people. Per-capita rates correct for this and let you compare risk fairly. Always report rates, not just counts, when comparing populations.

### E. Color and Style

**Q20. What types of color palettes are there?**
- **Categorical** — distinct hues for unordered groups (causes, states).
- **Sequential** — light to dark for ordered data (severity scale).
- **Diverging** — two hues meeting at a midpoint for change-from-baseline.

**Q21. What is a colorblind-friendly palette?**
A palette designed so people with color blindness (about 8% of men) can still distinguish all categories. Examples: viridis (sequential), seaborn's `colorblind`. Avoid red-green pairings.

**Q22. Why might you avoid red in mortality data?**
Red signals danger and can sensationalize. For neutral reporting, prefer muted blues or grays. Reserve red for genuine emphasis.

### F. Ethical and Best Practices

**Q23. What are common visualization pitfalls in mortality data?**
- Truncated y-axis exaggerating change.
- Cherry-picked time windows supporting a narrative.
- Mixing counts and rates.
- Ignoring population.
- Sensationalist titles.
- Confusing definitions across years.

**Q24. Why does y-axis truncation matter?**
A 5% increase looks dramatic if you start the y-axis at 99.5%; the same 5% looks tiny if the axis starts at 0. Always start mortality y-axes at 0 unless you have a strong reason and clearly disclose it.

**Q25. What ethical considerations apply to mortality data?**
- **Privacy** — don't identify individuals; aggregate carefully.
- **Honesty** — don't manipulate axes or windows.
- **Sensitivity** — careful language; avoid blaming the deceased.
- **Impact** — visualizations have real policy consequences.

**Q26. What is data ink and why does it matter?**
Tufte's principle: most ink in a chart should encode data. Decorative gridlines, 3D effects, gradients are "chartjunk" that distracts without informing. Maximize data-ink ratio.

**Q27. How would you visualize the demographic breakdown of accident victims?**
- **Bar chart** of count or rate by age group, ordered by age.
- **Heatmap** of age × sex showing rate.
- **Pyramid chart** showing male/female mirrored by age — a natural fit for demographics.

### G. Practical & Code

**Q28. What's the difference between matplotlib and seaborn?**
- **matplotlib** — foundational, low-level, verbose, very flexible.
- **seaborn** — high-level statistical plots with better defaults and direct pandas support.
Use them together: seaborn for the plot, matplotlib for fine-tuning.

**Q29. How do you make a multi-panel figure?**
`fig, axes = plt.subplots(rows, cols)` — returns a figure and grid of axes. Plot into each axis individually. Call `plt.tight_layout()` to space them properly.

**Q30. How do you save a publication-quality plot?**
`plt.savefig('out.png', dpi=300, bbox_inches='tight')`. Use SVG or PDF for vector output that scales without pixelation. Always set `dpi` for raster.

**Q31. How do you add interactivity to a plot?**
- **Plotly** — `import plotly.express as px`. Interactive HTML output.
- **Bokeh** — server-rendered interactive plots.
- **Altair** — declarative, based on Vega-Lite.
Best for dashboards and exploratory work; static formats are better for papers.

**Q32. How do you visualize a year × cause heatmap?**
```python
pivot = df.pivot_table(values='deaths', index='year', columns='cause', aggfunc='sum')
sns.heatmap(pivot, annot=True, fmt='d', cmap='Reds')
```
This produces a year × cause grid colored by death count.

**Q33. How do you make a stacked bar chart of cause-of-death over time?**
```python
df.pivot_table(values='deaths', index='year', columns='cause').plot(kind='bar', stacked=True)
```
Each bar is a year; segments represent causes. Useful for showing absolute composition over time.

**Q34. What's the difference between a stacked and grouped bar chart?**
- **Stacked** — segments stacked vertically; total bar height = grand total.
- **Grouped** — separate bars side by side, one per category.
Stacked emphasizes totals; grouped emphasizes individual category comparison.

### H. Statistical Concepts

**Q35. What does correlation measure?**
The strength of a linear relationship between two numeric variables, ranging from −1 (perfect negative) to +1 (perfect positive). Note: 0 doesn't mean "unrelated" — it means "no linear relationship". Two variables can be strongly related non-linearly with correlation near 0.

**Q36. What's the difference between correlation and causation?**
Correlation measures statistical association; causation requires that one variable directly causes the other. Two variables can be correlated due to:
- Direct causation.
- Reverse causation.
- A common cause (confounder).
- Pure coincidence.
Mortality analysis is full of confounders — always be cautious about causal claims.

**Q37. What is Simpson's Paradox in mortality data?**
A trend reverses when groups are aggregated. Famous example: Berkeley graduate admissions appeared to discriminate against women in aggregate, but per-department analysis showed slight bias *toward* women. The aggregate misled because women applied to more competitive departments. Always check whether stratification reverses your finding.

**Q38. How do you detect a change-point in time-series mortality data?**
- **Visual** — annotate the suspected change date.
- **Rolling statistics** — change in mean / variance.
- **Statistical tests** — Bayesian online changepoint detection, Pettitt test.
For policy evaluation, plot pre-policy and post-policy data with confidence bands.

**Q39. What is a confidence interval and why include it?**
A range around a point estimate that reflects sampling uncertainty. A 95% CI on a death rate says "if we did this analysis many times, 95% of intervals would contain the true rate". Including CIs prevents readers from over-interpreting noise as a real change.

**Q40. How would you communicate a finding from accident data to a non-technical audience?**
- Choose **one** chart that tells the central story.
- Title states the finding ("Cyclist deaths increased 40% in 2023").
- Source and time period clearly labeled.
- Annotate key events on the chart.
- Strip every non-data element you can.
- Test with a non-technical reader before finalizing.
- Provide caveats — data definitions, missing data, confounders.
The goal of an explanatory chart is to be understood in 5 seconds and remembered an hour later.
