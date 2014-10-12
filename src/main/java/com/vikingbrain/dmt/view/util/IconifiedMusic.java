/* 
 * Copyright 2007 Steven Osborn 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package com.vikingbrain.dmt.view.util;

import android.graphics.drawable.Drawable;
 
public class IconifiedMusic implements Comparable<IconifiedMusic>{ 
    
	public enum STATUS {PLAYED, PLAYING, LOADING, NONE };
	
	 private String mTitle = "";
	 private String mFullPath = "";
	 private String mIndex = "";
     private Drawable mIcon; 
     private boolean mSelectable = true; 
     private boolean mSelected;
     private STATUS mStatus;
     
     public IconifiedMusic(String title, String fullPath, String index, Drawable bullet, STATUS status) {
	    	 super();
	    	 this.mTitle = title;
	    	 this.mFullPath = fullPath;
	    	 this.mIndex = index;
	    	 this.mIcon = bullet;
	    	 this.mStatus = status;
     }     

     public boolean isSelected() {
      	return mSelected;
      }

	public void setSelected(boolean selected) {
      	this.mSelected = selected;
      }

  	public boolean isSelectable() { 
           return mSelectable; 
      } 

     public void setSelectable(boolean selectable) { 
          mSelectable = selectable; 
     } 
          
     public void setIcon(Drawable icon) { 
          mIcon = icon; 
     } 
      
     public Drawable getIcon() { 
          return mIcon; 
     } 
     
	public final String getTitle() {
		return mTitle;
	}

	public final void setTitle(String title) {
		this.mTitle = title;
	}

	public final String getFullPath() {
		return mFullPath;
	}

	public final void setFullPath(String fullPath) {
		this.mFullPath = fullPath;
	}

	public final String getIndex() {
		return mIndex;
	}

	public final void setIndex(String index) {
		this.mIndex = index;
	}

	/** Make IconifiedMusic comparable by its name */ 
     
     public int compareTo(IconifiedMusic other) { 
          if(this.mFullPath != null) 
               return this.mFullPath.compareTo(other.getFullPath()); 
          else 
               throw new IllegalArgumentException(); 
     }

	public STATUS getStatus() {
		return mStatus;
	}

	public void setStatus(STATUS status) {
		this.mStatus = status;
	}

	public final boolean isPlayed() {
		return STATUS.PLAYED.equals(mStatus);
	}

	public final boolean isPlaying() {
		return STATUS.PLAYING.equals(mStatus);
	}

	public final boolean isLoading() {
		return STATUS.LOADING.equals(mStatus);
	}

} 

