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
import com.vikingbrain.dmt.dao.LinkDAO;
import com.vikingbrain.dmt.dao.GenericDAO.TypeOrder;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.Link;
import com.vikingbrain.dmt.pojo.TypeLink;
import com.vikingbrain.dmt.pojo.TypeLinkStatus;
import com.vikingbrain.dmt.pojo.TypeWebClient;

/**
 * Implementation of web app links DAO.
 * 
 * @author Rafael Iñigo
 */
public class LinkDAOImpl implements LinkDAO {

	/** Generic DAO. */
	private GenericDAO genericDAO;

	/**
	 * Constructor.
	 * @param ctx android context
	 */
    public LinkDAOImpl(Context ctx){
        super();
        genericDAO = GenericDAO.getInstance(ctx);
    }

    /**
     * Build a web app link object form a cursor.
     * @param cursor the cursor
     * @return the link
     */
    private Link buildObjectFromCursor(Cursor cursor){
    	Link link = null;

        int idColumn = cursor.getColumnIndex(Constants.PRIMARY_KEY_ID); 
        int imageColumn = cursor.getColumnIndex(Link.COL_IMAGE); 
        int nameColumn = cursor.getColumnIndex(Link.COL_NAME);            
        int urlEndColumn = cursor.getColumnIndex(Link.COL_URL_END); 
        int urlDefaultColumn = cursor.getColumnIndex(Link.COL_URL_DEFAULT); 
        int statusColumn = cursor.getColumnIndex(Link.COL_STATUS); 
        int linkTypeColumn = cursor.getColumnIndex(Link.COL_LINK_TYPE);
        int authenticationColumn = cursor.getColumnIndex(Link.COL_AUTHENTICATION);
        int userNameColumn = cursor.getColumnIndex(Link.COL_USER_NAME);
        int passwordColumn = cursor.getColumnIndex(Link.COL_PASSWORD);

        
        long id = cursor.getLong(idColumn); 
        String image = cursor.getString(imageColumn); 
        String name = cursor.getString(nameColumn);            
        String urlEnd = cursor.getString(urlEndColumn); 
        String urlDefault = cursor.getString(urlDefaultColumn);                       
        TypeLinkStatus estado = TypeLinkStatus.findById(cursor.getInt(statusColumn));
        TypeLink tipoLink = TypeLink.findById(cursor.getInt(linkTypeColumn));
        Integer authentication = cursor.getInt(authenticationColumn);
        String userName = cursor.getString(userNameColumn);            
        String password = cursor.getString(passwordColumn);            
        		
        link = new Link(id, image, name, urlEnd, urlDefault, estado, tipoLink, authentication, userName, password);

        return link;
    }

    /** {@inheritDoc} */
    public Link getById(long id){
    	Cursor cursor = genericDAO.get(Link.TABLE_NAME, Link.COLUMNS_NAMES, id);
    	Link link = null;
    	if (cursor != null){            
    		link = buildObjectFromCursor(cursor);
            cursor.close();
    	}
        return link;
    }
    
    /**
     * Create an Android ContentValues with column names and object attributes.
     * @param link the link
     * @return the content values object
     */
    private ContentValues createContentValues(Link link){
		ContentValues values = new ContentValues();
		
		values.put(Link.COL_IMAGE, link.getImage());
		values.put(Link.COL_NAME, link.getName());
		values.put(Link.COL_URL_END, link.getUrlEnd());
		values.put(Link.COL_URL_DEFAULT, link.getUrlDefault());
		values.put(Link.COL_STATUS, link.getTypeLinkStatus().getId());
		values.put(Link.COL_LINK_TYPE, link.getTypeLink().getId());
		values.put(Link.COL_AUTHENTICATION, link.getAuthentication());
		values.put(Link.COL_USER_NAME, link.getUserName());
		values.put(Link.COL_PASSWORD, link.getPassword());

		return values;
    }
    
    
    /** {@inheritDoc} */
	public long insert(Link link){
		ContentValues values = createContentValues(link);
		return genericDAO.insert(Link.TABLE_NAME, values);
	}

	/** {@inheritDoc} */
    public int update(Link link) {
    	long id = link.getId();    	
    	ContentValues values = createContentValues(link);		
        return genericDAO.update(Link.TABLE_NAME, id, values);
    }

    /** {@inheritDoc} */
	public List<Link> getAll(){
		List<Link> list = new ArrayList<Link>();
		Cursor cursor = null;		
		
		if(genericDAO != null){            			
            
            cursor = genericDAO.get(Link.TABLE_NAME, Link.COLUMNS_NAMES, Link.COL_NAME, TypeOrder.ASC); //OrberBy columna NAME  ASC          
                        
            if(cursor != null){
            	                    
                int count = cursor.getCount();                                  
                
                for(int i=0; i<count; i++){                                        
                    Link link = buildObjectFromCursor(cursor);                    
                    list.add(link);                    
                    cursor.moveToNext();
                }
            }
            
            cursor.close();		
		}
        return list;
	}

	/** {@inheritDoc} */
    public int delete(long id) {
        return genericDAO.delete(Link.TABLE_NAME, id);
    }

    /** {@inheritDoc} */
    public Link getTransmissionLink(){
    	String transmissionAppName = TypeWebClient.TRANSMISSION.getCodename();
    	
    	Cursor cursor = genericDAO.get(Link.TABLE_NAME, Link.COLUMNS_NAMES, Link.COL_NAME +" = '"+ transmissionAppName +"'");
    	Link link = null;
    	if (cursor != null){
			int idColumn = cursor.getColumnIndex(Constants.PRIMARY_KEY_ID);	
			long id = cursor.getLong(idColumn); 
            cursor.close();
            link = getById(id); //get link object with find by id
    	}
        return link;  
    }

}
