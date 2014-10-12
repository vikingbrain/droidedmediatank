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
package com.vikingbrain.dmt.dao;

import java.util.List;

import com.vikingbrain.dmt.pojo.Link;

/**
 * Interface for web app links DAO.
 * 
 * @author Rafael Iñigo
 */
public interface LinkDAO {		

	/**
	 * Get web app link by id.
	 * @param id the id
	 * @return the web app link
	 */
	Link getById(long id);
	
	/**
	 * Insert a web app link.
	 * @param link the web app link
	 * @return the row ID
	 */
	long insert(Link link);
	
	/**
	 * Update a web app link.
	 * @param link the web app link
	 * @return the row ID
	 */
	int update(Link link);

	/**
	 * Get all web app links.
	 * @return a list of web app links
	 */
	List<Link> getAll();
	
	/**
	 * Delete a web app link by id.
	 * @param id the id
	 * @return the row ID
	 */
	int delete(long id);
	
	/**
	 * Get the Transmission web app link.
	 * @return the Transmission web app link
	 */
	Link getTransmissionLink();
}
