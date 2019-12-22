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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.Level;

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

	private BufferedWriter outputFile;
	private LoggerOut IOThread;
	private String destination;

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

	private String parseFileString(String file) {
		if (file.charAt(0) == '*') {
			boolean preName = file.charAt(1) == '*';
			String pre = DragonAPICore.getMinecraftDirectoryString()+"/logs/";
			file = file.replaceFirst("\\*", pre);
			if (preName)
				file = file.replaceFirst("\\*", mod.getDisplayName());
		}
		return file;
	}

	private void reloadConfigs() {
		logLoading = DragonOptions.LOGLOADING.getState();
		printDebug = DragonOptions.DEBUGMODE.getState();
	}

	/** Preface with '*' to use the log folder as a parent and preface with an additional '*' to preface the mod name. */
	public ModLogger setOutput(String file) {
		file = this.parseFileString(file);
		try {
			this.flushOutput();
			File f = new File(file);
			if (f.exists())
				f.delete();
			File par = new File(f.getParent());
			if (!par.exists())
				par.mkdirs();
			f.createNewFile();
			destination = f.getAbsolutePath();
			this.setOutput(new BufferedWriter(new PrintWriter(f)));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public ModLogger setOutput(OutputStream out) {
		this.flushOutput();
		destination = out.getClass().getSimpleName()+" OutputStream '"+out+"'";
		this.setOutput(new BufferedWriter(new PrintWriter(out)));
		return this;
	}

	private void setOutput(BufferedWriter buf) {
		this.logChange();
		outputFile = buf;
		IOThread = new LoggerOut();
		Thread th = new Thread(IOThread);
		th.setDaemon(true);
		th.setName(mod.getDisplayName()+" - Custom I/O Logger");
		th.start();
	}

	private void logChange() {
		this.log("===============================================================================================================================");
		this.log("Logging is being redirected to "+destination+". Check there for any and all logging information including debugging and errors!");
		this.log("===============================================================================================================================");
	}

	private void flushOutput() {
		if (outputFile != null) {
			IOThread.terminated = true;
		}
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
		if (outputFile != null) {
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

	private class LogLine {

		private final String message;
		private final Level level;
		private final Thread sender;
		private final long time;

		private LogLine(String s, Level l) {
			message = s;
			sender = Thread.currentThread();
			level = l;
			time = System.currentTimeMillis();
		}

		@Override
		public String toString() {
			return this.parseTime()+" "+this.parseThread()+": "+message+NEWLINE;
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

	}

	private class LoggerOut implements Runnable {

		private ConcurrentLinkedQueue<LogLine> messages = new ConcurrentLinkedQueue();
		private boolean terminated = false;

		private LoggerOut() {

		}

		private void addMessage(String s, Level l) {
			messages.add(new LogLine(s, l));
		}

		@Override
		public void run() {
			while (!terminated || !messages.isEmpty()) { //killed by MC if closes (deamon thread)
				LogLine current = null;
				try {
					while (!messages.isEmpty()) {
						current = messages.remove();
						outputFile.write(current.toString());
					}
					if (current != null)
						outputFile.flush();
					Thread.sleep(50);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					ReikaJavaLibrary.pConsole("ERROR: Could not output logger line to its IO destination '"+destination+"'!");
					ReikaJavaLibrary.pConsole(current.level, current.message);
					e.printStackTrace();
				}
			}
			try {
				outputFile.close();
			}
			catch (IOException e) {
				e.printStackTrace();
				ReikaJavaLibrary.pConsole("ERROR: Could not close logger stream!");
			}
		}

		@Override
		public String toString() {
			return messages.size()+" Messages from "+mod.getDisplayName()+": {"+messages+"}";
		}

	}

}
