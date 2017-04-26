package Reika.DragonAPI.Command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Stack;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

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

	private static final HashMap<UUID, Stack> objectStack = new HashMap();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args[0].equalsIgnoreCase("define")) {
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
			return;
		}
		else if (args[0].equalsIgnoreCase("look")) {
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
			return;
		}
		Opcodes o = Opcodes.valueOf(args[0].toUpperCase(Locale.ENGLISH));
		args = Arrays.copyOfRange(args, 1, args.length);
		try {
			o.call(this, ics, args, this.getStack(ics));
			if (o != Opcodes.OUTPUT)
				Opcodes.OUTPUT.call(this, ics, null, this.getStack(ics));
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

	@Override
	protected void error(ICommandSender ics, String s) {
		super.error(ics, s);
		try {
			Opcodes.FLUSH.call(this, ics, null, this.getStack(ics));
		}
		catch (Exception e) {
			//never happens
		}
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
		OUTPUT(),
		FLUSH();

		private static final Opcodes[] list = values();

		private void call(ReflectiveBasedCommand cmd, ICommandSender ics, String[] args, Stack s) throws ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
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
					s.push(f.get(s.pop()));
					break;
				}
				case SETFIELD: {
					Class c = cmd.findClass(args[0]);
					Field f = c.getDeclaredField(cmd.deSRG(c, args[1]));
					f.setAccessible(true);
					if (s.size() < 2)
						throw new IllegalArgumentException("Operand stack underflow");
					Object arg = s.pop();
					f.set(s.pop(), arg);
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
					s.push(m.invoke(s.pop(), vals));
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
			}
		}
	}

}
