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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.service.util.UtilProfile;
import com.vikingbrain.dmt.view.Eula.OnEulaAgreedTo;

/**
 * This is the activity for the dashboard user interface.
 * 
 * @author Rafael Iñigo
 */
public class HomeActivity extends AbstractProfileChangerActivity implements OnEulaAgreedTo {
	
	@Override
	public void onEulaAgreedTo() {
		onResume();
	}

	/**
	 * onCreate - called when the activity is first created. Called when the
	 * activity is first created. This is where you should do all of your normal
	 * static set up: create views, bind data to lists, etc. This method also
	 * provides you with a Bundle containing the activity's previously frozen
	 * state, if there was one.
	 * 
	 * Always followed by onStart().
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    //ActionBarSherlock
		//This is the only one activity with a diferent setDisplayHomeAsUpEnabled
	    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		setContentView(R.layout.activity_home);
		
        //Muestra el Eula si procede
        Eula.show(this, getSettingsService());
	}

	/**
	 * onDestroy The final call you receive before your activity is destroyed.
	 * This can happen either because the activity is finishing (someone called
	 * finish() on it, or because the system is temporarily destroying this
	 * instance of the activity to save space. You can distinguish between these
	 * two scenarios with the isFinishing() method.
	 * 
	 */
	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * onPause Called when the system is about to start resuming a previous
	 * activity. This is typically used to commit unsaved changes to persistent
	 * data, stop animations and other things that may be consuming CPU, etc.
	 * Implementations of this method must be very quick because the next
	 * activity will not be resumed until this method returns. Followed by
	 * either onResume() if the activity returns back to the front, or onStop()
	 * if it becomes invisible to the user.
	 * 
	 */
	@SuppressWarnings("PMD.UselessOverridingMethod")
	protected void onPause() {
		super.onPause();
	}

	/**
	 * onRestart Called after your activity has been stopped, prior to it being
	 * started again. Always followed by onStart().
	 * 
	 */
	@SuppressWarnings("PMD.UselessOverridingMethod")
	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * onResume Called when the activity will start interacting with the user.
	 * At this point your activity is at the top of the activity stack, with
	 * user input going to it. Always followed by onPause().
	 * 
	 */
	protected void onResume() {
		super.onResume();
		
		if (getSettingsService().isEulaAccepted()){
			
			//Get list of profiles
			List<Profile> profiles = getSettingsService().getProfiles();

			if (profiles.size() == 0){
				//There are no profiles, ask to configure settings now
					showAlertConfigurePrerefencesNow(R.string.require_config_no_profiles);
			} else {
				//Load the navigation list with the available profiles
				loadProfileNavigation(profiles);		
				
				//Show or hide available dashboard buttons for this nmt model
				manageDashboardButtons();
			}
		}		

	}

	/**
	 * onStart Called when the activity is becoming visible to the user.
	 * Followed by onResume() if the activity comes to the foreground, or
	 * onStop() if it becomes hidden.
	 * 
	 */
	@SuppressWarnings("PMD.UselessOverridingMethod")
	protected void onStart() {
		super.onStart();
	}

	/**
	 * onStop Called when the activity is no longer visible to the user because
	 * another activity has been resumed and is covering this one. This may
	 * happen either because a new activity is being started, an existing one is
	 * being brought in front of this one, or this one is being destroyed.
	 * 
	 * Followed by either onRestart() if this activity is coming back to
	 * interact with the user, or onDestroy() if this activity is going away.
	 */
	@SuppressWarnings("PMD.UselessOverridingMethod")
	protected void onStop() {
		super.onStop();
	}


	/**
	 * Show alert dialog that redirects to settings screen.
	 */
	private void showAlertConfigurePrerefencesNow(int idMessageReason){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(idMessageReason)
		       .setCancelable(false)
		       .setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {		        	   
		        	   startSettingsActivityForced();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void manageDashboardButtons(){
		//Show or hide davidbox depending buttons
		Button buttonDiskUsage = (Button)findViewById(R.id.home_btn_feature5);
		Button buttonRemoteControl = (Button)findViewById(R.id.home_btn_feature6);
		Button buttonServices = (Button)findViewById(R.id.home_btn_feature7);
		Button buttonPlaylist = (Button)findViewById(R.id.home_btn_feature8);
		
		boolean isDavidBoxSupported = UtilProfile.isDavidBoxSupported(getSettingsService().getActiveProfile());
		if (isDavidBoxSupported){
			buttonDiskUsage.setVisibility(View.VISIBLE);
			buttonRemoteControl.setVisibility(View.VISIBLE);			
			buttonServices.setVisibility(View.VISIBLE);
			buttonPlaylist.setVisibility(View.VISIBLE);
		} else {
			
			//TODO visibility gone for the LINEARLAYOUT, to not allow empy space in between screen
			
			buttonDiskUsage.setVisibility(View.GONE);
			buttonRemoteControl.setVisibility(View.GONE);			
			buttonServices.setVisibility(View.GONE);
			buttonPlaylist.setVisibility(View.GONE);
		}		
	}
    
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		super.onNavigationItemSelected(itemPosition, itemId);
		
		//Show or hide available dashboard buttons for this nmt model
		manageDashboardButtons();
		
        return true;
	}
	
}