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
package com.vikingbrain.dmt.service.impl;

import java.util.List;

import com.vikingbrain.dmt.dao.ConfigDAO;
import com.vikingbrain.dmt.dao.ProfileDAO;
import com.vikingbrain.dmt.pojo.Config;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.SettingsService;

/**
 * Service operation for settings.
 * 
 * @author Rafael Iñigo
 */
public class SettingsServiceImpl implements SettingsService {

	/** The tag to use in logging. */
	private static final String TAG = SettingsServiceImpl.class.getSimpleName();

	/** DAO's. */
	private ConfigDAO configDAO;
	private ProfileDAO profileDAO;
	
	/**
	 * Constructor.
	 * @param configDAO DAO for configuration
	 * @param profileDAO DAO for profiles
	 */
	public SettingsServiceImpl(ConfigDAO configDAO, ProfileDAO profileDAO) {
		super();
		this.configDAO = configDAO;
		this.profileDAO = profileDAO;
	}

	/** {@inheritDoc} */
	public Config getConfig() {	    
		Config nmtConfig = configDAO.getConfig();		 
		DmtLogger.d(TAG, "Getting config");	    
	    return nmtConfig;
	}

	/** {@inheritDoc} */
	public int update(Config nmtConfig){
		return configDAO.update(nmtConfig);
	}

	/** {@inheritDoc} */
	public boolean isEulaAccepted() {
		Config nmtConfig = getConfig();
		Boolean accepted = false;
		if (nmtConfig.isEulaAccepted()){
			accepted = true;
		}
		return accepted;
	}

	/** {@inheritDoc} */
	public int acceptEula(){
		Config nmtConfig = getConfig();
		nmtConfig.setEula(Constants.TRUE_NUMBER);
		return configDAO.update(nmtConfig);
	}	

	/** {@inheritDoc} */
	public boolean isShowConsole(){
		Config nmtConfig = getConfig();
		return nmtConfig.isShowConsole();
	}

	/** {@inheritDoc} */
	public boolean isShowNmj(){
		Config nmtConfig = getConfig();
		return nmtConfig.isShowNmj();
	}

	/** {@inheritDoc} */
	public int updateShowConsole(boolean value){
		Config nmtConfig = getConfig();
		if (value) {
			nmtConfig.setShowConsole(Constants.TRUE_NUMBER);
		} else {
			nmtConfig.setShowConsole(Constants.FALSE_NUMBER);
		}
		return configDAO.update(nmtConfig);
	}

	/** {@inheritDoc} */
	public int updateShowNmjExcludes(boolean value){
		Config nmtConfig = getConfig();
		if (value) {
			nmtConfig.setShowNmj(Constants.TRUE_NUMBER);
		} else {
			nmtConfig.setShowNmj(Constants.FALSE_NUMBER);
		}
		return configDAO.update(nmtConfig);
	}
	
	/** {@inheritDoc} */
	public Profile getProfile(Long id){
		Profile profile = profileDAO.getById(id);
		return profile;
	}
	
	/** {@inheritDoc} */
	public List<Profile> getProfiles(){
		List<Profile> profiles = profileDAO.getAll();
		return profiles;
	}

	/** {@inheritDoc} */
	public long saveOrUpdate(Profile profile){
		if (null == profile.getId()){
			return profileDAO.insert(profile);
		} else {
			return profileDAO.update(profile);
		}
	}

	/**
	 * It gets the first profile in the list of profiles.
	 * @return first profile from list of profiles
	 */
	private Profile getFirstProfileAvailable(){
		Profile defaultProfile = null;
		List<Profile> profiles = getProfiles();
		if (null != profiles
				&& !profiles.isEmpty()){
			defaultProfile = profiles.get(Constants.NUMBER_ZERO);
		}
		return defaultProfile;
	}
	
	/** {@inheritDoc} */
	public Profile getActiveProfile(){
		Config config = getConfig();
		Long idActiveProfile = config.getActiveProfile();
		Profile activeProfile = profileDAO.getById(idActiveProfile);
		if (null == activeProfile){
			activeProfile = getFirstProfileAvailable();

			//Initialize the config for this profile to be active
			changeActiveProfile(activeProfile.getId());			
		}
		return activeProfile;
	}

	/** {@inheritDoc} */
	public int changeActiveProfile(long idActiveProfile){
		Config config = getConfig();
		config.setActiveProfile(idActiveProfile);
		return configDAO.update(config);		
	}	
	
	/** {@inheritDoc} */
    public int deleteProfile(long id) {
    	return profileDAO.delete(id);    	
    }
	
}