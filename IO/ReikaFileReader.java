/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.util.ArrayList;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ReikaFileReader extends DragonAPICore {

	public static int getFileLength(File f) {
		int len;
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(f));
			lnr.skip(Long.MAX_VALUE);
			len = lnr.getLineNumber()+1+1;
			lnr.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load file data due to "+e.getCause()+" and "+e.getClass()+" !");
		}
		return len;
	}

	/** Gets all files with the given extension in a directory and any subdirectories. */
	public static ArrayList<File> getAllFilesInFolder(File f, String... ext) {
		ArrayList<File> li = new ArrayList();
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				File in = files[i];
				if (in.isDirectory()) {
					li.addAll(getAllFilesInFolder(in, ext));
				}
				else {
					if (ext == null) {
						li.add(in);
					}
					else {
						for (int k = 0; k < ext.length; k++) {
							if (in.getName().endsWith(ext[k])) {
								li.add(in);
							}
						}
					}
				}
			}
		}
		return li;
	}

	/** Gets all files in a directory and any subdirectories. */
	public static ArrayList<File> getAllFilesInFolder(File f) {
		return getAllFilesInFolder(f, null);
	}

	public static String readTextFile(Class root, String path) {
		InputStream in = root.getResourceAsStream(path);
		StringBuilder sb = new StringBuilder();
		BufferedReader p;
		try {
			p = new BufferedReader(new InputStreamReader(in));
		}
		catch (NullPointerException e) {
			ReikaJavaLibrary.pConsole("File "+path+" does not exist!");
			return sb.toString();
		}
		int i = 0;
		try {
			String line = null;
			while((line = p.readLine()) != null) {
				if (!line.isEmpty()) {
					sb.append(line);
					i++;
					sb.append("\n");
				}
			}
			p.close();
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole(e.getMessage()+" on loading line "+i);
		}
		return sb.toString();
	}

	public static String getHash(String path) {
		return getHash(new File(path));
	}

	public static String getHash(File file) {
		try {
			InputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;

			do {
				numRead = fis.read(buffer);
				if (numRead > 0)
					complete.update(buffer, 0, numRead);
			}
			while (numRead != -1);

			fis.close();
			byte[] hash = complete.digest();

			String result = "";

			for (int i = 0; i < hash.length; i++) {
				result += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
			}

			return result.toUpperCase();
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
