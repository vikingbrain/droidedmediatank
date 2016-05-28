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

/**
 * Utiliity class for http requests.
 * 
 * @author Rafael Iñigo
 */
public class UtilityHttp {

	/**
	 * It encodes special symbols in their valid representations
	 * for http requests.
	 * @param str the text to encode
	 * @return the text encoded for http
	 */
	public static String encodeHttpParameter(String str){
		
		//It translate the empty space
		String encoded = str.replaceAll(" ", "%20");
		//It translate the & symbol		
		encoded = encoded.replaceAll("&", "%26");

//Not required		
//		encoded = encoded.replaceAll("$", "%24");
//		encoded = encoded.replaceAll("`", "%60");
//		encoded = encoded.replaceAll(":", "%3A");
//		encoded = encoded.replaceAll("<", "%3C");
//		encoded = encoded.replaceAll(">", "%3E");
//		encoded = encoded.replaceAll("[", "%5B");
//		encoded = encoded.replaceAll("]", "%5D");
//		encoded = encoded.replaceAll("{", "%7B");
//		encoded = encoded.replaceAll("}", "%7D");
		
		return encoded;
	}
}
