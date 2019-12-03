package Reika.DragonAPI.Interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface DataProvider {

	public InputStream getDataStream() throws IOException;

	public boolean canBeReloaded();

}
