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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.graphics.Bitmap;

public class ImageScanner<T> {

	private Class<T> type;
	private List<ScanTask> tasks = new LinkedList<ScanTask>();
	
	public ImageScanner(Class<T> type) throws RuntimeException {
		this.type = type;
		try {
			if(type == Class.forName("android.graphics.Bitmap")) {
				return;
			}
		} catch (ClassNotFoundException e) { }
		try {
			if(type == Class.forName("java.awt.image.BufferedImage")) {
				return;
			}
		} catch (ClassNotFoundException e) { }
		throw new RuntimeException("Unsupport Class");
	}
	
	public ImageScanner(Class<T> type, String s) throws RuntimeException {
		this(type);
		JsonObject json = new JsonParser().parse(s).getAsJsonObject();
		JsonArray taskArray = json.get("tasks").getAsJsonArray();
		for(JsonElement element : taskArray) {
			String className = element.getAsJsonObject().get("type").getAsString();
			if(className == null || !className.endsWith("ScanTask"))
				throw new IllegalArgumentException();
			try {
				tasks.add((ScanTask) Class.forName("com.jun90.projects.scan.support." + className)
						.getConstructor(String.class).newInstance(element.toString()));
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
				throw new IllegalArgumentException();
			}
		}
	}
	
	/**
	 * Add task
	 * @param task Task
	 */
	public synchronized boolean addTask(ScanTask task) {
		return tasks.add(task);
	}

	/**
	 * Removes all of the tasks
	 */
	public synchronized void clear() {
		tasks.clear();
	}
	
	private synchronized Mat run(Mat mat) throws IllegalArgumentException {
		for(ScanTask task : tasks) {
			mat = task.run(mat);
			if(mat == null) throw new RuntimeException();
		}
		return mat;
	}

	/**
	 * Run tasks
	 * @param image Source image
	 * @return Image
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("unchecked")
	public synchronized T run(T image) throws IllegalArgumentException {
		if(image == null) throw new IllegalArgumentException();
		try {
			if(type == Class.forName("android.graphics.Bitmap")) {
				Bitmap source = (Bitmap) image;
				/* Image to Mat */
				Mat mat = new Mat(), t = new Mat();
				Utils.bitmapToMat(source, t);
				Imgproc.cvtColor(t, mat, Imgproc.COLOR_BGRA2BGR);
				/* Work */
				mat = run(mat);
				/* Mat to Image */
				Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(mat, bitmap);
				return (T) bitmap;
			}
		} catch (ClassNotFoundException e) { }
		try {
			if(type == Class.forName("java.awt.image.BufferedImage")) {
				BufferedImage source = (BufferedImage) image;
				if(source.getType() != BufferedImage.TYPE_3BYTE_BGR) throw new IllegalArgumentException();
				/* Image to Mat */
				Mat mat = new Mat(source.getHeight(), source.getWidth(), CvType.CV_8UC3);
				mat.put(0, 0, ((DataBufferByte) source.getRaster().getDataBuffer()).getData());
				/* Work */
				mat = run(mat);
				/* Mat to Image */
				return (T) HighGui.toBufferedImage(mat);
			}
		} catch (ClassNotFoundException e) { }
		return null;
	}
	
	public synchronized String toJSON() {
		JsonObject json = new JsonObject();
		JsonArray taskArray = new JsonArray();
		for(ScanTask task : tasks)
			taskArray.add(new JsonParser().parse(task.toJSON()));
		json.add("tasks", taskArray);
		return json.toString();
	}
	
}
