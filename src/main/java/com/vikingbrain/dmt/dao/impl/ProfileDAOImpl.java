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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.vikingbrain.dmt.dao.GenericDAO;
import com.vikingbrain.dmt.dao.ProfileDAO;
import com.vikingbrain.dmt.dao.GenericDAO.TypeOrder;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.Profile;

/**
 * Implementation of profiles DAO.
 * 
 * @author Rafael Iñigo
 */
public class ProfileDAOImpl implements ProfileDAO {

	/** Generic DAO. */
	private GenericDAO genericDAO;
	
	/**
	 * Constructor.
	 * @param ctx android context
	 */	
    public ProfileDAOImpl(Context ctx){
        super();
        genericDAO = GenericDAO.getInstance(ctx);
    }

    /**
     * Build a profile object form a cursor.
     * @param cursor the cursor
     * @return the profile
     */
    private Profile buildObjectFromCursor(Cursor cursor){
    	Profile profile = null;
    	
        int idColumn = cursor.getColumnIndex(Constants.PRIMARY_KEY_ID); 
        int nameColumn = cursor.getColumnIndex(Profile.COL_NAME);
        int typeColumn = cursor.getColumnIndex(Profile.COL_TYPE_NMT);            
        int ipColumn = cursor.getColumnIndex(Profile.COL_IP_NMT); 
        int ftpPortColumn = cursor.getColumnIndex(Profile.COL_FTP_PORT);            
        int ftpUserColumn = cursor.getColumnIndex(Profile.COL_FTP_USER);
        int ftpPasswordColumn = cursor.getColumnIndex(Profile.COL_FTP_PASSWORD);
        int ftpNmtDriveNameColumn = cursor.getColumnIndex(Profile.COL_FTP_NMT_DRIVE_NAME);
		int myihomeActiveColumn = cursor.getColumnIndex(Profile.COL_MYIHOME_ACTIVE);
		int llinkActiveColumn = cursor.getColumnIndex(Profile.COL_LLINK_ACTIVE); 
		int llinkPortColumn = cursor.getColumnIndex(Profile.COL_LLINK_PORT);
        int ipWebClientsColumn = cursor.getColumnIndex(Profile.COL_IP_WEB_CLIENTS); 

        long id = cursor.getLong(idColumn); 
        String name = cursor.getString(nameColumn);                        
        String typeNmt = cursor.getString(typeColumn);            
        String ipNmt = cursor.getString(ipColumn); 
        String ftpPort = cursor.getString(ftpPortColumn);
        String ftpUser = cursor.getString(ftpUserColumn);
        String ftpPassword = cursor.getString(ftpPasswordColumn);
        String ftpNmtDriveName = cursor.getString(ftpNmtDriveNameColumn);                        
		Integer myihomeActive = cursor.getInt(myihomeActiveColumn);
		Integer llinkActive = cursor.getInt(llinkActiveColumn);
		String llinkPort = cursor.getString(llinkPortColumn);           
        String ipWebClients = cursor.getString(ipWebClientsColumn);            


		profile = new Profile(id, name, typeNmt, ipNmt, ftpPort, ftpUser, ftpPassword, 
        		ftpNmtDriveName,  myihomeActive, llinkActive, llinkPort, ipWebClients);
		return profile;
    }
    
    /** {@inheritDoc} */
    public Profile getById(long id){
    	Cursor cursor = genericDAO.get(Profile.TABLE_NAME, Profile.COLUMNS_NAMES, id);
    	Profile profile = null;
    	if (cursor != null){

    	    if( cursor.moveToFirst() ){
    	    	profile = buildObjectFromCursor(cursor);
    	    }
            cursor.close();
    	}
        return profile;    	
    }

    /**
     * Create an Android Content values with column names and object attributes.
     * @param profile the profile
     * @return the content values object
     */
    private ContentValues createContentValues(Profile profile){
		ContentValues values = new ContentValues();
		
		values.put(Profile.COL_NAME, profile.getName());
		values.put(Profile.COL_TYPE_NMT, profile.getTypeNmt());
		values.put(Profile.COL_IP_NMT, profile.getIpNmt());
		values.put(Profile.COL_FTP_PORT, profile.getFtpPort());
		values.put(Profile.COL_FTP_USER, profile.getFtpUser());
		values.put(Profile.COL_FTP_PASSWORD, profile.getFtpPassword());
		values.put(Profile.COL_FTP_NMT_DRIVE_NAME, profile.getFtpNmtDriveName());
		values.put(Profile.COL_MYIHOME_ACTIVE, profile.getMyihomeActive());
		values.put(Profile.COL_LLINK_ACTIVE, profile.getLlinkActive());
		values.put(Profile.COL_LLINK_PORT, profile.getLlinkPort());
		values.put(Profile.COL_IP_WEB_CLIENTS, profile.getIpWebClients());

		return values;
    }
    
    /** {@inheritDoc} */
	public long insert(Profile profile){
		ContentValues values = createContentValues(profile);			
		return genericDAO.insert(Profile.TABLE_NAME, values);
	}

	/** {@inheritDoc} */
    public int update(Profile profile) {
    	long id = profile.getId();    	
		ContentValues values = createContentValues(profile);		
        return genericDAO.update(Profile.TABLE_NAME, id, values);
    }

    /** {@inheritDoc} */
	public List<Profile> getAll(){
		List<Profile> list = new ArrayList<Profile>();
		Cursor cursor = null;		
		
		if(genericDAO != null){            			
            
			//OrberBy column NAME ASC
            cursor = genericDAO.get(Profile.TABLE_NAME, Profile.COLUMNS_NAMES, Profile.COL_NAME, TypeOrder.ASC);          
                        
            if(cursor != null){
                    
                int count = cursor.getCount();                                  
                
                for(int i=0; i<count; i++){                    
                	Profile profile = buildObjectFromCursor(cursor);                	
                    list.add(profile);                    
                    cursor.moveToNext();
                }
            }
            
            cursor.close();		
		}
        return list;
	}
	
	/** {@inheritDoc} */
    public int delete(long id) {
        return genericDAO.delete(Profile.TABLE_NAME, id);
    }
    
}