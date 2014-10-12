package com.vikingbrain.dmt.oi.filemanager;

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

/**
 * Dec 7, 2008: Peli: Use inflated layout.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.view.util.AbstractIconifiedView;

public class IconifiedTextView extends AbstractIconifiedView {

	public IconifiedTextView(Context context) {
		super(context);

		// inflate rating
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.filelist_item, this, true);

		setIcon((ImageView) findViewById(R.id.icon));
		setText((TextView) findViewById(R.id.text));
		setInfo((TextView) findViewById(R.id.info));
	}

}
