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
package com.vikingbrain.dmt.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.vikingbrain.dmt.dao.LinkDAO;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.Link;
import com.vikingbrain.dmt.pojo.TypeLink;
import com.vikingbrain.dmt.pojo.TypeLinkStatus;
import com.vikingbrain.dmt.service.LinkService;

/**
 * Service operations for web app links.
 * 
 * @author Rafael Iñigo
 */
public class LinkServiceImpl implements LinkService {

	/** The DAO. */
	private LinkDAO linkDAO;
	
	/**
	 * Constructor.
	 * @param linkDAO the DAO.
	 */
	public LinkServiceImpl(LinkDAO linkDAO) {
		super();
		this.linkDAO = linkDAO;
	}

	/** {@inheritDoc} */
	public Link getLink(long _id){
		return linkDAO.getById(_id);
	}	
	
	/** {@inheritDoc} */
	public long insert(Link link){
		return linkDAO.insert(link);
	}

	/** {@inheritDoc} */
	public long saveOrUpdate(Link link){
		if (null == link.getId()){
			return linkDAO.insert(link);
		} else {
			return linkDAO.update(link);
		}
	}
	
	/** {@inheritDoc} */
	public List<Link> getAll(){
		return linkDAO.getAll();
	}
	
	/** {@inheritDoc} */
	public List<Link> getLinksShowables(){
		List<Link> listaCompleta = getAll();
		List<Link> listaMostrables = new ArrayList<Link>();
		for (Link link : listaCompleta){
			if (TypeLinkStatus.SHOW == link.getTypeLinkStatus()){	 
				//If it is showable
				listaMostrables.add(link);
			}
		}
				
		return listaMostrables;		
	}

	/** {@inheritDoc} */
	public long insertFavorite(String name, String urlEnd, int authentication, String newUserName, String newPassword){
		Link link = new Link(null, Constants.ICON_STAR, name, urlEnd, urlEnd, 
				TypeLinkStatus.SHOW, TypeLink.CUSTOM, authentication, newUserName, newPassword);
		return insert(link);
	}
	
	/** {@inheritDoc} */
	public int delete(long id){
		return linkDAO.delete(id);
	}	
	
	/** {@inheritDoc} */
	public Link getTransmissionLink(){
		return linkDAO.getTransmissionLink();
	}
	
}
