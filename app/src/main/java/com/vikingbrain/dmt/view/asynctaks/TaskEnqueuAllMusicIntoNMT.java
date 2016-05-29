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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.service.FtpService;
import com.vikingbrain.dmt.view.asynctaks.util.NmtTaskResult;
import com.vikingbrain.dmt.view.util.UtilityView;
import com.vikingbrain.nmt.responses.DavidBoxResponse;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Asynchronous task that enqueues all music files from a specified directory.
 * 
 * @author Rafael Iñigo
 */
public class TaskEnqueuAllMusicIntoNMT extends AsyncTask<Object, Integer, NmtTaskResult>{
	
	private static final String TAG = TaskEnqueuAllMusicIntoNMT.class.getSimpleName();
	
	private Context context;
	private DavidBoxService davidBoxService;
	private FtpService ftpService;
	
	protected DMTFile _file;
				
	private ProgressDialog dialog;
	private boolean isPlayFirst;

	private int numberMusicFiles;

	public TaskEnqueuAllMusicIntoNMT(Context ctx, DavidBoxService davidBoxService, FtpService ftpService, boolean isPlayFirst) {
	    this.context = ctx;
	    this.davidBoxService = davidBoxService;
	    this.ftpService = ftpService;
	    this.isPlayFirst = isPlayFirst;
	    
	    dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				
    	if (isPlayFirst){
    		dialog.setTitle(context.getString(R.string.title_start_new_queue_nmt));
    	} else {
    		dialog.setTitle(context.getString(R.string.title_add_music_nmt_queue));
    	}    			
    	dialog.setMessage(context.getString(R.string.title_loading));	
		
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // actually could set running = false; right here, but I'll
                // stick to contract.
                cancel(true);
            }
        });
	}

	@Override
	protected void onPreExecute() {
		dialog.show();
	}
	 
	@Override
	protected NmtTaskResult doInBackground(Object... params) {
		_file = (DMTFile)params[0];
		
		NmtTaskResult nmtTaskResult = new NmtTaskResult();

		try {
			//Get all music files from folder and subfolders
			List<DMTFile> listAllMusicFiles = ftpService.getAllMusicFilesQueueableRecursive(_file);
			numberMusicFiles = listAllMusicFiles.size();
		
			int count = 0;
			
			//Insert all music files in the list in the NMT queue 
			for (DMTFile musicFile : listAllMusicFiles){
				count++;
				
				if (isPlayFirst && count == 1){
					
					publishProgress(0);
					//It parses the response so the NMT is not overloaded with http requests
					DavidBoxResponse response = davidBoxService.startAudioOnNMT(musicFile);
					if (! response.isValid()){
						nmtTaskResult.setDavidBoxResponse(response);
						break;
					}
				} else {
					
					publishProgress((int) ((count * 100) / numberMusicFiles));
					//It parses the response so the NMT is not overloaded with http requests
					DavidBoxResponse response = davidBoxService.insertGenericFileIntoNMTQueu(musicFile);
					if (! response.isValid()){
						nmtTaskResult.setDavidBoxResponse(response);
						break;
					}
				}
				
				// Escape early if cancel() is called
	            if (isCancelled()) break;				
			}
		} catch (TheDavidBoxClientException e) {
			DmtLogger.e(TAG, "error do in background", e);
			nmtTaskResult.setTheDavidBoxClientException(e);
		}
		return nmtTaskResult;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (dialog.isShowing()) {
			int intProgress = progress[0];
			dialog.setProgress(intProgress);

			if (intProgress == 0){
				dialog.setMessage(context.getString(R.string.title_starting_music_file));
			} else {
				dialog.setMessage(context.getString(R.string.title_adding_files_nmt_queue));
			}
		}
    }
		   
	@Override
	protected void onPostExecute(NmtTaskResult result) {
        if (dialog.isShowing()) {    	        	
        	
    		if (UtilityView.isOperationProblem(result)){
        		//Dismiss because a new dialog is going to be shown
       			dialog.dismiss();
    			//Show to the user if there has been any problem with the operation
    			UtilityView.showDialogOperationProblem(context, result);
    		} else {
    			//Control if there is no files in the folder
    			if (0 == numberMusicFiles){
    				dialog.setMessage(context.getString(R.string.title_error_no_simple_music_files_found));
    			} else {
		        	if (isPlayFirst){
		        		dialog.setMessage(context.getString(R.string.title_start_new_queue_nmt_done, numberMusicFiles));
		        	} else {
		        		dialog.setMessage(context.getString(R.string.title_add_music_nmt_queue_done, numberMusicFiles));
		        	}
				}
        	}
        }
	}			
}
