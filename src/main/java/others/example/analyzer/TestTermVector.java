package others.example.analyzer;

/**
 * Created by rajanishivarajmaski1 on 10/12/16.
 * http://makble.com/what-is-term-vector-in-lucene
 */

import lia.common.Utils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class TestTermVector {
    public static Analyzer analyzer = new StandardAnalyzer();
    public static IndexWriterConfig config = new IndexWriterConfig(analyzer);
    public static RAMDirectory ramDirectory = new RAMDirectory();
    public static IndexWriter indexWriter;

    public static void main(String[] args) {
        Document doc = new Document();
        doc.add(new Field("title", "quick fox brown fox",
                Utils.getTextFieldTypeWithTermVectors()
        ));
        //  doc.add(new Field("body", "quick fox run faster", TextField.TYPE_STORED));

        try {
            indexWriter = new IndexWriter(ramDirectory, config);
            indexWriter.addDocument(doc);
            doc = new Document();
            doc.add(new Field("title", "fox run faster",
                    Utils.getTextFieldTypeWithTermVectors()
            ));
            indexWriter.addDocument(doc);
            doc = new Document();
            doc.add(new Field("title", "yellow fox kind fox",
                    Utils.getTextFieldTypeWithTermVectors()
            ));
            indexWriter.addDocument(doc);
            doc = new Document();
            doc.add(new Field("title", " quick brown fox",
                    Utils.getTextFieldTypeWithTermVectors()
            ));
            indexWriter.addDocument(doc);
            indexWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            IndexReader idxReader = DirectoryReader.open(ramDirectory);

            for (int i = 0; i < 4; i++) {
                System.out.println("\n doc" + (i + 1) + ":");
                System.out.println(" Title: " + idxReader.document(i).get("title") + "\n");

                Terms terms = idxReader.getTermVector(i, "title");
                TermsEnum termsEnum = terms.iterator();
                BytesRef bytesRef = termsEnum.next();
                while (bytesRef != null) {
                    Term termInstance = new Term("title", bytesRef);
                    long docCount = idxReader.docFreq(termInstance);
                    System.out.println(bytesRef.utf8ToString() + "  tf: " + termsEnum.totalTermFreq() +
                            "  df: " + docCount);
                    bytesRef = termsEnum.next();
                }

            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
