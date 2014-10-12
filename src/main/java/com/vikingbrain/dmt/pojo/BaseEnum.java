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

import java.util.List;

/**
 * Interface for all enums in the app.
 * 
 * @author Rafael Iñigo
 */
public interface BaseEnum {

	/**
	 * Get the id.
	 * @return id the id
	 */
	int getId();
	
	/**
	 * Get the android resource id for the description.
	 * @return the id for android resource description
	 */
	int getIdDescripcion();
	
	/**
	 * Get the list of types.
	 * @param <T> the type of enum.
	 * @return the List<T> the list of enums
	 */
	<T extends BaseEnum> List<T> getList();				
	
}
