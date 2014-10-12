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

/* $Id: BulletedTextView.java 57 2007-11-21 18:31:52Z steven $ 
 * 
 * Copyright 2007 Steven Osborn 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.oi.filemanager.ThumbnailLoader;

public class IconifiedServiceView extends LinearLayout {

	private TextView text;
	private TextView info;
	private Button buttonChangeStatus;
	private Button buttonRefreshStatus;

	public IconifiedServiceView(Context context) {
		super(context);

		// inflate rating
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.row_service, this, true);

		text = (TextView) findViewById(R.id.text);
		info = (TextView) findViewById(R.id.info);
		buttonChangeStatus = (Button) findViewById(R.id.button_change_status);
		buttonRefreshStatus = (Button) findViewById(R.id.button_refresh_status);
	}

	public void setText(String words) {
		text.setText(words);

		int height = getHeight();

		if (height > 0) {
			ThumbnailLoader.setThumbnailHeight(height);
		}
	}

	public void setInfo(String infoText) {
		info.setText(infoText);
	}

	public void setTextColor(int color) {
		text.setTextColor(color);
	}

	public void setInfoColor(int color) {
		info.setTextColor(color);
	}

	public void setButtonChangeStatusText(String text) {
		buttonChangeStatus.setText(text);
	}

	public Button getButtonChangeStatus() {
		return buttonChangeStatus;
	}

	public void setButtonChangeStatusVisibility(int visibility) {
		buttonChangeStatus.setVisibility(visibility);
	}

	public void setButtonRefreshStatusText(String text) {
		buttonRefreshStatus.setText(text);
	}

	public Button getButtonRefreshStatus() {
		return buttonRefreshStatus;
	}

	public void setButtonRefreshStatusVisibility(int visibility) {
		buttonRefreshStatus.setVisibility(visibility);
	}

}
