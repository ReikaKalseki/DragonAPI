/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;

public class LoggingFilters {

	private static final Filter mismatchFilter = new ItemBlockMismatchFilter();
	private static final Filter soundLoaderFilter = new CustomSoundLoaderFilter();
	private static final Filter noTextureFilter = new MissingTextureFilter();

	public abstract static class CoreFilter implements Filter {

		private static boolean skipNext = false;
		protected final LoggerType type;

		protected CoreFilter(LoggerType log) {
			type = log;
		}

		@Override
		public final Result getOnMismatch() {
			return Result.NEUTRAL;
		}

		@Override
		public final Result getOnMatch() {
			return Result.NEUTRAL;
		}

		@Override
		public final Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
			return Result.NEUTRAL;
		}

		@Override
		public final Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
			return Result.NEUTRAL;
		}

		@Override
		public final Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
			return Result.NEUTRAL;
		}

		@Override
		public final Result filter(LogEvent event) {
			if (skipNext) {
				return Result.NEUTRAL;
			}
			String msg = this.parse(event.getMessage(), event.getLevel());
			if (msg == null) {
				return Result.DENY;
			}
			else if (!msg.isEmpty()) {
				skipNext = true;
				getLogger(type).log(event.getLevel(), msg);
				skipNext = false;
				return Result.DENY;
			}
			else {
				return Result.NEUTRAL;
			}
		}

		/** Return null to silence the message. Return anything else to replace the message, except an empty string for "original message" */
		protected abstract String parse(Message msg, Level lvl);
	}

	public static final class ReplyFilter extends CoreFilter {

		private final String input;
		private final String reply;

		private final boolean sameStream;

		private boolean nextPrint;

		public ReplyFilter(Logger log, String seek, String msg) {
			super(null);
			input = seek;
			reply = msg;

			sameStream = log == getLogger(LoggerType.FML);
		}

		@Override
		protected final String parse(Message msg, Level lvl) {
			String sg = msg.getFormattedMessage();
			if (nextPrint) {
				nextPrint = false;
				ReikaJavaLibrary.pConsole(reply);
			}
			if (sg.contains(input)) {
				if (sameStream)
					nextPrint = true;
				else
					ReikaJavaLibrary.pConsole(reply);
			}
			return "";
		}

	}

	public static abstract class ReplyWithContextFilter extends CoreFilter {

		private final String input;

		private final boolean sameStream;

		private boolean nextPrint;

		public ReplyWithContextFilter(Logger log, String seek) {
			super(null);
			input = seek;

			sameStream = log == getLogger(LoggerType.FML);
		}

		@Override
		protected final String parse(Message msg, Level lvl) {
			String sg = msg.getFormattedMessage();
			if (nextPrint) {
				nextPrint = false;
				ReikaJavaLibrary.pConsole(this.getReply(sg));
			}
			if (sg.contains(input)) {
				if (sameStream)
					nextPrint = true;
				else
					ReikaJavaLibrary.pConsole(this.getReply(sg));
			}
			return "";
		}

		protected abstract String getReply(String msg);

	}

	private static class ItemBlockMismatchFilter extends CoreFilter {

		private final String PREFIX = "Block <-> ItemBlock name mismatch";
		private final String TAG = "DragonAPI:minecraft";

		private int counter = 0;

		private ItemBlockMismatchFilter() {
			super(LoggerType.FML);
		}

		@Override
		protected String parse(Message msg, Level lvl) {
			if (counter > 0) {
				counter--;
				return null;
			}
			String sg = msg.getFormattedMessage();
			if (sg.contains(PREFIX) && sg.contains(TAG)) {
				counter = 7;
				return null;
			}
			return "";
		}

	}

	private static class CustomSoundLoaderFilter extends CoreFilter {

		private final String PREFIX = "Invalid sounds.json";

		private CustomSoundLoaderFilter() {
			super(LoggerType.SOUND);
		}


		@Override
		protected String parse(Message msg, Level lvl) {
			String sg = msg.getFormattedMessage();
			if (sg.contains(PREFIX)) {
				return null;
			}
			return "";
		}

	}

	private static class MissingTextureFilter extends CoreFilter {

		private final String PREFIX = "Using missing texture, unable to load";

		private MissingTextureFilter() {
			super(LoggerType.TEXTURE);
		}

		@Override
		protected String parse(Message msg, Level lvl) {
			String sg = msg.getFormattedMessage();
			if (sg.contains(PREFIX)) {
				String tex = sg.substring(PREFIX.length()+1);
				ReikaJavaLibrary.pConsole("ERROR: Texture Map could not find texture '"+tex+"'; File not found.");
				return null;//"ERROR: Texture Map could not find texture '"+tex+"'; File not found.";
			}
			return "";
		}

	}

	public static void registerFilter(Filter f, LoggerType type) {
		Logger log = getLogger(type);
		if (log != null) {
			log.addFilter(f);
		}
		else {
			throw new RuntimeException("Logger interception is not working! Notify Reika immediately!");
		}
	}

	private static Logger getLogger(LoggerType type) {
		switch(type) {
			case FML:
				return (Logger)FMLRelaunchLog.log.getLogger();
			case SOUND:
				return (Logger)LogManager.getLogger(SoundHandler.class);
			case TEXTURE:
				return (Logger)LogManager.getLogger(TextureMap.class);
			case CHAT:
				return (Logger)LogManager.getLogger(GuiNewChat.class);
			case SERVER:
				return (Logger)LogManager.getLogger(MinecraftServer.class);
			case WORLD:
				return (Logger)LogManager.getLogger(WorldServer.class);
			default:
				return null;
		}
	}

	public static enum LoggerType {
		FML(),
		TEXTURE(),
		SOUND(),
		CHAT(),
		SERVER(),
		WORLD();

		public boolean isClientOnly() {
			return this == TEXTURE || this == SOUND || this == CHAT;
		}
	}

	public static void registerCoreFilters() {
		registerFilter(mismatchFilter, LoggerType.FML);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			registerFilter(soundLoaderFilter, LoggerType.SOUND);
			registerFilter(noTextureFilter, LoggerType.TEXTURE);
		}
	}

}
