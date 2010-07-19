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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mawqey.qontacts.model.object.Contact;
import com.mawqey.qontacts.model.object.Phone;
import com.mawqey.qontacts.model.object.PhoneUpdated;
import com.mawqey.qontacts.R;

public class ContactView extends Activity implements OnClickListener {
	private static final String EXTRA_CONTACT_ITEM = "com.mawqey.qontacts.ContactView_contact_item";
	private static final String EXTRA_CONTACT_LIST_POSITION = "com.mawqey.qontacts.ContactView_contact_list_position";
	
	private static final int DIALOG_UPDATE = 1;
	
	private Contact mContactItem;
	private int mContactListItemPosition;
	private String mContactName;
	private ArrayList<Phone> mContactCurrentNumbers;
	private ArrayList<PhoneUpdated> mContactUpdatedNumbers;
	
	public ContactView() {}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_view);
		
		Intent intent = getIntent();
		
		mContactListItemPosition = intent.getIntExtra(EXTRA_CONTACT_LIST_POSITION, -1);
		mContactItem = intent.getParcelableExtra(EXTRA_CONTACT_ITEM);
		
		mContactName = mContactItem.getDisplayName();
		mContactCurrentNumbers = mContactItem.getPhone();
		mContactUpdatedNumbers = mContactItem.getPhoneUpdated();
		
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		ImageView contactDisplayPhoto = (ImageView) findViewById(R.id.contact_photo);
		Bitmap contactPhoto = mContactItem.getPhoto();
		if (contactPhoto != null) {
			contactDisplayPhoto.setImageBitmap(contactPhoto);
		}
		
		TextView contactDisplayName = (TextView) findViewById(R.id.contact_name);
		contactDisplayName.setText(mContactName);
		
		LinearLayout currentNumberLayout = (LinearLayout) findViewById(R.id.contact_current_numbers_strings);
		
		TextView currentNumbersTitle = (TextView) findViewById(R.id.contact_current_numbers_title);
		currentNumbersTitle.setText(getResources().getQuantityString(R.plurals.contact_single_current_numbers_title, mContactCurrentNumbers.size(), mContactCurrentNumbers.size()));
		
		for(int i = 0 ; i < mContactCurrentNumbers.size(); i++) {
			View currentNumberItem = linflater.inflate(R.layout.contact_view_number_item, null);
			
			TextView currentNumberItemLabel = (TextView) currentNumberItem.findViewById(R.id.number_label);
			currentNumberItemLabel.setText(mContactCurrentNumbers.get(i).getLabel());
			
			TextView currentNumberItemString = (TextView) currentNumberItem.findViewById(R.id.number_string);
			currentNumberItemString.setText(mContactCurrentNumbers.get(i).getNumber());
			
			currentNumberLayout.addView(currentNumberItem);
		}
		
		LinearLayout updatedNumberLayout = (LinearLayout) findViewById(R.id.contact_updated_numbers_strings);
		
		TextView updatedNumbersTitle = (TextView) findViewById(R.id.contact_updated_numbers_title);
		updatedNumbersTitle.setText(getResources().getQuantityString(R.plurals.contact_single_updated_numbers_title, mContactUpdatedNumbers.size(), mContactUpdatedNumbers.size()));
		
		for(int i = 0 ; i < mContactUpdatedNumbers.size(); i++) {
			View updatedNumberItem = linflater.inflate(R.layout.contact_view_number_item, null);
			
			TextView updatedNumberItemLabel = (TextView) updatedNumberItem.findViewById(R.id.number_label);
			updatedNumberItemLabel.setText(mContactUpdatedNumbers.get(i).getLabel());
			
			TextView updatedNumberItemString = (TextView) updatedNumberItem.findViewById(R.id.number_string);
			updatedNumberItemString.setText(mContactUpdatedNumbers.get(i).getNumber());
			
			updatedNumberLayout.addView(updatedNumberItem);
		}
		
        Button updateContact = (Button) findViewById(R.id.btn_update_single);
        updateContact.setOnClickListener(this);
    }
    
    public static void actionView(Context context, Contact contactItem, int position) {
    	Intent intent = new Intent(context, ContactView.class);
    	intent.putExtra(EXTRA_CONTACT_ITEM, contactItem);
    	intent.putExtra(EXTRA_CONTACT_LIST_POSITION, position);
    	((Activity) context).startActivityForResult(intent, ContactList.VIEW_SINGLE_CONTACT);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_update_single:
				showDialog(DIALOG_UPDATE);
				break;
		}
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_UPDATE:
				String alertText = getString(R.string.contact_single_update_confirm_dialog, mContactName);
				return createConfirmUpdateDialog(this, alertText);
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
	
	private Dialog createConfirmUpdateDialog(final Context context, String alertText) {
		return new AlertDialog.Builder(context)
			.setMessage(alertText)
			.setCancelable(false)
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_UPDATE);
					ContactList.onUpdateContact(mContactUpdatedNumbers);
					Toast.makeText(ContactView.this, getString(R.string.contact_single_update_notification, mContactName), Toast.LENGTH_LONG).show();
					Intent resultIntent = new Intent();
					resultIntent.putExtra(ContactList.EXTRA_CONTACT_ITEM_POSITION, mContactListItemPosition);
					setResult(RESULT_OK, resultIntent);
					ContactView.this.finish();
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_UPDATE);
				}
			})
			.create();
	}

}
