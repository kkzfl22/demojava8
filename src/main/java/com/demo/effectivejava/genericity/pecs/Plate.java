package com.demo.effectivejava.genericity.pecs;

public class Plate<T> {
	private T item;

	public Plate(T t) {
		item = t;
	}

	public void set(T t) {
		item = t;
	}

	public T get() {
		return item;
	}
}

// Lev 1
class Food {
}

// Lev 2
class Fruit extends Food {
}

class Meat extends Food {
}

// Lev 3
class Apple extends Fruit {
}

class Banana extends Fruit {
}

class Pork extends Meat {
}

class Beef extends Meat {
}

// Lev 4
class RedApple extends Apple {
}

class GreenApple extends Apple {
}