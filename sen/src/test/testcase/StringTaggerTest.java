package testcase;

import java.io.IOException;

import net.java.sen.StringTagger;
import net.java.sen.Token;
import junit.framework.TestCase;

public class StringTaggerTest extends TestCase {
    public static String TESTSTR = "新聞とモーニング娘";
    static {
        System.setProperty("sen.home", ".");
    }
    public void testAnalyze() throws IllegalArgumentException, IOException {
        StringTagger tagger = StringTagger.getInstance();
        Token[] token = tagger.analyze(TESTSTR);
//        for(int i=0;i<token.length;i++){
//            System.out.print(token[i].toString()+"\t");
//            System.out.println(token[i].getPos());
//        }
        assertEquals(token[0].getBasicString(),"新聞");
        assertEquals(token[0].getPos(),"名詞-一般");
        assertEquals(token[1].getBasicString(),"と");
        assertEquals(token[1].getPos(),"フィラー");
        assertEquals(token[2].getBasicString(),"モーニング");
        assertEquals(token[2].getPos(),"名詞-一般");
    }
}
