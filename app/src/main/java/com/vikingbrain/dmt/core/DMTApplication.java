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
package com.vikingbrain.dmt.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Application;
import android.content.Context;

import com.vikingbrain.dmt.dao.ConfigDAO;
import com.vikingbrain.dmt.dao.LinkDAO;
import com.vikingbrain.dmt.dao.ProfileDAO;
import com.vikingbrain.dmt.dao.impl.ConfigDAOImpl;
import com.vikingbrain.dmt.dao.impl.LinkDAOImpl;
import com.vikingbrain.dmt.dao.impl.ProfileDAOImpl;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.service.FtpService;
import com.vikingbrain.dmt.service.LinkService;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.TransmissionService;
import com.vikingbrain.dmt.service.impl.DavidBoxServiceImpl;
import com.vikingbrain.dmt.service.impl.FtpServiceImpl;
import com.vikingbrain.dmt.service.impl.LinkServiceImpl;
import com.vikingbrain.dmt.service.impl.SettingsServiceImpl;
import com.vikingbrain.dmt.service.impl.TransmissionServiceImpl;

/**
 * Class loaded on application startup that creates singletons and injects implementation objects 
 * for DAO's and Services.
 * 
 * @author Rafael Iñigo
 */
public class DMTApplication extends Application {

	/** Tag for logging. */
	private static final String TAG = DMTApplication.class.getSimpleName();
	
	//DAO's
	private static ConfigDAO configDAO;
	private static ProfileDAO profileDAO;
	private static LinkDAO linkDAO;

	//Services business logic
	private static LinkService linkService;
	private static SettingsService settingsService;
	private static FtpService ftpService;
	private static DavidBoxService davidBoxService;
	private static TransmissionService transmissionService;	
	
    @Override
    public void onCreate() {

    	//Here because it can not be moved to GenericDAO because of the methods it is using
		FileOutputStream fos;
		try {
			fos = openFileOutput(Constants.NMJ_NO_VIDEO, Context.MODE_PRIVATE);
			fos.close();
			
			fos = openFileOutput(Constants.NMJ_NO_MUSIC, Context.MODE_PRIVATE);
			fos.close();

			fos = openFileOutput(Constants.NMJ_NO_PHOTO, Context.MODE_PRIVATE);
			fos.close();

			fos = openFileOutput(Constants.NMJ_NO_ALL, Context.MODE_PRIVATE);
			fos.close();

			fos = openFileOutput(Constants.PLAYLIST_MYIHOME_FILE_NAME, Context.MODE_WORLD_READABLE);
			fos.close();

			fos = openFileOutput(Constants.PLAYLIST_LLINK_FILE_NAME, Context.MODE_WORLD_READABLE);
			fos.close();

			fos = openFileOutput(Constants.PLAYLIST_NMT_FILE_NAME, Context.MODE_WORLD_READABLE);
			fos.close();

		} catch (FileNotFoundException e1) {
			DmtLogger.e(TAG, e1.getMessage());			
		} catch (IOException ioe) {
			DmtLogger.e(TAG, ioe.getMessage());
		}
		
		//Init DAO's singletons
		initDAOs();
		
		//Init Services singletons
		initServices();		
    }

    /**
     * Init the DAO's.
     */
    private void initDAOs(){
		 if (null == configDAO) {
			 configDAO = new ConfigDAOImpl(this);
		 }
		 if (null == profileDAO) {
			 profileDAO = new ProfileDAOImpl(this);
		 }
		 if (null == linkDAO){
			 linkDAO = new LinkDAOImpl(this);			 
		 }    	
    }

    /**
     * Init the services.
     */
    private void initServices(){
		 if (null == linkService){
			  linkService = new LinkServiceImpl(linkDAO);			 
		 }
		 if (null == settingsService){
		  	  settingsService = new SettingsServiceImpl(configDAO, profileDAO);			 
		 }
		 if (null == ftpService){
		  	  ftpService = new FtpServiceImpl(settingsService);			 
		 }
		 if (null == davidBoxService){
		  	  davidBoxService = new DavidBoxServiceImpl(settingsService);			 
		 }
		 if (null == transmissionService){
		  	  transmissionService = new TransmissionServiceImpl(settingsService, linkService);			 
		 }    	
    }
    	
    /**
     * Get singleton link service.
     * @return the link service
     */
    public static LinkService getLinkService() {	
	  return linkService;
    }

    /**
     * Get singleton settings service.
     * @return the settings service
     */
    public static SettingsService getSettingsService() {	  
	    return settingsService;
    }

    /**
     * Get singleton FTP service.
     * @return the FTP service
     */
    public static FtpService getFtpService() {
	    return ftpService;
    }

    /**
     * Get singleton DavidBox service.
     * @return the DavidBox service
     */
    public static DavidBoxService getDavidBoxService() {	  
	    return davidBoxService;
    }

    /**
     * Get singleton Transmission service.
     * @return the Transmission service.
     */
    public static TransmissionService getTransmissionService() {	  
	    return transmissionService;
    }

    /**
     * Check if Android device version is equal or higher than some version.
     * @param version the android version to check
     * @return if Android device version is equal or higher
     */
	public static boolean isAndroidVersionEqualOrHigherThan(int version){
		boolean isEqualOrHigher = false;
	    if (android.os.Build.VERSION.SDK_INT >= version){ 
	    	isEqualOrHigher = true;
	    }
	    return isEqualOrHigher;
	}
    
}
