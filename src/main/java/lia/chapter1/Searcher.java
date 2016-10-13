package lia.chapter1;

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

import lia.common.Utils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.Scanner;

// From chapter 1

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
public class Searcher {

    public static void main(String[] args) throws IllegalArgumentException,
            IOException, ParseException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Index directory path: ");
        String indexDir = scanner.nextLine();
        System.out.println("Search string: ");
        String q = scanner.nextLine();                      //2

        search(indexDir, "content", q);
    }


    /**
     * creates index reader pointing to indexes and index searcher to search for docs matching given query(set to queryparser)
     *
     * @param indexDir
     * @param fieldName
     * @param searchString
     * @throws ParseException
     * @throws IOException
     */
    public static void search(String indexDir, final String fieldName, final String searchString) throws ParseException, IOException {
        IndexSearcher searcher = Utils.getIndexSearcher(indexDir);

        QueryParser parser = new QueryParser(fieldName, new StandardAnalyzer());
        Query query = parser.parse(searchString);

        TopDocs docs = searcher.search(query, 100);
        ScoreDoc[] hits = docs.scoreDocs;
        int numTotalHits = docs.totalHits;
        System.out.println("Total match:" + numTotalHits);
        Document[] documents = new Document[numTotalHits];
        System.out.println("result docs: ");
        for (int i = 0; i < numTotalHits; i++) {
            documents[i] = searcher.doc(hits[i].doc);
            System.out.println(documents[i].get("filepath"));
        }

    }
}


/*
#1 Parse provided index directory
#2 Parse provided query string
#3 Open index
#4 Parse query
#5 Search index
#6 Write search stats
#7 Retrieve matching document
#8 Display filename
#9 Close IndexSearcher
*/

