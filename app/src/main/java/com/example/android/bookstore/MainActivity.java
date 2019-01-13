package com.example.android.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.bookstore.data.BookContract;
import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    BookDbHelper helper = new BookDbHelper(this);

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

    public void insertBook(){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_NAME, "Example book title");
        values.put(BookEntry.COLUMN_AUTHOR, "Unknown");
        values.put(BookEntry.COLUMN_PRICE, 5);
        values.put(BookEntry.COLUMN_QUANTITY, 1);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Droid Books");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "00000 000 000");

        db.insert(BookEntry.TABLE_NAME, null, values);

    }

    public void displayBooks(){

        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_NAME,
                BookEntry.COLUMN_AUTHOR,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, null, null,
                null, null, null);


            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

        TextView booksTexView = (TextView) findViewById(R.id.TV_books);

        try {
            booksTexView.setText(BookEntry._ID
                    + " | " + BookEntry.COLUMN_NAME
                    + " | " + BookEntry.COLUMN_AUTHOR
                    + " | " + BookEntry.COLUMN_PRICE
                    + " | " + BookEntry.COLUMN_QUANTITY
                    + " | " + BookEntry.COLUMN_SUPPLIER_NAME
                    + " | " + BookEntry.COLUMN_SUPPLIER_PHONE + "\n");

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentAuthor = cursor.getString(authorColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = String.valueOf(supplierNameColumnIndex);
//                String currentSupplier = cursor.getString(supplierNameColumnIndex);
//                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                booksTexView.append("\n" + currentID
                        + " | " + currentName
                        + " | " + currentAuthor
                        + " | " + currentPrice
                        + " | " + currentQuantity
                        + " | " + currentSupplier);
                       // + " | " + currentSupplierPhone);
            }
        }
        finally {
            cursor.close();
        }


    }




}
