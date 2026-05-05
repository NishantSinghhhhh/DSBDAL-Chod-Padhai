# Assignment 11 — Text Analytics: Document Preprocessing and TF-IDF

Notebook: `Assignment_11_Complete.ipynb`
Focus: tokenization, POS tagging, stop-word removal, stemming, lemmatization, Bag-of-Words, Term Frequency, Inverse Document Frequency, and TF-IDF vectorization.

---

## 1. Why Text Preprocessing?

Raw text is **unstructured** — it isn't already organized into rows and columns. Machine-learning algorithms work on numbers, not words, so before any model can learn from text, the text must be converted into a numeric representation. That process is called **text preprocessing** or **NLP preprocessing**, and it's done in steps that progressively clean and normalize the input.

Why bother?

- **Reduce noise.** Raw text contains punctuation, capitalization, URLs, HTML tags, and other artifacts that don't help and often hurt.
- **Reduce sparsity.** "Run", "running", "ran", "runs" all carry similar meaning. Treating them as one token shrinks the vocabulary dramatically.
- **Standardize.** Two documents talking about the same topic must end up with overlapping tokens for the model to recognize the similarity.
- **Improve signal-to-noise ratio.** Common words like "the", "is", "and" appear everywhere and add little discriminative power.

A typical pipeline looks like:

1. Lowercasing.
2. Removing punctuation, special characters, numbers (depending on task).
3. Tokenization.
4. POS tagging (optional, for downstream tasks).
5. Stop-word removal.
6. Stemming or lemmatization.
7. Vectorization (Bag-of-Words / TF-IDF / embeddings).

---

## 2. Tokenization

### 2.1 What It Is
**Tokenization** is the process of breaking a stream of text into smaller pieces called **tokens**. The tokens are usually words, but they can also be sub-words, characters, or sentences depending on the granularity you need.

- "Cats are sitting on the mat." → `['Cats', 'are', 'sitting', 'on', 'the', 'mat', '.']`

### 2.2 Types of Tokenization

- **Word tokenization** — split on whitespace and punctuation. Most common.
- **Sentence tokenization** — split a paragraph into sentences. Useful when you need to retain sentence boundaries.
- **Sub-word tokenization** — break words into smaller pieces (BPE, WordPiece, SentencePiece). Used in modern transformer models.
- **Character tokenization** — every character is a token. Used in low-resource languages or for typos.

### 2.3 Tokenization Pitfalls

- **Contractions**: "don't" → "do" + "n't" or "do" + "not"?
- **Hyphens**: "state-of-the-art" → keep as one or split into pieces?
- **Numbers**: keep digits, replace with `<NUM>`, or drop?
- **URLs / emails**: keep as one token, replace with placeholder, or drop?
- **Languages without spaces** (Chinese, Japanese): require specialized tokenizers.

---

## 3. Part-of-Speech (POS) Tagging

### 3.1 What It Is
POS tagging assigns a grammatical category to each token: noun, verb, adjective, adverb, preposition, etc.

- "The cat sits on the mat" → `[('The', DT), ('cat', NN), ('sits', VBZ), ('on', IN), ('the', DT), ('mat', NN)]`

Common tag set: **Penn Treebank** (NN, VB, JJ, RB, IN, DT, …).

### 3.2 Why It's Useful

- **Lemmatization** uses POS tags to know whether to convert "saw" to "see" (verb) or "saw" (noun, the tool).
- **Information extraction** (e.g., extracting all noun phrases from a document).
- **Parsing**, **named entity recognition**, **dependency analysis**.
- **Filtering** — keeping only nouns and verbs, for example, before computing similarity.

---

## 4. Stop Words

### 4.1 What They Are
**Stop words** are extremely common words ("a", "the", "is", "and", "to", "of", "in") that appear in almost every document and carry little discriminative power. Removing them shrinks the vocabulary and emphasizes content words.

### 4.2 When to Remove Them

- **Bag-of-Words / TF-IDF for classification** — yes, remove them.
- **Information retrieval** — sometimes remove, sometimes keep (modern search engines often keep them).
- **Sentiment analysis** — be careful: "not" can flip polarity. Removing "not" can ruin the model.
- **Sequence models / embeddings** — usually keep them. Modern models learn to weight them appropriately.

### 4.3 Languages
Standard stop-word lists exist for English (NLTK, spaCy), and similar lists exist for many other languages.

---

## 5. Stemming

### 5.1 What It Is
**Stemming** is a crude rule-based procedure that chops off word suffixes to produce a common root form (the "stem"). The stem is not necessarily a real word.

| Word | Stem (Porter) |
|---|---|
| running | run |
| runner | runner |
| ran | ran |
| studies | studi |
| studying | studi |
| dogs | dog |
| caresses | caress |

### 5.2 Algorithms

- **Porter Stemmer** — the original, oldest, fastest. Produces some odd stems ("studi") but consistent.
- **Snowball Stemmer** — improved Porter, supports multiple languages.
- **Lancaster Stemmer** — more aggressive than Porter, can be too aggressive.

### 5.3 Pros and Cons

- ✅ Fast — purely rule-based.
- ✅ Simple — no dictionary needed.
- ❌ Produces non-words ("studi", "consoli").
- ❌ Can over-stem ("universities" → "univers" loses meaning).
- ❌ Can under-stem ("ran" stays "ran", but "running" → "run").

---

## 6. Lemmatization

### 6.1 What It Is
**Lemmatization** reduces a word to its dictionary base form (the **lemma**), using vocabulary and morphological analysis. Unlike stemming, the result is always a real word.

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
- Slower than stemming because of the lexicon lookup, but more accurate.

### 6.3 Stemming vs Lemmatization

| Aspect | Stemming | Lemmatization |
|---|---|---|
| Output | Crude stem, not always a real word | Real dictionary word |
| Speed | Fast | Slower |
| Accuracy | Lower | Higher |
| Dependency | Just rules | Lexicon + sometimes POS |
| Language support | Easy to extend | Harder to extend |
| Use case | High-volume IR, fast pipelines | When precision matters |

---

## 7. Bag-of-Words (BoW)

### 7.1 What It Is
The **Bag-of-Words** model represents a document as a vector of **word counts**, ignoring grammar and word order. The vocabulary is the set of all unique words across all documents (the "corpus").

Document A: "the cat sat on the mat"
Document B: "the dog sat on the floor"

Vocabulary: `{the, cat, sat, on, mat, dog, floor}`
A: `[2, 1, 1, 1, 1, 0, 0]`
B: `[2, 0, 1, 1, 0, 1, 1]`

### 7.2 Strengths and Weaknesses

- ✅ Simple, fast, easy to explain.
- ✅ Works well for many classification tasks (spam, sentiment).
- ❌ Ignores word order — "dog bites man" and "man bites dog" are identical.
- ❌ Sparse — vocabulary is huge, most cells are 0.
- ❌ Treats all words equally; doesn't distinguish important from common.

---

## 8. Term Frequency (TF)

### 8.1 What It Is
**Term Frequency** is how often a term appears in a document. Several common forms:

- **Raw count** — `tf(t, d) = count of t in d`.
- **Normalized count** — `tf(t, d) = count(t, d) / total tokens in d` (so longer documents don't dominate).
- **Logarithmic** — `tf(t, d) = 1 + log(count)` (diminishing returns for very frequent terms).

### 8.2 Why Just TF Isn't Enough
A word that appears in *every* document, like "the", has high TF in every document but discriminates between none of them. We need a way to down-weight such pervasive terms — that's IDF.

---

## 9. Inverse Document Frequency (IDF)

### 9.1 The Idea
**IDF** measures how rare a term is across the entire corpus. Rare terms carry more discriminative information than common terms.

> idf(t) = log( N / df(t) )

Where `N` is the total number of documents and `df(t)` is the number of documents containing term `t`.

- Term in every document → idf = log(1) = 0.
- Term in 1 of N documents → idf = log(N), the maximum.

A common variant adds smoothing: `idf(t) = log( (1 + N) / (1 + df(t)) ) + 1` — used by scikit-learn to avoid division-by-zero and ensure non-zero IDF for terms in every document.

---

## 10. TF-IDF

### 10.1 The Combination

> tfidf(t, d) = tf(t, d) · idf(t)

- High in document `d` if the term is **frequent in `d`** but **rare across the corpus**.
- Zero (or small) if the term is everywhere — those terms don't help distinguish documents.

### 10.2 What TF-IDF Captures

- **Specificity** — which terms make a document special compared to others.
- **Topic signal** — domain-specific terms (e.g., "antibiotic" in a medical document) get high weights.
- **De-emphasis of common words** — "the", "is", and other ubiquitous words shrink to near zero even without explicit stop-word removal.

### 10.3 Use Cases

- **Document classification** (spam, sentiment, topic).
- **Information retrieval** (search engines historically ranked by TF-IDF; modern systems use it as a feature).
- **Document similarity** — cosine similarity between TF-IDF vectors.
- **Keyword extraction** — top-k highest TF-IDF words in a document are good summary keywords.

### 10.4 Limitations

- Still ignores word order and context (just like Bag-of-Words).
- Doesn't capture semantic similarity ("car" and "automobile" are different tokens, different IDFs).
- Sparse — high-dimensional vectors, mostly zero.
- Can't generalize to unseen words.

These limitations motivate modern approaches — **word embeddings** (Word2Vec, GloVe), **contextual embeddings** (BERT, GPT) — which capture word semantics and context.

---

## 11. NLTK and Friends

- **NLTK** (Natural Language Toolkit) — the classic Python library for NLP teaching. Provides tokenizers, stemmers, lemmatizers, POS taggers, stop-word lists, WordNet.
- **spaCy** — modern, production-ready NLP. Faster and more accurate than NLTK.
- **scikit-learn** — provides `CountVectorizer` (Bag-of-Words) and `TfidfVectorizer` (TF-IDF) as standard preprocessing tools.
- **Hugging Face Transformers** — modern context-aware embeddings.

---

## 12. Viva Questions (40)

### A. Text Preprocessing Fundamentals

**Q1. What is text analytics / text mining?**
The process of extracting useful information, patterns, and insights from unstructured text. It includes preprocessing, statistical analysis, classification, clustering, sentiment analysis, summarization, and topic modeling. Anywhere text exists at scale — emails, support tickets, medical records, social media — text analytics adds value.

**Q2. Why does text need preprocessing?**
Raw text is high-noise and high-variance. Punctuation, casing, inflection, stop words, and synonyms all add variation without adding signal. Preprocessing reduces noise, shrinks vocabulary, and standardizes representations so models can learn from the actual content rather than surface forms.

**Q3. What is unstructured data?**
Data that isn't organized in a predefined row-column format. Text, audio, images, and video are unstructured. About 80% of enterprise data is unstructured, which is why preprocessing pipelines are so important.

**Q4. What is the typical NLP preprocessing pipeline?**
1. Lowercasing.
2. Removing punctuation, special characters, numbers.
3. Tokenization.
4. POS tagging (optional).
5. Stop-word removal.
6. Stemming or lemmatization.
7. Vectorization (BoW, TF-IDF, embeddings).
Each step is task-dependent — sentiment analysis often skips stop-word removal because "not" matters.

### B. Tokenization

**Q5. What is tokenization?**
Splitting a stream of text into smaller units called **tokens**, usually words. Tokens are the basic units for downstream processing. Word tokenization splits on whitespace and punctuation; sentence tokenization splits paragraphs into sentences; sub-word tokenization breaks words into smaller pieces.

**Q6. Why is tokenization harder than just splitting on spaces?**
Real text has contractions ("don't"), hyphens ("state-of-the-art"), URLs, emails, abbreviations ("U.S.A."), and emojis. Some languages (Chinese, Japanese, Thai) don't use spaces between words at all and need specialized tokenizers.

**Q7. What are sub-word tokens?**
Tokens smaller than words — pieces created by algorithms like Byte-Pair Encoding, WordPiece, and SentencePiece. They handle out-of-vocabulary words gracefully ("unfriendliness" → "un" + "friend" + "li" + "ness") and are the foundation of modern transformer models like BERT and GPT.

**Q8. What is sentence tokenization and when is it useful?**
Splitting a paragraph into individual sentences. Useful when you need to preserve sentence boundaries — summarization, machine translation, question answering, sentiment analysis at the sentence level.

### C. POS Tagging

**Q9. What is POS tagging?**
**Part-of-Speech tagging** assigns a grammatical category — noun, verb, adjective, adverb, etc. — to each token. The standard tag set is Penn Treebank (NN for noun, VB for verb, JJ for adjective, etc.).

**Q10. Why is POS tagging useful?**
- Disambiguates words with multiple meanings ("saw" — verb or noun?).
- Helps lemmatization choose the right base form.
- Enables filtering (e.g., extract only noun phrases for keyword extraction).
- Foundation for syntactic parsing and named entity recognition.

**Q11. How does a POS tagger work?**
Modern POS taggers are typically **neural sequence models** (BiLSTM, transformer) trained on annotated corpora. Older taggers used Hidden Markov Models or Conditional Random Fields. They use both the word's identity and its surrounding context to assign tags.

### D. Stop Words

**Q12. What are stop words?**
Common words that occur in almost every document — "a", "the", "is", "and", "of", "in", "to". They carry minimal discriminative information and are usually removed before vectorization to shrink vocabulary and emphasize content words.

**Q13. When should you keep stop words?**
- **Sentiment analysis** — "not" is technically a stop word, but it flips polarity.
- **Search engines** — modern search keeps stop words for phrase matching.
- **Sequence models / embeddings** — let the model learn to weight them.
- **Stylometric analysis** — stop-word patterns are author fingerprints.

**Q14. Are stop-word lists language-specific?**
Yes — every language has its own list. NLTK includes lists for English, Spanish, French, German, Hindi, and many others. Don't apply an English stop-word list to French text.

### E. Stemming

**Q15. What is stemming?**
A rule-based procedure that strips suffixes from words to produce a common root form (the "stem"). The stem is not always a real dictionary word — "studies" stems to "studi". Used to collapse different inflected forms of a word so they're treated identically.

**Q16. What is the Porter stemmer?**
The classical English stemmer, designed by Martin Porter in 1980. A series of cascaded rule-based steps that strip suffixes — "ies" → "i", "ed" → "", "ing" → "", etc. Fast, simple, and the de-facto standard for English stemming.

**Q17. What's the difference between Porter and Snowball stemmers?**
**Snowball** (also called Porter2) is an improved Porter stemmer with refined rules and support for many languages. Slightly more accurate than Porter; the recommended default for most NLP work that needs stemming.

**Q18. What is the Lancaster stemmer?**
A more aggressive English stemmer than Porter. It can reduce words to very short stems (e.g., "maximum" → "maxim"), which is sometimes useful but often over-stems.

**Q19. What is over-stemming and under-stemming?**
- **Over-stemming**: words with different meanings collapse to the same stem (e.g., "universal" and "universe" both → "univers").
- **Under-stemming**: related forms fail to collapse (e.g., "ran" and "running" stay separate because the rule-based stripping doesn't know about irregular verbs).
The Porter family balances both reasonably well.

### F. Lemmatization

**Q20. What is lemmatization?**
Reducing words to their dictionary base form (the "lemma") using vocabulary and morphological analysis. Unlike stemming, the result is always a real word: "ran" → "run", "better" → "good", "mice" → "mouse".

**Q21. How does lemmatization differ from stemming?**
- **Stemming** strips suffixes by rules; output may not be a real word.
- **Lemmatization** uses a lexicon (like WordNet) and possibly POS info; output is always a real word.
- Stemming is faster; lemmatization is more accurate.
- Stemming is language-easy to extend; lemmatization needs a lexicon for each language.

**Q22. Why does lemmatization sometimes need POS tags?**
To disambiguate. "Saw" is "see" if a verb but stays "saw" if a noun. Without POS info, the lemmatizer has to guess and often guesses wrong.

**Q23. When would you use stemming over lemmatization?**
When speed and simplicity matter more than precision — large-scale information retrieval, real-time pipelines, exploratory analysis. Stemming is also useful when no lemmatizer exists for the target language.

**Q24. When would you use lemmatization over stemming?**
When the result needs to be human-readable (search highlighting, summarization, keyword extraction) or when you need higher accuracy for downstream tasks (sentiment, topic modeling).

### G. Vectorization

**Q25. What is the Bag-of-Words model?**
A representation where each document is a vector of word counts over a fixed vocabulary, ignoring grammar and word order. Simple, fast, and often surprisingly effective for classification — but it cannot capture word order or semantics.

**Q26. What is term frequency (TF)?**
How often a term appears in a document. Variants:
- **Raw count**: just the count.
- **Normalized**: count divided by document length.
- **Log-scaled**: `1 + log(count)` — diminishing returns.
Used as the local component of TF-IDF.

**Q27. What is document frequency (DF)?**
The number of documents in the corpus that contain a given term. Stop words have high DF (they appear in nearly every document); rare or technical terms have low DF.

**Q28. What is inverse document frequency (IDF)?**
A measure of how rare a term is across the corpus: `idf(t) = log(N / df(t))`. Rare terms have high IDF; common terms have low IDF; terms in every document have IDF = 0. The "inverse" reflects that we want rare terms to count for more.

**Q29. What is TF-IDF?**
The product of term frequency and inverse document frequency: `tfidf(t, d) = tf(t, d) · idf(t)`. High when a term appears often in a particular document but rarely across the corpus — i.e., the term is *characteristic* of that document. Low for ubiquitous words.

**Q30. Why use TF-IDF over raw counts?**
TF-IDF down-weights common words like "the" and emphasizes domain-specific words like "diagnosis". The result is a representation where documents are described by what makes them *distinctive*, not by what makes them *typical*.

**Q31. Why is the logarithm used in IDF?**
To dampen the effect of extreme values. Without the log, very rare terms would get astronomically high weights and dominate the vector. The log compresses the range and produces a more useful weighting.

**Q32. What does scikit-learn's `TfidfVectorizer` do?**
Combines tokenization, vocabulary building, optional stop-word removal, TF-IDF computation, and L2 normalization in one step. Output is a sparse matrix where each row is a document and each column is a vocabulary term, with TF-IDF values.

**Q33. What are the limitations of TF-IDF?**
- Ignores word order and context.
- Sparse — vocabulary can be tens of thousands, mostly zeros.
- No semantic similarity (treats "car" and "automobile" as completely unrelated).
- Doesn't handle out-of-vocabulary words at inference.

**Q34. What's the alternative to TF-IDF in modern NLP?**
**Word embeddings** (Word2Vec, GloVe, FastText) — dense vectors that capture semantic similarity. **Contextual embeddings** (BERT, GPT) — vectors that depend on surrounding words, capturing context and meaning much better than any static model.

### H. Practical & Code-Specific

**Q35. What is NLTK?**
**Natural Language Toolkit** — a Python library that provides classical NLP tools: tokenizers, POS taggers, stemmers (Porter, Lancaster, Snowball), lemmatizers (WordNet), stop-word lists, and access to many corpora. The standard teaching tool for NLP.

**Q36. What are common pitfalls in text preprocessing?**
- Removing too aggressively (losing important words).
- Forgetting to lowercase before tokenization.
- Treating punctuation inconsistently.
- Not handling encoding issues (UTF-8 vs Latin-1).
- Using English-only tools on multilingual data.
- Stop-word removal in sentiment tasks where "not" matters.

**Q37. Should you preprocess test data the same way as training data?**
Yes — exactly. The same vocabulary, same tokenizer, same lowercasing, same stop-word list, same vectorizer (fit on training, applied to test). Any difference introduces leakage or distribution shift and degrades test-set performance.

**Q38. What is cosine similarity and how does it relate to TF-IDF?**
Cosine similarity measures the angle between two vectors: `cos(θ) = (a · b) / (|a| · |b|)`. Range is [−1, 1]; for non-negative TF-IDF vectors it's [0, 1]. Two documents with similar TF-IDF profiles have a small angle between them and high cosine similarity. It's the default similarity metric for text.

**Q39. How would you build a simple text classifier?**
1. Preprocess text (lowercase, tokenize, remove stop words, lemmatize).
2. Vectorize with TF-IDF (`TfidfVectorizer`).
3. Train a classifier — Multinomial Naïve Bayes or Logistic Regression are strong baselines.
4. Evaluate with cross-validation.
5. Iterate — tune n-grams, max features, IDF settings, and hyperparameters.

**Q40. How would you go beyond TF-IDF?**
- **Word embeddings** (Word2Vec, GloVe, FastText) — dense semantic vectors.
- **Contextual embeddings** (BERT, RoBERTa) — context-aware token embeddings.
- **Sentence embeddings** (Sentence-BERT, USE) — direct vector for whole sentences.
- **Fine-tuned transformers** for end-to-end task-specific models.
- **LLM-based features** for complex tasks like classification, summarization, or QA.
