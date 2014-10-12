package com.vikingbrain.dmt.oi.filemanager;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;

import com.vikingbrain.dmt.oi.filemanager.compatibility.BitmapDrawable_Compatible;
import com.vikingbrain.dmt.oi.filemanager.util.MimeTypes;
import com.vikingbrain.dmt.pojo.DMTFile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.pojo.util.UtilityFile;
import com.vikingbrain.dmt.service.FtpService;

public class ThumbnailLoader extends Thread {

	private static final String TAG = "OIFM_ThumbnailLoader";
	
	private FtpService ftpService;
	
    List<IconifiedText> listFile;
    boolean cancel;
    DMTFile file;
    Handler handler;
    Context context;

	private MimeTypes mMimeTypes;
	
    private static int thumbnailWidth = 32;
    private static int thumbnailHeight = 32;
    
    private boolean isDavidBoxSupported;
    
	ThumbnailLoader(FtpService ftpService, DMTFile file, List<IconifiedText> list, Handler handler, Context context, MimeTypes mimetypes,
			boolean isDavidBoxSupported) {
		super("Thumbnail Loader");
		
		this.ftpService = ftpService;
		listFile = list;
		this.file = file;
		this.handler = handler;
		this.context = context;
		this.mMimeTypes = mimetypes;
		this.isDavidBoxSupported = isDavidBoxSupported;
	}
	
	public static void setThumbnailHeight(int height) {
		thumbnailHeight = height;
		thumbnailWidth = height * 4 / 3;
	}
	
	public void run() {
		int count = listFile.size();

		//Detect if music operations icon is going to show in the action bar
		showMusicOperations(listFile);
		
		DmtLogger.v(TAG, "Scanning for thumbnails (files=" + count + ")");
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inSampleSize = 16;
		
		for (int x=0; x<count; x++) {
			if (cancel) {
				DmtLogger.v(TAG, "Thumbnail loader canceled");
				listFile = null;
				return;
			}
			IconifiedText text = listFile.get(x);
			String fileName = text.getText();
			try {
				options.inJustDecodeBounds = true;
				options.outWidth = 0;
				options.outHeight = 0;
				options.inSampleSize = 1;
				
				if ("video/mpeg".equals(mMimeTypes.getMimeType(fileName ))){
					// don't try to decode mpegs.
					continue;
				}
				
				BitmapFactory.decodeFile(ftpService.getFile(file, fileName).getPath(), options);
				
				if (options.outWidth > 0 && options.outHeight > 0) {
					// Now see how much we need to scale it down.
					int widthFactor = (options.outWidth + thumbnailWidth - 1) / thumbnailWidth;
					int heightFactor = (options.outHeight + thumbnailHeight - 1) / thumbnailHeight;
					
					widthFactor = Math.max(widthFactor, heightFactor);
					widthFactor = Math.max(widthFactor, 1);
					
					// Now turn it into a power of two.
					if (widthFactor > 1
						&& ((widthFactor & (widthFactor-1)) != 0)) {
							while ((widthFactor & (widthFactor-1)) != 0) {
								widthFactor &= widthFactor-1;
							}
							
							widthFactor <<= 1;
					}
					options.inSampleSize = widthFactor;
					options.inJustDecodeBounds = false;
					
					Bitmap bitmap = BitmapFactory.decodeFile(ftpService.getFile(file, fileName).getPath(), options);
				
					if (bitmap != null) {
						
						// SDK 1.6 and higher only:
						// BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
						
						BitmapDrawable drawable = BitmapDrawable_Compatible.getNewBitmapDrawable(context.getResources(), bitmap);
						
						drawable.setGravity(Gravity.CENTER);
						drawable.setBounds(0, 0, thumbnailWidth, thumbnailHeight);
						
						text.setIcon(drawable);
						
						Message msg = handler.obtainMessage(FileManagerActivity.MESSAGE_ICON_CHANGED);
						msg.obj = text;
						msg.sendToTarget();
					}
				}
				
			} catch (Exception e) {
				// That's okay, guess it just wasn't a bitmap.
				DmtLogger.d(TAG, "That's okay, guess it just wasn't a bitmap");
			}			
		}
		
		DmtLogger.v(TAG, "Done scanning for thumbnails");
		listFile = null;
	}

	/**
	 * Check if there is music queuable file (more than two simple music files). 
	 * If found just one it is not neccesary to check more files
	 * If show music operation icon has not already been sent
	 * @param list
	 */
	private void showMusicOperations(List<IconifiedText> list){
		//Music operation icon bar
		int countMusicQueuableFiles = 0;
		boolean messageSent = false;

		for (IconifiedText element : list) {
			String fileName = element.getText();

			if (!messageSent
				&& isDavidBoxSupported
						&& UtilityFile.isMusicQueuable(fileName)){
										
					countMusicQueuableFiles++;
					//If there is 2 or more music queueable files
					if (countMusicQueuableFiles >= 2){
						//send show music operation icon in bar
						Message msg = handler.obtainMessage(FileManagerActivity.MESSAGE_SHOW_ICON_MUSIC_OPERATIONS);
						msg.sendToTarget();

						//Mark as send so do not send again
						messageSent = true;						
					}				
			}
		}
	}
}