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
 * Asynchronous task for picking a file or directory from FTP. 
 * 
 * @author Rafael Iñigo
 */
public class TaskFtpPickFileOrDirectory extends AsyncTask<Object, Void, DMTFile>{

	private OnResultTaskFtpPickFileOrDirectory mCallback;
	private FtpService ftpService;

	protected DMTFile currentDirectory;
	protected String filename;

	public TaskFtpPickFileOrDirectory(OnResultTaskFtpPickFileOrDirectory mCallback, FtpService ftpService) {
		super();
		this.mCallback = mCallback;
		this.ftpService = ftpService;
	}

	@Override
	protected DMTFile doInBackground(Object... params) {	
		
		currentDirectory = (DMTFile)params[0];
		filename = (String)params[1];
	
		DMTFile file = ftpService.getFile(currentDirectory.getAbsolutePath(), filename);										
		return file;
	}

	@Override
	protected void onPostExecute(DMTFile file){ //result
		if (null != mCallback){
			mCallback.onResultTaskFtpPickFileOrDirectory(file);
		}
	}

	// Container Activity must implement this interface
    public interface OnResultTaskFtpPickFileOrDirectory {
        void onResultTaskFtpPickFileOrDirectory(DMTFile file);
    }		

}
