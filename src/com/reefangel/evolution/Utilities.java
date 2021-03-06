package com.reefangel.evolution;

import android.graphics.drawable.Drawable;

public class Utilities {
	static void centerAround(int x, int y, Drawable d) {
		int w = d.getIntrinsicWidth();
		int h = d.getIntrinsicHeight();
		int left = x - w / 2;
		int top = y ;
		int right = left + w;
		int bottom = top + h;
		d.setBounds(left, top, right, bottom);
	}

}
