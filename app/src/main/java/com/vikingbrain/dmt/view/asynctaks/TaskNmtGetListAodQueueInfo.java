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

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.view.asynctaks.util.NmtTaskResult;
import com.vikingbrain.dmt.view.util.UtilityView;
import com.vikingbrain.nmt.responses.playback.ObjectQueueElement;
import com.vikingbrain.nmt.responses.playback.ResponseListAodQueueInfo;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Asynchronous task for getting the list of enqueued audio files. 
 * 
 * @author Rafael Iñigo
 */
public class TaskNmtGetListAodQueueInfo extends AsyncTask<Object, Void, NmtTaskResult>{
	
	private Context context;
	private OnResultTaskNmtGetListAodQueueInfo mCallback;
	private DavidBoxService davidBoxService;	
	
	public TaskNmtGetListAodQueueInfo(Context context, OnResultTaskNmtGetListAodQueueInfo mCallback, 
			DavidBoxService davidBoxService) {
		super();
		this.context = context;
		this.mCallback = mCallback;
		this.davidBoxService = davidBoxService;
	}

	@Override
	protected NmtTaskResult doInBackground(Object... params) {
		
		NmtTaskResult result = new NmtTaskResult();
		try {		
			ResponseListAodQueueInfo response = davidBoxService.getListAodQueueInfo();
			result.setDavidBoxResponse(response);
		} catch (TheDavidBoxClientException e) {
			result.setTheDavidBoxClientException(e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(NmtTaskResult result) {
		if (null != result.getTheDavidBoxClientException()){
			//Show to the user if there has been any problem with the operation
			UtilityView.showDialogOperationProblem(context, result);
		} else if (null != mCallback){
			ResponseListAodQueueInfo response = (ResponseListAodQueueInfo)result.getDavidBoxResponse();
			//If the playlist is empty on NMT the response is not valid
			if (response.isValid()){
				mCallback.onResultTaskNmtGetListAodQueueInfo(response.getQueue());
			} else {
				//Response is not valid so there is not any music list playing
				mCallback.onResultTaskNmtGetListAodQueueInfo(null);
			}
		}
	}

	// Container Activity must implement this interface
    public interface OnResultTaskNmtGetListAodQueueInfo {
        void onResultTaskNmtGetListAodQueueInfo(List<ObjectQueueElement> queue);
    }	

}	
