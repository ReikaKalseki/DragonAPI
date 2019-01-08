package Reika.DragonAPI.ASM.Patchers.Hooks;

import java.util.ArrayList;
import java.util.HashSet;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class AFKDetection extends Patcher {

	private static HashSet<String> packetList = new HashSet(ReikaJavaLibrary.makeListFrom(
			"C0CPacketInput",
			"C03PacketPlayer",
			"C07PacketPlayerDigging",
			"C08PacketPlayerBlockPlacement",
			"C09PacketHeldItemChange",
			"C01PacketChatMessage",
			"C0APacketAnimation",
			"C0BPacketEntityAction",
			"C02PacketUseEntity",
			"C0DPacketCloseWindow",
			"C0EPacketClickWindow",
			"C11PacketEnchantItem",
			"C10PacketCreativeInventoryAction",
			"C0FPacketConfirmTransaction",
			"C12PacketUpdateSign"
			));

	public AFKDetection() {
		super("net.minecraft.network.NetHandlerPlayServer", "nh");
	}

	@Override
	protected void apply(ClassNode cn) {
		for (MethodNode m : cn.methods) {
			ArrayList<String> args = ReikaASMHelper.parseMethodArguments(m);
			if (args.size() == 1 && args.get(0).charAt(args.get(0).length()-1) == ';') {
				String s = args.get(0);
				s = s.substring(s.lastIndexOf('/')+1, s.length()-1);
				if (packetList.contains(s)) {
					m.instructions.insert(this.createCall());
					ReikaASMHelper.log("Adding listener to handler for packet "+s);
				}
			}
		}
	}

	private InsnList createCall() {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Auxiliary/Trackers/AFKTracker", "refreshPlayer", "(Lnet/minecraft/network/NetHandlerPlayServer;Lnet/minecraft/network/Packet;)V", false));
		return li;
	}

	/*

		processInput(C0CPacketInput)
		//processPlayer(C03PacketPlayer)
		//setPlayerLocation(double, double, double, float, float)
		processPlayerDigging(C07PacketPlayerDigging)
		processPlayerBlockPlacement(C08PacketPlayerBlockPlacement)
		processHeldItemChange(C09PacketHeldItemChange)
		processChatMessage(C01PacketChatMessage)
		processAnimation(C0APacketAnimation)
		processEntityAction(C0BPacketEntityAction)
		processUseEntity(C02PacketUseEntity)
		//processClientStatus(C16PacketClientStatus)
		processCloseWindow(C0DPacketCloseWindow)
		processClickWindow(C0EPacketClickWindow)
		processEnchantItem(C11PacketEnchantItem)
		processCreativeInventoryAction(C10PacketCreativeInventoryAction)
		processConfirmTransaction(C0FPacketConfirmTransaction)
		processUpdateSign(C12PacketUpdateSign)

	 */
}
