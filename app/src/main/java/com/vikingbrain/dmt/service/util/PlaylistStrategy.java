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

import com.vikingbrain.dmt.pojo.DMTFile;

/**
 * Interface for the playlist strategies.
 * 
 * @author Rafael Iñigo
 */
public interface PlaylistStrategy {

	/**
	 * Playlist file on the Android local storage.
 	 * @param fileDir local Android directory where playlist
 	 * files where created initially
	 * @return el local file
	 */
	File getPlaylistTarget(File fileDir);
	
	/**
	 * It will calculate the right path for every music file
	 * @param file the file to add to the row of the playlist
	 * @return line to be writen on the playlist file
	 */
	String calculateUrlForFile(DMTFile file);
	
}
