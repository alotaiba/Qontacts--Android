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
 * ContactsAPI3
 * 
 * This class is the implementation of ContactsAPI for Android SDK API level 3
 * or later, and less than API level 5. 
 * 
 * Adopted the method by higherpass: http://www.higherpass.com/
 */

package com.mawqey.qontacts.model;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.telephony.PhoneNumberUtils;

import com.mawqey.qontacts.R;
import com.mawqey.qontacts.model.object.Contact;
import com.mawqey.qontacts.model.object.ContactItems;
import com.mawqey.qontacts.model.object.Phone;
import com.mawqey.qontacts.model.object.PhoneUpdated;

public class ContactsAPI3 extends ContactsAPI {
 	private Cursor mCursor;
 	private ContentResolver mContentResolver;
 	private Context mContext;
 	
	private static final String[] PEOPLE_PROJECTION = new String[] {
 		People._ID,
 		People.DISPLAY_NAME
    };
	
 	private static final String[] PHONE_PROJECTIONS = new String[] {
		Contacts.Phones._ID,
		Contacts.Phones.PERSON_ID,
		Contacts.Phones.NUMBER,
		Contacts.Phones.NUMBER_KEY,
		Contacts.Phones.TYPE,
		Contacts.Phones.LABEL
 	};
 	
 	public ContactsAPI3() {}
 	
 	public ContactsAPI3(ContentResolver contentResolver, Context context) {
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
 		return(new Intent(Intent.ACTION_PICK, People.CONTENT_URI));
 	}
 	
 	@Override
 	public ContactItems newContactList() {
 		ContactItems contacts = new ContactItems();
 		String id;
 		
 		this.mCursor = this.mContentResolver.query(People.CONTENT_URI,
 													PEOPLE_PROJECTION,
 													null,
 													null,
 													People.DISPLAY_NAME + " ASC");
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
 		int column = this.mCursor.getColumnIndex(People._ID);
 		String id = mCursor.getString(column);
 		return id;
 	}
 	
 	@Override
 	public String getDisplayName() {
 		int column = this.mCursor.getColumnIndex(People.DISPLAY_NAME);
 		String name = mCursor.getString(column);
 		return name;
 	}
 	
 	@Override
 	public ArrayList<Phone> getPhoneNumbers(String id) {
 		ArrayList<Phone> phones = new ArrayList<Phone>();
 		Cursor pCur = this.mContentResolver.query(Contacts.Phones.CONTENT_URI, 
									 				PHONE_PROJECTIONS, 
									 				Contacts.Phones.PERSON_ID +" = ?", 
									 				new String[]{id},
									 				null);
 		while (pCur.moveToNext()) {
 			int numberIdColumn = pCur.getColumnIndex(Contacts.Phones._ID);
			int numberColumn = pCur.getColumnIndex(Contacts.Phones.NUMBER);
			int numberTypeColumn = pCur.getColumnIndex(Contacts.Phones.TYPE);
			int numberLabelColumn = pCur.getColumnIndex(Contacts.Phones.LABEL);
			
			String numberId = pCur.getString(numberIdColumn);
			String numberString = pCur.getString(numberColumn);
			String numberTypeString = pCur.getString(numberTypeColumn);
			String numberLabelString = (Integer.parseInt(numberTypeString) == Contacts.PhonesColumns.TYPE_CUSTOM) ? pCur.getString(numberLabelColumn) : getPhoneNumberLabel(Integer.parseInt(numberTypeString));
			
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
 		String label = "";
 		
 		switch(type) {
	 		case Contacts.PhonesColumns.TYPE_WORK:
	 			label = mContext.getString(R.string.NUMBER_TYPE_WORK);
	 			break;
	 		case Contacts.PhonesColumns.TYPE_PAGER:
	 			label = mContext.getString(R.string.NUMBER_TYPE_PAGER);
	 			break;
	 		case Contacts.PhonesColumns.TYPE_OTHER:
	 			label = mContext.getString(R.string.NUMBER_TYPE_OTHER);
	 			break;
	 		case Contacts.PhonesColumns.TYPE_MOBILE:
	 			label = mContext.getString(R.string.NUMBER_TYPE_MOBILE);
	 			break;
	 		case Contacts.PhonesColumns.TYPE_HOME:
	 			label = mContext.getString(R.string.NUMBER_TYPE_HOME);
	 			break;
	 		case Contacts.PhonesColumns.TYPE_FAX_WORK:
	 			label = mContext.getString(R.string.NUMBER_TYPE_FAX_WORK);
	 			break;
	 		case Contacts.PhonesColumns.TYPE_FAX_HOME:
	 			label = mContext.getString(R.string.NUMBER_TYPE_FAX_HOME);
	 			break;
	 		default:
	 			label = mContext.getString(R.string.unknown);
	 			break;
 		}
 		
 		return label;
 	}
 	
 	@Override
 	public Bitmap getPhoto(String id) {
 		Long contactId = Long.valueOf(id);
 		Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, contactId);
 		Bitmap contactPhoto = People.loadContactPhoto(mContext, uri, R.drawable.unknown, null);
 		
 		return contactPhoto;
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
			String number_key = PhoneNumberUtils.getStrippedReversed(newNumbers.get(i).getNumber());
			
			values.clear();
			values.put(Contacts.Phones.NUMBER, number);
			values.put(Contacts.Phones.NUMBER_KEY, number_key);
			
			Uri uri = ContentUris.withAppendedId(Contacts.Phones.CONTENT_URI, Integer.parseInt(id));
			cr.update(uri, values, null, null);
		}
 		
	}


}
