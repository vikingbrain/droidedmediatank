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

import java.util.Date;

/**
 * POJO for NMT file.
 * 
 * @author Rafael Iñigo
 */
public class DMTFile {
	
	private String path;
	
	public static final int	TYPE_DIRECTORY = 1;
	public static final int	TYPE_FILE = 0;
	public static final int	TYPE_LINK = 2;

	private int type;
	private String link;
	private Date modifiedDate;
	private long size;
	
	/**
     * The system dependent file separator character.
     */
    public static final char separatorChar;

    /**
     * The system dependent file separator string. The initial value of this
     * field is the system property "file.separator".
     */
    public static final String separator;
    
    static {
        // The default protection domain grants access to these properties
    	separatorChar = "/".charAt(0); //$NON-NLS-1$ //$NON-NLS-2$          
        separator = new String(new char[] { separatorChar }, 0, 1);
    }

    /**
     * Constructor.
     * @param path the file of directory path
     */
	public DMTFile(String path){
		this.path = path;		
	}
	
	/**
	 * Constructor.
	 * @param path the file of directory path
	 * @param link the link if there is a link
	 * @param modifiedDate the modified date
	 * @param type the type of file
	 * @param size the size
	 */
	public DMTFile(String path, String link, Date modifiedDate, int type, long size){
		this.path = path;		
		this.link = link;
		this.modifiedDate = modifiedDate;
		this.type = type;
		this.size = size;		
	}
	
	/**
	 * Get auxiliary root file object information.
	 * @return the root file
	 */
	public static DMTFile getRootDMTFile(){
		DMTFile dMTFile = new DMTFile("/");
		dMTFile.setType(DMTFile.TYPE_DIRECTORY);
		return dMTFile;
	}
	
   /**
    * Returns the pathname of the parent of this file. This is the path up to
    * but not including the last name. {@code null} is returned if there is no
    * parent.
    * 
    * @return this file's parent pathname or {@code null}.
    */
   public String getParent() {
       int length = path.length(), firstInPath = 0;
       if (separatorChar == '\\' && length > 2 && path.charAt(1) == ':') {
           firstInPath = 2;
       }
       int index = path.lastIndexOf(separatorChar);
       if (index == -1 && firstInPath > 0) {
           index = 2;
       }
       if (index == -1 || path.charAt(length - 1) == separatorChar) {
           return null;
       }
       if (path.indexOf(separatorChar) == index
               && path.charAt(firstInPath) == separatorChar) {
           return path.substring(0, index + 1);
       }
       return path.substring(0, index);
   }

   /**
    * Returns the absolute path of this file.
    * 
    * @return the absolute file path.
    * 
    * @see java.lang.SecurityManager#checkPropertyAccess
    */
   public String getAbsolutePath() {
	   return path;
   }
   
   /**
    * Indicates if this file represents a <em>directory</em> on the
    * underlying file system.
    * 
    * @return {@code true} if this file is a directory, {@code false}
    *         otherwise.
    * @throws SecurityException
    *             if a {@code SecurityManager} is installed and it denies read
    *             access to this file.
    */
   public boolean isDirectory() {
	   boolean esDirectorio = false;
       if (path.length() == 0) {
    	   esDirectorio = false;
       } else if (DMTFile.TYPE_DIRECTORY == getType()){
    	   esDirectorio = true;
       //NEW link management    	   
       } else if (DMTFile.TYPE_LINK == getType()){
    	   esDirectorio = true;
       }
       return esDirectorio;
   }

   /**
    * Check if it is a link.
    * @return if it is a link
    */
   public boolean isLink() {
	   boolean isLink = false;
	   if (DMTFile.TYPE_LINK == getType()){
		   isLink = true;
	   }
	   return isLink;
	}

   /**
    * Returns the name of the file or directory represented by this file.
    * 
    * @return this file's name or an empty string if there is no name part in
    *         the file's path.
    */
   public String getName() {
       int separatorIndex = path.lastIndexOf(separator);
       
       String name = "";
       if (separatorIndex < 0){
    	   name = path;
       } else {
    	   name = path.substring(separatorIndex + 1, path.length());
       }       
       return name;
   }

   /**
    * Returns a string containing a concise, human-readable description of this
    * file.
    * 
    * @return a printable representation of this file.
    */
   @Override
   public String toString() {
       return path;
   }
   
   /**
    * Getter of property.
    * @return the path
    */
	public String getPath() {
		return path;
	}

	/**
	 * Check if exists.
	 * @return if exists
	 */
	public boolean exists(){
		return true;
	}
	
	/**
	 * Check if can write.
	 * @return if it can write
	 */
	public boolean canWrite(){
		//TODO
		return true;
	}

	/**
	 * Check if make dirs.
	 * @return if make dirs
	 */
	public boolean mkdirs(){
		//FIXME
		return false;
	}
	
	/**
	 * Getter of property.
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Setter of property.
	 * @param size the size
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Getter of property.
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Setter of property.
	 * @param type the type
	 */
	public void setType(int type) {
		this.type = type;
	}	

	/**
	 * Getter of property.
	 * @return the link
	 */
    public String getLink() {
		return link;
	}

    /**
     * Getter of property.
     * @return the last modified date
     */
	public Date getModifiedDate() {
		return modifiedDate;
	}

}