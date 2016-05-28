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

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Link;
import com.vikingbrain.dmt.pojo.TypeLink;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.LinkService;
import com.vikingbrain.dmt.view.util.UtilityView;

/**
 * Activity to show link (custom link or app link provided by the app) so it can be modified.
 * 
 * @author Rafael Iñigo
 */
public class LinkDetailActivity extends BaseActivity {
	
	private static final String TAG = LinkDetailActivity.class.getSimpleName();
	
	private LinkService linkService;
	
    ImageView icono;
    EditText textName;               
    TextView textUrlStart;
    EditText textUrlEnd;
    CheckBox checkAuthentication;
    LinearLayout layoutUserPassword;
    EditText textUserName;
    EditText textPassword;
    TextView info;

    Button saveButton;
    Button deleteButton;
    Button restoreButton;

    LinearLayout infoLinearLayout;

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	//Emulate back button
	    	onBackPressed();
	        return true;	    	    
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        
        linkService = DMTApplication.getLinkService();
        
        setContentView(R.layout.link_detail);
                                
        //Fields
        icono = (ImageView) findViewById(R.id.icon);
        textName = (EditText) findViewById(R.id.textName);               
        textUrlStart = (TextView) findViewById(R.id.textUrlStart);
        textUrlEnd = (EditText) findViewById(R.id.textUrlEnd);
        info = (TextView) findViewById(R.id.info);
        
        checkAuthentication = (CheckBox)findViewById(R.id.checkAuthentication);
        layoutUserPassword = (LinearLayout)findViewById(R.id.linearlayout_user_password);
        textUserName = (EditText) findViewById(R.id.textUserName);
        textPassword = (EditText) findViewById(R.id.textPassword);
        textPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        textPassword.setTransformationMethod(new PasswordTransformationMethod());

        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        restoreButton = (Button) findViewById(R.id.restoreButton);

        infoLinearLayout = (LinearLayout) findViewById(R.id.infoLinearLayout);

        //Set the text for the url start
        String textHttpIpOrDomain = "http://ipOrDomain";
        textUrlStart.setText((CharSequence)textHttpIpOrDomain);

	    //Check if a url was asked as intent extra
	    Bundle b = getIntent().getExtras();
	    if (b != null) {
	    	//Edition mode	    		    	
	    	long id = b.getLong("id");	    	
	    	final Link link = linkService.getLink(id);
	    	    	
	    	updateViewWithCurrentData(link);

	    	OnClickListener listenerEditLink = new OnClickListener() {
				public void onClick(View view) {				
					DmtLogger.d(TAG, "Pressed save button");
					
					String newName = textName.getText().toString();
					String newUrlEnd = textUrlEnd.getText().toString();
					
					int authentication = 0;
					if (checkAuthentication.isChecked()){
						authentication = 1;
					}
					
					String newUserName = textUserName.getText().toString();
					String newPassword = textPassword.getText().toString();
						
					DmtLogger.d(TAG, "newName " + newName);
					DmtLogger.d(TAG, "newUrlEnd " + newUrlEnd);
					
					//Check required fields before saving
					if (checkRequiredFields(newName, newUrlEnd)){
						//Set new values
						if (TypeLink.CUSTOM == link.getTypeLink()){
							//Change name if is a custom user link
							link.setName(newName);
						}
						link.setUrlEnd(newUrlEnd);
						link.setAuthentication(authentication);
						link.setUserName(newUserName);
						link.setPassword(newPassword);
						//Save or update
						linkService.saveOrUpdate(link);

						finish();
					}					
				}
	    	};
	    	
			// Handle button-click
			saveButton.setOnClickListener(listenerEditLink);

	    } else {
	    	//Mode add favorite
	    	
			//Set the image
			icono.setImageResource(R.drawable.icon_star);
			
	    	OnClickListener listenerAddFavorite = new OnClickListener() {
				public void onClick(View view) {				
					DmtLogger.d(TAG, "Pressed save favorite");
					
					String newName = textName.getText().toString();
					String newUrlEnd = textUrlEnd.getText().toString();

					int authentication = 0;
					if (checkAuthentication.isChecked()){
						authentication = 1;
					}					
					
					String newUserName = textUserName.getText().toString();
					String newPassword = textPassword.getText().toString();

					DmtLogger.d(TAG, "newName " + newName);
					DmtLogger.d(TAG, "newUrlEnd " + newUrlEnd);
					
					//Check required fields before saving
					if (checkRequiredFields(newName, newUrlEnd)){
						//save
						linkService.insertFavorite(newName, newUrlEnd, authentication, newUserName, newPassword);
						
						showToastInformacion(R.string.msg_added_fav_ok);						
						
						finish();
					}
				}
			};
	    	
			// Handle button-click
			saveButton.setOnClickListener(listenerAddFavorite);	    	
	    }
		
	    checkAuthentication.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				changeVisibilityUserPassword(isChecked);	
			}
		});
    }

    private void changeVisibilityUserPassword(boolean isAuthenticationChecked){
		if (isAuthenticationChecked){
			layoutUserPassword.setVisibility(View.VISIBLE);
		} else {
			layoutUserPassword.setVisibility(View.GONE);
		}    	
    }
    
	private void updateViewWithCurrentData(Link link){
		//Set the image
		icono.setImageResource(UtilityView.getIdImage(link));
		
		//Name of the link
		UtilityView.setTextToLink(link, textName);
		
		if (TypeLink.ORIGINAL == link.getTypeLink()){
			
			//Make non-editable the link's name when is an Original
	    	textName.setFilters(new InputFilter[] {new InputFilter() {  
	    		//Workaround to make it non-editable
	    		@Override
	    		public CharSequence filter(CharSequence source,int start, int end, Spanned dest,int dstart,int dend) {
	    			return source.length() <1 ? dest.subSequence(dstart, dend) :"";
	    		}
	    	} });
			
		}

		//Button restore default for Original links
		if (TypeLink.ORIGINAL == link.getTypeLink()){
			restoreButton.setVisibility(View.VISIBLE);			
			//Asignaci������n de la acci������n de restore url
			restoreButton.setOnClickListener(getListenerRestoreUrl(link));		
		}

		//Button delete for Custom link
		if (TypeLink.CUSTOM == link.getTypeLink()){
			deleteButton.setVisibility(View.VISIBLE);			
			//Sets the action listener for the delete button
			deleteButton.setOnClickListener(getListenerDeleteFavorite(link));
		}
		
		//UrlEnd
		textUrlEnd.setText(link.getUrlEnd());			
		
		//Authentication
		boolean isAuthenticationChecked = link.isAuthentication(); 
		checkAuthentication.setChecked(isAuthenticationChecked);
		textUserName.setText((CharSequence)link.getUserName());
		textPassword.setText((CharSequence)link.getPassword());
		
		//Show only username and password is the checkbox authentication is checked
		changeVisibilityUserPassword(isAuthenticationChecked);
	}
	
	private OnClickListener getListenerRestoreUrl(final Link enlace){
    	OnClickListener listenerRestoreUrl = new OnClickListener() {
			public void onClick(View view) {				
				DmtLogger.d(TAG, "Pressed button restore url");
				
				//Put the default url value in the text box
				textUrlEnd.setText(enlace.getUrlDefault());
			}
		};
		return listenerRestoreUrl;
	}
	
	private OnClickListener getListenerDeleteFavorite(final Link link){
    	OnClickListener listenerDeleteFavorite = new OnClickListener() {
			public void onClick(View view) {				
				DmtLogger.d(TAG, "Pressed button delete favorite");
				
				//delete the favorite
				linkService.delete(link.getId());
					
				finish();
			}
		};
		return listenerDeleteFavorite;
	}
	
	private boolean checkRequiredFields(String newName, String newUrlEnd){
		boolean valid = false;
		if ("".equals(newName)){
			
			showToastError(R.string.msg_invalid_name);			
			textName.requestFocus();
			
		} else if ("".equals(newUrlEnd)){
			
			showToastError(R.string.msg_invalid_urlEnd);			
			textUrlEnd.requestFocus();
			
		} else {
			valid = true;
		}
		return valid;
	}

}
