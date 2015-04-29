/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.util.Collection;

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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class DragonAPIClassTransfomer implements IClassTransformer {

	private static final MultiMap<String, ClassPatch> classes = new MultiMap().setNullEmpty();

	private static enum ClassPatch {
		CREEPERBOMBEVENT("net.minecraft.entity.monster.EntityCreeper", "xz"),
		ITEMRENDEREVENT("net.minecraft.client.gui.inventory.GuiContainer", "bex"),
		SLOTCLICKEVENT("net.minecraft.inventory.Slot", "aay"),
		ICECANCEL("net.minecraft.world.World", "ahb"),
		HELDRENDEREVENT("net.minecraft.client.renderer.EntityRenderer", "blt"),
		POTIONEFFECTID("net.minecraft.potion.PotionEffect", "rw"),
		POTIONPACKETID("net.minecraft.network.play.server.S1DPacketEntityEffect", "in"),
		POTIONPACKETID2("net.minecraft.client.network.NetHandlerPlayClient", "bjb"),
		BLOCKPLACE("net.minecraft.item.ItemBlock", "abh"),
		SETBLOCK("net.minecraft.world.chunk.Chunk", "apx"),
		GUIEVENT("net.minecraft.entity.player.EntityPlayer", "yz"),
		ITEMUPDATE("net.minecraft.entity.item.EntityItem", "xk"),
		TILERENDER("net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher", "bmk"),
		WORLDRENDER("net.minecraft.client.renderer.RenderGlobal", "bma"),
		NIGHTVISEVENT("net.minecraft.client.renderer.EntityRenderer", "blt"),
		FARCLIPEVENT("net.minecraft.client.renderer.EntityRenderer", "blt"),
		PUSHENTITYOUT("net.minecraft.entity.Entity", "sa"),
		//PLAYERRENDER("net.minecraft.client.renderer.entity.RenderPlayer", "bop"),
		;

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
				m.instructions.insert(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				m.instructions.insert(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/CreeperExplodeEvent", "<init>", "(Lnet/minecraft/entity/monster/EntityCreeper;)V", false));
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
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/RenderItemInSlotEvent", "<init>", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;)V", false));
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
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
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/inventory/Slot", "getSlotIndex", "()I", false)); //do not obfuscate, is Forge func
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
				m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/inventory/Slot", FMLForgePlugin.RUNTIME_DEOBF ? "field_75224_c" : "inventory", "Lnet/minecraft/inventory/IInventory;"));
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 2));
				m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/SlotEvent$RemoveFromSlotEvent", "<init>", "(ILnet/minecraft/inventory/IInventory;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)V", false));
				m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
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
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "<init>", "(Lnet/minecraft/world/World;IIIZ)V", false));
				m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 5));
				m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				m.instructions.add(new InsnNode(Opcodes.POP));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 5));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "getResult", "()Lcpw/mods/fml/common/eventhandler/Event$Result;", false));
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
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "wouldFreezeNaturally", "()Z", false));
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
							m.instructions.insert(ain, new InsnNode(Opcodes.POP));
							m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
							m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/RenderFirstPersonItemEvent", "<init>", "()V", false));
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
			case BLOCKPLACE: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "placeBlockAt", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFFI)Z"); //Forge func, so no srg
				/*
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode min = (MethodInsnNode)ain;
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_149689_a" : "onBlockPlacedBy";
						if (min.name.equals(func)) {
							m.instructions.insert(min, new InsnNode(Opcodes.POP));
							m.instructions.insert(min, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
							m.instructions.insert(min, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent", "<init>", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;ILnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)V", false));
							m.instructions.insert(min, new VarInsnNode(Opcodes.ALOAD, 2));
							m.instructions.insert(min, new VarInsnNode(Opcodes.ALOAD, 1));
							m.instructions.insert(min, new VarInsnNode(Opcodes.ILOAD, 11));
							m.instructions.insert(min, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemBlock", "field_150939_a", "Lnet/minecraft/block/Block;"));
							m.instructions.insert(min, new VarInsnNode(Opcodes.ALOAD, 0));
							m.instructions.insert(min, new VarInsnNode(Opcodes.ILOAD, 6));
							m.instructions.insert(min, new VarInsnNode(Opcodes.ILOAD, 5));
							m.instructions.insert(min, new VarInsnNode(Opcodes.ILOAD, 4));
							m.instructions.insert(min, new VarInsnNode(Opcodes.ALOAD, 3));
							m.instructions.insert(min, new InsnNode(Opcodes.DUP));
							m.instructions.insert(min, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent"));
							m.instructions.insert(min, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
							break;
						}
					}
				}*/

				InsnList pre = new InsnList();
				LabelNode L1 = new LabelNode();
				LabelNode L2 = new LabelNode();
				pre.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				pre.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent"));
				pre.add(new InsnNode(Opcodes.DUP));
				pre.add(new VarInsnNode(Opcodes.ALOAD, 3));
				pre.add(new VarInsnNode(Opcodes.ILOAD, 4));
				pre.add(new VarInsnNode(Opcodes.ILOAD, 5));
				pre.add(new VarInsnNode(Opcodes.ILOAD, 6));
				pre.add(new VarInsnNode(Opcodes.ALOAD, 0));
				pre.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/item/ItemBlock", "field_150939_a", "Lnet/minecraft/block/Block;"));
				pre.add(new VarInsnNode(Opcodes.ILOAD, 11));
				pre.add(new VarInsnNode(Opcodes.ALOAD, 1));
				pre.add(new VarInsnNode(Opcodes.ALOAD, 2));
				pre.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/PlayerPlaceBlockEvent", "<init>", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;ILnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)V", false));
				pre.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				pre.add(new JumpInsnNode(Opcodes.IFEQ, L1));
				pre.add(L2);
				pre.add(new InsnNode(Opcodes.ICONST_0));
				pre.add(new InsnNode(Opcodes.IRETURN));
				pre.add(L1);
				m.instructions.insert(pre);
				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
				break;
			}
			case SETBLOCK: {
				//Look for IRETURN immediately after an ICONST_1; this is a "return true"
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_150807_a", "func_150807_a", "(IIILnet/minecraft/block/Block;I)Z");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.IRETURN) {
						if (ain.getPrevious().getOpcode() == Opcodes.ICONST_1) {
							AbstractInsnNode loc = ain.getPrevious();
							m.instructions.insertBefore(loc, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
							m.instructions.insertBefore(loc, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent"));
							m.instructions.insertBefore(loc, new InsnNode(Opcodes.DUP));
							m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ALOAD, 0));
							m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 1));
							m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 2));
							m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 3));
							m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent", "<init>", "(Lnet/minecraft/world/chunk/Chunk;III)V", false));
							m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
							m.instructions.insertBefore(loc, new InsnNode(Opcodes.POP));
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 1!");
							break;
						}
					}
				}

				m = ReikaASMHelper.getMethodByName(cn, "func_76589_b", "setBlockMetadata", "(IIII)Z");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.IRETURN) {
						if (ain.getPrevious().getOpcode() == Opcodes.ICONST_1) {
							AbstractInsnNode loc = ain.getPrevious();
							m.instructions.insertBefore(loc, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
							m.instructions.insertBefore(loc, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent"));
							m.instructions.insertBefore(loc, new InsnNode(Opcodes.DUP));
							m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ALOAD, 0));
							m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 1));
							m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 2));
							m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ILOAD, 3));
							m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/SetBlockEvent", "<init>", "(Lnet/minecraft/world/chunk/Chunk;III)V", false));
							m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
							m.instructions.insertBefore(loc, new InsnNode(Opcodes.POP));
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 2!");
							break;
						}
					}
				}
				break;
			}
			case GUIEVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "openGui", "(Ljava/lang/Object;ILnet/minecraft/world/World;III)V");
				InsnList pre = new InsnList();
				LabelNode L1 = new LabelNode();
				LabelNode L2 = new LabelNode();
				pre.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				pre.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/PlayerOpenGuiEvent"));
				pre.add(new InsnNode(Opcodes.DUP));
				pre.add(new VarInsnNode(Opcodes.ALOAD, 0));
				pre.add(new VarInsnNode(Opcodes.ALOAD, 1));
				pre.add(new VarInsnNode(Opcodes.ALOAD, 3));
				pre.add(new VarInsnNode(Opcodes.ILOAD, 4));
				pre.add(new VarInsnNode(Opcodes.ILOAD, 5));
				pre.add(new VarInsnNode(Opcodes.ILOAD, 6));
				pre.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/PlayerOpenGuiEvent", "<init>", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;Lnet/minecraft/world/World;III)V", false));
				pre.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				pre.add(new JumpInsnNode(Opcodes.IFNE, L1));
				pre.add(L2);
				m.instructions.insert(pre);
				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 1!");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.RETURN) {
						m.instructions.insertBefore(ain, L1);
						ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler 2!");
						break;
					}
				}
				break;
			}
			case ITEMUPDATE: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");
				InsnList fire = new InsnList();
				fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/ItemUpdateEvent"));
				fire.add(new InsnNode(Opcodes.DUP));
				fire.add(new VarInsnNode(Opcodes.ALOAD, 0));
				fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/ItemUpdateEvent", "<init>", "(Lnet/minecraft/entity/item/EntityItem;)V", false));
				fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				fire.add(new InsnNode(Opcodes.POP));

				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
						MethodInsnNode min = (MethodInsnNode)ain;
						if (min.name.equals(m.name) && min.owner.equals("net/minecraft/entity/Entity")) {
							m.instructions.insert(min, fire);
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
						}
					}
				}
				break;
			}
			case TILERENDER: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147549_a", "renderTileEntityAt", "(Lnet/minecraft/tileentity/TileEntity;DDDF)V");
				InsnList fire = new InsnList();
				fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/TileEntityRenderEvent"));
				fire.add(new InsnNode(Opcodes.DUP));
				fire.add(new VarInsnNode(Opcodes.ALOAD, 1));
				fire.add(new VarInsnNode(Opcodes.DLOAD, 2));
				fire.add(new VarInsnNode(Opcodes.DLOAD, 4));
				fire.add(new VarInsnNode(Opcodes.DLOAD, 6));
				fire.add(new VarInsnNode(Opcodes.FLOAD, 8));
				fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/TileEntityRenderEvent", "<init>", "(Lnet/minecraft/tileentity/TileEntity;DDDF)V", false));
				fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				fire.add(new InsnNode(Opcodes.POP));
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode min = (MethodInsnNode)ain;
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_147500_a" : "renderTileEntityAt";
						if (min.name.equals(func)) {
							m.instructions.insert(min, fire);
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
							break;
						}
					}
				}
				break;
			}
			case WORLDRENDER: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147589_a", "renderEntities", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/culling/ICamera;F)V");
				InsnList fire = new InsnList();
				fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/EntityRenderingLoopEvent"));
				fire.add(new InsnNode(Opcodes.DUP));
				fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/EntityRenderingLoopEvent", "<init>", "()V", false));
				fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				fire.add(new InsnNode(Opcodes.POP));
				AbstractInsnNode loc = null;
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						MethodInsnNode min = (MethodInsnNode)ain;
						String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_78483_a" : "disableLightmap";
						if (min.name.equals(func)) {
							while (loc == null) {
								ain = ain.getPrevious();
								if (ain.getOpcode() == Opcodes.ALOAD && ((VarInsnNode)ain).var == 0)
									loc = ain;
							}
							m.instructions.insertBefore(loc, fire);
							ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
							break;
						}
					}
				}
				break;
			}
			case NIGHTVISEVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82830_a", "getNightVisionBrightness", "(Lnet/minecraft/entity/player/EntityPlayer;F)F");
				m.instructions.clear();
				m.instructions.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/NightVisionBrightnessEvent"));
				m.instructions.add(new InsnNode(Opcodes.DUP));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
				m.instructions.add(new VarInsnNode(Opcodes.FLOAD, 2));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/NightVisionBrightnessEvent", "<init>", "(Lnet/minecraft/entity/player/EntityPlayer;F)V", false));
				m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 3));
				m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				m.instructions.add(new InsnNode(Opcodes.POP));
				m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
				m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/NightVisionBrightnessEvent", "brightness", "F"));
				m.instructions.add(new InsnNode(Opcodes.FRETURN));
				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
				break;
			}
			//case PLAYERRENDER: {
			//
			//	break;
			//}
			case FARCLIPEVENT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78479_a", "setupCameraTransform", "(FI)V");
				String fd = FMLForgePlugin.RUNTIME_DEOBF ? "field_78530_s" : "farPlaneDistance";
				InsnList add = new InsnList();
				add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/FarClippingPlaneEvent"));
				add.add(new InsnNode(Opcodes.DUP));
				add.add(new VarInsnNode(Opcodes.FLOAD, 1));
				add.add(new VarInsnNode(Opcodes.ILOAD, 2));
				add.add(new VarInsnNode(Opcodes.ALOAD, 0));
				add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", fd, "F"));
				add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/FarClippingPlaneEvent", "<init>", "(FIF)V", false));
				add.add(new VarInsnNode(Opcodes.ASTORE, 3));
				add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				add.add(new VarInsnNode(Opcodes.ALOAD, 3));
				add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				add.add(new InsnNode(Opcodes.POP));
				add.add(new VarInsnNode(Opcodes.ALOAD, 0));
				add.add(new VarInsnNode(Opcodes.ALOAD, 3));
				add.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/FarClippingPlaneEvent", "farClippingPlaneDistance", "F"));
				add.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", fd, "F"));
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.PUTFIELD) {
						m.instructions.insert(ain, add);
						ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
						break;
					}
				}
				break;
			}
			case PUSHENTITYOUT: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_145771_j", "func_145771_j", "(DDD)Z");
				InsnList add = new InsnList();
				LabelNode L1 = new LabelNode();
				LabelNode L2 = new LabelNode();
				add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/EntityPushOutOfBlocksEvent"));
				add.add(new InsnNode(Opcodes.DUP));
				add.add(new VarInsnNode(Opcodes.ALOAD, 0));
				add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/EntityPushOutOfBlocksEvent", "<init>", "(Lnet/minecraft/entity/Entity;)V", false));
				add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				add.add(new JumpInsnNode(Opcodes.IFEQ, L1));
				add.add(L2);
				add.add(new InsnNode(Opcodes.ICONST_0));
				add.add(new InsnNode(Opcodes.IRETURN));
				add.add(L1);
				m.instructions.insert(add);
				ReikaJavaLibrary.pConsole("DRAGONAPI: Successfully applied "+this+" ASM handler!");
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
			Collection<ClassPatch> c = classes.get(className);
			if (c != null) {
				for (ClassPatch p : c) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Patching class "+p.deobfName);
					opcodes = p.apply(opcodes);
				}
				classes.remove(className); //for maximizing performance
			}
		}
		return opcodes;
	}

	static {
		for (int i = 0; i < ClassPatch.list.length; i++) {
			ClassPatch p = ClassPatch.list[i];
			String s = !FMLForgePlugin.RUNTIME_DEOBF ? p.deobfName : p.obfName;
			classes.addValue(s, p);
		}
	}
}
