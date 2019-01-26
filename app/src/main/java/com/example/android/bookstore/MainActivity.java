package com.example.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    BookDbHelper helper = new BookDbHelper(this);
    // BookCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayBooks();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Using switch statement as more options will be added later
        switch (item.getItemId()) {
            case R.id.test_data:
                insertBook();
                displayBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_NAME, "Apps For Beginners");
        values.put(BookEntry.COLUMN_AUTHOR, "C. oder");
        values.put(BookEntry.COLUMN_PRICE, 5);
        values.put(BookEntry.COLUMN_QUANTITY, 1);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Droid Books");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "00000 000 000");

        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    public void displayBooks() {

        //separate out the column titles from between the [square brackets] as was causing app to crash
        String supplierString = BookEntry.COLUMN_SUPPLIER_NAME.substring(1, 14);
        String supplierPhoneString = BookEntry.COLUMN_SUPPLIER_PHONE.substring(1, 22);


        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_NAME,
                BookEntry.COLUMN_AUTHOR,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };

        //use contentResolver to interact with the database
        Cursor cursor = getContentResolver().query(BookEntry.CONTENT_URI, projection, null, null,
                null);

        ListView listView = (ListView) findViewById(R.id.LV_books);
        BookCursorAdapter adapter = new BookCursorAdapter(this, cursor, 0);

        listView.setAdapter(adapter);


//        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
//        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
//        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR);
//        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
//        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
//        int supplierNameColumnIndex = cursor.getColumnIndex(supplierString);
//        int supplierPhoneColumnIndex = cursor.getColumnIndex(supplierPhoneString);


    }


}
