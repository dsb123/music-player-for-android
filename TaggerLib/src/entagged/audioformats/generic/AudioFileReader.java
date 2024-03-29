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
package entagged.audioformats.generic;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import entagged.audioformats.AudioFile;
import entagged.audioformats.EncodingInfo;
import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;

/*
 * This abstract class is the skeleton for tag readers. It handles the
 * creation/closing of the randomaccessfile objects and then call the subclass
 * method getEncodingInfo and getTag. These two method have to be implemented in
 * te subclass.
 * 
 * @author Raphael Slinckx
 * 
 * @version $Id: AudioFileReader.java,v 1.1.2.1 2007/11/14 01:36:14
 *          ericnotthered Exp $
 * @since v0.02
 */
public abstract class AudioFileReader {

	/*
	 * Returns the encoding info object associated wih the current File. The
	 * subclass can assume the RAF pointer is at the first byte of the file. The
	 * RandomAccessFile must be kept open after this function, but can point at
	 * any offset in the file.
	 * 
	 * @param raf The RandomAccessFile associtaed with the current file
	 * @exception IOException is thrown when the RandomAccessFile operations
	 * throw it (you should never throw them manually) @exception
	 * CannotReadException when an error occured during the parsing of the
	 * encoding infos
	 */
	protected abstract EncodingInfo getEncodingInfo(RandomAccessFile raf)
			throws CannotReadException, IOException;

	/*
	 * Same as above but returns the Tag contained in the file, or a new one.
	 * 
	 * @param raf The RandomAccessFile associted with the current file
	 * @exception IOException is thrown when the RandomAccessFile operations
	 * throw it (you should never throw them manually) @exception
	 * CannotReadException when an error occured during the parsing of the tag
	 */
	protected abstract Tag getTag(RandomAccessFile raf)
			throws CannotReadException, IOException;

	/*
	 * Reads the given file, and return an AudioFile object containing the Tag
	 * and the encoding infos present in the file. If the file has no tag, an
	 * empty one is returned. If the encodinginfo is not valid , an error is
	 * thrown.
	 * 
	 * @param f The file to read @exception CannotReadException If anything went
	 * bad during the read of this file
	 */
	public AudioFile read(File f) throws CannotReadException {
		if (!f.canRead())
			throw new CannotReadException("Can't read file \""
					+ f.getAbsolutePath() + "\"");

		if (f.length() <= 150)
			throw new CannotReadException("Less than 150 byte \""
					+ f.getAbsolutePath() + "\"");

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(f, "r");
			raf.seek(0);

			EncodingInfo info = getEncodingInfo(raf);

			Tag tag;
			try {
				raf.seek(0);
				tag = getTag(raf);
			} catch (CannotReadException e) {
				System.err.println(e.getMessage());
				tag = new GenericTag();
			}

			return new AudioFile(f, info, tag);

		} catch (Exception e) {
			throw new CannotReadException("\"" + f + "\" :" + e, e);
		} finally {
			try {
				if (raf != null)
					raf.close();
			} catch (Exception ex) {
				System.err.println("\"" + f + "\" :" + ex);
			}
		}
	}

}
