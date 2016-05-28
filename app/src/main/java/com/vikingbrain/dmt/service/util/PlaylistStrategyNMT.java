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

import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.DMTFile;

/**
 * Playlist strategy for NMT files.
 * 
 * @author Rafael Iñigo
 */
public class PlaylistStrategyNMT implements PlaylistStrategy {

	/** 
	 * It is use to calculate the relative paths.
	 * So if the nmt folder with the playlist files moves, the playlist
	 * file will still works because it uses relative paths.
	 */
	private String initialPath;
	
	/**
	 * 	Constructor. It's necessary the initial path to build
	 * the appropiate relative paths later
	 * @param initialPath base path that will be ommited in links
	 * so they are gonna be relative
	 */
	public PlaylistStrategyNMT(String initialPath) {
		super();
		this.initialPath = initialPath + "/";
	}

	/** {@inheritDoc} */	
	public File getPlaylistTarget(File fileDir){
		File playlistFile = new File(fileDir, Constants.PLAYLIST_NMT_FILE_NAME);
		return playlistFile;
	}

	/** {@inheritDoc} */
	public String calculateUrlForFile(DMTFile file){
	   //Let's calculate the relative path to the initial folder 
	   String relativePath = file.getPath().replace(initialPath, "");					   
	   return relativePath;
	}

}
