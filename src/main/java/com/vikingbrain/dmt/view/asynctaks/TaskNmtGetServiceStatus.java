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

import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.view.asynctaks.util.NmtTaskResult;
import com.vikingbrain.dmt.view.util.UtilityView;
import com.vikingbrain.nmt.responses.system.ResponseGetNmtServiceStatus;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Asynchronous task for getting the status of a NMT service. 
 * 
 * @author Rafael Iñigo
 */
public class TaskNmtGetServiceStatus extends AsyncTask<Object, Void, NmtTaskResult>{
	
	private Context context;
	private OnResultTaskNmtGetServiceStatus mCallback;
	private DavidBoxService davidBoxService;	
	
	protected String _serviceName;
	
	public TaskNmtGetServiceStatus(Context context,  OnResultTaskNmtGetServiceStatus mCallback, DavidBoxService davidBoxService) {
		super();
		this.context = context;
		this.mCallback = mCallback;
		this.davidBoxService = davidBoxService;
	}

	@Override
	protected NmtTaskResult doInBackground(Object... params) {
		_serviceName = (String)params[0];		
		NmtTaskResult result = new NmtTaskResult();
	
		try {
			ResponseGetNmtServiceStatus response = davidBoxService.getNmtServiceStatus(_serviceName);
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
		} else if (null != mCallback){
			ResponseGetNmtServiceStatus response = (ResponseGetNmtServiceStatus)result.getDavidBoxResponse();
			mCallback.onResultTaskNmtGetServiceStatus(_serviceName, response);
		}		
	}

	// Container Activity must implement this interface
    public interface OnResultTaskNmtGetServiceStatus {
        void onResultTaskNmtGetServiceStatus(String serviceName, ResponseGetNmtServiceStatus response);
    }	

};	
