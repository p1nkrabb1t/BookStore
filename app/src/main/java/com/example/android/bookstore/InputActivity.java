package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class InputActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int mBookLoader = 0;
    private Uri mBookUri;
    private boolean mEdited = false;

    //set variables to store the data added/edited via the text fields
    private EditText mNameEditText;
    private EditText mAuthorEditText;
    private EditText mPriceEditText;
    private EditText mDiscountEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private EditText mStockEditText;
    private EditText mMinStockEditText;


    //set up listener to detect when a field has potentially been edited
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mEdited = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //open up the input screen and retrieve relevant info via uri
        Intent i = getIntent();
        mBookUri = i.getData();

        //set the title based on whether adding or updating, depending on the method used to get to input screen
        if (mBookUri == null) {
            setTitle("Add New Book");

        } else {
            //use the loader to populate the text fields
            setTitle("Update Book Details");
            getLoaderManager().initLoader(mBookLoader, null, this);
        }

        //assign the data variables to the text input fields
        mNameEditText = (EditText) findViewById(R.id.ET_title);
        mAuthorEditText = (EditText) findViewById(R.id.ET_author);
        mPriceEditText = (EditText) findViewById(R.id.ET_rrp);
        mDiscountEditText = (EditText) findViewById(R.id.ET_discount);
        mSupplierEditText = (EditText) findViewById(R.id.ET_Supplier);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.ET_Supplier_phone);
        mStockEditText = (EditText) findViewById(R.id.ET_stock_qty);
        mMinStockEditText = (EditText) findViewById(R.id.ET_stock_min);

        // set listener onto each of the input fields
        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mDiscountEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);
        mMinStockEditText.setOnTouchListener(mTouchListener);
    }


    @Override
    //use loader to load data on a background thread
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_NAME,
                BookEntry.COLUMN_AUTHOR,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(this, mBookUri, projection, null, null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //check first if there is data to load
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //update the adapter with the data from the newly retrieved cursor
        if (cursor.moveToFirst()) {
            int nameColumn = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
            int authorColumn = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR);
            int priceColumn = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            // int discountColumn = cursor.getColumnIndex(BookEntry.COLUMN_DISCOUNT); --add in feature later
            int supplierColumn = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumn = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
            int stockColumn = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            // int minStockColumn = cursor.getColumnIndex(BookEntry.COLUMN_MIN_STOCK); --add in feature later

            String name = cursor.getString(nameColumn);
            String author = cursor.getString(authorColumn);
            int price = cursor.getInt(priceColumn);
            //int discount = cursor.getInt(discountColumn); --add in feature later
            String supplier = cursor.getString(supplierColumn);
            String supplierPhone = cursor.getString(supplierPhoneColumn);
            int stock = cursor.getInt(stockColumn);
            //int minStock = cursor.getInt(minStockColumn); --add in feature later


            mNameEditText.setText(name);
            mAuthorEditText.setText(author);
            mPriceEditText.setText(price);
            //mDiscountEditText.setText(discount); --add in feature later
            mSupplierEditText.setText(supplier);
            mSupplierPhoneEditText.setText(supplierPhone);
            mStockEditText.setText(stock);
            //mMinStockEditText.setText(minStock); --add in feature later
        }

    }

    @Override
    //reset all values to null
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mDiscountEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mStockEditText.setText("");
        mMinStockEditText.setText("");

    }
}
