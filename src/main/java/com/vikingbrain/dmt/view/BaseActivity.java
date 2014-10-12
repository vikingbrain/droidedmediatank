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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.oi.filemanager.FileManagerActivity;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.util.UtilProfile;

/**
 * Abstract class for all the activities in the application.
 * 
 * @author Rafael Iñigo
 */
public abstract class BaseActivity extends SherlockFragmentActivity {
	
	/** Service with the settings. */
	private SettingsService settingsService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    //ActionBarSherlock
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);
	    
		settingsService = DMTApplication.getSettingsService();
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        // app icon in action bar clicked; go home
	        Intent intent = new Intent(this, HomeActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	        return true;	    	    
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Starts the activity settings.
	 */
	public void startSettingsActivityForced(){
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);		
	}

	/**
	 * Show the message in an information toast.
	 * @param idMensaje id of resource
	 */
	public void showToastInformacion(int idMessage) {
		showCustomToast(R.drawable.icon_ok_small, idMessage, null, true);
	}

	/**
	 * Show the message in an error toast.
	 * @param idMensaje id of resource
	 */
	public void showToastError(int idMessage) {
		showCustomToast(R.drawable.icon_error_small, idMessage, null, false);
	}

	/**
	 *  Displays a toast with the icon ok and the message
	 * @param idIcon id or icon resource
	 * @param idMessage id of message resource
	 * @param textNoI18n text not i18n
	 * @param isPositive if message is info or is error
	 */
	private void showCustomToast(int idIcon, int idMessage, CharSequence textNoI18n, boolean isPositive) {
		Context context = this;																

		LayoutInflater inflater = getLayoutInflater();
		
		View layout = inflater.inflate(R.layout.toast_view, (ViewGroup) findViewById(R.id.toast_layout_root));

		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(idIcon);
		TextView text = (TextView) layout.findViewById(R.id.msgToast);
		
		if (null != textNoI18n){
			text.setText(textNoI18n);
		} else {
			text.setText(idMessage);
		}

		if (!isPositive){
			text.setTextColor(Color.RED);
		}
		
		int duration = Toast.LENGTH_SHORT;
		int offsetX = 0;
		int offsetY = 0;
		
		Toast toast = Toast.makeText(context, idMessage, duration); //idMensaje just for nothing
		toast.setGravity(Gravity.BOTTOM, offsetX, offsetY);		
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();		
	}
	
	/** 
	 * Handle the click on the home button.
	 * @param v the View
	 */
	public void onClickHome (View v) {
	    goHome (this);
	}

	/**
	 * Handle the click on the About button.
	 * @param v the View
	 */
	public void onClickInfo (View v) {
	    startActivity (new Intent(getApplicationContext(), InfoActivity.class));
	}

	/**
	 * Handle the click of a Feature button.
	 * @param v View
	 */
	public void onClickFeature (View v) {
	    int id = v.getId ();
		
		//Get active profile
		Profile activeProfile = settingsService.getActiveProfile();

	    switch (id) {
	      case R.id.home_btn_feature1 :
			   if (checkSpecifiedModelAndIpOrDomain(activeProfile)
					&& checkSpecifiedFtpSettings(activeProfile)){
					startActivity (new Intent(getApplicationContext(), FileManagerActivity.class));
				}
	           break;
	      case R.id.home_btn_feature2 :
			   if (checkSpecifiedModelAndIpOrDomain(activeProfile)){
					startActivity (new Intent(getApplicationContext(), WebClientsActivity.class));
				}
	           break;
	      case R.id.home_btn_feature3 :
	           startActivity (new Intent(getApplicationContext(), SettingsActivity.class));
	           break;
	      case R.id.home_btn_feature4 :
	           startActivity (new Intent(getApplicationContext(), InfoActivity.class));
	           break;	           
	      case R.id.home_btn_feature5 :
			   if (checkSpecifiedModelAndIpOrDomain(activeProfile)){
					startActivity (new Intent(getApplicationContext(), DiskUsageActivity.class));
				}
	           break;
	      case R.id.home_btn_feature6 :
			   if (checkSpecifiedModelAndIpOrDomain(activeProfile)){		  
					startActivity (new Intent(getApplicationContext(), RemoteControlActivity.class));
				}
	           break;	           	           
	      case R.id.home_btn_feature7 :
			   if (checkSpecifiedModelAndIpOrDomain(activeProfile)){		  
					startActivity (new Intent(getApplicationContext(), ServicesActivity.class));
				}
	           break;	           	           
	      case R.id.home_btn_feature8 :
			   if (checkSpecifiedModelAndIpOrDomain(activeProfile)){		  
					startActivity (new Intent(getApplicationContext(), PlaylistActivity.class));
				}
	           break;	           	           
	      default: 
	    	   break;
	    }
	}
	
	/**
	 * Check if a it has been specified model and IP or domain for the profile.
	 * @param profile the profile to look up
	 * @return if model and IP or domain were specified
	 */	
	private boolean checkSpecifiedModelAndIpOrDomain(Profile profile){		
		if (!UtilProfile.isModelSpecified(profile)){
			showAlertDialog(R.string.require_config_profile_model);			
			return false;
		}
		if (!UtilProfile.isIpOrDomainSpecified(profile)){
			showAlertDialog(R.string.require_config_profile_ipdomain);
			return false;
		}
		return true;
	}
	
	/**
	 * Check if FTP user and password were specified for the profile.
	 * @param profile the profile to look up
	 * @return if FTP user and password were specified for the profile
	 */
	private boolean checkSpecifiedFtpSettings(Profile profile){
		if (!UtilProfile.isFtpUserSpecified(profile)){
			showAlertDialog(R.string.require_config_profile_ftp_user);
			return false;
		}
		if (!UtilProfile.isFtpPasswordSpecified(profile)){
			showAlertDialog(R.string.require_config_profile_ftp_password);
			return false;
		}
		return true;
	}
	
	/**
	 * Show alert dialog with only one OK button.
	 * @param idMessageReason id with the message resource
	 */
	public void showAlertDialog(int idMessageReason){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(idMessageReason)
		       .setCancelable(false)
		       .setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {		        	   
		        	   
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Go back to the home activity.
	 * @param context the Context
	 */
	public void goHome(Context context) {
	    final Intent intent = new Intent(context, HomeActivity.class);
	    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    context.startActivity (intent);
	}

	/**
	 * Show a string on the screen via Toast.
	 * @param msg the message
	 */
	public void toast (String msg) {
	    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	}
	
}
