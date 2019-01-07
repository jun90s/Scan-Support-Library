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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ZoomScanTask implements ScanTask {
	
	private final double x, y;
	
	/**
	 * Constructing a scaling task
	 * @param x X scaling
	 * @param y Y scaling
	 * @throws IllegalArgumentException
	 */
	public ZoomScanTask(double x, double y) throws IllegalArgumentException {
		if(x <= 0 || y <= 0) throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
	}
	
	public ZoomScanTask(String s) {
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		if(!json.get("type").getAsString().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException();
		x = json.get("x").getAsDouble();
		y = json.get("y").getAsDouble();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public Mat run(Mat mat) {
		if(x == 1 && y == 1) return mat;
		Mat outputMat = new Mat();
		Imgproc.resize(mat, outputMat, 
				new Size(Math.round(mat.cols() * x), Math.round(mat.rows() * y)));
		return outputMat;
	}

	@Override
	public String toJSON() {
		JsonObject json = new JsonObject();
		json.addProperty("type", getClass().getSimpleName());
		json.addProperty("x", x);
		json.addProperty("y", y);
		return json.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof ZoomScanTask) {
			ZoomScanTask target = (ZoomScanTask) object;
			if(target.x == x && target.y == y)
				return true;
		}
		return false;
	}

}
