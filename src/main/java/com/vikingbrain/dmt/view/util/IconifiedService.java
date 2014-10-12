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

import com.vikingbrain.dmt.pojo.TypeServiceStatus;
 
public class IconifiedService implements Comparable<IconifiedService>{     	 
	 
	 private String mName = "";
	 private TypeServiceStatus mStatus;
     private Drawable mIcon; 
     private boolean mSelectable = true; 
     private boolean mSelected;
     
     public IconifiedService(String name, TypeServiceStatus status, Drawable bullet) {
    	 super();
    	 this.mName = name;
    	 this.mStatus = status;
    	 this.mIcon = bullet;
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
     
	public final String getName() {
		return mName;
	}

	public final void setName(String name) {
		this.mName = name;
	}

	public final TypeServiceStatus getStatus() {
		return mStatus;
	}

	public final void setStatus(TypeServiceStatus status) {
		this.mStatus = status;
	}

	/** Make IconifiedService comparable by its name */ 
     
     public int compareTo(IconifiedService other) { 
          if(this.mName != null) 
               return this.mName.compareTo(other.getName()); 
          else 
               throw new IllegalArgumentException(); 
     }
     
} 

