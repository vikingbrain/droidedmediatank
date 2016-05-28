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
package com.vikingbrain.dmt.view.asynctaks.util;

import java.util.List;

import com.vikingbrain.nmt.responses.system.ResponseGetDeviceInfo;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Response object for async task GetDeviceInfo. 
 * 
 * @author Rafael Iñigo
 */
public class TaskResultDavidboxGetDevicesInfo {
	
	private List<ResponseGetDeviceInfo> listDeviceInfo = null;
	private TheDavidBoxClientException theDavidBoxClientException = null;
		
	public final List<ResponseGetDeviceInfo> getListDeviceInfo() {
		return listDeviceInfo;
	}

	public final void setListDeviceInfo(List<ResponseGetDeviceInfo> listDeviceInfo) {
		this.listDeviceInfo = listDeviceInfo;
	}

	public final TheDavidBoxClientException getTheDavidBoxClientException() {
		return theDavidBoxClientException;
	}

	public final void setTheDavidBoxClientException(TheDavidBoxClientException theDavidBoxClientException) {
		this.theDavidBoxClientException = theDavidBoxClientException;
	}

}
