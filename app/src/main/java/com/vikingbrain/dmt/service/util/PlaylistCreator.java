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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;

/**
 * It uses the playlist strategies to build the different playlists.
 * 
 * @author Rafael Iñigo
 */
public class PlaylistCreator {

	/** Tag for logging. */
	private static final String TAG = PlaylistCreator.class.getSimpleName();
	
	/** The playlist strategy that will be used. */
	private PlaylistStrategy strategy;

	/**
	 * Constructor.
	 * @param strategy the strategy to build the playlist
	 */
	public PlaylistCreator(PlaylistStrategy strategy) {
		super();
		this.strategy = strategy;
	}
	
	/**
	 * Write the string buffer into the destination file
	 * @param destination the destination file
	 * @param buffer the string buffer
	 * @throws IOException exception in the operation
	 */
	private void writeFile(File destination, StringBuffer buffer) throws IOException{
		FileWriter fwriter = new FileWriter(destination);
        BufferedWriter bwriter = new BufferedWriter(fwriter);
        bwriter.write(buffer.toString());
        bwriter.close();		
	}
	
	/**
	 * It builds a m3u playlist with the files provided using the specified strategy.
	 * @param files the music files
	 * @return the stringbuffer with the playlist content
	 */
	private StringBuffer buildM3u(List<DMTFile> files){
		UtilM3U m3u = new UtilM3U(strategy);
		StringBuffer buffer = m3u.buildM3U(files);
		DmtLogger.d(TAG,"buffer: " + buffer);
		return buffer;		
	}
	
	/**
	 * It builds and save a m3u playlist file using the specified strategy.
	 * @param files the music files
	 * @param fileDir the file dir
	 * @return the playlist file
	 * @throws IOException exception in the operation
	 */
	public File buildM3UPlaylist(List<DMTFile> files, File fileDir) throws IOException{
		StringBuffer buffer = buildM3u(files);
		File playlistTarget = strategy.getPlaylistTarget(fileDir);
		writeFile(playlistTarget, buffer);
		return playlistTarget;
	}
	
}
