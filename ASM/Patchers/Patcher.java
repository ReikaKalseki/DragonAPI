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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.Strings;

import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;

import cpw.mods.fml.relauncher.Side;

public abstract class Patcher {

	public final String obfName;
	public final String deobfName;

	public static final boolean genClasses = ReikaJVMParser.isArgumentPresent("-DragonAPI_exportASM");

	private static final HashSet<Patcher> activePatches = new HashSet();

	public Patcher(String s) {
		this(s, s);
	}

	public Patcher(String deobf, String obf) {
		deobfName = deobf;
		obfName = obf;
		if (deobfName.length() < obfName.length() && !deobfName.contains(".")) {
			throw new MisuseException("Swapped obf and deobf names!");
		}
		if (Strings.isNullOrEmpty(obf) || Strings.isNullOrEmpty(deobf)) {
			throw new MisuseException("Empty class specification! If you want to disable the patcher, mark it deprecated!");
		}
	}

	public Patcher activate() {
		activePatches.add(this);
		return this;
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

		if (Arrays.equals(data, newdata)) {
			ReikaASMHelper.log("WARNING: ASM handler "+this+" made no changes to the class!");
		}

		return newdata;
	}

	protected final void log(String s) {
		ReikaASMHelper.log(s);
	}

	protected abstract void apply(ClassNode cn);

	public final boolean isEnabled() {
		String key = this.isDisabledByDefault() ? "enable" : "disable";
		String tag = "-DragonAPI_"+key+"_ASM_" + this.name();
		boolean arg = ReikaJVMParser.isArgumentPresent(tag);
		return this.isDisabledByDefault() ? arg : !arg;
	}

	public boolean isDisabledByDefault() {
		return false;
	}

	public final boolean isExceptionThrowing() {
		String tag = "-DragonAPI_silence_ASM_" + this.name();
		return !ReikaJVMParser.isArgumentPresent(tag);
	}

	public final String name() {
		return this.nameKey().toUpperCase(Locale.ENGLISH);
	}

	protected String nameKey() {
		return this.getClass().getSimpleName();
	}

	public final boolean patchesForgeCode() {
		return !this.isObfable() && (deobfName.startsWith("net.minecraftforge") || deobfName.startsWith("cpw.mods.fml"));
	}

	public final boolean patchesModCode() {
		return !this.isObfable() && !this.patchesForgeCode();
	}

	private final boolean isObfable() {
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
		return this.name()+" ["+(this.isObfable() ? deobfName+"/"+obfName : deobfName)+"]";
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

	public static Collection<Patcher> getActivePatchers() {
		return Collections.unmodifiableCollection(activePatches);
	}

}
