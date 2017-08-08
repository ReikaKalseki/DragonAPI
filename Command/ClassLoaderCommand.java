/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.lang.reflect.Field;
import java.util.Vector;

import net.minecraft.command.ICommandSender;
import net.minecraft.launchwrapper.Launch;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class ClassLoaderCommand extends DragonCommandBase {

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		try {
			Field f = ClassLoader.class.getDeclaredField("classes");
			f.setAccessible(true);
			ClassLoader cl = null;
			switch (args[0]) {
				case "launch":
					cl = Launch.classLoader;
					break;
				case "system":
					cl = ClassLoader.getSystemClassLoader();
					break;
				case "relaunch":
					//cl = FMLRelaunchClassLoader
					break;
				case "current":
					cl = this.getClass().getClassLoader();
					break;
			}
			if (cl == null)
				throw new IllegalArgumentException("Invalid classloader type!");
			Vector<Class> classes =  (Vector<Class>)f.get(cl);
			ReikaJavaLibrary.pConsole("Classes loaded with "+cl);
			for (Class c : classes) {
				ReikaJavaLibrary.pConsole(c.getName());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getCommandString() {
		return "classloader";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

}
