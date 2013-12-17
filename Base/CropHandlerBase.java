package Reika.DragonAPI.Base;

public abstract class CropHandlerBase extends ModHandlerBase {

	public abstract int getRipeMeta();

	public abstract int getFreshMeta();

	public abstract boolean isCrop(int id);

	public abstract boolean isRipeCrop(int id, int meta);

}
