/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.IMetadataSection;

import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class DirectResource implements IResource {

	public final String path;
	private byte[] data;

	public DirectResource(String path) {
		this.path = path;
	}

	@Override
	public InputStream getInputStream() {
		//ReikaJavaLibrary.pConsole("Loading "+path+", data="+data);
		if (data == null) {
			InputStream st = this.calcStream();
			if (st == null)
				throw new RuntimeException("Resource not found at "+path);
			data = ReikaJavaLibrary.streamToBytes(st);
		}
		//ReikaJavaLibrary.pConsole("Loaded "+path+", data="+data);
		return new ByteArrayInputStream(data);
	}

	private InputStream calcStream() {
		File f = new File(path);
		if (f.exists()) {
			try {
				return new FileInputStream(f);
			}
			catch (FileNotFoundException e) {
				return null;
			}
		}
		else
			return DragonAPIInit.class.getClassLoader().getResourceAsStream(path);
	}

	@Override
	public boolean hasMetadata() {
		return false;
	}

	@Override
	public IMetadataSection getMetadata(String p_110526_1_) {
		return null;
	}

}
