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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.codecs.TermVectorsFormat;

import java.io.IOException;

// From chapter 4
public final class SimpleAnalyzer extends Analyzer {

    public static void main(String[] args) throws IOException {

        TokenStream tokenStream = new SimpleAnalyzer().tokenStream("text", "The quick brown fox..");
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            int startOffset = offsetAttribute.startOffset();
            System.out.println(startOffset);
            int endOffset = offsetAttribute.endOffset();
            System.out.println(endOffset);
            String term = charTermAttribute.toString();
            System.out.println(term);

        }

        /*AnalyzerUtils.displayTokensWithFullDetails(new SimpleAnalyzer(),
              "The quick brown fox....");*/
    }

    //copied from org.apache.lucene.analysis.core.SimpleAnalyzer. For understanding and reference to LIA book
    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        return new TokenStreamComponents(new LowerCaseTokenizer());
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }
}
