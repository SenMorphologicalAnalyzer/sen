/*
 * Token.java - Token which is used at Viterbi
 * 
 * Copyright (C) 2001, 2002 Taku Kudoh, Takashi Okamoto
 * Taku Kudoh <taku-ku@is.aist-nara.ac.jp>
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
 * 
 */

package net.java.sen;


final public class Token {
    private Node node;
    private String pos = null;

    /**
     * token which represents morpheme.
     */
    protected Token(Node n){
        node = n;
        pos = node.getPos();
    }

    /**
     * get start index of this token.
     */
    public int start(){
        return node.begin;
    }

    /**
     * get end index of this token.
     */
    public int end() {
        return node.begin + node.length;
    }

    /**
     * get length of this token.
     */
    public int length() {
        return node.length;
    }

    /**
     * get part of speech.  
     * @return part of speech which represents this token.
     */
    public String getPos() {
        return pos;
    }

    /**
     * set part of speech.
     * 
     * @pos part of speech.
     */
    protected void setPos(String pos) {
        this.pos = pos;
    }

    /**
     * get un-conjugate string.
     * 
     * @return un-conjugate representation for morpheme.
     */ 
    public String getBasicString() {
        return node.getBasicString();
    }

    /**
     * get reading.
     */
    public String getReading(){
        return node.getReading();
    }

    /**
     * get pronunciation.
     */
    public String getPronunciation(){
        return node.getPronunciation();
    }

    /**
     * get string representation.
     */
    public String toString() {
        return node.toString();
    }
}
