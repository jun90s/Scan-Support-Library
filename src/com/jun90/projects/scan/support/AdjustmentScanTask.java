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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AdjustmentScanTask implements ScanTask {

	private final double contrast;
	private final int brightness;
	
	/**
	 * Construct an adjustment task
	 * @param contrast Contrast (Between -1.0 and 1.0)
	 * @param brightness Brightness (Between -1.0 and 1.0)
	 */
	public AdjustmentScanTask(double contrast, double brightness) {
		if(contrast < -1 || contrast > 1 || brightness < -1 || brightness > 1)
			throw new IllegalArgumentException();
		this.contrast = contrast + 1;
		this.brightness = (int) (brightness * 256);
	}
	
	public AdjustmentScanTask(String s) {
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		if(!json.get("type").getAsString().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException();
		contrast = json.get("contrast").getAsDouble() + 1;
		brightness = (int) (json.get("brightness").getAsDouble() * 256);
	}
	
	public double getContrast() {
		return contrast - 1;
	}

	public double getBrightness() {
		return brightness / 256.0;
	}

	@Override
	public Mat run(Mat mat) {
		if(contrast == 1 && brightness == 0) return mat;
		Mat outputMat = Mat.zeros(mat.rows(), mat.cols(), mat.type());
		mat.convertTo(outputMat, mat.type(), contrast, brightness);
		return outputMat;
	}

	@Override
	public String toJSON() {
		JsonObject json = new JsonObject();
		json.addProperty("type", getClass().getSimpleName());
		json.addProperty("contrast", getContrast());
		json.addProperty("brightness", getBrightness());
		return json.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof AdjustmentScanTask) {
			AdjustmentScanTask target = (AdjustmentScanTask) object;
			if(target.contrast == contrast && target.brightness == brightness)
				return true;
		}
		return false;
	}

}
