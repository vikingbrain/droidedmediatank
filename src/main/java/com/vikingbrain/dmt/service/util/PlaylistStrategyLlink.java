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
package com.vikingbrain.dmt.service.util;

import java.io.File;

import android.net.Uri;

import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.service.SettingsService;

/**
 * Playlist strategy for Llink.
 * 
 * @author Rafael Iñigo
 */
public class PlaylistStrategyLlink implements PlaylistStrategy {

	/**
	 * Service with the settings operations.
	 */
	private SettingsService settingsService;		
	
	/**
	 * Constructor. Param is necessary to calculate the strem urls in the playlsit.
	 * @param settingsService the settings service
	 */
	public PlaylistStrategyLlink(SettingsService settingsService) {
		super();
		this.settingsService = settingsService;
	}

	/** {@inheritDoc} */
	public File getPlaylistTarget(File fileDir){
		File playlistFile = new File(fileDir, Constants.PLAYLIST_LLINK_FILE_NAME);
		return playlistFile;
	}

	/** {@inheritDoc} */
	public String calculateUrlForFile(DMTFile file){
		Uri streamUri = UtilProfile.getUriLlinkStream(file, settingsService.getActiveProfile());
		return streamUri.toString();
	}

}
