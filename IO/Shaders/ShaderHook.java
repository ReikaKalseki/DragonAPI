package Reika.DragonAPI.IO.Shaders;


public interface ShaderHook {

	public void onPreRender(ShaderProgram s);
	public void onPostRender(ShaderProgram s);
	public void updateEnabled(ShaderProgram s);

}
