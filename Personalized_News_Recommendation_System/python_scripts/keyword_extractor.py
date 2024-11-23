import sys
from sklearn.feature_extraction.text import TfidfVectorizer

def extract_keywords(text):
    if not text:
        print("Error: No text provided", file=sys.stderr)
        sys.exit(1)

    # Extract keywords using TF-IDF
    vectorizer = TfidfVectorizer(max_features=10, stop_words='english')
    X = vectorizer.fit_transform([text])
    keywords = vectorizer.get_feature_names_out()
    
    # Print keywords to stdout (to be captured by Java)
    print(",".join(keywords))

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Usage: python script.py 'text content'", file=sys.stderr)
        sys.exit(1)

    text_content = sys.argv[1]
    extract_keywords(text_content)
