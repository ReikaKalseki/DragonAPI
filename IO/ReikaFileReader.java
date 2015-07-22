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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;

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

	public static BufferedReader getReader(URL url, int timeout, ConnectionErrorHandler ch, DataFetcher f) {
		if (!isInternetAccessible(timeout)) {
			ch.onNoInternet();
			return null;
		}

		try {
			URLConnection c = url.openConnection();
			c.setConnectTimeout(timeout);
			if (f != null) {
				try {
					f.fetchData(c);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			return new BufferedReader(new InputStreamReader(c.getInputStream()));
		}
		catch (UnknownHostException e) { //Server not found
			ch.onServerNotFound();
		}
		catch (ConnectException e) { //Redirect/tampering
			ch.onServerRedirected();
		}
		catch (SocketTimeoutException e) { //Slow internet, cannot load a text file...
			ch.onTimedOut();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean isInternetAccessible(int timeout) {
		try {
			URLConnection c = new URL("http://www.google.com").openConnection();
			c.setConnectTimeout(timeout);
			((HttpURLConnection)c).getResponseCode();
			return true;
		}
		catch (IOException ex) {
			return false;
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

	public static ArrayList<String> getFileAsLines(URL url, int timeout, boolean printStackTrace, ConnectionErrorHandler ch) {
		return getFileAsLines(url, timeout, printStackTrace, ch, null);
	}

	public static ArrayList<String> getFileAsLines(URL url, int timeout, boolean printStackTrace, ConnectionErrorHandler ch, DataFetcher f) {
		BufferedReader r = getReader(url, timeout, ch, f);
		return r != null ? getFileAsLines(r, printStackTrace) : null;
	}

	public static ArrayList<String> getFileAsLines(File f, boolean printStackTrace) {
		return getFileAsLines(getReader(f), printStackTrace);
	}

	public static ArrayList<String> getFileAsLines(BufferedReader r, boolean printStackTrace) {
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

	public static void writeLinesToFile(String s, ArrayList<String> li, boolean printStackTrace) {
		writeLinesToFile(new File(s), li, printStackTrace);
	}

	public static void writeLinesToFile(File f, ArrayList<String> li, boolean printStackTrace) {
		try {
			writeLinesToFile(new BufferedWriter(new PrintWriter(f)), li, printStackTrace);
		}
		catch (IOException e) {
			if (printStackTrace) {
				e.printStackTrace();
			}
		}
	}

	public static void writeLinesToFile(BufferedWriter p, ArrayList<String> li, boolean printStackTrace) {
		String sep = System.getProperty("line.separator");
		try {
			for (String s : li) {
				p.write(s+sep);
			}
			p.flush();
			p.close();
		}
		catch (IOException e) {
			if (printStackTrace) {
				e.printStackTrace();
			}
		}
	}

	public static String getHash(String path, HashType type) {
		return getHash(new File(path), type);
	}

	public static String getHash(File file, HashType type) {
		try {
			return getHash(new FileInputStream(file), type);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getHash(InputStream is, HashType type) {
		StringBuffer sb = new StringBuffer();
		try {
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance(type.tag);
			int numRead;

			do {
				numRead = is.read(buffer);
				if (numRead > 0)
					complete.update(buffer, 0, numRead);
			}
			while (numRead != -1);

			is.close();
			byte[] hash = complete.digest();

			for (int i = 0; i < hash.length; i++) {
				sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1).toUpperCase());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			sb.append("IO ERROR: ");
			sb.append(e.toString());
		}
		return sb.toString();
	}

	public static interface ConnectionErrorHandler {

		void onServerRedirected();
		void onTimedOut();
		void onNoInternet();
		void onServerNotFound();

	}

	public static interface DataFetcher {

		void fetchData(URLConnection c) throws Exception;

	}

	public static enum HashType {
		MD5("MD5"),
		SHA1("SHA-1"),
		SHA256("SHA-256");

		private final String tag;

		private HashType(String s) {
			tag = s;
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

	public static InputStream getFileInsideJar(File f, String name) {
		try {
			return getFileInsideJar(new JarFile(f), name);
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream getFileInsideJar(JarFile jar, String name) {
		try {
			return jar.getInputStream(jar.getEntry(name));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void deleteFolderWithContents(File f) {
		deleteFolderWithContents(f, 10);
	}

	public static void deleteFolderWithContents(File f, int tries) {
		deleteFolderWithContents(f, tries, 0);
	}

	private static void deleteFolderWithContents(File f, int tries, int attempt) {
		try {
			FileUtils.deleteDirectory(f);
		}
		catch (Exception e) {
			if (tries > attempt)
				deleteFolderWithContents(f, tries, attempt+1);
			else
				e.printStackTrace();
		}
	}
}
