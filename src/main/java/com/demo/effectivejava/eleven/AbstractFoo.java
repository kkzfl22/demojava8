package com.demo.effectivejava.eleven;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractFoo {
	private int x, y;

	private enum State {
		NEW, INITIALIZING, INITIALIZED
	}

	private final AtomicReference<State> init = new AtomicReference<AbstractFoo.State>(State.NEW);

	protected AbstractFoo(int x, int y) {
		initialize(x, y);
	}

	protected AbstractFoo() {

	}

	protected final void initialize(int x, int y) {
		if (!init.compareAndSet(State.NEW, State.INITIALIZING)) {
			throw new IllegalStateException("Already initialized");
		}
		this.x = x;
		this.y = y;
		init.set(State.INITIALIZED);
	}

	private void checkInit() {
		if (init.get() != State.INITIALIZED) {
			throw new IllegalStateException("Uninitialized");
		}

	}

	protected final int getx() {
		checkInit();
		return x;
	}

	protected final int gety() {
		checkInit();
		return y;
	}
}
