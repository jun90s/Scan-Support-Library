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
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RotatingScanTask implements ScanTask {

	private final double x, y;
	private final int angle;
	
	/** 
	 * Constructing a rotation task
	 * @param x Center of rotation on the x-axis
	 * @param y Center of rotation on the y-axis
	 * @param angle Angle
	 * @throws IllegalArgumentException
	 */
	public RotatingScanTask(double x, double y, int angle) throws IllegalArgumentException {
		if(x < 0 || x > 1 || y < 0 || y > 1) throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
		this.angle = angle;
	}
	
	public RotatingScanTask(String s) {
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		if(!json.get("type").getAsString().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException();
		x = json.get("x").getAsDouble();
		y = json.get("y").getAsDouble();
		angle = json.get("angle").getAsInt();
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getAngle() {
		return angle;
	}

	@Override
	public Mat run(Mat mat) {
		if(angle % 360 == 0) return mat;
		Mat outputMat = new Mat();
		Mat m = Imgproc.getRotationMatrix2D(new Point(Math.round(mat.cols() * x), 
				Math.round(mat.rows() * y)), angle, 1);
		Imgproc.warpAffine(mat, outputMat, m, mat.size());
		return outputMat;
	}

	@Override
	public String toJSON() {
		JsonObject json = new JsonObject();
		json.addProperty("type", getClass().getSimpleName());
		json.addProperty("x", x);
		json.addProperty("y", y);
		json.addProperty("angle", angle);
		return json.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof RotatingScanTask) {
			RotatingScanTask target = (RotatingScanTask) object;
			if(target.x == x && target.y == y && target.angle == target.angle)
				return true;
		}
		return false;
	}

}
