/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

public class FileInputThread implements Runnable {

	private Object returnObj;
	private String filepath;
	private Class referenceClass;
	private FileType type;

	@Override
	public void run() {

	}

	public Object getFile() {
		return returnObj;
	}

	public FileInputThread setFiletype(FileType type) {
		this.type = type;
		return this;
	}

	public FileInputThread setFilepath(Class root, String path) {
		filepath = path;
		referenceClass = root;
		return this;
	}

	public enum FileType {
		IMAGE(),
		TEXT(),
		XML(),
		SOUND();
	}

}
