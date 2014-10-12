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
package com.vikingbrain.dmt.service;

import java.io.File;
import java.util.List;

import android.net.Uri;
import android.os.Handler;

import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.TypeStreamer;

/**
 * Interface with service operation for FTP service.
 * 
 * @author Rafael Iñigo
 */
public interface FtpService {			
	
	//Code message for console has been changed (Console needs to refresh)
	int MESSAGE_CONSOLE_CHANGED = 10001;

	/**
	 * Get the handler.
	 * @return the handler
	 */
	Handler getHandler();
	
	/**
	 * Set the handler.
	 * @param handler the handler
	 */
	void setHandler(Handler handler);
	
	/**
	 * Disconnect from ftp session.
	 */
	void disconnect();

	/**
	 * Get a file from an uri.
	 * @param uri the uri
	 * @return the file
	 */
	DMTFile getFile(Uri uri);
	
	/**
	 * List all files and directories in a path.
	 * @param path the path
	 * @return all files and directories
	 */
	DMTFile[] listFiles(String path);
	
	/**
	 * Get a file from a file path.
	 * @param filePath the file path
	 * @return the file
	 */
	DMTFile getFile(String filePath);
	
	/**
     * Returns a new file made from the pathname of the parent of this file.
     * This is the path up to but not including the last name. {@code null} is
     * returned when there is no parent.
	 * @param file the file
	 * @return the parent file
	 */
	DMTFile getParentFile(DMTFile file);

	/**
	 * Get a file from the current directory.
	 * @param curdir current directory
	 * @param filename the file name
	 * @return the file if found
	 */
	DMTFile getFile(String curdir, String filename);	
	
	/**
	 * Creates a DMTFile only with the information about current directory
	 * and the filename.
	 * @param curdir the directory
	 * @param filename the filename
	 * @return a mock DMTFile
	 */
	DMTFile createMockDMTFile(String curdir, String filename);
	
	/**
	 * Get a file from the current directory.
	 * @param curdir a DMTFile
	 * @param filename the filename
	 * @return the file if found
	 */
	DMTFile getFile(DMTFile curdir, String filename);

	/**
	 * Creates a DMTFile only with the information about current directory
	 * and the filename.
	 * @param curdir the directory
	 * @param filename the filename
	 * @return a mock DMTFile
	 */
	DMTFile createMockDMTFile(DMTFile curdir, String filename);

	/**
	 * Returns the path only (without file name).
	 * @param file the DMTFile
	 * @return a DMTFile
	 */
	DMTFile getPathWithoutFilename(DMTFile file);	

	/**
	 * This method renames a remote file or directory. It can also be used to move a file or a directory. In example: 
 	 * client.rename("oldname", "newname"); // This one renames
   	 * client.rename("the/old/path/oldname", "/a/new/path/newname"); // This one moves
	 * @param oldFile the old name
	 * @param newName the new name
	 * @return if it was successful
	 */
	boolean remaneFileOrFolder(DMTFile oldFile, String newName);
	
	/**
	 * Move a file or folder.
	 * @param fileOrigen origin path
	 * @param pathDestino target path 
	 * @return if it was successful
	 */
	boolean moveFileOrFolder(DMTFile fileOrigin, String pathTarget);
	
	/**
	 * It deletes a file.
	 * To delete a remote file call:
	 * 
	 * client.deleteFile(relativeOrAbsolutePath);
	 * In example:
	 * 
	 * client.deleteFile("useless.txt");
	 *
	 * @param file the file to delete
	 * @return if it was successful
	 */
	boolean deleteFile(DMTFile file);

	/**
	 * Create a new directory (You can also remove an existing one).
	 * @param file the file to create
	 * @return if it was successful
	 */
	boolean createDirectory(DMTFile file);
	
	/**
	 * Delete a directory.
	 * Please note that usually FTP servers can delete only empty directories.
	 * @param file the file
	 * @return if it was successful
	 */
	boolean deleteDirectory(DMTFile file);

	/**
	 * If the directory has NMJ exclude for a explicit excludeType
	 * @param path the directory path
	 * @param excludeType .no_video.nmj, .no_music.nmj, .no_photo.nmj, .no_all.nmj or any other
	 * @return if it has the hidden file
	 */
	boolean isNmjExclude(String path, String excludeType);
	
	/**
	 * Enable a specific exclude for the directory uploading to the directory
	 * the exclude file.
	 * @param currentDirectory the directory
	 * @param excludeFile the exclude file to upload
	 * @return if it was successful
	 */
	boolean enableNmjExclude(DMTFile currentDirectory, File excludeFile);
	
	/**
	 * Disable a specific exclude for the directory.
	 * @param currentDirectory the directory
	 * @param excludeType the exclude type
	 * @return if it was successful
	 */
	boolean disableNmjExclude(DMTFile currentDirectory, String excludeType);
	
	/**
	 * Create a playlist with the music files of the file or directory.
	 * @param settingsService the service for the settings
	 * @param file the file
	 * @param fileDir the directory
	 * @param streamer the streamer type
	 * @return the file with the playlist
	 */
	File createPlaylistForAndroid(SettingsService settingsService, DMTFile file, File fileDir, TypeStreamer streamer);

	/**
	 * Create a playlist with the music files of the file or directory,
	 * first create it temporary in local Android storage,
	 * and then upload the playlist file to the ftp server. 
	 * @param file the ftp server folder where are the music files or subfolders
	 * @param localPlaylistFile playlist local file to storage temporaly the playlist
	 */
	void createUploadPlaylistForNMT(DMTFile file, File localPlaylistFile) throws Exception;
	
	/**
	 * Get all files that can be queued in a playlist. That is all basic simple
	 * music file extensions but not playlist files. This way is not possible to add
	 * playlist files to playlist files.
	 * @param file the file with the root
	 * @return a list with all simple music files
	 */
	List<DMTFile> getAllMusicFilesQueueableRecursive(DMTFile file);
		
}