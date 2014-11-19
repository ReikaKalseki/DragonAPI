package Reika.DragonAPI.Exception;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.tree.ClassNode;

public abstract class ASMException extends RuntimeException {

	protected final ClassNode node;
	protected final String label;
	public static final boolean DEV_ENV = !FMLForgePlugin.RUNTIME_DEOBF;

	private ASMException(ClassNode cn, String name) {
		label = name;
		node = cn;
	}

	@Override
	public final String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.toString());
		sb.append(" not found in class ");
		sb.append(node.name);
		sb.append(".\n");
		sb.append("This is a critical ASM error and the class transformer operation cannot proceed.");
		sb.append(" If you are the developer of this mod, check for proper use of SRG/deobf names. If not, report it to the developer.");
		sb.append("Additional information:\n");
		sb.append(this.getAdditionalInformation());
		return sb.toString();
	}

	protected abstract String getAdditionalInformation();

	public final boolean isVanillaClass() {
		return !node.name.startsWith("net.minecraftforge") && !node.name.startsWith("cpw");
	}

	public static final class NoSuchASMMethodException extends ASMException {

		private final String signature;

		public NoSuchASMMethodException(ClassNode cn, String name, String sig) {
			super(cn, name);
			signature = sig;
		}

		@Override
		public String toString() {
			return "Method "+label+" "+signature;
		}

		@Override
		protected String getAdditionalInformation() {
			StringBuilder sb = new StringBuilder();
			if (DEV_ENV && label.startsWith("func_")) {
				sb.append("Use of SRG name in dev environment. This may be an error.\n");
			}
			else if (!DEV_ENV && !label.startsWith("func_") && this.isVanillaClass()) {
				sb.append("Use of non-SRG name in compiled game on vanilla code. This is very likely an error.\n");
			}
			sb.append("Identified methods:\n");
			sb.append(node.methods);
			return sb.toString();
		}

	}

	public static final class NoSuchASMFieldException extends ASMException {

		public NoSuchASMFieldException(ClassNode cn, String name) {
			super(cn, name);
		}

		@Override
		public String toString() {
			return "Field "+label;
		}

		@Override
		protected String getAdditionalInformation() {
			StringBuilder sb = new StringBuilder();
			if (DEV_ENV && label.startsWith("field_")) {
				sb.append("Use of SRG name in dev environment. This may be an error.\n");
			}
			else if (!DEV_ENV && !label.startsWith("field_") && this.isVanillaClass()) {
				sb.append("Use of non-SRG name in compiled game on vanilla code. This is very likely an error.\n");
			}
			sb.append("Identified fields:\n");
			sb.append(node.fields);
			return sb.toString();
		}

	}

}
