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

/**
 *  
 *
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
final public class Token {
    private Node node;
    // cache for each value.
    private String pos = null;
    private String pronunciation = null;
    private String basic = null;
    private String read = null;
    private String nodeStr = null;

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
     * get un-conjugate string. This method is thread unsafe.
     * 
     * @return un-conjugate representation for morpheme.
     */ 
    public String getBasicString() {
    	if (basic == null)
    		basic = node.getBasicString();
    	return basic;
    }

    /**
     * get reading. This method is thread unsafe.
     */
    public String getReading(){
    	if (read == null)
    		read = node.getReading();
        return read;
    }

    /**
     * get pronunciation. This method is thread unsafe.
     */
    public String getPronunciation(){
    	if (pronunciation == null)
    		pronunciation = node.getPronunciation();
        return pronunciation;
    }

    /**
     * get string representation. This method is thread unsafe.
     */
    public String toString() {
    	if (nodeStr == null)
    		nodeStr = node.toString();
		return nodeStr;
    }
}
