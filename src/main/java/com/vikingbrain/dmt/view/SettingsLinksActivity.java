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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Link;
import com.vikingbrain.dmt.pojo.TypeLink;
import com.vikingbrain.dmt.pojo.TypeLinkStatus;
import com.vikingbrain.dmt.pojo.TypeWebClient;
import com.vikingbrain.dmt.service.LinkService;
import com.vikingbrain.dmt.view.util.UtilityView;

/**
 * Activity to show list of links.
 * 
 * @author Rafael Iñigo
 *
 */
public class SettingsLinksActivity extends SherlockListActivity {

	EfficientAdapter ef;
	private LinkService linkService;
	List<Link> linkList;	
	
	ActionMode mMode;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	//Emulate back button
	    	onBackPressed();
	        return true;	    	    
	    case R.id.menu_new:
	      	Intent i = new Intent(this, SettingsLinkDetailActivity.class);     
	      	startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getSupportMenuInflater().inflate(R.menu.settings_manage_web_links, menu);                
 		return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);              

	    //ActionBarSherlock
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);		

        //load link service
    	linkService = DMTApplication.getLinkService();
    	//Fill the list of links from data database
    	fillUpListOfLinks();
    	
        ef = new EfficientAdapter(this);                   
        setListAdapter(ef);

        //Listener for the row click
        getListView().setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {

				Link link = linkList.get(position);

	            mMode = startActionMode(new ActionModeLinkSelected(link));
			}
    		
		});

    }

    public class ViewHolder {
    	ImageView icon;
        TextView text;
        TextView info;
    }

    /** Rellena la lista de enlaces de BBDD. */
    public void fillUpListOfLinks(){
    	linkList = linkService.getAll(); 
    }
    
    private class EfficientAdapter extends BaseAdapter {
    	
        private LayoutInflater mInflater;        
        private Context mContext;
        
        public EfficientAdapter(Context context) {
        	mContext = context;        	            
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount() {
        	return linkList.size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position) {
            return position;
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position) {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
            	convertView = mInflater.inflate(R.layout.filelist_item, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.info = (TextView) convertView.findViewById(R.id.info);
                                
                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            Link enlace = linkList.get(position);
            
            //Image
            setLinkImage(mContext, holder.icon, enlace);
            //Tile of the web client
            UtilityView.setTextToLink(enlace, holder.text);                        
            //Info. "show" vs "hide" (visibility on grid panel)
            putInformationGridVisibility(enlace, holder.info);                        

            return convertView;
        }
    }
    
    private void setLinkImage(Context mContext, ImageView icon, Link enlace){
    	
    	int idImagenResource = UtilityView.getIdImage(enlace);
    	
        Bitmap mIcon = BitmapFactory.decodeResource(mContext.getResources(), idImagenResource);
        icon.setImageBitmap(mIcon);
    }
    

    private void putInformationGridVisibility(Link link, TextView info){
    	TypeLinkStatus typeLinkStatus = link.getTypeLinkStatus();
    	
        int colorLinkStatus = (TypeLinkStatus.SHOW == typeLinkStatus) ? getResources().getColor(R.color.lightGreen) : 
			getResources().getColor(R.color.lightRed);
        info.setTextColor(colorLinkStatus);
        
    	//"show" vs "hide" text
    	info.setText(typeLinkStatus.getIdDescripcion());    	
    }

	@Override
	protected void onRestart() {
		super.onRestart();				
		//Fill up the list of links from database
		fillUpListOfLinks();
		//Make the screen to show the new data obtained
		ef.notifyDataSetChanged();
	}
	
	
    private final class ActionModeLinkSelected implements ActionMode.Callback {

    	final Link link;
		
		public ActionModeLinkSelected(Link link) {
			super();
			this.link = link;
		}

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {        	
            // Inflate the menu.
            getSupportMenuInflater().inflate(R.menu.settings_manage_web_links_link_selected, menu);                         		
            return true;
        }

		@Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			//Put name of the link in the title
			mode.setTitle(getLinkName());
						
			//Show delete operation ONLY for custom links
			if (TypeLink.ORIGINAL != link.getTypeLink()){
   			 	menu.findItem(R.id.menu_delete).setVisible(true);   			 	
    		}

    		return true;
		}

		private String getLinkName(){
			String displayableName = "";
			
			String appName = link.getName();			
			if (TypeLink.ORIGINAL == link.getTypeLink()){
	        	TypeWebClient typeWebClient = TypeWebClient.findByCodename(appName);
	        	int idTextResource = typeWebClient.getTitle();
	        	displayableName = getString(idTextResource);				
			} else {
				//Custom user link
				displayableName = appName;
			}			
			return displayableName;
		}
		
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	                      
    		switch (item.getItemId()) {
    	    case R.id.menu_edit:

    	    	//Go to edit profile settings screen;
				Intent intentEditLink= new Intent(getApplicationContext(), SettingsLinkDetailActivity.class);
				intentEditLink.putExtra("id", link.getId());
		        startActivity (intentEditLink);    	    	
    	    	
    	    	mode.finish();
				break;
								
		    case R.id.menu_delete:
	
		    	//Delete the profile and refresh the list of profiles
		    	linkService.delete(link.getId());

		    	//Finish the action mode
		    	mode.finish();
		    	
		    	//Restart the activity, reload list
		    	onRestart();
		    	
				break;
												
		    case R.id.menu_visibility:
		    	
            	if (TypeLinkStatus.SHOW == link.getTypeLinkStatus()){
            		link.setTypeLinkStatus(TypeLinkStatus.HIDE);
            	} else {
            		link.setTypeLinkStatus(TypeLinkStatus.SHOW);
            	}
            	linkService.saveOrUpdate(link);

		    	mode.finish();
		    	
		    	//Restart the activity, reload list
		    	onRestart();
		    	
				break;
		    	
			}
            
            return true;
        }
		
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
			
    }
}
