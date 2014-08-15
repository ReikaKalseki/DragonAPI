package Reika.DragonAPI.Auxiliary;

import net.minecraft.launchwrapper.IClassTransformer;

public class DragonAPIClassTransfomer implements IClassTransformer {

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		return arg2;
	}

}
