package net.java.sen;

/*
 * Node.java - Node which is representation of the morpheme.
 * 
 * Copyright (C) 2001, 2002 Taku Kudoh, Takashi Okamoto Taku Kudoh
 * <taku-ku@is.aist-nara.ac.jp> Takashi Okamoto <tora@debian.org>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final public class Node {
  private static Log log = LogFactory.getLog(Node.class);

  public CToken token = null; // token itself
  public Node prev = null; // link for previous context
  public Node next = null; // link for next context
  public Node lnext = null;
  public Node rnext = null;

  // surface, no need to be NULL terminated, use length if needed
  public char surface[] = null;

  // POS, sub-POS, cfrom ... etc, must be NULL terminated
  public String termInfo = null;

  public int begin = 0; // begining of position
  public int length = 0; // length of surface

  public int end = 0; // next seek position
  // (causion: 'end' doesn't work correctly)

  public int cost = 0; // cost of best path
  public int id = 0; // unique id of this node

  /**
   * get start index of this node.
   */
  public int start() {
    return begin;
  }

  /**
   * get end index of this node.
   */
  public int end() {
    return begin + length;
  }

  /**
   * get length of this node.
   */
  public int length() {
    return length;
  }

  /**
   * get part of speech as chasen format.
   * 
   * @return part of speach.
   */
  public String getPos() {
    int cnt = 0;

    if (termInfo == null)
      return null;

    // to avoid bug.
    if (termInfo.length() == 0) {
      log.error("feature information is null at '" + toString() + "'.");
      log.error("token id = " + this.token.posID);
      log.error("token rcAttr2 = " + this.token.rcAttr2);
      log.error("token rcAttr1 = " + this.token.rcAttr1);
      log.error("token lcAttr = " + this.token.lcAttr);
      log.error("token length = " + this.token.length);
      log.error("token cost = " + this.token.cost);
      return null;
    }

    while (termInfo.charAt(cnt++) != ',');
    if (termInfo.charAt(cnt) != '*') {
      while (termInfo.charAt(cnt++) != ',');
      if (termInfo.charAt(cnt) != '*') {
        while (termInfo.charAt(cnt++) != ',');
        if (termInfo.charAt(cnt) != '*') {
          while (termInfo.charAt(cnt++) != ',');
        }
      }
    }

    // convert to chasen format
    return termInfo.substring(0, cnt - 1).replace(',', '-');
  }

  /**
   * get un-conjugate string.
   * 
   * @return un-conjugate representation for morpheme.
   */
  public String getBasicString() {
    int cnt = 0, begin;

    if (termInfo == null)
      return toString();

    // to avoid bug.
    if (termInfo.length() == 0) {
      log.error("feature information is null at '" + toString() + "'.");
      log.error("token id = " + this.token.posID);
      log.error("token rcAttr2 = " + this.token.rcAttr2);
      log.error("token rcAttr1 = " + this.token.rcAttr1);
      log.error("token lcAttr = " + this.token.lcAttr);
      log.error("token length = " + this.token.length);
      log.error("token cost = " + this.token.cost);

      return null;
    }
    log.debug("posInfo=" + termInfo);
    for (int i = 0; i < 6; i++) {
      while (termInfo.charAt(cnt++) != ',');
    }
    begin = cnt;
    while (termInfo.charAt(cnt) != ',')
      cnt++;
    return termInfo.substring(begin, cnt);
  }

  /**
   * clear node.
   */
  protected void clear() {
    token = null;
    prev = null;
    next = null;
    lnext = null;
    rnext = null;
    surface = null;
    termInfo = null;
    begin = 0;
    length = 0;
    end = 0;
    cost = 0;
    id = 0;
  }

  /**
   * copy node.
   */
  protected void copy(Node org) {
    token = org.token;
    prev = org.prev;
    next = org.next;
    lnext = org.lnext;
    rnext = org.rnext;
    surface = org.surface;
    termInfo = org.termInfo;
    begin = org.begin;
    length = org.length;
    end = org.end;
    cost = org.cost;
    id = org.id;
  }

  /**
   * convert to string.
   */
  public String toString() {
    if (surface != null) {
      return new String(surface, begin, length);
    } else {
      return null;
    }
  }
  public String getReading() {
    if (termInfo == null || termInfo.length() == 0)
      return null;

    int begin = getFieldBegin(7);
    int end = getFieldBegin(8);

    return termInfo.substring(begin, end - 1);
  }

  public String getPronunciation() {
    if (termInfo == null || termInfo.length() == 0)
      return null;

    int begin = getFieldBegin(8);
    int end = termInfo.length();

    return termInfo.substring(begin, end);
  }

  private int getFieldBegin(int pos) {
    int cnt = 0;
    int ptr = 0;

    while (cnt < pos && ptr < termInfo.length()) {
      if (termInfo.charAt(ptr++) == ',')
        cnt++;
    }

    if (cnt != pos)
      return -1;

    return ptr++;
  }
}

