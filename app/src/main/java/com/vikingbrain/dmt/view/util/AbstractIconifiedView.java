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

import com.vikingbrain.dmt.oi.filemanager.ThumbnailLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Abstract class for all iconified views. 
 * 
 * @author Rafael Iñigo
 */
public class AbstractIconifiedView extends LinearLayout {

	private TextView text;
	private TextView info;
	private ImageView icon;

	public AbstractIconifiedView(Context context) {
		super(context);
	}

	public void setText(String words) {
		text.setText(words);

		int height = getHeight();

		if (height > 0) {
			ThumbnailLoader.setThumbnailHeight(height);
		}
	}	
	
	public void setText(TextView text) {
		this.text = text;
	}

	public void setInfo(TextView info) {
		this.info = info;
	}

	public void setIcon(ImageView icon) {
		this.icon = icon;
	}
	
	/**
	 * Set text to info TextView.
	 * @param infoText the text
	 */
	public void setInfo(String infoText) {
		info.setText(infoText);
	}

	public void setIcon(Drawable bullet) {
		icon.setImageDrawable(bullet);
	}

	public void setTextColor(int color) {
		text.setTextColor(color);
	}

	public void setInfoColor(int color) {
		info.setTextColor(color);
	}
}
