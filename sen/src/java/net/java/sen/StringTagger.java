/*
 * StringTagger.java - generate tag from string.
 * 
 * Copyright (C) 2002 Takashi Okamoto
 * Takashi Okamoto <tora@debian.org>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 */
package net.java.sen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class generate morpheme tags from String.
 */
public class StringTagger {
  private static Log log = LogFactory.getLog(StringTagger.class);
  private static HashMap hash = new HashMap();
  private Viterbi viterbi = null;
  private Node node = null;
  Token[] token = null;
  int cnt = 0;
  private static StringTagger tagger = null;
  private String DEFAULT_CONFIG = "conf/sen.xml";
  protected String unknownPos = null;

  // configuration file
  String tokenFile = null;
  String doubleArrayFile = null;
  String posInfoFile = null;
  String connectFile = null;

  String charset = null;

  /**
   * Construct new StringTagger. Currently only support Locale.JAPANESE.
   * 
   * StringTagger instance is exsisted only one for each Locale.
   * 
   * @param locale
   *          Locale to generate morphological analyzer.
   */
  private StringTagger(Locale locale) throws IOException,
      IllegalArgumentException {
    if (locale.equals(Locale.JAPANESE)) {
      this.init_ja(DEFAULT_CONFIG);
    } else {
      throw new IllegalArgumentException("Locale '" + locale.getDisplayName()
          + "' isn't supported.");
    }
  }

  /**
   * Obtain StringTagger instance for specified locale.
   * 
   * @param locale
   *          Locale to generate morphological analyzer.
   */
  public static synchronized StringTagger getInstance(Locale locale)
      throws IOException, IllegalArgumentException {
    Object tagger = hash.get(locale);
    if (tagger == null) {
      tagger = (Object) new StringTagger(locale);
      hash.put(locale, tagger);
      return (StringTagger) tagger;
    } else {
      return (StringTagger) tagger;
    }
  }

  /**
   * Initialize mophological analyzer for Japanse.
   */
  private void init_ja(String confFile) throws IOException {
    String confPath = System.getProperty("sen.home")
        + System.getProperty("file.separator") + confFile;

    readConfig(confPath);

    net.java.sen.Tokenizer tokenizer = new net.java.sen.ja.JapaneseTokenizer(
        tokenFile, doubleArrayFile, posInfoFile, connectFile, charset);

    viterbi = new Viterbi(tokenizer);
  }

  /**
   * Analyze string.
   * 
   * @param input
   *          string to analyze.
   * @return token array which represents morphemes.
   */
  public synchronized Token[] analyze(String input) throws IOException {
    if (log.isDebugEnabled()) {
      log.debug("analyzer:" + input);
    }

    int len = 0;
    Node node = viterbi.analyze(input.toCharArray()).next;
    Node iter = node;

    if (node == null)
      return null;

    while (iter.next != null) {
      len++;
      iter = iter.next;
    }

    token = new Token[len];

    int i = 0;
    while (node.next != null) {
      token[i] = new Token(node);

      if (token[i].getPos() == null) {
        token[i].setPos(unknownPos);
      }

      i++;
      node = node.next;
    }
    cnt = 0;

    return token;
  }

  /**
   * Get next morpheme.
   * 
   * @return next token. return null when next token doesn't exist.
   */
  public Token next() {
    if (token == null && cnt == token.length)
      return null;
    return token[cnt++];
  }

  /**
   * Check StringTagger have more morphemes or not.
   * 
   * @return true if StringTagger has more morphemes.
   */
  public boolean hasNext() {
    if (token == null && cnt == token.length)
      return false;
    return true;
  }

  /**
   * Read configuration file.
   * 
   * @param confFile
   *          configuration file
   */
  private void readConfig(String confFile) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(confFile));
      NodeList nl = doc.getFirstChild().getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        org.w3c.dom.Node n = nl.item(i);
        if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
          String nn = n.getNodeName();
          String value = n.getFirstChild().getNodeValue();

          if (nn.equals("charset")) {
            charset = value;
          } else if (nn.equals("unknown-pos")) {
            unknownPos = value;
          }

          if (nn.equals("dictionary")) {
            // read nested tag in <dictinary>
            NodeList dnl = n.getChildNodes();
            for (int j = 0; j < dnl.getLength(); j++) {
              org.w3c.dom.Node dn = dnl.item(j);
              if (dn.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

                String dnn = dn.getNodeName();
                if (dn.getFirstChild() == null) {
                  throw new IllegalArgumentException("element '" + dnn
                      + "' is empty");
                }
                String dvalue = dn.getFirstChild().getNodeValue();

                if (dnn.equals("connection-cost")) {
                  connectFile = SenUtils.getPath(dvalue);
                } else if (dnn.equals("double-array-trie")) {
                  doubleArrayFile = SenUtils.getPath(dvalue);
                } else if (dnn.equals("token")) {
                  tokenFile = SenUtils.getPath(dvalue);
                } else if (dnn.equals("pos-info")) {
                  posInfoFile = SenUtils.getPath(dvalue);
                } else {
                  throw new IllegalArgumentException("element '" + dnn
                      + "' is invalid");
                }
              }
            }
          }
        }
      }
    } catch (ParserConfigurationException e) {
      throw new IllegalArgumentException(e.getMessage());
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException(e.getMessage());
    } catch (SAXException e) {
      throw new IllegalArgumentException(e.getMessage());
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

}

