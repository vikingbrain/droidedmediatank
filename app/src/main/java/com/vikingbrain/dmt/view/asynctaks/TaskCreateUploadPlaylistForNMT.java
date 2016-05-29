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

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.FtpService;
import com.vikingbrain.dmt.view.asynctaks.util.TaskResult;

/**
 * Asynchronous task for creating and uploading a music playlist file to NMT. The playlist file
 * can be opened in NMT.
 * 
 * @author Rafael Iñigo
 */
public class TaskCreateUploadPlaylistForNMT extends AsyncTask<Object, Void, TaskResult>{
	
	private static final String TAG = TaskCreateUploadPlaylistForNMT.class.getSimpleName();

	private Context context;
	private OnResultTaskCreateUploadPlaylistForNMT mCallback;
	private FtpService ftpService;
	
	private ProgressDialog dialog;
	
	protected DMTFile _file;
	protected File _fileDir;
	
	public TaskCreateUploadPlaylistForNMT(Context ctx, OnResultTaskCreateUploadPlaylistForNMT mCallback, FtpService ftpService) {		
	    this.context = ctx;
	    this.mCallback = mCallback;
		this.ftpService = ftpService;
		
		dialog = new ProgressDialog(context);
		dialog.setTitle(context.getString(R.string.title_create_upload_nmt_playlist));		
	}

	@Override
	protected void onPreExecute() {
		dialog.show();
	}

	@Override
	protected TaskResult doInBackground(Object... params) {
		_file = (DMTFile) params[0];
		_fileDir = (File) params[1];
		
		TaskResult taskResult = new TaskResult();
		
		try {
			ftpService.createUploadPlaylistForNMT(_file, _fileDir);			
		} catch (Exception e) {			
			DmtLogger.e(TAG, "error do in background", e);
			taskResult.setException(e);
		}		
		return taskResult;
	}			
	
	@Override
	protected void onPostExecute(TaskResult result) {
        if (dialog.isShowing()) {        	
        	//Dismiss the progress dialog because it's not possible to pause the spinning circle
            dialog.dismiss();
            
            AlertDialog.Builder dialogSummary = new AlertDialog.Builder(context);
    		dialogSummary.setTitle(context.getString(R.string.title_create_upload_nmt_playlist));    		
        	
        	if (null != result.getException()){
        		String messageException = result.getException().getMessage();
				if (!"".equals(messageException)) {
					dialogSummary.setMessage(context.getString(R.string.error_generic_exception_detail, messageException));
				} else {
					dialogSummary.setMessage(context.getString(R.string.error_generic));
				}
        	} else {
				dialogSummary.setMessage(context.getString(R.string.title_create_upload_nmt_playlist_done));
				//Refresh the file browser when dialog is dismissed
				dialogSummary.setOnCancelListener(new OnCancelListener(){
					
					@Override
					public void onCancel(DialogInterface arg0) {
						if (null != mCallback){
							mCallback.onResultTaskCreateUploadPlaylistForNMT();
						}
					}					
				});
			}
        	
        	//show the summary dialog
        	dialogSummary.show();
        }
	}			

	// Container Activity must implement this interface
    public interface OnResultTaskCreateUploadPlaylistForNMT {
        void onResultTaskCreateUploadPlaylistForNMT();
    }	

}
