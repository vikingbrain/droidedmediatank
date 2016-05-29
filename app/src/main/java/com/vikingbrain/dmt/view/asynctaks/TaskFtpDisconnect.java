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

import com.vikingbrain.dmt.service.FtpService;

import android.os.AsyncTask;

/**
 * Asynchronous task for disconnecting from the FTP connection.
 * 
 * @author Rafael Iñigo
 */
public class TaskFtpDisconnect extends AsyncTask<Object, Void, Void>{	
	
	private FtpService ftpService;
		
	public TaskFtpDisconnect(FtpService ftpService) {
		super();
		this.ftpService = ftpService;
	}

	@Override
	protected Void doInBackground(Object... params) {
		ftpService.disconnect();
		return null;
	}
};
