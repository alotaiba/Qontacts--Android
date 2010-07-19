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

public class ContactItems {
 	private ArrayList<Contact> contacts = new ArrayList<Contact>();
 	
 	public ContactItems() {}
 	
 	public ArrayList<Contact> getContacts() {
 		return contacts;
 	}
 
 	public void setContacts(ArrayList<Contact> contacts) {
 		this.contacts = contacts;
 	}
 	
 	public void addContact(Contact contact) {
 		this.contacts.add(contact);
 	}
  	

}
