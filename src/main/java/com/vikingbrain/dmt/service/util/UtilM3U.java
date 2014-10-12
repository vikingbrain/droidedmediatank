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

import java.util.List;

import com.vikingbrain.dmt.pojo.DMTFile;

/**
 * Utility class for m3u playlist files. It will calculate the right path 
 * for every music file. The lines will be different depending on m3u is build 
 * for playing in Android using myIhome, Android using Llink, or NMT.
 * 
 * @author Rafael Iñigo
 */
public class UtilM3U {
	
	// Playlist strategy to use when building the playlist
	private PlaylistStrategy strategy;

	public static final String EXTM3U = "#EXTM3U";
	public static final String EXTINF = "#EXTINF";

	/**
	 * Constructor.
	 * @param strategy strategy for building the playlist
	 */
	public UtilM3U(PlaylistStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Build the m3u content from a list of files.
	 * @param files the list of files
	 * @return the m3u content
	 */
	public StringBuffer buildM3U(List<DMTFile> files) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(EXTM3U).append("\n");
 
		for (DMTFile file : files) {
			buffer.append(buildLine(file));
		}
		return buffer;
	}

	/**
	 * It builds a line for the playlist music file depending
	 * on the strategy provided for the playlist construction.
	 * @param file the file
	 * @return the line content
	 */
	private StringBuffer buildLine(DMTFile file) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(EXTINF);
		buffer.append(":");
		buffer.append("-1"); // It indicates it is a stream so it's not possible
								// to determine the audio length
		buffer.append(",");
		buffer.append(file.getName());
		buffer.append("\n");
		// Build the URL of the file that will be used to put in the playlist
		buffer.append(strategy.calculateUrlForFile(file));
		buffer.append("\n");
		return buffer;
	}

}
