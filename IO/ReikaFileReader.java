package Reika.DragonAPI.IO;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import Reika.DragonAPI.DragonAPICore;

public class ReikaFileReader extends DragonAPICore {

	public static int getFileLength(File f) {
		int len;
		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(f));
			lnr.skip(Long.MAX_VALUE);
			len = lnr.getLineNumber()+1;
			lnr.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load file data due to "+e.getCause()+"!");
		}
		return len;
	}

}
