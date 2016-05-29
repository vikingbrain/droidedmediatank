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
 * POJO for the configuration.
 * 
 * @author Rafael Iñigo
 */
public class Config {

    public static final String TABLE_NAME = "config";       
    
    public static final String COL_EULA = "eula";        
	public static final String COL_DEFAULT_ZOOM = "defaultZoom";
	public static final String COL_SHOW_CONSOLSE = "showConsole";
	public static final String COL_SHOW_NMJ = "showNmj";
	public static final String COL_ACTIVE_PROFILE = "activeProfile";
	
    public static String[] COLUMNS_NAMES = {Constants.PRIMARY_KEY_ID, COL_EULA, COL_DEFAULT_ZOOM, COL_SHOW_CONSOLSE, COL_SHOW_NMJ, COL_ACTIVE_PROFILE };

    private Long id;
    private Integer eula; //1=true, 0=false        
	private TypeZoom defaultZoom;
	private Integer showConsole; //1=true, 0=false
	private Integer showNmj; //1=true, 0=false		
	private Long activeProfile; //Foreign key
	
	/**
	 * Constructor.
	 * @param id the primary key
	 * @param eula eula accepted
	 * @param defaultZoom default zoom
	 * @param showConsole if show console
	 * @param showNmj if show nmj manager
	 * @param activeProfile foreign key active profile
	 */
	public Config(Long id, Integer eula, TypeZoom defaultZoom, Integer showConsole, Integer showNmj, Long activeProfile) {
		super();
		this.id = id;
		this.eula = eula;
		this.defaultZoom = defaultZoom;
		this.showConsole = showConsole;
		this.showNmj = showNmj;
		this.activeProfile = activeProfile;
	}

	/**
	 * Getter of property.
	 * @return the primary key
	 */
	public Long getId() {
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
	 * @return if EULA accepted
	 */
	public Integer getEula() {
		return eula;
	}	
	
	/**
	 * Setter of property.
	 * @param eula
	 */
	public void setEula(Integer eula) {
		this.eula = eula;
	}		
	
	/**
	 * Check if EULA is accepted.
	 * @return if eula is accepted
	 */
	public final boolean isEulaAccepted() {
		return (this.eula == 1) ? true : false;
	}

	/**
	 * Getter of property.
	 * @return default zoom
	 */
	public TypeZoom getDefaultZoom(){
		return defaultZoom;
	}
	
	/**
	 * Setter of property.
	 * @param defaultZoom
	 */
	public void setDefaultZoom(TypeZoom defaultZoom){
		this.defaultZoom = defaultZoom;
	}

	/**
	 * Getter of property.
	 * @return if show console
	 */
	public Integer getShowConsole() {
		return showConsole;
	}	
	
	/**
	 * Setter of property.
	 * @param showConsole
	 */
	public void setShowConsole(Integer showConsole) {
		this.showConsole = showConsole;
	}		
	
	/**
	 * Check if show console.
	 * @return if show console
	 */
	public final boolean isShowConsole() {
		return (this.showConsole == 1) ? true : false;
	}

	/**
	 * Getter of property.
	 * @return if show NMJ
	 */
	public Integer getShowNmj() {
		return showNmj;
	}

	/**
	 * Setter of property.
	 * @param showNmj if show NMJ
	 */
	public void setShowNmj(Integer showNmj) {
		this.showNmj = showNmj;
	}

	/**
	 * Check if show NMJ.
	 * @return if show NMJ
	 */
	public final boolean isShowNmj() {
		return (this.showNmj == 1) ? true : false;
	}

	/**
	 * Getter of property.
	 * @return id of active profile
	 */
	public Long getActiveProfile() {
		return activeProfile;
	}

	/**
	 * Setter of property.
	 * @param activeProfile the id of the active profile
	 */
	public void setActiveProfile(Long activeProfile) {
		this.activeProfile = activeProfile;
	}

}