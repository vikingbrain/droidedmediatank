/*
 * Copyright 2011-2014 Rafael Iñigo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vikingbrain.dmt.pojo.util;

import com.vikingbrain.dmt.pojo.Constants;

/**
 * Class for logging.
 * 
 * @author Rafael Iñigo
 *
 */
public class DmtLogger {

	public static void i(String tag, String string) {
		if (Constants.DEBUG_ENABLED) android.util.Log.i(tag, string);
	}
	public static void e(String tag, String string) {
	    if (Constants.DEBUG_ENABLED) android.util.Log.e(tag, string);
	}
	public static void e(String tag, String string, Throwable t) {
	    if (Constants.DEBUG_ENABLED) android.util.Log.e(tag, string, t);
	}
	public static void d(String tag, String string) {
	    if (Constants.DEBUG_ENABLED) android.util.Log.d(tag, string);
	}
	public static void v(String tag, String string) {
	    if (Constants.DEBUG_ENABLED) android.util.Log.v(tag, string);
	}
	public static void w(String tag, String string) {
	    if (Constants.DEBUG_ENABLED) android.util.Log.w(tag, string);
	}

}
