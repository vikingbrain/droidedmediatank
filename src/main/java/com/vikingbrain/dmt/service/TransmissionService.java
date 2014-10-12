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

import java.io.InputStream;
import java.util.List;

import ca.benow.transmission.model.AddedTorrentInfo;
import ca.benow.transmission.model.TorrentStatus;

import com.vikingbrain.dmt.service.util.TransmissionConnectionData;

/**
 * Interface with service operation for Transmission.
 * 
 * @author Rafael Iñigo
 */
public interface TransmissionService {

	/**
	 * Init transmission client.
	 * @param newUsername user name
	 * @param newPassword user password
	 */
	void initClient(String newUsername, String newPassword);
	
	/**
	 * Get transmission web app url.
	 * @return the url
	 */
	String getTransmissionWebUrl();
	
	/**
	 * Get all torrents from transmission.
	 * @return a list of torrents
	 * @throws Exception exception in the operation
	 */
	List<TorrentStatus> getAllTorrents() throws Exception;
	
	/**
	 * Add torrent to transmission (accepts torrent filename or URL).
	 * @param torrent the string with the torrent file name or URL
	 * @param startWhenAdded if start when added
	 * @return the information about added torrent
	 * @throws Exception exception in the operation
	 */
	AddedTorrentInfo addTorrent(String torrent, boolean startWhenAdded) throws Exception;

	/**
	 * Add torrent to transmission.
	 * @param in the input stream with the torrent
	 * @param startWhenAdded if start when added
	 * @return the information about added torrent
	 * @throws Exception exception in the operation
	 */
	AddedTorrentInfo addTorrent(InputStream in, boolean startWhenAdded) throws Exception;
		
	/**
	 * Get Transmission connection data.
	 * @return the transmission connection data
	 */
	TransmissionConnectionData getTransmissionConnectionData();

}
