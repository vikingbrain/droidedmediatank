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
package com.vikingbrain.dmt.service.util;

import android.content.Context;
import android.net.Uri;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.pojo.util.UtilityHttp;

/**
 * Utility class for profiles.
 * 
 * @author Rafael Iñigo
 */
public class UtilProfile {

	/** Tag for logging. */
	private static final String TAG = UtilProfile.class.getSimpleName();

	/**
	 * It creates a profile with default attributes.
	 * @return the profile
	 */
	public static Profile buildProfileDefaultFields(){
		Profile profile = new Profile(null, "", "", "", "", "","",
				Constants.FTP_NMT_DRIVE_NAME_DEFAULT,Constants.NUMBER_ONE, Constants.NUMBER_ONE, "", "");
		return profile;
	}
	
	/**
	 * Get the base MyiHome url for the profile.
	 * @param profile the profile
	 * @return the base url
	 */
	private static String getBaseStreamMyiHome(Profile profile){
		String ipOrDomain = profile.getIpNmt();
		String base = "http://" + ipOrDomain + Constants.MYIHOME_PORT_AND_STREAM;
		return base;
	}

	/**
	 * Get the base Llink url for the profile.
	 * @param profile the profile
	 * @return the base url
	 */
	private static String getBaseStreamLlink(Profile profile){
		String ipOrDomain = profile.getIpNmt();
		String savedLlinkPort = profile.getLlinkPort();
		String llinkPort = "".equals(savedLlinkPort) ? Constants.LLINK_PORT_DEFAULT : savedLlinkPort;
		String base = "http://" + ipOrDomain + ":" + llinkPort;
		return base;		
	}

	/**
	 * Get the MyiHome Uri for a file with a profile.
	 * @param file the file
	 * @param profile the profile
	 * @return the Uri
	 */
	public static Uri getUriMyiHomeStream(DMTFile file, Profile profile){
		String realPathNMT = calculateRealPathNMT(file, profile);		
		String realUrl = getBaseStreamMyiHome(profile) + realPathNMT;

		Uri uriActivity = Uri.parse(realUrl);
		DmtLogger.d(TAG, "final URI:" + uriActivity);

		return uriActivity;							
	}

	/**
	 * Get the llink stream url like:
	 * http://popcorn:8001/SATA_DISK/ztesting/Lisa_Ekdahl/02.mp3
	 * Or if ftp root has not drive name then it will be:
	 * http://popcorn:8001/ztesting/Lisa_Ekdahl/02.mp3
	 * @param file the file
	 * @param profile the profile
	 * @return the Uri
	 */
	public static Uri getUriLlinkStream(DMTFile file, Profile profile){
		String realPathNMT = calculateRealPathNMT(file, profile);		
		//Now convert real path like /opt/sybhttpd/localhost.drives/SATA_DISK/ztesting/02.mp3
		//to /SATA_DISK/ztesting/02.mp3
		//I just remove the text "/opt/sybhttpd/localhost.drives" from the path
		String llinkFilePath = realPathNMT.replace(Constants.FTP_NMT_DRIVE_NAME_DEFAULT, "");		
		
		String realUrl = getBaseStreamLlink(profile) + llinkFilePath;		
		
		Uri uriActivity = Uri.parse(realUrl);
		DmtLogger.d(TAG, "final URI:" + uriActivity);

		return uriActivity;							
	}

	/**
	 * Calculate the real NMT path with "file://" like for example file:///opt/sybhttpd/localhost.drives/SATA_DISK/ztesting/02.mp3.
	 * @param file the file
	 * @param profile the profile
	 * @return the real NMT path
	 */
	public static String calculateNmtPathWithFileSchema(DMTFile file, Profile profile){
		String filePathWithFileSchema = "file://" + calculateRealPathNMT(file, profile);
		return filePathWithFileSchema;
	}	

	/**
	 * Calculates the real linux path in the system from a relative ftp path.
	 * It also replace empty spaces of the path with %20.
	 * It returns something like:
	 * /opt/sybhttpd/localhost.drives/SATA_DISK/ztesting/02.mp3 or
	 * /opt/sybhttpd/localhost.drives/HARD_DISK/ztesting/02.mp3, etc...
	 * @param file the file
	 * @param the profile
	 * @return the real NMT path for the file
	 */
	private static String calculateRealPathNMT(DMTFile file, Profile profile){
	 	String userDefinedFtpNmtRealRoot = profile.getFtpNmtDriveName();
		 	
	 	String pathFTP = file.getPath(); //The relative path in the ftp browser
	 	DmtLogger.d(TAG, "pathFTP:" + pathFTP); 

		//Encode special symbols in the path so it is accessible via http
		String encodedPath = UtilityHttp.encodeHttpParameter(pathFTP);

		String inicioPathNMT = "";
				
		//Si la ruta ftp del fichero contiene estos valores ignoro lo que tenga la setting nmt real path
		if (encodedPath.startsWith("/SATA_DISK")
				|| encodedPath.startsWith("/HARD_DISK")
				|| encodedPath.startsWith("/USB_DRIVE")
				|| encodedPath.startsWith("/NETWORK_SHARE")){
				
			inicioPathNMT = Constants.FTP_NMT_DRIVE_NAME_DEFAULT;
		} else {
			inicioPathNMT = userDefinedFtpNmtRealRoot; //Use the drive name specified by user in settings
		}

		String rutaRealNMT = inicioPathNMT + encodedPath;
		return rutaRealNMT;			
	}
	
	/**
	 * Check if DavidBox service is supported for the device.
	 * @param profile the profile
	 * @return if davidbox service is supported
	 */
	public static boolean isDavidBoxSupported(Profile profile){
		String typeNmt = profile.getTypeNmt();
		boolean supported = false;
		if ("Popcorn Hour A200".equalsIgnoreCase(typeNmt)
				|| "Popcorn Hour A210".equalsIgnoreCase(typeNmt)
				|| "Popcorn Hour A300".equalsIgnoreCase(typeNmt)
				|| "Popcorn Hour A400".equalsIgnoreCase(typeNmt)
				|| "Popcorn Hour C200".equalsIgnoreCase(typeNmt)
				|| "Popcorn Hour C300".equalsIgnoreCase(typeNmt)
				|| "PopBox 3D".equalsIgnoreCase(typeNmt)
				|| "PopBox V8".equalsIgnoreCase(typeNmt)){
			supported = true;
		}
		return supported;
	}

	/**
	 * Get the IP for the web clients, if not specified IP for web clients 
	 * then get the ipOrDomain for FTP.
	 * @param profile the profile
	 * @return the IP or domain for the web clients
	 */
	public static String getIpOrDomainForWebClients(Profile profile){
		//Get the ip for the web clients
		String ipOrDomain = profile.getIpWebClients(); 
		//if not specified ip for web clients, then get the ipOrDomain for ftp
		if ("".equals(ipOrDomain)){
			ipOrDomain = profile.getIpNmt(); 
		}
		return ipOrDomain;
	}

	/**
	 * Check if MyiHome is available for a profile.
	 * @param profile the profile
	 * @return if MyiHome is available
	 */
	public static boolean isMyiHomeAvailable(Profile profile){
		boolean active = profile.isMyihomeActive();
		//Special case MyiHome is disable don't care what is in DDBB
    	if ("Popcorn Hour A300".equalsIgnoreCase(profile.getTypeNmt())
    			|| "Popcorn Hour A400".equalsIgnoreCase(profile.getTypeNmt())
    			|| "Popcorn Hour C300".equalsIgnoreCase(profile.getTypeNmt())){
    		active = false;
    	}		
		return active;
	}
	
	/**
	 * Check if Llink is available for a profile.
	 * @param profile the profile
	 * @return if Llink is available
	 */
	public static boolean isLlinkAvailable(Profile profile){
		boolean active = profile.isLlinkActive();
		return active;
	}

	/**
	 * Return the alias for the profile.
	 * @param profile the profile
	 * @param context application context
	 * @return the alias for the profile
	 */
	public static String getAlias(Profile profile, Context context){
		String description = "";		
		if (!"".equals(profile.getName())){
			description = profile.getName();
		} else if (!"".equals(profile.getTypeNmt())){
			description = profile.getTypeNmt();
		} else if (!"".equals(profile.getIpNmt())){
			description = profile.getIpNmt();
		} else if (null != context){
			description = context.getString(R.string.profile);
		}
		return description;
	}
	
	/**
	 * Check if NMT model is specified for a profile.
	 * @param profile the profile
	 * @return if NMT model is specified
	 */
	public static boolean isModelSpecified(Profile profile){
		boolean isModelSpecified = false;
		if (!"".equals(profile.getTypeNmt())){
			isModelSpecified = true;
		}
		return isModelSpecified;
	}

	/**
	 * Check if IP or domain name is specified for a profile.
	 * @param profile the profile
	 * @return if IP or domain name is specified
	 */
	public static boolean isIpOrDomainSpecified(Profile profile){
		boolean isIpOrDomainSpecified = false;
		if (!"".equals(profile.getIpNmt())){
			isIpOrDomainSpecified = true;
		}
		return isIpOrDomainSpecified;
	}

	/**
	 * Check if FTP user name is specified for a profile.
	 * @param profile the profile
	 * @return if FTP use name is specified
	 */
	public static boolean isFtpUserSpecified(Profile profile){
		boolean isFtpUserSpecified = false;
		if (!"".equals(profile.getFtpUser())){
			isFtpUserSpecified = true;
		}
		return isFtpUserSpecified;
	}

	/**
	 * Check if FTP user password is specified for a profile.
	 * @param profile the profile
	 * @return if FTP use password is specified
	 */	
	public static boolean isFtpPasswordSpecified(Profile profile){
		boolean isFtpPasswordSpecified = false;
		if (!"".equals(profile.getFtpPassword())){
			isFtpPasswordSpecified = true;
		}
		return isFtpPasswordSpecified;
	}

}
