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

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.pojo.util.UtilityFile;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.util.UtilProfile;
import com.vikingbrain.dmt.view.asynctaks.TaskNmtDeleteAudioItemFromQueue;
import com.vikingbrain.dmt.view.asynctaks.TaskNmtGetListAodQueueInfo;
import com.vikingbrain.dmt.view.util.IconifiedMusic;
import com.vikingbrain.dmt.view.util.IconifiedMusicListAdapter;
import com.vikingbrain.nmt.responses.playback.ObjectQueueElement;

/**
 * Activity to show playlist of currently playing files on NMT.
 * 
 * @author Rafael Iñigo
 */
public class PlaylistActivity extends BaseActivity implements DisplayFragment.OnCurrentlyAudioPlayingDetectedListener,
			TaskNmtGetListAodQueueInfo.OnResultTaskNmtGetListAodQueueInfo,
			TaskNmtDeleteAudioItemFromQueue.OnResultTaskNmtDeleteItemFromQueue {

	/** Tag for logging. */
	private static final String TAG = PlaylistActivity.class.getSimpleName();
	
	private DavidBoxService davidBoxService;
	private SettingsService settingsService;
	
	private TextView mEmptyText;
	private ProgressBar mProgressBar;
	private ListView list;
	private List<IconifiedMusic> iml =  new ArrayList<IconifiedMusic>();

	//Used when removing item from playlist
    private boolean fileDeleted = false;
    private int positionAtDelete;

//    private ActionMode mMode;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	    
	    	    
	    setContentView(R.layout.activity_playlist);
	    	    
	    davidBoxService = DMTApplication.getDavidBoxService();
	    settingsService = DMTApplication.getSettingsService();
	    	    
	    list = (ListView)findViewById(R.id.listMusic);
		
		mProgressBar = (ProgressBar) findViewById(R.id.scan_progress);
		mEmptyText = (TextView) findViewById(R.id.empty_text);
		
		getPlaylistContent();
		
		list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {

				IconifiedMusic musicItem = iml.get(position);
					
				boolean isDavidBoxSupported = UtilProfile.isDavidBoxSupported(settingsService.getActiveProfile());
				
				//Only if it is not already played or playing
				if (! musicItem.isPlayed()
					&& !musicItem.isPlaying()){
					
//		            mMode = startActionMode(new ActionModeMusicPlaylistFile(musicItem, isDavidBoxSupported, position));
					startActionMode(new ActionModeMusicPlaylistFile(musicItem, isDavidBoxSupported, position));
				}
			}
    		
		});
				
	}

	/**
	 * Show progress bar and execute the task to get the 
	 * list of audio files in the playlist. 
	 */
	private void getPlaylistContent(){
		//show the progress icon
		mProgressBar.setVisibility(View.VISIBLE);
		//get the content for the playlist
		new TaskNmtGetListAodQueueInfo(this, this, davidBoxService).execute();
	}

	@Override
	public void onResultTaskNmtGetListAodQueueInfo(List<ObjectQueueElement> queue) {
		refreshPlaylistContent(queue);		
	}

	/**
	 * Refresh de view with the queue content provided as parameter.
	 * @param queue the queue of elements
	 */
	private void refreshPlaylistContent(List<ObjectQueueElement> queue){
		mProgressBar.setVisibility(View.GONE);
		
		if (null == queue
				|| queue.isEmpty()){
			mEmptyText.setVisibility(View.VISIBLE);
		} else {
			iml = adapt(queue);		
			IconifiedMusicListAdapter imla = new IconifiedMusicListAdapter(this);
			imla.setListItems(iml, list.hasTextFilter());			
			list.setAdapter(imla);
			list.setTextFilterEnabled(true);
		}		
		
		//Go to the delete position if the list is refreshing after deleting element
		if(fileDeleted){
			list.setSelection(positionAtDelete);
		}

	}
	
	private List<IconifiedMusic> adapt(List<ObjectQueueElement> queue){
		List<IconifiedMusic> iml = new ArrayList<IconifiedMusic>();
		for (ObjectQueueElement element: queue){
			
			String fullPath = element.getFullPath();
			
		    int separatorIndex = fullPath.lastIndexOf(DMTFile.separator);
		    
		    String title = "";
		    if (separatorIndex < 0){
		    	title = fullPath;
		    } else {
		    	title = fullPath.substring(separatorIndex + 1, fullPath.length());
		    }		    			   
			
		    Drawable image = getResources().getDrawable(R.drawable.icon_music_file_nmt);
			iml.add(new IconifiedMusic(title, fullPath, element.getIndex(), image, IconifiedMusic.STATUS.LOADING));							
		}
		return iml;
		
	}

	@Override
	/**
	 * It has the playlist, and now it knows the fullpath of current audio that is playing
	 * so it changes the background in the matching music element in the list and it
	 * scrolls the list to the right position (scroll only for >= froyo 2.2
	 */
	public void onCurrentlyAudioPlayingDetected(String fullPath) {
		
		int oldPlayingPosition = getPositionLastTimeMarkedPlaying();
		int newPlayingPosition = getPositionPlaylistOfCurrentlyPlaying(fullPath);
		
		for (int i = 0; i < iml.size(); i++) { 
			
			IconifiedMusic im = iml.get(i);
			
			if (i < newPlayingPosition){
				im.setStatus(IconifiedMusic.STATUS.PLAYED);
				DmtLogger.d(TAG, "Played. Position: " + i);
				
			} else if (i == newPlayingPosition){
				im.setStatus(IconifiedMusic.STATUS.PLAYING);
				DmtLogger.d(TAG, "Playing. Position: " + i);
				
			} else if (i > newPlayingPosition){
				im.setStatus(IconifiedMusic.STATUS.NONE);
				DmtLogger.d(TAG, "Not played, not playing. Position: " + i);
			}
		}
		
		DmtLogger.d(TAG, "Notify data changed");
		notifyIPlaylistChanges(oldPlayingPosition, newPlayingPosition);
	}
	
	private int getPositionLastTimeMarkedPlaying(){
		int positionMarkedPlaying = 0; //Init the var just in case
		for (int i = 0; i < iml.size(); i++) { 
			IconifiedMusic im = iml.get(i);
			if (im.isPlaying()){
				positionMarkedPlaying = i;
			}
		}
		return positionMarkedPlaying;
	}
	
	private int getPositionPlaylistOfCurrentlyPlaying(String fullPath){
		int playingPosition = 0; //Init the var just in case
		for (int i = 0; i < iml.size(); i++) { 
			
			IconifiedMusic im = iml.get(i);
			
			//compare the absolute paths of files
			//fullPath is like "/opt/..."
			//im.getFullPath() is like "file:///opt/..."			
			if (isMusicFileCurrentlyPlaying(fullPath, im.getFullPath())){
				playingPosition = i;
				DmtLogger.d(TAG, "Item in playlist is currently playing. Position: " + i);
			}
		}
		return playingPosition;
	}

	/**
	 * Put the data in the adapter. Do not move the playlist to the current playing position
	 * every time I get info for the currently playing. It's annoying getting the list moving every 5 seconds.
	 * @param oldPlayingPosition the old playing position
	 * @param newPlayingPosition the new playing position
	 */
	private void notifyIPlaylistChanges(int oldPlayingPosition, int newPlayingPosition) {
		if (list.getAdapter() != null) {
			//Set the items with the modifications in the adapter
			((IconifiedMusicListAdapter)list.getAdapter()).setListItems(iml, list.hasTextFilter());
			((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
			
			//Move the list to the position of the new currently playing item
			//It's useful in order to not move the playlist every time I get information about the currently playing item
			if (oldPlayingPosition != newPlayingPosition){
				//Move the list to the position
				list.setSelection(newPlayingPosition);
			}
		}
	}

	private boolean isMusicFileCurrentlyPlaying(String fullPathCurrentlyPlaying, String itemFullPath){
		boolean isPlaying = false;
		if (UtilityFile.isAbsolutePathsEqual(fullPathCurrentlyPlaying, itemFullPath)){
			isPlaying = true;
			DmtLogger.d(TAG, "Currently playing full path found in playlist");
		}
		return isPlaying;
	}

    private final class ActionModeMusicPlaylistFile implements ActionMode.Callback {

    	final IconifiedMusic iconifiedMusic;
    	final boolean davidboxSupported;
		final int position;
		
		public ActionModeMusicPlaylistFile(IconifiedMusic iconifiedMusic, boolean davidboxSupported, int position) {
			super();
			this.iconifiedMusic = iconifiedMusic;
			this.davidboxSupported = davidboxSupported;
			this.position = position;
		}

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {        	
            // Inflate the menu.
            getSupportMenuInflater().inflate(R.menu.playlist_file_mode, menu);                         		
            return true;
        }

		@Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			if (davidboxSupported){
   			 	menu.findItem(R.id.menu_playlist_remove_item).setVisible(true);   			 	
			}
    		return true;
		}

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	                      
    		switch (item.getItemId()) {
    	    case R.id.menu_playlist_remove_item:
				positionAtDelete = position;
				executeDeleteItemFromQueue(iconifiedMusic.getIndex());										

    	    	mode.finish();
				break;
    		}
            
            return true;
        }
		
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
			
    }    
    
    /**
     * Calls the task to delete a item from the queue.
     * @param index the index to remove
     */
	private void executeDeleteItemFromQueue(String index){
		new TaskNmtDeleteAudioItemFromQueue(this, this, davidBoxService).execute(index);
	}

	@Override
	public void onResultTaskNmtDeleteItemFromQueue() {
		getPlaylistContent();
	}
	
}
