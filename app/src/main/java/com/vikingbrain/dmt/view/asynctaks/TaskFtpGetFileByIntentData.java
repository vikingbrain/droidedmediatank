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

import android.content.Intent;
import android.os.AsyncTask;

import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.service.FtpService;

/**
 * Asynchronous task for retrieving a file from an intent data.
 * 
 * @author Rafael Iñigo
 */
public class TaskFtpGetFileByIntentData extends AsyncTask<Object, Void, DMTFile>{
	
	private OnResultTaskFtpGetFileByIntentData mCallback;
	private FtpService ftpService;
	
	protected Intent intent;
	
	public TaskFtpGetFileByIntentData(OnResultTaskFtpGetFileByIntentData mCallback, FtpService ftpService) {
		super();
		this.mCallback = mCallback;
		this.ftpService = ftpService;
	}

	@Override
	protected DMTFile doInBackground(Object... params) {
		intent = (Intent) params[0];
		
		// Set current directory and file based on intent data.
		DMTFile file = ftpService.getFile(intent.getData());
		return file;
	}

	@Override
	protected void onPostExecute(DMTFile file){ //result
		if (null != mCallback){
			mCallback.onResultTaskFtpGetFileByIntentData(file);
		}
	}

	// Container Activity must implement this interface
    public interface OnResultTaskFtpGetFileByIntentData {
        void onResultTaskFtpGetFileByIntentData(DMTFile file);
    }		
}
