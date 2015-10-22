package com.exec.filler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

public class UserModelFillerFromRequest<E> {

	private static Field[] fields;

	public void userModelFiller(HttpServletRequest request,
			Object userHibernateObject) throws NoSuchMethodException,
			SecurityException, NumberFormatException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			UnsupportedEncodingException {

		@SuppressWarnings("unchecked")
		Class<E> classLevelObject = (Class<E>) userHibernateObject.getClass();
		fields = userHibernateObject.getClass().getDeclaredFields();
		Method method = null;

		for (Field field : fields) {
			String fieldKey = extractFieldKeyFromModel(field);
			String fieldValue = extractValueFromKeyOfDataModel(request, field);
			if (fieldValue != null) {
				if (field.getType().getSimpleName().equals("Integer")) {
					method = classLevelObject.getMethod(("set" + fieldKey),
							Integer.class);
					method.invoke(userHibernateObject,
							Integer.parseInt(fieldValue));
				}

				else if (field.getType().getSimpleName().equals("String")) {
					method = classLevelObject.getMethod(("set" + fieldKey),
							String.class);
					method.invoke(userHibernateObject,
							URLDecoder.decode(fieldValue, "UTF-8").toString());
				}

				// sysout model data values to ensure the correctness of parsing
				/*
				 * method = classLevelObject.getMethod(("get" + field .getName()
				 * .substring(0, 1) .toUpperCase()
				 * .concat(field.getName().substring(1,
				 * field.getName().length()))));
				 * System.out.println(method.invoke
				 * (userHibernateObject).toString());
				 */
			}
		}

	}

	public void userModelFillerForUpdation(Object dataBaseValues,
			Object toBeUpdatedValues) throws NoSuchMethodException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		@SuppressWarnings("unchecked")
		Class<E> classLevelObject = (Class<E>) dataBaseValues.getClass();
		fields = dataBaseValues.getClass().getDeclaredFields();
		Method methodGet = null;
		Method methodSet = null;
		for (Field field : fields) {
			methodGet = classLevelObject.getMethod("get"
					+ extractFieldKeyFromModel(field));
			Object valuesFromDatabase = methodGet.invoke(dataBaseValues);
			Object valuesFromclient = methodGet.invoke(toBeUpdatedValues);
			if (valuesFromDatabase != null && valuesFromclient == null) {
				if (field.getType().getSimpleName().equals("Integer")) {
					methodSet = classLevelObject.getMethod("set"
							+ extractFieldKeyFromModel(field), Integer.class);
					methodSet.invoke(toBeUpdatedValues,
							(Integer)methodGet.invoke(dataBaseValues));
				} else if (field.getType().getSimpleName().equals("String")) {
					methodSet = classLevelObject.getMethod("set"
							+ extractFieldKeyFromModel(field), String.class);
					methodSet.invoke(toBeUpdatedValues,
							(String)methodGet.invoke(dataBaseValues));
				}

			}

		}

	}

	protected static String extractValueFromKeyOfDataModel(Object request,
			Object field) {
		if (request instanceof HttpServletRequest)
			return ((HttpServletRequest) request).getParameter(((Field) field)
					.getName());

		return null;
	}

	protected static String extractFieldKeyFromModel(Field field) {
		return field.getName().substring(0, 1).toUpperCase()
				.concat(field.getName().substring(1, field.getName().length()));
	}
}
