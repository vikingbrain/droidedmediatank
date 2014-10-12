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
import com.vikingbrain.nmt.responses.system.ResponseListNmtServices;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Asynchronous task for listing NMT services. 
 * 
 * @author Rafael Iñigo
 */
public class TaskNmtListNmtServices extends AsyncTask<Object, Void, NmtTaskResult>{
	
	private Context context;
	private OnResultTaskNmtListNmtServices mCallback;
	private DavidBoxService davidBoxService;	
	
	public TaskNmtListNmtServices(Context context,  OnResultTaskNmtListNmtServices mCallback, DavidBoxService davidBoxService) {
		super();
		this.context = context;
		this.mCallback = mCallback;
		this.davidBoxService = davidBoxService;
	}

	@Override
	protected NmtTaskResult doInBackground(Object... params) {

		NmtTaskResult result = new NmtTaskResult();
		try {
			ResponseListNmtServices response = davidBoxService.listNmtServices();
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
			ResponseListNmtServices response = (ResponseListNmtServices)result.getDavidBoxResponse();
			mCallback.onResultTaskNmtListNmtServices(response);
		}
	}

	// Container Activity must implement this interface
    public interface OnResultTaskNmtListNmtServices {
        void onResultTaskNmtListNmtServices(ResponseListNmtServices response);
    }	

};	
