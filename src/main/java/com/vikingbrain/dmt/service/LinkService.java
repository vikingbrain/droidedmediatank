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
package com.vikingbrain.dmt.service;

import java.util.List;

import com.vikingbrain.dmt.pojo.Link;

/**
 * Interface with service operation for web app links.
 * 
 * @author Rafael Iñigo
 */
public interface LinkService {

	/**
	 * Get link by id.
	 * @param id the id
	 * @return the link
	 */
	Link getLink(long id);
	
	/**
	 * Insert a new link.
	 * @param link the link
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	long insert(Link link);
	
	/**
	 * Save or update a link
	 * @param link the link
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	long saveOrUpdate(Link link);
	
	/**
	 * Get all links.
	 * @return all links
	 */
	List<Link> getAll();

	/**
	 * Get links marked as not hidden.
	 * @return a list of links
	 */
	List<Link> getLinksShowables();	
	
	/**
	 * Insert a new favorite link.
	 * @param name the name
	 * @param urlEnd the ending section of the url
	 * @param authentication if it needs authentication
	 * @param newUserName the username
	 * @param newPassword the password
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	long insertFavorite(String name, String urlEnd, int authentication, String newUserName, String newPassword);
	
	/**
	 * Delete a link.
	 * @param id the id
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise
	 */
	int delete(long id);	
	
	/**
	 * Get the link for the transmission web client.
	 * @return the transmission link
	 */
	Link getTransmissionLink();
}
