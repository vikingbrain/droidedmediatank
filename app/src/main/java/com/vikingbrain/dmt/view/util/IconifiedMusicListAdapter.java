/* $Id: BulletedTextListAdapter.java 57 2007-11-21 18:31:52Z steven $ 
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
package com.vikingbrain.dmt.view.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.pojo.util.DmtLogger;

/** @author Steven Osborn - http://steven.bitsetters.com */ 
public class IconifiedMusicListAdapter extends BaseAdapter implements Filterable { 

	private static final String TAG = IconifiedMusicListAdapter.class
			.getSimpleName();

	/** Remember our context so we can use it when constructing views. */
	private Context mContext;

	private static String lastFilter;

	class IconifiedFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence arg0) {

			lastFilter = (arg0 != null) ? arg0.toString() : null;

			Filter.FilterResults results = new Filter.FilterResults();

			// No results yet?
			if (mOriginalItems == null) {
				results.count = 0;
				results.values = null;
				return results;
			}

			int count = mOriginalItems.size();

			if (arg0 == null || arg0.length() == 0) {
				results.count = count;
				results.values = mOriginalItems;
				return results;
			}

			List<IconifiedMusic> filteredItems = new ArrayList<IconifiedMusic>(
					count);

			int outCount = 0;
			CharSequence lowerCs = arg0.toString().toLowerCase();

			for (int x = 0; x < count; x++) {
				IconifiedMusic text = mOriginalItems.get(x);

				if (text.getTitle().toLowerCase().contains(lowerCs)) {
					// This one matches.
					filteredItems.add(text);
					outCount++;
				}
			}

			results.count = outCount;
			results.values = filteredItems;
			return results;
		}

		@Override
		protected void publishResults(CharSequence arg0, FilterResults arg1) {
			mItems = (List<IconifiedMusic>) arg1.values;
			notifyDataSetChanged();
		}

		List<IconifiedMusic> synchronousFilter(CharSequence filter) {
			FilterResults results = performFiltering(filter);
			return (List<IconifiedMusic>) (results.values);
		}
	}

	private IconifiedFilter mFilter = new IconifiedFilter();

	private List<IconifiedMusic> mItems = new ArrayList<IconifiedMusic>();
	private List<IconifiedMusic> mOriginalItems = new ArrayList<IconifiedMusic>();

	public IconifiedMusicListAdapter(Context context) {
		mContext = context;
	}

	public void addItem(IconifiedMusic it) {
		mItems.add(it);
	}

	public void setListItems(List<IconifiedMusic> lit, boolean filter) {
		mOriginalItems = lit;

		if (filter) {
			mItems = mFilter.synchronousFilter(lastFilter);
		} else {
			mItems = lit;
		}
	}

	/** @return The number of items in the */
	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int position) {
		return mItems.get(position);
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	/*
	 * public boolean isSelectable(int position) { try{ return
	 * mItems.get(position).isSelectable(); }catch (IndexOutOfBoundsException
	 * aioobe){ return super.isSelectable(position); } }
	 */

	/** Use the array index as a unique id. */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * @param convertView
	 *            The old view to overwrite, if one is passed
	 * @returns a IconifiedTextView that holds wraps around an IconifiedText
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		IconifiedMusicView btv;
		// if (convertView == null) {
		// btv = new IconifiedMusicView(mContext, mItems.get(position));
		// } else { // Reuse/Overwrite the View passed
		// // We are assuming(!) that it is castable!
		// btv = (IconifiedMusicView) convertView;
		// }

		// Solves the problem of ghost data becuse of recycling the view
		btv = new IconifiedMusicView(mContext);

		IconifiedMusic current = mItems.get(position);

		// title
		btv.setText(current.getTitle());
		// icon
		btv.setIcon(current.getIcon());

		int colorBlack = mContext.getResources().getColor(R.color.black);
		int colorSecondaryTextDark = mContext.getResources().getColor(
				android.R.color.secondary_text_dark);
		int colorRed = mContext.getResources().getColor(R.color.lightRed);
		int colorGreen = mContext.getResources().getColor(R.color.lightGreen);
		int colorBlue = mContext.getResources().getColor(R.color.lightBlue);
		
		DmtLogger.d(TAG,
				"Position: " + position + ". isPlaying: " + current.isPlaying()
						+ ". isPlayed: " + current.isPlayed() + ". title: "
						+ current.getTitle());

		if (current.isPlaying()) {
			btv.setInfo(mContext.getString(R.string.now_playing));
			// colors
			btv.setTextColor(colorBlack);
			btv.setInfoColor(colorGreen);
		} else if (current.isPlayed()) {
			btv.setInfo(mContext.getString(R.string.played));
			// colors
			btv.setTextColor(colorSecondaryTextDark);
			btv.setInfoColor(colorRed);
		} else if (current.isLoading()) {
			btv.setInfo(mContext.getString(R.string.title_loading));
			// colors
			btv.setTextColor(colorBlack);
			btv.setInfoColor(colorBlue);
		} else {
			// No text
			// colors
			btv.setTextColor(colorBlack);
			btv.setInfoColor(colorGreen);
		}

		// isSelected is still usable

		return btv;
	}

	public Filter getFilter() {
		return mFilter;
	}

}
