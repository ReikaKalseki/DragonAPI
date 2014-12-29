/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.util.HashMap;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class DragonAPIClassTransfomer implements IClassTransformer {

	private static final HashMap<String, ClassPatch> classes = new HashMap();

	private static enum ClassPatch {
		CREEPERBOMBEVENT("net.minecraft.entity.monster.EntityCreeper", "xz"),
		ITEMRENDEREVENT("net.minecraft.client.gui.inventory.GuiContainer", "bex"),
		SLOTCLICKEVENT("net.minecraft.inventory.Slot", "aay"),
		ICECANCEL("net.minecraft.world.World", "ahb"),
		HELDRENDEREVENT("net.minecraft.client.renderer.EntityRenderer", "blt"),
		POTIONEFFECTID("net.minecraft.potion.PotionEffect", "rw"),
		POTIONPACKETID("net.minecraft.network.play.server.S1DPacketEntityEffect", "in"),
		POTIONPACKETID2("net.minecraft.client.network.NetHandlerPlayClient", "bjb");

		private final String obfName;
		private final String deobfName;

		private static final ClassPatch[] list = values();

		private ClassPatch(String deobf, String obf) {
			obfName = obf;
			deobfName = deobf;
		}

		private byte[] apply(byte[] data) {
			ClassNode cn = new ClassNode();
			ClassReader classReader = new ClassReader(data);
			classReader.accept(cn, 0);
			switch(this) {
			case CREEPERBOMBEVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146077_cc", "func_146077_cc", "()V");
				AbstractInsnNode pos = null;
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.IFNE) {
						pos = ain;
						break;
					}
				}
				while (pos.getNext() instanceof LineNumberNode || pos.getNext() instanceof LabelNode) {
					pos = pos.getNext();
				}
				m.instructions.insert(pos, new InsnNode(Opcodes.POP));
				m.instructions.insert(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z"));
				m.instructions.insert(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/CreeperExplodeEvent", "<init>", "(Lnet/minecraft/entity/monster/EntityCreeper;)V"));
				m.instructions.insert(pos, new VarInsnNode(Opcodes.ALOAD, 0));
				m.instructions.insert(pos, new InsnNode(Opcodes.DUP));
				m.instructions.insert(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/CreeperExplodeEvent"));
				m.instructions.insert(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
			}
			break;
			case ITEMRENDEREVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146977_a", "func_146977_a", "(Lnet/minecraft/inventory/Slot;)V");
				AbstractInsnNode pos = m.instructions.getFirst();
				m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				m.instructions.insertBefore(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/RenderItemInSlotEvent"));
				m.instructions.insertBefore(pos, new InsnNode(Opcodes.DUP));
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/RenderItemInSlotEvent", "<init>", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;)V"));
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z"));
				m.instructions.insertBefore(pos, new InsnNode(Opcodes.POP));

				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
			}
			break;
			case SLOTCLICKEVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82870_a", "onPickupFromSlot", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V");
				AbstractInsnNode pos = m.instructions.getFirst();
				m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				m.instructions.insertBefore(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/SlotEvent$RemoveFromSlotEvent"));
				m.instructions.insertBefore(pos, new InsnNode(Opcodes.DUP));
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/inventory/Slot", "getSlotIndex", "()I")); //do not obfuscate, is Forge func
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
				m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/inventory/Slot", FMLForgePlugin.RUNTIME_DEOBF ? "field_75224_c" : "inventory", "Lnet/minecraft/inventory/IInventory;"));
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 2));
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/SlotEvent$RemoveFromSlotEvent", "<init>", "(ILnet/minecraft/inventory/IInventory;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)V"));
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z"));
				m.instructions.insertBefore(pos, new InsnNode(Opcodes.POP));

				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
			}
			break;
			case ICECANCEL: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72834_c", "canBlockFreeze", "(IIIZ)Z");
				LabelNode l4 = new LabelNode(new Label());
				LabelNode l6 = new LabelNode(new Label());
				m.instructions.clear();
				m.instructions.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent"));
				m.instructions.add(new InsnNode(Opcodes.DUP));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
				m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
				m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
				m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "<init>", "(Lnet/minecraft/world/World;IIIZ)V"));
				m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 5));
				m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z"));
				m.instructions.add(new InsnNode(Opcodes.POP));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "getResult", "()Lcpw/mods/fml/common/eventhandler/Event$Result;"));
				m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 6));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
				m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "cpw/mods/fml/common/eventhandler/Event$Result", "ALLOW", "Lcpw/mods/fml/common/eventhandler/Event$Result;"));
				m.instructions.add(new JumpInsnNode(Opcodes.IF_ACMPNE, l4));
				m.instructions.add(new InsnNode(Opcodes.ICONST_1));
				m.instructions.add(new InsnNode(Opcodes.IRETURN));
				m.instructions.add(l4);
				//FRAME APPEND [Reika/DragonAPI/Instantiable/Event/IceFreezeEvent cpw/mods/fml/common/eventhandler/Event$Result]
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
				m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "cpw/mods/fml/common/eventhandler/Event$Result", "DENY", "Lcpw/mods/fml/common/eventhandler/Event$Result;"));
				m.instructions.add(new JumpInsnNode(Opcodes.IF_ACMPNE, l6));
				m.instructions.add(new InsnNode(Opcodes.ICONST_0));
				m.instructions.add(new InsnNode(Opcodes.IRETURN));
				m.instructions.add(l6);
				//FRAME SAME
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "wouldFreezeNaturally", "()Z"));
				m.instructions.add(new InsnNode(Opcodes.IRETURN));

				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
				break;
			}
			case HELDRENDEREVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78476_b", "renderHand", "(FI)V");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_78440_a" : "renderItemInFirstPerson";
						MethodInsnNode min = (MethodInsnNode)ain;
						if (min.name.equals(func)) {
							while (ain.getNext() instanceof LabelNode || ain.getNext() instanceof LineNumberNode)
								ain = ain.getNext();
							m.instructions.insert(ain, new InsnNode(Opcodes.POP));
							m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z"));
							m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/RenderFirstPersonItemEvent", "<init>", "()V"));
							m.instructions.insert(ain, new InsnNode(Opcodes.DUP));
							m.instructions.insert(ain, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/RenderFirstPersonItemEvent"));
							m.instructions.insert(ain, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
							break;
						}
					}
				}
				break;
			}
			case POTIONEFFECTID: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82719_a", "writeCustomPotionEffectToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.I2B) {
						MethodInsnNode nbt = (MethodInsnNode)ain.getNext(); //set to NBT
						nbt.name = FMLForgePlugin.RUNTIME_DEOBF ? "func_74768_a" : "setInteger";
						nbt.desc = "(Ljava/lang/String;I)V";
						m.instructions.remove(ain); //delete the byte cast
						ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 1!");
						break;
					}
				}

				m = ReikaASMHelper.getMethodByName(cn, "func_82722_b", "readCustomPotionEffectFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/potion/PotionEffect;");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.LDC) {
						MethodInsnNode nbt = (MethodInsnNode)ain.getNext(); //get from NBT
						nbt.name = FMLForgePlugin.RUNTIME_DEOBF ? "func_74762_e" : "getInteger";
						nbt.desc = "(Ljava/lang/String;)I";
						ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 2!");
						break;
					}
				}
				break;
			}
			case POTIONPACKETID: {
				FieldNode f = ReikaASMHelper.getFieldByName(cn, "field_149432_b", "field_149432_b");
				f.desc = "I";
				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 1!");

				MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "(ILnet/minecraft/potion/PotionEffect;)V");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.I2B) {
						FieldInsnNode put = (FieldInsnNode)ain.getNext();
						put.desc = "I";
						m.instructions.remove(ain);
						ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 2!");
						break;
					}
				}

				m = ReikaASMHelper.getMethodByName(cn, "func_148837_a", "readPacketData", "(Lnet/minecraft/network/PacketBuffer;)V");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode min = (MethodInsnNode)ain;
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "readByte" : "readByte";
						String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "readInt" : "readInt";
						if (min.name.equals(func)) {
							min.name = func2;
							min.desc = "()I";
							FieldInsnNode put = (FieldInsnNode)ain.getNext();
							put.desc = "I";
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 3!");
							break;
						}
					}
				}

				m = ReikaASMHelper.getMethodByName(cn, "func_148840_b", "writePacketData", "(Lnet/minecraft/network/PacketBuffer;)V");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode min = (MethodInsnNode)ain;
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "writeByte" : "writeByte";
						String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "writeInt" : "writeInt";
						if (min.name.equals(func)) {
							min.name = func2;
							min.desc = "(I)Lio/netty/buffer/ByteBuf;";
							FieldInsnNode get = (FieldInsnNode)ain.getPrevious();
							get.desc = "I";
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 4!");
							break;
						}
					}
				}

				if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
					m = ReikaASMHelper.getMethodByName(cn, "func_149427_e", "func_149427_e", "()B");
					m.desc = "()I"; //Change getID() return to int; does not need code changes elsewhere, as it is passed into a PotionEffect <init>.
					ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 5!");

					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.GETFIELD) {
							FieldInsnNode fin = (FieldInsnNode)ain;
							fin.desc = "I";
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 6!");
							break;
						}
					}
				}
				break;
			}
			case POTIONPACKETID2: { //Changes the call to func_149427_e, which otherwise looks for ()B and NSMEs
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147260_a", "handleEntityEffect", "(Lnet/minecraft/network/play/server/S1DPacketEntityEffect;)V");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode min = (MethodInsnNode)ain;
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_149427_e" : "func_149427_e";
						if (min.name.equals(func)) {
							min.desc = "()I";
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
							break;
						}
					}
				}
				break;
			}
			}

			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
			cn.accept(writer);
			byte[] newdata = writer.toByteArray();
			//ClassNode vcn = new ClassNode(); //verify
			//new ClassReader(newdata).accept(vcn, 0);
			return newdata;
		}
	}

	@Override
	public byte[] transform(String className, String className2, byte[] opcodes) {
		if (!classes.isEmpty()) {
			ClassPatch p = classes.get(className);
			if (p != null) {
				ReikaJavaLibrary.pConsole("DRAGONAPI: Patching class "+className);
				opcodes = p.apply(opcodes);
				classes.remove(className); //for maximizing performance
			}
		}
		return opcodes;
	}

	static {
		for (int i = 0; i < ClassPatch.list.length; i++) {
			ClassPatch p = ClassPatch.list[i];
			String s = !FMLForgePlugin.RUNTIME_DEOBF ? p.deobfName : p.obfName;
			classes.put(s, p);
		}
	}
}
