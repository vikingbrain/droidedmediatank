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
package com.vikingbrain.dmt.view.asynctaks;

import android.os.AsyncTask;

import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Asynchronous task for sending a remote control key to NMT device. 
 * 
 * @author Rafael Iñigo
 */
public class TaskNmtExecuteKeyIR extends AsyncTask<Object, Void, Void>{
	
	/** Tag for logging. */
	private static final String TAG = TaskNmtExecuteKeyIR.class.getSimpleName();
	
	private DavidBoxService davidBoxService;
	
	protected String _keyName;	
	
	public TaskNmtExecuteKeyIR(DavidBoxService davidBoxService) {
		super();
		this.davidBoxService = davidBoxService;
	}

	@Override
	protected Void doInBackground(Object... params) {
		_keyName = (String)params[0];
	
		try {
			davidBoxService.executeKeyIR(_keyName);
		} catch (TheDavidBoxClientException e) {
			DmtLogger.d(TAG, "exception to ignore");
		}
		return null;
	}

};			
