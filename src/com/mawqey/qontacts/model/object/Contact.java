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
package com.mawqey.qontacts.model.object;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
 	private String id;
 	private String displayName;
 	private ArrayList<Phone> phone;
 	private ArrayList<PhoneUpdated> phoneUpdated;
 	private Bitmap photo;
 	
 	public Contact() {}
 	
 	public Contact(Parcel in) {
 		readFromParcel(in);
 	}
 	
 	public Contact(String id, String displayName, ArrayList<Phone> phone, ArrayList<PhoneUpdated> phoneUpdated, Bitmap photo) {
		this.setId(id);
		this.setDisplayName(displayName);
		this.setPhone(phone);
		this.setPhoneUpdated(phoneUpdated);
		this.setPhoto(photo);
 	}
 	
 	public String getId() {
 		return id;
 	}
 	public void setId(String id) {
  		this.id = id;
 	}
 	public String getDisplayName() {
 		return displayName;
 	}
 	public void setDisplayName(String dName) {
 		this.displayName = dName;
 	}
 	public ArrayList<Phone> getPhone() {
 		return phone;
 	}
 	public void setPhone(ArrayList<Phone> phone) {
 		this.phone = phone;
 	}
 	public void addPhone(Phone phone) {
 		this.phone.add(phone);
 	}
	public void setPhoneUpdated(ArrayList<PhoneUpdated> phoneUpdated) {
		this.phoneUpdated = phoneUpdated;
	}
	public ArrayList<PhoneUpdated> getPhoneUpdated() {
		return phoneUpdated;
	}
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	public Bitmap getPhoto() {
		return photo;
	}
 	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.displayName);
		//TODO: Use specific write methods for efficiency
		dest.writeValue(this.phone);
		dest.writeValue(this.phoneUpdated);
		dest.writeValue(this.photo);
	}
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		this.setId(in.readString());
		this.setDisplayName(in.readString());
		//TODO: Use specific read methods for efficiency
		this.setPhone((ArrayList<Phone>) in.readValue(Phone.class.getClassLoader()));
		this.setPhoneUpdated((ArrayList<PhoneUpdated>) in.readValue(PhoneUpdated.class.getClassLoader()));
		this.setPhoto((Bitmap) in.readValue(Bitmap.class.getClassLoader()));
	}

	@SuppressWarnings("unchecked")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public Object createFromParcel(Parcel source) {
			return new Contact(source);
		}

		@Override
		public Object[] newArray(int size) {
			return new Contact[size];
		}
		
	};
}
