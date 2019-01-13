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

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RotatingScanTask implements ScanTask {

	private final int angle;
	
	/** 
	 * Constructing a rotation task
	 * @param x Center of rotation on the x-axis
	 * @param y Center of rotation on the y-axis
	 * @param angle Angle
	 * @throws IllegalArgumentException
	 */
	public RotatingScanTask(int angle) throws IllegalArgumentException {
		this.angle = angle;
	}
	
	public RotatingScanTask(String s) {
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		if(!json.get("type").getAsString().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException();
		angle = json.get("angle").getAsInt();
	}

	public int getAngle() {
		return angle;
	}

	@Override
	public Mat run(Mat mat) {
		if(angle % 360 == 0) return mat;
		double radians = Math.toRadians(angle);
		int diagonal = (int) (Math.sqrt(mat.cols() * mat.cols() + mat.rows() * mat.rows()));
		Mat tempMat = new Mat(diagonal, diagonal, CvType.CV_8UC3), tempMat2 = new Mat(), outputMat = new Mat();
		int offsetX = (diagonal - mat.cols()) / 2;
		int offsetY = (diagonal - mat.rows()) / 2;
		mat.copyTo(tempMat.submat(offsetY, offsetY + mat.rows(), offsetX, offsetX + mat.cols()));
		int width = (int) (Math.abs(mat.cols() * Math.cos(radians)) + Math.abs(mat.rows() * Math.sin(radians)));
		int height = (int) (Math.abs(mat.cols() * Math.sin(radians)) + Math.abs(mat.rows() * Math.cos(radians)));
		Mat m = Imgproc.getRotationMatrix2D(new Point((int) (tempMat.cols() * 0.5), (int) (tempMat.rows() * 0.5)), -angle, 1);
		Imgproc.warpAffine(tempMat, tempMat2, m, tempMat.size());
		new Mat(tempMat2, new Rect((diagonal - width) / 2, (diagonal - height) / 2, width, height)).copyTo(outputMat);
		return outputMat;
	}

	@Override
	public String toJSON() {
		JsonObject json = new JsonObject();
		json.addProperty("type", getClass().getSimpleName());
		json.addProperty("angle", angle);
		return json.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof RotatingScanTask) {
			RotatingScanTask target = (RotatingScanTask) object;
			if(target.angle == target.angle)
				return true;
		}
		return false;
	}

}
