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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import ca.benow.transmission.TransmissionClient;
import ca.benow.transmission.model.AddedTorrentInfo;
import ca.benow.transmission.model.TorrentStatus;
import ca.benow.transmission.model.TransmissionSession.SessionField;

import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.Link;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.LinkService;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.TransmissionService;
import com.vikingbrain.dmt.service.util.TransmissionConnectionData;
import com.vikingbrain.dmt.service.util.UtilProfile;

/**
 * Service operations for Transmission.
 * 
 * @author Rafael Iñigo
 */
public class TransmissionServiceImpl implements TransmissionService {

	/** Tag for logging. */
	private static final String TAG = TransmissionServiceImpl.class.getSimpleName();

	/** Services required. */
	private SettingsService settingsService;
	private LinkService linkService;
	
	/** The Transmission client. */
	private TransmissionClient client;
	
	/**
	 * Constructor.
	 * @param _settingsService the settings service
	 * @param _linkService the link service
	 */
	public TransmissionServiceImpl(SettingsService _settingsService, LinkService _linkService) {
		super();
		this.settingsService = _settingsService;
		this.linkService = _linkService;
	}	

	/** {@inheritDoc} */
	public TransmissionConnectionData getTransmissionConnectionData() {
		TransmissionConnectionData transmissionConnectionData = new TransmissionConnectionData();

		//Get the link for transmission
		Link transmissionLink = linkService.getTransmissionLink();

		transmissionConnectionData.setUser(transmissionLink.getUserName());
		transmissionConnectionData.setPassword(transmissionLink.getPassword());
		transmissionConnectionData.setAuthentication(transmissionLink.isAuthentication());

		String transmissionUrl = getTransmissionWebUrl();
		try {
			//Calculate host and port from transmission because it is gonna
			//use this url host and port to upload the file
			URL urlTransmissionWeb = new URL(transmissionUrl);
			
			String host = urlTransmissionWeb.getHost();
			int port = urlTransmissionWeb.getPort();
	
			if (!"".equals(host)
				&& -1 == port){
					// default port (9091) with the default user/pass (transmission/transmission)
					port = Constants.DEFAULT_TRANSMISSION_PORT;		
			}
			transmissionConnectionData.setHost(host);
			transmissionConnectionData.setPort(port);		
		} catch (MalformedURLException e){
			transmissionConnectionData.setTextWrongData(e.getMessage());
		}
		return transmissionConnectionData;
	}

	/** {@inheritDoc} */
	public String getTransmissionWebUrl(){
		Profile activeProfile = settingsService.getActiveProfile();		
		String ipOrDomain = UtilProfile.getIpOrDomainForWebClients(activeProfile);
		String urlVerdadera = "http://" + ipOrDomain + linkService.getTransmissionLink().getUrlEnd();
		return urlVerdadera;
	}
		
	/** {@inheritDoc} */
	public void initClient(String newUsername, String newPassword){
		//info: URL for client is something like http://transmission:transmission@192.168.1.118:9091/transmission/rpc
		 TransmissionConnectionData tcd = getTransmissionConnectionData();
		 if (tcd.isAuthentication()){
			 //Secured call
			 client = new TransmissionClient(tcd.getHost(), tcd.getPort(), newUsername, newPassword);
		 } else {
			 //Normal call
			 client = new TransmissionClient(tcd.getHost(), tcd.getPort());
		 }
	}	
		
	/** {@inheritDoc} */
	public List<TorrentStatus> getAllTorrents() throws Exception {
		List<TorrentStatus> torrents = null;
		torrents = client.getAllTorrents();
		return torrents;
	}
		
	/** {@inheritDoc} */
	public AddedTorrentInfo addTorrent(String torrent, boolean startWhenAdded) throws Exception{
		AddedTorrentInfo addedTorrent = null;
		boolean isPaused = !startWhenAdded;

		Map<SessionField, Object> parameters = client.getSession();
		String downloadDir = (String)parameters.get(SessionField.downloadDir);
		int peerLimit = (Integer)parameters.get(SessionField.peerLimitPerTorrent);						
		int bandwdithPriority = Constants.BANDWIDTHPRIORITY_PER_TORRENT;
		
		addedTorrent = client.addTorrent(downloadDir, torrent, isPaused, peerLimit, 
					bandwdithPriority, null, null, null, null, null);		
		DmtLogger.d(TAG, "Torrent added");
		
		return addedTorrent;
	}

	/** {@inheritDoc} */
	public AddedTorrentInfo addTorrent(InputStream in, boolean startWhenAdded) throws Exception{
		AddedTorrentInfo addedTorrent = null;
		boolean isPaused = !startWhenAdded;

		Map<SessionField, Object> parameters = client.getSession();			
		String downloadDir = (String)parameters.get(SessionField.downloadDir);
		int peerLimit = (Integer)parameters.get(SessionField.peerLimitPerTorrent);						
		int bandwdithPriority = Constants.BANDWIDTHPRIORITY_PER_TORRENT;
				
		addedTorrent = client.addTorrent(downloadDir, in, isPaused, peerLimit, 
					bandwdithPriority, null, null, null, null, null);
		DmtLogger.d(TAG, "Torrent added");
		
		return addedTorrent;
	}

}