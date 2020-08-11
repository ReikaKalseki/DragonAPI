/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.IO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

public final class ReikaCSVReader {

	private ReikaCSVReader() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	private final ArrayList<String> lineData = new ArrayList();

	public ReikaCSVReader(Class root, String path) throws IOException {
		try (InputStream input = root.getResourceAsStream(path)) {
			lineData.addAll(ReikaFileReader.getFileAsLines(input, true, Charset.defaultCharset()));
		}
	}

}
