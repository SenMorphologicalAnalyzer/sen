package testcase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import net.java.sen.StringTagger;
import net.java.sen.Token;
import net.java.sen.processor.CompositPostProcessor;
import net.java.sen.processor.PostProcessor;
import net.java.sen.processor.RemarkPostProcessor;
import net.java.sen.processor.RemarkPreProcessor;
import junit.framework.TestCase;

public class RemarkProcessorTest extends TestCase {
    public static String TESTSTR = "&lt; コメント &gt;  です";

    static {
        System.setProperty("sen.home", ".");
    }
    public void testAnalyze() throws IllegalArgumentException, IOException {
        StringTagger tagger = StringTagger
                .getInstance("conf/sen-processor.xml");
        RemarkPostProcessor remarkPostPr = new RemarkPostProcessor();
        RemarkPreProcessor remarkPrePr = new RemarkPreProcessor();
        remarkPrePr.readRules(new BufferedReader(new StringReader("&lt; &gt; 記号-注釈")));
        tagger.addPostProcessor(remarkPostPr);
        tagger.addPreProcessor(remarkPrePr);
       
        Token[] token = tagger.analyze(TESTSTR);
        System.out.println("token="+token[0].toString());        
        System.out.println("token="+token[1].toString());        
        assertEquals(token[1].getPos(), "記号-注釈");
        assertEquals(token[1].toString(), "&lt; コメント &gt;");
    }
}