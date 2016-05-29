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
import com.vikingbrain.dmt.view.asynctaks.util.TaskResultDavidboxGetDevicesInfo;
import com.vikingbrain.dmt.view.util.UtilityView;
import com.vikingbrain.nmt.responses.system.ResponseGetDeviceInfo;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Asynchronous task for retrieving information about drives on NMT devices.
 * 
 * @author Rafael Iñigo
 */
public class TaskDavidboxGetDevicesInfo extends AsyncTask<Object, Void, TaskResultDavidboxGetDevicesInfo>{
	
	private Context context;
	private OnResultTaskDavidboxGetDevicesInfo mCallback;
	private DavidBoxService davidBoxService;	
	
	public TaskDavidboxGetDevicesInfo(Context context, OnResultTaskDavidboxGetDevicesInfo mCallback, DavidBoxService davidBoxService) {
		super();
		this.context = context;
		this.mCallback = mCallback;
		this.davidBoxService = davidBoxService;
	}

	@Override
	protected TaskResultDavidboxGetDevicesInfo doInBackground(Object... params) {

		TaskResultDavidboxGetDevicesInfo result = new TaskResultDavidboxGetDevicesInfo();
		
		try{
			List<ResponseGetDeviceInfo> listDeviceInfo = davidBoxService.getDiskSpaces();
			result.setListDeviceInfo(listDeviceInfo);
		} catch (TheDavidBoxClientException e) {
			result.setTheDavidBoxClientException(e);
		}
		return result;
	}

	@Override
	protected void onPostExecute(TaskResultDavidboxGetDevicesInfo result) {
		if (null != result.getTheDavidBoxClientException()){
			//Show to the user if there has been any problem with the operation
			UtilityView.showDialogOperationProblem(context, result);
		} else if (null != mCallback){
			//notify the call back
			mCallback.onResultTaskDavidboxGetDevicesInfo(result.getListDeviceInfo());
		}
	}

	// Container Activity must implement this interface
    public interface OnResultTaskDavidboxGetDevicesInfo {
        void onResultTaskDavidboxGetDevicesInfo(List<ResponseGetDeviceInfo> listDeviceInfo);
    }	

};	
