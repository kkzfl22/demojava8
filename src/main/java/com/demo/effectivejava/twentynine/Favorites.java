package com.demo.effectivejava.twentynine;

import java.util.HashMap;
import java.util.Map;

public class Favorites {

	private Map<Class<?>, Object> favoritesMap = new HashMap<>();

	public <T> void putFavorite(Class<T> type, T instance) {
		if (null == type) {
			throw new NullPointerException("type is null");
		}
		favoritesMap.put(type, instance);
	}

	public <T> T getFavorite(Class<T> type) {
		return type.cast(favoritesMap.get(type));
	}

}
