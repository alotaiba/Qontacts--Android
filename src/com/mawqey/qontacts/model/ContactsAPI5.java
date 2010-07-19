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
 * ContactsAPI5
 * 
 * This class is the implementation of ContactsAPI for Android SDK API level 5
 * or later. 
 * 
 * Adopted the method by higherpass: http://www.higherpass.com/
 */

package com.mawqey.qontacts.model;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.telephony.PhoneNumberUtils;

import com.mawqey.qontacts.model.object.Contact;
import com.mawqey.qontacts.model.object.ContactItems;
import com.mawqey.qontacts.model.object.Phone;
import com.mawqey.qontacts.model.object.PhoneUpdated;

public class ContactsAPI5 extends ContactsAPI {
 	private Cursor mCursor;
 	private ContentResolver mContentResolver;
 	private Context mContext;
 	
 	private static final String[] PEOPLE_PROJECTION = new String[] {
 		ContactsContract.Contacts._ID,
 		ContactsContract.Contacts.DISPLAY_NAME
    };
 	
 	private static final String[] PHONE_PROJECTIONS = new String[] {
		ContactsContract.Data._ID,
		ContactsContract.CommonDataKinds.Phone.NUMBER,
		ContactsContract.CommonDataKinds.Phone.TYPE
 	};
 	
	public ContactsAPI5() {
		// TODO Auto-generated constructor stub
	}
	
 	public ContactsAPI5(ContentResolver contentResolver, Context context) {
 		setContentResolver(contentResolver);
 		setContext(context);
 	}
 	
 	@Override
	public Cursor getCursor() {
		return mCursor;
	}
 	
 	@Override
	public void setCursor(Cursor mCursor) {
		this.mCursor = mCursor;
	}
 	
 	@Override
	public ContentResolver getContentResolver() {
		return mContentResolver;
	}
 	
 	@Override
	public void setContentResolver(ContentResolver mContentResolver) {
		this.mContentResolver = mContentResolver;
	}
 	
 	@Override
	public Context getContext() {
		return mContext;
	}
 	
 	@Override
	public void setContext(Context mContext) {
		this.mContext = mContext;
	}
 	
	@Override
	public Intent getContactIntent() {
		return(new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI));
	}
	
	@Override
	public ContactItems newContactList() {
		ContactItems contacts = new ContactItems();
 		String id;
 		
 		this.mCursor = this.mContentResolver.query(ContactsContract.Contacts.CONTENT_URI,
 													PEOPLE_PROJECTION,
 													null,
 													null,
 													ContactsContract.Contacts.DISPLAY_NAME + " ASC");
 		if (this.mCursor.getCount() > 0) {
 			while (this.mCursor.moveToNext()) {
 				Contact c = new Contact();
 				id = this.getID();
 				c.setId(id);
 				c.setDisplayName(this.getDisplayName());
 				c.setPhone(this.getPhoneNumbers(id));
 				c.setPhoto(this.getPhoto(id));
 				contacts.addContact(c);
 			}
 		}
 		this.mCursor.close();
 		
 		return(contacts);
	}
	
	@Override
	public String getID() {
 		int column = this.mCursor.getColumnIndex(ContactsContract.Contacts._ID);
 		String id = mCursor.getString(column);
 		return id;
	}
	
	@Override
	public String getDisplayName() {
 		int column = this.mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
 		String name = mCursor.getString(column);
 		return name;
	}
	
	@Override
	public ArrayList<Phone> getPhoneNumbers(String id) {
 		ArrayList<Phone> phones = new ArrayList<Phone>();
 		
 		Cursor pCur = this.mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
									 				PHONE_PROJECTIONS, 
									 				ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
									 				new String[]{id},
									 				null);
 		while (pCur.moveToNext()) {
 			int numberIdColumn = pCur.getColumnIndex(ContactsContract.Data._ID);
			int numberColumn = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			int numberTypeColumn = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
						
			String numberId = pCur.getString(numberIdColumn);
			String numberString = pCur.getString(numberColumn);
			String numberTypeString = pCur.getString(numberTypeColumn);
			String numberLabelString = getPhoneNumberLabel(Integer.parseInt(numberTypeString));
			
 			phones.add(new Phone(
 					numberId,
 					numberString,
 					numberTypeString,
 					numberLabelString
 			));
 		} 
 		pCur.close();
 		return(phones);
	}
	
	@Override
	public String getPhoneNumberLabel(int type) {
		return (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(this.mContext.getResources(), type, "Unknown");
	}
	
	@Override
	public Bitmap getPhoto(String id) {
		Bitmap photo = null;
		Long contactId = Long.valueOf(id);
	    Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
	    InputStream photoDataStream = Contacts.openContactPhotoInputStream(this.mContentResolver, uri);
	    if (photoDataStream != null) {
	    	photo = BitmapFactory.decodeStream(photoDataStream);
	    }
	    return photo;
	}
	
 	/**
 	 * Qontacts Extension
 	 */
	
	@Override
	public ArrayList<Contact> getUpdateableContacts() {
 		ContactItems contacts = newContactList();
		ArrayList<Contact> contactsAll = contacts.getContacts();
		ArrayList<Contact> contactsFiltered = new ArrayList<Contact>();
		
		for (int i = 0; i < contactsAll.size(); i++) {
			Contact contactItem = contactsAll.get(i);
			Contact contactItemUpdated = checkUpdateableContact(contactItem);
			
			if (contactItemUpdated != null) {
				contactsFiltered.add(contactItemUpdated);
			}
		}
		
		return contactsFiltered;
	}

	@Override
	public Contact checkUpdateableContact(Contact contactItem) {
		ArrayList<Phone> contactCurrentNumbers = contactItem.getPhone();
		ArrayList<PhoneUpdated> contactUpdatedNumbers = this.getUpdatedPhoneNumbers(contactCurrentNumbers);
		
		if (contactUpdatedNumbers.size() > 0) {
			contactItem.setPhoneUpdated(contactUpdatedNumbers);
			return contactItem;
		}
		
		return null;
	}
	
	@Override
	public ArrayList<PhoneUpdated> getUpdatedPhoneNumbers(ArrayList<Phone> contactCurrentNumbers) {
		ArrayList<PhoneUpdated> contactUpdatedNumbers = new ArrayList<PhoneUpdated>();
		if (contactCurrentNumbers.size() > 0) {
			for (int i = 0; i < contactCurrentNumbers.size(); i++) {
				String number = this.updateableNumber(contactCurrentNumbers.get(i).getNumber());
				if ((number != null) && (number.length() > 0)) {
					String id = contactCurrentNumbers.get(i).getId();
					
					String type = contactCurrentNumbers.get(i).getType();
					String label = contactCurrentNumbers.get(i).getLabel();
					contactUpdatedNumbers.add(new PhoneUpdated(
													id,
													number,
													type,
													label
												));
				}
			}
		}
		return contactUpdatedNumbers;
	}
	
	@Override
	protected String updateableNumber(String number) {
		if ((number != null) && (number.length() > 0)) {
			number = PhoneNumberUtils.stripSeparators(number);
			
			String _retNumber = "";
			String _tempPrefix = "";
			String _tempNumber = number;
			
			if (number.startsWith("+974")) {
				_tempPrefix = "+974";
				_tempNumber = number.substring(4);
			} else if (number.startsWith("00974")) {
				_tempPrefix = "00974";
				_tempNumber = number.substring(5);
			}
			
            if ((_tempNumber.length() == 7) && (
            									_tempNumber.startsWith("3") || 
            									_tempNumber.startsWith("4") || 
            									_tempNumber.startsWith("5") || 
            									_tempNumber.startsWith("6") || 
            									_tempNumber.startsWith("7")
            								   ))
            {
            	_retNumber = _tempPrefix + _tempNumber.charAt(0) + _tempNumber;
            }
            
			return PhoneNumberUtils.formatNumber(_retNumber);
		}
		return null;
	}
	
	@Override
	public void updateContactNumbers(ArrayList<PhoneUpdated> newNumbers) {
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		
		for (int i = 0; i < newNumbers.size(); i++) {
			String id = newNumbers.get(i).getId();
			String number = newNumbers.get(i).getNumber();
			
			values.clear();
			values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
			Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, Integer.parseInt(id));
			cr.update(uri, values, null, null);
		}
	}

}
