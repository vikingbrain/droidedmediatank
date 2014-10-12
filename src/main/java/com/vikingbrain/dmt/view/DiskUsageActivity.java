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

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.view.asynctaks.TaskDavidboxGetDevicesInfo;
import com.vikingbrain.nmt.responses.system.ResponseGetDeviceInfo;

/**
 * Activity with the disk usage graph.
 * 
 * @author Rafael Iñigo
 */
public class DiskUsageActivity extends BaseActivity implements TaskDavidboxGetDevicesInfo.OnResultTaskDavidboxGetDevicesInfo {

	private DavidBoxService davidBoxService;
	LinearLayout chartDisplay;
	ListView listCharts;

	private ProgressBar mProgressBar;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.app_chart);

	    davidBoxService = DMTApplication.getDavidBoxService();
	    
	    chartDisplay = (LinearLayout) findViewById(R.id.chartDisplay);
	    listCharts = (ListView) findViewById(R.id.listCharts);
	    
	    mProgressBar = (ProgressBar) findViewById(R.id.scan_progress);
		mProgressBar.setVisibility(View.VISIBLE);

		//Execute the async task that calls the service
		new TaskDavidboxGetDevicesInfo(this, this, davidBoxService).execute();		
		
//		To show graph in a new intent
//	    Intent achartIntent = createChart(this);
//	    startActivity(achartIntent);	    
//	    finish();
	}
			
	@Override
	public void onResultTaskDavidboxGetDevicesInfo(final List<ResponseGetDeviceInfo> listDeviceInfo) {
		mProgressBar.setVisibility(View.GONE);
		
	    if (null == listDeviceInfo){
	    	showToastError(R.string.nmt_unavailable);
	    } else {
	    	if (listDeviceInfo.size() == 0){
	    		showToastError(R.string.nmt_no_hard_disk_no_usb_drive);		    		
	    	} else if (listDeviceInfo.size() == 1){
	    		//If there is only one device show graph directly
	    		//else, offer a list and show graph on list item click
	    		//There is only one device			    		
	    		showGraphInView(listDeviceInfo.get(Constants.NUMBER_ZERO));
	    	} else {
	    		//There are more than one device
	    		//So we show a list and show graph when the item in the list is clicked
	    		
	    		List<String> texts = new ArrayList<String>();
	    	    for (ResponseGetDeviceInfo deviceInfo : listDeviceInfo){
	    	    	texts.add(deviceInfo.getAccessPath());
	    	    }
	    	    
		    	String[] testValues = texts.toArray(new String[texts.size()]);

		    	// Create a simple array adapter (of type string) with the test values		    			    	
		    	ListAdapter adapter = getSimpleArrayAdapter(testValues);		    			    	
		    	
		    	listCharts.setAdapter(adapter);
		    	listCharts.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
						
						showGraphInView(listDeviceInfo.get(position));
					}
		    		
				});

	    	}	    	
	    }		
	}

	private ListAdapter getSimpleArrayAdapter(String[] testValues){
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testValues);
		return adapter;
	}

	
    /**
     * Builds a category renderer to use the provided colors.
     * @param colors the colors
     * @return the category renderer
     */
    protected DefaultRenderer buildCategoryRenderer(int[] colors) {
      DefaultRenderer renderer = new DefaultRenderer();
      renderer.setLabelsTextSize(15);
      renderer.setLegendTextSize(15);
      renderer.setMargins(new int[] { 20, 30, 15, 0 });
      for (int color : colors) {
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(color);
        renderer.addSeriesRenderer(r);
      }
      return renderer;
    }
    
    private GraphicalView createChart(Context context, ResponseGetDeviceInfo deviceInfo) {
	    int[] colors = new int[] { context.getResources().getColor(R.color.darkRed),
	    		context.getResources().getColor(R.color.verdeOscuro)}; 
	    DefaultRenderer renderer = buildCategoryRenderer(colors);
	    renderer.setZoomButtonsVisible(true);
	    renderer.setZoomEnabled(true);
	    renderer.setChartTitleTextSize(20);
	    	    
	    renderer.setLabelsColor(Color.BLACK);
	    
	    CategorySeries categorySeries = new CategorySeries("");
	    try {
	    	//Title in the graph
		    renderer.setChartTitle(deviceInfo.getAccessPath() + " ("+deviceInfo.getSizeHuman()+")"); //It is the drive name
		    
		    long usedInNumeric = deviceInfo.getSize() - deviceInfo.getFreeDiskSpace();
		    //TODO i18n words 'used' and 'free' 
		    categorySeries.add("Used " + deviceInfo.getUsedDiskSpaceHuman(), usedInNumeric);
		    categorySeries.add("Free " + deviceInfo.getFreeDiskSpaceHuman(), deviceInfo.getFreeDiskSpace());
	    } catch (Exception e) {
	    	showToastError(R.string.error_parsing_data);
		}
	    return ChartFactory.getPieChartView(context, categorySeries, renderer);	
    }
    
    private void showGraphInView(ResponseGetDeviceInfo deviceInfo){
    	chartDisplay.addView(createChart(this, deviceInfo));
    }

}
