package Deep_clone;

import java.util.Map;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CopyUtils {

	/*
	 * ComplexObject obj = ... ComplexObject copy = CopyUtils.deepCopy(obj);
	 * 
	 * You need to write a deepCopy() method that works on objects of arbitrary
	 * number of class fields and arbitrary types, elements of an array/list can be
	 * absolutely any data types, including arrays and lists. And also there are
	 * recursive data structures - when an object somewhere in its depths contains a
	 * reference to itself (or to a part of itself).
	 * 
	 * Some details: - First of all, the method should work correctly. Speed is also
	 * important, but not as much as correctness - You can only use the features of
	 * the standard J2SE library - Code should be written in Java (version 21 and
	 * above) or Kotlin. - The assignment must have a working main() method, to
	 * demonstrate how it works - The completed assignment should be posted on
	 * GitHub
	 * 
	 * P.S. I know about hacks with `java.io.Serializable` and
	 * `java.lang.Cloneable`, please don't use them
	 */

	public static Object deepCopy(Object obj) throws Exception {

		Map<Object, Object> alreadyCopied = new HashMap<>();
		return deepCopyElement(obj, alreadyCopied);
	}

	private static Object deepCopyElement(Object obj, Map<Object, Object> alreadyCopied) throws Exception {

		if (obj == null)
			return obj;

		Class<?> cls = obj.getClass();

		// check if primitive data type, wrapper class of primitive data type, string or
		// enum
		if (isPrimitive(cls))
			return obj;

		if (alreadyCopied.containsKey(obj))
			return alreadyCopied.get(obj);

		// if array
		if (cls.isArray()) {
			return copyArray(obj, cls, alreadyCopied);
		}

		// if Arrays$ArrayList inner class used by Arrays.asList()
		if (List.class.isAssignableFrom(cls)) {
			return copyArrayArrayList(obj, cls, alreadyCopied);
		}

		// create object
		Object copiedObj = createObject(obj, cls);
		alreadyCopied.put(obj, copiedObj);

		// copy declared fields
		return copyFields(obj, cls, copiedObj, alreadyCopied);
	}

	private static boolean isPrimitive(Class<?> cls) {
		return cls.isPrimitive() || cls == Byte.class || cls == Short.class || cls == Integer.class || cls == Long.class
				|| cls == Float.class || cls == Double.class || cls == Boolean.class || cls == Character.class
				|| cls == String.class || cls.isEnum();
	}

	private static Object copyArray(Object obj, Class<?> cls, Map<Object, Object> alreadyCopied) throws Exception {
		int len = Array.getLength(obj);
		Object copiedObj = Array.newInstance(cls.getComponentType(), len);
		alreadyCopied.put(obj, copiedObj);
		for (int i = 0; i < len; i++) {
			Object element = Array.get(obj, i);
			Object copiedElement = deepCopyElement(element, alreadyCopied);
			Array.set(copiedObj, i, copiedElement);
		}
		return copiedObj;
	}

	private static Object copyArrayArrayList(Object obj, Class<?> cls, Map<Object, Object> alreadyCopied)
			throws Exception {
		// consider the original object as a List
		List<?> originalList = (List<?>) obj;

		// create a new ArrayList to copy the elements into
		List<Object> copiedObj = new ArrayList<>(originalList.size());

		// loop over the original list and deep copy the elements
		for (Object element : originalList) {
			copiedObj.add(deepCopyElement(element, alreadyCopied)); // Recursively deep copy each element
		}
		return copiedObj;
	}

	private static Object createObject(Object obj, Class<?> cls) throws Exception {
		Constructor<?> constructorToUse = null;
		// try to find a no-argument constructor
		try {
			constructorToUse = cls.getDeclaredConstructor();
			constructorToUse.setAccessible(true);
			return constructorToUse.newInstance();
		} catch (NoSuchMethodException e) {
			System.out.println("No no-argument constructor is found");
		}

		// if not found, try to find a constructor with argument(s)
		Constructor<?>[] constructors = cls.getDeclaredConstructors();
		Object[] argsToUse = null;

		for (Constructor<?> constructor : constructors) {
			constructor.setAccessible(true);
			Class<?>[] paramTypes = constructor.getParameterTypes();
			int paramLengh = paramTypes.length;

			// match constructor parameters with values from the original object
			Object[] args = new Object[paramLengh];
			boolean matchFound = true;
			for (int i = 0; i < paramLengh; i++) {
				Field matchingField = findMatchingField(cls, paramTypes[i], i);
				if (matchingField == null) {
					matchFound = false;
					break;
				}
				matchingField.setAccessible(true);
				args[i] = matchingField.get(obj);
			}

			if (matchFound) {
				constructorToUse = constructor;
				argsToUse = args;
				break;
			}
		}

		// instantiate the new object
		return constructorToUse.newInstance(argsToUse);
	}

	private static Field findMatchingField(Class<?> cls, Class<?> paramType, int index) {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if (field.getType().equals(paramType)) {
				return field;
			}
		}
		return null;
	}

	private static Object copyFields(Object obj, Class<?> cls, Object copiedObj, Map<Object, Object> alreadyCopied)
			throws Exception {
		Class<?> currentCls = cls;
		while (currentCls != null) {
			for (Field field : currentCls.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					Object value = field.get(obj);
					Object copiedValue = deepCopyElement(value, alreadyCopied);
					field.set(copiedObj, copiedValue);
				}
			}
			currentCls = currentCls.getSuperclass();
		}
		return copiedObj;
	}
}
