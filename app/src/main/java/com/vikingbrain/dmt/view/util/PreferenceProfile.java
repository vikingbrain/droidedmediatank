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
package com.vikingbrain.dmt.view.util;

import android.content.Context;
import android.preference.Preference;

/**
 * Wrapper class for Preference to be able to do nothing
 * but hold the id.
 * 
 * @author Rafael Iñigo
 */
public class PreferenceProfile extends Preference {
	
	private Long id;
	
	public PreferenceProfile(Context context, Long id) { 
		super(context);
		this.id = id;
	}
	
	public void click() {
		//do nothing
	}
	
	public Long getId() {
		return id;
	}
	
}
