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
package com.vikingbrain.dmt.service;

import java.util.List;

import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.nmt.responses.DavidBoxResponse;
import com.vikingbrain.nmt.responses.playback.ResponseListAodQueueInfo;
import com.vikingbrain.nmt.responses.system.ResponseGetDeviceInfo;
import com.vikingbrain.nmt.responses.system.ResponseGetNmtServiceStatus;
import com.vikingbrain.nmt.responses.system.ResponseListNmtServices;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Interface with service operation for davidbox service.
 * 
 * @author Rafael Iñigo
 */
public interface DavidBoxService {

	/**
	 * Check if a file can be opened in NMT.
	 * @param file the file to check
	 * @return if a file can be opened in NMT.
	 */
	boolean isOpenableInNmt(DMTFile file);

	/**
	 * Play audio file on NMT.
	 * http://popcorn:8008/playback?arg0=start_aod&arg1=nombre&arg2=file:///opt/sybhttpd/localhost.drives/SATA_DISK/ztesting/music/mp3.mp3&arg3=show
	 * @param file file the DMTFile
	 * @return the response object
	 * @throws TheDavidBoxClientException exception in the client
	 */
	DavidBoxResponse startAudioOnNMT(DMTFile file)  throws TheDavidBoxClientException;

	/**
	 * Starts any video, music or photo on NMT.
	 * It takes care of the file type and decide the corresponding operation. It will return null if the extension in not supported
	 * for playing the file on NMT.
	 * @param file the file to play on NMT
	 * @return the davidbox response or null if the extension is not supported for playing the file on NMT
	 * @throws TheDavidBoxClientException exception in the client
	 */
	DavidBoxResponse startGenericFileOnNMT(DMTFile file) throws TheDavidBoxClientException;

	/**
	 * Execute the key remote infrared command on NMT.
	 * play, stop, etc..
	 * @param keyName the key name
	 * @return response object from the client
	 * @throws TheDavidBoxClientException exception in the client
	 */
	DavidBoxResponse executeKeyIR(String keyName) throws TheDavidBoxClientException;

	/**
	 * It gets video, audio or photo that is currently playing on NMT.
	 * @return response object from the client
	 * @throws TheDavidBoxClientException exception in the client
	 */
	DavidBoxResponse getCurrentlyPlaying() throws TheDavidBoxClientException;	

	/**
	 * Get the devices information.
	 * @return the list of responses with all the devices info
	 * @throws TheDavidBoxClientException exception in the client
	 */
	List<ResponseGetDeviceInfo> getDiskSpaces() throws TheDavidBoxClientException;

	/**
	 * It checks if the NMT exists for the IP.
	 * @return if NMT exists for the IP of the client
	 * @throws TheDavidBoxClientException  exception in the client
	 */
	boolean checkNmtExist() throws TheDavidBoxClientException;

	/**
	 * Insert Audio, Video or Photo into the its right NMT queu. It will return null if the extension in not supported
	 * for playing the file on NMT.
	 * @param file file the file to enqueue.
	 * @return the davidbox response or null if the extension is not supported for playing the file on NMT
	 * @throws TheDavidBoxClientException exception in the client
	 */
	DavidBoxResponse insertGenericFileIntoNMTQueu(DMTFile file) throws TheDavidBoxClientException;

	/**
	 * List all NMT services.
	 * @return the response with the list of NMT services
	 * @throws TheDavidBoxClientException exception in the client
	 */
	ResponseListNmtServices listNmtServices() throws TheDavidBoxClientException;	

	/**	
	* Get status for a NMT service.
	* @return the response with the information
	* @throws DavidBoxClientException exception in the client
	*/	
	ResponseGetNmtServiceStatus getNmtServiceStatus(String serviceName) throws TheDavidBoxClientException;

	/**
	 * Set status for a NMT service.
	 * @param serviceName the service name
	 * @param status the new status
 	 * @return the response with the information
	* @throws DavidBoxClientException exception in the client
	 */
	DavidBoxResponse setNmtServiceStatus(String serviceName, boolean status) throws TheDavidBoxClientException;

    /**
     * List of audio files on NMT queue.
     * @return the response with the information
	 * @throws DavidBoxClientException exception in the client
     */
    ResponseListAodQueueInfo getListAodQueueInfo() throws TheDavidBoxClientException;

    /**
     * Delete any entry in the queue.
     * http://popcorn:8008/playback?arg0=delete_aod_entry_queue&arg1=index
     * @param index index to remove from the queue
	 * @throws DavidBoxClientException exception in the client
     */
    /**
     *  Delete audio entry from the audio queue.
     * @param index index to remove from the queue
     * @return the response with the information     
	 * @throws DavidBoxClientException exception in the client
     */
    DavidBoxResponse deleteAodEntryQueue(String index) throws TheDavidBoxClientException;

}
