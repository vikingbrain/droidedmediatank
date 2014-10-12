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
package com.vikingbrain.dmt.pojo.util;

import java.util.Arrays;
import java.util.List;

import android.net.Uri;

import com.vikingbrain.dmt.pojo.DMTFile;

/**
 * Class with utilities for file extensions.
 * 
 * @author Rafael Iñigo
 */
public class UtilityFile {

	/** List of video file extensions. */
	private static final List<String> LIST_VIDEO_EXTENSIONS = Arrays.asList(
			".mpeg", ".mpg", ".mpe", ".qt", ".mov", ".avi", ".movie", ".mv", ".mp4",
			".asf", ".vob", ".ts", ".m2v", ".m2p", ".divx", ".xvid", ".dat",
			".m1v", ".m4v", ".dvr-msm2t", ".m2t", ".vro", ".hnl", ".trp", ".div",
			".xvi", ".rmp", ".rmp4", ".m2ts", ".mts", ".tp", ".iso", ".img", ".mkv",
			".vdr", ".evo", ".h264", ".mk3d");
	
	/** List of simple music file extensions. */
	private static final List<String> LIST_SIMPLE_MUSIC_FILE_EXTENSIONS = Arrays.asList(
			".wav", ".m4a", ".mpga", ".mp2", ".mp3", ".pcm", ".ogg", ".wma", ".mp1",
			".ac3", ".aac", ".mpa", ".dts", ".flac", ".mka");

	/** List of music playlist file extensions. */
	private static final List<String> LIST_MUSIC_PLAYLIST_EXTENSIONS = Arrays.asList(
			".m3u", ".pls");

	/** List of photo file extensions. */
	private static final List<String> LIST_PHOTO_EXTENSIONS = Arrays.asList(
			".jpeg", ".jpg", ".jpe", ".png", ".bmp", ".gif", ".tif", ".tif");
	
	/**
	 * Convert File into Uri (Android.net.Uri).
	 * @param file the file
	 * @return Uri the Uri
	 */	
	public static Uri getUri(DMTFile file) {
		if (file != null) {
			return Uri.parse(file.getAbsolutePath());			
		}
		return null;
	}	

	/**
	 * Gets the extension of a file name, like ".png" or ".jpg".
	 * 
	 * @param uri
	 * @return Extension including the dot("."); "" if there is no extension;
	 *         null if uri was null.
	 */
	public static final String getExtension(String uri) {
		if (uri == null) {
			return null;
		}

		int dot = uri.lastIndexOf(".");
		if (dot >= 0) {
			return uri.substring(dot).toLowerCase();
		} else {
			// No extension.
			return "";
		}
	}

	/**
	 * Checks if filename is a video.
	 * @param fileName the file name
	 * @return if it is a video
	 */
	public static final boolean isVideo(String fileName){
		boolean video = false;
		String extension = getExtension(fileName);
		if (LIST_VIDEO_EXTENSIONS.contains(extension)){
			video = true;
		}
		return video;
	}

	/**
	 * Check if filename is music.
	 * @param fileName the file name
	 * @return if it is music
	 */
	public static final boolean isMusic(String fileName){
		boolean music = false;
		String extension = getExtension(fileName);
		if (isMusicSimple(extension)
				|| isMusicPlaylist(extension)){
			music = true;
		}
		return music;
	}

	/**
	 * It is music but it is not a playlist file itselft. So it is possible
	 * to add it to a queue.
	 * @param fileName the file name
	 * @return if it is music but not a playlist file itself
	 */
	public static final boolean isMusicQueuable(String fileName){
		boolean musicQueuable = false;
		String extension = getExtension(fileName);
		if (isMusicSimple(extension)
				&& !isMusicPlaylist(extension)){
			musicQueuable = true;
		}
		return musicQueuable;
	}

	/**
	 * Check if extension is for a simple music file.
	 * @param extension the file extension
	 * @return if it is a simple music file
	 */
	private static final boolean isMusicSimple(String extension){
		boolean musicSimple = false;
		if (LIST_SIMPLE_MUSIC_FILE_EXTENSIONS.contains(extension)){
			musicSimple = true;
		}
		return musicSimple;
	}
	
	/**
	 * Check if extension is for a playlist.
	 * @param extension the file extension
	 * @return if it is a playlist
	 */
	private static final boolean isMusicPlaylist(String extension){
		boolean musicPlaylist = false;
		if (LIST_MUSIC_PLAYLIST_EXTENSIONS.contains(extension)){
			musicPlaylist = true;
		}
		return musicPlaylist;
	}

	/**
	 * Check if file name is a photo.
	 * @param fileName the file name
	 * @return if it is a photo
	 */
	public static final boolean isPhoto(String fileName){
		boolean photo = false;
		String extension = getExtension(fileName);
		if (LIST_PHOTO_EXTENSIONS.contains(extension)){
			photo = true;
		}
		return photo;
	}
	
	/**
	 * It solves the problem in xml responses because some fullPaths 
	 * are file:///opt/... and other are like /opt/...
	 * @param fullPath the full path
	 * @return the NMT absolute path
	 */
	public static String getMTAbsolutePath(String fullPath){
		String absolutePath = "";
		if (fullPath.startsWith("file://")){
			absolutePath = fullPath.replace("file://", "");
		} else {
			absolutePath = fullPath;
		}
		return absolutePath;
	}
	
	/**
	 * Checks if two paths have the same absolute path. 
	 * @param nmtPath1 the path
	 * @param nmtPath2 the other path
	 * @return if both has the same absolute path
	 */
	public static boolean isAbsolutePathsEqual(String nmtPath1, String nmtPath2){
		boolean equals = false;
		String absolutePath1 = getMTAbsolutePath(nmtPath1);
		String absolutePath2 = getMTAbsolutePath(nmtPath2);
		if (absolutePath1.equals(absolutePath2)){
			equals = true;
		}
		return equals;
	}	
}
