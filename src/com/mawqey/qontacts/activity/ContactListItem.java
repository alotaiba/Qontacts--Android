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

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.mawqey.qontacts.activity.ContactList.ContactListAdapter;
import com.mawqey.qontacts.model.object.Phone;
import com.mawqey.qontacts.model.object.PhoneUpdated;
import com.mawqey.qontacts.R;

public class ContactListItem extends RelativeLayout {
	//Items properties
	public String mContactId;
	public ArrayList<Phone> mContactCurrentNumbers;
	public ArrayList<PhoneUpdated> mContactUpdatedNumbers;
	public boolean mSelected;
	
	private ContactListAdapter mAdapter;
	private boolean mDownEvent;
	private int mCheckRight;
	private boolean mCachedViewPositions;

	
	private final static float CHECKMARK_PAD = 10.0F;
	
	public ContactListItem(Context context) {
		super(context);
	}

	public ContactListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContactListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void bindViewInit(ContactListAdapter contactListAdapter) {
		mAdapter = contactListAdapter;
		mCachedViewPositions = false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = false;
		int touchX = (int) event.getX();
		
		if (!mCachedViewPositions) {
			float paddingScale = getContext().getResources().getDisplayMetrics().density;
			int checkPadding = (int) ((CHECKMARK_PAD * paddingScale) + 0.5);
			mCheckRight = findViewById(R.id.selected).getRight() + checkPadding;
			mCachedViewPositions = true;
		}
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownEvent = true;
				if (touchX < mCheckRight) {
					handled = true;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				mDownEvent = false;
				break;
			case MotionEvent.ACTION_UP:
				if (mDownEvent) {
					if (touchX < this.mCheckRight) {
						mSelected = !mSelected;
						mAdapter.updateSelected(this, this.mSelected);
						handled = true;
					}
				}
				break;
		}
		
		if (handled) {
			postInvalidate();
		} else {
			handled = super.onTouchEvent(event);
		}
		
		return handled;
	}

}
