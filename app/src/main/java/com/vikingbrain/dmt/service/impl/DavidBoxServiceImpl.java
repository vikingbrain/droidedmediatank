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
package com.vikingbrain.dmt.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.pojo.util.UtilityFile;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.TheDavidboxBridge;
import com.vikingbrain.dmt.service.util.UtilProfile;
import com.vikingbrain.nmt.client.TheDavidBoxClient;
import com.vikingbrain.nmt.operations.playback.DeleteAodEntryQueueOperation;
import com.vikingbrain.nmt.operations.playback.GetCurrentAodInfoOperation;
import com.vikingbrain.nmt.operations.playback.GetCurrentPodInfoOperation;
import com.vikingbrain.nmt.operations.playback.GetCurrentVodInfoOperation;
import com.vikingbrain.nmt.operations.playback.InsertAodQueueOperation;
import com.vikingbrain.nmt.operations.playback.InsertPodQueueOperation;
import com.vikingbrain.nmt.operations.playback.InsertVodQueueOperation;
import com.vikingbrain.nmt.operations.playback.ListAodQueueInfoOperation;
import com.vikingbrain.nmt.operations.playback.StartAodOperation;
import com.vikingbrain.nmt.operations.playback.StartPodOperation;
import com.vikingbrain.nmt.operations.playback.StartVodOperation;
import com.vikingbrain.nmt.operations.system.CheckNmtExistOperation;
import com.vikingbrain.nmt.operations.system.GetDeviceInfoOperation;
import com.vikingbrain.nmt.operations.system.GetNmtServiceStatusOperation;
import com.vikingbrain.nmt.operations.system.ListDevicesOperation;
import com.vikingbrain.nmt.operations.system.ListNmtServicesOperation;
import com.vikingbrain.nmt.operations.system.SendKeyOperation;
import com.vikingbrain.nmt.operations.system.SetNmtServiceStatusOperation;
import com.vikingbrain.nmt.responses.DavidBoxResponse;
import com.vikingbrain.nmt.responses.ResponseSimple;
import com.vikingbrain.nmt.responses.playback.ResponseGetCurrentAodInfo;
import com.vikingbrain.nmt.responses.playback.ResponseGetCurrentPodInfo;
import com.vikingbrain.nmt.responses.playback.ResponseGetCurrentVodInfo;
import com.vikingbrain.nmt.responses.playback.ResponseInsertAodQueue;
import com.vikingbrain.nmt.responses.playback.ResponseInsertPodQueue;
import com.vikingbrain.nmt.responses.playback.ResponseInsertVodQueue;
import com.vikingbrain.nmt.responses.playback.ResponseListAodQueueInfo;
import com.vikingbrain.nmt.responses.system.ObjectDevice;
import com.vikingbrain.nmt.responses.system.ResponseCheckNmtExist;
import com.vikingbrain.nmt.responses.system.ResponseGetDeviceInfo;
import com.vikingbrain.nmt.responses.system.ResponseGetNmtServiceStatus;
import com.vikingbrain.nmt.responses.system.ResponseListDevices;
import com.vikingbrain.nmt.responses.system.ResponseListNmtServices;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Service operations for DavidBox service.
 * 
 * @author Rafael Iñigo
 */
public class DavidBoxServiceImpl implements DavidBoxService {

	/** Tag for logging. */
	private static final String TAG = DavidBoxServiceImpl.class.getSimpleName();

	/** Service for setttings. */
	private SettingsService settingsService;

	/** Bridge used for using davidbox service with the currently active profile. */
	private TheDavidboxBridge theDavidboxBridge = new TheDavidboxBridge();
	
	/**
	 * Constructor.
	 * @param settingsService the settings service
	 */
	public DavidBoxServiceImpl(SettingsService settingsService) {
		super();
		this.settingsService = settingsService;
	}

	/**
	 * Get the davidbox client for the currently active profile.
	 * @return the david box client for the currently active profile
	 */
	private TheDavidBoxClient getTheDavidBoxClient(){
		Profile activeProfile = settingsService.getActiveProfile();
		return theDavidboxBridge.getTheDavidBoxClientForActiveProfile(activeProfile);
	}
	
	/** {@inheritDoc} */
	public boolean isOpenableInNmt(DMTFile file){
		boolean reproducible = false;
		String fileName = file.getName();
		if (UtilityFile.isVideo(fileName)
				|| UtilityFile.isMusic(fileName)
				|| UtilityFile.isPhoto(fileName)){
			reproducible = true;
		}
		return reproducible;
	}

	/**
	 * Calculate the file path with the file schema file:// for the provided file
	 * @param file the file
	 * @return the file path with the file schema file:// for the provided file
	 */
	private String calculateNmtFilePathFileSchema(DMTFile file){
		return UtilProfile.calculateNmtPathWithFileSchema(file, settingsService.getActiveProfile());
	}

	/**
	 * Starts video on NMT.
	 * http://popcorn:8008/playback?arg0=start_vod&arg1="+fileName+"&arg2="+pathWithFileDots+"&arg3=show&arg4="
	 * @throws TheDavidBoxClientException 
	 */
	private DavidBoxResponse startVideoOnNMT(DMTFile file) throws TheDavidBoxClientException{
		//real nmt path without empty spaces
		String filePathWithFileSchema = calculateNmtFilePathFileSchema(file);
		StartVodOperation operation = getTheDavidBoxClient().getModulePlayback().buildStartVodOperation(filePathWithFileSchema);
		ResponseSimple response = operation.execute();
		return response;		
	}

	/** {@inheritDoc} */
	public DavidBoxResponse startAudioOnNMT(DMTFile file) throws TheDavidBoxClientException{
		//real nmt path without empty spaces
		String filePathWithFileSchema = calculateNmtFilePathFileSchema(file);		
		StartAodOperation operation = getTheDavidBoxClient().getModulePlayback().buildStartAodOperation(filePathWithFileSchema);
		ResponseSimple response = operation.execute();
		return response;		
	}

	/**
	 * It starts photo on NMT.
	 * @param file the file
	 * @return the davidbox response
	 * @throws TheDavidBoxClientException exception in the operation
	 */
	private DavidBoxResponse startPhotoOnNMT(DMTFile file) throws TheDavidBoxClientException{
		//real nmt path without empty spaces
		String filePathWithFileSchema = calculateNmtFilePathFileSchema(file);
		StartPodOperation operation = getTheDavidBoxClient().getModulePlayback().buildStartPodOperation(filePathWithFileSchema);
		ResponseSimple response = operation.execute();
		return response;		
	}
	
	/** {@inheritDoc} */
	public DavidBoxResponse startGenericFileOnNMT(DMTFile file) throws TheDavidBoxClientException{
		
		//Init the response, it will return null if the extension in not supported
		//for playing the file on NMT
		DavidBoxResponse response = null;
		
		//Path like file://opt/... without empty spaces
		String filePathWithFileSchema = calculateNmtFilePathFileSchema(file);
		
		if (UtilityFile.isVideo(filePathWithFileSchema)){	
			response = startVideoOnNMT(file);			
		} else if (UtilityFile.isMusic(filePathWithFileSchema)){
			response = startAudioOnNMT(file);
		} else if (UtilityFile.isPhoto(filePathWithFileSchema)){
			response = startPhotoOnNMT(file);
		} 
		
		//TODO find how to open html files on nmt

		return response;
	}
		
	/** {@inheritDoc} */
	public DavidBoxResponse executeKeyIR(String keyName) throws TheDavidBoxClientException{
		SendKeyOperation operation = getTheDavidBoxClient().getModuleSystem().buildSendKeyOperation(keyName);
		ResponseSimple response = operation.execute();
		return response;
	}	
	
	/** {@inheritDoc} */
	public ResponseGetCurrentVodInfo getCurrentVodInfo() throws TheDavidBoxClientException{
		GetCurrentVodInfoOperation operation = getTheDavidBoxClient().getModulePlayback().buildGetCurrentVodInfoOperation();
		ResponseGetCurrentVodInfo response = operation.execute();
		return response;		
	}

	/** {@inheritDoc} */
	public ResponseGetCurrentAodInfo getCurrentAodInfo() throws TheDavidBoxClientException{
		GetCurrentAodInfoOperation operation = getTheDavidBoxClient().getModulePlayback().buildGetCurrentAodInfoOperation();
		ResponseGetCurrentAodInfo response = operation.execute();
		return response;		
	}

	/** {@inheritDoc} */
	public ResponseGetCurrentPodInfo getCurrentPodInfo() throws TheDavidBoxClientException{
		GetCurrentPodInfoOperation operation = getTheDavidBoxClient().getModulePlayback().buildGetCurrentPodInfoOperation();
		ResponseGetCurrentPodInfo response = operation.execute();
		return response;		
	}

	/** {@inheritDoc} */
	public DavidBoxResponse getCurrentlyPlaying() throws TheDavidBoxClientException{
		//First check if video is playing
		DavidBoxResponse davidBoxResponse = getCurrentVodInfo();
		
		if (! davidBoxResponse.isValid()){
			//no video playing, check if audio is playing now
			davidBoxResponse = getCurrentAodInfo();
			
			if (! davidBoxResponse.isValid()){
				//check photo playing
				davidBoxResponse = getCurrentPodInfo();
			}
		}
		
		return davidBoxResponse;		
	}		
    
	/**
	 * Get list of devices.
	 * @return the list of devices
	 * @throws TheDavidBoxClientException exception in the operation
	 */
	private ResponseListDevices getListDevices() throws TheDavidBoxClientException{
		ListDevicesOperation operation = getTheDavidBoxClient().getModuleSystem().buildListDevicesOperation();
		ResponseListDevices response = operation.execute();
		return response;				
	}

	/**
	 * Get info for a specific device.
	 * @param deviceName the device name
	 * @return the response object with the info
	 * @throws TheDavidBoxClientException exception in the operation
	 */
	private ResponseGetDeviceInfo getDeviceInfo(String deviceName) throws TheDavidBoxClientException{
		GetDeviceInfoOperation operation = getTheDavidBoxClient().getModuleSystem().buildGetDeviceInfoOperation(deviceName);
		ResponseGetDeviceInfo response = operation.execute();
		return response;		
	}

	/** {@inheritDoc} */
	public List<ResponseGetDeviceInfo> getDiskSpaces() throws TheDavidBoxClientException{
		//Where the results are gone to set back from the method
		List<ResponseGetDeviceInfo> listResults = new ArrayList<ResponseGetDeviceInfo>();
		
		ResponseListDevices responseListDevices = getListDevices();
		if (null != responseListDevices
				&& responseListDevices.isValid()){

			for (ObjectDevice objectDevice : responseListDevices.getDevices()){

				//we are not using when type is "cd rom"
				if ("harddisk".equals(objectDevice.getType())
						|| "usb".equals(objectDevice.getType())){

					DmtLogger.d(TAG, "Found harddisk or usb");
							
					ResponseGetDeviceInfo responseGetDeviceInfo = getDeviceInfo(objectDevice.getName());	
					if (responseGetDeviceInfo.isValid()){
						
						listResults.add(responseGetDeviceInfo);
					}					
					
				}
			}
		}		
		return listResults;
	}

	/** {@inheritDoc} */
	public boolean checkNmtExist() throws TheDavidBoxClientException{
		boolean check = false;		
		CheckNmtExistOperation operation = getTheDavidBoxClient().getModuleSystem().buildCheckNmtExistOperation();
		ResponseCheckNmtExist response = operation.execute();
		if (response.isValid()
				&& response.isExist()){
			check = true;
		}
		return check;
	}
	
	/**
	 * Insert a video into the NMT queue.
	 * @param file the file
	 * @return the response from the service
	 * @throws TheDavidBoxClientException exception in the operation
	 */
	private ResponseInsertVodQueue insertVideoIntoNMTQueu(DMTFile file) throws TheDavidBoxClientException{
		String filePathWithFileSchema = calculateNmtFilePathFileSchema(file);		
		InsertVodQueueOperation operation = getTheDavidBoxClient().getModulePlayback().buildInsertVodQueueOperation(filePathWithFileSchema);
		ResponseInsertVodQueue response = operation.execute();
		return response;		
	}
	
	/**
	 * It inserts an audio file in the NMT queue.	
	 * @param file the file
	 * @return the response from the service
	 * @throws TheDavidBoxClientException exception in the operation
	 */
	private ResponseInsertAodQueue insertAudioIntoNMTQueu(DMTFile file) throws TheDavidBoxClientException{
		String filePathWithFileSchema = calculateNmtFilePathFileSchema(file); //Path without empty spaces	
		InsertAodQueueOperation operation = getTheDavidBoxClient().getModulePlayback().buildInsertAodQueueOperation(filePathWithFileSchema);
		ResponseInsertAodQueue response = operation.execute();
		return response;		
	}

	/**
	 * It inserts an photo file in the NMT queue.	
	 * @param file the file
	 * @return the response from the service
	 * @throws TheDavidBoxClientException exception in the operation
	 */
	private DavidBoxResponse insertPhotoIntoNMTQueu(DMTFile file) throws TheDavidBoxClientException{
		String filePathWithFileSchema = calculateNmtFilePathFileSchema(file); //Path without empty spaces
		InsertPodQueueOperation operation = getTheDavidBoxClient().getModulePlayback().buildInsertPodQueueOperation(filePathWithFileSchema);
		ResponseInsertPodQueue response = operation.execute();
		return response;		
	}
	
	/** {@inheritDoc} */
	public DavidBoxResponse insertGenericFileIntoNMTQueu(DMTFile file) throws TheDavidBoxClientException{
		
		//Init the response. It will return null if the extension in not supported
		//for playing the file on NMT
		DavidBoxResponse response = null;

		//Path like file://opt/...
		String filePathWithFileSchema = calculateNmtFilePathFileSchema(file);
		
		//music first because it's the most common operation
		if (UtilityFile.isMusic(filePathWithFileSchema)){
			response = insertAudioIntoNMTQueu(file);
		} else if (UtilityFile.isVideo(filePathWithFileSchema)){
			response = insertVideoIntoNMTQueu(file);
		} else if (UtilityFile.isPhoto(filePathWithFileSchema)){
			response = insertPhotoIntoNMTQueu(file);
		} 
		
		return response;
	}

	/** {@inheritDoc} */
	public ResponseListNmtServices listNmtServices() throws TheDavidBoxClientException{
		ListNmtServicesOperation operation = getTheDavidBoxClient().getModuleSystem().buildListNmtServicesOperation();
		ResponseListNmtServices response = operation.execute();
		return response;		
	}

	/** {@inheritDoc} */	
	public ResponseGetNmtServiceStatus getNmtServiceStatus(String serviceName) throws TheDavidBoxClientException{
		GetNmtServiceStatusOperation operation = getTheDavidBoxClient().getModuleSystem().buildGetNmtServiceStatusOperation(serviceName);
		ResponseGetNmtServiceStatus response = operation.execute();
		return response;		
	}
	
	/** {@inheritDoc} */
	public DavidBoxResponse setNmtServiceStatus(String serviceName, boolean status) throws TheDavidBoxClientException{
		SetNmtServiceStatusOperation operation = getTheDavidBoxClient().getModuleSystem().buildSetNmtServiceStatusOperation(serviceName, status);
		DavidBoxResponse response = operation.execute();
		return response;		
	}

    /** {@inheritDoc} */
    public ResponseListAodQueueInfo getListAodQueueInfo() throws TheDavidBoxClientException{
    	ListAodQueueInfoOperation operation = getTheDavidBoxClient().getModulePlayback().buildListAodQueueInfoOperation();
		ResponseListAodQueueInfo response = operation.execute();
		return response;		
    }

    /** {@inheritDoc} */
    public DavidBoxResponse deleteAodEntryQueue(String index) throws TheDavidBoxClientException{
    	DeleteAodEntryQueueOperation operation = getTheDavidBoxClient().getModulePlayback().buildDeleteAodEntryQueueOperation(index);
		ResponseSimple response = operation.execute();
		return response;		
    }

}