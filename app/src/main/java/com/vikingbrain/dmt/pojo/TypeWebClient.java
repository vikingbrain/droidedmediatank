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

import com.vikingbrain.dmt.R;

public enum TypeWebClient {

	TRANSMISSION("transmission", 
			R.string.layout_transmission, 
			R.drawable.icon_transmission,
			R.string.info_transmission, 
			R.string.info_transmission_homepage, 
			R.string.info_transmission_installation),
	BTCGI("btcgi", 
			R.string.layout_btcgi, 
			R.drawable.icon_btcgi,
			R.string.info_btcgi, 
			R.string.info_btcgi_homepage, 
			R.string.info_btcgi_installation),
	NZBGET("nzbget", 
			R.string.layout_nzbget, 
			R.drawable.icon_nzbget,
			R.string.info_nzbget, 
			R.string.info_nzbget_homepage, 
			R.string.info_nzbget_installation),
	CASGLE("casgle", 
			R.string.layout_casgle, 
			R.drawable.icon_casgle,
			R.string.info_casgle,
			R.string.info_casgle_homepage, 
			R.string.info_casgle_installation),
	AMULE ("amule", 
			R.string.layout_amule,
			R.drawable.icon_amule,
			R.string.info_amule,
			R.string.info_amule_homepage,
			R.string.info_amule_installation),
	C200_REMOTE("c200_remote",
			R.string.layout_c200_remote, 
			R.drawable.icon_c200_remote,
			R.string.info_c200_remote, 
			R.string.info_c200_remote_homepage, 
			R.string.info_c200_remote_installation),
	CSI_GAYA("csi_gaya",
			R.string.layout_csi_gaya,
			R.drawable.icon_csi_gaya,
			R.string.info_csi_gaya,
			R.string.info_csi_gaya_homepage, 
			R.string.info_csi_gaya_installation),
	DOWNLOAD_MANAGER("download_manager", 
			R.string.layout_download_manager,
			R.drawable.icon_download_manager,
			R.string.info_download_manager, 
			R.string.info_download_manager_homepage, 
			R.string.info_download_manager_installation),
	ETVNET("etvnet", 
			R.string.layout_etvnet,
			R.drawable.icon_etvnet,
			R.string.info_etvnet,
			R.string.info_etvnet_homepage,
			R.string.info_etvnet_installation),
	FEEDTIME("feedtime",
			R.string.layout_feedtime,
			R.drawable.icon_feedtime,
			R.string.info_feedtime,
			R.string.info_feedtime_homepage,
			R.string.info_feedtime_installation),
	FILEMANAGER("filemanager",
			R.string.layout_filemanager, 
			R.drawable.icon_file_manager,
			R.string.info_filemanager, 
			R.string.info_filemanager_homepage,
			R.string.info_filemanager_installation),
	KARTINATV("kartinatv", 
			R.string.layout_kartinatv,
			R.drawable.icon_kartinatv,
			R.string.info_kartinatv,
			R.string.info_kartinatv_homepage, 
			R.string.info_kartinatv_installation),
	NMJ_MANAGER("nmj_manager",
			R.string.layout_nmj_manager,
			R.drawable.icon_nmj_manager,
			R.string.info_nmj_manager, 
			R.string.info_nmj_manager_homepage, 
			R.string.info_nmj_manager_installation),
	OVERSIGHT("oversight",
			R.string.layout_oversight, 
			R.drawable.icon_oversight,
			R.string.info_oversight, 
			R.string.info_oversight_homepage, 
			R.string.info_oversight_installation),
	PHPTERM("phpterm",
			R.string.layout_phpterm, 
			R.drawable.icon_phpterm,
			R.string.info_phpterm, 
			R.string.info_phpterm_homepage,
			R.string.info_phpterm_installation),
	SYNK("synk", 
			R.string.layout_synk, 
			R.drawable.icon_synk,
			R.string.info_synk,
			R.string.info_synk_homepage, 
			R.string.info_synk_installation),
	PWRC("pwrc", 
			R.string.layout_pwrc,
			R.drawable.icon_pwrc,
			R.string.info_pwrc, 
			R.string.info_pwrc_homepage, 
			R.string.info_pwrc_installation),
	SYSINFO("sysinfo",
			R.string.layout_sysinfo,
			R.drawable.icon_sysinfo,
			R.string.info_sysinfo, 
			R.string.info_sysinfo_homepage,
			R.string.info_sysinfo_installation),
	TORRENTWATCH("torrentwatch",
			R.string.layout_torrentwatch, 
			R.drawable.icon_torrentwatch,
			R.string.info_torrentwatch, 
			R.string.info_torrentwatch_homepage, 
			R.string.info_torrentwatch_installation),
	MEDIATANKCONTROLER("mediatankcontroller", 
			R.string.layout_mediatankcontroller, 
			R.drawable.icon_media_tank_controller,
			R.string.info_mediatankcontroller,
			R.string.info_mediatankcontroller_homepage, 
			R.string.info_mediatankcontroller_installation),
	NMTDVR("nmtdvr", 
			R.string.layout_nmtdvr,
			R.drawable.icon_nmtdvr,
			R.string.info_nmtdvr, 
			R.string.info_nmtdvr_homepage, 
			R.string.info_nmtdvr_installation),
	IPKG_WEB("ipkg_web", 
			R.string.layout_ipkg_web, 
			R.drawable.icon_ipkg,
			R.string.info_ipkg_web, 
			R.string.info_ipkg_web_homepage, 
			R.string.info_ipkg_web_installation),
	COUCHPOTATO("couchpotato",
			R.string.layout_couchpotato,
			R.drawable.icon_couchpotato,
			R.string.info_couchpotato,
			R.string.info_couchpotato_homepage,
			R.string.info_couchpotato_installation),
	MUSICBROWSER("musicbrowser",
			R.string.layout_musicbrowser,
			R.drawable.icon_musicbrowser,
			R.string.info_musicbrowser, 
			R.string.info_musicbrowser_homepage,
			R.string.info_musicbrowser_installation),
	SICKBEARD("sickbeard", 
			R.string.layout_sickbeard, 
			R.drawable.icon_sickbeard,
			R.string.info_sickbeard,
			R.string.info_sickbeard_homepage,
			R.string.info_sickbeard_installation);
	
	private String codename; //codename matching the the name column in ddbb 
    private int title; //i18n
    private int icon; //icon for drawables
    private int description; //i18n
    private int homepage; //i18n
    private int installation; //i18n
        
    /**
     * Constructor.
     * @param codename code name matching the the name column in database
     * @param title the title
     * @param icon the icon
     * @param description the description
     * @param homepage the home page
     * @param installation installation instructions
     */
    private TypeWebClient(String codename, int title, int icon, int description, int homepage, int installation) {
    	this.codename = codename;
		this.title = title;
		this.icon = icon;
		this.description = description;
		this.homepage = homepage;
		this.installation = installation;
	}

    /**
     * Getter of property.
     * @return code name matching the the name column in database
     */
	public String getCodename() {
		return codename;
	}

	/**
	 * Setter of property.
	 * @param codename code name matching the the name column in database
	 */
	public void setCodename(String codename) {
		this.codename = codename;
	}

	/**
	 * Getter of property.
	 * @return the title
	 */
	public int getTitle() {
		return title;
	}

	/**
	 * Setter of property.
	 * @param title the title
	 */
	public void setTitle(int title) {
		this.title = title;
	}

	/**
	 * Getter of property.
	 * @return the icon
	 */
	public int getIcon() {
		return icon;
	}

	/**
	 * Setter of property.
	 * @param icon the icon
	 */
	public void setIcon(int icon) {
		this.icon = icon;
	}

	/**
	 * Getter of property.
	 * @return the description
	 */
	public int getDescription() {
		return description;
	}

	/**
	 * Setter of property.
	 * @param description the description
	 */
	public void setDescription(int description) {
		this.description = description;
	}

	/**
	 * Getter of property.
	 * @return the home page
	 */
	public int getHomepage() {
		return homepage;
	}

	/**
	 * Setter of property.
	 * @param homepage the home page
	 */
	public void setHomepage(int homepage) {
		this.homepage = homepage;
	}

	/**
	 * Getter of property.
	 * @return the installation instructions
	 */
	public int getInstallation() {
		return installation;
	}
	
	/**
	 * Setter of property.
	 * @param installation the installation instructions
	 */
	public void setInstallation(int installation) {
		this.installation = installation;
	}

	/**
	 * Find the enumeration type with all the i18n information
	 * just by the web client codename
	 * @param codename web client name
	 * @return the enumeration type with all the i18n information
	 */
	public static TypeWebClient findByCodename(String codename) {
		TypeWebClient type = null;
	    for (TypeWebClient actual : TypeWebClient.values()) {
        	if(actual.getCodename().equalsIgnoreCase(codename)){
        		type = actual;
        	}
	    }
		return type;
	}

}
