package com.vikingbrain.dmt.oi.filemanager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.oi.filemanager.util.MimeTypes;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.pojo.util.UtilityFile;
import com.vikingbrain.dmt.service.FtpService;

public class DirectoryScanner extends Thread {

	private static final String TAG = "OIFM_DirScanner";
	
	private FtpService ftpService;
	private boolean showNmjExcludes;
	
	private DMTFile currentDirectory;
	boolean cancel;

	private String mSdCardPath;
	private Context context;
    private MimeTypes mMimeTypes;
	private Handler handler;
	private long operationStartTime;

	private boolean mWriteableOnly;

	private boolean mDirectoriesOnly;
	
	// Update progress bar every n files
	static final private int PROGRESS_STEPS = 50;

	// Cupcake-specific methods
    static Method formatter_formatFileSize;

    static {
    	initializeCupcakeInterface();
    }
    

	DirectoryScanner(FtpService ftpService, boolean showNmjExcludes, DMTFile directory, Context context, Handler handler, MimeTypes mimeTypes, 
			String sdCardPath, boolean writeableOnly, boolean directoriesOnly) {
		super("Directory Scanner");
		this.ftpService = ftpService;
		currentDirectory = directory;
		this.context = context;
		this.handler = handler;
		this.mMimeTypes = mimeTypes;
		this.mSdCardPath = sdCardPath;
		this.mWriteableOnly = writeableOnly;
		this.mDirectoriesOnly = directoriesOnly;
		this.showNmjExcludes = showNmjExcludes;
	}
	
	private void clearData() {
		// Remove all references so we don't delay the garbage collection.
		context = null;
		mMimeTypes = null;
		handler = null;
	}

	public void run() {
		DmtLogger.v(TAG, "Scanning directory " + currentDirectory);
		
		DmtLogger.v(TAG, "Llamada a ftpService.listFiles. currentDirectory.getPath() " + currentDirectory.getPath());
		DMTFile[] files = ftpService.listFiles(currentDirectory.getPath());
		
		int totalCount = 0;
		
		if (cancel) {
			DmtLogger.v(TAG, "Scan aborted");
			clearData();
			return;
		}
		
		if (files == null) {
			DmtLogger.v(TAG, "Returned null - inaccessible directory?");
			totalCount = 0;
		} else {
			totalCount = files.length;
		}
		
		operationStartTime = SystemClock.uptimeMillis();
		
		DmtLogger.v(TAG, "Counting files... (total count=" + totalCount + ")");

		int progress = 0;
		
		/** Dir separate for sorting */
		List<IconifiedText> listDir = new ArrayList<IconifiedText>(totalCount);

		/** Files separate for sorting */
		List<IconifiedText> listFile = new ArrayList<IconifiedText>(totalCount);

		/** SD card separate for sorting */
		List<IconifiedText> listSdCard = new ArrayList<IconifiedText>(3);
		
		boolean noMedia = false;
				
		boolean noNmjVideo = false;
		boolean noNmjMusic = false;
		boolean noNmjPhoto = false;
		boolean noNmjAll = false;

		if (showNmjExcludes){
			noNmjVideo = ftpService.isNmjExclude(currentDirectory.getPath(), Constants.NMJ_NO_VIDEO);
			noNmjMusic = ftpService.isNmjExclude(currentDirectory.getPath(), Constants.NMJ_NO_MUSIC);
			noNmjPhoto = ftpService.isNmjExclude(currentDirectory.getPath(), Constants.NMJ_NO_PHOTO);
			noNmjAll = ftpService.isNmjExclude(currentDirectory.getPath(), Constants.NMJ_NO_ALL);			
		}
		
		// Cache some commonly used icons.
		Drawable sdIcon = context.getResources().getDrawable(R.drawable.icon_sdcard);
		Drawable folderIcon = context.getResources().getDrawable(R.drawable.icon_folder);		
		//New link support		
		Drawable folderLinkIcon = context.getResources().getDrawable(R.drawable.icon_folder_link);		
		Drawable genericFileIcon = context.getResources().getDrawable(R.drawable.icon_file);

		Drawable currentIcon = null; 
		
		if (files != null) {
			for (DMTFile currentFile : files){ 
				if (cancel) {
					// Abort!
					DmtLogger.v(TAG, "Scan aborted while checking files");
					clearData();
					return;
				}

				progress++;
				updateProgress(progress, totalCount);
				
				if (currentFile.isLink()){
					//New link support					
					currentIcon = folderLinkIcon;
					
					if (!mWriteableOnly || currentFile.canWrite()){
						listDir.add(new IconifiedText( 
								currentFile.getName(), "", currentIcon, currentFile));
					}
					
				} else if (currentFile.isDirectory()) { 
					if (currentFile.getAbsolutePath().equals(mSdCardPath)) {
						currentIcon = sdIcon;

						listSdCard.add(new IconifiedText( 
								currentFile.getName(), "", currentIcon, currentFile)); 
					} else {
						currentIcon = folderIcon;
						
						if (!mWriteableOnly || currentFile.canWrite()){
							listDir.add(new IconifiedText( 
									currentFile.getName(), "", currentIcon, currentFile));
						}
					}
				}else{ 
					String fileName = currentFile.getName(); 
					
					// Is this the ".nomedia" file?
					if (!noMedia
						&& fileName.equalsIgnoreCase(".nomedia")) {
							// It is no media!
							noMedia = true;
					}
					
					String mimetype = mMimeTypes.getMimeType(fileName);

					currentIcon = getDrawableForMimetype(currentFile, mimetype);
					if (currentIcon == null) {
						currentIcon = genericFileIcon;
					}

					String size = "";

					try {
						size = (String) formatter_formatFileSize.invoke(null, context, currentFile.getSize());
					} catch (Exception e) {
						// The file size method is probably null (this is most
						// likely not a Cupcake phone), or something else went wrong.
						// Let's fall back to something primitive, like just the number
						// of KB.
						size = Long.toString(currentFile.getSize() / 1024);
						size +=" KB";

						// Technically "KB" should come from a string resource,
						// but this is just a Cupcake 1.1 fallback, and KB is universal
						// enough.
					}

					if (!mDirectoriesOnly){		
						listFile.add(new IconifiedText( 
							currentFile.getName(), size, currentIcon, currentFile));
					}
				} 
			}
		}
		
		DmtLogger.v(TAG, "Sorting results...");
		
		//Collections.sort(mListSdCard); 
		Collections.sort(listDir, new ICComparator()); 
		Collections.sort(listFile, new ICComparator()); 

		if (!cancel) {
			DmtLogger.v(TAG, "Sending data back to main thread");
			
			DirectoryContents contents = new DirectoryContents();

			contents.listDir = listDir;
			contents.listFile = listFile;
			contents.listSdCard = listSdCard;

			contents.noNmjVideo = noNmjVideo;
			contents.noNmjMusic = noNmjMusic;
			contents.noNmjPhoto = noNmjPhoto;
			contents.noNmjAll = noNmjAll;
			
			Message msg = handler.obtainMessage(FileManagerActivity.MESSAGE_SHOW_DIRECTORY_CONTENTS);
			msg.obj = contents;
			msg.sendToTarget();
		}

		clearData();
	}
	
	private void updateProgress(int progress, int maxProgress) {
		// Only update the progress bar every n steps...
		if (progress % PROGRESS_STEPS == 0) {
			// Also don't update for the first second.
			long curTime = SystemClock.uptimeMillis();
			
			if (curTime - operationStartTime < 1000L) {
				return;
			}
			
			// Okay, send an update.
			Message msg = handler.obtainMessage(FileManagerActivity.MESSAGE_SET_PROGRESS);
			msg.arg1 = progress;
			msg.arg2 = maxProgress;
			msg.sendToTarget();
		}
	}

	/**
     * Return the Drawable that is associated with a specific mime type
     * for the VIEW action.
     * 
     * @param mimetype
     * @return
     */
    Drawable getDrawableForMimetype(DMTFile file, String mimetype) {
     if (mimetype == null) {
    	 return null;
     }
     
   	 PackageManager pm = context.getPackageManager();

   	 Uri data = UtilityFile.getUri(file);   	
   	 Intent intent = new Intent(Intent.ACTION_VIEW);
   	 
   	 // Let's probe the intent exactly in the same way as the VIEW action
   	 // is performed in FileManagerActivity.openFile(..)
     intent.setDataAndType(data, mimetype);
     
   	 final List<ResolveInfo> lri = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
   	 
   	 if (lri != null && lri.size() > 0) {
   		 
   		 // return first element
   		 int index = 0;
   		 
   		 // Actually first element should be "best match",
   		 // but it seems that more recently installed applications
   		 // could be even better match.
   		 index = lri.size()-1;
   		 
   		 final ResolveInfo ri = lri.get(index);
   		 return ri.loadIcon(pm);
   	 }
   	 
   	 return null;
    }

    private static void initializeCupcakeInterface() {
        try {
            formatter_formatFileSize = Class.forName("android.text.format.Formatter").getMethod("formatFileSize", Context.class, long.class);
        } catch (Exception ex) {
       	 	// This is not cupcake.
       	 	return;
        }
    }
}
	
class ICComparator implements Comparator{
	public int compare(Object o1, Object o2) {
		IconifiedText it1 = (IconifiedText) o1;
		IconifiedText it2 = (IconifiedText) o2;

	    String s1 = it1.getText();
	    String s2 = it2.getText();
	    return s1.toLowerCase().compareTo(s2.toLowerCase());
	  }
	}