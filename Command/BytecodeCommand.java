/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Stack;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Command.BytecodeCommand.BytecodeProgram.ByteCodeInstruction;
import Reika.DragonAPI.Exception.InstallationException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

/** Example use:
	LDC {dimID} [+1 stack]<br>
	INVOKESTATIC net.minecraftforge.common.DimensionManager getWorld [-1 stack, +1 stack]<br>
	LDC {x} [+1 stack]<br>
	LDC {y} [+1 stack]<br>
	LDC {z} [+1 stack]<br>
	INVOKEVIRTUAL net.minecraft.world.World getTileEntity [-4 stack, +1 stack]<br>
	GETFIELD {tileClass} {fieldName} [-1 stack, +1 stack]<br>
	OUTPUT<br>
	FLUSH [empty stack]<br>
 */
public class BytecodeCommand extends ReflectiveBasedCommand {

	private static final String PROGRAM_PATH = "DragonAPI_CommandPrograms";

	private static final HashMap<UUID, Stack> objectStack = new HashMap();

	private final HashSet<UUID> playerPermissions = new HashSet();

	private BytecodeProgram program;

	public BytecodeCommand() {
		for (String s : DragonOptions.BYTECODELIST.getStringArray()) {
			try {
				playerPermissions.add(UUID.fromString(s));
			}
			catch (IllegalArgumentException e) {
				throw new InstallationException(DragonAPIInit.instance, "Invalid UUID in whitelist for "+this.getCommandString()+": "+s);
			}
		}
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		boolean admin = this.hasPermissionToRun(ics);
		if (args[0].equalsIgnoreCase("define")) {
			if (admin) {
				try {
					String cl = args[1];
					if (cl.equalsIgnoreCase("*stack*")) {
						Object o = this.getStack(ics).pop();
						if (o instanceof Class) {
							Class c = (Class)o;
							if (this.addClassShortcut(c))
								this.sendChatToSender(ics, EnumChatFormatting.GREEN+"New shortcut defined for class: #"+c.getSimpleName()+" for "+c.getName());
							else
								this.sendChatToSender(ics, EnumChatFormatting.RED+"Class shortcut already defined for "+args[1]);
							return;
						}
					}
					Class c = this.findClass(cl);
					if (this.addClassShortcut(c))
						this.sendChatToSender(ics, EnumChatFormatting.GREEN+"New shortcut defined for class: #"+c.getSimpleName()+" for "+c.getName());
					else
						this.sendChatToSender(ics, EnumChatFormatting.RED+"Class shortcut already defined for "+args[1]);
				}
				catch (ClassNotFoundException e) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"No such class: "+args[1]);
				}
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You do not have permission to use this command in this way.");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("shortcuts")) {
			EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
			this.sendChatToSender(ics, "Defined class shortcuts: ");
			for (String s : this.getClassShortcuts().keySet()) {
				String sg = "#"+s+" - "+this.getClassShortcuts().get(s).getName();
				this.sendChatToSender(ics, sg);
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("self")) {
			if (admin) {
				EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
				this.getStack(ics).push(ep);
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Loaded self onto the stack.");
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You do not have permission to use this command in this way.");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("held")) {
			if (admin) {
				EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
				this.getStack(ics).push(ep.getCurrentEquippedItem());
				this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Loaded held item onto the stack.");
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You do not have permission to use this command in this way.");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("look")) {
			if (admin) {
				EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
				MovingObjectPosition mov = ReikaPlayerAPI.getLookedAtBlock(ep, 4.5, false);
				if (mov != null) {
					this.getStack(ics).push(ep.worldObj);
					this.getStack(ics).push(mov.blockX);
					this.getStack(ics).push(mov.blockY);
					this.getStack(ics).push(mov.blockZ);
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Loaded looked position onto the stack.");
				}
				else {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"Not looking at a block.");
				}
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You do not have permission to use this command in this way.");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("getclass")) {
			if (admin) {
				EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
				try {
					this.getStack(ics).push(this.findClass(args[1]));
					this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Loaded held item onto the stack.");
				}
				catch (ClassNotFoundException e) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"No such class '"+args[1]+'!');
				}
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You do not have permission to use this command in this way.");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("startProgram")) {
			if (admin) {
				EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
				if (args.length < 2) {
					this.sendChatToSender(ics, EnumChatFormatting.RED+"Not enough arguments: You must specify a name!");
					return;
				}
				this.startProgram(ep, args);
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You do not have permission to use this command in this way.");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("saveProgram")) {
			if (admin) {
				EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
				this.finishProgram(ep);
			}
			else {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"You do not have permission to use this command in this way.");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("runProgram")) {
			EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
			if (args.length < 2) {
				this.sendChatToSender(ics, EnumChatFormatting.RED+"Not enough arguments: You must specify a name!");
				return;
			}
			this.runProgram(ep, args[1]);
			return;
		}
		if (!admin) {
			this.sendChatToSender(ics, EnumChatFormatting.RED+"You do not have permission to use this command in this way.");
			return;
		}
		boolean removeTop = !args[0].startsWith("&");
		if (!removeTop)
			args[0] = args[0].substring(1);
		Opcodes o = Opcodes.valueOf(args[0].toUpperCase(Locale.ENGLISH));
		args = Arrays.copyOfRange(args, 1, args.length);
		if (program != null) {
			program.instructions.add(new ByteCodeInstruction(o, args));
			this.sendChatToSender(ics, EnumChatFormatting.GREEN+"Added instruction to program:");
			this.sendChatToSender(ics, program.toString());
		}
		else {
			try {
				o.call(this, ics, args, this.getStack(ics), removeTop);
				for (EntityPlayer ep : ReikaPlayerAPI.getOps()) {
					if (ep != ics) {
						ReikaChatHelper.sendChatToPlayer(ep, "Player "+ics+" ran bytecode command: "+Arrays.toString(args));
					}
				}
				if (o != Opcodes.OUTPUT)
					Opcodes.OUTPUT.call(this, ics, null, this.getStack(ics), false);
			}
			catch (ClassNotFoundException e) {
				this.error(ics, "No such class: "+e);
			}
			catch (NoSuchFieldException e) {
				this.error(ics, "No such field: "+e);
			}
			catch (NoSuchMethodException e) {
				this.error(ics, "No such method: "+e);
			}
			catch (IllegalArgumentException e) {
				this.error(ics, "Invalid specified type: "+e);
			}
			catch (IllegalAccessException e) {
				//never happens
			}
			catch (InvocationTargetException e) {
				this.error(ics, "Called method threw exception: "+e);
			}
			catch (InstantiationException e) {
				this.error(ics, "Could not construct object: "+e);
			}
		}
	}

	private boolean hasPermissionToRun(ICommandSender ics) {
		if (ics instanceof RConConsoleSource) //console
			return true;
		if (DragonAPICore.isSinglePlayer())
			return true;
		EntityPlayer ep = this.getCommandSenderAsPlayer(ics);
		return playerPermissions.contains(ep.getUniqueID());
	}

	@Override
	protected void error(ICommandSender ics, String s) {
		super.error(ics, s);
		try {
			//Opcodes.FLUSH.call(this, ics, null, this.getStack(ics));
		}
		catch (Exception e) {
			//never happens
		}
	}

	private void startProgram(EntityPlayerMP ep, String[] args) {
		if (program != null) {
			this.sendChatToSender(ep, EnumChatFormatting.YELLOW+"Cannot start writing a program; another is already being written: "+program);
			return;
		}
		HashSet<String> args2 = new HashSet();
		for (int i = 2; i < args.length; i++) {
			args2.add(args[i].toLowerCase(Locale.ENGLISH));
		}
		program = new BytecodeProgram(ep, args[1], args2);
		this.sendChatToSender(ep, EnumChatFormatting.GREEN+"Started writing program: "+program);
	}

	private void finishProgram(EntityPlayerMP ep) {
		try {
			program.save();
			this.sendChatToSender(ep, EnumChatFormatting.GREEN+"Saved program: "+program);
			program = null;
		}
		catch (IOException e) {
			this.sendChatToSender(ep, EnumChatFormatting.RED+"Could not save program file: "+e.toString());
			e.printStackTrace();
		}
	}

	private void runProgram(EntityPlayerMP ep, String name) {
		BytecodeProgram program = this.findProgram(name);
		if (program == null) {
			this.sendChatToSender(ep, EnumChatFormatting.RED+"No such program '"+name+"'!");
			return;
		}
		program.stack.clear();
		program.stack.addAll(this.getStack(ep));
		this.getStack(ep).clear();
		if (program.allowAnyUser || (program.allowAnyAdmin && ReikaPlayerAPI.isAdmin(ep)) || program.programOwner.equals(ep.getUniqueID())) {
			try {
				program.run(ep, this);
				this.sendChatToSender(ep, EnumChatFormatting.GREEN+"Program ran successfully! Stack: ");
				this.sendChatToSender(ep, program.stack.toString());
			}
			catch (Exception e) {
				this.sendChatToSender(ep, EnumChatFormatting.RED+"Program threw exception!");
				this.sendChatToSender(ep, e.toString());
				e.printStackTrace();
			}
		}
		else {
			this.sendChatToSender(ep, EnumChatFormatting.RED+"You do not have permission to run this!");
		}
	}

	private BytecodeProgram findProgram(String name) {
		File base = DragonAPICore.getMinecraftDirectory();
		File f = new File(base, PROGRAM_PATH+"/"+name+".byteprog");
		if (!f.exists())
			return null;
		return BytecodeProgram.readFile(f);
	}

	private Stack getStack(ICommandSender ics) {
		UUID id = this.getUID(ics);
		Stack s = objectStack.get(id);
		if (s == null) {
			s = new Stack();
			objectStack.put(id, s);
		}
		return s;
	}

	@Override
	public String getCommandString() {
		return "bytecodeexec";
	}

	@Override
	protected boolean isAdminOnly() {
		return false; //for program admins
	}

	private static enum Opcodes {
		LDC(),
		NEW(),
		DUP(),
		POP(),
		SWAP(),
		INVOKESTATIC(),
		INVOKEVIRTUAL(),
		GETSTATIC(),
		GETFIELD(),
		SETFIELD(),
		//THROW(),
		INSTANCEOF(),
		OUTPUT(),
		FLUSH();

		private static final Opcodes[] list = values();

		private void call(ReflectiveBasedCommand cmd, ICommandSender ics, String[] args, Stack s, boolean removeTop) throws ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
			switch(this) {
				case FLUSH:
					s.clear();
					break;
				case GETFIELD: {
					Class c = cmd.findClass(args[0]);
					Field f = c.getDeclaredField(cmd.deSRG(c, args[1]));
					f.setAccessible(true);
					if (s.isEmpty())
						throw new IllegalArgumentException("Operand stack underflow");
					s.push(f.get(removeTop ? s.pop() : s.peek()));
					break;
				}
				case SETFIELD: {
					Class c = cmd.findClass(args[0]);
					Field f = c.getDeclaredField(cmd.deSRG(c, args[1]));
					f.setAccessible(true);
					if (s.size() < 2)
						throw new IllegalArgumentException("Operand stack underflow");
					Object arg = s.pop();
					f.set(removeTop ? s.pop() : s.peek(), arg);
					break;
				}
				case GETSTATIC: {
					Class c = cmd.findClass(args[0]);
					Field f = c.getDeclaredField(cmd.deSRG(c, args[1]));
					f.setAccessible(true);
					s.push(f.get(null));
					break;
				}
				case INVOKESTATIC: {
					Class c = cmd.findClass(args[0]);
					Class[] types = cmd.parseTypes(args[2]);
					Method m = c.getDeclaredMethod(cmd.deSRG(c, args[1]), types);
					m.setAccessible(true);
					Object[] vals = new Object[types.length];
					if (vals.length > s.size())
						throw new IllegalArgumentException("Operand stack underflow");
					for (int i = vals.length-1; i >= 0; i--) {
						vals[i] = s.pop();
					}
					s.push(m.invoke(null, vals));
					break;
				}
				case INVOKEVIRTUAL: {
					Class c = cmd.findClass(args[0]);
					Class[] types = cmd.parseTypes(args[2]);
					Method m = c.getDeclaredMethod(cmd.deSRG(c, args[1]), types);
					m.setAccessible(true);
					Object[] vals = new Object[types.length];
					if (vals.length+1 > s.size())
						throw new IllegalArgumentException("Operand stack underflow");
					for (int i = vals.length-1; i >= 0; i--) {
						vals[i] = s.pop();
					}
					s.push(m.invoke(removeTop ? s.pop() : s.peek(), vals));
					break;
				}
				case LDC:
					s.push(cmd.parseObject(args[0]));
					break;
				case NEW: {
					Class c = cmd.findClass(args[0]);
					Class[] types = cmd.parseTypes(args[1]);
					Object[] vals = new Object[types.length];
					if (vals.length > s.size())
						throw new IllegalArgumentException("Operand stack underflow");
					for (int i = vals.length-1; i >= 0; i--) {
						vals[i] = s.pop();
					}
					Constructor cn = c.getDeclaredConstructor(types);
					cn.setAccessible(true);
					Object o = cn.newInstance(args);
					s.push(o);
					break;
				}
				case OUTPUT:
					sendChatToSender(ics, "Current stack: [");
					for (Object o : s)
						sendChatToSender(ics, cmd.toReadableString(o));
					sendChatToSender(ics, "]");
					break;
				case DUP:
					if (s.isEmpty())
						throw new IllegalArgumentException("Operand stack underflow");
					s.push(s.peek());
					break;
				case POP:
					if (s.isEmpty())
						throw new IllegalArgumentException("Operand stack underflow");
					s.pop();
					break;
				case SWAP: {
					if (s.size() < 2)
						throw new IllegalArgumentException("Operand stack underflow");
					Object top = s.pop();
					Object o2 = s.pop();
					s.push(top);
					s.push(o2);
					break;
				}
				case INSTANCEOF: {
					if (s.size() < 2)
						throw new IllegalArgumentException("Operand stack underflow");
					Object obj = s.pop();
					Object type = s.pop();
					s.push(((Class)type).isAssignableFrom(obj.getClass()));
					break;
				}
			}
		}
	}

	static class BytecodeProgram {

		private final String programName;
		private final UUID programOwner;

		private final boolean allowAnyAdmin;
		private final boolean allowAnyUser;

		private final ArrayList<ByteCodeInstruction> instructions = new ArrayList();

		private final Stack stack = new Stack();

		private BytecodeProgram(EntityPlayerMP ep, String name, HashSet<String> args) {
			this(ep.getUniqueID(), name, args.contains("admin"), args.contains("public"));
		}

		private BytecodeProgram(UUID id, String name, boolean admin, boolean any) {
			programOwner = id;
			programName = name;

			allowAnyAdmin = admin;
			allowAnyUser = any;
		}

		private static BytecodeProgram readFile(File f) {
			ArrayList<String> li = ReikaFileReader.getFileAsLines(f, true);
			String name = li.remove(0);
			String uid = li.remove(0);
			String admin = li.remove(0);
			String any = li.remove(0);
			String space = li.remove(0);

			BytecodeProgram p = new BytecodeProgram(UUID.fromString(uid), name, Boolean.parseBoolean(admin), Boolean.parseBoolean(any));
			for (String s : li) {
				p.instructions.add(ByteCodeInstruction.parseInstruction(s));
			}
			return p;
		}

		private void run(EntityPlayerMP ep, BytecodeCommand c) throws Exception {
			for (ByteCodeInstruction p : instructions) {
				p.execute(ep, c, stack);
			}
		}

		private void save() throws IOException {
			File base = DragonAPICore.getMinecraftDirectory();
			File f = new File(base, PROGRAM_PATH+"/"+programName+".byteprog");
			f.getParentFile().mkdirs();
			if (f.exists())
				f.delete();
			f.createNewFile();
			ArrayList<String> li = this.serializeProgram();
			this.prependHeader(li);
			ReikaFileReader.writeLinesToFile(f, li, true);
		}

		private void prependHeader(ArrayList<String> li) {
			li.add(0, programName);
			li.add(1, programOwner.toString());
			li.add(2, String.valueOf(allowAnyAdmin));
			li.add(3, String.valueOf(allowAnyUser));
			li.add(4, "===========================");
		}

		private ArrayList<String> serializeProgram() {
			ArrayList<String> li = new ArrayList();
			for (ByteCodeInstruction p : instructions) {
				li.add(p.serialize());
			}
			return li;
		}

		@Override
		public String toString() {
			return instructions.toString();
		}

		static class ByteCodeInstruction {

			private final Opcodes operation;
			private final String[] arguments;

			private ByteCodeInstruction(Opcodes o, String... args) {
				operation = o;
				arguments = args;
			}

			private static ByteCodeInstruction parseInstruction(String s) {
				String[] parts = s.split(":");
				Opcodes o = Opcodes.valueOf(parts[0]);
				String[] args = parts[1].split("\\|");
				return new ByteCodeInstruction(o, args);
			}

			private String serialize() {
				String args = Arrays.toString(arguments).replace(", ", "|");
				args = args.substring(1, args.length()-1);
				return operation.toString()+":"+args;
			}

			private void execute(EntityPlayerMP ep, BytecodeCommand c, Stack s) throws Exception {
				operation.call(c, ep, arguments, s, true);
			}

			@Override
			public String toString() {
				return operation.toString()+" on "+Arrays.toString(arguments);
			}

		}

	}

}
