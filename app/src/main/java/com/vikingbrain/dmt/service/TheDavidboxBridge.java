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

import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.nmt.client.TheDavidBoxClient;
import com.vikingbrain.nmt.client.impl.TheDavidBoxClientImpl;
import com.vikingbrain.nmt.util.ClientOptions;

/**
 * Bridge pattern to decouple TheDavidBoxClient implementation.
 * 
 * @author Rafael Iñigo
 */
public class TheDavidboxBridge {

	/** TheDavidBoxclient client library. */
	private TheDavidBoxClient theDavidBoxClient = null;

	/**
	 * Creates a new instance of TheDavidBoxClient for the active profile settings.
	 * @param activeProfile the active profile
	 * @return the davidbox client
	 */
	public TheDavidBoxClient getTheDavidBoxClientForActiveProfile(Profile activeProfile){
		//Get IP of NMT for the profile
		String remoteHostActiveProfile = activeProfile.getIpNmt();
		if (null == theDavidBoxClient){
			//Create a client with the custom client options
			ClientOptions clientOptions = new ClientOptions.Builder().build();
			theDavidBoxClient = new TheDavidBoxClientImpl(remoteHostActiveProfile, clientOptions);
		} else {
			theDavidBoxClient.setRemoteHost(remoteHostActiveProfile);
		}
		return theDavidBoxClient;
	}

}
