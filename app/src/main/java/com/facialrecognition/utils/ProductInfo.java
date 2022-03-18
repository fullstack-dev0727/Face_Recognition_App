package com.facialrecognition.utils;

import android.graphics.Color;

import java.io.Serializable;

public class ProductInfo implements Serializable{
	private static 			final long serialVersionUID = 1L;    
	public String 			name = "";
	public int	 			red = 0;
	public int	 			green = 0;
	public int	 			blue = 0;

	public int getColor() {
		return Color.rgb(red, green, blue);
	}
}
