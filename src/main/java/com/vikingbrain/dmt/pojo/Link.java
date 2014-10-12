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
package com.vikingbrain.dmt.pojo;


/**
 * POJO for the web app link.
 * 
 * @author Rafael Iñigo
 */
public class Link {
	
    public static final String TABLE_NAME = "link";       
    
    public static final String COL_IMAGE = "image";
    public static final String COL_NAME = "name";
    public static final String COL_URL_END = "urlEnd";
    public static final String COL_URL_DEFAULT = "urlDefault";
    public static final String COL_STATUS = "status";
    public static final String COL_LINK_TYPE = "linkType";
    public static final String COL_AUTHENTICATION = "authentication";
    public static final String COL_USER_NAME = "userName";
    public static final String COL_PASSWORD = "password";
    
    public static String[] COLUMNS_NAMES = {Constants.PRIMARY_KEY_ID, COL_IMAGE,COL_NAME,COL_URL_END,COL_URL_DEFAULT,
    				COL_STATUS,COL_LINK_TYPE, COL_AUTHENTICATION, COL_USER_NAME, COL_PASSWORD};
    
    private Long id;
    private String image;
    private String name;
    private String urlEnd;
    private String urlDefault;
    private TypeLinkStatus typeLinkStatus;
    private TypeLink typeLink;
    
    private Integer authentication; //1=true, 0=false
    private String userName;
    private String password;
    
    /**
     * Constructor.
     * @param id the primary key
     * @param image the image
     * @param name the name
     * @param urlEnd the ending section of the url
     * @param urlDefault the default url
     * @param tipoEstado type of status
     * @param tipoLink type of link
     * @param authentication if authentication is required
     * @param userName the username
     * @param password the password
     */
	public Link(Long id, String image, String name, String urlEnd, String urlDefault, 
			TypeLinkStatus typeLinkStatus, TypeLink typeLink, Integer authentication, String userName, String password) {
		super();
		this.id = id;
		this.image = image;
		this.name = name;
		this.urlEnd = urlEnd;
		this.urlDefault = urlDefault;
		this.typeLinkStatus = typeLinkStatus;
		this.typeLink = typeLink;
		this.authentication = authentication;
		this.userName = userName;
		this.password = password;
	}
	
	/**
	 * Getter of property.
	 * @return the primary key
	 */
	public final Long getId() {
        return id;
    }
	
	/**
	 * Setter of property.
	 * @param id the primary key
	 */
    public final void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Getter of property.
     * @return the image
     */
	public final String getImage() {
		return image;
	}
	
	/**
	 * Setter of property.
	 * @param image the image
	 */
	public final void setImage(String image) {
		this.image = image;
	}
	
	/**
	 * Getter of property.
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Setter of property.
	 * @param name the name
	 */
	public final void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Getter of property.
	 * @return the end section for the url
	 */
	public final String getUrlEnd() {
		return urlEnd;
	}
	
	/**
	 * Setter of property.
	 * @param urlEnd the end section for the url
	 */
	public final void setUrlEnd(String urlEnd) {
		this.urlEnd = urlEnd;
	}
	
	/**
	 * Getter of property.
	 * @return the default url
	 */
	public final String getUrlDefault() {
		return urlDefault;
	}
	
	/**
	 * Setter of property.
	 * @param urlDefault the default url
	 */
	public final void setUrlDefault(String urlDefault) {
		this.urlDefault = urlDefault;
	}
	
	/**
	 * Getter of property.
	 * @return the type of link status
	 */
	public final TypeLinkStatus getTypeLinkStatus() {
		return typeLinkStatus;
	}

	/**
	 * Setter of property.
	 * @param typeLinkStatus the type of link status
	 */
	public final void setTypeLinkStatus(TypeLinkStatus typeLinkStatus) {
		this.typeLinkStatus = typeLinkStatus;
	}

	/**
	 * Getter of property.
	 * @return type of link
	 */
	public final TypeLink getTypeLink() {
		return typeLink;
	}

	/**
	 * Setter of property.
	 * @param typeLink type of link
	 */
	public final void setTypeLink(TypeLink typeLink) {
		this.typeLink = typeLink;
	}
	
	/**
	 * Getter of property.
	 * @return if authentication is required
	 */
	public final Integer getAuthentication() {
		return authentication;
	}

	/**
	 * Setter of property.
	 * @param authentication if authentication is required
	 */
	public final void setAuthentication(Integer authentication) {
		this.authentication = authentication;
	}

	/**
	 * Check if authentication is required
	 * @return if authentication is required
	 */
	public final boolean isAuthentication() {
		return (this.authentication == Constants.NUMBER_ONE) ? true : false;
	}

	/**
	 * Getter of property.
	 * @return the user name
	 */
	public final String getUserName() {
		return userName;
	}

	/**
	 * Setter of property.
	 * @param userName the user name
	 */
	public final void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Getter of property.
	 * @return the password
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * Setter of property.
	 * @param password the password
	 */
	public final void setPassword(String password) {
		this.password = password;
	}

}
