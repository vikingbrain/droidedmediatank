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
 * Asynchronous task for starting a file (video, audio, photo) on NMT via TheDavidBox service. 
 * 
 * @author Rafael Iñigo
 */
public class TaskNmtStartGenericFile extends AsyncTask<Object, Void, NmtTaskResult>{
	
	private Context context;
	private OnResultTaskNmtStartGenericFile mCallback;
	private DavidBoxService davidBoxService;
	
	protected DMTFile _file;
		
	public TaskNmtStartGenericFile(Context context, OnResultTaskNmtStartGenericFile mCallback,
			DavidBoxService davidBoxService) {
		super();
		this.context = context;
		this.mCallback = mCallback;
		this.davidBoxService = davidBoxService;
	}

	@Override
	protected NmtTaskResult doInBackground(Object... params) {
		_file = (DMTFile) params[0];

		NmtTaskResult result = new NmtTaskResult();
		try {
			DavidBoxResponse response = davidBoxService.startGenericFileOnNMT(_file);
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
		} else if (null == result.getDavidBoxResponse()
				&& null != mCallback){
				//Null response means that it is not a nmt video, nmt audio or nmt photo extension
				//so it is not possible to play on nmt.		
				//notify the caller about the unsupported extension
				mCallback.onResultTaskNmtStartGenericFileUnsupportedExtension();			
		}		
	}
	
	// Container Activity must implement this interface
    public interface OnResultTaskNmtStartGenericFile {
        void onResultTaskNmtStartGenericFileUnsupportedExtension();
    }	
	
}
