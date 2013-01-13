package com.github.nigelzor.hogs;

public class NoMove extends Move {

	@Override
	public Board apply(Board input) {
		return input;
	}
}
