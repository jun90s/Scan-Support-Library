/* 
 * Copyright 2019 Zhang Jun <jun90s@163.com>.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jun90.projects.scan.support;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CroppingScanTask implements ScanTask {

	private final double x, y, width, height;
	
	/**
	 * Constructing a clipping task
	 * @param x Starting position
	 * @param y Starting position
	 * @param width Width
	 * @param height Height
	 * @throws IllegalArgumentException
	 */
	public CroppingScanTask(double x, double y, double width, double height) throws IllegalArgumentException {
		if(x + width > 1 || y + height > 1 || x < 0 || x > 1 || y < 0 || y > 1 
				|| width <= 0 || width > 1 || height <= 0 || height > 1)
			throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public CroppingScanTask(String s) {
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		if(!json.get("type").getAsString().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException();
		x = json.get("x").getAsDouble();
		y = json.get("y").getAsDouble();
		width = json.get("width").getAsDouble();
		height = json.get("height").getAsDouble();
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	@Override
	public Mat run(Mat mat) {
		if(x == 0 && y == 0 && width == 1 && height == 1) return mat;
		Mat outputMat = new Mat();
		new Mat(mat, new Rect((int) Math.round(mat.cols() * x),
				(int) Math.round(mat.rows() * y),
				(int) Math.round(mat.cols() * width), 
				(int) Math.round(mat.rows() * height))).copyTo(outputMat);
		return outputMat;
	}

	@Override
	public String toJSON() {
		JsonObject json = new JsonObject();
		json.addProperty("type", getClass().getSimpleName());
		json.addProperty("x", x);
		json.addProperty("y", y);
		json.addProperty("width", width);
		json.addProperty("height", height);
		return json.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof CroppingScanTask) {
			CroppingScanTask target = (CroppingScanTask) object;
			if(target.x == x && target.y == y && target.width == target.width && target.height == target.height)
				return true;
		}
		return false;
	}
	
}
