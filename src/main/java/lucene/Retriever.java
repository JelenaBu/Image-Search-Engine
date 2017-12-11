package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Retriever {

    private final IndexSearcher indexSearcher;
    private final IndexReader indexReader;
    private final MultiFieldQueryParser queryParser;
    private Query query;

    public Retriever(String indexPath) throws IOException {
        indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        indexSearcher = new IndexSearcher(indexReader);
        queryParser = new MultiFieldQueryParser(new String[] { "url", "alt" }, new StandardAnalyzer());
    }

    public TopDocs searchQuery(String queryString) throws IOException, ParseException {
        query = queryParser.parse(queryString);
        return indexSearcher.search(query, 50);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }
}