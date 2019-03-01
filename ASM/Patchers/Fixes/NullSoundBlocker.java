/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Fixes;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.client.audio.ISound;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class NullSoundBlocker extends Patcher {

	public NullSoundBlocker() {
		super("net.minecraft.client.audio.SoundManager", "btj");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "(Lnet/minecraft/client/audio/SoundHandler;Lnet/minecraft/client/settings/GameSettings;)V");
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "com/google/common/collect/Lists", "newArrayList", "()Ljava/util/ArrayList;");
		min.owner = "Reika/DragonAPI/ASM/Patchers/Fixes/NullSoundBlocker";
		min.name = "getFilteringList";
	}

	public static ArrayList getFilteringList() {
		return new FilteredSoundList();
	}

	private static class FilteredSoundList extends ArrayList {

		@Override
		public boolean addAll(int pos, Collection c) {
			ArrayList invalid = this.verify(c);
			if (invalid.isEmpty()) {
				return this.addAll(pos, c);
			}
			else {
				throw new IllegalArgumentException("Invalid sound objects ("+invalid+") were added to the list!");
			}
		}

		@Override
		public boolean addAll(Collection c) {
			ArrayList invalid = this.verify(c);
			if (invalid.isEmpty()) {
				return super.addAll(c);
			}
			else {
				throw new IllegalArgumentException("Invalid sound objects ("+invalid+") were added to the list!");
			}
		}

		@Override
		public Object set(int pos, Object o) {
			if (this.verify(o)) {
				return super.set(pos, o);
			}
			else {
				throw new IllegalArgumentException("Invalid sound object ("+o+") was added to the list!");
			}
		}

		@Override
		public void add(int pos, Object o) {
			if (this.verify(o)) {
				super.add(pos, o);
			}
			else {
				throw new IllegalArgumentException("Invalid sound object ("+o+") was added to the list!");
			}
		}

		@Override
		public boolean add(Object o) {
			if (this.verify(o)) {
				//ReikaJavaLibrary.pConsole("adding sound "+o);
				return super.add(o);
			}
			else {
				throw new IllegalArgumentException("Invalid sound object ("+o+") was added to the list!");
			}
		}

		private ArrayList verify(Collection c) {
			ArrayList li = new ArrayList();
			for (Object o : c) {
				if (!this.verify(o))
					li.add(o);
			}
			return li;
		}

		private boolean verify(Object o) {
			return o != null && o instanceof ISound;
		}

	}

}
