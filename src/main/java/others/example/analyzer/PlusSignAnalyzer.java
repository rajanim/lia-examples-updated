package others.example.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.Reader;

// from blog : http://www.citrine.io/blog/2015/2/14/building-a-custom-analyzer-in-lucene

public class PlusSignAnalyzer extends Analyzer {

    /* This is the only function that we need to override for our analyzer.
     * It takes in a java.io.Reader object and saves the tokenizer and list
     * of token filters that operate on it.
     */

    public static void main(String[] args) throws IOException {
        TokenStream tokenStream = new PlusSignAnalyzer().tokenStream("text", "The+quick+-+brown+fox..");
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

    }

    protected TokenStreamComponents createComponents(String field,
                                                     Reader reader) throws IOException {
        Tokenizer tokenizer = new PlusSignTokenizer(reader);
        TokenStream filter = new EmptyStringTokenFilter(tokenizer);
        filter = new LowerCaseFilter(filter);
        return new TokenStreamComponents(tokenizer, filter);
    }

    //copied from org.apache.lucene.analysis.core.SimpleAnalyzer. For understanding and reference to LIA book
    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        return new TokenStreamComponents(new LowerCaseTokenizer());
    }
}