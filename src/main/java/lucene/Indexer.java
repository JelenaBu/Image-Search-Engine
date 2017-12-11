package lucene;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException{
        final Path indexPath = Paths.get(indexDirectoryPath);

        if (Files.exists(indexPath)) {
            Files.walk(indexPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }

        final Directory indexDirectory = FSDirectory.open(indexPath);
        writer = new IndexWriter(indexDirectory, new IndexWriterConfig());
    }

    public int createIndex() throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/dump")))) {
            String line;
            Document document;

            while ((line = reader.readLine()) != null) {
                if(line.contains("<img")){
                    document = new Document();

                    String[] lineSplit = line.split("<img");
                    String imgTag;
                    if(lineSplit.length < 2)
                        imgTag = "";
                    else
                        imgTag = line.split("<img")[1];
                    int index=0;
                    int found = 0;
                    while (found == 0) {
                        for (; index < imgTag.length() - 1; index++) {
                            if (imgTag.substring(index, index + 2).equals("/>") || imgTag.substring(index, index + 1).equals(">") || imgTag.substring(index+1, index + 2).equals(">")) {
                                if(imgTag.substring(index+1, index + 2).equals(">"))
                                    index++;
                                found = 1;
                                break;
                            }
                        }
                        if (found == 0) {
                            imgTag = imgTag.concat(reader.readLine());
                        }
                    }
                    imgTag = imgTag.substring(1,index);

                    String sep = "\"";
                    if(imgTag.contains("src='")){
                        sep="'";
                    }

                    if(imgTag.contains("src=")){
                        String imgUrl= imgTag.split("src="+sep)[1];

                        imgUrl = imgUrl.substring(0,imgUrl.indexOf(sep));



                        if(imgTag.contains("alt=\"")) {
                            sep = "\"";
                            if (imgTag.contains("alt='")) {
                                sep = "'";
                            }

                            String alt = imgTag.split("alt=" + sep)[1];
                            alt = alt.substring(0, alt.indexOf(sep));

                            document.add(new TextField("url", imgUrl, Field.Store.YES));
                            document.add(new TextField("alt", alt, Field.Store.YES));
                            writer.addDocument(document);
                        }
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.numDocs();
    }

    public void close() throws IOException{
        writer.close();
    }
}