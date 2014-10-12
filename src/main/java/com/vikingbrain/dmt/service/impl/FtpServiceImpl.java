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
package com.vikingbrain.dmt.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.pojo.TypeStreamer;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.pojo.util.UtilityFile;
import com.vikingbrain.dmt.service.FtpService;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.util.PlaylistCreator;
import com.vikingbrain.dmt.service.util.PlaylistStrategy;
import com.vikingbrain.dmt.service.util.PlaylistStrategyLlink;
import com.vikingbrain.dmt.service.util.PlaylistStrategyMyiHome;
import com.vikingbrain.dmt.service.util.PlaylistStrategyNMT;

public class FtpServiceImpl implements FtpService {

	/** Tag for logging. */
	private static final String TAG = FtpServiceImpl.class.getSimpleName();
	
	private String HOST;
	private Integer PORT;
	private String USERNAME;
	private String PASSWORD;	

	private static final String ENCODING = "UTF-8";
	
	private FileTransferClient client;
	private SettingsService settingsService;

	//Handler for printing in console
	private Handler handler;	
	
	/** "-a". Parameter -a will show the hidden files. */
	private static final String PARAM_SHOW_HIDDEN_FILES = "-a ";

	/** "." .Parameter list files current directory. */ 
	private static final String PARAM_CURRENT_DIRECTORY = ".";
		
	/** "-a ." .Parameter list files current directory including hidden files. */ 
	private static final String PARAM_CURRENT_DIRECTORY_INCLUDING_HIDDEN_FILES = PARAM_SHOW_HIDDEN_FILES + PARAM_CURRENT_DIRECTORY;

	public FtpServiceImpl(SettingsService settingsService) {
		super();
		this.settingsService = settingsService;
		this.client = new FileTransferClient();			
	}

	/** {@inheritDoc} */
	public Handler getHandler() {
		return handler;
	}

	/** {@inheritDoc} */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * It loads the FTP user configuration from bbdd.
	 */
	private void loadFtpConfiguration(){
		//Get the active profile
		Profile activeProfile = settingsService.getActiveProfile();
		HOST = activeProfile.getIpNmt();
		
		String puerto = activeProfile.getFtpPort();
		if (null != puerto && !"".equals(puerto)){
			PORT = Integer.valueOf(puerto);		
		} else {
			PORT = 21; //DEFAULT PORT
		}
		USERNAME = activeProfile.getFtpUser();
		PASSWORD = activeProfile.getFtpPassword();		
	}
	
	/**
	 * Connect and log on ftp user if user is not connected.
	 */
	private void connectAndLogonIfRequired() {
		loadFtpConfiguration();
		try{
			if (!client.isConnected()){
				
				printConsole("Connecting "+ HOST);
				printConsole("Port "+ PORT);
	
				client.setRemoteHost(HOST);
				client.setRemotePort(PORT);
				
				//Encoding
				client.getAdvancedSettings().setControlEncoding(ENCODING);
				
				//Default mode in AndFTP
				//Passive mode
				client.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
				printConsole("Added passive mode");
			}

			if (!client.isConnected()){
				printConsole("User: "+USERNAME);
				printConsole("Pass: HIDDEN");
				
				client.setUserName(USERNAME);
				client.setPassword(PASSWORD); 				
				
				client.connect();
				printConsole("STATUS: Authenticated");								
			}

		} catch (IllegalStateException e) {
			printExceptionConsole(e);					
		} catch (IOException e) {
			printExceptionConsole(e);					
		} catch (FTPException e) {
			printExceptionConsole(e);					
		}		
	}

	/** {@inheritDoc} */
	public void disconnect() {
		try{
			//Disconnect without sending any advice to the server 
			client.disconnect(true);
			printConsole("STATUS: Disconnected");
			
		} catch (IllegalStateException e) {
			printExceptionConsole(e);					
		} catch (IOException e) {
			printExceptionConsole(e);					
		} catch (FTPException e) {
			printExceptionConsole(e);					
		}
	}

	/** {@inheritDoc} */
	public DMTFile getFile(Uri uri) {
		DMTFile file = null;
		if (uri != null) {
			String filepath = uri.getPath();
			if (filepath != null) {
				file = getFile(uri.getPath());
			}
		}
		return file;
	}

	/**
	 * List files and directories in directory and subdirectories.
	 * @param path the path
	 * @return all files and directories in current directory and subdirectories
	 */
	private List<DMTFile> listFilesRecursive(String path) {
		List<DMTFile> list = new ArrayList<DMTFile>();
		DMTFile file = getFile(path); //Get the real file
		
		if (null != file ){ //It checks if the file exists
			if (! file.isDirectory()){
				list.add(file);
			} else {
				DMTFile[] files = listFilesWithoutConsoleMessage(file.getPath());
				for (int x = 0; x < files.length; x++) {
					DMTFile childFile = files[x];		   
					if (childFile.isDirectory()){
						list.addAll(listFilesRecursive(childFile.getPath()));					
					} else {
						list.add(childFile);
					}
				}
			}
		}
		return list;
	}

	/** {@inheritDoc} */
	public DMTFile[] listFiles(String path) {	
		DMTFile[] fileList = listFiles(path, true);
		printConsole("Found: "+fileList.length + " elements");
		return fileList;
	}

	/**
	 * List all files and directories without updating console info.
	 * @param path the file path
	 * @return the list of files
	 */
	private DMTFile[] listFilesWithoutConsoleMessage(String path) {	
		DMTFile[] fileList = listFiles(path, true);
		return fileList;
	}

	/**
	 * List all files and directories including hidden files or not.
	 * @param dir the directory
	 * @param showHidden if it is required to show hidden files
	 * @return all the files
	 */
	private DMTFile[] listFiles(String dir, boolean showHidden) {
		
		connectAndLogonIfRequired();

		List<DMTFile> fileList = new ArrayList<DMTFile>();

		try {
			
			FTPFile[] elements;
			
			client.changeDirectory(dir);
			if (showHidden){
				//It works, show all hidden files
				//Fixes folders with empty spaces was not listing files
				elements = client.directoryList(PARAM_CURRENT_DIRECTORY_INCLUDING_HIDDEN_FILES);
			} else {
				//Fixes to folders with empty spaces was not listing files
				elements = client.directoryList(PARAM_CURRENT_DIRECTORY);
			}
			
			DMTFile dmtFile;
			
			for(FTPFile element : elements){
				
				DmtLogger.d(TAG, "element: "+element.toString());
				
				String separator = "/";
				  if (dir.endsWith("/")) {
					  separator = "";
				  }
						
				int calculatedType = DMTFile.TYPE_FILE; //0
				if (element.isDir()){
					calculatedType = DMTFile.TYPE_DIRECTORY;
				} else if (element.isFile()){
					calculatedType = DMTFile.TYPE_FILE;
				} else if (element.isLink()){
					calculatedType = DMTFile.TYPE_LINK;
				}

				//Files "." and ".." are ignored
				if (!".".equals(element.getName())
						&& !"..".equals(element.getName())){
					dmtFile = new DMTFile(dir + separator + element.getName(),
							element.getLinkedName(),
							element.lastModified(),
							calculatedType,
							element.size());				
					fileList.add(dmtFile);
				}				
			}
			
		} catch (IllegalStateException e) {
			printExceptionConsole(e);					
		} catch (IOException e) {
			printExceptionConsole(e);					
		} catch (FTPException e) {
			printExceptionConsole(e);					
		} catch (ParseException e) {
			printExceptionConsole(e);
		}			
		
		return fileList.toArray(new DMTFile[]{});
	}
	
	/** {@inheritDoc} */
	public DMTFile getFile(String filePath) {
		DmtLogger.d(TAG, "Getting file by path " + filePath);		
		connectAndLogonIfRequired();
		
		DMTFile fileOnlyWithPath = new DMTFile(filePath);
		
		DMTFile fileFound = null;
		
		String pathParent = fileOnlyWithPath.getParent();			
		if (pathParent == null) {
			pathParent = "/"; //Root
		}
		
		DMTFile[] childList = listFilesWithoutConsoleMessage(pathParent);
		
		
		String pathSeekChild = fileOnlyWithPath.getPath();
		for(DMTFile child : childList){
			if (pathSeekChild.equals(child.getPath())){
				//child found
				fileFound = child;
			}			
		}		
		
		return fileFound;
	}

	/** {@inheritDoc} */
   public DMTFile getParentFile(DMTFile file) {
	   
	   connectAndLogonIfRequired();
	   
	   DMTFile root = DMTFile.getRootDMTFile();
	   
	   DMTFile parent = null;	   
       String tempParent = file.getParent();
       if (tempParent == null) {
    	   DmtLogger.d(TAG, "tempParent == null");
    	   parent = DMTFile.getRootDMTFile();
       } else if (root.getPath().equals(tempParent)){
    	   DmtLogger.d(TAG, "parent is root");
    	   parent = root;
       } else {
    	   DmtLogger.d(TAG, "parent result of seeking getFile. tempParent:"+tempParent);
    	   parent = getFile(tempParent);
       }
       return parent;	       	       
   }

   /** {@inheritDoc} */
	public DMTFile getFile(String curdir, String filename) {
		String separator = "/";
		  if (curdir.endsWith("/")) {
			  separator = "";
		  }
		  DMTFile clickedFile = getFile(curdir + separator + filename);
		return clickedFile;
	}

	/** {@inheritDoc} */	
	public DMTFile createMockDMTFile(String curdir, String filename) {
		String separator = "/";
		  if (curdir.endsWith("/")) {
			  separator = "";
		  }
		  //It only creates a mock DMTFile only with the path
		  DMTFile clickedFile = new DMTFile(curdir + separator + filename);
		  
		return clickedFile;
	}

	/** {@inheritDoc} */
	public DMTFile getFile(DMTFile curdir, String filename) {
		DMTFile file = getFile(curdir.getAbsolutePath(), filename);
		return file;
	}

	/** {@inheritDoc} */
	public DMTFile createMockDMTFile(DMTFile curdir, String filename) {
		DMTFile file = createMockDMTFile(curdir.getAbsolutePath(), filename);
		return file;
	}
	
	/** {@inheritDoc} */
	public DMTFile getPathWithoutFilename(DMTFile file) {
		DMTFile fileEncontrado = null;
		 if (file != null) {
			 if (file.isDirectory()) {
				 // no file to be split off. Return everything
				 return file;
			 } else {
				 String filename = file.getName();
				 String filepath = file.getAbsolutePath();
	  
				 // Construct path without file name.
				 String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
				 if (pathwithoutname.endsWith("/")) {
					 pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
				 }
				 fileEncontrado = getFile(pathwithoutname);
			 }
		 }
		 return fileEncontrado;
	}

	/** {@inheritDoc} */
	public boolean remaneFileOrFolder(DMTFile oldFile, String newName){
		boolean correcto = false;
		try {			
			String oldName = oldFile.getPath();
			DmtLogger.d(TAG, "Pre: client.rename con oldName:"+oldName +" y newName:"+newName);
			client.rename(oldName, newName);
			DmtLogger.d(TAG + "Post. MV or RM OK: ", newName);	
			correcto = true;
			printConsole("MV or RM OK: "+newName);
		} catch (IllegalStateException e) {
			printExceptionConsole(e);					
		} catch (IOException e) {
			printExceptionConsole(e);					
		} catch (FTPException e) {
			printExceptionConsole(e);					
		}
		return correcto;
	}

	/** {@inheritDoc} */
	public boolean moveFileOrFolder(DMTFile fileOrigin, String pathTarget){
		DmtLogger.d(TAG, "Pre. moveFileOrFolder");
		return remaneFileOrFolder(fileOrigin, pathTarget);
	}

	/** {@inheritDoc} */
	public boolean deleteFile(DMTFile file){
		boolean correcto = false;
		try {
			client.deleteFile(file.getPath());
			correcto = true;
			printConsole("DELETED: "+file.getPath());			
		} catch (IllegalStateException e) {
			printExceptionConsole(e);				
		} catch (IOException e) {
			printExceptionConsole(e);					
		} catch (FTPException e) {
			printExceptionConsole(e);					
		}
		return correcto;
	}		
	
	/** {@inheritDoc} */
	public boolean createDirectory(DMTFile file){
		boolean correcto = false;
		try {
			client.createDirectory(file.getPath());			
			correcto = true;
			printConsole("CREATED: "+file.getPath());
		} catch (IllegalStateException e) {
			printExceptionConsole(e);					
		} catch (IOException e) {
			printExceptionConsole(e);				
		} catch (FTPException e) {
			printExceptionConsole(e);				
		}
		return correcto;		
	}	
	
	/** {@inheritDoc} */
	public boolean deleteDirectory(DMTFile file){
		boolean correcto = false;
		try {
			client.deleteDirectory(file.getPath());			
			correcto = true;
			printConsole("DELETED: "+file.getPath());
		} catch (IllegalStateException e) {
			printExceptionConsole(e);					
		} catch (IOException e) {
			printExceptionConsole(e);					
		} catch (FTPException e) {
			printExceptionConsole(e);					
		}
		return correcto;		
	}
		
	/**
	 * Print text in the console.
	 * @param text the text to show
	 */
	private void printConsole(String text) {
		Message msg = getHandler().obtainMessage(MESSAGE_CONSOLE_CHANGED);
		msg.obj = "\n" + text ;
		msg.sendToTarget();		
	}
	
	/**
	 * Print exception information in the console.
	 * @param e the exception
	 */
	private void printExceptionConsole(Exception e) {
		
		if (null != e.getMessage()) {
			DmtLogger.d(TAG, e.getMessage());
			
			String textNonI18n = e.getMessage();
			DmtLogger.d(TAG + "CONSOLE: ", textNonI18n);					
			printConsole(textNonI18n);
		}				
	}

	/** {@inheritDoc} */
	public boolean isNmjExclude(String path, String excludeType){
		boolean existe = false;				
		try {
			//Access the directory
			client.changeDirectory(path);
			//Fix to folders with empty spaces was not listing files
			String[] allFilesFound = client.directoryNameList(PARAM_CURRENT_DIRECTORY_INCLUDING_HIDDEN_FILES, true);
			
			if (null != allFilesFound){				
				List<String> listNamesFilesAndDirectories = Arrays.asList(allFilesFound);
				for (String name : listNamesFilesAndDirectories){
					if (name.contains(excludeType)){						
						existe = true;
					}
				}
			}
			
		} catch (FTPException e) {
			printExceptionConsole(e);
		} catch (IOException e) {
			printExceptionConsole(e);
		}
		return existe;
	}
	
	/** {@inheritDoc} */
	public boolean enableNmjExclude(DMTFile currentDirectory, File excludeFile){
		//Tiene lugar en el directorio actual. 
		boolean correcto = false;
		try {
			
			//Muevo al directorio actual
			client.changeDirectory(currentDirectory.getPath());
			client.uploadFile(excludeFile.getPath(), excludeFile.getName());
			
			//Informaci������n consola
			String rutaCompleta = currentDirectory.getPath() + "/" + excludeFile.getName();
			printConsole("UPLOADED: "+rutaCompleta);
			
			correcto = true;
		} catch (IllegalStateException e) {
			printExceptionConsole(e);
		} catch (FileNotFoundException e) {
			printExceptionConsole(e);
		} catch (IOException e) {
			printExceptionConsole(e);
		} catch (FTPException e) {
			printExceptionConsole(e);
		}
		return correcto;
	}
	
	/** {@inheritDoc} */
	public boolean disableNmjExclude(DMTFile currentDirectory, String excludeType){
		//It happens in the current directory 
		boolean isSuccessful = false;
		try {
			//Create the mock file with the file name
			DMTFile fileMockToDelete = null;
			if (Constants.NMJ_NO_VIDEO.equals(excludeType)){
				fileMockToDelete = createMockDMTFile(currentDirectory, Constants.NMJ_NO_VIDEO);
			} else if (Constants.NMJ_NO_MUSIC.equals(excludeType)){
				fileMockToDelete = createMockDMTFile(currentDirectory, Constants.NMJ_NO_MUSIC);
			} else if (Constants.NMJ_NO_PHOTO.equals(excludeType)){
				fileMockToDelete = createMockDMTFile(currentDirectory, Constants.NMJ_NO_PHOTO);
			} else if (Constants.NMJ_NO_ALL.equals(excludeType)){
				fileMockToDelete = createMockDMTFile(currentDirectory, Constants.NMJ_NO_ALL);
			}

			//Let's move to the destiny directory
			client.changeDirectory(currentDirectory.getPath());
			//Process is successful if it can delete it
			isSuccessful = deleteFile(fileMockToDelete);
			
		} catch (IllegalStateException e) {
			printExceptionConsole(e);
		} catch (FileNotFoundException e) {
			printExceptionConsole(e);
		} catch (IOException e) {
			printExceptionConsole(e);
		} catch (FTPException e) {
			printExceptionConsole(e);
		}
		return isSuccessful;
	}

	/** {@inheritDoc} */
	public File createPlaylistForAndroid(SettingsService settingsService, DMTFile file, File fileDir, TypeStreamer streamer){

		File playlistLocalFile = null;		
		PlaylistCreator playlistCreator = null;
		
		try{
			//Strategy pattern. Create the playlist builder with the right strategy
			//because each strategy has its ows file name and the calculation links for m3u rows				
			if (TypeStreamer.MYIHOME == streamer){
				playlistCreator = new PlaylistCreator(new PlaylistStrategyMyiHome(settingsService));
			} else if (TypeStreamer.LLINK == streamer){
				playlistCreator = new PlaylistCreator(new PlaylistStrategyLlink(settingsService));
			}	
			
			//Get all files recursively also with subfolders for the folder
			List<DMTFile> files = getAllMusicFilesQueueableRecursive(file);

			//Create the playlist and save it on the appropieate file (it knows thanks to
			//the patter strategy
			playlistLocalFile = playlistCreator.buildM3UPlaylist(files, fileDir);				
		
		} catch (Exception e){
			printExceptionConsole(e);
		}
		return playlistLocalFile;
	}	
	
	/** {@inheritDoc} */
	public void createUploadPlaylistForNMT(DMTFile file, File localPlaylistFile) throws Exception {
		
		try {
			//Strategy pattern. Create the playlist builder with the right strategy
			//because each strategy has its ows file name and the calculation links for m3u rows 
			PlaylistStrategy playlistStrategy = new PlaylistStrategyNMT(file.getPath());
			PlaylistCreator playlistCreator = new PlaylistCreator(playlistStrategy);
			
			//Get all files recursively also with subfolders for the folder
			List<DMTFile> files = getAllMusicFilesQueueableRecursive(file);
	
			synchronized (this) {				
				//Temporary location for the nmt playlist file
				File playlistFile = playlistCreator.buildM3UPlaylist(files, localPlaylistFile);				
				//Go to the NMT remote directory where to put the playlist file
				client.changeDirectory(file.getPath());
				//Upload to the remote ftp server				
				client.uploadFile(playlistFile.getPath(), playlistFile.getName());

			}
		
	     } catch (Exception e){
	    	 printExceptionConsole(e);
	    	 throw e;
	     }
		
	}

	/** {@inheritDoc} */
	public List<DMTFile> getAllMusicFilesQueueableRecursive(DMTFile file){
		List<DMTFile> listAllFiles = listFilesRecursive(file.getPath());
		List<DMTFile> musicFilesQueuable = new ArrayList<DMTFile>();
		
		for (DMTFile dmtFile : listAllFiles){
			if (UtilityFile.isMusicQueuable(dmtFile.getName())) {
				musicFilesQueuable.add(dmtFile);
			}
		}
		return musicFilesQueuable;
	}

}