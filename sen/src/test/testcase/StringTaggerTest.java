package testcase;

import java.io.IOException;

import net.java.sen.StringTagger;
import net.java.sen.Token;
import junit.framework.TestCase;

public class StringTaggerTest extends TestCase {
    public static String TESTSTR = "��ʹ�ȥ⡼�˥�̼";
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
        assertEquals(token[0].getBasicString(),"��ʹ");
        assertEquals(token[0].getPos(),"̾��-����");
        assertEquals(token[1].getBasicString(),"��");
        assertEquals(token[1].getPos(),"�ե��顼");
        assertEquals(token[2].getBasicString(),"�⡼�˥�");
        assertEquals(token[2].getPos(),"̾��-����");
    }
}
