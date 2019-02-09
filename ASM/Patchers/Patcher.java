/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers;

import java.util.Locale;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
import cpw.mods.fml.relauncher.Side;

public abstract class Patcher {

	public final String obfName;
	public final String deobfName;

	public static final boolean genClasses = ReikaJVMParser.isArgumentPresent("-DragonAPI_exportASM");

	public Patcher(String s) {
		this(s, s);
	}

	public Patcher(String deobf, String obf) {
		deobfName = deobf;
		obfName = obf;
	}

	public final byte[] apply(byte[] data) {
		Side s = ReikaASMHelper.getSide();
		if (!this.runsOnSide(s)) {
			ReikaASMHelper.log("Skipping " + this + " ASM handler; does not run on side " + s);
			return data;
		}

		for (int i = 0; i < CoreModDetection.list.length; i++) {
			CoreModDetection c = CoreModDetection.list[i];
			if (c.isInstalled()) {
				if (!this.runWithCoreMod(c)) {
					ReikaASMHelper.log("Skipping " + this + " ASM handler; not compatible with " + c);
					return data;
				}
			}
		}

		ClassNode cn = new ClassNode();
		ClassReader classReader = new ClassReader(data);
		classReader.accept(cn, 0);
		this.apply(cn);
		ReikaASMHelper.log("Successfully applied " + this + " ASM handler!");
		int flags = ClassWriter.COMPUTE_MAXS;
		if (this.computeFrames())
			flags |= ClassWriter.COMPUTE_FRAMES;
		ClassWriter writer = new ClassWriter(flags);
		cn.accept(writer);
		byte[] newdata = writer.toByteArray();

		if (genClasses) {
			try {
				ReikaASMHelper.writeClassFile(cn, "ASMOutput");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return newdata;
	}

	protected final void log(String s) {
		ReikaASMHelper.log(s);
	}

	protected abstract void apply(ClassNode cn);

	public final boolean isEnabled() {
		String tag = "-DragonAPI_disable_ASM_" + this.name();
		return !ReikaJVMParser.isArgumentPresent(tag);
	}

	public final boolean isExceptionThrowing() {
		String tag = "-DragonAPI_silence_ASM_" + this.name();
		return !ReikaJVMParser.isArgumentPresent(tag);
	}

	public final String name() {
		return this.getClass().getSimpleName().toUpperCase(Locale.ENGLISH);
	}

	public final boolean patchesForgeCode() {
		return !this.isObfable() && (deobfName.startsWith("net.minecraftforge") || deobfName.startsWith("cpw.mods.fml"));
	}

	public final boolean patchesModCode() {
		return !this.isObfable() && !this.patchesForgeCode();
	}

	private boolean isObfable() {
		return !deobfName.equals(obfName);
	}

	public boolean runWithCoreMod(CoreModDetection c) {
		return true;
	}

	public boolean runsOnSide(Side s) {
		return true;
	}

	@Override
	public final String toString() {
		return this.name()+" ["+deobfName+"/"+obfName+"]";
	}

	public boolean computeFrames() {
		return false;
	}

	private class ASMApplicationException extends RuntimeException {

		private ASMApplicationException(ASMException e) {
			super(e);
		}
		/*
		@Override
		public String getMessage() {
			return "Error running Patcher '"+Patcher.this.name()+":\n"+super.getMessage();
		}*/

	}

}
