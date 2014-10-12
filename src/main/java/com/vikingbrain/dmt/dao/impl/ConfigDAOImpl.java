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
package com.vikingbrain.dmt.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.vikingbrain.dmt.dao.ConfigDAO;
import com.vikingbrain.dmt.dao.GenericDAO;
import com.vikingbrain.dmt.pojo.Config;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.TypeZoom;

/**
 * Implementation of configuration DAO. There is only one row in table configuration,
 * this row will keep all common config configuration.
 * 
 * @author Rafael Iñigo
 */
public class ConfigDAOImpl implements ConfigDAO {

	/** Generic DAO. */
	private GenericDAO genericDAO;
	
	/**
	 * Constructor.
	 * @param ctx android context
	 */
    public ConfigDAOImpl(Context ctx){
        super();
        genericDAO = GenericDAO.getInstance(ctx);
    }

    /**
     * Build a config object form a cursor.
     * @param cursor the cursor
     * @return the config
     */
    private Config buildObjectFromCursor(Cursor cursor){

    	Config config = null;
    	
        int idColumn = cursor.getColumnIndex(Constants.PRIMARY_KEY_ID);    		
        int eulaColumn = cursor.getColumnIndex(Config.COL_EULA);
		int defaultZoomColumn = cursor.getColumnIndex(Config.COL_DEFAULT_ZOOM);
		int showConsoleColumn = cursor.getColumnIndex(Config.COL_SHOW_CONSOLSE);
		int showNmjColumn = cursor.getColumnIndex(Config.COL_SHOW_NMJ);
		int activeProfileColumn = cursor.getColumnIndex(Config.COL_ACTIVE_PROFILE);

        long id = cursor.getLong(idColumn); 
        Integer eula = cursor.getInt(eulaColumn);            
		Integer defaultZoom = cursor.getInt(defaultZoomColumn);
		TypeZoom tipoZoom = TypeZoom.findById(defaultZoom);			
		Integer showConsole = cursor.getInt(showConsoleColumn);      
		Integer showNmj = cursor.getInt(showNmjColumn);
        long activeProfile = cursor.getLong(activeProfileColumn); 
		
		config = new Config(id, eula, tipoZoom, showConsole, showNmj, activeProfile);
		
		return config;
    }
    
    /** {@inheritDoc} */
    public Config getConfig(){
    	Cursor cursor = genericDAO.get(Config.TABLE_NAME, Config.COLUMNS_NAMES, (long)Constants.NUMBER_ONE);
    	Config config = null;
    	if (cursor != null){
    		            
    		config = buildObjectFromCursor(cursor);
            
            cursor.close();
    	}
        return config;    	
    }
    
	/** {@inheritDoc} */
    public int update(Config config) {
    	long id = config.getId();
    	
		ContentValues values = new ContentValues();
		values.put(Config.COL_EULA, config.getEula());		
		values.put(Config.COL_DEFAULT_ZOOM, config.getDefaultZoom().getId());
		values.put(Config.COL_SHOW_CONSOLSE, config.getShowConsole());
		values.put(Config.COL_SHOW_NMJ, config.getShowNmj());
		values.put(Config.COL_ACTIVE_PROFILE, config.getActiveProfile());
		
        return genericDAO.update(Config.TABLE_NAME, id, values);
    }
    
}