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

import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.service.FtpService;

/**
 * Asynchronous task for retrieving a file.
 * 
 * @author Rafael Iñigo
 */
public class TaskFtpGetFileBundleContextFile extends AsyncTask<Object, Void, DMTFile>{

	private OnResultTaskFtpGetFileBundleContextFile mCallback;
	private FtpService ftpService;

	protected String bundleContextFile;
	protected DMTFile mContextFile;

	public TaskFtpGetFileBundleContextFile(OnResultTaskFtpGetFileBundleContextFile mCallback, FtpService ftpService) {
		super();
		this.mCallback = mCallback;
		this.ftpService = ftpService;
	}

	@Override
	protected DMTFile doInBackground(Object... params) {			
		bundleContextFile = (String)params[0];

		if ("".equals(bundleContextFile)) {
			mContextFile = new DMTFile("");
		} else {
			mContextFile = ftpService.getFile(bundleContextFile);
		}
		return mContextFile;
	}

	@Override
	protected void onPostExecute(DMTFile file){ //result
		if (null != mCallback){
			mCallback.onResultTaskFtpGetFileBundleContextFile(file);
		}
	}

	// Container Activity must implement this interface
    public interface OnResultTaskFtpGetFileBundleContextFile {
        void onResultTaskFtpGetFileBundleContextFile(DMTFile _mContextFile);
    }		

}
