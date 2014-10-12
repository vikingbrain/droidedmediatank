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
package com.vikingbrain.dmt.view;

import java.util.List;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.view.util.NavigationListProfilesAdapter;

/**
 * Abstract class for the activity that changes profiles.
 * 
 * @author Rafael Iñigo
 */
public abstract class AbstractProfileChangerActivity extends BaseActivity
		implements ActionBar.OnNavigationListener {

	/** Service with the settings. */
	private SettingsService settingsService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settingsService = DMTApplication.getSettingsService();
	}

	/**
	 * Getter of property.
	 * @return the settings service
	 */
	public SettingsService getSettingsService() {
		return settingsService;
	}

	/**
	 * Setter of property
	 * @param settingsService the settings service
	 */
	public void setSettingsService(SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	/**
	 * Load the navigation with all the available profiles.
	 * @param profiles the profile list
	 */
	protected void loadProfileNavigation(List<Profile> profiles) {

		// Get active profile
		Profile activeProfile = settingsService.getActiveProfile();

		NavigationListProfilesAdapter navigationListApdater = new NavigationListProfilesAdapter(this, profiles);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		getSupportActionBar().setListNavigationCallbacks(navigationListApdater, this);

		// Position of active profile
		int positionCurrentAliasSelected = getPositionActivePofileInListOfProfiles(activeProfile, profiles);

		getSupportActionBar().setSelectedNavigationItem(positionCurrentAliasSelected);
	}

	/**
	 * Finds the position of the profile in the list of profiles.
	 * @param activeProfile the profile to search
	 * @param profiles the list of profiles
	 * @return position in the list
	 */
	private int getPositionActivePofileInListOfProfiles(Profile activeProfile, List<Profile> profiles) {
		int position = 0;
		for (int i = 0; i < profiles.size(); i++) {
			Profile profile = profiles.get(i);
			if (profile.getId().longValue() == activeProfile.getId().longValue()) {
				position = i;
			}
		}
		return position;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		getSettingsService().changeActiveProfile(itemId);
		return true;
	}

}
