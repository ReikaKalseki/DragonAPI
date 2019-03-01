/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * This code courtesy of Techjar
 *
 * <h3>Potentially Upset Readers</h3>
 *
 * <p>
 * This is a patch for FML so that it no longer tries to remove the vanilla blocks if the ItemBlocks mapped to them are removed.
 * Removing (and failing, as FML does) the blocks in this manner causes Forge to regenerate the id registry for modded blocks, scrambling them
 * in-world and completely destroying the save.
 * 
 * This is a temporary piece of code; I have since fixed the issue from happening in the future by adding an _technical to the registry names of the
 * ItemBlocks, but doing so would normally trigger the missing mapping this ASM is designed to fix. When I update to 1.8 - where it is a safe
 * assumption that no worlds will survive anyways - I can safely remove it in the knowledge that everyone is already under the new mappings.
 * </p>
 *
 *
 * <h3>Responses to Possible Criticisms</h3>
 *
 * <b>Why not just stop creating the ItemBlocks for technical blocks?</b>
 * <blockquote><p>
 * The ItemBlocks are required for rendering the blocks (all of which are unobtainable technical blocks like pumpkin stems, unlit redstone torches,
 * and portal blocks) as items in the inventory. Otherwise, the ItemStack created will have a null item and will immediately crash. Special-casing
 * 40 different renderers - all of which would have to be hand-written for these blocks - is an extremely onerous task.
 * </p><p>
 * Additionally, removing them does not remove the need for this ASM code until it is guaranteed that <i>everyone</i> has loaded the world without
 * the itemblocks and with this code present, as only then is the world safe to load without this code or the itemblocks. Given the difficulty in
 * getting many players to update at all, let alone use specific versions in sequence, such an approach is completely nonviable.
 * <b>Destroying existing worlds is absolutely unacceptable.
 * </b></p></blockquote><br />
 *
 *
 * <b>OMG WHY ARE YOU ASM-ING INTO FML ARE YOU INSANE!?!?!11!?</b>
 * <blockquote><p>
 * While this approach certainly <i>looks</i> insane, its only effect is to compensate for an unexpected edge case and oversight in FML.
 * It has no other effect on the code, and barring JVM errors, cannot cause any other issues.
 * </p></blockquote><br />
 *
 *
 * <b>Why not just make this a Pull Request into Forge?</b>
 * <blockquote><p>Because this code is to fix a rare edge-case, and said edge-case is one that design purists feel should <b>never</b> have
 * happened to begin with (and possibly in part due to the fact it was me who suggested it), all mentions of putting this natively into Forge/FML
 * were met with derision and hostility. While I never actually made a PR, when I mentioned my initial intentions to others, I was either laughed
 * at or flippantly told "maybe you shouldn't be rendering these blocks".
 * </p><p>
 * Additionally, at the time this code was written, all development on Forge/FML for MC 1.7 had been frozen. As such, even if the fix <i>had</i>
 * been included, it would have only made it into 1.8, making it far too late to be of any use.
 * </p></blockquote>
 * 
 *
 * <b>Why not use FML's MissingMappingEvent and ignore the missing mappings?</b>
 * <blockquote><p>I tried this. This does not fix the issue, because the call to GameData.block() is what actually breaks the data file, and
 * that is called regardless of the action type of the missing mapping.
 * </p></blockquote>
 */
public class FMLItemBlockPatch implements IClassTransformer {

	public static final int MAX_VANILLA_BLOCK = 175;
	public static final int SPACE_LOW = 165;
	public static final int SPACE_HIGH = 169;

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		if (arg0.equals("cpw.mods.fml.common.registry.GameData")) {
			return this.patchClassASM(arg0, arg2);
		}
		return arg2;
	}

	public byte[] patchClassASM(String name, byte[] bytes) {
		String methodName = "processIdRematches";
		String methodDesc = "(Ljava/lang/Iterable;ZLcpw/mods/fml/common/registry/GameData;Ljava/util/Map;)Ljava/util/List;";

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			if (m.name.equals(methodName) && m.desc.equals(methodDesc)) {
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 9));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "cpw/mods/fml/common/event/FMLMissingMappingsEvent$MissingMapping", "id", "I"));
				toInject.add(new IntInsnNode(Opcodes.SIPUSH, SPACE_LOW));
				LabelNode label1 = new LabelNode();
				toInject.add(new JumpInsnNode(Opcodes.IF_ICMPLT, label1));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 9));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "cpw/mods/fml/common/event/FMLMissingMappingsEvent$MissingMapping", "id", "I"));
				toInject.add(new IntInsnNode(Opcodes.SIPUSH, SPACE_HIGH));
				LabelNode label2 = new LabelNode();
				toInject.add(new JumpInsnNode(Opcodes.IF_ICMPLE, label2));
				toInject.add(label1);
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 9));
				toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "cpw/mods/fml/common/event/FMLMissingMappingsEvent$MissingMapping", "id", "I"));
				toInject.add(new IntInsnNode(Opcodes.SIPUSH, MAX_VANILLA_BLOCK));
				LabelNode label3 = new LabelNode();
				toInject.add(new JumpInsnNode(Opcodes.IF_ICMPLE, label3));
				toInject.add(label2);
				AbstractInsnNode foundNode = null;
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode insn = m.instructions.get(i);
					if (foundNode != insn && insn instanceof FieldInsnNode) {
						FieldInsnNode insn2 = (FieldInsnNode)insn;
						if (insn2.getOpcode() == Opcodes.GETSTATIC && insn2.owner.equals("cpw/mods/fml/common/event/FMLMissingMappingsEvent$Action") && insn2.name.equals("DEFAULT")) {
							m.instructions.insertBefore(m.instructions.get(i - 1), toInject);
							i += toInject.size();
							foundNode = insn;
						}
					} else if (insn instanceof MethodInsnNode) {
						MethodInsnNode insn2 = (MethodInsnNode)insn;
						if (insn2.getOpcode() == Opcodes.INVOKESPECIAL && insn2.owner.equals("cpw/mods/fml/common/registry/GameData") && insn2.name.equals("block")) {
							m.instructions.insert(insn, label3);
							break;
						}
					}
				}
				break;
			}
		}

		// ASM specific for cleaning up and returning the final bytes for JVM processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		/*
		try {
			FileOutputStream out = new FileOutputStream(new File("GameData.class"));
			out.write(writer.toByteArray());
			out.flush(); out.close();
		} catch (Exception ex) {}
		 */
		return writer.toByteArray();
	}
}
