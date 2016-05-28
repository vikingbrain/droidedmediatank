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

import android.preference.Preference;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.vikingbrain.dmt.view.util.PreferenceCheckBox;
import com.vikingbrain.dmt.view.util.PreferenceCustomLink;
import com.vikingbrain.dmt.view.util.PreferenceEditText;
import com.vikingbrain.dmt.view.util.PreferenceList;
import com.vikingbrain.dmt.view.util.PreferenceProfile;

/**
 * Abstract class for the settings activities.
 * 
 * @author Rafael Iñigo
 */
public abstract class AbstractSettingsActivity extends SherlockPreferenceActivity {

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	
    	// Perform click action, which always is a Preference
    	Preference item = (Preference) getListAdapter().getItem(position);
    	
		// Let the Preference open the right dialog
		if (item instanceof PreferenceList) {
			((PreferenceList)item).click();
		} else if (item instanceof PreferenceCheckBox) {
    		((PreferenceCheckBox)item).click();
		} else if (item instanceof PreferenceEditText) {
    		((PreferenceEditText)item).click();
		} else if (item instanceof PreferenceCustomLink) { 
    		((PreferenceCustomLink)item).click();
		} else if (item instanceof PreferenceProfile) { 
    		((PreferenceProfile)item).click();
		}

    }

}
