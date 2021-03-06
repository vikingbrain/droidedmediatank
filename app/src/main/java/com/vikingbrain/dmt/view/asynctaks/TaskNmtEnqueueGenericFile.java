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

import android.content.Context;
import android.os.AsyncTask;

import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.view.asynctaks.util.NmtTaskResult;
import com.vikingbrain.dmt.view.util.UtilityView;
import com.vikingbrain.nmt.responses.DavidBoxResponse;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Asynchronous task for adding a file (video, audio or photo) to NMT queue. 
 * 
 * @author Rafael Iñigo
 */
public class TaskNmtEnqueueGenericFile extends AsyncTask<Object, Void, NmtTaskResult>{
	
	private Context context;
	private DavidBoxService davidBoxService;
	
	protected DMTFile _file;		
	
	public TaskNmtEnqueueGenericFile(Context context, DavidBoxService davidBoxService) {
		super();
		this.context = context;
		this.davidBoxService = davidBoxService;
	}

	@Override
	protected NmtTaskResult doInBackground(Object... params) {
		_file = (DMTFile)params[0];

		NmtTaskResult result = new NmtTaskResult();
		
		try {
			DavidBoxResponse response = davidBoxService.insertGenericFileIntoNMTQueu(_file);
			result.setDavidBoxResponse(response);
		} catch (TheDavidBoxClientException e) {
			result.setTheDavidBoxClientException(e);
		}
		return result;			
	}

	@Override
	protected void onPostExecute(NmtTaskResult result) {
		if (UtilityView.isOperationProblem(result)){
			//Show to the user if there has been any problem with the operation
			UtilityView.showDialogOperationProblem(context, result);
		}
	}			
};	
