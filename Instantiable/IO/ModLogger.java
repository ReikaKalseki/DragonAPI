/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.Level;

import com.google.common.base.Charsets;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ModLogger {

	private boolean logLoading;
	private boolean printDebug;
	private final boolean shouldWarn;

	private final DragonAPIMod mod;

	private static boolean logAll = false;
	private static boolean logNone = false;

	private LoggerOut IOThread;

	private static final String NEWLINE = System.getProperty("line.separator");

	private static final ArrayList<ModLogger> loggers = new ArrayList();

	public ModLogger(DragonAPIMod mod, boolean warn) {
		this.mod = mod;
		logLoading = DragonOptions.LOGLOADING.getState();
		printDebug = DragonOptions.DEBUGMODE.getState();
		shouldWarn = warn;
		if (mod == null)
			throw new IllegalArgumentException("Cannot create a logger for a null mod!");
		if (this.shouldLog())
			ReikaJavaLibrary.pConsole(mod.getTechnicalName()+": Creating logger. Log Loading: "+logLoading+"; Debug mode: "+printDebug+"; Warnings: "+warn);
		loggers.add(this);
	}

	private File parseFileString(String file) {
		if (file.charAt(0) == '*') {
			File log = new File(DragonAPICore.getMinecraftDirectory(), "logs");
			file = file.substring(1);
			boolean preName = file.charAt(0) == '*';
			String pre = "";
			if (preName) {
				pre = mod.getDisplayName();
				file = file.substring(1);
			}
			return new File(log, pre+file);
		}
		return new File(file);
	}

	private void reloadConfigs() {
		logLoading = DragonOptions.LOGLOADING.getState();
		printDebug = DragonOptions.DEBUGMODE.getState();
	}

	/** Preface with '*' to use the log folder as a parent and preface with an additional '*' to preface the mod name. */
	public ModLogger setOutput(String file) {
		File f = this.parseFileString(file);
		if (f.exists())
			f.delete();
		File par = new File(f.getParent());
		if (!par.exists())
			par.mkdirs();
		this.logChange(f);
		IOThread = new LoggerOut(mod.getDisplayName()+" - Custom I/O Logger", f);
		IOThread.start();
		return this;
	}

	private void logChange(File f) {
		this.log("===============================================================================================================================");
		this.log("Logging is being redirected to "+f.getAbsolutePath()+". Check there for any and all logging information including debugging and errors!");
		this.log("===============================================================================================================================");
	}

	public void debug(Object o) {
		if (this.shouldDebug()) {
			this.write(Level.INFO, mod.getTechnicalName()+" DEBUG: "+o);
			ReikaChatHelper.write("DEBUG: "+o);
		}
	}

	public void log(Object o) {
		if (this.shouldLog())
			this.write(Level.INFO, mod.getTechnicalName()+": "+o);
	}

	public void logError(Object o) {
		this.write(Level.ERROR, mod.getTechnicalName()+" ERROR: "+o);
	}

	private void write(Level l, String s) {
		if (IOThread != null) {
			IOThread.addMessage(s, l);
		}
		else
			ReikaJavaLibrary.pConsole(l, s);
	}

	public boolean shouldLog() {
		if (logNone)
			return false;
		if (logAll)
			return true;
		return logLoading;
	}

	public boolean shouldDebug() {
		//if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment())
		//return true;
		if (logNone)
			return false;
		if (logAll)
			return true;
		return printDebug;
	}

	public boolean shouldWarn() {
		return shouldWarn;
	}

	public void warn(Object o) {
		if (this.shouldWarn()) {
			this.write(Level.WARN, o.toString());
			ReikaChatHelper.write(o);
		}
	}

	public static void setAllLoggingTrue() {
		logAll = true;
		logNone = false;
	}

	public static void setAllLoggingFalse() {
		logAll = false;
		logNone = true;
	}

	public static void setAllLoggingDefault() {
		logNone = false;
		logAll = false;
	}

	public static int getActiveLoggers() {
		return loggers.size();
	}

	public static void reloadLoggers() {
		for (ModLogger m : loggers) {
			m.reloadConfigs();
		}
	}

	private static class LogLine implements CharSequence {

		private final String message;
		private final Level level;
		private final Thread sender;
		private final long time;
		private final String stringValue;

		private LogLine(String s, Level l) {
			message = s;
			sender = Thread.currentThread();
			level = l;
			time = System.currentTimeMillis();
			stringValue = this.parseTime()+" "+this.parseThread()+": "+message+NEWLINE;
		}

		@Override
		public String toString() {
			return stringValue;
		}

		private String parseTime() {
			return "["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(time))+"]";
		}

		private String parseThread() {
			return "["+sender+" ("+sender.getState()+")/"+level+"]";
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof LogLine) {
				LogLine l = (LogLine)o;
				return l.message.equals(message) && l.time == time && l.level == level && l.sender == sender;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (int)time;
		}

		@Override
		public int length() {
			return stringValue.length();
		}

		@Override
		public char charAt(int index) {
			return stringValue.charAt(index);
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return stringValue.subSequence(start, end);
		}

	}

	public static class LoggerOut extends Thread {

		private final File outputFile;
		private ConcurrentLinkedQueue<LogLine> messages = new ConcurrentLinkedQueue();

		public LoggerOut(String n, File f) {
			outputFile = f;
			this.setName(n);
			this.setDaemon(true);
		}

		public void addMessage(String s, Level l) {
			messages.add(new LogLine(s, l));
		}

		@Override
		public void run() {
			while (!messages.isEmpty()) { //killed by MC if closes (deamon thread)
				try {
					java.nio.file.Files.write(outputFile.toPath(), messages, Charsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
					messages.clear();
					Thread.sleep(50);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					ReikaJavaLibrary.pConsole("ERROR: Could not output logger contents line to its IO destination '"+outputFile+"'!");
					ReikaJavaLibrary.pConsole(messages);
					messages.add(new LogLine("ERROR WRITING: "+e.toString(), Level.ERROR));
					e.printStackTrace();
				}
			}
		}

		@Override
		public String toString() {
			return messages.size()+" Messages from "+this.getName()+": {"+messages+"}";
		}

	}

}
