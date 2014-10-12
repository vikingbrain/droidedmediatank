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

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.service.util.UtilProfile;

/**
 * Custom navigation list adapter for profiles.
 * 
 * @author Rafael Iñigo
 */
public class NavigationListProfilesAdapter extends BaseAdapter implements SpinnerAdapter {

	Context mContext;
	private LayoutInflater m_layoutInflater;
	private List<Profile> profiles;
     
    public NavigationListProfilesAdapter(Context p_context, List<Profile> profiles) {
    	this.mContext = p_context;
        this.m_layoutInflater = LayoutInflater.from(p_context);
        this.profiles = profiles;
    }
    
	@Override
	public int getCount() {
		return profiles.size();
	}

	@Override
	public Object getItem(int position) {
		return profiles.get(position);
	}

	@Override
	public long getItemId(int position) {		
		long id = 0;
		Profile profile = profiles.get(position);
		if (null != profile){
			id = profile.getId();
		}
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
	        convertView = m_layoutInflater.inflate(
	                R.layout.sherlock_spinner_dropdown_item, parent, false);
	    }
		Profile profilePosition = profiles.get(position);
	    ((TextView) convertView.findViewById(android.R.id.text1))
	    		.setText(UtilProfile.getAlias(profilePosition, mContext));		    
	    return convertView;		
	}
	
}
