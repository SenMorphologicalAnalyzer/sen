/*
 * StreamTagger.java - generate tag from Stream.
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
 * License along with Sen; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */
package net.java.sen;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

/**
 * This class generate morpheme tags from Stream.
 */
public class StreamTagger {

  private StringTagger stringTagger = null;
  private static final int BUFFER_SIZE = 64;
  private final char[] buffer = new char[BUFFER_SIZE];
  private int cnt = 0;
  private Token[] token;
  private boolean complete = false;
  private Reader reader;
  public StreamTagger() {
  }

  /**
   * Construct new StreamTagger. Currently only support Locale.JAPANESE.
   * 
   * @param reader
   *          Reader to add tag.
   * @param locale
   *          Locale to generate morphological analyzer.
   *  
   */
  public StreamTagger(Reader reader, Locale locale) throws IOException,
      IllegalArgumentException {
    stringTagger = StringTagger.getInstance(locale);
    this.reader = reader;
  }

  /**
   * Check have more morphemes or not.
   * 
   * @return true if StreamTagger has more morphemes.
   */
  public boolean hasNext() throws IOException {
    if (token == null || token.length == cnt) {
      int i;

      do {
        if ((i = readToBuffer()) == -1)
          return false;
        token = stringTagger.analyze(new String(buffer, 0, i));
      } while (token == null);
      cnt = 0;

    }

    // when this is happend?
    if (token.length == 0)
      return false;

    return true;
  }

  private int readToBuffer() throws IOException {
    int pos = 0;
    int res = 0;

    while ((pos < BUFFER_SIZE) && (!complete)
        && ((res = reader.read(buffer, pos, 1)) != -1)) {
      switch (Character.getType(buffer[pos])) {
        case Character.OTHER_PUNCTUATION :
          if (pos == 0)
            continue;
          return pos + 1;
        default :
          pos++;
      }
    }

    if (res == -1) {
      complete = true;
    }

    if (complete && pos == 0) {
      return -1;
    }

    return pos;
  }

  /**
   * Get next morpheme.
   * 
   * @return morpheme
   * @throws IOException
   */
  public Token next() throws IOException {
    if (token == null || token.length == cnt) {
      int i;

      do {
        if ((i = readToBuffer()) == -1)
          return null;
        token = stringTagger.analyze(new String(buffer, 0, i));
      } while (token == null);
      cnt = 0;

    }
    return token[cnt++];
  }
}

