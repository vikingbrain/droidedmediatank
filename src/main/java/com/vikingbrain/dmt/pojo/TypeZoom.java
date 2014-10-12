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
package com.vikingbrain.dmt.pojo;

import java.util.ArrayList;
import java.util.List;

import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;

import com.vikingbrain.dmt.R;

/**
 * Enumeration with zoom types.
 * 
 * @author Rafael Iñigo
 */
public enum TypeZoom implements BaseEnum {

	FAR {
		public int getId() {
			return Constants.NUMBER_ONE;
		}
		public int getIdDescripcion() {
			return R.string.pref_zoom_far;
		}
		public ZoomDensity getZoomDensity() {
			return WebSettings.ZoomDensity.FAR;
		}
	},

	MEDIUM {
		public int getId() {
			return Constants.NUMBER_TWO;
		}
		public int getIdDescripcion() {
			return R.string.pref_zoom_medium;
		}
		public ZoomDensity getZoomDensity() {
			return WebSettings.ZoomDensity.MEDIUM;
		}		
	},

	CLOSE {
		public int getId() {
			return Constants.NUMBER_THREE;
		}
		public int getIdDescripcion() {
			return R.string.pref_zoom_close;
		}
		public ZoomDensity getZoomDensity() {
			return WebSettings.ZoomDensity.CLOSE;
		}		
	};

	/**
	 * Returns the id.
	 * @return id the id
	 */
	public abstract int getId();
	
	/**
	 * Returns the resource id for the description.
	 * @return the id for resource description
	 */
	public abstract int getIdDescripcion();

	/**
	 * Returns the Android zoom density.
	 * @return the Android zoom density
	 */
	public abstract ZoomDensity getZoomDensity();

	/**
	 * Find an instance by the id.
	 * @param _id the id to find
	 * @return the type
	 */
	public static TypeZoom findById(int _id) {
		TypeZoom type = null;
	    for (TypeZoom actual : TypeZoom.values()) {
        	if(actual.getId() == _id){
        		type = actual;
        	}
	    }
		return type;
	}	
	
	/**
	 * Find an instance by the resource id description.
	 * @param _idDescription the resource id
	 * @return the type
	 */
	public static TypeZoom findByIdDescripcion(int _idDescription) {
		TypeZoom type = null;
	    for (TypeZoom actual : TypeZoom.values()) {
        	if(actual.getIdDescripcion() == _idDescription){
        		type = actual;
        	}
	    }
		return type;	
	}
	
	/**
	 * Get the list of types.
	 * @return the list of types
	 */
	public List<BaseEnum> getList(){
		List<BaseEnum> list = new ArrayList<BaseEnum>();
		for (TypeZoom actual : TypeZoom.values()) {
			list.add(actual);
		}
		return list;
	}

}