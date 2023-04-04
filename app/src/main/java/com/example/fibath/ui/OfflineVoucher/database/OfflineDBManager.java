package com.example.fibath.ui.OfflineVoucher.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.example.fibath.classes.model.SoldVoucherResponse;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.OfflineVoucher.database.model.DownloadedVouchers;

public class OfflineDBManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "voucher_db";

    public OfflineDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DownloadedVouchers.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DownloadedVouchers.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertVouchers(String id, String amount, String number, String serialNumber, String expiryDate, String bulkId,int reDownloaded) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues vouchers = new ContentValues();
        try {
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_ID, id);
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_AMOUNT, amount);
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_NUMBER, number);
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_SERIAL_NUMBER, serialNumber);
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_EXPIRY_DATE, expiryDate);
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_RETAILER_NAME, User.getUserSessionUsername());
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_STATUS, "1");
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_BULK_ID, bulkId);
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_SYNC, "no");
            vouchers.put(DownloadedVouchers.COLUMN_VOUCHER_REDOWNLOAD, reDownloaded>0?reDownloaded:0);
            db.insert(DownloadedVouchers.TABLE_NAME, null, vouchers);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public DownloadedVouchers getVoucher(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DownloadedVouchers.TABLE_NAME,
                new String[]{DownloadedVouchers.COLUMN_ID, DownloadedVouchers.COLUMN_VOUCHER_AMOUNT, DownloadedVouchers.COLUMN_VOUCHER_NUMBER, DownloadedVouchers.COLUMN_VOUCHER_SERIAL_NUMBER, DownloadedVouchers.COLUMN_VOUCHER_EXPIRY_DATE, DownloadedVouchers.COLUMN_VOUCHER_RETAILER_NAME, DownloadedVouchers.COLUMN_VOUCHER_STATUS, DownloadedVouchers.COLUMN_VOUCHER_BULK_ID, DownloadedVouchers.COLUMN_TIMESTAMP},
                DownloadedVouchers.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        DownloadedVouchers note = new DownloadedVouchers(
                cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_ID)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_NUMBER)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_SERIAL_NUMBER)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_EXPIRY_DATE)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_RETAILER_NAME)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_STATUS)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_BULK_ID)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_SYNC)),
                cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_REDOWNLOAD)),
                cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<DownloadedVouchers> getAllDownloadedVouchersByBulkId(String bulkid) {
        List<DownloadedVouchers> notes = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DownloadedVouchers.TABLE_NAME + " WHERE " + DownloadedVouchers.COLUMN_VOUCHER_STATUS + " = 0 " + " AND " + DownloadedVouchers.COLUMN_VOUCHER_BULK_ID + " =?" + " ORDER BY " + DownloadedVouchers.COLUMN_TIMESTAMP + " ASC", new String[]{String.valueOf(bulkid)});

        if (cursor.moveToFirst()) {
            do {
                DownloadedVouchers note = new DownloadedVouchers();
                note.setId(cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_ID)));
                note.setVoucherAmount(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_AMOUNT)));
                note.setVoucherNumber(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_NUMBER)));
                note.setVoucherSerialNumber(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_SERIAL_NUMBER)));
                note.setVoucherExpiryDate(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_EXPIRY_DATE)));
                note.setVoucherRetailerName(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_RETAILER_NAME)));
                note.setVoucherStatus(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_STATUS)));
                note.setVoucherBulkId(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_BULK_ID)));
                note.setVoucherReDownload(cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_REDOWNLOAD)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_TIMESTAMP)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;
    }

    public List<DownloadedVouchers> getAllBulkDownloadedVouchers() {
        List<DownloadedVouchers> notes = new ArrayList<>();
        String selectQuery = "SELECT " + DownloadedVouchers.COLUMN_VOUCHER_BULK_ID + ", COUNT (" + DownloadedVouchers.COLUMN_VOUCHER_BULK_ID + ") AS COUNTER FROM " + DownloadedVouchers.TABLE_NAME + " WHERE " + DownloadedVouchers.COLUMN_VOUCHER_STATUS + " = 0 " + " GROUP BY " + DownloadedVouchers.COLUMN_VOUCHER_BULK_ID + " ORDER BY " + DownloadedVouchers.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DownloadedVouchers note = new DownloadedVouchers();
                note.setVoucherBulkId(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_BULK_ID)));
                note.setVoucherCount(cursor.getString(cursor.getColumnIndex("COUNTER")));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;
    }

    public List<DownloadedVouchers> getAllUnPrintedVoucher() {
        List<DownloadedVouchers> notes = new ArrayList<>();
        String selectQuery = " SELECT " + DownloadedVouchers.COLUMN_VOUCHER_AMOUNT + ", COUNT (" + DownloadedVouchers.COLUMN_VOUCHER_AMOUNT + ") AS COUNTER FROM " + DownloadedVouchers.TABLE_NAME + " WHERE " + DownloadedVouchers.COLUMN_VOUCHER_STATUS + " = 1 " + " GROUP BY " + DownloadedVouchers.COLUMN_VOUCHER_AMOUNT + " ORDER BY " + DownloadedVouchers.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DownloadedVouchers note = new DownloadedVouchers();
                note.setVoucherAmount(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_AMOUNT)));
                note.setVoucherCount(cursor.getString(cursor.getColumnIndex("COUNTER")));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;
    }

    public List<DownloadedVouchers> getAllFreshVouchers(){
        List<DownloadedVouchers> notes = new ArrayList<>();
        String selectQuery = " SELECT  *  FROM " + DownloadedVouchers.TABLE_NAME + " WHERE " + DownloadedVouchers.COLUMN_VOUCHER_STATUS + " = 1 "  + " ORDER BY " + DownloadedVouchers.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DownloadedVouchers note = new DownloadedVouchers();
                note.setId(cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_ID)));
                note.setVoucherId(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_ID)));
                note.setVoucherAmount(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_AMOUNT)));
                note.setVoucherNumber(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_NUMBER)));
                note.setVoucherSerialNumber(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_SERIAL_NUMBER)));
                note.setVoucherExpiryDate(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_EXPIRY_DATE)));
                note.setVoucherRetailerName(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_RETAILER_NAME)));
                note.setVoucherStatus(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_STATUS)));
                note.setVoucherBulkId(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_BULK_ID)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_TIMESTAMP)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;
    }

    public List<DownloadedVouchers> printVouchers(String amount, int quantity) {
        List<DownloadedVouchers> notes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DownloadedVouchers.TABLE_NAME + " WHERE " + DownloadedVouchers.COLUMN_VOUCHER_AMOUNT + " =  '" + amount + "'AND " + DownloadedVouchers.COLUMN_VOUCHER_STATUS + " = 1 " + " ORDER BY " + DownloadedVouchers.COLUMN_TIMESTAMP + " DESC LIMIT " + quantity;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DownloadedVouchers note = new DownloadedVouchers();
                note.setId(cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_ID)));
                note.setVoucherId(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_ID)));
                note.setVoucherAmount(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_AMOUNT)));
                note.setVoucherNumber(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_NUMBER)));
                note.setVoucherSerialNumber(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_SERIAL_NUMBER)));
                note.setVoucherExpiryDate(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_EXPIRY_DATE)));
                note.setVoucherRetailerName(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_RETAILER_NAME)));
                note.setVoucherStatus(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_STATUS)));
                note.setVoucherReDownload(cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_REDOWNLOAD)));
                note.setVoucherBulkId(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_BULK_ID)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_TIMESTAMP)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;
    }

    public List<DownloadedVouchers> getAllUnSyncedVouchers() {
        List<DownloadedVouchers> notes = new ArrayList<>();
        String selectQuery = " SELECT " + DownloadedVouchers.COLUMN_VOUCHER_ID + ", COUNT (" + DownloadedVouchers.COLUMN_VOUCHER_ID + ") AS COUNTER FROM " + DownloadedVouchers.TABLE_NAME + " WHERE " + DownloadedVouchers.COLUMN_VOUCHER_STATUS + " = 0 " + " AND " + DownloadedVouchers.COLUMN_VOUCHER_SYNC + " =" + "'no'" + " GROUP BY " + DownloadedVouchers.COLUMN_VOUCHER_ID + " ORDER BY " + DownloadedVouchers.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DownloadedVouchers note = new DownloadedVouchers();
                note.setVoucherId(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_ID)));
                note.setVoucherCount(cursor.getString(cursor.getColumnIndex("COUNTER")));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;
    }


    public List<DownloadedVouchers> filterDownloadedVouchersBySerialNumber(String bulkid,Double serialStart,Double serialEnd){
        List<DownloadedVouchers> notes = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DownloadedVouchers.TABLE_NAME + " WHERE CAST( "+DownloadedVouchers.COLUMN_VOUCHER_SERIAL_NUMBER +" AS INTEGER) BETWEEN  " +serialStart+ " AND " +serialEnd+ "  AND " + DownloadedVouchers.COLUMN_VOUCHER_STATUS + " = 0 " + " AND " + DownloadedVouchers.COLUMN_VOUCHER_BULK_ID + " =?" + " ORDER BY " + DownloadedVouchers.COLUMN_TIMESTAMP + " ASC", new String[]{String.valueOf(bulkid)});

        if (cursor.moveToFirst()) {
            do {
                DownloadedVouchers note = new DownloadedVouchers();
                note.setId(cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_ID)));
                note.setVoucherAmount(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_AMOUNT)));
                note.setVoucherNumber(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_NUMBER)));
                note.setVoucherSerialNumber(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_SERIAL_NUMBER)));
                note.setVoucherExpiryDate(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_EXPIRY_DATE)));
                note.setVoucherRetailerName(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_RETAILER_NAME)));
                note.setVoucherStatus(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_STATUS)));
                note.setVoucherBulkId(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_BULK_ID)));
                note.setVoucherReDownload(cursor.getInt(cursor.getColumnIndex(DownloadedVouchers.COLUMN_VOUCHER_REDOWNLOAD)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DownloadedVouchers.COLUMN_TIMESTAMP)));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        db.close();

        return notes;

    }

    public void updateVoucher(List<DownloadedVouchers> vouchers) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(DownloadedVouchers.COLUMN_VOUCHER_STATUS, "0");

        try {
            for (int i = 0; i < vouchers.size(); i++) {
                db.update(DownloadedVouchers.TABLE_NAME, values, DownloadedVouchers.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(vouchers.get(i).getId())});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int getCountOfPrintedVoucher(String bulkid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + DownloadedVouchers.TABLE_NAME + " WHERE " + DownloadedVouchers.COLUMN_VOUCHER_STATUS + " = 0 " + " AND " + DownloadedVouchers.COLUMN_VOUCHER_BULK_ID + " =?" + " ORDER BY " + DownloadedVouchers.COLUMN_TIMESTAMP + " ASC", new String[]{String.valueOf(bulkid)});
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public void updateSyncStatus(List<DownloadedVouchers> vouchers) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(DownloadedVouchers.COLUMN_VOUCHER_SYNC, "yes");

        try {
            for (int i = 0; i < vouchers.size(); i++) {
                db.update(DownloadedVouchers.TABLE_NAME, values, DownloadedVouchers.COLUMN_VOUCHER_ID + " = ?", new String[]{String.valueOf(vouchers.get(i).getVoucherId())});
            }
            db.setTransactionSuccessful();
        } finally {
            System.out.println("Updated@@@@@");
            db.endTransaction();
        }
    }


    public void updateSyncStatusFromServerDirectly(List<SoldVoucherResponse> vouchers) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(DownloadedVouchers.COLUMN_VOUCHER_SYNC, "yes");
        values.put(DownloadedVouchers.COLUMN_VOUCHER_STATUS, "0");

        try {
            for (int i = 0; i < vouchers.size(); i++) {
                values.put(DownloadedVouchers.COLUMN_VOUCHER_REDOWNLOAD,vouchers.get(i).getReDownloaded()>0?vouchers.get(i).getReDownloaded():0);

                db.update(DownloadedVouchers.TABLE_NAME, values, DownloadedVouchers.COLUMN_VOUCHER_ID + " = ?", new String[]{String.valueOf(vouchers.get(i).getVoucherId().getId())});
            }
            db.setTransactionSuccessful();
        } finally {
            System.out.println("Updated@@@@@");
            db.endTransaction();
        }

    }




        public void deleteVoucher(List<DownloadedVouchers> vouchers) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            for (int i = 0; i < vouchers.size(); i++) {
                db.delete(DownloadedVouchers.TABLE_NAME, DownloadedVouchers.COLUMN_ID + " = ?", new String[]{String.valueOf(vouchers.get(i).getId())});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public int checkRecord() {
        int count = 0;
        String countQuery = "SELECT COUNT(*) FROM " + DownloadedVouchers.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);

        }

        return count;
    }
}
