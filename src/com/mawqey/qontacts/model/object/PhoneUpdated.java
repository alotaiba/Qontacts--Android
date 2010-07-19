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

import android.os.Parcel;
import android.os.Parcelable;

public class PhoneUpdated implements Parcelable {
	private String id;
 	private String number;
 	private String type;
 	private String label;
 	
 	public PhoneUpdated() {}
 	
 	public PhoneUpdated(Parcel in) {
 		readFromParcel(in);
 	}
 	
 	public PhoneUpdated(String id, String number, String type, String label) {
 		this.setId(id);
 		this.setNumber(number);
 		this.setType(type);
 		this.setLabel(label);
 	}
 	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
 
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.number);
		dest.writeString(this.type);
		dest.writeString(this.label);
	}
	
	private void readFromParcel(Parcel in) {
		this.setId(in.readString());
		this.setNumber(in.readString());
		this.setType(in.readString());
		this.setLabel(in.readString());
	}
	
	public static final Parcelable.Creator<PhoneUpdated> CREATOR = new Parcelable.Creator<PhoneUpdated>() {

		@Override
		public PhoneUpdated createFromParcel(Parcel source) {
			return new PhoneUpdated(source);
		}

		@Override
		public PhoneUpdated[] newArray(int size) {
			return new PhoneUpdated[size];
		}
		
	};

}
