package lia.chapter3;

/**
 * Copyright Manning Publications Co.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan
 */

import junit.framework.TestCase;
import lia.common.Utils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

// From chapter 3
public class NearRealTimeTest extends TestCase {
    public void testNearRealTime() throws Exception {
        Directory dir = new RAMDirectory();
        IndexWriter writer = Utils.getIndexWriter(dir);
        for (int i = 0; i < 10; i++) {
            Document doc = new Document();
            doc.add(new Field("id", "" + i, StringField.TYPE_STORED));
            doc.add(new Field("text", "aaa", TextField.TYPE_STORED));
            writer.addDocument(doc);
        }
        IndexSearcher searcher = Utils.getIndexSearcher(dir);  // #A

        Query query = new TermQuery(new Term("text", "aaa"));
        TopDocs docs = searcher.search(query, 1);
        assertEquals(10, docs.totalHits);                        // #B

        writer.deleteDocuments(new Term("id", "7"));             // #2

        Document doc = new Document();                           // #3
        doc.add(new Field("id",                                  // #3
                "11",                                  // #3
                StringField.TYPE_STORED));   // #3
        doc.add(new Field("text",                                // #3
                "bbb",                                 // #3
                TextField.TYPE_STORED));                // #3
        writer.addDocument(doc);                                 // #3

        IndexReader newReader = searcher.getIndexReader();         // #4
        assertFalse(searcher.getIndexReader() == newReader);                        // #5
        searcher.getIndexReader().close();                                          // #6
        searcher = new IndexSearcher(newReader);

        TopDocs hits = searcher.search(query, 10);               // #7
        assertEquals(9, hits.totalHits);                         // #7

        query = new TermQuery(new Term("text", "bbb"));          // #8
        hits = searcher.search(query, 1);                        // #8
        assertEquals(1, hits.totalHits);                         // #8

        newReader.close();
        writer.close();
    }
}

/*
  #1 Create near-real-time reader
  #A Wrap reader in IndexSearcher
  #B Search returns 10 hits
  #2 Delete 1 document
  #3 Add 1 document
  #4 Reopen reader
  #5 Confirm reader is new
  #6 Close old reader
  #7 Verify 9 hits now
  #8 Confirm new document matched
*/

/*
#1 IndexWriter returns a reader that's able to search all previously committed changes to the index, plus any uncommitted changes.  The returned reader is always readOnly.
#2,#3 We make changes to the index, but do not commit them.
#4,#5,#6 Ask the reader to reopen.  Note that this simply re-calls writer.getReader again under the hood.  Because we made changes, the newReader will be different from the old one so we must close the old one.
#7, #8 The changes made with the writer are reflected in new searches.
*/