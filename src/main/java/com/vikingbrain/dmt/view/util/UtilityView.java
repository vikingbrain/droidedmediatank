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
package com.vikingbrain.dmt.view.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.pojo.Link;
import com.vikingbrain.dmt.pojo.TypeLink;
import com.vikingbrain.dmt.pojo.TypeWebClient;
import com.vikingbrain.dmt.view.asynctaks.util.NmtTaskResult;
import com.vikingbrain.dmt.view.asynctaks.util.TaskResultDavidboxGetDevicesInfo;
import com.vikingbrain.nmt.responses.DavidBoxResponse;
import com.vikingbrain.nmt.util.TypeReturnValue;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * Utility class for the view layer.
 * 
 * @author Rafael Iñigo
 */
public class UtilityView {

	/**
	 * Check if a text is an url.
	 * @param text the text to check
	 * @return if it is an url
	 */
	public static boolean isUrl(String text){
		boolean isUrl = false;
		if (text.startsWith("http")){
			isUrl = true;
		}
		return isUrl;
	}
	
	/**
	 * Get the id image resource for a link.
	 * @param link the link
	 * @return the id image resource
	 */
	public static int getIdImage(Link link){
		int idImagenResource;
		
		if (TypeLink.ORIGINAL == link.getTypeLink()){
        	TypeWebClient typeWebClient = TypeWebClient.findByCodename(link.getName());
       		idImagenResource = typeWebClient.getIcon();
		} else {
			//Custom user link
			idImagenResource = R.drawable.icon_star;
		}
		
		return idImagenResource;
	}

	/**
	 * Set set text to a link.
	 * @param link the link
	 * @param textView the view component
	 */
	public static void setTextToLink(Link link, TextView textView){
		String name = link.getName();

		if (TypeLink.ORIGINAL == link.getTypeLink()){
        	TypeWebClient typeWebClient = TypeWebClient.findByCodename(name);
        	int idTextResource = typeWebClient.getTitle(); //i18n title
        	textView.setText(idTextResource);
		} else {
			//Custom user link
			textView.setText((CharSequence)name);
		}			
	}

    /**
     * Display a continue / cancel dialog.
     * @param context the current context.
     * @param icon The dialog icon.
     * @param title The dialog title.
     * @param message The dialog message.
     * @param onContinue The dialog listener for the continue button.
     * @param onCancel The dialog listener for the cancel button.
     */
    public static void showContinueCancelDialog(Context context, int icon, String title, String message, DialogInterface.OnClickListener onContinue, DialogInterface.OnClickListener onCancel) {            
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setCancelable(true);
	    builder.setIcon(icon);
	    builder.setTitle(title);
	    builder.setMessage(message);
	
	    builder.setPositiveButton(context.getResources().getString(R.string.continue_title), onContinue);
	    builder.setNegativeButton(context.getResources().getString(R.string.cancel), onCancel);
	    AlertDialog alert = builder.create();
	    alert.show();
    }

    /**
     * Show a dialog for an operation problem.
     * @param context the current context.
     * @param description the description of the problem
     * @param httpRequest the http request
     * @param httpResponse the http response
     */
	private static final void showDialogOperationProblem(Context context, String description, String httpRequest, String httpResponse){
		Dialog dialog = new Dialog(context);

		dialog.setContentView(R.layout.dialog_error_nmt_service);
		dialog.setTitle(R.string.title_dialog_error_nmt_service);

		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText((CharSequence)description); //no i18n because TypeErrorCode is given by the davidbox client library
		
		TextView showDebugInfo = (TextView) dialog.findViewById(R.id.showDebugInfo);    		
		final EditText debugInfo = (EditText) dialog.findViewById(R.id.debugInfo);
		
		debugInfo.setText(context.getString(R.string.debug_info_request_response, httpRequest, httpResponse));		
		
		showDebugInfo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	
            	if (debugInfo.getVisibility() == View.GONE){
            		debugInfo.setVisibility(View.VISIBLE);
            	} else {
            		debugInfo.setVisibility(View.GONE);            		
            	}
            }
        });    	
		
		dialog.show();
	}

	/**
	 * Show a dialog for an async task operation problem.
	 * @param context the context
	 * @param result the result of the async task
	 */
	public static final void showDialogOperationProblem(Context context, NmtTaskResult nmtTaskResult){
		TheDavidBoxClientException clientException = nmtTaskResult.getTheDavidBoxClientException();
		if (null != clientException){
			//Show the exception information
			showDialogOperationProblem(context, clientException.getMessage(), clientException.getRequest(), clientException.getResponse());
		} else if (null != nmtTaskResult.getDavidBoxResponse()){
			DavidBoxResponse response = nmtTaskResult.getDavidBoxResponse();
			//No exception, but if response is not valid
			if (!response.isValid()){
				TypeReturnValue typeErrorCode = response.getTypeReturnValue();
				String problemDescription = typeErrorCode.getId() + " - " + typeErrorCode.getDescription();
				showDialogOperationProblem(context, problemDescription, "", "");
			}
		}
	}

	/**
	 * Show a dialog for the TaskResultDavidboxGetDevicesInfo async task operation problem.
	 * @param context the context
	 * @param result the result of the async task
	 */
	public static final void showDialogOperationProblem(Context context, TaskResultDavidboxGetDevicesInfo result){
		TheDavidBoxClientException clientException = result.getTheDavidBoxClientException();
		if (null != clientException){
			//Show the exception information
			showDialogOperationProblem(context, clientException.getMessage(), clientException.getRequest(), clientException.getResponse());
		}
	}

	/**
	 * Check if there has been a problem in the result of an async task opertaion.
	 * @param result the async task result
	 * @return if there was a problem
	 */
	public static final boolean isOperationProblem(NmtTaskResult result){
		boolean isProblem = false;
		
		TheDavidBoxClientException clientException = result.getTheDavidBoxClientException();
		if (null != clientException){
			isProblem = true;
		} else if (null != result.getDavidBoxResponse()){
			DavidBoxResponse response = result.getDavidBoxResponse();
			//No exception, but if response is not valid
			if (!response.isValid()){
				isProblem = true;
			}
		}
		return isProblem;
	}

//	/**
//	 * Check if there has been a problem in the result of an async task TaskResultDavidboxGetDevicesInfo opertaion.
//	 * @param result the async task result
//	 * @return if there was a problem
//	 */
//	public static final boolean isOperationProblem(TaskResultDavidboxGetDevicesInfo result){
//		boolean isProblem = false;
//		
//		TheDavidBoxClientException clientException = result.getTheDavidBoxClientException();
//		if (null != clientException){
//			isProblem = true;
//		}
//		return isProblem;
//	}

	/**
	 * Convert seconds into human hh:mm:ss
	 * @param secs number of seconds
	 * @return time in human hh:mm:ss
	 */
	public static final String formatSeconds(int secs) {

		int hours = secs / 3600, 
		remainder = secs % 3600, 
		minutes = remainder / 60, 
		seconds = remainder % 60;

		String disHour = (hours < 10 ? "0" : "") + hours, disMinu = (minutes < 10 ? "0"
				: "")
				+ minutes, disSec = (seconds < 10 ? "0" : "") + seconds;
		
		String time = disHour + ":" + disMinu + ":" + disSec;
		
		return time;
	}
	
}
