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
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CorrectionScanTask implements ScanTask {

	/* leftTop, leftBottom, rightTop, rightBottom; */
	private final RelativePoint[] relativePoint = new RelativePoint[4];
	
	/**
	 * Construct a corrective task
	 * @param leftTop Left And Top Point
	 * @param rightTop Right And Top Point
	 * @param rightBottom Right And Bottom Point
	 * @param leftBottom Left And Bottom Point
	 */
	public CorrectionScanTask(RelativePoint leftTop, RelativePoint rightTop, RelativePoint rightBottom, RelativePoint leftBottom) {
		if(leftTop.getX() < 0 || leftTop.getX() > 1 || leftTop.getY() < 0 || leftTop.getY() > 1)
			throw new IllegalArgumentException();
		if(rightTop.getX() < 0 || rightTop.getX() > 1 || rightTop.getY() < 0 || rightTop.getY() > 1)
			throw new IllegalArgumentException();
		if(rightBottom.getX() < 0 || rightBottom.getX() > 1 || rightBottom.getY() < 0 || rightBottom.getY() > 1)
			throw new IllegalArgumentException();
		if(leftBottom.getX() < 0 || leftBottom.getX() > 1 || leftBottom.getY() < 0 || leftBottom.getY() > 1)
			throw new IllegalArgumentException();
		if(leftTop.getX() >= rightTop.getX() || rightTop.getY() >= rightBottom.getY()
				|| rightBottom.getX() <= leftBottom.getX() || leftBottom.getY() <= leftTop.getY())
			throw new IllegalArgumentException();
		relativePoint[0] = new RelativePoint(leftTop);
		relativePoint[1] = new RelativePoint(rightTop);
		relativePoint[2] = new RelativePoint(rightBottom);
		relativePoint[3] = new RelativePoint(leftBottom);
	}
	
	public CorrectionScanTask(String s) {
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		if(!json.get("type").getAsString().equals(getClass().getSimpleName()))
			throw new IllegalArgumentException();
		JsonArray pointArray = json.get("points").getAsJsonArray();
		if(pointArray.size() != relativePoint.length)
			throw new IllegalArgumentException();
		for(int i = 0; i < relativePoint.length; i++)
			relativePoint[i] = new RelativePoint(pointArray.get(i).getAsJsonObject().toString());
	}

	public RelativePoint[] getPoints() {
		RelativePoint[] points = new RelativePoint[4];
		for(int i = 0; i < relativePoint.length; i++)
			points[i] = new RelativePoint(relativePoint[i]);
		return points;
	}
	
	private double getLength(AbsolutePoint p1, AbsolutePoint p2) {
		return Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()));
	}
	
	@Override
	public Mat run(Mat mat) {
		if(relativePoint[0].getX() == 0 && relativePoint[0].getY() == 0
				&& relativePoint[1].getX() == 1 && relativePoint[1].getY() == 0
				&& relativePoint[2].getX() == 1 && relativePoint[2].getY() == 1
				&& relativePoint[3].getX() == 0 && relativePoint[3].getY() == 1)
			return mat;
		AbsolutePoint[] absolutePoint = new AbsolutePoint[relativePoint.length];
		for(int i = 0; i < relativePoint.length; i++)
			absolutePoint[i] = new AbsolutePoint(relativePoint[i], mat.cols(), mat.rows());
		double diagonal1 = getLength(absolutePoint[0], absolutePoint[2]), diagonal2 = getLength(absolutePoint[1], absolutePoint[3]);
		double diagonal = diagonal1 > diagonal2 ? diagonal1 : diagonal2;
		double leftEdge = getLength(absolutePoint[0], absolutePoint[3]), rightEdge = getLength(absolutePoint[1], absolutePoint[2]);
		double height1 = leftEdge > rightEdge ? leftEdge : rightEdge;
		double width1 = Math.sqrt(diagonal * diagonal - height1 * height1);
		double topEdge = getLength(absolutePoint[0], absolutePoint[1]), bottomEdge = getLength(absolutePoint[3], absolutePoint[2]);
		double width2 = topEdge > bottomEdge ? topEdge : bottomEdge;
		double height2 = Math.sqrt(diagonal * diagonal - width2 * width2);
		double area1 = width1 * height1, area2 = width2 * height2;
		double width = area1 > area2 ? width1 : width2;
		double height = area1 > area2 ? height1 : height2;
		MatOfPoint2f src = new MatOfPoint2f(new Point(absolutePoint[0].getX(), absolutePoint[0].getY()), new Point(absolutePoint[1].getX(), absolutePoint[1].getY()), 
				new Point(absolutePoint[2].getX(), absolutePoint[2].getY()), new Point(absolutePoint[3].getX(), absolutePoint[3].getY()));
		MatOfPoint2f dst = new MatOfPoint2f(new Point(0, 0), new Point(width, 0), 
				new Point(width, height), new Point(0, height));
		Mat m = Imgproc.getPerspectiveTransform(src, dst);
		Mat outputMat = new Mat();
		Imgproc.warpPerspective(mat, outputMat, m, new Size(width, height));
		return outputMat;
	}

	@Override
	public String toJSON() {
		JsonObject json = new JsonObject();
		json.addProperty("type", getClass().getSimpleName());
		JsonArray points = new JsonArray();
		for(int i = 0; i < relativePoint.length; i++)
			points.add(new JsonParser().parse(relativePoint[i].toJSON()).getAsJsonObject());
		json.add("points", points);
		return json.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof CorrectionScanTask) {
			CorrectionScanTask target = (CorrectionScanTask) object;
			boolean same = true;
			for(int i = 0; i < relativePoint.length; i++)
				same &= target.relativePoint[i].equals(relativePoint[i]);
			return same;
		}
		return false;
	}

}
