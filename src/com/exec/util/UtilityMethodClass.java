package com.exec.util;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class UtilityMethodClass {
	public static boolean userExists(List list, Object... model) {
		for (Object listObject : list) {
			Object listObjectToArray[] = (Object[]) listObject;
			for (int index = 0; index < listObjectToArray.length; index++) {
				if (listObjectToArray[index].equals(model[index]))
					return true;
				// System.out.println(listObjectToArray[index]);
			}

		}
		return false;
	}
	public static List queryDataAndLoginObject(String queryString,Session newSession) {

		newSession.beginTransaction();
		Query query = newSession.createQuery(queryString);
		List dataModels = query.list();
//		for (Object o : dataModels)
//			System.out.println(o);
		return dataModels;
	}
}
