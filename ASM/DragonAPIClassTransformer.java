/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.lang.reflect.Modifier;
import java.util.Collection;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.BiomeDictionary;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Auxiliary.CoreModDetection;
import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodInstructionException;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Instantiable.Event.XPUpdateEvent;
import Reika.DragonAPI.Interfaces.ASMEnum;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJVMParser;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class DragonAPIClassTransformer implements IClassTransformer {

	private static final MultiMap<String, ClassPatch> classes = new MultiMap(new HashSetFactory()).setNullEmpty();
	private static int bukkitFlags;
	private static boolean nullItemPrintout = false;
	private static boolean nullItemCrash = false;

	private static enum ClassPatch implements ASMEnum {

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
		ENTITYRENDER("net.minecraft.client.renderer.entity.RenderManager", "bnn"),
		WORLDRENDER("net.minecraft.client.renderer.RenderGlobal", "bma"),
		NIGHTVISEVENT("net.minecraft.client.renderer.EntityRenderer", "blt"),
		FARCLIPEVENT("net.minecraft.client.renderer.EntityRenderer", "blt"),
		PUSHENTITYOUT("net.minecraft.entity.Entity", "sa"),
		CREATIVETAB("net.minecraft.client.gui.inventory.GuiContainerCreative", "bfl"),
		BURNBLOCK("net.minecraft.block.BlockFire", "alb"),
		PLAYERRENDERPASS("net.minecraft.entity.player.EntityPlayer", "yz"),
		//CHATSIZE("net.minecraft.client.gui.GuiNewChat", "bcc"),
		//PLAYERRENDER("net.minecraft.client.renderer.entity.RenderPlayer", "bop"),
		TILEUPDATE("net.minecraft.world.World", "ahb"),
		//TILEUPDATE1("net.minecraft.world.World", "ahb"),
		//TILEUPDATE2("net.minecraft.tileentity.TileEntity", "aor"),
		FURNACEUPDATE("net.minecraft.tileentity.TileEntityFurnace", "apg"),
		MUSICEVENT("net.minecraft.client.audio.MusicTicker", "btg"),
		SOUNDEVENTS("net.minecraft.client.audio.SoundManager", "btj"),
		CLOUDRENDEREVENT1("net.minecraft.client.settings.GameSettings", "bbj"),
		CLOUDRENDEREVENT2("net.minecraft.client.renderer.EntityRenderer", "blt"),
		PROFILER("net.minecraft.profiler.Profiler", "qi"),
		SPRINTEVENT("net.minecraft.network.NetHandlerPlayServer", "nh"),
		//JUMPCHECKEVENTSERVER("net.minecraft.network.NetHandlerPlayServer", "nh"),
		//JUMPCHECKEVENTCLIENT("net.minecraft.entity.EntityLivingBase", "sv"),
		MOBTARGETEVENT("net.minecraft.world.World", "ahb"),
		//TOOLTIPEVENT("net.minecraft.item.Item", "adb"),
		PERMUTEDBIOMEREG("net.minecraftforge.common.BiomeDictionary"),
		BLOCKTICKEVENT("net.minecraft.world.WorldServer", "mt"),
		ADDCRAFTINGEVENT("net.minecraft.item.crafting.CraftingManager", "afe"),
		ADDSMELTINGEVENT("net.minecraft.item.crafting.FurnaceRecipes", "afa"),
		ENDSEED("net.minecraft.world.WorldProviderEnd", "aqr"),
		NETHERSEED("net.minecraft.world.WorldProviderHell", "aqp"),
		MUSICTYPEEVENT("net.minecraft.client.Minecraft", "bao"),
		//ITEMSTACKNULL("net.minecraft.item.ItemStack", "add"),
		LIGHTMAP("net.minecraft.client.renderer.EntityRenderer", "blt"),
		CHATEVENT("net.minecraft.client.gui.GuiNewChat", "bcc"),
		POTIONITEM("net.minecraft.entity.projectile.EntityPotion", "zo"),
		RAYTRACEEVENT1("net.minecraft.entity.projectile.EntityArrow", "zc"),
		RAYTRACEEVENT2("net.minecraft.entity.projectile.EntityThrowable", "zk"),
		RAYTRACEEVENT3("net.minecraft.entity.projectile.EntityFireball", "ze"),
		ENDERATTACKTPEVENT("net.minecraft.entity.monster.EntityEnderman", "ya"),
		ATTACKAGGROEVENT1("net.minecraft.entity.monster.EntityMob", "yg"),
		ATTACKAGGROEVENT2("net.minecraft.entity.monster.EntityPigZombie", "yh"),
		PIGZOMBIEAGGROSPREADEVENT("net.minecraft.entity.monster.EntityPigZombie", "yh"),
		BIOMEMUTATIONEVENT("net.minecraft.world.gen.layer.GenLayerHills", "axr"),
		CRASHNOTIFICATIONS("net.minecraft.crash.CrashReport", "b"),
		KEEPINVEVENT("net.minecraft.entity.player.EntityPlayer", "yz"),
		KEEPINVEVENT2("net.minecraft.entity.player.EntityPlayerMP", "mw"),
		HOTBARKEYEVENT("net.minecraft.client.gui.inventory.GuiContainer", "bex"),
		RENDERBLOCKEVENT("net.minecraft.client.renderer.WorldRenderer", "blo"),
		MOUSEOVEREVENT("net.minecraft.client.renderer.EntityRenderer", "blt"),
		POSTITEMUSEEVENT("net.minecraft.item.ItemStack", "add"),
		POSTITEMUSEEVENT2("net.minecraftforge.common.ForgeHooks"),
		ITEMSIZETEXTEVENT("net.minecraft.client.renderer.entity.RenderItem", "bny"),
		//NOREROUTECUSTOMTEXMAP("net.minecraft.client.renderer.texture.TextureMap", "bpz"),
		FARDESPAWNEVENT("net.minecraft.entity.EntityLiving", "sw"),
		//PARTICLELIMIT("net.minecraft.client.particle.EffectRenderer", "bkn"),
		SETBLOCKLIGHT("net.minecraft.world.World", "ahb"),
		XPUPDATE("net.minecraft.entity.item.EntityXPOrb", "sq"),
		SPAWNERCHECK("net.minecraft.tileentity.MobSpawnerBaseLogic", "agq"),
		;

		private final String obfName;
		private final String deobfName;

		private static final ClassPatch[] list = values();

		private ClassPatch(String s) {
			this(s, s);
		}

		private ClassPatch(String deobf, String obf) {
			obfName = obf;
			deobfName = deobf;
		}

		public byte[] apply(byte[] data) {
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
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case ITEMRENDEREVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146977_a", "func_146977_a", "(Lnet/minecraft/inventory/Slot;)V");
					AbstractInsnNode pos = m.instructions.getFirst();
					m.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					m.instructions.insertBefore(pos, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemInSlotEvent"));
					m.instructions.insertBefore(pos, new InsnNode(Opcodes.DUP));
					m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/RenderItemInSlotEvent", "<init>", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;)V", false));
					m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					m.instructions.insertBefore(pos, new InsnNode(Opcodes.POP));

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
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

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");

					m = ReikaASMHelper.getMethodByName(cn, "func_75215_d", "putStack", "(Lnet/minecraft/item/ItemStack;)V");
					pos = m.instructions.getFirst();

					m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/SlotEvent$AddToSlotEvent", "fire", "(Lnet/minecraft/inventory/Slot;Lnet/minecraft/item/ItemStack;)V", false));

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
					break;
				}
				case ICECANCEL: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72834_c", "canBlockFreeze", "(IIIZ)Z");
					/*
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
					 */

					m.instructions.clear();
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
					m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
					m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
					m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/IceFreezeEvent", "fire", "(Lnet/minecraft/world/World;IIIZ)Z", false));
					m.instructions.add(new InsnNode(Opcodes.IRETURN));

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
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
								m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/RenderFirstPersonItemEvent", "<init>", "()V", false));
								m.instructions.insert(ain, new InsnNode(Opcodes.DUP));
								m.instructions.insert(ain, new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/RenderFirstPersonItemEvent"));
								m.instructions.insert(ain, new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
								break;
							}
						}
					}
					break;
				}
				case POTIONEFFECTID: {
					//if (Loader.isModLoaded("Potion ID Helper"))
					//	break;
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82719_a", "writeCustomPotionEffectToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;");
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.I2B) {
							MethodInsnNode nbt = (MethodInsnNode)ain.getNext(); //set to NBT
							nbt.name = FMLForgePlugin.RUNTIME_DEOBF ? "func_74768_a" : "setInteger";
							nbt.desc = "(Ljava/lang/String;I)V";
							m.instructions.remove(ain); //delete the byte cast
							ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");
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
							ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
							break;
						}
					}
					break;
				}
				case POTIONPACKETID: {
					//if (Loader.isModLoaded("Potion ID Helper"))
					//	break;
					FieldNode f = ReikaASMHelper.getFieldByName(cn, "field_149432_b", "field_149432_b");
					f.desc = "I";
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");

					MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "(ILnet/minecraft/potion/PotionEffect;)V");
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.I2B) {
							FieldInsnNode put = (FieldInsnNode)ain.getNext();
							put.desc = "I";
							m.instructions.remove(ain);
							ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
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
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler 3!");
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
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler 4!");
								break;
							}
						}
					}

					if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
						m = ReikaASMHelper.getMethodByName(cn, "func_149427_e", "func_149427_e", "()B");
						m.desc = "()I"; //Change getID() return to int; does not need code changes elsewhere, as it is passed into a PotionEffect <init>.
						ReikaASMHelper.log("Successfully applied "+this+" ASM handler 5!");

						for (int i = 0; i < m.instructions.size(); i++) {
							AbstractInsnNode ain = m.instructions.get(i);
							if (ain.getOpcode() == Opcodes.GETFIELD) {
								FieldInsnNode fin = (FieldInsnNode)ain;
								fin.desc = "I";
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler 6!");
								break;
							}
						}
					}
					break;
				}
				case POTIONPACKETID2: { //Changes the call to func_149427_e, which otherwise looks for ()B and NSMEs
					//if (Loader.isModLoaded("Potion ID Helper"))
					//	break;
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147260_a", "handleEntityEffect", "(Lnet/minecraft/network/play/server/S1DPacketEntityEffect;)V");
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
							MethodInsnNode min = (MethodInsnNode)ain;
							String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_149427_e" : "func_149427_e";
							if (min.name.equals(func)) {
								min.desc = "()I";
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
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
							ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
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
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
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
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");
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
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
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
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.RETURN) {
							m.instructions.insertBefore(ain, L1);
							ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
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
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
							}
						}
					}
					break;
				}
				case TILERENDER: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147549_a", "renderTileEntityAt", "(Lnet/minecraft/tileentity/TileEntity;DDDF)V");
					InsnList fire = new InsnList();
					fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/TileEntityRenderEvent"));
					fire.add(new InsnNode(Opcodes.DUP));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 1));
					fire.add(new VarInsnNode(Opcodes.DLOAD, 2));
					fire.add(new VarInsnNode(Opcodes.DLOAD, 4));
					fire.add(new VarInsnNode(Opcodes.DLOAD, 6));
					fire.add(new VarInsnNode(Opcodes.FLOAD, 8));
					fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/TileEntityRenderEvent", "<init>", "(Lnet/minecraft/tileentity/TileEntity;DDDF)V", false));
					fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					fire.add(new InsnNode(Opcodes.POP));
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
							MethodInsnNode min = (MethodInsnNode)ain;
							String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_147500_a" : "renderTileEntityAt";
							if (min.name.equals(func)) {
								m.instructions.insert(min, fire);
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
								break;
							}
						}
					}
					break;
				}
				case ENTITYRENDER: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147939_a", "func_147939_a", "(Lnet/minecraft/entity/Entity;DDDFFZ)Z");
					InsnList fire = new InsnList();
					fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/EntityRenderEvent"));
					fire.add(new InsnNode(Opcodes.DUP));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 1));
					fire.add(new VarInsnNode(Opcodes.DLOAD, 2));
					fire.add(new VarInsnNode(Opcodes.DLOAD, 4));
					fire.add(new VarInsnNode(Opcodes.DLOAD, 6));
					fire.add(new VarInsnNode(Opcodes.FLOAD, 8));
					fire.add(new VarInsnNode(Opcodes.FLOAD, 9));
					fire.add(new VarInsnNode(Opcodes.ILOAD, 10));
					fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/EntityRenderEvent", "<init>", "(Lnet/minecraft/entity/Entity;DDDFFZ)V", false));
					fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					fire.add(new InsnNode(Opcodes.POP));
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
							MethodInsnNode min = (MethodInsnNode)ain;
							String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_76986_a" : "doRender";
							if (min.name.equals(func)) {
								m.instructions.insert(min, fire);
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
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
					fire.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/EntityRenderingLoopEvent"));
					fire.add(new InsnNode(Opcodes.DUP));
					fire.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/EntityRenderingLoopEvent", "<init>", "()V", false));
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
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
								break;
							}
						}
					}
					break;
				}
				case NIGHTVISEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82830_a", "getNightVisionBrightness", "(Lnet/minecraft/entity/player/EntityPlayer;F)F");
					m.instructions.clear();
					/*
					m.instructions.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/NightVisionBrightnessEvent"));
					m.instructions.add(new InsnNode(Opcodes.DUP));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.add(new VarInsnNode(Opcodes.FLOAD, 2));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/NightVisionBrightnessEvent", "<init>", "(Lnet/minecraft/entity/player/EntityPlayer;F)V", false));
					m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 3));
					m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					m.instructions.add(new InsnNode(Opcodes.POP));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
					m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/Client/NightVisionBrightnessEvent", "brightness", "F"));
					m.instructions.add(new InsnNode(Opcodes.FRETURN));
					 */

					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.add(new VarInsnNode(Opcodes.FLOAD, 2));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/NightVisionBrightnessEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;F)F", false));
					m.instructions.add(new InsnNode(Opcodes.FRETURN));

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
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
					add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/FarClippingPlaneEvent"));
					add.add(new InsnNode(Opcodes.DUP));
					add.add(new VarInsnNode(Opcodes.FLOAD, 1));
					add.add(new VarInsnNode(Opcodes.ILOAD, 2));
					add.add(new VarInsnNode(Opcodes.ALOAD, 0));
					add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/EntityRenderer", fd, "F"));
					add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/FarClippingPlaneEvent", "<init>", "(FIF)V", false));
					add.add(new VarInsnNode(Opcodes.ASTORE, 3));
					add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					add.add(new VarInsnNode(Opcodes.ALOAD, 3));
					add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					add.add(new InsnNode(Opcodes.POP));
					add.add(new VarInsnNode(Opcodes.ALOAD, 0));
					add.add(new VarInsnNode(Opcodes.ALOAD, 3));
					add.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/Client/FarClippingPlaneEvent", "farClippingPlaneDistance", "F"));
					add.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/renderer/EntityRenderer", fd, "F"));
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.PUTFIELD) {
							m.instructions.insert(ain, add);
							ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
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
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case CREATIVETAB: {
					InsnList add = new InsnList();
					LabelNode L26 = new LabelNode();
					add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/CreativeTabGuiRenderEvent"));
					add.add(new InsnNode(Opcodes.DUP));
					add.add(new VarInsnNode(Opcodes.ALOAD, 0));
					add.add(new VarInsnNode(Opcodes.ALOAD, 4));
					add.add(new VarInsnNode(Opcodes.ALOAD, 0));
					boolean obf = FMLForgePlugin.RUNTIME_DEOBF;
					String fd1 = obf ? "field_147062_A" : "searchField";
					add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainerCreative", fd1, "Lnet/minecraft/client/gui/GuiTextField;"));
					String fd2 = obf ? "tabPage" : "tabPage"; //Forge?
					add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/gui/inventory/GuiContainerCreative", fd2, "I"));
					add.add(new VarInsnNode(Opcodes.ALOAD, 0));
					String fd3 = obf ? "field_146999_f" : "xSize";
					String fd4 = obf ? "field_147000_g" : "ySize";
					add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", fd3, "I"));
					add.add(new VarInsnNode(Opcodes.ALOAD, 0));
					add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", fd4, "I"));
					add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/CreativeTabGuiRenderEvent", "<init>", "(Lnet/minecraft/client/gui/inventory/GuiContainerCreative;Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/client/gui/GuiTextField;III)V", false));
					add.add(new VarInsnNode(Opcodes.ASTORE, 9));
					add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					add.add(new VarInsnNode(Opcodes.ALOAD, 9));
					add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					add.add(new JumpInsnNode(Opcodes.IFNE, L26));

					/*
				add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/CreativeTabGuiRenderEvent"));
				add.add(new InsnNode(Opcodes.DUP));
				add.add(new VarInsnNode(Opcodes.ALOAD, 0));
				add.add(new VarInsnNode(Opcodes.ALOAD, 4));
				add.add(new VarInsnNode(Opcodes.ALOAD, 0));
				add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainerCreative", "searchField", "Lnet/minecraft/client/gui/GuiTextField;"));
				add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/gui/inventory/GuiContainerCreative", "tabPage", "I"));
				add.add(new VarInsnNode(Opcodes.ALOAD, 0));
				add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", "xSize", "I"));
				add.add(new VarInsnNode(Opcodes.ALOAD, 0));
				add.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", "ySize", "I"));
				add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/CreativeTabGuiRenderEvent", "<init>", "(Lnet/minecraft/client/gui/inventory/GuiContainerCreative;Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/client/gui/GuiTextField;III)V", false));
				add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				add.add(new InsnNode(Opcodes.POP));
					 */

					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146976_a", "drawGuiContainerBackgroundLayer", "(FII)V");
					String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_73729_b" : "drawTexturedModalRect";
					//String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_110577_a" : "bindTexture";
					boolean primed = false;
					AbstractInsnNode loc1 = null;
					AbstractInsnNode loc2 = null;
					for (int i = 0; i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (!primed && ain.getOpcode() == Opcodes.LDC) {
							LdcInsnNode lin = (LdcInsnNode)ain;
							if ("textures/gui/container/creative_inventory/tab_".equals(lin.cst)) {
								primed = true;
								loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, i, Opcodes.ALOAD, 0);
							}
						}
						else if (primed && ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
							MethodInsnNode min = (MethodInsnNode)ain;
							if (min.name.equals(func)) {
								m.instructions.insert(min, L26); //add the label node
								//m.instructions.insert(ain, add);
								ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");
								break;
							}
						}
					}
					m.instructions.insertBefore(loc1, add);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
					break;
				}
				case BURNBLOCK: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_149674_a", "updateTick", "(Lnet/minecraft/world/World;IIILjava/util/Random;)V");
					String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_147465_d" : "setBlock";
					String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_147468_f" : "setBlockToAir";
					String world = "net/minecraft/world/World";
					MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, world, func, "(IIILnet/minecraft/block/Block;II)Z");
					InsnList add = new InsnList();
					LabelNode L34 = new LabelNode();
					add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent"));
					add.add(new InsnNode(Opcodes.DUP));
					add.add(new VarInsnNode(Opcodes.ALOAD, 1));
					add.add(new VarInsnNode(Opcodes.ILOAD, 10));
					add.add(new VarInsnNode(Opcodes.ILOAD, 12));
					add.add(new VarInsnNode(Opcodes.ILOAD, 11));
					add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent", "<init>", "(Lnet/minecraft/world/World;III)V", false));
					add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					add.add(new JumpInsnNode(Opcodes.IFNE, L34));
					AbstractInsnNode loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(min), Opcodes.ALOAD, 1);
					AbstractInsnNode loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(min), Opcodes.POP);
					m.instructions.insertBefore(loc1, add);
					m.instructions.insert(loc2, L34);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");

					m = ReikaASMHelper.getMethodByName(cn, /*"func_149841_a", */"tryCatchFire", "(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V"); // Forge one
					min = ReikaASMHelper.getFirstMethodCall(cn, m, world, func, "(IIILnet/minecraft/block/Block;II)Z");
					add = new InsnList();
					LabelNode L12 = new LabelNode();
					add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent"));
					add.add(new InsnNode(Opcodes.DUP));
					add.add(new VarInsnNode(Opcodes.ALOAD, 1));
					add.add(new VarInsnNode(Opcodes.ILOAD, 2));
					add.add(new VarInsnNode(Opcodes.ILOAD, 3));
					add.add(new VarInsnNode(Opcodes.ILOAD, 4));
					add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent", "<init>", "(Lnet/minecraft/world/World;III)V", false));
					add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					add.add(new JumpInsnNode(Opcodes.IFNE, L12));
					loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(min), Opcodes.ALOAD, 1);
					loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(min), Opcodes.POP);
					m.instructions.insertBefore(loc1, add);
					m.instructions.insert(loc2, L12);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");

					min = ReikaASMHelper.getFirstMethodCall(cn, m, world, func2, "(III)Z");
					//add = ReikaASMHelper.copyInsnList(add, L12, L12);
					add = new InsnList();
					add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent"));
					add.add(new InsnNode(Opcodes.DUP));
					add.add(new VarInsnNode(Opcodes.ALOAD, 1));
					add.add(new VarInsnNode(Opcodes.ILOAD, 2));
					add.add(new VarInsnNode(Opcodes.ILOAD, 3));
					add.add(new VarInsnNode(Opcodes.ILOAD, 4));
					add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/BlockConsumedByFireEvent", "<init>", "(Lnet/minecraft/world/World;III)V", false));
					add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					add.add(new JumpInsnNode(Opcodes.IFNE, L12));
					loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(min), Opcodes.ALOAD, 1);
					loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(min), Opcodes.POP);
					m.instructions.insertBefore(loc1, add);
					//m.instructions.insert(loc2, L12);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 3!");
					break;
				}/*
			case CHATSIZE: {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146237_a", "func_146237_a", "(Lnet/minecraft/util/IChatComponent;IIZ)V");
				for (int i = 0; i < m.instructions.size(); i++) {
					AbstractInsnNode ain = m.instructions.get(i);
					if (ain.getOpcode() == Opcodes.BIPUSH) {
						IntInsnNode iin = (IntInsnNode)ain;
						if (iin.operand == 100) {
							iin.operand = 1000; //increase history to 1000 lines
						}
					}
				}
				ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
				break;
			}*/
				/*
			case TILEUPDATE: { DOES NOT WORK
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72939_s", "updateEntities", "()V");
				String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_145845_h" : "updateEntity";
				MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(m, "net/minecraft/tileentity/TileEntity", name, "()V");
				LabelNode exit = new LabelNode();
				InsnList add = new InsnList();
				add.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
				add.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/TileUpdateEvent"));
				add.add(new InsnNode(Opcodes.DUP));
				add.add(new VarInsnNode(Opcodes.ALOAD, 8));
				add.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/TileUpdateEvent", "<init>", "(Lnet/minecraft/tileentity/TileEntity;)V", false));
				add.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
				add.add(new JumpInsnNode(Opcodes.IFNE, exit));
				m.instructions.insertBefore(min, add);
				m.instructions.insert(min, exit);
				ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
				break;
			}*//*
				case TILEUPDATE: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72939_s", "updateEntities", "()V");
					String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_145837_r" : "isInvalid";
					AbstractInsnNode pre = ReikaASMHelper.getNthMethodCall(cn, m, "net/minecraft/tileentity/TileEntity", name, "()Z", 1).getPrevious();
					AbstractInsnNode post = ReikaASMHelper.getNthMethodCall(cn, m, "net/minecraft/tileentity/TileEntity", name, "()Z", 2).getPrevious();

					while (post.getPrevious() instanceof FrameNode) {
						post = post.getPrevious();
					}

					LabelNode label = new LabelNode();
					InsnList evt = new InsnList();
					evt.add(new VarInsnNode(Opcodes.ALOAD, 4));
					evt.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/TileUpdateEvent", "fire", "(Lnet/minecraft/tileentity/TileEntity;)Z", false));
					evt.add(new JumpInsnNode(Opcodes.IFEQ, label));

					m.instructions.insertBefore(post, label);
					m.instructions.insertBefore(pre, evt);
					ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
					}*//*
				case TILEUPDATE1: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72939_s", "updateEntities", "()V");
					String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_145845_h" : "updateEntity";
					MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/tileentity/TileEntity", name, "()V");
					min.name = "updateEntity_DAPIRelay";

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case TILEUPDATE2: {

					LabelNode L1 = new LabelNode();
					LabelNode L2 = new LabelNode();

					InsnList insns = new InsnList();

					insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/TileUpdateEvent", "fire", "(Lnet/minecraft/tileentity/TileEntity;)Z", false));
					insns.add(new JumpInsnNode(Opcodes.IFNE, L1));
					insns.add(L2);
					insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/tileentity/TileEntity", "updateEntity", "()V", false));
					insns.add(L1);
					//insns.add(new FrameNode(Opcodes.F_SAME));
					insns.add(new InsnNode(Opcodes.RETURN));

					ReikaASMHelper.addMethod(cn, insns, "updateEntity_DAPIRelay", "()V", Modifier.PUBLIC);

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}*/
				case TILEUPDATE: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72939_s", "updateEntities", "()V");
					String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_145837_r" : "isInvalid";
					String name2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_72899_e" : "blockExists";
					MethodInsnNode loc1 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/tileentity/TileEntity", name, "()Z");
					MethodInsnNode loc2 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", name2, "(III)Z");
					JumpInsnNode jump = (JumpInsnNode)loc2.getNext();
					jump.setOpcode(Opcodes.IFNE);
					ReikaASMHelper.deleteFrom(m.instructions, loc1, loc2);
					m.instructions.insertBefore(jump, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/TileUpdateEvent", "fire", "(Lnet/minecraft/tileentity/TileEntity;)Z", false));

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case FURNACEUPDATE: {
					InsnList pre = new InsnList();
					LabelNode L1 = new LabelNode();
					LabelNode L2 = new LabelNode();
					pre.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					pre.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/FurnaceUpdateEvent$Pre"));
					pre.add(new InsnNode(Opcodes.DUP));
					pre.add(new VarInsnNode(Opcodes.ALOAD, 0));
					pre.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/FurnaceUpdateEvent$Pre", "<init>", "(Lnet/minecraft/tileentity/TileEntityFurnace;)V", false));
					pre.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					pre.add(new JumpInsnNode(Opcodes.IFEQ, L1));
					pre.add(L2);
					pre.add(new InsnNode(Opcodes.RETURN));
					pre.add(L1);

					InsnList post = new InsnList();
					post.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					post.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/FurnaceUpdateEvent$Post"));
					post.add(new InsnNode(Opcodes.DUP));
					post.add(new VarInsnNode(Opcodes.ALOAD, 0));
					post.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/FurnaceUpdateEvent$Post", "<init>", "(Lnet/minecraft/tileentity/TileEntityFurnace;)V", false));
					post.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					post.add(new InsnNode(Opcodes.POP));

					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_145845_h", "updateEntity", "()V");
					AbstractInsnNode ret = ReikaASMHelper.getLastInsn(m.instructions, Opcodes.RETURN);
					m.instructions.insert(pre);
					m.instructions.insertBefore(ret, post);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case PLAYERRENDERPASS: {
					InsnList insns = new InsnList();
					insns.add(new InsnNode(Opcodes.ICONST_1));
					insns.add(new InsnNode(Opcodes.IRETURN));
					ReikaASMHelper.addMethod(cn, insns, "shouldRenderInPass", "(I)Z", Modifier.PUBLIC); //Forge method, no SRG
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case MUSICEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73660_a", "update", "()V");

					String handler = FMLForgePlugin.RUNTIME_DEOBF ? "func_147118_V" : "getSoundHandler"; //()Lnet/minecraft/client/audio/SoundHandler;
					String play = FMLForgePlugin.RUNTIME_DEOBF ? "func_147682_a" : "playSound"; //(Lnet/minecraft/client/audio/ISound;)V

					LabelNode L5 = new LabelNode();

					InsnList post = new InsnList();

					post.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
					post.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/PlayMusicEvent"));
					post.add(new InsnNode(Opcodes.DUP));
					post.add(new VarInsnNode(Opcodes.ALOAD, 0));
					post.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/audio/MusicTicker", "field_147678_c", "Lnet/minecraft/client/audio/ISound;"));
					post.add(new VarInsnNode(Opcodes.ALOAD, 0));
					post.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/audio/MusicTicker", "field_147676_d", "I"));
					post.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/PlayMusicEvent", "<init>", "(Lnet/minecraft/client/audio/ISound;I)V", false));
					post.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
					post.add(new JumpInsnNode(Opcodes.IFNE, L5));

					AbstractInsnNode loc = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/client/audio/SoundHandler", play, "(Lnet/minecraft/client/audio/ISound;)V");

					m.instructions.insert(loc, L5);

					loc = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(loc), Opcodes.ALOAD, 0); //Get last ALOAD 0 before
					loc = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(loc), Opcodes.ALOAD, 0); //Get next last ALOAD 0

					m.instructions.insertBefore(loc, post);

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");

					break;
				}
				case SOUNDEVENTS: {

					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_148594_a", "getNormalizedVolume", "(Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/SoundPoolEntry;Lnet/minecraft/client/audio/SoundCategory;)F");
					m.instructions.clear();
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/SoundVolumeEvent", "fire", "(Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/SoundPoolEntry;Lnet/minecraft/client/audio/SoundCategory;)F", false));
					m.instructions.add(new InsnNode(Opcodes.FRETURN));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");

					m = ReikaASMHelper.getMethodByName(cn, "func_148606_a", "getNormalizedPitch", "(Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/SoundPoolEntry;)F");
					m.instructions.clear();
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/SoundPitchEvent", "fire", "(Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/SoundPoolEntry;)F", false));
					m.instructions.add(new InsnNode(Opcodes.FRETURN));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");

					break;
				}
				case CLOUDRENDEREVENT1: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_74309_c", "shouldRenderClouds", "()Z");
					m.instructions.clear();
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/CloudRenderEvent", "fire", "()Z", false));
					m.instructions.add(new InsnNode(Opcodes.IRETURN));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case CLOUDRENDEREVENT2: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82829_a", "renderCloudsCheck", "(Lnet/minecraft/client/renderer/RenderGlobal;F)V");
					AbstractInsnNode loc = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IFEQ);
					while (loc.getPrevious() instanceof FieldInsnNode || loc.getPrevious() instanceof MethodInsnNode) {
						m.instructions.remove(loc.getPrevious());
					}
					m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/CloudRenderEvent", "fire", "()Z", false));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case PROFILER: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_76320_a", "startSection", "(Ljava/lang/String;)V");
					m.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/ProfileEvent", "fire", "(Ljava/lang/String;)V", false));
					m.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 1));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case SPRINTEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147357_a", "processEntityAction", "(Lnet/minecraft/network/play/client/C0BPacketEntityAction;)V");
					String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_70031_b" : "setSprinting";
					AbstractInsnNode call = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/entity/player/EntityPlayerMP", func, "(Z)V");
					InsnList evt = new InsnList();
					evt.add(new VarInsnNode(Opcodes.ALOAD, 0));
					String field = FMLForgePlugin.RUNTIME_DEOBF ? "field_147369_b" : "playerEntity";
					evt.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/NetHandlerPlayServer", field, "Lnet/minecraft/entity/player/EntityPlayerMP;"));
					evt.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PlayerSprintEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false));
					m.instructions.insert(call, evt);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}/*
				case JUMPCHECKEVENTSERVER: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147347_a", "processPlayer", "(Lnet/minecraft/network/play/client/C03PacketPlayer;)V");
					String name = FMLForgePlugin.RUNTIME_DEOBF ? "field_70122_E" : "onGround";
					AbstractInsnNode call = ReikaASMHelper.getNthFieldCall(cn, m, "net/minecraft/entity/player/EntityPlayerMP", name, 2);
					AbstractInsnNode loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(call), Opcodes.ALOAD, 0);
					AbstractInsnNode loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(call), Opcodes.DCMPL).getNext();
					LabelNode tgt = ((JumpInsnNode)loc2).label;

					Collection<AbstractInsnNode> c = new ArrayList();
					int pre = m.instructions.indexOf(loc1);

					for (int i = pre; i <= m.instructions.indexOf(loc2); i++) {
						c.add(m.instructions.get(i));
					}

					for (AbstractInsnNode ain : c) {
						m.instructions.remove(ain);
					}

					InsnList evt = new InsnList();

					evt.add(new VarInsnNode(Opcodes.ALOAD, 0));
					evt.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/NetHandlerPlayServer", "playerEntity", "Lnet/minecraft/entity/player/EntityPlayerMP;"));
					evt.add(new VarInsnNode(Opcodes.ALOAD, 1));
					//evt.add(new VarInsnNode(Opcodes.DLOAD, 15));
					evt.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/JumpCheckEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/network/play/client/C03PacketPlayer;)Z", false));
					evt.add(new JumpInsnNode(Opcodes.IFEQ, tgt));

					m.instructions.insertBefore(m.instructions.get(pre), evt);

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case JUMPCHECKEVENTCLIENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70636_d", "onLivingUpdate", "()V");
					String name = FMLForgePlugin.RUNTIME_DEOBF ? "field_70122_E" : "onGround";
					AbstractInsnNode call = ReikaASMHelper.getFirstFieldCall(cn, m, "net/minecraft/entity/EntityLivingBase", name);
					AbstractInsnNode loc1 = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(call), Opcodes.ALOAD, 0);
					AbstractInsnNode loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(call), Opcodes.ALOAD, 0).getNext().getNext();
					LabelNode tgt = ((JumpInsnNode)loc2).label;

					Collection<AbstractInsnNode> c = new ArrayList();
					int pre = m.instructions.indexOf(loc1);

					for (int i = pre; i <= m.instructions.indexOf(loc2); i++) {
						c.add(m.instructions.get(i));
					}

					for (AbstractInsnNode ain : c) {
						m.instructions.remove(ain);
					}

					InsnList evt = new InsnList();

					evt.add(new VarInsnNode(Opcodes.ALOAD, 0));
					evt.add(new VarInsnNode(Opcodes.ALOAD, 0));
					evt.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/EntityLivingBase", "jumpTicks", "I"));
					evt.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/JumpCheckEventClient", "fire", "(Lnet/minecraft/entity/EntityLivingBase;I)Z", false));
					evt.add(new JumpInsnNode(Opcodes.IFEQ, tgt));

					m.instructions.insertBefore(m.instructions.get(pre), evt);

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}*/
				case MOBTARGETEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72846_b", "getClosestVulnerablePlayer", "(DDDD)Lnet/minecraft/entity/player/EntityPlayer;");
					m.instructions.clear();
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 1));
					m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 3)); //+2 since double is 2 spots
					m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 5));
					m.instructions.add(new VarInsnNode(Opcodes.DLOAD, 7));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Libraries/World/ReikaWorldHelper", "getClosestVulnerablePlayer", "(Lnet/minecraft/world/World;DDDD)Lnet/minecraft/entity/player/EntityPlayer;", false));
					m.instructions.add(new InsnNode(Opcodes.ARETURN));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}/*
				case TOOLTIPEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77624_a", "addInformation", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;Z)V");
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
					m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/ItemTooltipEvent", "fire", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;Z)V", false));
					m.instructions.add(new InsnNode(Opcodes.RETURN));
					break;
				}*/
				case PERMUTEDBIOMEREG: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "registerVanillaBiomes", "()V");
					m.instructions.insertBefore(m.instructions.getLast(), new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/DragonAPIClassTransformer", "registerPermutedBiomesToDictionary", "()V", false));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case BLOCKTICKEVENT: {
					String sig = "(Lnet/minecraft/world/World;IIILjava/util/Random;)V";
					String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_149674_a" : "updateTick";
					int shift = ((bukkitFlags & (BukkitBitflags.CAULDRON.flag | BukkitBitflags.THERMOS.flag)) != 0) ? 3 : 0;

					InsnList fire = new InsnList();
					fire.add(new VarInsnNode(Opcodes.ALOAD, 0));
					fire.add(new VarInsnNode(Opcodes.ILOAD, 16+shift));
					fire.add(new VarInsnNode(Opcodes.ILOAD, 5+shift));
					fire.add(new InsnNode(Opcodes.IADD));
					fire.add(new VarInsnNode(Opcodes.ILOAD, 18+shift));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 13+shift));
					String getY = FMLForgePlugin.RUNTIME_DEOBF ? "func_76662_d" : "getYLocation";
					fire.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/chunk/storage/ExtendedBlockStorage", getY, "()I", false));
					fire.add(new InsnNode(Opcodes.IADD));
					fire.add(new VarInsnNode(Opcodes.ILOAD, 17+shift));
					fire.add(new VarInsnNode(Opcodes.ILOAD, 6+shift));
					fire.add(new InsnNode(Opcodes.IADD));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 19+shift));
					fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "NATURAL", "LReika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags;"));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "flag", "I"));
					fire.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent", "fire", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V", false));
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147456_g", "func_147456_g", "()V");
					AbstractInsnNode ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func, sig);
					m.instructions.insert(ain, fire);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");

					String xc = FMLForgePlugin.RUNTIME_DEOBF ? "field_77183_a" : "xCoord";
					String yc = FMLForgePlugin.RUNTIME_DEOBF ? "field_77181_b" : "yCoord";
					String zc = FMLForgePlugin.RUNTIME_DEOBF ? "field_77182_c" : "zCoord";
					fire.clear();
					fire.add(new VarInsnNode(Opcodes.ALOAD, 0));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 7));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", xc, "I"));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 7));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", yc, "I"));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 7));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", zc, "I"));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 9));
					fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "SCHEDULED", "LReika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags;"));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "flag", "I"));
					fire.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent", "fire", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V", false));
					m = ReikaASMHelper.getMethodByName(cn, "func_147454_a", "scheduleBlockUpdateWithPriority", "(IIILnet/minecraft/block/Block;II)V");
					ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func, sig);
					m.instructions.insert(ain, fire);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");

					fire.clear();
					fire.add(new VarInsnNode(Opcodes.ALOAD, 0));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 4));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", xc, "I"));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 4));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", yc, "I"));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 4));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/NextTickListEntry", zc, "I"));
					fire.add(new VarInsnNode(Opcodes.ALOAD, 6));
					fire.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "SCHEDULED", "LReika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags;"));
					fire.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent$UpdateFlags", "flag", "I"));
					fire.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BlockTickEvent", "fire", "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V", false));
					m = ReikaASMHelper.getMethodByName(cn, "func_72955_a", "tickUpdates", "(Z)Z");
					ain = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func, sig);
					m.instructions.insert(ain, fire);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 3!");
					break;
				}
				case ADDCRAFTINGEVENT: { //replace list with one that fires events
					//String name = FMLForgePlugin.RUNTIME_DEOBF ? "field_77597_b" : "recipes";
					//for (FieldNode f : cn.fields) {
					//	if (f.name.equals(name)) {
					//
					//	}
					//}
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "()V");
					TypeInsnNode type = (TypeInsnNode)ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.NEW);
					MethodInsnNode cons = (MethodInsnNode)type.getNext().getNext();

					String s = "Reika/DragonAPI/Instantiable/Data/Collections/EventRecipeList";

					type.desc = s;
					cons.owner = s;

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case ADDSMELTINGEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_151394_a", "func_151394_a", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;F)V");
					LabelNode L1 = new LabelNode();
					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new VarInsnNode(Opcodes.ALOAD, 2));
					li.add(new VarInsnNode(Opcodes.FLOAD, 3));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/AddSmeltingEvent", "fire", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;F)Z", false));
					li.add(new JumpInsnNode(Opcodes.IFEQ, L1));
					m.instructions.insert(li);

					AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.RETURN);
					m.instructions.insertBefore(ain, L1);
					break;
				}
				case ENDSEED: {
					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/DimensionSeedEvent", "fire", "(Lnet/minecraft/world/WorldProvider;)J", false));
					li.add(new InsnNode(Opcodes.LRETURN));
					String name = FMLForgePlugin.RUNTIME_DEOBF ? "getSeed" : "getSeed"; //Forge
					ReikaASMHelper.addMethod(cn, li, name, "()J", Modifier.PUBLIC);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case NETHERSEED: {
					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/DimensionSeedEvent", "fire", "(Lnet/minecraft/world/WorldProvider;)J", false));
					li.add(new InsnNode(Opcodes.LRETURN));
					String name = FMLForgePlugin.RUNTIME_DEOBF ? "getSeed" : "getSeed"; //Forge
					ReikaASMHelper.addMethod(cn, li, name, "()J", Modifier.PUBLIC);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case MUSICTYPEEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147109_W", "func_147109_W", "()Lnet/minecraft/client/audio/MusicTicker$MusicType;");
					m.instructions.clear();
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/MusicTypeEvent", "fire", "(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/client/audio/MusicTicker$MusicType;", false));
					m.instructions.add(new InsnNode(Opcodes.ARETURN));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				/*
				case ITEMSTACKNULL: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_150996_a", "(Lnet/minecraft/item/Item;)V");
					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/DragonAPIClassTransformer", "validateItemStack", "(Lnet/minecraft/item/Item;)V", false));
					m.instructions.insert(li);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
				}
				 */
				case LIGHTMAP: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78472_g", "updateLightmap", "(F)V");
					String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_110564_a" : "updateDynamicTexture";
					AbstractInsnNode loc = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/client/renderer/texture/DynamicTexture", func, "()V");
					loc = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(loc), Opcodes.ALOAD, 0);
					m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/LightmapEvent", "fire", "()V", false));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case CHATEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146234_a", "printChatMessageWithOptionalDeletion", "(Lnet/minecraft/util/IChatComponent;I)V");
					m.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/ChatEvent", "firePre", "(Lnet/minecraft/util/IChatComponent;)V", false));
					m.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 1));

					AbstractInsnNode loc = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEINTERFACE);
					m.instructions.insert(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/ChatEvent", "firePost", "(Lnet/minecraft/util/IChatComponent;)V", false));
					m.instructions.insert(loc, new VarInsnNode(Opcodes.ALOAD, 1));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case POTIONITEM: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70184_a", "onImpact", "(Lnet/minecraft/util/MovingObjectPosition;)V");
					AbstractInsnNode start = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.GETSTATIC);
					AbstractInsnNode end = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.ASTORE);
					AbstractInsnNode pre = start.getPrevious();
					String item = FMLForgePlugin.RUNTIME_DEOBF ? "field_151068_bn" : "potionitem";
					String stack = FMLForgePlugin.RUNTIME_DEOBF ? "field_70197_d" : "potionDamage";
					String getItem = FMLForgePlugin.RUNTIME_DEOBF ? "func_77973_b" : "getItem";
					String getFX = FMLForgePlugin.RUNTIME_DEOBF ? "func_77832_l" : "getEffects";

					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/projectile/EntityPotion", stack, "Lnet/minecraft/item/ItemStack;"));
					li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", getItem, "()Lnet/minecraft/item/Item;", false));
					li.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/item/ItemPotion"));
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/projectile/EntityPotion", stack, "Lnet/minecraft/item/ItemStack;"));
					li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemPotion", getFX, "(Lnet/minecraft/item/ItemStack;)Ljava/util/List;", false));
					li.add(new VarInsnNode(Opcodes.ASTORE, 2));

					ReikaASMHelper.deleteFrom(m.instructions, start, end);

					m.instructions.insert(pre, li);

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case RAYTRACEEVENT1:
				case RAYTRACEEVENT2:
				case RAYTRACEEVENT3: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");

					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/EntityAboutToRayTraceEvent", "fire", "(Lnet/minecraft/entity/Entity;)V", false));

					String func1 = FMLForgePlugin.RUNTIME_DEOBF ? "func_149668_a" : "getCollisionBoundingBoxFromPool";
					String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_72933_a" : "rayTraceBlocks";
					String func3 = FMLForgePlugin.RUNTIME_DEOBF ? "func_147447_a" : "func_147447_a";

					String world = FMLForgePlugin.RUNTIME_DEOBF ? "field_70170_p" : "worldObj";

					AbstractInsnNode min1 = null;
					AbstractInsnNode min2 = null;
					AbstractInsnNode min3 = null;

					try {
						min1 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func1, "(Lnet/minecraft/world/World;III)Lnet/minecraft/util/AxisAlignedBB;");
					}
					catch (NoSuchASMMethodInstructionException e) {

					}
					try {
						min2 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", func2, "(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;");
					}
					catch (NoSuchASMMethodInstructionException e) {

					}
					try {
						min3 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", func3, "(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZZ)Lnet/minecraft/util/MovingObjectPosition;");
					}
					catch (NoSuchASMMethodInstructionException e) {

					}

					AbstractInsnNode pre1 = min1 != null ? ReikaASMHelper.getLastNonZeroALOADBefore(m.instructions, m.instructions.indexOf(min1)) : null;
					AbstractInsnNode pre2 = min2 != null ? ReikaASMHelper.getLastFieldRefBefore(m.instructions, m.instructions.indexOf(min2), world).getPrevious() : null;
					AbstractInsnNode pre3 = min3 != null ? ReikaASMHelper.getLastFieldRefBefore(m.instructions, m.instructions.indexOf(min3), world).getPrevious() : null;

					if (pre1 != null) {
						m.instructions.insertBefore(pre1, ReikaASMHelper.copyInsnList(li));
					}
					if (pre2 != null) {
						m.instructions.insertBefore(pre2, ReikaASMHelper.copyInsnList(li));
					}
					if (pre3 != null) {
						m.instructions.insertBefore(pre3, ReikaASMHelper.copyInsnList(li));
					}

					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case ENDERATTACKTPEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70097_a", "attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z");

					AbstractInsnNode loc = ReikaASMHelper.getNthOpcode(m.instructions, Opcodes.INSTANCEOF, 3);

					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new VarInsnNode(Opcodes.FLOAD, 2));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/EnderAttackTPEvent", "fire", "(Lnet/minecraft/entity/monster/EntityEnderman;Lnet/minecraft/util/DamageSource;F)Z", false));

					//ReikaASMHelper.changeOpcode(loc.getNext(), Opcodes.IFNE);
					m.instructions.insertBefore(loc.getPrevious(), li);
					m.instructions.remove(loc.getPrevious());
					m.instructions.remove(loc);

					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));

					//m.instructions.clear();
					//m.instructions.add(new InsnNode(Opcodes.ICONST_0));
					//m.instructions.add(new InsnNode(Opcodes.IRETURN));

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case ATTACKAGGROEVENT1: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70097_a", "attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z");
					AbstractInsnNode loc = ReikaASMHelper.getNthOfOpcodes(m.instructions, 3, Opcodes.IF_ACMPEQ, Opcodes.IF_ACMPNE);

					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new VarInsnNode(Opcodes.FLOAD, 2));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/AttackAggroEvent", "fire", "(Lnet/minecraft/entity/monster/EntityMob;Lnet/minecraft/util/DamageSource;F)Z", false));

					ReikaASMHelper.changeOpcode(loc, Opcodes.IFEQ);
					m.instructions.insertBefore(loc.getPrevious().getPrevious(), li);
					m.instructions.remove(loc.getPrevious().getPrevious());
					m.instructions.remove(loc.getPrevious());

					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case ATTACKAGGROEVENT2: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70097_a", "attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z");
					AbstractInsnNode loc = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.INSTANCEOF);

					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new VarInsnNode(Opcodes.FLOAD, 2));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/AttackAggroEvent", "fire", "(Lnet/minecraft/entity/monster/EntityMob;Lnet/minecraft/util/DamageSource;F)Z", false));

					//ReikaASMHelper.changeOpcode(loc.getNext(), Opcodes.IFNE);
					m.instructions.insertBefore(loc.getPrevious(), li);
					m.instructions.remove(loc.getPrevious());
					m.instructions.remove(loc);

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case PIGZOMBIEAGGROSPREADEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70097_a", "attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z");
					AbstractInsnNode loc = ReikaASMHelper.getNthOpcode(m.instructions, Opcodes.INSTANCEOF, 1); //would be 2, but since ASM remove the other, is 1

					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode)loc.getPrevious()).var));
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new VarInsnNode(Opcodes.FLOAD, 2));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PigZombieAggroSpreadEvent", "fire", "(Lnet/minecraft/entity/monster/EntityPigZombie;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/DamageSource;F)Z", false));

					//ReikaASMHelper.changeOpcode(loc.getNext(), Opcodes.IFNE);
					m.instructions.insertBefore(loc.getPrevious(), li);
					m.instructions.remove(loc.getPrevious());
					m.instructions.remove(loc);

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case BIOMEMUTATIONEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_75904_a", "getInts", "(IIII)[I");
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					String get = FMLForgePlugin.RUNTIME_DEOBF ? "func_150568_d" : "getBiome";
					Object[] patt = {
							Opcodes.ILOAD,
							new IntInsnNode(Opcodes.SIPUSH, 128),
							new InsnNode(Opcodes.IADD),
							new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/world/biome/BiomeGenBase", get, "(I)Lnet/minecraft/world/biome/BiomeGenBase;", false),
							Opcodes.IFNULL
					};
					VarInsnNode id = (VarInsnNode)ReikaASMHelper.getPattern(m.instructions, patt);
					JumpInsnNode jump = (JumpInsnNode)m.instructions.get(m.instructions.indexOf(id)+patt.length-1);
					int var = id.var; //10

					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ILOAD, 1));
					li.add(new VarInsnNode(Opcodes.ILOAD, 2));
					li.add(new VarInsnNode(Opcodes.ILOAD, 8));
					li.add(new VarInsnNode(Opcodes.ILOAD, 9));
					li.add(new VarInsnNode(Opcodes.ILOAD, var));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BiomeMutationEvent", "fireTry", "(Lnet/minecraft/world/gen/layer/GenLayer;IIIII)Z", false));

					ReikaASMHelper.changeOpcode(jump, Opcodes.IFEQ);
					ReikaASMHelper.deleteFrom(m.instructions, id, jump.getPrevious());
					m.instructions.insertBefore(jump, li);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");


					AbstractInsnNode end = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IASTORE);
					AbstractInsnNode start = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(end), Opcodes.ILOAD, var);
					AbstractInsnNode pre = start.getPrevious();

					li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ILOAD, 1));
					li.add(new VarInsnNode(Opcodes.ILOAD, 2));
					li.add(new VarInsnNode(Opcodes.ILOAD, 8));
					li.add(new VarInsnNode(Opcodes.ILOAD, 9));
					li.add(new VarInsnNode(Opcodes.ILOAD, var));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BiomeMutationEvent$GetMutatedBiomeEvent", "fireGet", "(Lnet/minecraft/world/gen/layer/GenLayer;IIIII)I", false));
					li.add(new InsnNode(Opcodes.IASTORE));
					ReikaASMHelper.deleteFrom(m.instructions, start, end);
					m.instructions.insert(pre, li);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");


					id = (VarInsnNode)ReikaASMHelper.getPattern(m.instructions, patt);
					jump = (JumpInsnNode)m.instructions.get(m.instructions.indexOf(id)+patt.length-1);
					var = id.var; //13

					li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ILOAD, 1));
					li.add(new VarInsnNode(Opcodes.ILOAD, 2));
					li.add(new VarInsnNode(Opcodes.ILOAD, 8));
					li.add(new VarInsnNode(Opcodes.ILOAD, 9));
					li.add(new VarInsnNode(Opcodes.ILOAD, var));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BiomeMutationEvent", "fireTry", "(Lnet/minecraft/world/gen/layer/GenLayer;IIIII)Z", false));

					ReikaASMHelper.changeOpcode(jump, Opcodes.IFEQ);
					ReikaASMHelper.deleteFrom(m.instructions, id, jump.getPrevious());
					m.instructions.insertBefore(jump, li);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 3!");


					AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.IINC, var, 128);
					if (ain == null) {
						ReikaASMHelper.log("Could not find normal IINC "+var+" 128 Insn. Checking for alternate.");
						ain = ReikaASMHelper.getLastInsn(m.instructions, Opcodes.SIPUSH, 128);
						start = ain.getPrevious();
						end = ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(ain), Opcodes.ISTORE);
					}
					else {
						start = ain;
						end = start;
					}
					pre = start.getPrevious();

					li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ILOAD, 1));
					li.add(new VarInsnNode(Opcodes.ILOAD, 2));
					li.add(new VarInsnNode(Opcodes.ILOAD, 8));
					li.add(new VarInsnNode(Opcodes.ILOAD, 9));
					li.add(new VarInsnNode(Opcodes.ILOAD, var));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/BiomeMutationEvent$GetMutatedBiomeEvent", "fireGet", "(Lnet/minecraft/world/gen/layer/GenLayer;IIIII)I", false));
					li.add(new VarInsnNode(Opcodes.ISTORE, var));
					ReikaASMHelper.deleteFrom(m.instructions, start, end);
					m.instructions.insert(pre, li);
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));

					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 4!");
					break;
				}
				case CRASHNOTIFICATIONS: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_71504_g", "populateEnvironment", "()V");
					InsnList li = new InsnList();
					li.add(new FieldInsnNode(Opcodes.GETSTATIC, "Reika/DragonAPI/Auxiliary/Trackers/CrashNotifications", "instance", "LReika/DragonAPI/Auxiliary/Trackers/CrashNotifications;"));
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Reika/DragonAPI/Auxiliary/Trackers/CrashNotifications", "notifyCrash", "(Lnet/minecraft/crash/CrashReport;)V", false));
					m.instructions.insertBefore(m.instructions.getLast(), li);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case KEEPINVEVENT: {
					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PlayerKeepInventoryEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/player/EntityPlayer;)Z", false));

					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_71049_a", "clonePlayer", "(Lnet/minecraft/entity/player/EntityPlayer;Z)V");
					AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, "keepInventory");
					AbstractInsnNode load = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD);
					ReikaASMHelper.deleteFrom(m.instructions, load.getNext(), ain.getNext());
					m.instructions.insert(load, ReikaASMHelper.copyInsnList(li));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 1!");

					li = new InsnList();
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PlayerKeepInventoryEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));

					m = ReikaASMHelper.getMethodByName(cn, "func_70645_a", "onDeath", "(Lnet/minecraft/util/DamageSource;)V");
					ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, "keepInventory");
					load = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD);
					ReikaASMHelper.deleteFrom(m.instructions, load.getNext(), ain.getNext());
					m.instructions.insert(load, ReikaASMHelper.copyInsnList(li));
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler 2!");
					break;
				}
				case KEEPINVEVENT2: {
					InsnList li = new InsnList();
					//li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PlayerKeepInventoryEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;)Z", false));

					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70645_a", "onDeath", "(Lnet/minecraft/util/DamageSource;)V");
					if (ReikaASMHelper.checkForClass("api.player.forge.PlayerAPITransformer")) {
						m = ReikaASMHelper.getMethodByName(cn, "localOnDeath", "(Lnet/minecraft/util/DamageSource;)V"); //Try his method instead
					}

					AbstractInsnNode ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.LDC, "keepInventory");
					if (ain == null)
						ReikaASMHelper.throwConflict(this, cn, m, "Could not find 'keepInventory' gamerule lookup");
					AbstractInsnNode load = ReikaASMHelper.getLastOpcodeBefore(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD);
					ReikaASMHelper.deleteFrom(m.instructions, load.getNext(), ain.getNext());
					m.instructions.insert(load, ReikaASMHelper.copyInsnList(li));
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case HOTBARKEYEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146983_a", "checkHotbarKeys", "(I)Z");
					String f = FMLForgePlugin.RUNTIME_DEOBF ? "field_147006_u" : "theSlot";
					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", f, "Lnet/minecraft/inventory/Slot;"));
					li.add(new VarInsnNode(Opcodes.ILOAD, 2));
					li.add(new VarInsnNode(Opcodes.ILOAD, 1));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/HotbarKeyEvent", "fire", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;II)Z", false));
					AbstractInsnNode jump = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IF_ICMPNE);
					AbstractInsnNode end = jump.getPrevious();
					AbstractInsnNode start = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(end), Opcodes.ILOAD, 1);
					ReikaASMHelper.deleteFrom(m.instructions, start, end);
					m.instructions.insertBefore(jump, li);
					ReikaASMHelper.changeOpcode(jump, Opcodes.IFEQ);
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case RENDERBLOCKEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147892_a", "updateRenderer", "(Lnet/minecraft/entity/EntityLivingBase;)V");
					String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_147805_b" : "renderBlockByRenderType";
					MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/client/renderer/RenderBlocks", name, "(Lnet/minecraft/block/Block;III)Z");

					String evt = "Reika/DragonAPI/Instantiable/Event/Client/RenderBlockAtPosEvent";
					/*
					m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, evt, "fire", "(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/client/renderer/RenderBlocks;Lnet/minecraft/block/Block;IIII)Z", false));
					m.instructions.insert(ain, new VarInsnNode(Opcodes.ILOAD, 17)); //renderpass "k2"
					AbstractInsnNode load = ain.getPrevious();
					while (load.getPrevious() instanceof VarInsnNode) {
						load = load.getPrevious();
					}
					m.instructions.insert(load, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.remove(ain);
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					 */

					name = FMLForgePlugin.RUNTIME_DEOBF ? "func_149701_w" : "getRenderBlockPass";
					MethodInsnNode checkpass = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", name, "()I");
					VarInsnNode store = (VarInsnNode)ReikaASMHelper.getFirstOpcodeAfter(m.instructions, m.instructions.indexOf(checkpass), Opcodes.ISTORE);
					int pass = -1;
					for (int i = m.instructions.indexOf(store); i < m.instructions.size(); i++) {
						AbstractInsnNode ain = m.instructions.get(i);
						if (ain.getOpcode() == Opcodes.ILOAD) {
							VarInsnNode vin = (VarInsnNode)ain;
							if (vin.var != store.var) {
								pass = vin.var;
								break;
							}
						}
					}

					min.desc = "(Lnet/minecraft/client/renderer/RenderBlocks;Lnet/minecraft/block/Block;IIILnet/minecraft/client/renderer/WorldRenderer;I)Z";
					min.name = "fire";
					min.owner = evt;
					min.setOpcode(Opcodes.INVOKESTATIC);
					m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, pass)); //renderpass "k2"

					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case MOUSEOVEREVENT: {
					if (CoreModDetection.VIVE.isInstalled()) {
						ReikaASMHelper.log("Skipping "+this+" ASM handler, not compatible with Vive!");
						break;
					}
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_78473_a", "getMouseOver", "(F)V");
					AbstractInsnNode ain = ReikaASMHelper.getNthOpcode(m.instructions, Opcodes.PUTFIELD, 2);
					m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/GetMouseoverEvent", "fire", "(F)V", false));
					m.instructions.insert(ain, new VarInsnNode(Opcodes.FLOAD, 1));
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case POSTITEMUSEEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_77943_a", "tryPlaceItemIntoWorld", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z");
					AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new VarInsnNode(Opcodes.ALOAD, 2));
					li.add(new VarInsnNode(Opcodes.ILOAD, 3));
					li.add(new VarInsnNode(Opcodes.ILOAD, 4));
					li.add(new VarInsnNode(Opcodes.ILOAD, 5));
					li.add(new VarInsnNode(Opcodes.ILOAD, 6));
					li.add(new VarInsnNode(Opcodes.FLOAD, 7));
					li.add(new VarInsnNode(Opcodes.FLOAD, 8));
					li.add(new VarInsnNode(Opcodes.FLOAD, 9));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PostItemUseEvent", "fire", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)V", false));
					m.instructions.insert(ain, li);
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case POSTITEMUSEEVENT2: {
					String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_71064_a" : "addStat";
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "onPlaceItemIntoWorld", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z");
					AbstractInsnNode ain = ReikaASMHelper.getLastInsn(m.instructions, Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", func, "(Lnet/minecraft/stats/StatBase;I)V", false);
					if (ain == null)
						throw new NullPointerException("addStat() Instruction not found!");
					InsnList li = new InsnList();
					li.add(new VarInsnNode(Opcodes.ALOAD, 0));
					li.add(new VarInsnNode(Opcodes.ALOAD, 1));
					li.add(new VarInsnNode(Opcodes.ALOAD, 2));
					li.add(new VarInsnNode(Opcodes.ILOAD, 3));
					li.add(new VarInsnNode(Opcodes.ILOAD, 4));
					li.add(new VarInsnNode(Opcodes.ILOAD, 5));
					li.add(new VarInsnNode(Opcodes.ILOAD, 6));
					li.add(new VarInsnNode(Opcodes.FLOAD, 7));
					li.add(new VarInsnNode(Opcodes.FLOAD, 8));
					li.add(new VarInsnNode(Opcodes.FLOAD, 9));
					li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/PostItemUseEvent", "fire", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)V", false));
					m.instructions.insert(ain, li);
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case ITEMSIZETEXTEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_94148_a", "renderItemOverlayIntoGUI", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V");
					AbstractInsnNode ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.ASTORE);
					int var = ((VarInsnNode)ain).var;
					m.instructions.insert(ain, new VarInsnNode(Opcodes.ASTORE, var));
					m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/ItemSizeTextEvent", "fire", "(Lnet/minecraft/item/ItemStack;Ljava/lang/String;)Ljava/lang/String;", false));
					m.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, var));
					m.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, 3));
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				/*
				case NOREROUTECUSTOMTEXMAP: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147634_a", "completeResourceLocation", "(Lnet/minecraft/util/ResourceLocation;I)Lnet/minecraft/util/ResourceLocation;");
					m.instructions.clear();
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/IO/DirectResourceManager", "getCompletedResourcePath", "(Lnet/minecraft/client/renderer/texture/TextureMap;Lnet/minecraft/util/ResourceLocation;I)Lnet/minecraft/util/ResourceLocation;", false));
					m.instructions.add(new InsnNode(Opcodes.ARETURN));
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				 */
				case FARDESPAWNEVENT: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70692_ba", "canDespawn", "()Z");
					m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/LivingFarDespawnEvent", "fire", "(Lnet/minecraft/entity/EntityLiving;)Z", false));
					m.instructions.add(new InsnNode(Opcodes.IRETURN));
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case SETBLOCKLIGHT: {
					if (CoreModDetection.FASTCRAFT.isInstalled()) {
						ReikaASMHelper.log("Skipping "+this+" ASM handler, not compatible with FastCraft!");
						break;
					}
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147465_d", "setBlock", "(IIILnet/minecraft/block/Block;II)Z");
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					String cl = /*CoreModDetection.fastCraftInstalled() ? "fastcraft/J" : */cn.name;
					String func = /*CoreModDetection.fastCraftInstalled() ? "d" : */"func_147451_t";
					String sig = /*CoreModDetection.fastCraftInstalled() ? "(Lnet/minecraft/world/World;III)Z" : */"(III)Z";
					MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, cl, func, sig);
					min.owner = "Reika/DragonAPI/ASM/DragonAPIClassTransformer";
					min.name = "updateSetBlockLighting";
					min.desc = "(IIILnet/minecraft/world/World;I)Z";
					min.setOpcode(Opcodes.INVOKESTATIC);
					m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ILOAD, 5));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case XPUPDATE: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					AbstractInsnNode loc = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.RETURN);
					m.instructions.insertBefore(loc, new VarInsnNode(Opcodes.ALOAD, 0));
					m.instructions.insertBefore(loc, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/XPUpdateEvent", "fire", "(Lnet/minecraft/entity/item/EntityXPOrb;)V", false));
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				case SPAWNERCHECK: {
					MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_98278_g", "updateSpawner", "()V");
					//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
					String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_70601_bi" : "getCanSpawnHere";
					MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/entity/EntityLiving", func, "()Z");
					min.owner = "Reika/DragonAPI/Instantiable/Event/EntitySpawnerCheckEvent";
					min.name = "fire";
					min.desc = "(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/tileentity/MobSpawnerBaseLogic;)Z";
					min.setOpcode(Opcodes.INVOKESTATIC);
					m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
					ReikaASMHelper.log("Successfully applied "+this+" ASM handler!");
					break;
				}
				default:
					break;
			}

			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS/* | ClassWriter.COMPUTE_FRAMES*/);
			cn.accept(writer);
			byte[] newdata = writer.toByteArray();
			//ClassNode vcn = new ClassNode(); //verify
			//new ClassReader(newdata).accept(vcn, 0);
			/*
			try {
				File f = new File("C:/testclass/"+cn.name+".class");
				f.getParentFile().mkdirs();
				f.createNewFile();
				FileOutputStream out = new FileOutputStream(f);
				out.write(newdata);
				out.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			 */
			return newdata;
		}

		public boolean isEnabled() {
			String tag = "-DragonAPI_disable_ASM_"+this.name();
			return !ReikaJVMParser.isArgumentPresent(tag);
		}

		public boolean isExceptionThrowing() {
			String tag = "-DragonAPI_silence_ASM_"+this.name();
			return !ReikaJVMParser.isArgumentPresent(tag);
		}
	}

	public static boolean updateSetBlockLighting(int x, int y, int z, World world, int flags) {
		if ((flags & 8) == 0) {
			return /*CoreModDetection.fastCraftInstalled() ? (boolean)ReikaReflectionHelper.cacheAndInvokeMethod("fastcraft.J", "d", world, x, y, z) : */world.func_147451_t(x, y, z);
		}
		else {
			return false;
		}
	}

	static class test extends EntityXPOrb {

		public test(World p_i1586_1_) {
			super(p_i1586_1_);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (onGround)
			{
				motionY *= -0.8999999761581421D;
			}

			++xpColor;
			++xpOrbAge;

			if (xpOrbAge >= 6000)
			{
				this.setDead();
			}
			XPUpdateEvent.fire(this);

		}
	}

	public static void validateItemStack(Item i) {
		if (i == null) {
			if (nullItemCrash || nullItemPrintout) {
				String s = "A mod created an ItemStack of a null item.\n";
				s += "Though somewhat common, this is a very bad practice as such ItemStacks crash almost immediately upon even basic use.\n";
				s += "Check the Stacktrace for the mod code coming before ItemStack.func_150996_a or ItemStack.<init>.\n";
				s += "Notify the developer of that mod.\n";
				s += "Though it is possible that in this case, the mod was not going to do anything with the item, such stacks are commonly\n";
				s += "registered the OreDictionary, dropped as entities, or added to dungeon loot tables, resulting in crashes in other mods.\n";
				s += "As a result, and the fact that a null-item stack is never necessary, it should be avoided in all cases.\n";
				if (nullItemCrash) {
					s += "You can turn this crash off in the DragonAPI configs, but you would likely crash anyways, usually soon afterward.";
					throw new IllegalStateException(s);
				}
				else {
					s += "You can disable this printout with a JVM argument, but doing so is not recommended.";
					ReikaASMHelper.logError(s);
					Thread.dumpStack();
				}
			}
		}
	}

	public static void registerPermutedBiomesToDictionary() { //Kept here to prevent premature init of ReikaBiomeHelper
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b instanceof BiomeGenMutated) {
				BiomeGenBase parent = ((BiomeGenMutated)b).baseBiome;
				BiomeDictionary.registerBiomeType(b, BiomeDictionary.getTypesForBiome(parent));
			}
		}
	}

	@Override
	public byte[] transform(String className, String className2, byte[] opcodes) {
		if (!classes.isEmpty()) {
			Collection<ClassPatch> c = classes.get(className);
			if (c != null) {
				ReikaASMHelper.activeMod = "DragonAPI";
				for (ClassPatch p : c) {
					ReikaASMHelper.log("Patching class "+p.deobfName);
					try {
						opcodes = p.apply(opcodes);
					}
					catch (ASMException e) {
						if (p.isExceptionThrowing())
							throw e;
						else {
							ReikaASMHelper.logError("ASM ERROR IN "+p+":");
							e.printStackTrace();
						}
					}
				}
				classes.remove(className); //for maximizing performance
				ReikaASMHelper.activeMod = null;
			}
		}
		return opcodes;
	}

	static {
		for (int i = 0; i < ClassPatch.list.length; i++) {
			ClassPatch p = ClassPatch.list[i];
			if (p.isEnabled()) {
				String s = !FMLForgePlugin.RUNTIME_DEOBF ? p.deobfName : p.obfName;
				classes.addValue(s, p);
			}
			else {
				ReikaASMHelper.log("******************************************************************************************");
				ReikaASMHelper.log("WARNING: ASM TRANSFORMER '"+p+"' HAS BEEN DISABLED. THIS CAN BREAK MANY THINGS.");
				ReikaASMHelper.log("IF THIS TRANSFORMER HAS BEEN DISABLED WITHOUT GOOD REASON, TURN IT BACK ON IMMEDIATELY!");
				ReikaASMHelper.log("******************************************************************************************");
			}
		}

		bukkitFlags = BukkitBitflags.calculateFlags();
		nullItemPrintout = !ReikaJVMParser.isArgumentPresent("-DragonAPI_noNullItemPrint");
	}

	private static enum BukkitBitflags {
		CAULDRON("kcauldron.KCauldron"),
		THERMOS("thermos.Thermos");

		private final String className;
		private final int flag;

		private static final BukkitBitflags[] list = values();

		private BukkitBitflags(String s) {
			className = s;
			flag = 1 << this.ordinal();
		}

		private boolean test() {
			try {
				return Class.forName(className) != null;
			}
			catch (ClassNotFoundException e) {
				return false;
			}
		}

		private static int calculateFlags() {
			int flags = 0;
			for (int i = 0; i < list.length; i++) {
				BukkitBitflags b = list[i];
				if (b.test()) {
					flags = flags | b.flag;
				}
			}
			return flags;
		}
	}
}
