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

import org.opencv.core.Core;
import org.opencv.core.Mat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MirrorScanTask implements ScanTask {

	private final boolean x, y;
	
	/**
	 * Construct a mirror flip task
	 * @param x Flip along the X axis
	 * @param y Flip along the Y axis
	 */
	public MirrorScanTask(boolean x, boolean y) {
		this.x = x;
		this.y = y;
	}
	
	public MirrorScanTask(String s) {
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		if(!json.get("type").getAsString().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException();
		x = json.get("x").getAsBoolean();
		y = json.get("y").getAsBoolean();
	}
	
	public boolean isX() {
		return x;
	}

	public boolean isY() {
		return y;
	}

	@Override
	public Mat run(Mat mat) {
		if(!x && !y) return mat;
		Mat outputMat = new Mat();
		if(x && y) {
			Core.flip(mat, outputMat, -1);
		} else if(x) {
			Core.flip(mat, outputMat, 1);
		} else if(y) {
			Core.flip(mat, outputMat, 0);
		} else {
			mat.copyTo(outputMat);
		}
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
		if(object instanceof MirrorScanTask) {
			MirrorScanTask target = (MirrorScanTask) object;
			if(target.x == x && target.y == y)
				return true;
		}
		return false;
	}

}
