package com.example.android.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    private static final int BOOKS = 1;
    private static final int BOOK = 2;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK);
    }


    //create instance of BookDbHelper
    private BookDbHelper mBookHelper;

    @Override
    public boolean onCreate() {
        mBookHelper = new BookDbHelper(getContext());

        return true;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mBookHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            //to handle queries on the whole table of books
            case BOOKS:
                cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            //to handle queries on a single book entry in the database
            case BOOK:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            //exception thrown if uri represents neither single item or table (invalid uri)
            default:
                throw new IllegalArgumentException(uri + " is not recognised");
        }

        //set uri of cursor to watch for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    //to get the MIME data type for the URI
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(uri + " is not recognised");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException(uri + "could not be added");
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(BookEntry.COLUMN_NAME);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Title not found");
        }

        // Check that the price is valid
        Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Price not valid");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity not valid");
        }

        // Check that the supplier field is not null
        String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        if (supplier == null || supplier.length() == 0) {
            throw new IllegalArgumentException("Supplier entry required");
        }

        SQLiteDatabase database = mBookHelper.getWritableDatabase();
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            //not valid entry
            return null;
        }
        //notify that database has changed and the cursor of displayed data should be updated
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mBookHelper.getWritableDatabase();

        int booksDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete multiple books as determined by the selection and selectionArgs passed in
                booksDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                Toast.makeText(getContext(), booksDeleted + " books have been deleted.", Toast.LENGTH_SHORT).show();
                break;
            case BOOK:
                // Delete the entry selected using the _ID taken from the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                booksDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                Toast.makeText(getContext(), BookEntry.COLUMN_NAME + " has been deleted.", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalArgumentException(uri + " is not recognised. No items have been deleted.");
        }

        if (booksDeleted != 0) {
            //notify that database has changed and the cursor should be updated
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return booksDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        //first check if a single item or multiple items are to be updated and handle accordingly
        switch (match) {
            case BOOKS:
                return performUpdate(uri, values, selection, selectionArgs);

            case BOOK:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return performUpdate(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(uri + " could not be updated.");
        }
    }

    //proceed with update and apply any new data to the relevant entries
    private int performUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mBookHelper.getWritableDatabase();

        //check if a new name is present
        if (values.containsKey(BookEntry.COLUMN_NAME)) {
            // Check that the name is not empty
            String name = values.getAsString(BookEntry.COLUMN_NAME);
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException("Book Name is required");
            }
        }

        //check if a new author is present
        if (values.containsKey(BookEntry.COLUMN_AUTHOR)) ;


        //check if a new supplier name is present
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            // Check that the supplier name field is not empty
            String supplier = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null || supplier.length() == 0) {
                throw new IllegalArgumentException("Supplier Name is required");
            }
        }

        //check if a new supplier phone number is present
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE)) ;


        //check if a new price is present
        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            // Check that the price is not empty
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Price is required");
            }
        }

        //check if a new quantity is present
        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            // Check that the quantity in stock is not empty
            Integer stock = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (stock == null || stock < 0) {
                throw new IllegalArgumentException("Stock Quantity is required");
            }
        }

        // Perform the update and get the number of database rows affected
        int updated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If any updated recorded, notify listeners that the data has changed
        if (updated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updated;

    }
}
