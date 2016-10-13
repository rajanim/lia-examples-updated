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

import junit.framework.Assert;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.io.StringReader;

// From chapter 4
//TODO class reports exception, need to // FIXME: 10/11/16
public class AnalyzerUtils {
    public static void displayTokens(Analyzer analyzer,
                                     String text) throws IOException {
        displayTokens(analyzer.tokenStream("contents", new StringReader(text)));  //A
    }

    public static void displayTokens(TokenStream stream)
            throws IOException {

        TermToBytesRefAttribute term = stream.addAttribute(TermToBytesRefAttribute.class);
        while (stream.incrementToken()) {
            System.out.print("[" + term.getBytesRef().utf8ToString() + "] ");    //B
        }
    }
  /*
    #A Invoke analysis process
    #B Print token text surrounded by brackets
  */

    public static int getPositionIncrement(AttributeSource source) {
        PositionIncrementAttribute attr = source.addAttribute(PositionIncrementAttribute.class);
        return attr.getPositionIncrement();
    }

    public static String getTerm(AttributeSource source) {
        TermToBytesRefAttribute attr = source.addAttribute(TermToBytesRefAttribute.class);
        return attr.getBytesRef().utf8ToString();
    }

    public static String getType(AttributeSource source) {
        TypeAttribute attr = source.addAttribute(TypeAttribute.class);
        return attr.type();
    }

    public static void setPositionIncrement(AttributeSource source, int posIncr) {
        PositionIncrementAttribute attr = source.addAttribute(PositionIncrementAttribute.class);
        attr.setPositionIncrement(posIncr);
    }

    public static void setTerm(AttributeSource source, String term) {
        TermToBytesRefAttribute attr = source.addAttribute(TermToBytesRefAttribute.class);
        //TODO  need to set term here attr.setTerm()
    }

    public static void setType(AttributeSource source, String type) {
        TypeAttribute attr = source.addAttribute(TypeAttribute.class);
        attr.setType(type);
    }

    public static void displayTokensWithPositions
            (Analyzer analyzer, String text) throws IOException {

        TokenStream stream = analyzer.tokenStream("contents",
                new StringReader(text));
        TermToBytesRefAttribute term = stream.addAttribute(TermToBytesRefAttribute.class);
        PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);

        int position = 0;
        while (stream.incrementToken()) {
            int increment = posIncr.getPositionIncrement();
            if (increment > 0) {
                position = position + increment;
                System.out.println();
                System.out.print(position + ": ");
            }

            System.out.print("[" + term.getBytesRef().utf8ToString() + "] ");
        }
        System.out.println();
    }

    public static void displayTokensWithFullDetails(Analyzer analyzer,
                                                    String text) throws IOException {

        TokenStream tokenStream = analyzer.tokenStream("contents",                        // #A
                new StringReader(text));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);  // #B
        TypeAttribute type = tokenStream.addAttribute(TypeAttribute.class);        // #B

        while (tokenStream.incrementToken()) {                                  // #C

            int startOffset = offsetAttribute.startOffset();
            System.out.println(startOffset);
            int endOffset = offsetAttribute.endOffset();
            System.out.println(endOffset);
            String term = charTermAttribute.toString();
            System.out.println(term);
            System.out.println(type.toString());
        }
    }
  /*
    #A Perform analysis
    #B Obtain attributes of interest
    #C Iterate through all tokens
    #D Compute position and print
    #E Print all token details
   */

    public static void assertAnalyzesTo(Analyzer analyzer, String input,
                                        String[] output) throws Exception {
        TokenStream stream = analyzer.tokenStream("field", new StringReader(input));

        TermToBytesRefAttribute termAttr = stream.addAttribute(TermToBytesRefAttribute.class);
        for (String expected : output) {
            Assert.assertTrue(stream.incrementToken());
            Assert.assertEquals(expected, termAttr.getBytesRef().utf8ToString());
        }
        Assert.assertFalse(stream.incrementToken());
        stream.close();
    }

    public static void displayPositionIncrements(Analyzer analyzer, String text)
            throws IOException {
        TokenStream stream = analyzer.tokenStream("text", text);
        PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);
        while (stream.incrementToken()) {
            System.out.println("posIncr=" + posIncr.getPositionIncrement());
        }
    }


    public static void main(String[] args) throws IOException {
        System.out.println("SimpleAnalyzer");
        displayPositionIncrements(new lia.chapter4.SimpleAnalyzer(),
                "The quick brown fox....");

        System.out.println("\n----");
        System.out.println("StandardAnalyzer");
        displayTokensWithFullDetails(new StandardAnalyzer(),
                "I'll email you at xyz@example.com");
    }
}

/*
#1 Invoke analysis process
#2 Output token text surrounded by brackets
*/

