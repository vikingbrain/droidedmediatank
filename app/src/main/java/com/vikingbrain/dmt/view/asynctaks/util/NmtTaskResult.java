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

import com.vikingbrain.nmt.responses.DavidBoxResponse;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Result object for async tasks over NMT services from TheDavidBox. 
 * 
 * @author Rafael Iñigo
 */
public class NmtTaskResult {
	
	private DavidBoxResponse davidBoxResponse = null;
	private TheDavidBoxClientException theDavidBoxClientException = null;
		
	public final DavidBoxResponse getDavidBoxResponse() {
		return davidBoxResponse;
	}
	
	public final void setDavidBoxResponse(DavidBoxResponse davidBoxResponse) {
		this.davidBoxResponse = davidBoxResponse;
	}		

	public final TheDavidBoxClientException getTheDavidBoxClientException() {
		return theDavidBoxClientException;
	}

	public final void setTheDavidBoxClientException(
			TheDavidBoxClientException theDavidBoxClientException) {
		this.theDavidBoxClientException = theDavidBoxClientException;
	}

}
