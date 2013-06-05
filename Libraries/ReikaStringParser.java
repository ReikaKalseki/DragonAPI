package Reika.DragonAPI.Libraries;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Reika.DragonAPI.DragonAPICore;

public class ReikaStringParser extends DragonAPICore {

	private static final String NUMBER_VARIABLE_CODE = "PARSE_NUMBER_VARIABLE";
	private static final String NUMBER_METHOD_CODE = "PARSE_NUMBER_METHOD";
	private static final String STRING_FORMAT_CODE = "PARSE_STRING_FORMAT";
	private static final String ENUM_FUNCTION_CODE = "PARSE_ENUM";

	public static String getStringWithEmbeddedReferences(String sg) {
		String[] parts = sg.split("+");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (isReference(parts[i])) {
				sb.append(parseReference(parts[i]));
			}
		}
		return sb.toString();
	}

	private static String parseReference(String sg) {
		if (sg.startsWith(NUMBER_VARIABLE_CODE))
			return parseNumberVariable(sg);
		if (sg.startsWith(NUMBER_METHOD_CODE))
			return parseNumberMethod(sg);
		if (sg.startsWith(STRING_FORMAT_CODE))
			return parseStringFormat(sg);
		if (sg.startsWith(ENUM_FUNCTION_CODE))
			return parseEnum(sg);
		return null;
	}

	private static String parseEnum(String sg) {
		String[] parts = sg.split(".");
		if (parts.length != 3)
			throw new RuntimeException("This method does not support multi-layer class calls! "+sg);
		String enumClassName = ReikaJavaLibrary.subtractFrom(parts[0], ENUM_FUNCTION_CODE+"(");
		parts[2] = parts[2].substring(0, parts[2].length()-1);
		Class enumClass;
		try {
			enumClass = Class.forName(enumClassName);
			Enum e = Enum.valueOf(enumClass, parts[1]);
			Method m = enumClass.getDeclaredMethod(parts[2], (Class[])null);
			Object o = m.invoke(e, (Object[])null);
			return o.toString();
		}
		catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Enum Class \""+enumClassName+"\" does not exist!");
		}
		catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Enum Class "+enumClassName+" does not contain method \""+parts[2]+"\"!");
		}
		catch (SecurityException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Enum Class "+enumClassName+"'s method "+parts[2]+" threw security exception!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+enumClassName+"'s method "+parts[2]+" threw illegal access exception!");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+enumClassName+"'s method "+parts[2]+" is not parameterless!");
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+enumClassName+"'s method "+parts[2]+" threw invocation target exception!");
		}
	}

	private static String parseStringFormat(String sg) {
		return null;
	}

	private static String parseNumberMethod(String sg) {
		String[] parts = sg.split(".");
		if (parts.length > 2)
			throw new RuntimeException("This method does not support multi-layer class calls! "+sg);
		parts[1] = parts[1].substring(0, parts[1].length()-1);
		String cl = ReikaJavaLibrary.subtractFrom(parts[0], NUMBER_METHOD_CODE+"(");
		String obj;
		try {
			Class c = Class.forName(cl);
			Method m = c.getMethod(parts[1], (Class[])null);
			Object g = new Object();
			Object o = m.invoke(g, (Object[])null);
			obj = o.toString();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Class \""+cl+"\" does not exist!");
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+" does not contain method \""+parts[1]+"\"!");
		}
		catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s method "+parts[1]+" threw security exception!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s method "+parts[1]+" threw illegal access exception!");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s method "+parts[1]+" is not parameterless!");
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s method "+parts[1]+" threw invocation target exception!");
		}
		return obj;
	}

	private static String parseNumberVariable(String sg) {
		String[] parts = sg.split(".");
		if (parts.length > 2)
			throw new RuntimeException("This method does not support multi-layer class calls! "+sg);
		parts[1] = parts[1].substring(0, parts[1].length()-1);
		String cl = ReikaJavaLibrary.subtractFrom(parts[0], NUMBER_VARIABLE_CODE+"(");
		String obj;
		try {
			Class c = Class.forName(cl);
			Field f = c.getField(parts[1]);
			obj = f.toString();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Class \""+cl+"\" does not exist!");
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+" does not contain field \""+parts[1]+"\"!");
		}
		catch (SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException("Class "+cl+"'s field "+parts[1]+" threw security exception!");
		}
		return obj;
	}

	private static boolean isReference(String sg) {
		if (sg.startsWith(NUMBER_VARIABLE_CODE))
			return true;
		if (sg.startsWith(NUMBER_METHOD_CODE))
			return true;
		if (sg.startsWith(STRING_FORMAT_CODE))
			return true;
		if (sg.startsWith(ENUM_FUNCTION_CODE))
			return true;
		return false;
	}
}
