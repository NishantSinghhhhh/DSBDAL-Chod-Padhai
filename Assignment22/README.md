# Assignment 22 — Text Analytics: Preprocessing + TF-IDF

Notebook: `Assignment_22_Complete.ipynb`
Focus: tokenization, POS tagging, stop-word removal, stemming, lemmatization, Bag-of-Words, Term Frequency, Inverse Document Frequency, and TF-IDF.

> Note: Same problem statement as Assignment 11. This is a fresh, standalone implementation.

---

## 1. Why Text Preprocessing?

Raw text is **unstructured** — it isn't already organized into rows and columns. ML algorithms work on numbers, not words, so before any model can learn from text, it must be converted to a numeric representation. That process is **text preprocessing** or **NLP preprocessing**, done in steps that progressively clean and normalize the input.

Why bother?

- **Reduce noise.** Raw text contains punctuation, capitalization, URLs, HTML tags, and other artifacts that don't help and often hurt.
- **Reduce sparsity.** "Run", "running", "ran", "runs" all carry similar meaning. Treating them as one token shrinks vocabulary dramatically.
- **Standardize.** Two documents talking about the same topic must end up with overlapping tokens for a model to recognize the similarity.
- **Improve signal-to-noise ratio.** Common words like "the", "is", "and" appear everywhere and add little discriminative power.

A typical pipeline:

1. Lowercasing.
2. Remove punctuation, special characters, numbers (depending on task).
3. Tokenization.
4. POS tagging (optional).
5. Stop-word removal.
6. Stemming or lemmatization.
7. Vectorization (Bag-of-Words / TF-IDF / embeddings).

---

## 2. Tokenization

### 2.1 What It Is
**Tokenization** breaks a stream of text into smaller pieces — **tokens**. Usually words, but sometimes sub-words, characters, or sentences.

### 2.2 Types

- **Word tokenization** — split on whitespace and punctuation. Most common.
- **Sentence tokenization** — paragraph → list of sentences.
- **Sub-word tokenization** — break words into smaller pieces (BPE, WordPiece, SentencePiece). Used in modern transformer models.
- **Character tokenization** — every character is a token.

### 2.3 Pitfalls

- Contractions: "don't" → "do" + "n't" or "do" + "not"?
- Hyphens: "state-of-the-art" → keep as one or split?
- Numbers: keep, replace with `<NUM>`, or drop?
- URLs / emails: keep, replace, or drop?
- Languages without spaces (Chinese, Japanese, Thai): require specialized tokenizers.

---

## 3. Part-of-Speech (POS) Tagging

### 3.1 What It Is
Assigns a grammatical category — noun, verb, adjective, adverb, etc. — to each token. Standard tag set: **Penn Treebank** (NN, VB, JJ, RB, etc.).

### 3.2 Why It's Useful

- **Lemmatization** uses POS to disambiguate ("saw" — verb or noun?).
- **Information extraction** — extract noun phrases, named entities.
- **Filtering** — keep only nouns and verbs for keyword extraction.
- **Foundation for parsing**, **named entity recognition**, **dependency analysis**.

---

## 4. Stop Words

### 4.1 What They Are
Common words ("a", "the", "is", "and", "of", "in") that appear in nearly every document and carry minimal discriminative power. Removing them shrinks vocabulary and emphasizes content words.

### 4.2 When to Remove vs Keep

- **BoW / TF-IDF for classification**: yes, remove.
- **Search engines**: usually keep (modern engines do).
- **Sentiment analysis**: be careful — "not" can flip polarity.
- **Embeddings / sequence models**: keep them; the model learns to weight them.

### 4.3 Languages
Standard stop-word lists exist for English, Spanish, French, German, Hindi, and many others. Don't apply an English list to French text.

---

## 5. Stemming

### 5.1 What It Is
A rule-based procedure that strips suffixes from words to produce a common root form (the **stem**). The stem isn't necessarily a real word.

| Word | Porter Stem |
|---|---|
| running | run |
| studies | studi |
| caresses | caress |
| dogs | dog |

### 5.2 Algorithms

- **Porter Stemmer** — original (1980), oldest, fastest.
- **Snowball / Porter2** — improved Porter, multi-language.
- **Lancaster** — more aggressive, sometimes too much.

### 5.3 Pros and Cons

- ✅ Fast and rule-based.
- ✅ No dictionary needed.
- ❌ Produces non-words ("studi", "univers").
- ❌ Can over-stem ("universities" → "univers" loses meaning).
- ❌ Can under-stem (irregular verbs).

---

## 6. Lemmatization

### 6.1 What It Is
Reducing a word to its dictionary base form (the **lemma**) using vocabulary and morphological analysis. Unlike stemming, the result is always a real word.

| Word | Lemma |
|---|---|
| running | run |
| ran | run |
| better | good |
| was | be |
| mice | mouse |
| children | child |

### 6.2 How It Works

- Uses a **lexicon** (e.g., WordNet) — a database of words and their dictionary forms.
- Often needs **POS information** to disambiguate. "Saw" → "see" if a verb, "saw" if a noun.
- Slower than stemming because of the lexicon lookup, but much more accurate.

### 6.3 Stemming vs Lemmatization

| Aspect | Stemming | Lemmatization |
|---|---|---|
| Output | Crude stem, may not be a real word | Real dictionary word |
| Speed | Fast | Slower |
| Accuracy | Lower | Higher |
| Dependency | Just rules | Lexicon + sometimes POS |
| Use case | High-volume IR, fast pipelines | When precision matters |

---

## 7. Bag-of-Words (BoW)

### 7.1 What It Is
Each document is represented as a vector of **word counts** over a fixed vocabulary, **ignoring grammar and word order**. The vocabulary is the set of all unique words across the corpus.

### 7.2 Strengths and Weaknesses

- ✅ Simple, fast, easy to explain.
- ✅ Works well for many classification tasks (spam, sentiment, topic).
- ❌ Ignores word order — "dog bites man" and "man bites dog" are identical.
- ❌ Sparse — vocabulary is huge, most cells zero.
- ❌ Treats all words equally; doesn't distinguish important from common.

---

## 8. Term Frequency (TF)

How often a term appears in a document. Several forms:

- **Raw count** — `tf = count`.
- **Normalized** — `tf = count / total tokens` (so longer docs don't dominate).
- **Log-scaled** — `tf = 1 + log(count)` (diminishing returns).

A word that appears in **every** document has high TF in every document but discriminates between none of them. We need a way to down-weight pervasive terms.

---

## 9. Inverse Document Frequency (IDF)

A measure of how rare a term is across the corpus. Rare terms carry more discriminative information than common ones.

> idf(t) = log( N / df(t) )

Where `N` is the number of documents and `df(t)` is the number of documents containing term `t`.

- Term in every document → idf = log(1) = 0.
- Term in 1 of N documents → idf = log(N), maximum.

A common variant adds smoothing: `idf = log((1 + N) / (1 + df)) + 1` to avoid division by zero.

---

## 10. TF-IDF

### 10.1 The Combination

> tfidf(t, d) = tf(t, d) · idf(t)

- **High** if the term is **frequent in `d` but rare across the corpus**.
- **Zero or small** if the term appears everywhere (no signal).

### 10.2 What TF-IDF Captures

- **Specificity** — terms that make a document distinctive.
- **Topic signal** — domain-specific terms get high weights.
- **De-emphasis of common words** — "the", "is" shrink to near zero even without explicit stop-word removal.

### 10.3 Use Cases

- Document classification (spam, sentiment, topic).
- Information retrieval (search engines historically ranked by TF-IDF).
- Document similarity (cosine similarity between TF-IDF vectors).
- Keyword extraction (top-k terms by TF-IDF in a document).

### 10.4 Limitations

- Still ignores word order and context.
- Sparse — high-dimensional, mostly zeros.
- Doesn't capture semantic similarity ("car" and "automobile" are different tokens).
- Can't generalize to unseen words.

These motivate modern approaches — **word embeddings** (Word2Vec, GloVe), **contextual embeddings** (BERT, GPT) — which capture meaning and context.

---

## 11. Cosine Similarity

> cos(θ) = (a · b) / (|a| · |b|)

The cosine of the angle between two vectors. For TF-IDF (non-negative) vectors, range is [0, 1]. The default similarity metric for documents — measures similarity in *direction*, ignoring length.

---

## 12. Viva Questions (40)

### A. Text Analytics Fundamentals

**Q1. What is text analytics?**
The process of extracting useful information, patterns, and insights from unstructured text. Includes preprocessing, statistical analysis, classification, clustering, sentiment analysis, summarization, and topic modeling.

**Q2. Why does text need preprocessing?**
Raw text is high-noise, high-variance — punctuation, casing, inflection, stop words, and synonyms add variation without adding signal. Preprocessing reduces noise, shrinks vocabulary, and standardizes representations so models can learn from content rather than surface forms.

**Q3. What is unstructured data?**
Data that isn't organized in a predefined row-column format. Text, audio, images, and video are unstructured. About 80% of enterprise data is unstructured.

**Q4. What is the typical NLP preprocessing pipeline?**
1. Lowercasing.
2. Remove punctuation, special characters, numbers.
3. Tokenization.
4. POS tagging (optional).
5. Stop-word removal.
6. Stemming or lemmatization.
7. Vectorization.

### B. Tokenization

**Q5. What is tokenization?**
Splitting a stream of text into smaller units called **tokens**, usually words. Tokens are the basic units for downstream processing.

**Q6. Why is tokenization harder than just splitting on spaces?**
Real text has contractions ("don't"), hyphens ("state-of-the-art"), URLs, emails, abbreviations ("U.S.A."), and emojis. Some languages (Chinese, Japanese, Thai) don't use spaces.

**Q7. What are sub-word tokens?**
Tokens smaller than words — pieces created by algorithms like Byte-Pair Encoding, WordPiece, and SentencePiece. They handle out-of-vocabulary words gracefully ("unfriendliness" → "un" + "friend" + "li" + "ness") and are the foundation of modern transformer models.

**Q8. What is sentence tokenization?**
Splitting a paragraph into individual sentences. Useful when you need to preserve sentence boundaries — summarization, machine translation, sentence-level sentiment analysis.

### C. POS Tagging

**Q9. What is POS tagging?**
**Part-of-Speech tagging** assigns a grammatical category — noun, verb, adjective, adverb, etc. — to each token. Standard tag set: Penn Treebank.

**Q10. Why is POS tagging useful?**
- Disambiguates words with multiple meanings ("saw" — verb or noun?).
- Helps lemmatization choose the right base form.
- Enables filtering (e.g., extract only nouns for keyword extraction).
- Foundation for syntactic parsing and named-entity recognition.

**Q11. How does a modern POS tagger work?**
Modern POS taggers are typically neural sequence models (BiLSTM, transformer) trained on annotated corpora. Older taggers used Hidden Markov Models or Conditional Random Fields. They use both the word's identity and its surrounding context to assign tags.

### D. Stop Words

**Q12. What are stop words?**
Common words that occur in almost every document — "a", "the", "is", "and", "of", "in", "to". They carry minimal discriminative information and are usually removed before vectorization.

**Q13. When should you keep stop words?**
- **Sentiment analysis** — "not" flips polarity.
- **Search engines** — modern search keeps them for phrase matching.
- **Sequence models / embeddings** — let the model weight them.
- **Stylometric analysis** — stop-word patterns are author fingerprints.

**Q14. Are stop-word lists language-specific?**
Yes. NLTK includes lists for English, Spanish, French, German, Hindi, and many others. Don't apply an English list to French text.

### E. Stemming

**Q15. What is stemming?**
A rule-based procedure that strips suffixes from words to produce a common root form. The stem isn't always a real dictionary word — "studies" stems to "studi". Used to collapse different inflected forms.

**Q16. What is the Porter stemmer?**
The classical English stemmer, designed by Martin Porter in 1980. Cascaded rule-based steps that strip suffixes. Fast, simple, and the de-facto standard for English stemming.

**Q17. What's the difference between Porter and Snowball stemmers?**
**Snowball (Porter2)** is an improved Porter stemmer with refined rules and support for many languages. Slightly more accurate; the recommended default for most NLP work.

**Q18. What is the Lancaster stemmer?**
A more aggressive English stemmer than Porter. Can reduce words to very short stems ("maximum" → "maxim"), which is sometimes useful but often over-stems.

**Q19. What is over-stemming and under-stemming?**
- **Over-stemming**: words with different meanings collapse to the same stem ("universal" and "universe" → "univers").
- **Under-stemming**: related forms fail to collapse ("ran" and "running" stay separate because rules don't handle irregular verbs).

### F. Lemmatization

**Q20. What is lemmatization?**
Reducing words to their dictionary base form (the **lemma**) using vocabulary and morphological analysis. Result is always a real word: "ran" → "run", "better" → "good".

**Q21. How does lemmatization differ from stemming?**
- Stemming strips suffixes by rules; output may not be a real word.
- Lemmatization uses a lexicon and possibly POS info; output is always a real word.
- Stemming is faster; lemmatization is more accurate.
- Stemming is easy to extend; lemmatization needs a lexicon per language.

**Q22. Why does lemmatization sometimes need POS tags?**
To disambiguate. "Saw" is "see" if a verb but stays "saw" if a noun. Without POS info the lemmatizer has to guess.

**Q23. When would you use stemming over lemmatization?**
When speed and simplicity matter more than precision — large-scale information retrieval, real-time pipelines, exploratory analysis. Also useful when no lemmatizer exists for the target language.

**Q24. When would you use lemmatization over stemming?**
When the result needs to be human-readable (search highlighting, summarization) or when you need higher accuracy for downstream tasks (sentiment, topic modeling).

### G. Vectorization

**Q25. What is the Bag-of-Words model?**
A representation where each document is a vector of word counts over a fixed vocabulary, ignoring grammar and word order. Simple, fast, and surprisingly effective for classification — but cannot capture word order or semantics.

**Q26. What is term frequency (TF)?**
How often a term appears in a document. Variants: raw count, normalized count (per document length), log-scaled (`1 + log(count)`).

**Q27. What is document frequency (DF)?**
The number of documents in the corpus that contain a given term. Stop words have high DF; rare technical terms have low DF.

**Q28. What is inverse document frequency (IDF)?**
A measure of how rare a term is across the corpus: `idf(t) = log(N / df(t))`. Rare terms get high IDF; ubiquitous terms get IDF = 0.

**Q29. What is TF-IDF?**
The product `tf · idf`. High when a term appears often in a particular document but rarely across the corpus — i.e., the term is *characteristic* of that document. Low for ubiquitous words.

**Q30. Why use TF-IDF over raw counts?**
TF-IDF down-weights common words like "the" and emphasizes domain-specific terms like "diagnosis". The result is a representation where documents are described by what makes them *distinctive* rather than typical.

**Q31. Why is the logarithm used in IDF?**
To dampen extreme values. Without the log, very rare terms would get astronomical weights and dominate the vector. The log compresses the range and produces a more useful weighting.

**Q32. What does scikit-learn's `TfidfVectorizer` do?**
Combines tokenization, vocabulary building, optional stop-word removal, TF-IDF computation, and L2 normalization into one step. Output is a sparse matrix where each row is a document and each column is a vocabulary term.

**Q33. What are the limitations of TF-IDF?**
- Ignores word order and context.
- Sparse — high-dimensional, mostly zeros.
- No semantic similarity (treats "car" and "automobile" as unrelated).
- Doesn't handle out-of-vocabulary words at inference.

**Q34. What's the modern alternative to TF-IDF?**
- **Word embeddings** (Word2Vec, GloVe, FastText) — dense semantic vectors.
- **Contextual embeddings** (BERT, RoBERTa, GPT) — vectors that depend on surrounding context.
- **Sentence embeddings** (Sentence-BERT) — single vectors per sentence/document.

### H. Practical & Code-Specific

**Q35. What is NLTK?**
**Natural Language Toolkit** — a Python library that provides classical NLP tools: tokenizers, POS taggers, stemmers, lemmatizers, stop-word lists, and access to many corpora. The standard teaching tool for NLP.

**Q36. What is the difference between NLTK and spaCy?**
- **NLTK** is teaching-oriented; provides individual tools for every step.
- **spaCy** is production-oriented; faster, more accurate, and provides integrated pipelines (tokenize + tag + parse + NER in one call).

**Q37. Should you preprocess test data the same as training data?**
Yes — exactly. Same vocabulary, same tokenizer, same lowercasing, same stop-word list, same vectorizer (fit on train, applied to test). Any difference introduces leakage or distribution shift and degrades test-set performance.

**Q38. What is cosine similarity and how does it relate to TF-IDF?**
`cos(θ) = (a · b) / (|a| · |b|)`. Measures the angle between two vectors. For non-negative TF-IDF vectors, range is [0, 1]. Two documents with similar TF-IDF profiles have a small angle — high cosine similarity. The default similarity metric for text.

**Q39. How would you build a simple text classifier?**
1. Preprocess text (lowercase, tokenize, remove stop words, lemmatize).
2. Vectorize with `TfidfVectorizer`.
3. Train a classifier — Multinomial Naïve Bayes or Logistic Regression are strong baselines.
4. Evaluate with cross-validation.
5. Iterate — tune n-grams, max features, IDF settings, and hyperparameters.

**Q40. How would you go beyond TF-IDF?**
- **Word embeddings** for semantic similarity.
- **Contextual embeddings** (BERT, RoBERTa) for context-aware representation.
- **Sentence embeddings** for direct document-level vectors.
- **Fine-tuned transformers** for end-to-end task-specific models.
- **LLM-based features** for complex tasks like classification, summarization, or QA.
