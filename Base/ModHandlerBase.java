/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.lang.reflect.Modifier;
import java.util.Random;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;
import Reika.DragonAPI.Libraries.Java.SemanticVersionParser;

/** Reflection tools to read other mods. */
public abstract class ModHandlerBase {

	protected final Random rand = new Random();

	protected ModHandlerBase() {
		this.verifyInstanceField();
	}

	private void verifyInstanceField() {
		if (!ReikaReflectionHelper.checkForField(this.getClass(), "instance", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL))
			throw new MisuseException("A mod handler must have a private static final 'instance' field!");
		if (!ReikaReflectionHelper.checkForMethod(this.getClass(), "getInstance", new Class[0], Modifier.PUBLIC, Modifier.STATIC))
			throw new MisuseException("A mod handler must have a public static 'getInstance()' method!");
	}

	public abstract boolean initializedProperly();

	public abstract ModList getMod();

	protected void noMod() {
		//throw new ModHandlerException(this.getMod());
	}

	public boolean hasMod() {
		return this.getMod().isLoaded();
	}

	protected final void logFailure(Exception e) {
		ReflectiveFailureTracker.instance.logModReflectiveFailure(this.getMod(), e);
	}

	public static interface VersionHandler {

		public boolean acceptVersion(String version);

	}

	public static final class VersionIgnore implements VersionHandler {

		@Override
		public boolean acceptVersion(String version) {
			return true;
		}

		@Override
		public String toString() {
			return "[Any]";
		}

	}

	public static final class SemanticVersionHandler implements VersionHandler {

		private final String minimum;
		private final String maximum;

		public SemanticVersionHandler(String min) {
			this(min, true);
		}

		public SemanticVersionHandler(String ver, boolean isMin) {
			this(isMin ? ver : null, isMin ? null : ver);
		}

		public SemanticVersionHandler(String min, String max) {
			minimum = min;
			maximum = max;
		}

		@Override
		public boolean acceptVersion(String version) {
			boolean min = (minimum == null || SemanticVersionParser.isVersionAtLeast(minimum, version));
			boolean max = (maximum == null || SemanticVersionParser.isVersionAtMost(version, maximum));
			return min && max;
		}

		@Override
		public String toString() {
			return (minimum != null ? minimum : "")+" - "+(maximum != null ? maximum : "");
		}

	}

	public static final class SearchVersionHandler implements VersionHandler {

		private final String searchKey;

		public SearchVersionHandler(String key) {
			searchKey = key;
		}

		@Override
		public boolean acceptVersion(String version) {
			return version.contains(searchKey);
		}

		@Override
		public String toString() {
			return "[Contains '"+searchKey+"']";
		}

	}

}
