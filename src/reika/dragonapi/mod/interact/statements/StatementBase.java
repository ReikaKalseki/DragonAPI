/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.statements;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.StatementMouseClick;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class StatementBase implements ITriggerExternal {

	private final String id;
	private IIcon icon;
	private String iconstring;
	private final Class[] argTypes;

	protected static final String questionIcon = "dragonapi:modinteract/question";

	private static final HashMap<Class, Constructor> constructors = new HashMap();

	public StatementBase(String name, Class... args) {
		this(name, name);
	}

	public StatementBase(String name, String ico, Class... args) {
		id = name;
		iconstring = ico;
		argTypes = args;
	}

	public final String getUniqueTag() {
		return id;
	}

	@SideOnly(Side.CLIENT)
	public final IIcon getIcon() {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister ico) {
		icon = ico.registerIcon(iconstring);
	}

	@Override
	public IStatement rotateLeft() {
		return this;
	}

	protected abstract int getArgCount();

	public final int maxParameters() {
		return this.getArgCount();
	}

	public final int minParameters() {
		return this.getArgCount();
	}

	public final IStatementParameter createParameter(int index) {
		return createArgument(argTypes[index], this);
	}

	private static StatementArgument createArgument(Class<? extends StatementArgument> type, StatementBase func) {
		try {
			Constructor c = constructors.get(type);
			if (c == null) {
				c = type.getConstructor(StatementBase.class);
				constructors.put(type, c);
			}
			return (StatementArgument)c.newInstance(func);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public final boolean isTriggerActive(TileEntity target, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
		return this.evaluate(target, side, source.getTile(), parameters);
	}

	protected abstract boolean evaluate(TileEntity te, ForgeDirection dir, TileEntity caller, IStatementParameter[] args);

	/** Your implementation must have a one-arg "StatementBase" constructor. */
	public static abstract class StatementArgument implements IStatementParameter {

		private final String id;
		private IIcon icon;
		private String iconstring;
		protected final StatementBase function;

		protected StatementArgument(String name, StatementBase func) {
			id = name;
			iconstring = id;
			function = func;
		}

		public final String getUniqueTag() {
			return id;
		}

		@SideOnly(Side.CLIENT)
		public IIcon getIcon() {
			return icon;
		}

		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister ico) {
			icon = ico.registerIcon(iconstring);
		}

		public ItemStack getItemStack() {
			return null;
		}

		@Override
		public final void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
			this.onClick(source.getTile(), stack, mouse.getButton(), mouse.isShift());
		}

		protected void onClick(TileEntity caller, ItemStack held, int button, boolean shift) {

		}

		@Override
		public IStatementParameter rotateLeft() {
			return null;
		}

	}

	public static class StackArgument extends StatementArgument {

		private ItemStack stack;

		public StackArgument(StatementBase func) {
			super("stock:stack", func);
		}

		@Override
		public String getDescription() {
			return stack != null ? "Empty" : stack.getDisplayName();
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			stack = tag.getBoolean("hasStack") ? ItemStack.loadItemStackFromNBT(tag) : null;
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			if (stack != null) {
				stack.writeToNBT(tag);
				tag.setBoolean("hasStack", true);
			}
			else {
				tag.setBoolean("hasStack", false);
			}
		}

		@Override
		protected void onClick(TileEntity caller, ItemStack held, int button, boolean shift) {
			stack = held != null ? held.copy() : null;
		}

		@Override
		public final ItemStack getItemStack() {
			return stack;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon() {
			return stack != null ? stack.getIconIndex() : null;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister ico) {

		}

	}

	public static final class StateArgument extends StatementArgument {

		private final ArrayList<ArgumentState> states = new ArrayList();
		private int index;

		public StateArgument(StatementBase func) {
			super("stock:states", func);
		}

		public StateArgument addState(ArgumentState e) {
			states.add(e);
			return this;
		}

		@Override
		public String getDescription() {
			return this.activeState().getDesc();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon() {
			return this.activeState().getIcon();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister ico) {
			for (ArgumentState a : states) {
				a.registerIcon(ico);
			}
		}

		public ArgumentState activeState() {
			return states.get(index);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			index = tag.getInteger("idx");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("idx", index);
		}

		@Override
		protected void onClick(TileEntity caller, ItemStack held, int button, boolean shift) {
			if (states.isEmpty())
				return;

			int i = button == 0 ? 1 : -1;
			int offset = index+i;
			if (offset < 0) {
				index = states.size()-1;
			}
			else if (offset >= states.size()) {
				index = 0;
			}
			else {
				index = offset;
			}
		}

	}

	public static abstract class ArgumentState {

		@SideOnly(Side.CLIENT)
		public abstract IIcon getIcon();

		@SideOnly(Side.CLIENT)
		public abstract void registerIcon(IIconRegister ico);

		public abstract String getDesc();
	}

}
