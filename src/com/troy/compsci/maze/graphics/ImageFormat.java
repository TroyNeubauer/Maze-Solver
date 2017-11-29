package com.troy.compsci.maze.graphics;

public enum ImageFormat {
	
	//format:off
	ALPHA			(1, true ), 
	LUMINANCE		(1, false), 
	LUMINANCE_ALPHA	(2, true ), 
	RGB				(3, false), 
	RGBA			(4, true ), 
	BGRA			(4, true ), 
	ABGR			(4, true );
	//format:on

	final int numComponents;

	final boolean hasAlpha;

	private ImageFormat(int numComponents, boolean hasAlpha) {
		this.numComponents = numComponents;
		this.hasAlpha = hasAlpha;
	}

	public int getNumComponents() {
		return numComponents;
	}

	public boolean isHasAlpha() {
		return hasAlpha;
	}
}
