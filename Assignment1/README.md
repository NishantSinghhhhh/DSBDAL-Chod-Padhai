# Assignment 1 — Data Wrangling I

## Objective
Take a raw, real-world dataset and turn it into a clean, fully-numeric table that any machine learning algorithm can consume. The work is split across two notebooks:

- `Data_Wrangling_I.ipynb` — full wrangling pipeline on the **Titanic** dataset (891 rows × 12 columns).
- `Assignment_1_Complete (1).ipynb` — the same pipeline applied to a local `weight_height.csv` dataset.

## Libraries Used

| Library | Purpose |
|---|---|
| `pandas` | DataFrame operations — loading, slicing, statistics, missing-value handling |
| `numpy` | Numerical computing; provides `np.nan` for missing values |
| `matplotlib.pyplot` | Base plotting library |
| `seaborn` | Statistical plots built on matplotlib |
| `sklearn.preprocessing` | `LabelEncoder` for category → integer, `MinMaxScaler` for normalization |

## Concepts Covered

### 1. Loading & Inspecting Data
- `pd.read_csv(url_or_path)` — reads CSV into a DataFrame (works with URLs too).
- `df.head(n)` / `df.tail(n)` — first / last `n` rows.
- `df.shape` — `(rows, columns)` tuple.
- `df.size` — total number of cells (rows × columns).
- `df.ndim` — number of dimensions (always 2 for a DataFrame).
- `df.columns.tolist()` — list of column names.
- `df.info()` — concise summary: non-null counts, dtypes, memory usage.

### 2. Summary Statistics
- `df.describe()` — count, mean, std, min, quartiles, max for numeric columns.
- `df.describe(include='object')` — count, unique, top, freq for text columns.
- `df.describe(include='all')` — combined view for every column.

### 3. Missing Value Detection
- `df.isnull()` — boolean mask of NaN cells.
- `df.isnull().sum()` — count of missing values per column.
- `df.notnull().sum()` — count of present values per column.
- Percent missing: `(df.isnull().sum() / len(df)) * 100`.

### 4. Missing Value Imputation
- `df['Age'].fillna(df['Age'].median())` — median fill, robust to outliers.
- `df['Cabin'].fillna('Unknown')` — placeholder fill for high-missingness text columns.
- `df['Embarked'].fillna(df['Embarked'].mode()[0])` — mode (most frequent) fill for categoricals.

### 5. Data Types & Conversion
- `df.dtypes` — dtype of each column (`int64`, `float64`, `object`, `category`, `bool`, `datetime64`).
- `df.select_dtypes(include=[...])` — pick columns by dtype.
- `df['col'].astype('category')` — convert int/string column to a proper categorical dtype (saves memory, signals intent).
- `df['col'].cat.categories` — list categories of a categorical column.

### 6. Normalization (Min-Max Scaling)
- Formula: `x_scaled = (x − x_min) / (x_max − x_min)` → range `[0, 1]`.
- `MinMaxScaler().fit_transform(df[numeric_cols])` — applies the scaling.
- Why it matters: distance-based models (KNN, K-Means) get biased by features with larger ranges.

### 7. Encoding Categorical Variables
Three techniques shown for converting text categories into numbers:

| Technique | Function | When to Use |
|---|---|---|
| **Label Encoding** | `LabelEncoder().fit_transform(col)` | Binary or ordinal categories (e.g., `male`/`female`) |
| **One-Hot Encoding** | `pd.get_dummies(df, columns=[...], drop_first=True)` | Nominal categories with no inherent order |
| **Manual Mapping** | `col.map({'S': 0, 'C': 1, 'Q': 2})` | When you need full control over the mapping order |

`drop_first=True` avoids the **dummy variable trap** (perfect multicollinearity) by dropping one category as the reference baseline.

## Pipeline Summary
1. Import libraries.
2. Load the dataset and document every variable (type + meaning).
3. Inspect dimensions, dtypes, summary statistics.
4. Detect missing values and decide a fill strategy per column.
5. Convert columns to their correct dtypes (`category` for ordinal/nominal ints).
6. Normalize continuous numeric columns to `[0, 1]`.
7. Encode categorical columns into numeric form.
8. Drop high-cardinality text columns (`Name`, `Ticket`, `Cabin`) that aren't useful as ML features.
9. Final DataFrame: no missing values, all numeric, ready for modeling.
