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
package com.vikingbrain.dmt.service.util;

/**
 * Holds the information for the transmission connection.
 * 
 * @author Rafael Iñigo
 */
public class TransmissionConnectionData {

	private String host;
	private int port;
	private String user;
	private String password;
	private String transmissionUrl;
	private boolean isAuthentication;
	private String textWrongData;
	
	/**
	 * Getter of property.
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}
	
	/**
	 * Setter of property.
	 * @param host the host
	 */
	public final void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Getter of property.
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}
	
	/**
	 * Setter of property.
	 * @param port the port
	 */
	public final void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Getter of property.
	 * @return the user name
	 */
	public final String getUser() {
		return user;
	}
	
	/**
	 * Setter of property.
	 * @param user the user name
	 */
	public final void setUser(String user) {
		this.user = user;
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
	
	/**
	 * Getter of property.
	 * @return the url for transmission
	 */
	public final String getTransmissionUrl() {
		return transmissionUrl;
	}
	
	/**
	 * Setter of property.
	 * @param transmissionUrl the url for transmission
	 */
	public final void setTransmissionUrl(String transmissionUrl) {
		this.transmissionUrl = transmissionUrl;
	}
	
	/**
	 * Check if authentication is required.
	 * @return if authentication is required
	 */
	public final boolean isAuthentication() {
		return isAuthentication;
	}
	
	/**
	 * Setter of property.
	 * @param isAuthentication if authentication is required
	 */
	public final void setAuthentication(boolean isAuthentication) {
		this.isAuthentication = isAuthentication;
	}
	
	/**
	 * Getter of property.
	 * @return the text for wrong data
	 */
	public String getTextWrongData() {
		return textWrongData;
	}
	
	/**
	 * Setter of property
	 * @param textWrongData text for wrong data
	 */
	public void setTextWrongData(String textWrongData) {
		this.textWrongData = textWrongData;
	}
	
}
