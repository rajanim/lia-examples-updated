package lia.chapter5;

import lia.common.Utils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * Created by rajanishivarajmaski1 on 10/10/16.
 */
public class BooksLikeThis {

    private IndexReader reader;
    private IndexSearcher searcher;

    public BooksLikeThis(String indexDir) throws IOException {
        this.searcher = Utils.getIndexSearcher(indexDir);
        this.reader = searcher.getIndexReader();


    }

    public static void main(String[] args) throws IOException {
        BooksLikeThis blk = new BooksLikeThis("/Users/rajanishivarajmaski1/University/CSC899/lucene_in_action/lia2e_2/index/");

        int numDocs = blk.reader.maxDoc();
        System.out.println("docs "+numDocs);

        for (int i = 0; i < numDocs; i++) {                    // #1
            System.out.println();
            Document doc = blk.reader.document(i);
         //   System.out.println("document object: " +doc.toString());
           System.out.println(doc.get("title") + " isbn:  "+doc.get("isbn"));
            System.out.println(doc.get("subject"));
            System.out.println(doc.get("author"));

            Document[] docs = blk.docsLike(i, 10);               // #2
            if (docs.length == 0) {
                System.out.println("  None like this");
            }
            for (Document likeThisDoc : docs) {
                System.out.println("  -> " + likeThisDoc.get("title"));
            }
        }
        blk.reader.close();
    }

    public Document[] docsLike(int id, int max) throws IOException {
        Document doc = reader.document(id);
        String[] authors = doc.getValues("author");
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        for (String author : authors) {
            query.add(new TermQuery(new Term("author", author)),   // #3
                    BooleanClause.Occur.SHOULD);
        }
        Terms terms = reader.getTermVector(id, "subject");
        TermsEnum termsEnum = terms.iterator();
        BytesRef bytesRef = termsEnum.next();
        while (bytesRef != null) {
            query.add(new TermQuery(new Term("subject", termsEnum.term())),   // #3
                    BooleanClause.Occur.SHOULD);

            bytesRef = termsEnum.next();
        }

        query.add(new TermQuery(                                       // #6
                new Term("isbn", doc.get("isbn"))), BooleanClause.Occur.MUST_NOT); // #6
        TopDocs hits = searcher.search(query.build(), 10);
        System.out.println("Q: " + query.build());
        int size = max;
        if (max > hits.scoreDocs.length) size = hits.scoreDocs.length;

        Document[] docs = new Document[size];
        for (int i = 0; i < size; i++) {
            docs[i] = reader.document(hits.scoreDocs[i].doc);
        }

        return docs;
    }


}
