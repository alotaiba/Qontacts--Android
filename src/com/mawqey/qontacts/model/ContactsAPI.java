/**
 * Qontacts Mobile Application
 * Qontacts is a mobile application that updates the address book contacts
 * to the new Qatari numbering scheme.
 * 
 * Copyright (C) 2010  Abdulrahman Saleh Alotaiba
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * ContactsAPI
 * 
 * This class is an abstract class to use by either ContactsAPI3 or
 * ContactsAPI5 depending on which SDK version is being used currently. 
 * 
 * Adopted the method by higherpass: http://www.higherpass.com/
 */

package com.mawqey.qontacts.model;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import com.mawqey.qontacts.model.object.Contact;
import com.mawqey.qontacts.model.object.ContactItems;
import com.mawqey.qontacts.model.object.Phone;
import com.mawqey.qontacts.model.object.PhoneUpdated;

public abstract class ContactsAPI {
	private static ContactsAPI sInstance;
	
	public ContactsAPI() {
		// TODO Auto-generated constructor stub
	}
	
	public static ContactsAPI getInstance() {
		if (sInstance == null) {
			String className;
            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
            Log.d("SDK_VERSION", String.valueOf(sdkVersion));
            if (sdkVersion < 5) {
                className = "com.mawqey.qontacts.model.ContactsAPI3";
                Log.d("CLASS_LOADED", "ContactsAPI3");
            } else {
                className = "com.mawqey.qontacts.model.ContactsAPI5";
                Log.d("CLASS_LOADED", "ContactsAPI5");
            }
            try {
                //Class<? extends ContactsAPI> clazz = Class.forName(ContactsAPI.class.getPackage() + "." + className).asSubclass(ContactsAPI.class);
            	Class<? extends ContactsAPI> clazz = Class.forName(className).asSubclass(ContactsAPI.class);
            	sInstance = clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
		}
		return sInstance;
	}
	
	public abstract Cursor getCursor();
	
	public abstract void setCursor(Cursor mCursor);
	
	public abstract ContentResolver getContentResolver();
	
	public abstract void setContentResolver(ContentResolver mContentResolver);
	
	public abstract Context getContext();
	
	public abstract void setContext(Context mContext);
	
	public abstract Intent getContactIntent();
	
	public abstract ContactItems newContactList();
	
	public abstract String getID();
	
	public abstract String getDisplayName();
	
	public abstract ArrayList<Phone> getPhoneNumbers(String id);
	
	public abstract String getPhoneNumberLabel(int type);
	
	public abstract Bitmap getPhoto(String id);
	
	//Qontacts Extension
	
	public abstract ArrayList<Contact> getUpdateableContacts();
	
	public abstract Contact checkUpdateableContact(Contact contactItem);
	
	public abstract ArrayList<PhoneUpdated> getUpdatedPhoneNumbers(ArrayList<Phone> contactCurrentNumbers);
	
	protected abstract String updateableNumber(String number);
	
	public abstract void updateContactNumbers(ArrayList<PhoneUpdated> newNumbers);

}
