package lia.common;

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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.util.Locale;

class NumericDateRangeQueryParser extends QueryParser {
    public NumericDateRangeQueryParser(
            String field, Analyzer a) {
        super(field, a);
    }
    public Query getRangeQuery(String field,
                               String part1,
                               String part2,
                               boolean inclusive)
            throws ParseException {
        TermRangeQuery query = (TermRangeQuery)
                super.getRangeQuery(field, part1, part2, inclusive, true);

        if ("pubmonth".equals(field)) {
            return LegacyNumericRangeQuery.newIntRange(
                    "pubmonth",
                    Integer.parseInt("1"),
                    Integer.parseInt("10"),
                    query.includesLower(),
                    query.includesUpper());
        } else {
            return query;
        }
    }
}

// From chapter 6
public class NumericQueryParserTest extends TestCase {
    private Analyzer analyzer;
    private IndexSearcher searcher;
    private Directory dir;

    protected void setUp() throws Exception {
        analyzer = new StandardAnalyzer();
        searcher = Utils.getIndexSearcher("");
    }

    protected void tearDown() throws Exception {
        dir.close();
    }
    public void testNumericRangeQuery() throws Exception {
        String expression = "price:[10 TO 20]";

        QueryParser parser = new NumericRangeQueryParser(
                "subject", analyzer);

        Query query = parser.parse(expression);
        System.out.println(expression + " parsed to " + query);
    }

    public void testDefaultDateRangeQuery() throws Exception {
        QueryParser parser = new QueryParser(
                "subject", analyzer);
        Query query = parser.parse("pubmonth:[1/1/04 TO 12/31/04]");
        System.out.println("default date parsing: " + query);
    }
    public void testDateRangeQuery() throws Exception {
        String expression = "pubmonth:[01/01/2010 TO 06/01/2010]";

        QueryParser parser = new NumericDateRangeQueryParser(
                "subject", analyzer);

        parser.setDateResolution("pubmonth", DateTools.Resolution.MONTH);    // 1
        parser.setLocale(Locale.US);

        Query query = parser.parse(expression);
        System.out.println(expression + " parsed to " + query);

        TopDocs matches = searcher.search(query, 10);
        assertTrue("expecting at least one result !", matches.totalHits > 0);
    }

    static class NumericRangeQueryParser extends QueryParser {
        public NumericRangeQueryParser(
                String field, Analyzer a) {
            super(field, a);
        }
        public Query getRangeQuery(String field,
                                   String part1,
                                   String part2,
                                   boolean inclusive)
                throws ParseException {
            TermRangeQuery query = (TermRangeQuery)            // A
                    super.getRangeQuery(field, part1, part2,         // A
                            inclusive, true);                // A

            return query;                                    // C
        }
    }
}

