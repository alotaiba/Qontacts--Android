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
package com.mawqey.qontacts.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.mawqey.qontacts.R;
import com.mawqey.qontacts.model.ContactsAPI;
import com.mawqey.qontacts.model.object.Contact;
import com.mawqey.qontacts.model.object.PhoneUpdated;

public class ContactList extends ListActivity implements OnItemClickListener, OnClickListener {
	public static final String EXTRA_CONTACT_ITEM_POSITION = "com.mawqey.qontacts.ContactList_contact_item_position";
	public static final int VIEW_SINGLE_CONTACT = 0;
	private static final int SELECT_NONE = 0;
	private static final int SELECT_ALL = 1;
	private static final int DIALOG_BATCH_UPDATE = 0;
	private static final int DIALOG_CONTEXT_UPDATE = 1;
	
	private View mMultiSelectPanel;
	private ListView mListView;
	private static ContactsAPI mContactsAccessor;
	private static ArrayList<Contact> mContactItems;
	private static ContactListAdapter mAdapter;
	private Button mUpdateContact;
	private Button mSelectContactsButton;
	private Contact mContactContext;
	
	public ContactList() {}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        
        mContactContext = null;
        
        mContactsAccessor = ContactsAPI.getInstance();
		mContactsAccessor.setContentResolver(getContentResolver());
		mContactsAccessor.setContext(this);
        
        mAdapter = new ContactListAdapter(this, R.layout.contact_list_item, mContactItems);
        setListAdapter(mAdapter);
		
        mListView = getListView();
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);
        
        mUpdateContact = (Button) findViewById(R.id.btn_update_list);
        mUpdateContact.setOnClickListener(this);
        
        mSelectContactsButton = (Button) findViewById(R.id.btn_select_list);
        mSelectContactsButton.setOnClickListener(this);
        
        mMultiSelectPanel = findViewById(R.id.contact_list_footer);
        if ( (mContactItems.size() > 0) && (mMultiSelectPanel.getVisibility() != View.VISIBLE) ) {
        	mMultiSelectPanel.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if (requestCode == VIEW_SINGLE_CONTACT && resultCode == RESULT_OK) {
        	int position = data.getIntExtra(EXTRA_CONTACT_ITEM_POSITION, -1);
        	if (position != -1) {
        		Contact contactItem = mAdapter.getItem(position);
        		removeContactFromList(contactItem);
        	}
    	}
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btn_update_list:
				showDialog(DIALOG_BATCH_UPDATE);
				break;
			case R.id.btn_select_list:
				int selectedCount = mAdapter.getSelectedContactsCount();
				if (selectedCount == mAdapter.items.size()) {
					mAdapter.selectContacts(SELECT_NONE);
				} else {
					mAdapter.selectContacts(SELECT_ALL);
				}
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Contact contact = (Contact) parent.getItemAtPosition(position);
		ContactView.actionView(this, contact, position);
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		String alertText;
		switch (id) {
			case DIALOG_BATCH_UPDATE:
				int count = mAdapter.getSelectedContactsCount();
				alertText = getResources().getQuantityString(R.plurals.contact_list_update_confirm_dialog, count, count);
				return createConfirmBatchUpdateDialog(this, alertText);
			case DIALOG_CONTEXT_UPDATE:
				alertText = getString(R.string.contact_list_update_confirm_context_dialog, mContactContext.getDisplayName());
				return createConfirmContextUpdateDialog(this, alertText);
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_btn_about:
			AboutView.showAboutView(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contacts_list_context_menu, menu);
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		mContactContext = (Contact) mAdapter.getItem(info.position);
		menu.setHeaderTitle(mContactContext.getDisplayName());
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.context_menu_update_contact:
			showDialog(DIALOG_CONTEXT_UPDATE);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
    public static void actionHandleOpenContacts(Context context,ArrayList<Contact> contactItems) {
    	Intent intent = new Intent(context, ContactList.class);
    	mContactItems = contactItems;
    	context.startActivity(intent);
    }
	
	public static void onUpdateContact(ArrayList<PhoneUpdated> newNumbers) {
		mContactsAccessor.updateContactNumbers(newNumbers);
	}
	
	public static void removeContactFromList(Contact contactItem) {
		String id = contactItem.getId();
		mAdapter.remove(contactItem);
		mAdapter.updateSelectedById(Long.valueOf(id), false);
	}
	
    private void setMultiPanelButtons(int count) {
    	if (mAdapter.items.size() > 0) { 	
    		if (mMultiSelectPanel.getVisibility() != View.VISIBLE) {
    			mMultiSelectPanel.setVisibility(View.VISIBLE);
    		}
    		
        	if (count == mAdapter.items.size()) {
        		mSelectContactsButton.setText(R.string.select_none);
        	} else {
        		mSelectContactsButton.setText(R.string.select_all);
        	}
        	
        	if (count > 0) {
        		mUpdateContact.setText(getString(R.string.contact_list_update_button, count));
        		mUpdateContact.setEnabled(true);
        	} else {
        		mUpdateContact.setText(R.string.update_list_action);
        		mUpdateContact.setEnabled(false);
        	}
    	} else {
    		if (mMultiSelectPanel.getVisibility() != View.GONE) {
    			mMultiSelectPanel.setVisibility(View.GONE);
    		}
    	}
    }
    
	private Dialog createConfirmBatchUpdateDialog(final Context context, String alertText) {
		return new AlertDialog.Builder(context)
			.setMessage(alertText)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_BATCH_UPDATE);
					new UpdateContacts().execute(mAdapter.getSelectedContacts());
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_BATCH_UPDATE);
				}
			})
			.create();
	}
	
	private Dialog createConfirmContextUpdateDialog(final Context context, String alertText) {
		return new AlertDialog.Builder(context)
			.setMessage(alertText)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_CONTEXT_UPDATE);
					onUpdateContact(mContactContext.getPhoneUpdated());
					Toast.makeText(ContactList.this, getString(R.string.contact_single_update_notification, mContactContext.getDisplayName()), Toast.LENGTH_LONG).show();
					removeContactFromList(mContactContext);
					mContactContext = null;
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_CONTEXT_UPDATE);
					mContactContext = null;
				}
			})
			.create();
	}
    
    class ContactListAdapter extends ArrayAdapter<Contact> {
    	public ArrayList<Contact> items;
    	private LayoutInflater mInflater;
    	private HashSet<Long> mChecked = new HashSet<Long>();
    	
    	private Drawable mSelectedIconOn;
    	private Drawable mSelectedIconOff;
    	
		public ContactListAdapter(Context context, int textViewResourceId, ArrayList<Contact> items) {
			super(context, textViewResourceId, items);
			Resources resources = context.getResources();
			this.items = items;
			this.mSelectedIconOn = resources.getDrawable(R.drawable.btn_check_buttonless_dark_on);
			this.mSelectedIconOff = resources.getDrawable(R.drawable.btn_check_buttonless_dark_off);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContactListItem itemView = (ContactListItem) convertView;
			
			if (itemView == null) {
				mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				itemView = (ContactListItem) mInflater.inflate(R.layout.contact_list_item, parent, false);
			}
			
			itemView.bindViewInit(this);
			
			Contact contact = items.get(position);
			if (contact != null) {
				itemView.mContactId = contact.getId();
				itemView.mContactCurrentNumbers = contact.getPhone();
				itemView.mContactUpdatedNumbers = contact.getPhoneUpdated();
				itemView.mSelected = mChecked.contains(Long.valueOf(itemView.mContactId));
				
				ImageView selectedView = (ImageView) itemView.findViewById(R.id.selected);
				selectedView.setImageDrawable(itemView.mSelected ? mSelectedIconOn : mSelectedIconOff);
				
				String contactName = contact.getDisplayName();
				TextView nameTextView = (TextView) itemView.findViewById(R.id.contact_list_item_name);
				nameTextView.setText(contactName);
				
				TextView updatedPhoneCount = (TextView) itemView.findViewById(R.id.count);
				updatedPhoneCount.setText(String.valueOf(itemView.mContactUpdatedNumbers.size()));
			}
			return itemView;
		}
		
		public void updateSelected(ContactListItem itemView, boolean newSelected) {
			ImageView selectedView = (ImageView) itemView.findViewById(R.id.selected);
			selectedView.setImageDrawable(newSelected ? mSelectedIconOn : mSelectedIconOff);
			
			Long id = Long.valueOf(itemView.mContactId);
			updateSelectedById(id, newSelected);
		}
		
		public void updateSelectedById(Long id, boolean newSelected) {
			if (newSelected) {
				mChecked.add(id);
			} else {
				mChecked.remove(id);
			}
			ContactList.this.setMultiPanelButtons(mChecked.size());
		}
		
		public int getSelectedContactsCount() {
			return mChecked.size();
		}
		
		public void selectContacts(int type) {
			switch (type) {
			case SELECT_NONE:
				for (int i = 0; i < items.size(); i++) {
					String id = items.get(i).getId();
					if (mChecked.contains(Long.valueOf(id))) {
						mChecked.remove(Long.valueOf(id));
					}
				}
				break;
			case SELECT_ALL:
				for (int i = 0; i < items.size(); i++) {
					String id = items.get(i).getId();
					if (!mChecked.contains(Long.valueOf(id))) {
						mChecked.add(Long.valueOf(id));
					}
				}
				break;
			}
			this.notifyDataSetChanged();
			ContactList.this.setMultiPanelButtons(mChecked.size());
		}
		
		public void clearSelection() {
			mChecked.clear();
		}
		
		public ArrayList<Contact> getSelectedContacts() {
			ArrayList<Contact> contactItems = new ArrayList<Contact>();
			for (int i = 0; i < items.size(); i++) {
				String id = items.get(i).getId();
				if (mChecked.contains(Long.valueOf(id))) {
					contactItems.add(items.get(i));
				}
			}
			return contactItems;
		}
    }
    
	private class UpdateContacts extends AsyncTask<ArrayList<Contact>, Integer, ArrayList<Contact>> {
		private ProgressDialog Dialog = new ProgressDialog(ContactList.this);
		
		@Override
		protected void onPreExecute() {
			Dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			Dialog.setMessage(getString(R.string.updating_contacts));
			Dialog.setCancelable(false);
			Dialog.show();
		}
		
		@Override
		protected ArrayList<Contact> doInBackground(ArrayList<Contact>... contacts) {
			ArrayList<Contact> contactsToRemove = new ArrayList<Contact>();
			Iterator<Contact> itr = contacts[0].iterator();

			int count = 0;
			Dialog.setProgress(count);
			Dialog.setMax(contacts[0].size());
			
			while (itr.hasNext()) {
				Contact contactItem = (Contact) itr.next();
				contactsToRemove.add(contactItem);
				onUpdateContact(contactItem.getPhoneUpdated());
				count++;
				publishProgress(count);
			}
			return contactsToRemove;
		}
		
		protected void onProgressUpdate(Integer... progress) {
			Dialog.setProgress(progress[0]);
		}
		
		@Override
		protected void onPostExecute(ArrayList<Contact> contactsToRemove) {
			Dialog.dismiss();
			for (Contact contact : contactsToRemove) {
				ContactList.removeContactFromList(contact);
			}
			
			ContactList.this.setMultiPanelButtons(0);
			ContactList.mAdapter.clearSelection();
			
			String notification = getResources().getQuantityString(R.plurals.contact_list_update_notification, contactsToRemove.size(), contactsToRemove.size());
			Toast.makeText(ContactList.this, notification, Toast.LENGTH_LONG).show();
		}
		
	}

}
