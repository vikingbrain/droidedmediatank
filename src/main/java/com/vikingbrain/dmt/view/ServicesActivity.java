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
package com.vikingbrain.dmt.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.TypeServiceStatus;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.view.asynctaks.TaskNmtGetServiceStatus;
import com.vikingbrain.dmt.view.asynctaks.TaskNmtListNmtServices;
import com.vikingbrain.dmt.view.asynctaks.TaskNmtSetServiceStatus;
import com.vikingbrain.dmt.view.util.IconifiedService;
import com.vikingbrain.dmt.view.util.IconifiedServiceListAdapter;
import com.vikingbrain.nmt.responses.system.ResponseGetNmtServiceStatus;
import com.vikingbrain.nmt.responses.system.ResponseListNmtServices;

/**
 * Activity for managing the NMT services.
 * 
 * @author Rafael Iñigo
 */
public class ServicesActivity extends BaseActivity implements TaskNmtListNmtServices.OnResultTaskNmtListNmtServices,
		TaskNmtGetServiceStatus.OnResultTaskNmtGetServiceStatus,
		TaskNmtSetServiceStatus.OnResultTaskNmtSetServiceStatus {

	private DavidBoxService davidBoxService;
	
	private TextView mEmptyText;
	private ProgressBar mProgressBar;
	private ListView list;

	private List<IconifiedService> isl =  new ArrayList<IconifiedService>();

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	    
	    	    
	    setContentView(R.layout.activity_services);
	    
	    davidBoxService = DMTApplication.getDavidBoxService();
	    
	    list = (ListView)findViewById(R.id.listServices);
		
		mProgressBar = (ProgressBar) findViewById(R.id.scan_progress);
		mEmptyText = (TextView) findViewById(R.id.empty_text);
		
		refreshListContent();
	}

	private void refreshListContent(){
		//show the progress icon
		mProgressBar.setVisibility(View.VISIBLE);
		//get the content for the list
		getListContent();
	}
	
	private void getListContent(){
		new TaskNmtListNmtServices(this, this, davidBoxService).execute();
	}

	@Override
	public void onResultTaskNmtListNmtServices(ResponseListNmtServices response) {
		List<String> serviceNames = response.getServices();
		refreshListContent(serviceNames);
	}

	private void getServiceStatus(String serviceName){
		new TaskNmtGetServiceStatus(this, this, davidBoxService).execute(serviceName);
	}

	@Override
	public void onResultTaskNmtGetServiceStatus(String serviceName, ResponseGetNmtServiceStatus response) {
					
		ResponseGetNmtServiceStatus.ServiceStatusType statusType = response.getServiceStatus();
		
		//init the service status value
		TypeServiceStatus status = TypeServiceStatus.UNKNOWN;
		if (ResponseGetNmtServiceStatus.ServiceStatusType.RUNNING.equals(statusType)){
			status = TypeServiceStatus.RUNNING;
		} else if (ResponseGetNmtServiceStatus.ServiceStatusType.STOPPED.equals(statusType)){
			status = TypeServiceStatus.STOPPED;
		} else if (ResponseGetNmtServiceStatus.ServiceStatusType.UNKNOWN.equals(statusType)){
			status = TypeServiceStatus.UNKNOWN;
		}
		
		IconifiedService rowService = getRowByServiceNameFromAdapter(serviceName);
		if (null != rowService){
			rowService.setStatus(status);
			//notify the list has changed
			((IconifiedServiceListAdapter)list.getAdapter()).notifyDataSetChanged();
		}
		//Go on searching for the next service with status "loading.." to make the request
		//and retrieve its status
		refreshServicesStatus();		
	}

	private void setServiceStatus(String serviceName, boolean newStatus){
		new TaskNmtSetServiceStatus(this, this, davidBoxService).execute(serviceName, newStatus);
	}

	@Override
	public void onResultTaskNmtSetServiceStatus(String serviceName){		
		//Do nothing, user will need to click the refresh button
	}

	/**
	 * Get the first service with "loading..." and make the request to get the status
	 */
	private void refreshServicesStatus(){
		List<IconifiedService> items = getItemsFromAdapter();
		for (IconifiedService is : items){
			if (TypeServiceStatus.LOADING.equals(is.getStatus())){
				getServiceStatus(is.getName());
				break;
			}
		}
	}
	
	private IconifiedService getRowByServiceNameFromAdapter(String serviceName){
		IconifiedService rowFound = null;	
		List<IconifiedService> items = getItemsFromAdapter();
		for (IconifiedService is : items){			
			if (serviceName.equals(is.getName())){
				rowFound = is;
			}
		}
		return rowFound;					
	}
	
	private void refreshListContent(List<String> serviceNames){
		//hide the progress
		mProgressBar.setVisibility(View.GONE);
		
		if (null == serviceNames){
			mEmptyText.setVisibility(View.VISIBLE);
		} else if (serviceNames.isEmpty()){
			mEmptyText.setVisibility(View.VISIBLE);
		} else {
			isl = adapt(serviceNames);		
			IconifiedServiceListAdapter isla = new IconifiedServiceListAdapter(this);
			isla.setListItems(isl, list.hasTextFilter());			
			list.setAdapter(isla);
			list.setTextFilterEnabled(true);

			//get the status for the first row with "loading..."
			refreshServicesStatus();
		}		
	}	

	private List<IconifiedService> adapt(List<String> serviceNames){
		List<IconifiedService> isl = new ArrayList<IconifiedService>();
		for (String serviceName: serviceNames){						
			isl.add(new IconifiedService(serviceName, TypeServiceStatus.LOADING, null));									
		}
		return isl;	
	}

	private List<IconifiedService> getItemsFromAdapter(){
		//initialize so not return null
		List<IconifiedService> items = new ArrayList<IconifiedService>();
		//Use only if the list has been loaded, if not, wait until next call
		if (null != list.getAdapter()){		
			IconifiedServiceListAdapter isla = (IconifiedServiceListAdapter)list.getAdapter();
			items = (List<IconifiedService>)isla.getItems();
		}
		return items;
	}

	public void onClickChangeRowStatus(View v) {
		
		if (null != v){
			IconifiedService rowService = (IconifiedService)v.getTag();
			if (null != rowService){
				String serviceName = rowService.getName();
				
				TypeServiceStatus currentStatus = rowService.getStatus();
				boolean  newStatus = true; //default value just in case
				if (TypeServiceStatus.RUNNING.equals(currentStatus)){
					newStatus = false;				
				} else if (TypeServiceStatus.STOPPED.equals(currentStatus)){
					newStatus = true;					
				}
				
				//Put in loading status so the are going ask later for their status after de request for
				//change the status
				rowService.setStatus(TypeServiceStatus.SENDING_COMMAND);
				//notify the list has changed
				((IconifiedServiceListAdapter)list.getAdapter()).notifyDataSetChanged();
				
				//make http request to change the satus
				setServiceStatus(serviceName, newStatus);				
			}
		}	
	}	
	
	public void onClickRefreshStatus(View v){
		if (null != v){
			IconifiedService rowService = (IconifiedService)v.getTag();
			if (null != rowService){
				String serviceName = rowService.getName();
				
				//Put in loading status so the are going ask later for their status after de request for
				//change the status
				rowService.setStatus(TypeServiceStatus.LOADING);
				//notify the list has changed
				((IconifiedServiceListAdapter)list.getAdapter()).notifyDataSetChanged();
				
				getServiceStatus(serviceName);
			}
		}
	}
}
