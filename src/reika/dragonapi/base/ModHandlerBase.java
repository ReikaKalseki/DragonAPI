/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.base;

import java.lang.reflect.Modifier;
import java.util.Random;

import reika.dragonapi.ModList;
import reika.dragonapi.auxiliary.trackers.ReflectiveFailureTracker;
import reika.dragonapi.exception.MisuseException;
import reika.dragonapi.libraries.java.ReikaReflectionHelper;
import reika.dragonapi.libraries.java.SemanticVersionParser;

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
