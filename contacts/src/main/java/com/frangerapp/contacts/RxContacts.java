package com.frangerapp.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.frangerapp.contacts.ColumnMapper.*;

/**
 * Android contacts as rx observable.
 *
 * @author Ulrich Raab
 * @author MADNESS
 */
public class RxContacts {
    private static final String[] PROJECTION = {
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.STARRED,
            ContactsContract.Data.PHOTO_URI,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.IN_VISIBLE_GROUP
    };

    private ContentResolver mResolver;

    public static Observable<Contact> fetch(@NonNull final Context context) {
        return Observable.create(new ObservableOnSubscribe<Contact>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull
                                          ObservableEmitter<Contact> e) throws Exception {
                new RxContacts(context).fetch(e);
            }
        });
    }

    private RxContacts(@NonNull Context context) {
        mResolver = context.getContentResolver();
    }


    private void fetch(ObservableEmitter<Contact> emitter) {
        HashMap<Long, Contact> contacts = new HashMap<>();
        Cursor cursor = createCursor();
        cursor.moveToFirst();
        int idColumnIndex = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        int inVisibleGroupColumnIndex = cursor.getColumnIndex(ContactsContract.Data.IN_VISIBLE_GROUP);
        int displayNamePrimaryColumnIndex = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY);
        int photoColumnIndex = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
        int thumbnailColumnIndex = cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI);
        int mimetypeColumnIndex = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        int dataColumnIndex = cursor.getColumnIndex(ContactsContract.Data.DATA1);
//        int indexPhoneType = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);

        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(idColumnIndex);
            Contact contact = contacts.get(id);
            if (contact == null) {
                contact = new Contact(id);
                mapInVisibleGroup(cursor, contact, inVisibleGroupColumnIndex);
                mapDisplayName(cursor, contact, displayNamePrimaryColumnIndex);
                mapPhoto(cursor, contact, photoColumnIndex);
                mapThumbnail(cursor, contact, thumbnailColumnIndex);
                contacts.put(id, contact);
            }
            String mimetype = cursor.getString(mimetypeColumnIndex);
            switch (mimetype) {
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE: {
                    mapEmail(cursor, contact, dataColumnIndex);
                    break;
                }
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE: {
                    mapPhoneNumber(cursor, contact, dataColumnIndex);
                    break;
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        for (Long key : contacts.keySet()) {
            if (!contacts.get(key).getPhoneNumbers().isEmpty()) {
                Contact contact = contacts.get(key);
                Set<PhoneNumber> phoneNumberList = contact.getPhoneNumbers();
                for (PhoneNumber phoneNumber : phoneNumberList) {
                    contact.setPhoneNumber(phoneNumber);
                    emitter.onNext(contacts.get(key));
                }
            }
        }
        emitter.onComplete();
    }

    private Cursor createCursor() {
        return mResolver.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                null,
                null,
                ContactsContract.Data.CONTACT_ID
        );
    }
}
