package Reika.DragonAPI.Extras;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import Reika.DragonAPI.Interfaces.IconEnum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum IconPrefabs implements IconEnum {

	TRANSPARENT("transparent"),
	FADE("fade"),
	FADE_BASICBLEND("fade_basic"),
	FADE_GENTLE("fade_gentle"),
	NOENTER("noentry"),
	CHECK("check"),
	X("x"),
	QUESTION("question"),
	BLANK("blank"),
	;

	private IIcon icon;
	private final String iconName;

	public static final IconPrefabs[] iconList = values();

	private IconPrefabs(String icon) {
		iconName = icon;
	}

	public IIcon getIcon() {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	private void register(IIconRegister ico) {
		String s = this.getIconName();
		icon = ico.registerIcon(s);
	}

	private String getIconName() {
		return "dragonapi:icon/"+iconName;
	}

	@SideOnly(Side.CLIENT)
	public static void registerAll(TextureMap ico) {
		for (int i = 0; i < iconList.length; i++) {
			IconPrefabs c = iconList[i];
			c.register(ico);
		}
	}
}
