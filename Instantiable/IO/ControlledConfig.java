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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.InvalidConfigException;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Exception.StupidIDException;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.ValueSortedMap;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.BoundedConfig;
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Configuration.CustomCategoryConfig;
import Reika.DragonAPI.Interfaces.Configuration.DecimalConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntArrayConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;
import Reika.DragonAPI.Interfaces.Configuration.SegmentedConfigList;
import Reika.DragonAPI.Interfaces.Configuration.SelectiveConfig;
import Reika.DragonAPI.Interfaces.Configuration.StringArrayConfig;
import Reika.DragonAPI.Interfaces.Configuration.StringConfig;
import Reika.DragonAPI.Interfaces.Configuration.UserSpecificConfig;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ControlledConfig {

	private static final HashMap<String, ControlledConfig> configs = new HashMap();

	private static final UserHash userHash = genUserHash();

	private final class PropertyComparator implements Comparator<String> {

		private final String category;

		private PropertyComparator(String s) {
			category = s;
		}

		@Override
		public int compare(String s1, String s2) {
			ValueSortedMap<String, Object> data = optionMap.get(category);
			if (data == null || data.isEmpty())
				return s1.compareToIgnoreCase(s2);
			Object o1 = data.get(s1);
			Object o2 = data.get(s2);
			if (o1 == null || o2 == null)
				return s1.compareToIgnoreCase(s2);
			int comp = entryComparator.compare(o1, o2);
			return comp == 0 ? s1.compareToIgnoreCase(s2) : comp;
		}

	};

	private final Comparator entryComparator = new Comparator<Object>() {

		@Override
		public int compare(Object o1, Object o2) {
			if (o1.getClass() != o2.getClass())
				return o1.getClass().getName().compareTo(o2.getClass().getName());
			if (o1 instanceof Enum) {
				return ((Enum)o1).compareTo((Enum)o2);
			}
			if (o1 instanceof ConfigList) {
				return Integer.compare(((ConfigList)o1).ordinal(), ((ConfigList)o2).ordinal());
			}
			if (o1 instanceof Comparable) {
				return ((Comparable)o1).compareTo(o2);
			}
			return 0;
		}

	};

	private Configuration config;

	private int readID;
	protected File configFile;

	public final DragonAPIMod configMod;

	private ConfigList[] optionList;
	private IDRegistry[] IDList;

	protected Object[] controls;
	protected int[] otherIDs;

	private final HashMap<SegmentedConfigList, String> specialFiles = new HashMap();
	private final MultiMap<String, SegmentedConfigList> specialConfigs = new MultiMap();
	private final HashMap<String, HashMap<String, String>> extraFiles = new HashMap();
	private final HashMap<String, ValueSortedMap<String, Object>> optionMap = new HashMap();
	private final HashMap<String, LinkedHashMap<String, DataElement>> additionalOptions = new HashMap();
	private final HashSet<String> orphanExclusions = new HashSet();

	private final ArrayList<String> queuedExceptions = new ArrayList();

	public ControlledConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id) {
		if (mod == null)
			throw new MisuseException("You cannot create a config with a null mod!");
		configMod = mod;
		optionList = option;
		IDList = id;
		String n = ReikaStringParser.stripSpaces(mod.getDisplayName());
		if (configs.containsKey(n))
			throw new IllegalArgumentException("Only one config permitted per mod!");
		configs.put(n, this);

		if (option != null) {
			controls = new Object[optionList.length];
		}
		else {
			controls = new Object[0];
			optionList = new ConfigList[0];
		}

		if (id != null)
			otherIDs = new int[IDList.length];
		else {
			otherIDs = new int[0];
			IDList = new IDRegistry[0];
		}

		for (int i = 0; i < optionList.length; i++) {
			ConfigList cfg = optionList[i];
			String s1 = this.getCategory(cfg);
			String s2 = this.getLabel(cfg);
			this.registerOption(s1, s2, cfg);
		}

		for (int i = 0; i < IDList.length; i++) {
			IDRegistry cfg = IDList[i];
			String s1 = this.getCategory(cfg);
			String s2 = this.getLabel(cfg);
			this.registerOption(s1, s2, cfg);
		}
	}

	private static UserHash genUserHash() {
		/*
		String username = System.getProperty("user.name");
		long diskSize = new File("/").getTotalSpace();
		int h1 = username.hashCode();
		int h2 = Long.toHexString(diskSize).hashCode();
		return h1 ^ h2;
		 */
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? getClientUserHash() : null;
	}

	@SideOnly(Side.CLIENT)
	private static UserHash getClientUserHash() {
		Session s = Minecraft.getMinecraft().getSession();
		GameProfile p = s.func_148256_e();
		String id = p != null ? p.getId().toString() : s.getUsername();
		return new UserHash(id);
	}

	private String getLabel(ConfigList cfg) {
		String s = cfg.getLabel();
		if (cfg instanceof UserSpecificConfig && ((UserSpecificConfig)cfg).isUserSpecific()) {
			s = "["+Character.toUpperCase(s.charAt(0))+this.getUserHash(s)+"]U "+s; //First char prefix is to keep original sorting
		}
		return s;
	}

	public static String getUserHash(String s) {
		int hash = userHash == null ? 0 : userHash.hash;
		String ret = Strings.padStart(Integer.toHexString(hash - s.hashCode()).toUpperCase(Locale.ENGLISH), 8, '0');
		if (userHash != null)
			ret = ret+"_"+(userHash.userID.indexOf('-') == -1 ? userHash.userID : userHash.userID.substring(0, 6));
		return ret;
	}

	private void registerOption(String s1, String s2, Object cfg) {
		String key = s1.toLowerCase(Locale.ENGLISH);
		ValueSortedMap<String, Object> map = optionMap.get(key);
		if (map == null) {
			map = new ValueSortedMap().setComparator(entryComparator);
			optionMap.put(s1.toLowerCase(Locale.ENGLISH), map);
		}
		map.put(s2, cfg);
	}

	protected final void registerProperty(String property, Object value) {
		if (optionList != null && optionList.length > 0) {
			try {
				Field f = optionList[0].getClass().getDeclaredField(property);
				f.setAccessible(true);
				f.set(null, value);
			}
			catch (Exception e) {
				queuedExceptions.add("Could not set config value property "+property+": "+e.toString());
			}
		}
	}

	protected final void registerOrphanExclusion(String s) {
		orphanExclusions.add(s.toLowerCase(Locale.ENGLISH));
	}

	private final String getConfigPath() {
		return ReikaFileReader.getFileNameNoExtension(configFile, true, true);//configFile.getAbsolutePath().substring(0, configFile.getAbsolutePath().length()-4);
	}

	public final File getConfigFolder() {
		return configFile.getParentFile();
	}

	public final Collection<String> getExtraFiles() {
		return Collections.unmodifiableCollection(extraFiles.keySet());
	}

	public final ArrayList<String> getSettingsAsLines() {
		ArrayList<String> li = new ArrayList();
		for (int i = 0; i < optionList.length; i++) {
			ConfigList c = optionList[i];
			Object val = controls[i];
			String s = c.getLabel()+" = "+val;
			li.add(s);
		}
		return li;
	}

	public final Object getControl(int i) {
		return controls[i];
	}

	public int getOtherID(int i) {
		return otherIDs[i];
	}

	public void loadCustomConfigFile(FMLPreInitializationEvent event, String file) {
		this.loadConfigFile(new File(file));
	}

	public void loadSubfolderedConfigFile(FMLPreInitializationEvent event) {
		String name = ReikaStringParser.stripSpaces(configMod.getDisplayName());
		String author = ReikaStringParser.stripSpaces(configMod.getModAuthorName());
		String file = event.getModConfigurationDirectory()+"/"+author+"/"+name+".cfg";
		this.loadConfigFile(new File(file));
	}

	public void loadDefaultConfigFile(FMLPreInitializationEvent event) {
		this.loadConfigFile(event.getSuggestedConfigurationFile());
	}

	private void loadConfigFile(File f) {
		configFile = f;

		if (optionList != null) {
			for (int i = 0; i < optionList.length; i++) {
				ConfigList c = optionList[i];
				if (c instanceof SegmentedConfigList) {
					SegmentedConfigList sg = (SegmentedConfigList)c;
					String s = sg.getCustomConfigFile();
					if (s != null) {
						s = this.parseFileString(s);
						specialFiles.put(sg, s);
						extraFiles.put(s, new HashMap());
						specialConfigs.addValue(s, sg);
					}
				}
			}
		}
	}

	private String parseFileString(String s) {
		if (s.charAt(0) == '*') {
			String suffix = s.replaceAll("\\*", "");
			s = ReikaFileReader.getRealPath(configFile)+"*"+suffix;
		}
		String ext = s.substring(s.lastIndexOf('.'));
		int post = ext.indexOf('*');
		if (post >= 0) {
			ext = ext.substring(0, post);
			s = s.replaceAll("\\*", "");
			s = s.replaceAll(ext, "");
			s = s+ext;
		}
		return s;
	}

	public final void initProps(FMLPreInitializationEvent event) {
		if (configFile == null)
			throw new MisuseException("Error loading "+configMod.getDisplayName()+": You must load a config file before reading it!");
		config = new Configuration(configFile);
		this.load();
	}

	public final void load() {
		if (configFile == null)
			throw new MisuseException("Error loading "+configMod.getDisplayName()+": You cannot load a config that is not yet configured!");
		this.loadConfig();
	}

	public final void save() {
		this.setOrder();
		config.save();
	}

	protected final Property getProperty(String cat, String name, boolean def) {
		return config.get(cat, name, def);
	}

	protected final Property getProperty(String cat, String name, String def) {
		return config.get(cat, name, def);
	}

	protected final Property getProperty(String cat, String name, int def) {
		return config.get(cat, name, def);
	}

	protected void onInit() {}

	protected void afterInit() {}

	private void loadConfig() {
		config.load();

		this.stripOrhpanedEntries();

		if (!specialFiles.isEmpty())
			this.loadExtraFiles();

		for (int i = 0; i < optionList.length; i++) {
			ConfigList cfg = optionList[i];
			String label = this.getLabel(cfg);
			if (cfg.shouldLoad()) {
				controls[i] = this.loadValue(optionList[i]);
			}
			else {
				controls[i] = this.getDefault(optionList[i]);
			}
		}

		for (int i = 0; i < IDList.length; i++) {
			otherIDs[i] = this.getValueFromConfig(IDList[i], config);
		}

		this.loadAdditionalData();

		if (!queuedExceptions.isEmpty()) {
			throw new RegistrationException(configMod, "Errors found loading config data!\n"+Arrays.toString(queuedExceptions.toArray(new String[queuedExceptions.size()])));
		}

		/*******************************/
		//save the data
		this.setOrder();
		config.save();

		if (!specialFiles.isEmpty())
			this.saveExtraFiles();
	}

	private void stripOrhpanedEntries() {
		HashSet<ConfigCategory> catNames = new HashSet();
		for (String s1 : config.getCategoryNames()) {
			ConfigCategory cat = config.getCategory(s1);
			ValueSortedMap<String, Object> map = optionMap.get(s1.toLowerCase(Locale.ENGLISH));
			if (map == null) {
				if (orphanExclusions.contains(s1))
					continue;
				boolean flag = true;
				for (String s : orphanExclusions) {
					if (s.startsWith(s1) || s1.startsWith(s))
						flag = false;
				}
				if (flag)
					catNames.add(cat);
			}
			else {
				HashSet<String> entryNames = new HashSet();
				for (String s2 : cat.getValues().keySet()) {
					if (s2.charAt(0) != '[' && !s2.contains("]U") && !map.containsKey(s2)) { //skip user-specific ones
						entryNames.add(s2);
					}
				}
				for (String s : entryNames) {
					cat.remove(s);
				}
			}
		}
		for (ConfigCategory c : catNames) {
			config.removeCategory(c);
		}
	}

	private Object loadValue(ConfigList cfg) {
		if (cfg instanceof BooleanConfig && ((BooleanConfig)cfg).isBoolean())
			return this.setState((BooleanConfig)cfg);
		if (cfg instanceof IntegerConfig && ((IntegerConfig)cfg).isNumeric())
			return this.setValue((IntegerConfig)cfg);
		if (cfg instanceof DecimalConfig && ((DecimalConfig)cfg).isDecimal())
			return this.setFloat((DecimalConfig)cfg);
		if (cfg instanceof StringConfig && ((StringConfig)cfg).isString())
			return this.setString((StringConfig)cfg);
		if (cfg instanceof IntArrayConfig && ((IntArrayConfig)cfg).isIntArray())
			return this.setIntArray((IntArrayConfig)cfg);
		if (cfg instanceof StringArrayConfig && ((StringArrayConfig)cfg).isStringArray())
			return this.setStringArray((StringArrayConfig)cfg);
		return null;
	}

	private Object getDefault(ConfigList cfg) {
		if (cfg instanceof BooleanConfig && ((BooleanConfig)cfg).isBoolean())
			return ((BooleanConfig)cfg).getDefaultState();
		if (cfg instanceof IntegerConfig && ((IntegerConfig)cfg).isNumeric())
			return ((IntegerConfig)cfg).getDefaultValue();
		if (cfg instanceof DecimalConfig && ((DecimalConfig)cfg).isDecimal())
			return ((DecimalConfig)cfg).getDefaultFloat();
		if (cfg instanceof StringConfig && ((StringConfig)cfg).isString())
			return ((StringConfig)cfg).getDefaultString();
		if (cfg instanceof IntArrayConfig && ((IntArrayConfig)cfg).isIntArray())
			return ((IntArrayConfig)cfg).getDefaultIntArray();
		if (cfg instanceof StringArrayConfig && ((StringArrayConfig)cfg).isStringArray())
			return ((StringArrayConfig)cfg).getDefaultStringArray();
		return null;
	}

	private void loadExtraFiles() {
		for (String s : extraFiles.keySet()) {
			File f = new File(s);
			if (f.exists()) {
				this.readData(extraFiles.get(s), f);
			}
		}

		for (ConfigList cfg : specialFiles.keySet()) {
			String file = specialFiles.get(cfg);
			HashMap<String, String> data = extraFiles.get(file);
			String s = data.get(this.getLabel(cfg));
			if (s == null) {
				controls[cfg.ordinal()] = this.getDefault(cfg);
			}
			else {
				Object o = this.getDefault(cfg);
				try {
					o = this.parseData(cfg, s);
					if (o == null) {
						o = this.getDefault(cfg);
						queuedExceptions.add("Config entry '"+this.getLabel(cfg)+"' returned a null value. This is invalid.");
					}
				}
				catch (Exception e) {
					configMod.getModLogger().logError("Could not parse config value for "+this.getLabel(cfg));
				}
				controls[cfg.ordinal()] = o;
			}
		}
	}

	private Object parseData(ConfigList cfg, String o) {
		if (cfg instanceof StringConfig && ((StringConfig)cfg).isString()) {
			return o;
		}
		else if (cfg instanceof IntegerConfig && ((IntegerConfig)cfg).isNumeric()) {
			return Integer.parseInt(o);
		}
		else if (cfg instanceof DecimalConfig && ((DecimalConfig)cfg).isDecimal()) {
			return Float.parseFloat(o);
		}
		else if (cfg instanceof IntArrayConfig && ((IntArrayConfig)cfg).isIntArray()) {
			o = o.replaceAll("[", "").replaceAll("]", "");
			String[] parts = o.split(",");
			int[] dat = new int[parts.length];
			for (int i = 0; i < dat.length; i++) {
				dat[i] = Integer.parseInt(parts[i]);
			}
			return dat;
		}
		else if (cfg instanceof StringArrayConfig && ((StringArrayConfig)cfg).isStringArray()) {
			o = o.replaceAll("[", "").replaceAll("]", "");
			String[] parts = o.split(",");
			return parts;
		}
		else {
			return o;
		}
	}

	private void saveExtraFiles() {
		for (String s : extraFiles.keySet()) {
			try {
				File f = new File(s);
				ArrayList<String> li = this.getDataToWriteToFile(s);
				if (!li.isEmpty()) {
					File parent = new File(f.getParent());
					if (!parent.exists())
						parent.mkdirs();
					if (f.exists())
						f.delete();
					f.createNewFile();
					if (f.exists()) {
						ReikaFileReader.writeLinesToFile(f, li, true, Charsets.UTF_8);
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ArrayList<String> getDataToWriteToFile(String s) {
		ArrayList<String> li = new ArrayList();
		for (SegmentedConfigList cfg : specialConfigs.get(s)) {
			Object dat = controls[cfg.ordinal()];
			if (cfg.saveIfUnspecified() || !dat.equals(this.getDefault(cfg))) {
				//ReikaJavaLibrary.pConsole(getLabel(cfg)+" - "+cfg.saveIfUnspecified()+"/"+dat+"&"+this.getDefault(cfg));
				String val = this.encodeData(cfg, dat);
				String sg = "[\""+this.getLabel(cfg)+"\"=\""+val+"\"";
				li.add(sg);
			}
		}
		return li;
	}

	private String encodeData(ConfigList cfg, Object o) {
		if (cfg instanceof StringConfig && ((StringConfig)cfg).isString()) {
			return (String)o;
		}
		else if (cfg instanceof BooleanConfig && ((BooleanConfig)cfg).isBoolean()) {
			return String.valueOf(o);
		}
		else if (cfg instanceof IntegerConfig && ((IntegerConfig)cfg).isNumeric()) {
			return String.valueOf(o);
		}
		else if (cfg instanceof DecimalConfig && ((DecimalConfig)cfg).isDecimal()) {
			return String.valueOf(o);
		}
		else if (cfg instanceof IntArrayConfig && ((IntArrayConfig)cfg).isIntArray()) {
			return Arrays.toString((int[])o);
		}
		else if (cfg instanceof StringArrayConfig && ((StringArrayConfig)cfg).isStringArray()) {
			return Arrays.toString((String[])o);
		}
		else {
			return "";
		}
	}

	private void readData(HashMap<String, String> map, File f) {
		List<String> li = ReikaFileReader.getFileAsLines(f, true, Charsets.UTF_8);
		for (String s : li) {
			if (s.startsWith("[")) {
				int min = s.indexOf('"');
				int max = s.lastIndexOf('"');
				String key = s.substring(min, max).replaceAll("\"", ""); //" [""="" "
				String[] dat = key.split("=");
				if (dat.length == 2)
					map.put(dat[0], dat[1]);
			}
		}
	}

	private boolean setState(BooleanConfig cfg) {
		Property prop = config.get(this.getCategory(cfg), this.getLabel(cfg), cfg.getDefaultState());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultState());
		if (cfg instanceof SelectiveConfig) {
			SelectiveConfig sfg = (SelectiveConfig)cfg;
			if (!sfg.saveIfUnspecified() && prop.getBoolean(cfg.getDefaultState()) == cfg.getDefaultState()) {
				this.removeConfigEntry(cfg);
				return cfg.getDefaultState();
			}
		}
		if (cfg instanceof BoundedConfig && !((BoundedConfig)cfg).isValueValid(prop))
			throw new InvalidConfigException(configMod, (BoundedConfig)cfg, prop);
		if (!prop.isBooleanValue())
			throw new StupidIDException(configMod, prop, Type.BOOLEAN);
		return prop.getBoolean(cfg.getDefaultState());
	}

	private int setValue(IntegerConfig cfg) {
		Property prop = config.get(this.getCategory(cfg), this.getLabel(cfg), cfg.getDefaultValue());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultValue());
		if (cfg instanceof SelectiveConfig) {
			SelectiveConfig sfg = (SelectiveConfig)cfg;
			if (!sfg.saveIfUnspecified() && prop.getInt(cfg.getDefaultValue()) == cfg.getDefaultValue()) {
				this.removeConfigEntry(cfg);
				return cfg.getDefaultValue();
			}
		}
		if (cfg instanceof BoundedConfig && !((BoundedConfig)cfg).isValueValid(prop))
			throw new InvalidConfigException(configMod, (BoundedConfig)cfg, prop);
		if (!prop.isIntValue())
			throw new StupidIDException(configMod, prop, Type.INTEGER);
		return prop.getInt();
	}

	private float setFloat(DecimalConfig cfg) {
		Property prop = config.get(this.getCategory(cfg), this.getLabel(cfg), cfg.getDefaultFloat());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultFloat());
		if (cfg instanceof SelectiveConfig) {
			SelectiveConfig sfg = (SelectiveConfig)cfg;
			if (!sfg.saveIfUnspecified() && (float)prop.getDouble(cfg.getDefaultFloat()) == cfg.getDefaultFloat()) {
				this.removeConfigEntry(cfg);
				return cfg.getDefaultFloat();
			}
		}
		if (cfg instanceof BoundedConfig && !((BoundedConfig)cfg).isValueValid(prop))
			throw new InvalidConfigException(configMod, (BoundedConfig)cfg, prop);
		if (!prop.isDoubleValue())
			throw new StupidIDException(configMod, prop, Type.DOUBLE);
		return (float)prop.getDouble(cfg.getDefaultFloat());
	}

	private String setString(StringConfig cfg) {
		Property prop = config.get(this.getCategory(cfg), this.getLabel(cfg), cfg.getDefaultString());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultString());
		if (cfg instanceof SelectiveConfig) {
			SelectiveConfig sfg = (SelectiveConfig)cfg;
			if (!sfg.saveIfUnspecified() && prop.getString().equals(cfg.getDefaultString())) {
				this.removeConfigEntry(cfg);
				return cfg.getDefaultString();
			}
		}
		if (cfg instanceof BoundedConfig && !((BoundedConfig)cfg).isValueValid(prop))
			throw new InvalidConfigException(configMod, (BoundedConfig)cfg, prop);
		return prop.getString();
	}

	private int[] setIntArray(IntArrayConfig cfg) {
		Property prop = config.get(this.getCategory(cfg), this.getLabel(cfg), cfg.getDefaultIntArray());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultIntArray());
		if (cfg instanceof SelectiveConfig) {
			SelectiveConfig sfg = (SelectiveConfig)cfg;
			if (!sfg.saveIfUnspecified() && Arrays.equals(prop.getIntList(), cfg.getDefaultIntArray())) {
				this.removeConfigEntry(cfg);
				return cfg.getDefaultIntArray();
			}
		}
		if (cfg instanceof BoundedConfig && !((BoundedConfig)cfg).isValueValid(prop))
			throw new InvalidConfigException(configMod, (BoundedConfig)cfg, prop);
		return prop.getIntList();
	}

	private String[] setStringArray(StringArrayConfig cfg) {
		Property prop = config.get(this.getCategory(cfg), this.getLabel(cfg), cfg.getDefaultStringArray());
		if (cfg.isEnforcingDefaults())
			prop.set(cfg.getDefaultStringArray());
		if (cfg instanceof SelectiveConfig) {
			SelectiveConfig sfg = (SelectiveConfig)cfg;
			if (!sfg.saveIfUnspecified() && Arrays.deepEquals(prop.getStringList(), cfg.getDefaultStringArray())) {
				this.removeConfigEntry(cfg);
				return cfg.getDefaultStringArray();
			}
		}
		if (cfg instanceof BoundedConfig && !((BoundedConfig)cfg).isValueValid(prop))
			throw new InvalidConfigException(configMod, (BoundedConfig)cfg, prop);
		return prop.getStringList();
	}

	private String getCategory(ConfigList cfg) {
		String ret = "Control Setup";

		if (cfg instanceof CustomCategoryConfig) {
			String s = ((CustomCategoryConfig)cfg).getCategory();
			if (!Strings.isNullOrEmpty(s))
				ret = s;
		}
		if (cfg instanceof UserSpecificConfig && ((UserSpecificConfig)cfg).isUserSpecific())
			ret = "Client Specific";
		if (cfg instanceof IDRegistry)
			ret = ((IDRegistry)cfg).getCategory();


		return ret;
	}

	private void removeConfigEntry(ConfigList cfg) {
		ConfigCategory cat = config.getCategory(this.getCategory(cfg));
		cat.remove(this.getLabel(cfg));
	}

	private int getValueFromConfig(IDRegistry id, Configuration config) {
		Property prop = config.get(this.getCategory(id), this.getLabel(id), String.valueOf(id.getDefaultID()));
		if (!prop.isIntValue())
			throw new StupidIDException(configMod, prop, Type.INTEGER);
		return prop.getInt(id.getDefaultID());
	}

	private void setOrder() {
		for (String s : config.getCategoryNames()) {
			ConfigCategory c = config.getCategory(s);
			List<String> li = new ArrayList(c.keySet());
			Collections.sort(li, new PropertyComparator(s.toLowerCase(Locale.ENGLISH)));
			c.setPropertyOrder(li);
		}
	}

	private final void loadAdditionalData() {
		for (String c : additionalOptions.keySet()) {
			HashMap<String, DataElement> map = additionalOptions.get(c);
			for (String n : map.keySet()) {
				DataElement e = map.get(n);
				e.data = e.load(config);
			}
		}
	}

	protected final <C> DataElement<C> registerAdditionalOption(String c, String n, C default_) {
		c = c.toLowerCase(Locale.ENGLISH);
		LinkedHashMap<String, DataElement> map = additionalOptions.get(c);
		if (map == null) {
			map = new LinkedHashMap();
			additionalOptions.put(c, map);
		}
		DataElement<C> e = new DataElement(this, c, n, default_, map.size());
		map.put(n, e);
		this.registerOption(c, n, e);
		return e;
	}
	/*
	public void reloadCategoryFromDefaults(String category) {
		config.load();
		ConfigCategory cat = config.getCategory(category);
		cat.clear();
		config.save();
		this.loadConfig();
	}*/

	protected static final class DataElement<C> implements Comparable<DataElement> {

		private C data;
		public final String category;
		public final String name;
		private final ControlledConfig parent;
		private final int index;

		private DataElement(ControlledConfig p, String c, String n, C default_, int idx) {
			parent = p;
			category = c;
			name = n;
			this.data = default_;
			index = idx;
		}

		private Object load(Configuration config) {
			if (data instanceof Boolean) {
				return config.get(category, name, (Boolean)data).getBoolean();
			}
			else if (data instanceof Integer) {
				return config.get(category, name, (Integer)data).getInt();
			}
			else if (data instanceof Float || data instanceof Double) {
				return (float)(config.get(category, name, (Float)data).getDouble());
			}
			else if (data instanceof String) {
				return config.get(category, name, (String)data).getString();
			}
			else if (data instanceof int[]) {
				return config.get(category, name, (int[])data).getIntList();
			}
			else if (data instanceof boolean[]) {
				return config.get(category, name, (boolean[])data).getIntList();
			}
			else if (data instanceof String[]) {
				return config.get(category, name, (String[])data).getStringList();
			}
			else {
				throw new RegistrationException(parent.configMod, "Invalid data element type: "+data+"!");
			}
		}

		public C getData() {
			return data;
		}

		public void setData(C val) {
			data = val;
		}

		@Override
		public String toString() {
			return this.category+":"+this.name+" > "+this.data.toString();
		}

		@Override
		public int compareTo(DataElement o) {
			return Integer.compare(this.index, o.index);
		}
	}

	public final void reload() {
		if (config == null)
			throw new MisuseException("You cannot reload a config before it is initialized!");
		this.load();
	}

	public static ControlledConfig getForMod(String mod) {
		return configs.get(mod);
	}

	public static Collection<ControlledConfig> getConfigs() {
		return Collections.unmodifiableCollection(configs.values());
	}

	public static void reloadAll() {
		for (ControlledConfig c : configs.values()) {
			c.reload();
		}
	}

	private static class UserHash {

		private final String userID;
		private final int hash;

		private UserHash(String id) {
			userID = id;
			hash = id.hashCode();
		}

	}
}
