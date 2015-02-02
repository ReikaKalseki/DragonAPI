/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.net.URLConnection;
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

	public static BufferedReader getReader(File f) {
		try {
			return new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedReader getReader(String path) {
		try {
			return new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedReader getReader(URL url, int timeout) {
		try {
			URLConnection c = url.openConnection();
			c.setConnectTimeout(timeout);
			return new BufferedReader(new InputStreamReader(c.getInputStream()));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedReader getURLReader(String url, int timeout) {
		try {
			return getReader(new URL(url), timeout);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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

	public static ArrayList<String> getFileAsLines(String path, boolean printStackTrace) {
		return getFileAsLines(getReader(path), printStackTrace);
	}

	public static ArrayList<String> getFileAsLines(URL url, int timeout, boolean printStackTrace) {
		return getFileAsLines(getReader(url, timeout), printStackTrace);
	}

	public static ArrayList<String> getFileAsLines(File f, boolean printStackTrace) {
		return getFileAsLines(getReader(f), printStackTrace);
	}

	private static ArrayList<String> getFileAsLines(BufferedReader r, boolean printStackTrace) {
		ArrayList<String> li = new ArrayList();
		String line = "";
		try {
			while (line != null) {
				line = r.readLine();
				if (line != null) {
					li.add(line);
				}
			}
			r.close();
		}
		catch (Exception e) {
			if (printStackTrace)
				e.printStackTrace();
		}
		return li;
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

	/** Edits individual lines matching in a file if they match a given criterion. */
	public static abstract class LineEditor {

		/** Attempt line editing? */
		public abstract boolean editLine(String s);

		/** The line used to replace strings that match the criteria. Args: Original line, newline separator */
		protected abstract String getReplacementLine(String s, String newline);

		public final boolean performChanges(File f) {
			try {
				BufferedReader r = new BufferedReader(new FileReader(f));
				String sep = System.getProperty("line.separator");
				String line = r.readLine();
				StringBuilder out = new StringBuilder();
				while (line != null) {
					String rep = this.editLine(line) ? this.getReplacementLine(line, sep) : line;
					if (rep == null) {

					}
					else {
						out.append(rep+sep);
					}
					line = r.readLine();
				}
				r.close();
				FileOutputStream os = new FileOutputStream(f);
				os.write(out.toString().getBytes());
				os.close();
				return true;
			}
			catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

	}

}
