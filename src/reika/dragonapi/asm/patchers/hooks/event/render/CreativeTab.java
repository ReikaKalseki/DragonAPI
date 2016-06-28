package reika.dragonapi.asm.patchers.hooks.event.render;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class CreativeTab extends Patcher {

	public CreativeTab() {
		super("net.minecraft.client.gui.inventory.GuiContainerCreative", "bfl");
	}

	@Override
	protected void apply(ClassNode cn) {
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
					break;
				}
			}
		}
		m.instructions.insertBefore(loc1, add);
	}
}
