/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * This file is part of P-GRADE Grid Portal.
 *
 * P-GRADE Grid Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * P-GRADE Grid Portal is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * P-GRADE Grid Portal.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2006-2009 MTA SZTAKI
 *
 */

package hu.sztaki.lpds.pgportal.services.utils;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.*;
import java.text.*;
import java.util.Calendar;
import java.util.regex.*;


public class MiscUtils {


	private MiscUtils() {
	}

	

	public static String getSpaceSizeFromKB(long xKB) {
		return getSpaceSizeFromByte(xKB * 1024);
	}

	public static String getSpaceSizeFromByte(long xB) {
		String ret = "";
		DecimalFormat myFormatter = new DecimalFormat("###.###");
		double xBd = (double) xB;
		long Hi = 1;
		Hi = Hi << 60;
		long Pi = 1;
		Pi = Pi << 50;
		double Pd = (double) Pi;
		long Ti = 1;
		Ti = Ti << 40;
		double Td = (double) Ti;
		long Gi = 1;
		Gi = Gi << 30;
		double Gd = (double) Gi;
		long Mi = 1;
		Mi = Mi << 20;
		double Md = (double) Mi;
		long Ki = 1;
		Ki = Ki << 10;
		double Kd = (double) Ki;
		if ((xB >= Hi) || (xB < 0))
			ret = "unreliable high";
		else if (xB >= Pi) {
			ret = myFormatter.format(xBd / Pd) + " [PB]";
		} else if (xB >= Ti) {
			ret = myFormatter.format(xBd / Td) + " [TB]";
		}

		else if (xB >= Gi) {
			ret = myFormatter.format(xBd / Gd) + " [GB]";
		} else if (xB >= Mi) {
			ret = myFormatter.format(xBd / Md) + " [MB]";
		} else if (xB >= Ki) {
			ret = myFormatter.format(xBd / Kd) + " [KB]";
		} else
			ret = myFormatter.format(xB) + "  [B]";
		return ret;
	}

	public static long getDUDirSizeInBytes(File f) {
		try {
			Process p = Runtime.getRuntime().exec(
					"du -s -b " + f.getAbsolutePath());
			p.waitFor();
			BufferedReader r = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String re = (r.readLine().split("\\s"))[0];
			r.close();
			return Long.parseLong(re);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public static long getFileSizeInBytes(File f) {
		long size = 0;
		if (f.isFile()) {
			size = f.length();
		} else if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					size += files[i].length();
				} else if (files[i].isDirectory()) {
					size += getFileSizeInBytes(files[i]);
				}
			}
		}
		return size;
	}

	public static boolean isHostValid(String host) {
		if (host == null) {
			return false;
		}
		if (host.equals("")) {
			return false;
		}

		String section = "([a-zA-Z\\d]){1}(([a-zA-Z\\d-])*([a-zA-Z\\d]){1})*";
		Pattern pattern = Pattern.compile("^(" + section + "\\." + section
				+ ")(\\." + section + ")*$");
		Matcher matcher = pattern.matcher(host.toLowerCase());
		if (!matcher.find())
			return false;
		return true;
	}

	public static boolean isPortValid(String port) {
		if (port == null) {
			return false;
		}

		Pattern pattern = Pattern.compile("^(\\d){4}$");
		Matcher matcher = pattern.matcher(port);
		if (!matcher.find())
			return false;

		return true;
	}

	public static void printLog(String path, String text) {
		// path : package.Class.func()
		java.util.Calendar c = Calendar.getInstance();
		System.out.print("[" + c.get(Calendar.YEAR) + "."
				+ (c.get(Calendar.MONTH) + 1) + "."
				+ c.get(Calendar.DAY_OF_MONTH) + "-"
				+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)
				+ ":" + c.get(Calendar.SECOND) + "] " + path + "-" + text);
	}

 public static void printlnLog(String string, String string0) {
        printLog(string, string0);
    }


}
