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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.view.util.UtilityView;
import com.vikingbrain.nmt.responses.DavidBoxResponse;
import com.vikingbrain.nmt.responses.playback.ResponseGetCurrentAodInfo;
import com.vikingbrain.nmt.responses.playback.ResponseGetCurrentPodInfo;
import com.vikingbrain.nmt.responses.playback.ResponseGetCurrentVodInfo;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Android fragment with the display to show information about what it is currently playing.
 * 
 * @author Rafael Iñigo
 */
public class DisplayFragment extends Fragment {

	OnCurrentlyAudioPlayingDetectedListener mCallback;
	
	private static final String TAG = DisplayFragment.class.getSimpleName();
	
	private static final int MESSAGE_UPDATE_THREAD_NOW = 1;
	private static final int MESSAGE_NMT_UNAVAILABE = 2;
	
	private DavidBoxService davidBoxService;
	
	LinearLayout display;
	
	private Handler currentHandler;
	RefreshDisplayThread refreshDisplayThread;
	boolean startThread = true;
	
	private ProgressBar mProgressBarDisplay;
	
	TextView mTitle;
	LinearLayout mTimeLayout;
	TextView mCurrentTime;
	TextView mTotalTime;
	ProgressBar mProgressTime;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCurrentlyAudioPlayingDetectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCurrentlyAudioPlayingDetectedListener");
        }
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		davidBoxService = DMTApplication.getDavidBoxService();
		
		currentHandler = new Handler() {
			public void handleMessage(Message msg) {
				DisplayFragment.this.handleMessage(msg);
			}
		};

		refreshDisplayThread = new RefreshDisplayThread(currentHandler);

		if (startThread){
			// at this point the surface is created and
			// we can safely start the game loop
			refreshDisplayThread.setRunning(true);
			refreshDisplayThread.start();
			startThread = false;
		}
	}

	
    @Override
	public void onDestroy() {
		super.onDestroy();
		refreshDisplayThread.setRunning(false);
	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (container == null) {
//            // We have different layouts, and in one of them this
//            // fragment's containing frame doesn't exist.  The fragment
//            // may still be created from its saved state, but there is
//            // no reason to try to create its view hierarchy because it
//            // won't be displayed.  Note this is not needed -- we could
//            // just run the code below, where we would create and return
//            // the view hierarchy; it would just never be used.
//            return null;
        	DmtLogger.d(TAG, "container is null, it's ok");
        }

        View v = inflater.inflate(R.layout.display_fragment, container, false);
        
        mProgressBarDisplay = (ProgressBar) v.findViewById(R.id.scan_progress_display);
        
        mTitle = (TextView)v.findViewById(R.id.title);
        mTimeLayout = (LinearLayout)v.findViewById(R.id.timeLayout);
        mCurrentTime = (TextView)v.findViewById(R.id.currentTime);
        mProgressTime = (ProgressBar)v.findViewById(R.id.progressTime);
        mTotalTime = (TextView)v.findViewById(R.id.totalTime);

		//show the progress icon
        mProgressBarDisplay.setVisibility(View.VISIBLE);
        
        return v;        
    }

	private void refreshDisplay(String titleToDisplay, String currentTime, String totalTime){
		//change the title
		refreshDisplayTitle(titleToDisplay);
		//change the numbers and the progress
		refreshDisplayTimes(currentTime, totalTime);		
	}
	
	private void refreshDisplayTimes(String currentTime, String totalTime){
		//show the times and progress
		setVisibilityDisplayTime(true);
		
		try {
			int currentTimeNumber = Integer.valueOf(currentTime);
			int totalTimeNumber = Integer.valueOf(totalTime);

			//Let's format to XX:XX:XX
			String currentTimeText = UtilityView.formatSeconds(currentTimeNumber);
			String totalTimeText = UtilityView.formatSeconds(totalTimeNumber);
			
			mCurrentTime.setText(currentTimeText);
			mTotalTime.setText(totalTimeText);						
			
			//Calculate the progress bar
			int percentage = (currentTimeNumber * 100) / totalTimeNumber;
			mProgressTime.setProgress(percentage);
	    } catch (Exception e) {
	    	setVisibilityDisplayTime(false);	    	
		}
	}
	
	/**
	 * It refresh the title in the display if the title param
	 * is different form the title that is currently showing
	 * @param title the new title to show
	 */
	private void refreshDisplayTitle(String title){
		String currentTitle = (String)mTitle.getText();
		//If is the same title is gona be shown, tet it be,it's moving horizantaly so do not refresh
		if (!title.equals(currentTitle)){
			mTitle.setText(title);
		}
	}

	private void showOnlyMessageInDisplay(int idTitle){
		mTitle.setText(idTitle);
		setVisibilityDisplayTime(false);
	}

	private void setVisibilityDisplayTime(boolean visible){		
		if (visible == true){
			mTimeLayout.setVisibility(View.VISIBLE);
		} else {
			mTimeLayout.setVisibility(View.GONE);
		}
	}
	
    private void processNmtUnavailable(){
    	showOnlyMessageInDisplay(R.string.nmt_unavailable);
		refreshDisplayThread.setRunning(false); //No more refreshing until this Activity is onCreate again
    }

	private String getSimpleTitle(String titleOrFullPath){		
		String cleanTitle = titleOrFullPath.substring(titleOrFullPath.lastIndexOf("/") + 1, titleOrFullPath.length());
		return cleanTitle;
	}

    private void processNmtResponse(DavidBoxResponse davidBoxResponse){
    	
		if (davidBoxResponse.isValid()){
			
			String fullPath = "";
			String titleToDisplay = "";
			String currentTime = "";
			String totalTime = "";
			
			if (davidBoxResponse instanceof ResponseGetCurrentVodInfo){
				
				ResponseGetCurrentVodInfo current = (ResponseGetCurrentVodInfo)davidBoxResponse;
				
				fullPath = current.getFullPath();
				titleToDisplay = getSimpleTitle(current.getFullPath());
				currentTime = current.getCurrentTime();
				totalTime = current.getTotalTime();
	
			} else if (davidBoxResponse instanceof ResponseGetCurrentAodInfo){
				
				ResponseGetCurrentAodInfo current = (ResponseGetCurrentAodInfo)davidBoxResponse;
				
				fullPath = current.getFullPath();
				titleToDisplay = current.getTitle();
				currentTime = current.getCurrentTime();
				totalTime = current.getTotalTime();

			} else if (davidBoxResponse instanceof ResponseGetCurrentPodInfo){
				
				ResponseGetCurrentPodInfo current = (ResponseGetCurrentPodInfo)davidBoxResponse;
				
				titleToDisplay = current.getTitle();
				currentTime = "0";
				totalTime = "5";				
			}
			
			//refresh the display with the information
			refreshDisplay(titleToDisplay, currentTime, totalTime);
			
			//Send the information of the fullPath that is currently playing
			mCallback.onCurrentlyAudioPlayingDetected(fullPath);
			
		} else {
			//There is nothing playing			
			showOnlyMessageInDisplay(R.string.nmt_nothing_playing);
		}									
    }
    
    private void handleMessage(Message message) {
    	switch (message.what) {
    	case MESSAGE_UPDATE_THREAD_NOW:
    		//hide the progress icon
    		mProgressBarDisplay.setVisibility(View.GONE);    		
    		processNmtResponse((DavidBoxResponse)message.obj);
            break;        	
    	
    	case MESSAGE_NMT_UNAVAILABE:
    		mProgressBarDisplay.setVisibility(View.GONE);
    		processNmtUnavailable();
    		break;
    	}
    }
    
	private class RefreshDisplayThread extends Thread {		
		
		Handler updateHandler;
		
		// flag to hold game state 
		private boolean running = false;
		public void setRunning(boolean running) {
			this.running = running;
		}
		
		public RefreshDisplayThread(Handler _updateHandler) {
			super();
			this.updateHandler = _updateHandler;
		}
		
		@Override
		public void run() {
			while (running) {
				try {
					synchronized (this) {						
						wait(5 * 1000);//5 segundos
						
						Message msg = null;
						
						try{
							boolean nmtExists = davidBoxService.checkNmtExist();
							if (nmtExists){
								//only if nmt exist							
								DavidBoxResponse davidBoxResponse = davidBoxService.getCurrentlyPlaying();
								msg = updateHandler.obtainMessage(MESSAGE_UPDATE_THREAD_NOW);
								msg.obj = davidBoxResponse;								
							} else {
								msg = updateHandler.obtainMessage(MESSAGE_NMT_UNAVAILABE);
							}
						} catch (TheDavidBoxClientException tdce){
							msg = updateHandler.obtainMessage(MESSAGE_NMT_UNAVAILABE);
						}
						msg.sendToTarget();
					}
				} catch (InterruptedException e) {
					
					//So, it's no longer running
					setRunning(false);
					
					DmtLogger.e(TAG, "InterruptedException", e);
				} 
			}
		}
		
	}	
	
	// Container Activity must implement this interface
    interface OnCurrentlyAudioPlayingDetectedListener {
        void onCurrentlyAudioPlayingDetected(String fullPath);
    }	
    	
}