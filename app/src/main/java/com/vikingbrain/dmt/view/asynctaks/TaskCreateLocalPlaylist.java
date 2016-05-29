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

import android.os.AsyncTask;

import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.TypeStreamer;
import com.vikingbrain.dmt.service.FtpService;
import com.vikingbrain.dmt.service.SettingsService;

/**
 * Asynchronous task for starting a file (video, audio, photo) on NMT via TheDavidBox service. 
 * 
 * @author Rafael Iñigo
 */
public class TaskCreateLocalPlaylist extends AsyncTask<Object, Void, File>{

	private OnResultTaskCreateLocalPlaylist mCallback;	
	private FtpService ftpService;
	
	protected SettingsService settingsService;
	protected DMTFile mContextFile;
	protected File fileDir;
	protected TypeStreamer typeStreamer;
	
	
	public TaskCreateLocalPlaylist(OnResultTaskCreateLocalPlaylist mCallback, 
			FtpService ftpService, SettingsService settingsService) {
		super();
		this.mCallback = mCallback;
		this.ftpService = ftpService;
		this.settingsService = settingsService;
	}
	
	@Override
	protected File doInBackground(Object... params) {
		mContextFile = (DMTFile) params[0];
		fileDir = (File) params[1];
		typeStreamer = (TypeStreamer) params[2];
		
		File playlistLocalFile = ftpService.createPlaylistForAndroid(settingsService, mContextFile, fileDir, typeStreamer);
		return playlistLocalFile;
	}			
	@Override
	protected void onPostExecute(File playlistLocalFile){ //result
		if (null != mCallback){
			mCallback.onResultTaskCreateLocalPlaylist(playlistLocalFile);
		}
	}
	
	// Container Activity must implement this interface
    public interface OnResultTaskCreateLocalPlaylist {
        void onResultTaskCreateLocalPlaylist(File playlistLocalFile);
    }		
	
}
