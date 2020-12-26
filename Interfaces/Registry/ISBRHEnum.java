package Reika.DragonAPI.Interfaces.Registry;

import Reika.DragonAPI.Base.ISBRH;

public interface ISBRHEnum {

	public int getRenderID();

	public ISBRH getRenderer();

	public void setRenderPass(int pass);

	public Class<? extends ISBRH> getRenderClass();

	public void setRenderID(int id);

	public void setRenderer(ISBRH r);

}
