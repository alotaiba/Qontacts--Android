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
package com.mawqey.qontacts;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mawqey.qontacts.activity.AboutView;
import com.mawqey.qontacts.activity.ContactList;
import com.mawqey.qontacts.model.ContactsAPI;
import com.mawqey.qontacts.model.object.Contact;

public class Qontacts extends Activity implements OnClickListener {
	private ContactsAPI mContactsAccessor;
	private Button analyzeContactsButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mContactsAccessor = ContactsAPI.getInstance();
        analyzeContactsButton = (Button) findViewById(R.id.btn_analyze_contacts);
        analyzeContactsButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_analyze_contacts:
				new AnalyzeContacts().execute();
				break;
		}
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
	
	private class AnalyzeContacts extends AsyncTask<Void, Void, ArrayList<Contact>> {
		private ProgressDialog Dialog = new ProgressDialog(Qontacts.this);
		
		@Override
		protected void onPreExecute() {
			analyzeContactsButton.setEnabled(false);
			Dialog.setMessage(getString(R.string.analyzing_contacts) + "\n" + getString(R.string.this_may_take_few_minutes));
			Dialog.show();
		}
		
		@Override
		protected ArrayList<Contact> doInBackground(Void... params) {
			mContactsAccessor.setContentResolver(getContentResolver());
			mContactsAccessor.setContext(Qontacts.this);
			ArrayList<Contact> contactsItems = mContactsAccessor.getUpdateableContacts();
			return contactsItems;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Contact> result) {
			Dialog.dismiss();
			analyzeContactsButton.setEnabled(true);
			ContactList.actionHandleOpenContacts(Qontacts.this, result);
		}
		
	}
	
}