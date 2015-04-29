/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.texture.TextureMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.Side;

public class LoggingFilters {

	private static final Filter mismatchFilter = new ItemBlockMismatchFilter();
	private static final Filter soundLoaderFilter = new CustomSoundLoaderFilter();

	public abstract static class CoreFilter implements Filter {

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
			String msg = this.parse(event.getMessage(), event.getLevel());
			if (msg == null) {
				return Result.DENY;
			}
			else if (!msg.isEmpty()) {
				LogManager.getLogger(event.getLoggerName()).log(event.getLevel(), msg);
				return Result.DENY;
			}
			else {
				return Result.NEUTRAL;
			}
		}

		/** Return null to silence the message. Return anything else to replace the message, except an empty string for "original message" */
		protected abstract String parse(Message msg, Level lvl);
	}

	private static class ItemBlockMismatchFilter extends CoreFilter {

		private int counter = 0;

		private ItemBlockMismatchFilter() {

		}

		@Override
		protected String parse(Message msg, Level lvl) {
			if (counter > 0) {
				counter--;
				return null;
			}
			String sg = msg.getFormattedMessage();
			if (sg.contains("Block <-> ItemBlock name mismatch") && sg.contains("DragonAPI:minecraft")) {
				counter = 7;
				return null;
			}
			return "";
		}

	}

	private static class CustomSoundLoaderFilter extends CoreFilter {

		private CustomSoundLoaderFilter() {

		}


		@Override
		protected String parse(Message msg, Level lvl) {
			String sg = msg.getFormattedMessage();
			if (sg.contains("Invalid sounds.json")) {
				return null;
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
		default:
			return null;
		}
	}

	public static enum LoggerType {
		FML(),
		TEXTURE(),
		SOUND();
	}

	public static void registerCoreFilters() {
		registerFilter(mismatchFilter, LoggerType.FML);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			registerFilter(soundLoaderFilter, LoggerType.SOUND);
	}

}
