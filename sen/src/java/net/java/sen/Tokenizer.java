/*
 * Tokenizer.java - get character token.
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

import java.io.IOException;

public abstract  class Tokenizer 
{
    private int id;

    private Node  bosNode2 = new Node();
    private Node  eosNode = new Node();
    public Dictionary dic = null; 
    public CToken bosToken = null;
    public CToken bosToken2 = new CToken();
    public CToken eosToken = null;
    public CToken unknownToken = new CToken();
    public Node  bosNode = new Node();

    public int skipCharClass (char[] c, int begin, int end, 
                              int char_class, int fail[])
    {
        int p = begin;

        while (p != end && (fail[0] = getCharClass (c[p])) == char_class) p++;

        // -- fixme -- 
        if(p==end)fail[0] = 0;

        return p;
    }

    public int skipCharClass (char c[], int begin, int end, int char_class)
    {
        int p = begin;

        while (p != end && getCharClass (c[p]) == char_class) p++;
        return p;
    }
     
    public abstract int getCharClass (char c);

    abstract public Node lookup (char c[], int begin) throws IOException;

    public void clear () { id = 0; }
     
    public Node getNewNode () 
    { 
        Node node = new Node();
        node.id = id++;
        return node;
    }

    Node getBOSNode ()
    {
        bosNode.clear();
        bosNode2.clear();

        bosNode.prev    = bosNode2;
        bosNode.surface = bosNode2.surface = null;
        bosNode.length  = bosNode2.length  = 0;
        bosNode.token   = bosToken;
        bosNode2.token  = bosToken2;

        return bosNode;
    }

    Node getEOSNode ()
    {
        eosNode.clear();

        eosNode.surface = null;
        eosNode.length  = 1;
        eosNode.token   = eosToken;
        return eosNode;
    }

    public boolean close () 
    {
        return dic.close ();
    }

	public Tokenizer(String tokenFile, 
                     String doubleArrayFile,
                     String posInfoFile,
                     String charset
                     ) throws IOException
    {
        dic = new Dictionary(tokenFile, doubleArrayFile, posInfoFile, charset);

        bosToken = dic.getBOSToken();
        bosToken2 = dic.getBOSToken();
        eosToken = dic.getEOSToken();
        unknownToken = dic.getUnknownToken();
    }
}

