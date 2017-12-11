package lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/query")
public class QueryParser {

    private static class QueryResult{

        public String url;
        public String alt;

        public QueryResult(Document doc){
            this.url = doc.get("url");
            this.alt = doc.get("alt");
        }
    }

    private static class Results {
        public String query;
        public int count;
        public ArrayList<QueryResult> results;

        public Results(String query){
            this.query = query;
            this.results = new ArrayList<>();
        }
    }

    private final Retriever retriever;

    public QueryParser() throws IOException {
        retriever = new Retriever("index");
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> search(
            @RequestParam(value="query", required=true) String query){

        try {
            final TopDocs topDocs = retriever.searchQuery(query);
            Results results = new Results(query);
            for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = retriever.getDocument(scoreDoc);
                results.results.add(new QueryResult(doc));
            }
            results.count = results.results.size();
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (ParseException e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}

