package com.demo.effectivejava.genericity.collection;

import java.util.HashMap;
import java.util.Map;

public class FavoritesImp implements Favorites {

	private Map<Class<?>, Object> favorites = new HashMap<>();

	@Override
	public <T> void putFavorite(Class<T> type, T instance) {
		if (type == null) {
			throw new NullPointerException("curr type is null");
		}
		favorites.put(type, instance);
	}

	@Override
	public <T> T getFavorite(Class<T> type) {
		return type.cast(favorites.get(type));
	}

}
