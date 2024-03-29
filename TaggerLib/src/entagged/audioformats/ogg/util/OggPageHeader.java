/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Rapha�l Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package entagged.audioformats.ogg.util;



/**
 *  $Id: OggPageHeader.java,v 1.2 2007/12/05 02:19:18 ericnotthered Exp $
 *
 * @author     Raphael Slinckx (KiKiDonK)
 * @version    16 d�cembre 2003
 */
public class OggPageHeader {
	private double absoluteGranulePosition;
	private byte[] checksum;
	private byte headerTypeFlag;

	private boolean isValid = false;
	private int pageLength = 0;
	private int pageSequenceNumber,streamSerialNumber;
	private byte[] segmentTable;

	public OggPageHeader( byte[] b ) {
		//System.err.println(new String(b, 0 , 4));
		int streamStructureRevision = b[4];
		//System.err.println("streamStructureRevision: " + streamStructureRevision);
		headerTypeFlag = b[5];
		//System.err.println("headerTypeFlag: " + headerTypeFlag);
		if ( streamStructureRevision == 0 ) {
			this.absoluteGranulePosition = 0;  //b[6] + (b[7]<<8) + (b[8]<<16) + (b[9]<<24) + (b[10]<<32) + (b[11]<<40) + (b[12]<<48) + (b[13]<<56);
			for ( int i = 0; i < 8; i++ )
				this.absoluteGranulePosition += u( b[i + 6] ) * Math.pow( 2, 8 * i );

			streamSerialNumber = u(b[14]) + ( u(b[15]) << 8 ) + ( u(b[16]) << 16 ) + ( u(b[17]) << 24 );
			//System.err.println("streamSerialNumber: " + streamSerialNumber);
			pageSequenceNumber = u(b[18]) + (u(b[19]) << 8 ) + ( u(b[20]) << 16 ) + ( u(b[21]) << 24 );
			//System.err.println("pageSequenceNumber: " + pageSequenceNumber);
			checksum = new byte[]{b[22], b[23], b[24], b[25]};


			//int pageSegments = u( b[26] );
			//System.err.println("pageSegments: " + pageSegments);

			this.segmentTable = new byte[b.length - 27];
			//System.err.println("pagesegment length; "+ (b.length-27));
			for ( int i = 0; i < segmentTable.length; i++ ) {
				segmentTable[i] = b[27 + i];
				this.pageLength += u( b[27 + i] );
				//System.err.println("acc page length: "+this.pageLength);
				//System.err.println(segmentTable[i]);
			}

			isValid = true;
		}
	}
	
	private int u(int i) {
		return i & 0xFF;
	}


	public double getAbsoluteGranulePosition() {
		//System.err.println("Number Of Samples: "+absoluteGranulePosition);
		return this.absoluteGranulePosition;
	}


	public byte[] getCheckSum() {
		return checksum;
	}


	public byte getHeaderType() {
		return headerTypeFlag;
	}


	public int getPageLength() {
		//System.err.println("This page length: "+pageLength);
		return this.pageLength;
	}
	
	public int getPageSequence() {
		return pageSequenceNumber;
	}
	
	public int getSerialNumber() {
		return streamSerialNumber;
	}

	public byte[] getSegmentTable() {
	    return this.segmentTable;
	}

	public boolean isValid() {
		return isValid;
	}

	public String toString() {
		String out = "Ogg Page Header:\n";

		out += "Is valid?: " + isValid + " | page length: " + pageLength + "\n";
		out += "Header type: " + headerTypeFlag;
		return out;
	}
}

