package com.team6project.cavallo_mall.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * description: Class designed to get and set fields using Reflections
 * author: Yuchen Bai
 * email: y.bai19@newcastle.ac.uk
 * date: 2021/2/20 11:31
 */
@Slf4j
public class Refs {

	private Refs() {}

	public static Field[] getAllFields(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	public static void setFieldValue(Object obj, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Object getFieldValue(Object objectX, Field field) {
		try {
			field.setAccessible(true);
			return field.get(objectX );
		} catch (Exception e) {
			log.info("  ######## WARN JReflection.getFieldValue error " + (objectX == null ? "object" : objectX.getClass().getSimpleName()) +field+"  ########");
		}
		return null;
	}

	public static Field getFieldByFieldName(Object obj, String fieldName) {
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				log.error("Get field error");
				return null;
			}
		}
		return null;
	}
}
