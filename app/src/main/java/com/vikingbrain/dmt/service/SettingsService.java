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
package com.vikingbrain.dmt.service;

import java.util.List;

import com.vikingbrain.dmt.pojo.Config;
import com.vikingbrain.dmt.pojo.Profile;

/**
 * Interface with service operation for settings.
 * 
 * @author Rafael Iñigo
 */
public interface SettingsService {

	/**
	 * Get the configuration.
	 * @return the configuration
	 */
	Config getConfig();
	
	/**
	 * Update configuration.
	 * @param config the configuration
	 * @return the row ID
	 */
	int update(Config config);

	/**
	 * Check if EULA was accepted.
	 * @return if accepted
	 */
	boolean isEulaAccepted();
	
	/**
	 * Accept EULA.
	 * @return the row ID
	 */
	int acceptEula();
	
	/**
	 * Check if user wants console to show.
	 * @return if true
	 */
	boolean isShowConsole();
	
	/**
	 * Check if user want to show NMJ excludes.
	 * @return if true
	 */
	boolean isShowNmj();
	
	/**
	 * Update value of show console
	 * @param value the new value
	 * @return the row ID
	 */
	int updateShowConsole(boolean value);
	
	/**
	 * Update show NMJ excludes status.
	 * @param value the new value
	 * @return the row ID
	 */
	int updateShowNmjExcludes(boolean value);
	
	/**
	 * Get profile by id
	 * @param id the id
	 * @return the profile
	 */
	Profile getProfile(Long id);

	/**
	 * List all profiles.
	 * @return a list of profiles
	 */
	List<Profile> getProfiles();	

	/**
	 * Save or update a profile
	 * @param profile the profile
	 * @return the row ID
	 */
	long saveOrUpdate(Profile profile);		
	
	/**
	 * Get the currect active profile from DDBB.
	 * @return the saved active profile
	 */
	Profile getActiveProfile();
	
	/**
	 * Change active profile.
	 * @param idActiveProfile the id of the new active profile
	 * @return the row ID
	 */
	int changeActiveProfile(long idActiveProfile);
	
	/**
	 * Delete profile by id.
	 * @param id the profile id
	 * @return the row ID
	 */
    int deleteProfile(long id);

}