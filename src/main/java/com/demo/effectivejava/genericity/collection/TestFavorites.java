package com.demo.effectivejava.genericity.collection;

public class TestFavorites {

	public static void main(String[] args) {
		FavoritesImp inst = new FavoritesImp();

		inst.putFavorite(String.class, "java");
		inst.putFavorite(Integer.class, 0xcafebabe);
		inst.putFavorite(Class.class, Favorites.class);

		String strValue = inst.getFavorite(String.class);
		int intValue = inst.getFavorite(Integer.class);
		Class<?> favClass = inst.getFavorite(Class.class);

		System.out.printf("%s %x %s %s", strValue, intValue, favClass.getName());

	}

}
