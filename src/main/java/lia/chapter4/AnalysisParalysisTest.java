package lia.chapter4;

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
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

// From chapter 4
public class AnalysisParalysisTest extends TestCase {
    public void testAnalyzer() throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
        String queryString = "category:/philosophy/eastern";

        Query query = new QueryParser("contents",
                analyzer).parse(queryString);
        assertEquals("path got split, yikes!",
                "category:\"philosophy eastern\"",
                query.toString("contents"));

        PerFieldAnalyzerWrapper perFieldAnalyzer =
                new PerFieldAnalyzerWrapper(analyzer);

        query = new QueryParser(
                "contents",
                perFieldAnalyzer).parse(queryString);
        assertEquals("leave category field alone",
                "category:/philosophy/eastern",
                query.toString("contents"));
    }
}
