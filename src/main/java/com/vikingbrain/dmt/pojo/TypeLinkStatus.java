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

import com.vikingbrain.dmt.R;

/**
 * Enumeration with link status types.
 * 
 * @author Rafael Iñigo
 */
public enum TypeLinkStatus implements BaseEnum {

	/**
	 * Link can be shown.
	 */
	SHOW {
		public int getId() {
			return Constants.NUMBER_ZERO;
		}
		public int getIdDescripcion() {
			return R.string.show;
		}
	},

	/**
	 * Link must be hidden.
	 */
	HIDE {
		public int getId() {
			return Constants.NUMBER_ONE;
		}
		public int getIdDescripcion() {
			return R.string.hide;
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
	 * Find an instance by the id.
	 * @param _id the id to find
	 * @return the type
	 */
	public static TypeLinkStatus findById(int _id) {
		TypeLinkStatus type = null;
	    for (TypeLinkStatus actual : TypeLinkStatus.values()) {
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
	public static TypeLinkStatus findByIdDescripcion(int _idDescription) {
		TypeLinkStatus type = null;
	    for (TypeLinkStatus actual : TypeLinkStatus.values()) {
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
		for (TypeLinkStatus actual : TypeLinkStatus.values()) {
			list.add(actual);
		}
		return list;
	}

}
