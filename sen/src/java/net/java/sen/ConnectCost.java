/*
 * ConnectCost.java - ConnectCost calicurating cost for connected each node.
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

import java.io.File;
import java.io.IOException;

import net.java.sen.io.FileAccessor;
import net.java.sen.io.FileAccessorFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Calicurating cost to connect the node.
 */
public final class ConnectCost {
	private static Log log = LogFactory.getLog(ConnectCost.class);

	private short matrix[];
	private int size1;
	private int size2;
	private int size3;

	/**
	 * Constructor 
	 */
	public ConnectCost(String connectFile) throws IOException {
		FileAccessor fd = null;
		long start;

		File f = new File(connectFile);

		log.info("connection file = " + f.toString());
		start = System.currentTimeMillis();
		fd = FileAccessorFactory.getInstance(f);
		size1 = fd.readShort();
		size2 = fd.readShort();
		size3 = fd.readShort();

        // first 3 value means matrix information:
        // *2 means information is short.
        // each matrix value is short, so / 2

		int len = ((int) f.length() - (3 * 2)) / 2;

		log.debug("size1=" + size1);
		log.debug("size2=" + size2);
		log.debug("size3=" + size3);
		log.debug("matrix size = " + len);

		matrix = new short[len];
		for (int i = 0; i < len; i++) {
			matrix[i] = fd.readShort();
		}
		log.info(
                 "time to load connect cost file = "
                 + (System.currentTimeMillis() - start)
                 + "[ms]");
	}

	/**
	 * get cost from three Node.
	 *
	 * @param lNode2
	 * @param lNode
	 * @param rNode
	 */
	int getCost(Node lNode2, Node lNode, Node rNode) {
		int pos =
			size3 * (size2 * lNode2.token.rcAttr2 + lNode.token.rcAttr1)
            + rNode.token.lcAttr;
		return matrix[pos] + rNode.token.cost;

        // above code means matrix is in memory.
        // if you hesitate consuming a lot of memory, you can use
        // following code. 
        /*
        try {
            fd.seek(pos*2+(3*2));
            short val = fd.readShort();
            if(log.isTraceEnabled()){
                log.trace("cost = " + val);
            }
            return val;
        } catch (IOException e){
            throw new RuntimeException(e.toString());
        }
        */
	}
}
