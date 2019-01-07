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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RelativePoint implements Point {

	private double x, y;

	public RelativePoint(RelativePoint point) {
		x = point.x;
		y = point.y;
	}

	public RelativePoint(AbsolutePoint point, int width, int height) {
		x = point.getX() * 1.0 / width;
		y = point.getY() * 1.0 / height;
	}
	
	public RelativePoint(String s) {
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		if(!json.get("type").getAsString().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException();
		x = json.get("x").getAsDouble();
		y = json.get("y").getAsDouble();
	}
	
	public RelativePoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof RelativePoint) {
			RelativePoint target = (RelativePoint) object;
			if(target.x == x && target.y == y)
				return true;
		}
		return false;
	}

	@Override
	public String toJSON() {
		JsonObject json = new JsonObject();
		json.addProperty("type", getClass().getSimpleName());
		json.addProperty("x", x);
		json.addProperty("y", y);
		return json.toString();
	}
	
}
