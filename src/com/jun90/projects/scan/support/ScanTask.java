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

public interface ScanTask {

	/**
	 * Run task
	 * @param mat Source Mat
	 * @return Mat
	 */
	public Mat run(Mat mat);
	
	public String toJSON();
	
}
