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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public abstract class NBTFile {

	public final String name;

	private final String filepath;
	private final Class reference;

	public boolean compressData = false;
	public boolean encryptData = false;

	public NBTFile(File f) {
		this(f.getName(), f);
	}

	public NBTFile(String name, File f) {
		this(name, f.getAbsolutePath(), null);
	}

	public NBTFile(String name, String path, Class c) {
		this.name = name;
		filepath = path;
		reference = c;
	}

	private InputStream encryptStreamData(InputStream in, boolean unpack) throws IOException {
		ArrayList<Byte> data = new ArrayList();
		int dat = in.read();
		while (dat != -1) {
			data.add((byte)dat);
			dat = in.read();
		}
		in.close();
		//ReikaJavaLibrary.cycleList(data, unpack ? -8 : 8);
		Collections.reverse(data);
		byte[] arr = new byte[data.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = data.get(i);
		}
		return new ByteArrayInputStream(arr);
	}

	private void encryptFileData(File f, boolean unpack) throws IOException {
		try (InputStream in = new FileInputStream(f); OutputStream out = new FileOutputStream(f)) {
			ArrayList<Byte> data = new ArrayList();
			int dat = in.read();
			while (dat != -1) {
				data.add((byte)dat);
				dat = in.read();
			}
			//ReikaJavaLibrary.cycleList(data, unpack ? -8 : 8);
			Collections.reverse(data);
			for (byte b : data) {
				out.write(b);
			}
			out.flush();
			out.close();
		}
	}

	public final void load() throws IOException {
		if (reference != null) {
			try(InputStream in = reference.getResourceAsStream(filepath)) {
				try(InputStream in2 = encryptData ? this.encryptStreamData(in, true) : in) {
					NBTTagCompound tag = compressData ? CompressedStreamTools.readCompressed(in2) : ReikaFileReader.readUncompressedNBT(in2);
					this.setDataFromLines(tag);
				}
			}
		}
		else {
			File f = new File(filepath);
			if (!f.exists())
				return;

			if (encryptData)
				this.encryptFileData(f, true);

			try (InputStream in = new FileInputStream(f)) {
				NBTTagCompound tag = compressData ? CompressedStreamTools.readCompressed(in) : ReikaFileReader.readUncompressedNBT(in);
				this.setDataFromLines(tag);
			}
		}
	}

	public final void save() throws IOException {
		String path = filepath;
		if (reference != null) {
			String pre = ReikaJavaLibrary.getClassLocation(reference).replaceAll("\\\\", "/");
			pre = pre.substring(0, pre.length()-reference.getSimpleName().length()-".class".length());
			pre = pre.replaceAll("/bin/", "/src/");
			path = pre+path;
		}
		File f = new File(path);
		f.getParentFile().mkdirs();
		f.delete();
		f.createNewFile();
		NBTTagCompound tag = this.getDataAsLines();

		if (compressData)
			CompressedStreamTools.writeCompressed(tag, new FileOutputStream(f));
		else
			ReikaFileReader.writeUncompressedNBT(tag, f);

		if (encryptData)
			this.encryptFileData(f, false);
	}

	private NBTTagCompound getDataAsLines() {
		NBTTagCompound dat = new NBTTagCompound();
		NBTTagCompound header = new NBTTagCompound();
		this.writeHeader(header);

		NBTTagCompound extra = this.writeExtraData();
		if (extra != null)
			header.setTag("extra", extra);

		dat.setTag("header", header);

		NBTTagList li = new NBTTagList();
		this.writeData(li);
		dat.setTag("data", li);
		return dat;
	}

	private void setDataFromLines(NBTTagCompound tag) {
		NBTTagCompound header = tag.getCompoundTag("header");
		this.readHeader(header);

		NBTTagCompound extra = header.getCompoundTag("extra");
		this.readExtraData(extra);

		NBTTagList li = tag.getTagList("data", NBTTypes.COMPOUND.ID);
		this.readData(li);
	}

	protected abstract void readHeader(NBTTagCompound header);

	/** Is a list of NBTTagCompounds! */
	protected abstract void readData(NBTTagList li);

	protected abstract void readExtraData(NBTTagCompound extra);

	protected abstract void writeHeader(NBTTagCompound header);

	/** Write a list of NBTTagCompounds! */
	protected abstract void writeData(NBTTagList li);

	protected abstract NBTTagCompound writeExtraData();

	public static final class SimpleNBTFile extends NBTFile {

		public NBTTagCompound data;

		public SimpleNBTFile(File f) {
			super(f);
		}

		@Override
		protected void readHeader(NBTTagCompound header) {

		}

		@Override
		protected void writeHeader(NBTTagCompound header) {

		}

		@Override
		protected void readData(NBTTagList li) {
			NBTTagCompound tag = li.getCompoundTagAt(0);
			data = tag != null && !tag.hasNoTags() ? tag : null;
		}

		@Override
		protected void writeData(NBTTagList li) {
			if (data != null) {
				li.appendTag(data.copy());
			}
		}

		@Override
		protected void readExtraData(NBTTagCompound extra) {

		}

		@Override
		protected NBTTagCompound writeExtraData() {
			return null;
		}

	}

}
