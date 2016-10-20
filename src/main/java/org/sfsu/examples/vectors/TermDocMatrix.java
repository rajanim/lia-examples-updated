package org.sfsu.examples.vectors;

import lia.common.Utils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rajanishivarajmaski1 on 10/18/16.
 */


public class TermDocMatrix {
    IndexSearcher searcher;
    IndexReader reader;
    Map<String, Integer> termIdMap;


    public TermDocMatrix() throws IOException {
        searcher = Utils.getBookIndexSearcher();
        reader = searcher.getIndexReader();
        termIdMap = computeTermIdMap(reader);
        dumpTermIdMap();
    }

    public static void main(String[] args) throws IOException {
        TermDocMatrix termDocMatrix = new TermDocMatrix();

        RealMatrix realMatrix = termDocMatrix.buildTermDocumentMatrix();
        for (int i = 0; i < 1; i++) {
            System.out.println(realMatrix.getRowVector(i).toString());
            System.out.println(realMatrix.getColumnVector(i).toString());
        }

    }

    private void dumpTermIdMap() {
        System.out.println(termIdMap.size());
        Iterator it = termIdMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());

        }

    }

    /**
     * Map term to a fix integer so that we can build document matrix later.
     * It's used to assign term to specific row in Term-Document matrix
     */
    private Map<String, Integer> computeTermIdMap(IndexReader reader) throws IOException {
        Map<String, Integer> termIdMap = new HashMap<>();
        int id = 0;
        Fields fields = MultiFields.getFields(reader);
        Terms terms = fields.terms("subject");
        TermsEnum itr = terms.iterator();
        BytesRef term = null;
        while ((term = itr.next()) != null) {
            String termText = term.utf8ToString();
            if (termIdMap.containsKey(termText))
                continue;
           // System.out.println(termText);
            termIdMap.put(termText, id++);
        }

        return termIdMap;
    }

    /**
     * build term-document matrix for the given directory
     */
    public RealMatrix buildTermDocumentMatrix() throws IOException {
        //iterate through directory to work with each doc
        int row = 0;
        int maxDoc = reader.maxDoc();       //get the number of documents here
        int numTerms = termIdMap.size();    //total number of terms
        RealMatrix tdMatrix = new Array2DRowRealMatrix(maxDoc, numTerms);
        for (int i = 0; i < 1; i++) {
            Document doc = reader.document(i);
            System.out.println("\n" + doc.get("subject") + "\n");
            Terms termsVector = reader.getTermVector(i, "subject");
            TermsEnum termsEnum = termsVector.iterator();
            BytesRef bytesRef = termsEnum.next();
            while ((bytesRef = termsEnum.next()) != null) {
                String termText = bytesRef.utf8ToString();
                int col = termIdMap.get(termText);
                long termFreq = termsEnum.totalTermFreq();
                long docCount = termsEnum.docFreq();
                double weight = 1.0; //computeTfIdfWeight(termFreq, docCount, numDocs);
                System.out.println(termText+ " "+ +row +" "+ col);
              tdMatrix.setEntry(row,col ,1.0);
            }
            row++;
        }


      /*  for (File f : corpus.listFiles()) {
            if (!f.isHidden() && f.canRead()) {
                //I build term document matrix for a subset of corpus so
                //I need to lookup document by path name.
                //If you build for the whole corpus, just iterate through all documents
                String path = f.getPath();
                BooleanQuery.Builder pathQuery = new BooleanQuery.Builder();
                pathQuery.add(new TermQuery(new Term("path", path)), BooleanClause.Occur.SHOULD);

                TopDocs hits = searcher.search(pathQuery.build(), 1);

                //get term vector
                Terms termVector = reader.getTermVector(hits.scoreDocs[0].doc, "contents");
                TermsEnum itr = termVector.iterator(null);
                BytesRef term = null;

                //compute term weight
                while ((term = itr.next()) != null) {
                    String termText = term.utf8ToString();
                    int row = termIdMap.get(termText);
                    long termFreq = itr.totalTermFreq();
                    long docCount = itr.docFreq();
                    double weight = 1.0; //computeTfIdfWeight(termFreq, docCount, numDocs);
                    tdMatrix.setEntry(row, col, weight);
                }
                col++;
            }
        }*/
        return tdMatrix;
    }
}


