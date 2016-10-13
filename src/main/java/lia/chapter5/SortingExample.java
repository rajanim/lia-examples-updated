package lia.chapter5;

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
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;

// From chapter 5
public class SortingExample {

    public static void main(String[] args) throws Exception {
        Query allBooks = new MatchAllDocsQuery();

        QueryParser parser = new QueryParser(
                "contents",                             // #1
                new StandardAnalyzer(                   // #1
                ));             // #1
        BooleanQuery.Builder query = new BooleanQuery.Builder();

        query.add(allBooks, BooleanClause.Occur.SHOULD);                             // #1
        query.add(parser.parse("java OR action"), BooleanClause.Occur.SHOULD);       // #1

        SortingExample example = new SortingExample();                     // #2

        example.displayResults(query.build(), Sort.RELEVANCE);

        example.displayResults(query.build(), Sort.INDEXORDER);

        example.displayResults(query.build(), new Sort(new SortField("category", SortField.Type.STRING)));

        example.displayResults(query.build(), new Sort(new SortField("pubmonth", SortField.Type.INT, true)));

        example.displayResults(query.build(),
                new Sort(new SortField("category", SortField.Type.STRING),
                        SortField.FIELD_SCORE,
                        new SortField("pubmonth", SortField.Type.INT, true)
                ));

        example.displayResults(query.build(), new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("category", SortField.Type.STRING)}));
    }

/*
  The Sort object (#1) encapsulates an ordered collection of
  field sorting information. We ask IndexSearcher (#2) to
  compute scores per hit. Then we call the overloaded search
  method that accepts the custom Sort (#3). We use the
  useful toString method (#4) of the Sort class to describe
  itself, and then create PrintStream that accepts UTF-8
  encoded output (#5), and finally use StringUtils (#6) from
  Apache Commons Lang for nice columnar output
  formatting. Later youùll see a reason to look at the
  explanation of score . For now, itùs commented out (#7).
*/

    public void displayResults(Query query, Sort sort)            // #1
            throws IOException {
        IndexSearcher searcher = Utils.getBookIndexSearcher();


        TopDocs results = searcher.search(query, 20, sort);           // #3

        System.out.println("\nResults for: " +                      // #4
                query.toString() + " sorted by " + sort);

     /*   System.out.println(StringUtils.rightPad("Title", 30) +
                StringUtils.rightPad("pubmonth", 10) +
                StringUtils.center("id", 4) +
                StringUtils.center("score", 15));*/

        PrintStream out = new PrintStream(System.out, true, "UTF-8");    // #5

        DecimalFormat scoreFormatter = new DecimalFormat("0.######");
        for (ScoreDoc sd : results.scoreDocs) {
            int docID = sd.doc;
            // float score = sd.score;
            Document doc = searcher.doc(docID);
         /*   out.println(
                    StringUtils.rightPad(                                                  // #6
                            StringUtils.abbreviate(doc.get("title"), 29), 30) +                // #6
                            StringUtils.rightPad(doc.get("pubmonth"), 10) +                        // #6
                            StringUtils.center("" + docID, 4) +                                    // #6
                            StringUtils.leftPad(                                                   // #6
                                    scoreFormatter.format(score), 12));   */                              // #6
            out.println("   " + doc.get("category"));
            //out.println(searcher.explain(query, docID));   // #7
        }


    }
}

/*
#1 Create test query
#2 Create example running
*/