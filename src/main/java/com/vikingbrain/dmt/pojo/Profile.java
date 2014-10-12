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
 * POJO for the profile.
 * 
 * @author Rafael Iñigo
 */
public class Profile {

    public static final String TABLE_NAME = "profile";       
    
	public static final String COL_NAME = "name";
    public static final String COL_TYPE_NMT = "typeNmt";        
    public static final String COL_IP_NMT = "ipNmt";
    public static final String COL_FTP_PORT = "ftpPort";
    public static final String COL_FTP_USER = "ftpUser";
    public static final String COL_FTP_PASSWORD = "ftpPassword";
    public static final String COL_FTP_NMT_DRIVE_NAME = "ftpNmtDriveName";	            
	public static final String COL_MYIHOME_ACTIVE = "myihomeActive";
	public static final String COL_LLINK_ACTIVE = "llinkActive";
	public static final String COL_LLINK_PORT = "llinkPort";
    public static final String COL_IP_WEB_CLIENTS = "ipWebClients";

    public static String[] COLUMNS_NAMES = {Constants.PRIMARY_KEY_ID, COL_NAME, COL_TYPE_NMT, COL_IP_NMT, COL_FTP_PORT, COL_FTP_USER, COL_FTP_PASSWORD, COL_FTP_NMT_DRIVE_NAME,
    	COL_MYIHOME_ACTIVE, COL_LLINK_ACTIVE, COL_LLINK_PORT, COL_IP_WEB_CLIENTS};

    private Long id;
    private String name;
    private String typeNmt;
    private String ipNmt;
    private String ftpPort;
    private String ftpUser;
    private String ftpPassword;
    private String ftpNmtDriveName;
	private Integer myihomeActive;
	private Integer llinkActive;
	private String llinkPort;			
	private String ipWebClients; //OPTIONAL Web clients ip or domain name
	
	/**
	 * Constructor.
	 * @param id the primary key
	 * @param name the name
	 * @param typeNmt type of NMT
	 * @param ipNmt the IP
	 * @param ftpPort the port
	 * @param ftpUser the user name
	 * @param ftpPassword the password
	 * @param ftpNmtDriveName ftp drive name
	 * @param myihomeActive if myiHome is active
	 * @param llinkActive if Llink is active
	 * @param llinkPort port for Llink
	 * @param ipWebClients IP for web clients
	 */
	public Profile(Long id, String name, String typeNmt, String ipNmt, String ftpPort, String ftpUser, String ftpPassword, 
			String ftpNmtDriveName, Integer myihomeActive, Integer llinkActive, String llinkPort, String ipWebClients) {
		super();
		this.id = id;
		this.name = name;
		this.typeNmt = typeNmt;
		this.ipNmt = ipNmt;
		this.ftpPort = ftpPort;
		this.ftpUser = ftpUser;
		this.ftpPassword = ftpPassword;
		this.ftpNmtDriveName = ftpNmtDriveName;
		this.myihomeActive = myihomeActive;
		this.llinkActive = llinkActive;
		this.llinkPort = llinkPort;
		this.ipWebClients = ipWebClients;
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
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Setter of property.
	 * @param name the name
	 */
	public final void setName(String name){
		this.name = name;
	}

	/**
	 * Getter of property.
	 * @return type of NMT
	 */
	public String getTypeNmt() {
		return typeNmt;
	}

	/**
	 * Setter of property.
	 * @param typeNmt type of NMT
	 */
	public void setTypeNmt(String typeNmt) {
		this.typeNmt = typeNmt;
	}

	/**
	 * Getter of property.
	 * @return the IP
	 */
	public String getIpNmt() {
		return ipNmt;
	}

	/**
	 * Setter of property.
	 * @param ipNmt the IP
	 */
	public void setIpNmt(String ipNmt) {
		this.ipNmt = ipNmt;
	}

	/**
	 * Getter of property.
	 * @return the FTP port
	 */
	public String getFtpPort() {
		return ftpPort;
	}

	/**
	 * Setter of property.
	 * @param ftpPort the FTP port
	 */
	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}

	/**
	 * Getter of property.
	 * @return the FTP user name
	 */
	public String getFtpUser() {
		return ftpUser;
	}

	/**
	 * Setter of property.
	 * @param ftpUser the FTP user name
	 */
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	/**
	 * Getter of property.
	 * @return the FTP password
	 */
	public String getFtpPassword() {
		return ftpPassword;
	}

	/**
	 * Setter of property.
	 * @param ftpPassword the FTP password
	 */
	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	/**
	 * Getter of property.
	 * @return the FTP drive name
	 */
	public String getFtpNmtDriveName() {
		return ftpNmtDriveName;
	}

	/**
	 * Setter of property.
	 * @param ftpNmtDriveName the FTP drive name
	 */
	public void setFtpNmtDriveName(String ftpNmtDriveName) {
		this.ftpNmtDriveName = ftpNmtDriveName;
	}
	
	/**
	 * Getter of property.
	 * @return if myIhome is active
	 */
	public Integer getMyihomeActive() {
		return myihomeActive;
	}

	/**
	 * Setter of property.
	 * @param myihomeActive if myIhome is active
	 */
	public void setMyihomeActive(Integer myihomeActive) {
		this.myihomeActive = myihomeActive;
	}

	/**
	 * Check if myIhome is active.
	 * @return if myIhome is active
	 */
	public final boolean isMyihomeActive() {
		return (this.myihomeActive == 1) ? true : false;
	}	
	
	/**
	 * Getter of property.
	 * @return if Llink is active
	 */
	public Integer getLlinkActive() {
		return llinkActive;
	}
	
	/**
	 * Setter of property.
	 * @param llinkActive if Llink is active
	 */
	public void setLlinkActive(Integer llinkActive){
		this.llinkActive = llinkActive;
	}

	/**
	 * Check if Llink is active
	 * @return if Llink is active
	 */
	public final boolean isLlinkActive() {
		return (this.llinkActive == 1) ? true : false;
	}

	/**
	 * Getter of property.
	 * @return Llink port
	 */
	public String getLlinkPort() {
		return llinkPort;
	}

	/**
	 * Setter of property.
	 * @param llinkPort the Llink port
	 */
	public void setLlinkPort(String llinkPort) {
		this.llinkPort = llinkPort;
	}

	/**
	 * Getter of property.
	 * @return IP for web clients
	 */
	public String getIpWebClients() {
		return ipWebClients;
	}

	/**
	 * Setter of property.
	 * @param ipWebClients IP for web clients
	 */
	public void setIpWebClients(String ipWebClients) {
		this.ipWebClients = ipWebClients;
	}
	
}