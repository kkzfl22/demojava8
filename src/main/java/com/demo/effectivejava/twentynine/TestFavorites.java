package com.demo.effectivejava.twentynine;

public class TestFavorites {

	public static void main(String[] args) {
		Favorites f = new Favorites();

		f.putFavorite(String.class, "java");
		f.putFavorite(Integer.class, 0xcafebabe);
		f.putFavorite(Class.class, Favorites.class);

		String favoriteString = f.getFavorite(String.class);
		System.out.println(favoriteString);

		int favoriteInt = f.getFavorite(Integer.class);
		System.out.println(favoriteInt);

		Class<?> favoriteClass = f.getFavorite(Class.class);
		System.out.println(favoriteClass.getName());
	}

}
