/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import Reika.DragonAPI.Instantiable.IO.XMLInterface;

import java.util.ArrayList;

import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchPage;

public class XMLResearch {

	private final XMLInterface info;
	private final ArrayList<ResearchPage> pages = new ArrayList();
	public final String name;

	public XMLResearch(String name, Class root, String path, InfusionRecipe recipe, int num) {
		info = new XMLInterface(root, path);
		this.name = name;
		XMLPage page = new XMLPage(recipe);
		pages.add(page);
		for (int i = 1; i < num; i++) {
			pages.add(new XMLPage(i));
		}
	}

	public XMLResearch(String name, Class root, String path, IArcaneRecipe recipe, int num) {
		info = new XMLInterface(root, path);
		this.name = name;
		XMLPage page = new XMLPage(recipe);
		pages.add(page);
		for (int i = 1; i < num; i++) {
			pages.add(new XMLPage(i));
		}
	}

	public XMLResearch(String name, Class root, String path, CrucibleRecipe recipe, int num) {
		info = new XMLInterface(root, path);
		this.name = name;
		XMLPage page = new XMLPage(recipe);
		pages.add(page);
		for (int i = 1; i < num; i++) {
			pages.add(new XMLPage(i));
		}
	}

	public void addPage() {
		int num = pages.size();
		pages.add(new XMLPage(num));
	}

	public ResearchPage[] getPages() {
		ResearchPage[] arr = new ResearchPage[pages.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = pages.get(i);
		}
		return arr;
	}

	private class XMLPage extends ResearchPage {

		private final int page;

		private XMLPage(InfusionRecipe recipe) {
			super(recipe);
			page = 0;
		}

		private XMLPage(IArcaneRecipe recipe) {
			super(recipe);
			page = 0;
		}

		private XMLPage(CrucibleRecipe recipe) {
			super(recipe);
			page = 0;
		}

		private XMLPage(int id) {
			super("");
			page = id;
		}

		@Override
		public String getTranslatedText() {
			return info.getValueAtNode("researches:"+name.toLowerCase()+":page"+page);
		}

		@Override
		public String toString() {
			return name+": "+this.getTranslatedText();
		}
	}

}