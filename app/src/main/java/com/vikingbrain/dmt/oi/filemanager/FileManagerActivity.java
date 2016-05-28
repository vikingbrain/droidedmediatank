/* 
 * Copyright (C) 2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Based on AndDev.org's file browser V 2.0.
 */

package com.vikingbrain.dmt.oi.filemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.oi.filemanager.util.MimeTypeParser;
import com.vikingbrain.dmt.oi.filemanager.util.MimeTypes;
import com.vikingbrain.dmt.oi.intents.FileManagerIntents;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.pojo.TypeStreamer;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.pojo.util.UtilityFile;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.service.FtpService;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.util.UtilProfile;
import com.vikingbrain.dmt.view.HomeActivity;
import com.vikingbrain.dmt.view.InfoActivity;
import com.vikingbrain.dmt.view.asynctaks.TaskCreateLocalPlaylist;
import com.vikingbrain.dmt.view.asynctaks.TaskCreateUploadPlaylistForNMT;
import com.vikingbrain.dmt.view.asynctaks.TaskEnqueuAllMusicIntoNMT;
import com.vikingbrain.dmt.view.asynctaks.TaskFtpDisconnect;
import com.vikingbrain.dmt.view.asynctaks.TaskNmtEnqueueGenericFile;
import com.vikingbrain.dmt.view.asynctaks.TaskNmtStartGenericFile;

public class FileManagerActivity extends SherlockListActivity implements
			TaskCreateUploadPlaylistForNMT.OnResultTaskCreateUploadPlaylistForNMT,
			TaskNmtStartGenericFile.OnResultTaskNmtStartGenericFile,
//FUCK			TaskFtpGetFileByIntentData.OnResultTaskFtpGetFileByIntentData,
//			TaskFtpGetParentFile.OnResultTaskFtpGetParentFile,
//			TaskFtpGetFileBundleDirectory.OnResultTaskFtpGetFileBundleDirectory,
//			TaskFtpGetFileBundleContextFile.OnResultTaskFtpGetFileBundleContextFile,
//			TaskFtpPickFileOrDirectory.OnResultTaskFtpPickFileOrDirectory,
			TaskCreateLocalPlaylist.OnResultTaskCreateLocalPlaylist {

	private static final String TAG = FileManagerActivity.class.getSimpleName();

	private FtpService ftpService;
	private SettingsService settingsService;
	private DavidBoxService davidBoxService;	
	
	private int mState;

	private static final int STATE_BROWSE = 1;
	private static final int STATE_PICK_FILE = 2;
	private static final int STATE_PICK_DIRECTORY = 3;
	private static final int STATE_MULTI_SELECT = 4;

	protected static final int REQUEST_CODE_MOVE = 1;
	protected static final int REQUEST_CODE_COPY = 2;

	/**
	 * @since 2011-02-11
	 */
	private static final int REQUEST_CODE_MULTI_SELECT = 3;

	private static final int MENU_DELETE = Menu.FIRST + 5;
	private static final int MENU_RENAME = Menu.FIRST + 6;
	private static final int MENU_SEND = Menu.FIRST + 7;
	private static final int MENU_OPEN = Menu.FIRST + 8;
	private static final int MENU_MOVE = Menu.FIRST + 9;
	private static final int MENU_COPY = Menu.FIRST + 10;

	private static final int DIALOG_NEW_FOLDER = 1;
	private static final int DIALOG_DELETE = 2;
	private static final int DIALOG_RENAME = 3;

	/**
	 * @since 2011-02-12
	 */
	private static final String BUNDLE_CURRENT_DIRECTORY = "current_directory";
	private static final String BUNDLE_CONTEXT_FILE = "context_file";
	private static final String BUNDLE_CONTEXT_TEXT = "context_text";
	private static final String BUNDLE_STEPS_BACK = "steps_back";

	/** Contains directories and files together */
	private ArrayList<IconifiedText> directoryEntries = new ArrayList<IconifiedText>();

	/** Dir separate for sorting */
	List<IconifiedText> mListDir = new ArrayList<IconifiedText>();

	/** Files separate for sorting */
	List<IconifiedText> mListFile = new ArrayList<IconifiedText>();

	/** SD card separate for sorting */
	List<IconifiedText> mListSdCard = new ArrayList<IconifiedText>();

	private DMTFile currentDirectory = new DMTFile("");

	private String mSdCardPath = "";

	private MimeTypes mMimeTypes;

	private String mContextText;
	private DMTFile mContextFile = new DMTFile("");
	private Drawable mContextIcon;

	/** How many steps one can make back using the back key. */
	private int mStepsBack;

	private EditText mEditFilename;
	private Button mButtonPick;
	private LinearLayout mDirectoryButtons;

    private boolean fileDeleted = false;
    private int positionAtDelete;
    private boolean deletedFileIsDirectory = false;

	private LinearLayout mDirectoryInput;
	private EditText mEditDirectory;

	/**
	 * @since 2011-02-11
	 */
	private LinearLayout mActionMultiselect;

	private TextView mEmptyText;
	private ProgressBar mProgressBar;

	private DirectoryScanner mDirectoryScanner;
	private DMTFile mPreviousDirectory;
	private ThumbnailLoader mThumbnailLoader;	

	private Handler currentHandler;

	private boolean mWritableOnly;

	// List of contents is ready, obj = DirectoryContents
	static final public int MESSAGE_SHOW_DIRECTORY_CONTENTS = 500; 
	// Set progress bar, arg1 = current value, arg2 = max value
	static final public int MESSAGE_SET_PROGRESS = 501; 
	// View needs to be redrawn, obj = IconifiedText
	static final public int MESSAGE_ICON_CHANGED = 502; 

	static final public int MESSAGE_SHOW_ICON_MUSIC_OPERATIONS = 10002;
	static final public int MESSAGE_REFRESH_LIST = 10003;

	private LinearLayout mNMJExclude;
	private ToggleButton mTogglebuttonNoVideo;
	private ToggleButton mTogglebuttonNoMusic;
	private ToggleButton mTogglebuttonNoPhoto;
	private ToggleButton mTogglebuttonNoAll;

	private TextView consoleText;
	private ScrollView consoleScroller;
		
	Intent intent;
	String action;
	
	DMTFile browseto;
	Bundle icicle;

	boolean showMenuMusicOperations = false;
    ActionMode mMode;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle _icicle) {
		super.onCreate(_icicle);

	    //ActionBarSherlock
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);

		icicle = _icicle; //For the async tasks
		
		ftpService = DMTApplication.getFtpService();
		settingsService = DMTApplication.getSettingsService();
		davidBoxService =  DMTApplication.getDavidBoxService();

		currentHandler = new Handler() {
			public void handleMessage(Message msg) {
				FileManagerActivity.this.handleMessage(msg);
			}
		};

		// Seteo del handler
		ftpService.setHandler(currentHandler);
		setContentView(R.layout.filelist);

		mEmptyText = (TextView) findViewById(R.id.empty_text);
		mProgressBar = (ProgressBar) findViewById(R.id.scan_progress);

		mNMJExclude = (LinearLayout) findViewById(R.id.nmj_exclude);
		mTogglebuttonNoVideo = (ToggleButton) findViewById(R.id.togglebuttonNoVideo);
		mTogglebuttonNoMusic = (ToggleButton) findViewById(R.id.togglebuttonNoMusic);
		mTogglebuttonNoPhoto = (ToggleButton) findViewById(R.id.togglebuttonNoPhoto);
		mTogglebuttonNoAll = (ToggleButton) findViewById(R.id.togglebuttonNoAll);
		assignOnClickNmjButtons();

		consoleText = (TextView) findViewById(R.id.consolaText);
		consoleScroller = (ScrollView) findViewById(R.id.consolaScroller);
		
		getListView().setOnCreateContextMenuListener(this);
		getListView().setEmptyView(findViewById(R.id.empty));
		getListView().setTextFilterEnabled(true);
		getListView().requestFocus();
		getListView().requestFocusFromTouch();

		mDirectoryButtons = (LinearLayout) findViewById(R.id.directory_buttons);
		mActionMultiselect = (LinearLayout) findViewById(R.id.action_multiselect);
		mEditFilename = (EditText) findViewById(R.id.filename);

		mButtonPick = (Button) findViewById(R.id.button_pick);

		mButtonPick.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				pickFileOrDirectory();
			}
		});

		// Initialize only when necessary:
		mDirectoryInput = null;

		// Create map of extensions:
		getMimeTypes();

		getSdCardPath();

		mState = STATE_BROWSE;

		intent = getIntent();
		action = intent.getAction();

		browseto = DMTFile.getRootDMTFile();

		// Default state
		mState = STATE_BROWSE;
		mWritableOnly = false;

		if (action != null) {

			if (action.equals(FileManagerIntents.ACTION_PICK_FILE)) {
				mState = STATE_PICK_FILE;
			} else if (action.equals(FileManagerIntents.ACTION_PICK_DIRECTORY)) {
				mState = STATE_PICK_DIRECTORY;
				mWritableOnly = intent.getBooleanExtra(
						FileManagerIntents.EXTRA_WRITEABLE_ONLY, false);

				// Remove edit text and make button fill whole line
				mEditFilename.setVisibility(View.GONE);
				mButtonPick.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
			}

		}

		if (mState == STATE_BROWSE) {
			// Remove edit text and button.
			mEditFilename.setVisibility(View.GONE);
			mButtonPick.setVisibility(View.GONE);
		}

		if (mState != STATE_MULTI_SELECT) {
			// Remove multiselect action buttons
			mActionMultiselect.setVisibility(View.GONE);
		}
				
//FUCK		new TaskFtpGetFileByIntentData(this, ftpService).execute(intent);
		AsyncTask getFileTask = new AsyncTask<Object, Void, DMTFile>(){
			
			protected Intent _intent;
			
			@Override
			protected DMTFile doInBackground(Object... params) {
				_intent = (Intent) params[0];
				
				// Set current directory and file based on intent data.
				// DMTFile file = FileUtils.getFile(intent.getData());
				DMTFile file = ftpService.getFile(_intent.getData());
				return file;
			}

			@Override
			protected void onPostExecute(DMTFile file){ //result
				continue1(file);
			}
		}.execute(intent);
		
	}

//	@Override
//FUCK	public void onResultTaskFtpGetFileByIntentData(DMTFile file) {
	private void continue1(DMTFile file){	
								
		if (file != null) {
			DMTFile dir = ftpService.getPathWithoutFilename(file);
			if (null != dir) { // avoid null pointer
				if (dir.isDirectory()) {
					browseto = dir;
				}
				if (!file.isDirectory()) {
					mEditFilename.setText(file.getName());
				}
			}
		}

		String title = intent.getStringExtra(FileManagerIntents.EXTRA_TITLE);
		if (title != null) {
			setTitle(title);
		}

		String buttontext = intent
				.getStringExtra(FileManagerIntents.EXTRA_BUTTON_TEXT);
		if (buttontext != null) {
			mButtonPick.setText(buttontext);
		}

		mStepsBack = 0;

		if (icicle != null) {
			String bundleCurrentDirectory = icicle.getString(BUNDLE_CURRENT_DIRECTORY);

//FUCK			new TaskFtpGetFileBundleDirectory(this, ftpService).execute(bundleCurrentDirectory);
			AsyncTask getFile2Task = new AsyncTask<Object, Void, DMTFile>(){
				
				protected String _bundleCurrentDirectory;
				protected DMTFile _browseto;
				
				@Override
				protected DMTFile doInBackground(Object... params) {	
					
					_bundleCurrentDirectory = (String)params[0];
					
					if ("/".equals(_bundleCurrentDirectory)) {
						// Por si gira la pantalla o lo que sea en la raï¿½ï¿½z, el getfile
						// devolverï¿½ï¿½a null y
						// sucederia un null pointer despuï¿½ï¿½s cuando se comprueba si es
						// un directorio
						_browseto = DMTFile.getRootDMTFile();
					} else {
						_browseto = ftpService.getFile(_bundleCurrentDirectory);
					}
					return _browseto;
				}

				@Override
				protected void onPostExecute(DMTFile file){ //result
					continue2(file);
				}
			}.execute(bundleCurrentDirectory);
			
		}
		
		//added if because the async task
		if (icicle == null) {
			browseTo(browseto);
		}
	}

//	@Override
//FUCK	public void onResultTaskFtpGetFileBundleDirectory(DMTFile _browseto) {
	private void continue2(DMTFile _browseto){	

			browseto = _browseto;
			
			String bundleContextFile = icicle.getString(BUNDLE_CONTEXT_FILE);
			
//FUCK			new TaskFtpGetFileBundleContextFile(this, ftpService).execute(bundleContextFile);
			AsyncTask continue2Task = new AsyncTask<Object, Void, DMTFile>(){
				
				protected String _bundleContextFile;
				protected DMTFile _mContextFile;
				@Override
				protected DMTFile doInBackground(Object... params) {						
					_bundleContextFile = (String)params[0];

					if ("".equals(_bundleContextFile)) {
						_mContextFile = new DMTFile("");
					} else {
						_mContextFile = ftpService.getFile(_bundleContextFile);
					}
					return _mContextFile;
				}

				@Override
				protected void onPostExecute(DMTFile _mContextFile){ //result
					continue3(_mContextFile);
				}
			}.execute(bundleContextFile);				
			
	}

//	@Override
//FUCK	public void onResultTaskFtpGetFileBundleContextFile(DMTFile _mContextFile) {
	private void continue3(DMTFile _mContextFile){
		
		mContextFile = _mContextFile; //VIENE DE ASYNC			
		mContextText = icicle.getString(BUNDLE_CONTEXT_TEXT);
		mStepsBack = icicle.getInt(BUNDLE_STEPS_BACK);

		browseTo(browseto);
	}

	@Override
	protected void onPause() {
		super.onPause();

		//Disconnect the ftp client
		new TaskFtpDisconnect(ftpService).execute("");
	}

	public void onDestroy() {
		super.onDestroy();

		try{	
			// Stop the scanner.
			DirectoryScanner scanner = mDirectoryScanner;
	
			if (scanner != null) {
				scanner.cancel = true;
			}
	
			mDirectoryScanner = null;
	
			ThumbnailLoader loader = mThumbnailLoader;
	
			if (loader != null) {
				loader.cancel = true;
				mThumbnailLoader = null;
			}
		} catch (Exception e) {
			 DmtLogger.e(TAG, "Exception onDestroy: " + e.getMessage());
		}
	}

	private void handleMessage(Message message) {

		switch (message.what) {
		case MESSAGE_SHOW_DIRECTORY_CONTENTS:
			showDirectoryContents((DirectoryContents) message.obj);
			break;

		case MESSAGE_SET_PROGRESS:
			setProgress(message.arg1, message.arg2);
			break;

		case MESSAGE_ICON_CHANGED:
			notifyIconChanged();
			break;

		case FtpService.MESSAGE_CONSOLE_CHANGED:
			updateConsole((String) message.obj);
			break;
		case MESSAGE_SHOW_ICON_MUSIC_OPERATIONS:
			//Show the music icon operations in the title bar if
			//some music queuable file has been found
			setVisibilityMusicBarIcon(true);
			break;
		case MESSAGE_REFRESH_LIST:
			refreshList();
			break;
		}
	}

	/**
	 * Notify that icon was changed.
	 */
	private void notifyIconChanged() {
		if (getListAdapter() != null) {
			((BaseAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	private void setProgress(int progress, int maxProgress) {
		mProgressBar.setMax(maxProgress);
		mProgressBar.setProgress(progress);
		mProgressBar.setVisibility(View.VISIBLE);
	}

	private void showDirectoryContents(DirectoryContents contents) {
		mDirectoryScanner = null;

		mListSdCard = contents.listSdCard;
		mListDir = contents.listDir;
		mListFile = contents.listFile;

		directoryEntries.ensureCapacity(mListSdCard.size() + mListDir.size() + mListFile.size());

		addAllElements(directoryEntries, mListSdCard);
		addAllElements(directoryEntries, mListDir);
		addAllElements(directoryEntries, mListFile);

		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this);
		itla.setListItems(directoryEntries, getListView().hasTextFilter());
		setListAdapter(itla);
		getListView().setTextFilterEnabled(true);

	     if(fileDeleted){
	    	 getListView().setSelection(positionAtDelete);
	     }

		selectInList(mPreviousDirectory);
		refreshDirectoryPanel();
		setProgressBarIndeterminateVisibility(false);

		mProgressBar.setVisibility(View.GONE);
		mEmptyText.setVisibility(View.VISIBLE);

		// Actualizo los botones NMJ con su valor cierto o falso
		mTogglebuttonNoVideo.setChecked(contents.noNmjVideo);
		mTogglebuttonNoMusic.setChecked(contents.noNmjMusic);
		mTogglebuttonNoPhoto.setChecked(contents.noNmjPhoto);
		mTogglebuttonNoAll.setChecked(contents.noNmjAll);

		//Se establece si la consola es visible o no		
		establecerVisiblidad(consoleScroller, settingsService.isShowConsole());
		establecerVisiblidad(mNMJExclude, settingsService.isShowNmj());

		Profile activeProfile = settingsService.getActiveProfile();
		boolean isDavidBoxSupported = UtilProfile.isDavidBoxSupported(activeProfile);
		mThumbnailLoader = new ThumbnailLoader(ftpService, currentDirectory,
				mListFile, currentHandler, this, mMimeTypes, isDavidBoxSupported);
		mThumbnailLoader.start();
	}

	private void refreshDirectoryPanel() {
		if (isDirectoryInputVisible()) {
			// Set directory path
			String path = currentDirectory.getAbsolutePath();
			mEditDirectory.setText(path);

			// Set selection to last position so user can continue to type:
			mEditDirectory.setSelection(path.length());
		} else {
			setDirectoryButtons();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// remember file name
		outState.putString(BUNDLE_CURRENT_DIRECTORY, currentDirectory.getAbsolutePath());
		outState.putString(BUNDLE_CONTEXT_FILE, mContextFile.getAbsolutePath()); 		
		outState.putString(BUNDLE_CONTEXT_TEXT, mContextText);

		outState.putInt(BUNDLE_STEPS_BACK, mStepsBack);
	}

	private boolean isDirectoryInputVisible() {
		boolean isVisible = false;
		if (mDirectoryInput != null 
				&& mDirectoryInput.getVisibility() == View.VISIBLE){
			isVisible = true;
		}
		return isVisible;
	}

	private void pickFileOrDirectory() {
		
		DMTFile file = null;
		if (mState == STATE_PICK_FILE) {
			String filename = mEditFilename.getText().toString();

//FUCK			new TaskFtpPickFileOrDirectory(this, ftpService).execute(currentDirectory, filename);
			AsyncTask pickFileOrDirectoryTask = new AsyncTask<Object, Void, DMTFile>(){
				
				protected DMTFile _currentDirectory;
				protected String _filename;
				
				@Override
				protected DMTFile doInBackground(Object... params) {
					_currentDirectory = (DMTFile)params[0];
					_filename = (String)params[1];
				
					DMTFile _file = ftpService.getFile(_currentDirectory.getAbsolutePath(), _filename);										
					return _file;
				}

				@Override
				protected void onPostExecute(DMTFile _file){ //result
					executeCommonIntent(_file);
				}
				
			}.execute(currentDirectory, filename);			
			
					
		} else if (mState == STATE_PICK_DIRECTORY) {
			file = currentDirectory;
			executeCommonIntent(file);
		}
	}

//	@Override
//FUCK	public void onResultTaskFtpPickFileOrDirectory(DMTFile file) {
//		executeCommonIntent(file);
//	}
	
	private void executeCommonIntent(DMTFile file){
		Intent intent = getIntent();
		intent.setData(UtilityFile.getUri(file));
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void getMimeTypes() {
		MimeTypeParser mtp = new MimeTypeParser();

		XmlResourceParser in = getResources().getXml(R.xml.mimetypes);

		try {
			mMimeTypes = mtp.fromXmlResource(in);
		} catch (XmlPullParserException e) {
			DmtLogger.e(TAG, "PreselectedChannelsActivity: XmlPullParserException", e);
			throw new RuntimeException("PreselectedChannelsActivity: XmlPullParserException");
		} catch (IOException e) {
			DmtLogger.e(TAG, "PreselectedChannelsActivity: IOException", e);
			throw new RuntimeException("PreselectedChannelsActivity: IOException");
		}
	}

	/**
	 * This function browses up one level according to the field:
	 * currentDirectory
	 */
	private void upOneLevel() {
		if (mStepsBack > 0) {
			mStepsBack--;
		}
		if (currentDirectory.getParent() != null){
			// browseTo(currentDirectory.getParentFile());
			DmtLogger.d(TAG, "currentDirectory: " + currentDirectory);		
			
//FUCK			new TaskFtpGetParentFile(this, ftpService).execute(currentDirectory);
			AsyncTask upOneLevelTask = new AsyncTask<Object, Void, DMTFile>(){
				
				protected DMTFile _currentDirectory;
				
				@Override
				protected DMTFile doInBackground(Object... params) {
					_currentDirectory = (DMTFile)params[0];

					DMTFile _parentFile = ftpService.getParentFile(_currentDirectory);
					return _parentFile;
				}

				@Override
				protected void onPostExecute(DMTFile _parentFile){ //result
					continueUpOneLevel(_parentFile);
				}
			}.execute(currentDirectory);			
			
		}
	}

//	@Override
//FUCK	public void onResultTaskFtpGetParentFile(DMTFile parentFile) {
	private void continueUpOneLevel(DMTFile parentFile){	
		browseTo(parentFile);
	}
	
	/**
	 * Jump to some location by clicking on a directory button.
	 * 
	 * This resets the counter for "back" actions.
	 * 
	 * @param aDirectory
	 */
	private void jumpTo(final DMTFile aDirectory) {
		mStepsBack = 0;
		browseTo(aDirectory);
	}

	/**
	 * Browse to some location by clicking on a list item.
	 * 
	 * @param aDirectory
	 */
	private void browseTo(final DMTFile aDirectory) {

		if (null != aDirectory) { // avoid null pointer
			DmtLogger.d(TAG, "browseTo. aDirectory:" + aDirectory);
			DmtLogger.d(TAG,
					"browseTo. aDirectory.toString():" + aDirectory.toString());
			if (aDirectory.isDirectory()) {
				if (!aDirectory.equals(currentDirectory)) { 
					mPreviousDirectory = currentDirectory;
					currentDirectory = aDirectory;
					refreshList();
				}
			} else {
				if (mState == STATE_BROWSE || mState == STATE_PICK_DIRECTORY) {
					// Lets start an intent to View the file, that was clicked...					
					openFile(aDirectory);
				} else if (mState == STATE_PICK_FILE) {
					// Pick the file
					mEditFilename.setText(aDirectory.getName());
				}
			}
		}
	}

	private void openFile(DMTFile aFile) {
		if (null == aFile) {
			Toast.makeText(this, R.string.error_file_does_not_exists,
					Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);

		Profile activeProfile = settingsService.getActiveProfile();

		Uri data = UtilProfile.getUriMyiHomeStream(aFile, activeProfile); //Just in case
        String type = mMimeTypes.getMimeType(aFile.getName());
        intent.setDataAndType(data, type);

		// Were we in GET_CONTENT mode?
		Intent originalIntent = getIntent();

		if (originalIntent != null && originalIntent.getAction() != null
				&& originalIntent.getAction().equals(Intent.ACTION_GET_CONTENT)) {
			// In that case, we should probably just return the requested data.
			intent.setData(Uri.parse(FileManagerProvider.FILE_PROVIDER_PREFIX
					+ aFile)); //FIXME this is weird
			setResult(RESULT_OK, intent);
			finish();
			return;
		}
	
		Intent intentMyiHome = null;
		Intent intentLlink = null;
		boolean davidboxSupported = UtilProfile.isDavidBoxSupported(activeProfile);
		
		int targets = 0;
		boolean isMyiHomeAvailable = UtilProfile.isMyiHomeAvailable(activeProfile);
		if (isMyiHomeAvailable){
			targets++;
			intentMyiHome = new Intent(Intent.ACTION_VIEW);
			Uri dataMyiHome = UtilProfile.getUriMyiHomeStream(aFile, activeProfile);
			intentMyiHome.setDataAndType(dataMyiHome, type);			
		}
		boolean isLlinkAvailable = UtilProfile.isLlinkAvailable(settingsService.getActiveProfile());	        		
		if (isLlinkAvailable){
			targets++;
			intentLlink = new Intent(Intent.ACTION_VIEW);
			Uri dataLlink = UtilProfile.getUriLlinkStream(aFile, activeProfile);		
			intentLlink.setDataAndType(dataLlink, type);			
		}
		if (davidboxSupported){
			targets++;
		}
		
		if (targets == 0){
			Toast.makeText(getApplicationContext(), R.string.no_target_available, Toast.LENGTH_SHORT).show();			
		} else {
            mMode = startActionMode(new ActionModePlayFile(intentMyiHome, intentLlink, davidboxSupported, aFile));			
		}
	}

	/**
	 * It's common to MyiHome or Llink intents
	 * @param intent
	 */
	private void ejecutarPlayEnAndroid(Intent intent){
		// Play the file in Android
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			
			//There is no android app to handle the intent. It's a http url so at least the browser should open it.
			//Clear the mimetype specified so more chances to match android apps intent filters
			//Save the current URI
			Uri currentUri = intent.getData();
			//Set the data this intent is operating on. This method automatically clears any type that was previously set by.
			intent.setData(currentUri);			
			try {
				//Second chance to start the intent, this time without the mimetype
				startActivity(intent);
			} catch (ActivityNotFoundException e2) {
				Toast.makeText(this, R.string.application_not_available, Toast.LENGTH_SHORT).show();
			}
		}		
	}

	/**
	 * Enqueue audio, video or photo file in NMT,
	 * @param file the file
	 */
	private void ejecutarEnqueueIntoNMT(DMTFile file){
		new TaskNmtEnqueueGenericFile(this, davidBoxService).execute(file);
	}
	
	private void ejecutarPlayEnNMT(DMTFile file){
		new TaskNmtStartGenericFile(this, this, davidBoxService).execute(file);
	}

	@Override
	public void onResultTaskNmtStartGenericFileUnsupportedExtension() {
		Toast.makeText(this, R.string.nmt_file_extension_not_supported, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Play first file and enqueue the rest, or enqueue all not playing the first one.
	 * @param currentFolder
	 * @param playFirst if you want the first file to be played
	 */
	private void executeEnqueueAllMusicOnNMT(DMTFile currentFolder, boolean playFirst){						
		new TaskEnqueuAllMusicIntoNMT(this, davidBoxService, ftpService, playFirst).execute(currentFolder);
	}

	/**
	 * Create and upload playlist for NMT (playlist openable on NMT)
	 * @param file
	 */
	private void createUploadPlaylistForNMTTask(DMTFile file){
		//Get the directory where playlist files were created during initialization
		File fileDir = getFilesDir(); //needed por creating files later
		new TaskCreateUploadPlaylistForNMT(this, this, ftpService).execute(file, fileDir);
	}

	/**
	 * Create a local playlist for the file/folder passed as parameter and open it. 
	 * @param _mContextFile
	 */
	private void executeOpenLocalPlaylist(DMTFile _mContextFile, TypeStreamer typeStreamer){
		//Get the directory where playlist files were created during initialization
		File fileDir = getFilesDir();
		new TaskCreateLocalPlaylist(this, ftpService, settingsService).execute(_mContextFile, fileDir, typeStreamer);	
	}

	@Override
	public void onResultTaskCreateLocalPlaylist(File playlistLocalFile) {
		//Open the playlist file in an application though an intent
		Intent intent = new Intent();  
		intent.setAction(Intent.ACTION_VIEW);		
        String type = mMimeTypes.getMimeType(playlistLocalFile.getName());
        //String type = "net.sourceforge.servestream/*";
        intent.setDataAndType(Uri.fromFile(playlistLocalFile), type);	        
        	        
        try {
        	startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
        	//No app found to open it so let's recommend one that can do the job	        	
        	openAppOnGooglePlay(Constants.PACKAGE_RECOMMENDED_APP_FOR_PLAYLISTS);
        }
	}
	
	private void refreshList() {

		boolean directoriesOnly = mState == STATE_PICK_DIRECTORY;

		// Cancel an existing scanner, if applicable.
		DirectoryScanner scanner = mDirectoryScanner;

		if (scanner != null) {
			scanner.cancel = true;
		}

		ThumbnailLoader loader = mThumbnailLoader;

		if (loader != null) {
			loader.cancel = true;
			mThumbnailLoader = null;
		}

		directoryEntries.clear();
		mListDir.clear();
		mListFile.clear();
		mListSdCard.clear();

		setProgressBarIndeterminateVisibility(true);

		// Don't show the "folder empty" text since we're scanning.
		mEmptyText.setVisibility(View.GONE);

		// Also DON'T show the progress bar - it's kind of lame to show that for less than a second.
		mProgressBar.setVisibility(View.VISIBLE);
		mNMJExclude.setVisibility(View.GONE);
		//Hide de music operations icon
		setVisibilityMusicBarIcon(false);

		setListAdapter(null);

		boolean showNmjExcludes = settingsService.isShowNmj();				
		mDirectoryScanner = new DirectoryScanner(ftpService, showNmjExcludes, currentDirectory,
				this, currentHandler, mMimeTypes, mSdCardPath, mWritableOnly,
				directoriesOnly);
		mDirectoryScanner.start();
	}

	private void setVisibilityMusicBarIcon(boolean visibility){

		showMenuMusicOperations = visibility;
		
		//Invalidate the menu for all versions
		supportInvalidateOptionsMenu();		
	}
	
	private void selectInList(DMTFile selectFile) {
		String filename = selectFile.getName();
		IconifiedTextListAdapter la = (IconifiedTextListAdapter) getListAdapter();
		int count = la.getCount();
		for (int i = 0; i < count; i++) {
			IconifiedText it = (IconifiedText) la.getItem(i);
			if (it.getText().equals(filename)) {
				getListView().setSelection(i);
				break;
			}
		}
	}

	private void addAllElements(List<IconifiedText> addTo,
			List<IconifiedText> addFrom) {
		int size = addFrom.size();
		for (int i = 0; i < size; i++) {
			addTo.add(addFrom.get(i));
		}
	}

	private void setDirectoryButtons() {
		String[] parts = currentDirectory.getAbsolutePath().split("/");

		mDirectoryButtons.removeAllViews();

		int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;

		// Add home button separately
		ImageButton ib = new ImageButton(this);
		ib.setImageResource(R.drawable.ic_launcher_home_small);
		ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT,
				WRAP_CONTENT));
		ib.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				jumpTo(DMTFile.getRootDMTFile());
			}
		});
		mDirectoryButtons.addView(ib);

		// Add other buttons

		String dir = "";

		for (int i = 1; i < parts.length; i++) {
			dir += "/" + parts[i];

			Button b = new Button(this);
			b.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT,
					WRAP_CONTENT));
			b.setText(parts[i]);
			b.setTag(dir);
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					String dir = (String) view.getTag();

					new AsyncTask<String, Long, DMTFile>(){
						
						protected String _dir;
						
						@Override
						protected DMTFile doInBackground(String... params) {
							_dir = params[0];
							
							DMTFile _file = ftpService.getFile(_dir);
							return _file;
						}
						
						@Override
						protected void onPostExecute(DMTFile _file){ //result						
							jumpTo(_file);
						}
					}.execute(dir);					
				} //end onClick
			});
			mDirectoryButtons.addView(b);

		}

		checkButtonLayout();
	}
	
	private void checkButtonLayout() {

		// Let's measure how much space we need:
		int spec = View.MeasureSpec.UNSPECIFIED;
		mDirectoryButtons.measure(spec, spec);

		int requiredwidth = mDirectoryButtons.getMeasuredWidth();
		int width = getWindowManager().getDefaultDisplay().getWidth();

		if (requiredwidth > width) {
			int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;

			// Create a new button that shows that there is more to the left:
			ImageButton ib = new ImageButton(this);
			ib.setImageResource(R.drawable.ic_menu_back_small);
			ib.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT,
					WRAP_CONTENT));
			//
			ib.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					// Up one directory.
					upOneLevel();
				}
			});
			mDirectoryButtons.addView(ib, 0);

			// New button needs even more space
			ib.measure(spec, spec);
			requiredwidth += ib.getMeasuredWidth();

			// Need to take away some buttons
			// but leave at least "back" button and one directory button.
			while (requiredwidth > width
					&& mDirectoryButtons.getChildCount() > 2) {
				View view = mDirectoryButtons.getChildAt(1);
				requiredwidth -= view.getMeasuredWidth();

				mDirectoryButtons.removeViewAt(1);
			}
		}
	}

	@Override
	protected void onListItemClick(ListView l, final View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		IconifiedTextListAdapter adapter = (IconifiedTextListAdapter) getListAdapter();

		if (adapter == null) {
			return;
		}

		IconifiedText text = (IconifiedText) adapter.getItem(position);

		String file = text.getText();
		String curdir = currentDirectory.getAbsolutePath();		
		
		new AsyncTask<String, Void, DMTFile>(){
			
			protected String _curdir;
			protected String _file;
			
			@Override
			protected DMTFile doInBackground(String... params) {
				_curdir = params[0];
				_file = params[1];
				
				DMTFile _clickedFile = ftpService.getFile(_curdir, _file);				
				return _clickedFile;
			}
    					
			@Override
			protected void onPostExecute(DMTFile _clickedFile){
				if (_clickedFile != null) {
					if (_clickedFile.isDirectory()) {
						// If we click on folders, we can return later by the "back" key.
						mStepsBack++;
					}
					
					browseTo(_clickedFile);
				}				
			}
			
    	}.execute(curdir, file);
    	
	}

	private void getSdCardPath() {
		mSdCardPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	}

 	@Override
 	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getSupportMenuInflater().inflate(R.menu.filemanager_main, menu);                
 		return true;
 	}

 	/**
 	 * Shows the submenu operations based in the user settings
 	 * @param menu
 	 */
 	private void prepareSubmenuMusicOperations(Menu menu){
		 //music operations submenu
		 if (null != currentDirectory
				 && !"".equals(currentDirectory.getAbsolutePath())){

			 menu.findItem(R.id.menu_play_all_nmt).setVisible(true);
			 menu.findItem(R.id.menu_enqueue_all_nmt).setVisible(true);
			 menu.findItem(R.id.menu_create_upload_playlist_nmt).setVisible(true);

			 Profile activeProfile = settingsService.getActiveProfile();
			//Only if MyiHome is available
			boolean isMyiHomeAvailable = UtilProfile.isMyiHomeAvailable(activeProfile);
	        if (isMyiHomeAvailable){
				 menu.findItem(R.id.menu_play_all_android_myihome).setVisible(true);
	        }
	        
	        //Only if Llink is available
			boolean isLlinkAvailable = UtilProfile.isLlinkAvailable(activeProfile);	        
	        if (isLlinkAvailable){
				 menu.findItem(R.id.menu_play_all_android_llink).setVisible(true);
	        }
		 }
 	}
 	
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
		 super.onPrepareOptionsMenu(menu);

		 if (showMenuMusicOperations){
			 menu.findItem(R.id.menu_music).setVisible(true);
			//music operations submenu
			 prepareSubmenuMusicOperations(menu);
		 } else{
			 menu.findItem(R.id.menu_music).setVisible(false);
		 }

		 return true;
	 }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case android.R.id.home:
	        // app icon in action bar clicked; go home
	        Intent intent = new Intent(this, HomeActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	        return true;	    	    

	    /* Music operations */
	    case R.id.menu_play_all_nmt:
	    	//It starts a new queue
			executeEnqueueAllMusicOnNMT(currentDirectory, true);
	        return true;	    	    

	    case R.id.menu_enqueue_all_nmt:
	    	//It enqueues all files in the current NMT queue
			executeEnqueueAllMusicOnNMT(currentDirectory, false);
	    	return true;	    	    

	    case R.id.menu_create_upload_playlist_nmt:
	    	//Create and upload playlist for NMT (playlist openable on NMT)
			createUploadPlaylistForNMTTask(currentDirectory);
	    	return true;	
	    	
	    case R.id.menu_play_all_android_myihome:
	    	//Create a local playlist for the file/folder passed as parameter and open it via myIhome
			 executeOpenLocalPlaylist(currentDirectory, TypeStreamer.MYIHOME);
	    	return true;	   
	    	
	    case R.id.menu_play_all_android_llink:
	    	//Create a local playlist for the file/folder passed as parameter and open it via Llink
			executeOpenLocalPlaylist(currentDirectory, TypeStreamer.LLINK);
	    	return true;	    	    
	    	
		case R.id.menu_create_folder:
			showDialog(DIALOG_NEW_FOLDER);
			return true;

		case R.id.menu_console:
			showHideConsole();
			return true;

		case R.id.menu_nmj_exclude:
			showHideNmjExclude();
			return true;

		}
		return super.onOptionsItemSelected(item);

	}

	private void establecerVisiblidad(View elemento, boolean valor){	
		if (valor){
			elemento.setVisibility(View.VISIBLE);
		} else {
			elemento.setVisibility(View.GONE);
		}
	}
	private void showHideConsole() {
		if (View.VISIBLE == consoleScroller.getVisibility()) {
			// hide show console
			establecerVisiblidad(consoleScroller,false);
			settingsService.updateShowConsole(false);
		} else {
			establecerVisiblidad(consoleScroller,true);
			settingsService.updateShowConsole(true);
		}
	}

	private void showHideNmjExclude() {
		if (View.VISIBLE == mNMJExclude.getVisibility()) {
			// hide show NMJ excludes
			settingsService.updateShowNmjExcludes(false);
			//Refresh the content
			refreshList();			
		} else {
			settingsService.updateShowNmjExcludes(true);
			//Refresh the content
			refreshList();			
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			DmtLogger.e(TAG, "bad menuInfo", e);
			return;
		}
		
		IconifiedTextListAdapter adapter = (IconifiedTextListAdapter) getListAdapter();

		if (adapter == null) {
			return;
		}

		IconifiedText it = (IconifiedText) adapter.getItem(info.position);
		menu.setHeaderTitle(it.getText());
		menu.setHeaderIcon(it.getIcon());

		DMTFile file = it.getFileTrick();

		if (null != file) { // avoid null pointer
			if (!file.isDirectory()
					&& mState == STATE_PICK_FILE) {
					// Show "open" menu
					menu.add(0, MENU_OPEN, 0, R.string.menu_open);
			}

			menu.add(0, MENU_MOVE, 0, R.string.menu_move);
			menu.add(0, MENU_RENAME, 0, R.string.menu_rename);
			menu.add(0, MENU_DELETE, 0, R.string.menu_delete);

		}
	}

	@Override
	//android.view.MenuItem trick Jake Wharton
	public boolean onContextItemSelected(android.view.MenuItem item) {
		super.onContextItemSelected(item);
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		// Remember current selection
		IconifiedTextListAdapter adapter = (IconifiedTextListAdapter) getListAdapter();

		if (adapter == null) {
			return false;
		}

		IconifiedText ic = (IconifiedText) adapter.getItem(menuInfo.position);
		mContextText = ic.getText();
		mContextIcon = ic.getIcon();
		mContextFile = ic.getFileTrick();
		
		switch (item.getItemId()) {
		case MENU_OPEN:
			openFile(mContextFile);
			return true;

		case MENU_MOVE:
			promptDestinationAndMoveFile();
			return true;

		case MENU_COPY:
			promptDestinationAndCopyFile();
			return true;

		case MENU_DELETE:
			showDialog(DIALOG_DELETE);
			return true;

		case MENU_RENAME:
			showDialog(DIALOG_RENAME);
			return true;

		case MENU_SEND:
			sendFile(mContextFile);
			return true;
		}

		return false;
	}
	
	/**
	 * It opens the Google Play store showing an specific app.
	 * @param packageName the package id
	 */
	private final void openAppOnGooglePlay(String packageId){
    	try{
    		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageId)));
    	} catch (ActivityNotFoundException anfe2) {
    		//If Google Play app is not installed on the device
    		startActivity(new Intent(Intent.ACTION_VIEW, 
    				Uri.parse("http://play.google.com/store/apps/details?id=" + packageId)));
    	}		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_NEW_FOLDER:
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.dialog_new_folder, null);
			final EditText et = (EditText) view.findViewById(R.id.foldername);
			et.setText("");
			return new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.create_new_folder)
					.setView(view)
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									if (null != et.getText()
											&& !"".equals(et.getText().toString())) {
										createNewFolder(et.getText().toString());
									}

								}

							})
					.setNegativeButton(android.R.string.cancel,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// Cancel should not do anything.
								}

							}).create();

		case DIALOG_DELETE:
			return new AlertDialog.Builder(this)
					.setTitle(getString(R.string.really_delete, mContextText))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
										deleteFileOrFolder(mContextFile);
								}

							})
					.setNegativeButton(android.R.string.cancel,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// Cancel should not do anything.
								}

							}).create();

		case DIALOG_RENAME:
			inflater = LayoutInflater.from(this);
			view = inflater.inflate(R.layout.dialog_new_folder, null);
			final EditText et2 = (EditText) view.findViewById(R.id.foldername);
			return new AlertDialog.Builder(this)
					.setTitle(R.string.menu_rename)
					.setView(view)
					.setPositiveButton(android.R.string.ok,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									if (null != et2.getText()
											&& !"".equals(et2.getText().toString())) {
											renameFileOrFolder(mContextFile, et2.getText().toString());
									}
								}

							})
					.setNegativeButton(android.R.string.cancel,
							new OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// Cancel should not do anything.
								}

							}).create();

		}
		return super.onCreateDialog(id);

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);

		switch (id) {
		case DIALOG_NEW_FOLDER:
			EditText et = (EditText) dialog.findViewById(R.id.foldername);
			et.setText("");
			break;

		case DIALOG_DELETE:
			((AlertDialog) dialog).setTitle(getString(R.string.really_delete,
					mContextText));
			break;

		case DIALOG_RENAME:
			et = (EditText) dialog.findViewById(R.id.foldername);
			et.setText(mContextText);
			TextView tv = (TextView) dialog.findViewById(R.id.foldernametext);
			if (null != mContextFile) { // avoid null pointer
				if (mContextFile.isDirectory()) {
					tv.setText(R.string.file_name);
				} else {
					tv.setText(R.string.file_name);
				}
			}
			((AlertDialog) dialog).setIcon(mContextIcon);
			break;

		}
	}

	private void promptDestinationAndMoveFile() {

		Intent intent = new Intent(FileManagerIntents.ACTION_PICK_DIRECTORY);

		intent.setData(UtilityFile.getUri(currentDirectory));

		Uri customUri = Uri.parse(Constants.SCHEMA_AND_HOST_DMT_NMT + UtilityFile.getUri(currentDirectory));
		intent.setData(customUri);

		intent.putExtra(FileManagerIntents.EXTRA_TITLE, getString(R.string.move_title));
		intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, getString(R.string.move_button));
		intent.putExtra(FileManagerIntents.EXTRA_WRITEABLE_ONLY, true);

		startActivityForResult(intent, REQUEST_CODE_MOVE);
	}

	private void promptDestinationAndCopyFile() {

		Intent intent = new Intent(FileManagerIntents.ACTION_PICK_DIRECTORY);

		intent.setData(UtilityFile.getUri(currentDirectory));

		intent.putExtra(FileManagerIntents.EXTRA_TITLE, getString(R.string.copy_title));
		intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, getString(R.string.copy_button));
		intent.putExtra(FileManagerIntents.EXTRA_WRITEABLE_ONLY, true);

		startActivityForResult(intent, REQUEST_CODE_COPY);
	}

	private void createNewFolder(String foldername) {
		if (!TextUtils.isEmpty(foldername)) {

			DMTFile mockDirectory = ftpService.createMockDMTFile(currentDirectory, foldername);

			new AsyncTask<DMTFile, Void, DMTFile>(){
				
				protected DMTFile _mockDirectory;
				
				@Override
				protected DMTFile doInBackground(DMTFile... params) {
					_mockDirectory = params[0];					
					
					DMTFile _file = null;
					
					boolean _createdDirectoryOK = ftpService.createDirectory(_mockDirectory);

					if (_createdDirectoryOK) {
						// Get the new file to get the real actual data
						_file = ftpService.getFile(_mockDirectory.getPath());
					}					 
					return _file;
				}

				@Override
				protected void onPostExecute(DMTFile _file){ //result
					createNewFolderTaskPost(_file);
				}

				
			}.execute(mockDirectory);

		}
	}

	private void createNewFolderTaskPost(DMTFile file){
		if (null != file) {  //file is null if createdDirectoryOK was false
			// Change into new directory:
			browseTo(file);
		} else {
			Toast.makeText(this, R.string.error_creating_new_folder, Toast.LENGTH_SHORT).show();
		}
	}

	private class RecursiveDeleteTask extends AsyncTask<Object, Void, Integer> {

		private FileManagerActivity activity = FileManagerActivity.this;
		private static final int success = 0;
		private static final int err_deleting_folder = 1;
		private static final int err_deleting_child_file = 2;
		private static final int err_deleting_file = 3;

		private DMTFile errorFile;

		/**
		 * Recursively delete a file or directory and all of its children.
		 * 
		 * @returns 0 if successful, error value otherwise.
		 */
		private int recursiveDelete(DMTFile file) {

			if (file.isDirectory()){
				DMTFile[] childs = ftpService.listFiles(file.getPath());
				if (null != childs){
					for (DMTFile childFile : childs) {
						if (childFile.isDirectory()) {
							int result = recursiveDelete(childFile);
							if (result > 0) {
								return result;
							}
						} else {
							boolean childDeletedOK = ftpService.deleteFile(childFile);
							if (!childDeletedOK) {
								errorFile = childFile;
								return err_deleting_child_file;
							}
						}
					}
				}
			}
			boolean deletedOK = false;
			if (file.isDirectory()){
				deletedOK = ftpService.deleteDirectory(file);
			} else {
				//it's a file			
				deletedOK = ftpService.deleteFile(file);
			}
			if (!deletedOK) {
				errorFile = file;
				return file.isDirectory() ? err_deleting_folder : err_deleting_file;
			}

			return success;
		}

		@Override
		protected void onPreExecute() {
			Toast.makeText(activity, R.string.deleting_files, Toast.LENGTH_SHORT).show();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected Integer doInBackground(Object... params) {
			Object files = params[0];
			
			if (files instanceof List<?>) {
				for (DMTFile file: (List<DMTFile>)files) {
					int result = recursiveDelete(file);
					if (result != success) return result;
				}
				return success;
			} else
				return recursiveDelete((DMTFile)files);

		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case success:
				activity.refreshList();
				if(deletedFileIsDirectory){
					Toast.makeText(activity, R.string.folder_deleted,Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(activity, R.string.file_deleted,Toast.LENGTH_SHORT).show();
				}
				break;
			case err_deleting_folder:
				Toast.makeText(activity,getString(R.string.error_deleting_folder,
						errorFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
				break;
			case err_deleting_child_file:
				Toast.makeText(activity,getString(R.string.error_deleting_child_file,
						errorFile.getAbsolutePath()),Toast.LENGTH_SHORT).show();
				break;
			case err_deleting_file:
				Toast.makeText(activity,getString(R.string.error_deleting_file,
						errorFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
				break;
			}
		}

	}

	private void deleteFileOrFolder(DMTFile file) {
		fileDeleted = true;
		positionAtDelete = getListView().getFirstVisiblePosition();
		deletedFileIsDirectory = file.isDirectory();
		new RecursiveDeleteTask().execute(file);
	}	

	private void renameFileOrFolder(DMTFile file, String newFileName) {

		if (newFileName != null 
				&& newFileName.length() > 0 
				&& newFileName.lastIndexOf('.') < 0) {
				newFileName += UtilityFile.getExtension(file.getName());
		}
		// Create a mock file with the absolute path of directory plus the name of the new file
		DMTFile newFile = ftpService.createMockDMTFile(currentDirectory, newFileName);

		new RenameTask().execute(file, newFile);
	}

	private class RenameTask extends AsyncTask<Object, Void, Integer> {
		
		private FileManagerActivity activity = FileManagerActivity.this;
		private static final int error_renaming_general = 0;
		private static final int folder_renamed = 1;
		private static final int file_renamed = 2;
				
		@Override
		protected Integer doInBackground(Object... params) {
			DMTFile _oldFile = (DMTFile)params[0];
			DMTFile _newFile = (DMTFile)params[1];

			boolean correcto = ftpService.remaneFileOrFolder(_oldFile, _newFile.getPath());

			// Custom DMT if (oldFile.renameTo(newFile)) {
			if (correcto) {
				// Consulto el fichero nuevo para traer sus datos VERDADEROS
				DMTFile newFile = ftpService.getFile(_newFile.getPath());
				
				if (null == newFile) { // avoid null pointer
					return error_renaming_general;
				} else if (newFile.isDirectory()) {
					return folder_renamed;
				} else {
					return file_renamed;
				}
			} else {
				return error_renaming_general;
			}
				
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case error_renaming_general:
				Toast.makeText(activity, R.string.error_renaming_general,Toast.LENGTH_SHORT).show();
				break;
			case folder_renamed:
				activity.refreshList();				
				Toast.makeText(activity, R.string.folder_renamed, Toast.LENGTH_LONG).show();
				break;
			case file_renamed:
				activity.refreshList();
				Toast.makeText(activity, R.string.file_renamed, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	}	

	private void sendFile(DMTFile file) {

		String filename = file.getName();
		String content = "hh";

		DmtLogger.i(TAG, "Title to send: " + filename);
		DmtLogger.i(TAG, "Content to send: " + content);

		Intent i = new Intent();
		i.setAction(Intent.ACTION_SEND);
		i.setType(mMimeTypes.getMimeType(file.getName()));
		i.putExtra(Intent.EXTRA_SUBJECT, filename);
		i.putExtra(Intent.EXTRA_STREAM,
				Uri.parse("content://" + FileManagerProvider.AUTHORITY
						+ file.getAbsolutePath()));

		i = Intent.createChooser(i, getString(R.string.menu_send));

		try {
			startActivity(i);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.send_not_available, Toast.LENGTH_SHORT).show();
			DmtLogger.e(TAG, "Email client not installed");
		}
	}

	// This code seems to work for SDK 2.3 (target="9")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& mStepsBack > 0) {
			upOneLevel();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private class MoveTask extends AsyncTask<Object, Void, Integer>{
		
		private FileManagerActivity activity = FileManagerActivity.this;
		private static final int error_moving_general = 0;
		private static final int folder_moved = 1;
		private static final int file_moved = 2;
		
		@Override
		protected Integer doInBackground(Object... params) {	
			Integer _mState = (Integer) params[0];
			DMTFile _movefrom = (DMTFile) params[1];
			Uri _uri = (Uri) params[2];
			
			//obtain
			DMTFile _moveto = ftpService.getFile(_uri);
			if (_moveto != null) {
				
				DmtLogger.d(TAG, "PRE MOVING");
				DmtLogger.d(TAG, "movefrom:" + _movefrom.getPath());
				DmtLogger.d(TAG, "moveto:" + _moveto.getPath());
				
				if (_mState != STATE_MULTI_SELECT) {
				    // Move single file.

					// Creo un mock con la ruta del destino mas el nombre del
					// fichero o directorio actual					
					DMTFile mockFileDestino = ftpService.createMockDMTFile(_moveto, _movefrom.getName());
					boolean correctoMover = ftpService.moveFileOrFolder(_movefrom, mockFileDestino.getPath());
					if (correctoMover) {
						// Rename was successful.
						// Consulto el fichero nuevo para traer sus datos VERDADEROS
						DMTFile newFile = ftpService.getFile(mockFileDestino.getPath());
						if (null == newFile) {
							return error_moving_general;
						} else if (newFile.isDirectory()) {
							return folder_moved;
						} else {
							return file_moved;
						}
					} else {
						return error_moving_general;
					}
				}
			}
			//Just in case
			return error_moving_general; //There is not move to
		}	

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case error_moving_general:
				Toast.makeText(activity, R.string.error_moving_general,Toast.LENGTH_SHORT).show();
				break;
			case folder_moved:
				activity.refreshList();
				Toast.makeText(activity, R.string.folder_moved,Toast.LENGTH_SHORT).show();
				break;
			case file_moved:
				activity.refreshList();
				Toast.makeText(activity, R.string.file_moved,Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
	
	/**
	 * This is called after the file manager finished.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_CODE_MOVE:
			if (resultCode == RESULT_OK && data != null) {
				// obtain the filename
				DMTFile movefrom = mContextFile;
				//Execute in async task
				new MoveTask().execute(mState, movefrom, data.getData());
			}
				
			break;
			
		case REQUEST_CODE_COPY:
			if (resultCode == RESULT_OK && data != null) {
				DmtLogger.d(TAG, "not implemented");
				//do nothing
			}
			break;

		case REQUEST_CODE_MULTI_SELECT:
			if (resultCode == RESULT_OK && data != null) {
				refreshList();
			}
			break;
		}
	}

	private void autoScrollDown(final TextView tv) {
		consoleScroller.post(new Runnable() {
			public void run() {
				consoleScroller.scrollTo(0, tv.getLineHeight() * tv.getLineCount());
			}
		});
	}

	private void updateConsole(String newText) {
		consoleText.append((CharSequence) newText);
		autoScrollDown(consoleText);
	}

	private void assignOnClickNmjButton(final ToggleButton toggleButton, final String typeExclude) {
		toggleButton.setOnClickListener(new ToggleButton.OnClickListener() {
			public void onClick(View v) {
				
				// Perform action on clicks
				if (toggleButton.isChecked()) {
					//Toggle button was disabled and user enabled it
					File fileDir = getFilesDir();
					
					new AsyncTask<Object, Void, Boolean>(){
						
						protected DMTFile _currentDirectory;
						protected File _fileDir;
						protected String _tipoExclude;
						
						@Override
						protected Boolean doInBackground(Object... params) {
							_currentDirectory = (DMTFile)params[0];
							_fileDir = (File)params[1];
							_tipoExclude = (String)params[2];
							
							File ficheroExclude = new File(_fileDir, _tipoExclude);								
							boolean correcto = ftpService.enableNmjExclude(_currentDirectory, ficheroExclude);
							return correcto;
						}
						@Override
						protected void onPostExecute(Boolean _correcto){ //result
							if (!_correcto) {
								// If operation is not successful then set toggle button to its original status
								toggleButton.setChecked(false);
							}
							Toast.makeText(FileManagerActivity.this, R.string.exclude_on, Toast.LENGTH_SHORT).show();
							//refresh the file browser
							refreshList();
						}							
					}.execute(currentDirectory, fileDir, typeExclude);			
					
				} else {
					// Is enabled and user wants to disable it
					new AsyncTask<Object, Void, Boolean>(){
						
						protected DMTFile _currentDirectory;
						protected String _tipoExclude;
						
						@Override
						protected Boolean doInBackground(Object... params) {
							_currentDirectory = (DMTFile)params[0];
							_tipoExclude = (String)params[1];
							
							boolean correcto = ftpService.disableNmjExclude(_currentDirectory, _tipoExclude);						
							return correcto;
						}
						@Override
						protected void onPostExecute(Boolean _correcto){ //result
							if (!_correcto) {
								//If operation is not successful then set toggle button to its original status
								toggleButton.setChecked(true);
							}
							Toast.makeText(FileManagerActivity.this, R.string.exclude_off, Toast.LENGTH_SHORT).show();
							//refresh the file browser
							refreshList();
						}							
					}.execute(currentDirectory, typeExclude);			
				}
			}
		});
	}

	/**
	 * Assigns on click listeners to NMJ buttons.
	 */
	private void assignOnClickNmjButtons() {		
		assignOnClickNmjButton(mTogglebuttonNoVideo, Constants.NMJ_NO_VIDEO);
		assignOnClickNmjButton(mTogglebuttonNoMusic, Constants.NMJ_NO_MUSIC);
		assignOnClickNmjButton(mTogglebuttonNoPhoto, Constants.NMJ_NO_PHOTO);
		assignOnClickNmjButton(mTogglebuttonNoAll, Constants.NMJ_NO_ALL);
	}

	@Override
	public void onResultTaskCreateUploadPlaylistForNMT() {
		refreshList();		
	}

	/**
	 * Handle the click on the About button.
	 * 
	 * @param v View
	 * @return void
	 */
	public void onClickInfo(View v) {
		startActivity(new Intent(getApplicationContext(), InfoActivity.class));
	}
	
	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg String
	 * @return void
	 */
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
    private final class ActionModePlayFile implements ActionMode.Callback {
    	    	
    	final Intent intentMyiHome; 
    	final Intent intentLlink;
		final boolean davidboxSupported;
		final DMTFile file;
		
		public ActionModePlayFile(Intent intentMyiHome, Intent intentLlink,
				boolean davidboxSupported, DMTFile file) {
			super();
			this.intentMyiHome = intentMyiHome;
			this.intentLlink = intentLlink;
			this.davidboxSupported = davidboxSupported;
			this.file = file;
		}
		
		
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {        	
            // Inflate the menu.
            getSupportMenuInflater().inflate(R.menu.filemanager_file_mode, menu);                    
     		
            if (null != file){
            	mode.setTitle(file.getName());
            }
            
            return true;
        }

		@Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			
			//Submenus for target button
    		if (null != intentMyiHome){
    	        //play in android with myihome
   			 	menu.findItem(R.id.text_play_android_myihome).setVisible(true);   			 	
    		}
    		
    		if (null != intentLlink){
    	        //play in android with Llink
   			 	menu.findItem(R.id.text_play_android_llink).setVisible(true);   			 	
    		}
    		
    		if (davidboxSupported){
    			//Solo si es abrible en nmt se a������ade esta opci������n
    			if(davidBoxService.isOpenableInNmt(file)){
    				//Play file in nmt action
       			 	menu.findItem(R.id.text_play_nmt).setVisible(true);   			 	
    			}
    			//Look if the file is queable			
    			if (UtilityFile.isMusicQueuable(file.getName())){
    				//Enqueu into NMT action
       			 	menu.findItem(R.id.text_enqueue_nmt).setVisible(true);   			 	
    			}    			    			    		
    		}
    		
    		return true;
        }

		
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	                      
    		switch (item.getItemId()) {
    	    case R.id.text_play_android_myihome:
				ejecutarPlayEnAndroid(intentMyiHome);										
	            mode.finish();
				break;
    	    case R.id.text_play_android_llink:
				ejecutarPlayEnAndroid(intentLlink);
	            mode.finish();
				break;
    	    case R.id.text_play_nmt:
				ejecutarPlayEnNMT(file);
	            mode.finish();
				break;
    	    case R.id.text_enqueue_nmt:
				ejecutarEnqueueIntoNMT(file);
	            mode.finish();
				break;
    		}
                    
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
                
    }
}