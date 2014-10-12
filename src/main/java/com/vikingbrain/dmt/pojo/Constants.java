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
package com.vikingbrain.dmt.pojo;

import java.util.Arrays;
import java.util.List;

/**
 * Class with constants.
 * 
 * @author Rafael Iñigo
 */
public class Constants {
	
	/** Enable it if you want the application to print logs. This constant must be false when released to Production. */
	public static boolean DEBUG_ENABLED = true;
		
	/** String representing the primary key. */
	public static final String PRIMARY_KEY_ID = "_id";
	
	//Used to send intents that DMT can catch, it allows to send and get get filepaths between activities using the getData of the Intent
	public static final String SCHEMA_AND_HOST_DMT_NMT = "dmt://nmt";
	
	/** For settings Screen. */
	public static final String ICON_STAR = "iconStar";
	
	public static final String EMPTY_TEXT = "";
	
	//true number
	public static final int TRUE_NUMBER = 1;
	//false number
	public static final int FALSE_NUMBER = 0;
	
	//Numbers
	public static final int NUMBER_ZERO = 0;
	public static final int NUMBER_ONE = 1;
	public static final int NUMBER_TWO = 2;
	public static final int NUMBER_THREE = 3;

	//NMJ Excludes
	public static final String NMJ_NO_VIDEO = ".no_video.nmj";
	public static final String NMJ_NO_MUSIC = ".no_music.nmj";
	public static final String NMJ_NO_PHOTO = ".no_photo.nmj";
	public static final String NMJ_NO_ALL = ".no_all.nmj";
	
	public static final String FTP_NMT_DRIVE_NAME_DEFAULT = "/opt/sybhttpd/localhost.drives";
	
	/** List of video file extensions. */
	public static final List<String> NMT_DEVICES_WITHOUT_MYIHOME = Arrays.asList(
			"Popcorn Hour A300", "Popcorn Hour A400", "Popcorn Hour C300");
	public static final String MYIHOME_PORT_AND_STREAM = ":8088/stream/file=";
	public static final String LLINK_PORT_DEFAULT = "8001";

	//file name with the playlists
	public static final String PLAYLIST_MYIHOME_FILE_NAME = "myIhomePlaylist.m3u"; //FIXME m3u or m3u8 (UTF-8)
	public static final String PLAYLIST_LLINK_FILE_NAME = "llinkPlaylist.m3u"; //FIXME m3u or m3u8 (UTF-8)
	public static final String PLAYLIST_NMT_FILE_NAME = "playOnNMT.m3u"; //FIXME m3u or m3u8 (UTF-8)
	
	public static final String PACKAGE_RECOMMENDED_APP_FOR_PLAYLISTS = "net.sourceforge.servestream"; //ServeStream is recommended";

	public static int WAITING_TIME_SAME_KEY_IR_REMOTE = 2 * 1000; //2 seconds		

	//Transmission upload client
	public static final int BANDWIDTHPRIORITY_PER_TORRENT = 33;	
	public static final int DEFAULT_TRANSMISSION_PORT = 9091;

	//Assets for local web pages 
	public static final String PATH_ASSET_HTML_REMOTE_CONTROL_INDEX_TABLET = "file:///android_asset/www/index_tablet.html";
	public static final String PATH_ASSET_HTML_REMOTE_CONTROL_QUICKNAV = "file:///android_asset/www/quicknav.html";
	public static final String PATH_ASSET_HTML_REMOTE_CONTROL_NUMERIC = "file:///android_asset/www/numeric.html";
	public static final String PATH_ASSET_HTML_REMOTE_CONTROL_ADVANCED = "file:///android_asset/www/advanced.html";
	
	//Binding between local web page and java code
	public static final String BASE_URL_FOR_COMMAND = "http://dmt_send_key/";
}
