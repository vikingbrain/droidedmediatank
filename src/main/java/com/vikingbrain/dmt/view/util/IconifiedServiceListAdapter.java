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
import com.vikingbrain.dmt.pojo.TypeServiceStatus;

/** @author Steven Osborn - http://steven.bitsetters.com */ 
public class IconifiedServiceListAdapter extends BaseAdapter implements Filterable { 

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
			
			
			List<IconifiedService> filteredItems = new ArrayList<IconifiedService>(count);
			
			int outCount = 0;
			CharSequence lowerCs = arg0.toString().toLowerCase();
			
			
			for (int x=0; x<count; x++) {
				IconifiedService text = mOriginalItems.get(x);
				
				if (text.getName().toLowerCase().contains(lowerCs)) {
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
			mItems = (List<IconifiedService>) arg1.values;
			notifyDataSetChanged();
		}
    	
		List<IconifiedService> synchronousFilter(CharSequence filter) {
			FilterResults results = performFiltering(filter);
			return (List<IconifiedService>) (results.values);
		}
     }
     
     private IconifiedFilter mFilter = new IconifiedFilter();


     private List<IconifiedService> mItems = new ArrayList<IconifiedService>(); 
     private List<IconifiedService> mOriginalItems = new ArrayList<IconifiedService>(); 

     public IconifiedServiceListAdapter(Context context) { 
          mContext = context; 
     } 

     public void addItem(IconifiedService it) { mItems.add(it); } 

     public void setListItems(List<IconifiedService> lit, boolean filter) {
    	 mOriginalItems = lit;

    	 if (filter) {
    		 mItems = mFilter.synchronousFilter(lastFilter);
    	 } else {
    		 mItems = lit;
    	 }
     } 

     /** @return The number of items in the */ 
     public int getCount() { return mItems.size(); } 

     public Object getItem(int position) { return mItems.get(position); } 
     
public Object getItems() { return mItems; }

     public boolean areAllItemsSelectable() { return false; } 
/*
     public boolean isSelectable(int position) { 
          try{ 
               return mItems.get(position).isSelectable(); 
          }catch (IndexOutOfBoundsException aioobe){ 
               return super.isSelectable(position); 
          } 
     } 
     */

     /** Use the array index as a unique id. */ 
     public long getItemId(int position) { 
          return position; 
     } 

     /** @param convertView The old view to overwrite, if one is passed 
      * @returns a IconifiedTextView that holds wraps around an IconifiedText */ 
     public View getView(int position, View convertView, ViewGroup parent) { 
    	 IconifiedServiceView btv; 
         if (convertView == null) {  
        	 	btv = new IconifiedServiceView(mContext);
         } else { // Reuse/Overwrite the View passed 
              // We are assuming(!) that it is castable! 
              btv = (IconifiedServiceView) convertView; 
         } 
         
         IconifiedService current = mItems.get(position);
         
         //name
         btv.setText(current.getName());
         
         //status Adapt the enum status to the show in the view
         int infoColor = mContext.getResources().getColor(R.color.black);
         TypeServiceStatus status = current.getStatus();
         if (TypeServiceStatus.RUNNING.equals(status)){
        	 btv.setInfo(mContext.getString(R.string.running));        	 
        	 btv.setButtonChangeStatusText(mContext.getString(R.string.stop));
        	 btv.setButtonChangeStatusVisibility(View.VISIBLE);
        	 btv.setButtonRefreshStatusVisibility(View.VISIBLE);
        	 infoColor = mContext.getResources().getColor(R.color.lightGreen);
        	 
         } else if (TypeServiceStatus.STOPPED.equals(status)){
        	 btv.setInfo(mContext.getString(R.string.stopped));
        	 btv.setButtonChangeStatusText(mContext.getString(R.string.start));
        	 btv.setButtonChangeStatusVisibility(View.VISIBLE);
        	 btv.setButtonRefreshStatusVisibility(View.VISIBLE);
        	 infoColor = mContext.getResources().getColor(R.color.lightRed);
        	 
         } else if (TypeServiceStatus.LOADING.equals(status)){
        	 btv.setInfo(mContext.getString(R.string.title_loading));        	 
        	 btv.setButtonChangeStatusVisibility(View.GONE);
        	 btv.setButtonRefreshStatusVisibility(View.GONE);
        	 infoColor = mContext.getResources().getColor(R.color.lightBlue);
        	 
         } else if (TypeServiceStatus.SENDING_COMMAND.equals(status)){
        	 btv.setInfo(mContext.getString(R.string.service_sending_command));
        	 btv.setButtonChangeStatusVisibility(View.GONE);
        	 btv.setButtonRefreshStatusVisibility(View.VISIBLE);
        	 infoColor = mContext.getResources().getColor(R.color.lightBlue);
        	 
         } else if (TypeServiceStatus.UNKNOWN.equals(status)){
        	 btv.setInfo(mContext.getString(R.string.unknown));
        	 btv.setButtonChangeStatusVisibility(View.GONE);
        	 btv.setButtonRefreshStatusVisibility(View.VISIBLE);
        	 infoColor = mContext.getResources().getColor(R.color.lightRed);
         }
         
         //Text button refresh
         btv.setButtonRefreshStatusText(mContext.getString(R.string.refresh));
         
         //Set the tag so later I can manage the onclick from a method in the activity
         btv.getButtonChangeStatus().setTag(current);
         btv.getButtonRefreshStatus().setTag(current);
         
       	 int color = mContext.getResources().getColor(R.color.black);
     	 btv.setTextColor(color);
     	 
     	 //Color for info
 	     btv.setInfoColor(infoColor); 	
 	     
 	     //isSelected is still usable
 	     
         return btv; 
     }

	public Filter getFilter() {
		return mFilter;
	} 

}
