package com.example.android.bookstore;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class InputActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int mBookLoader = 0;
    private Uri mBookUri;
    private boolean mEdited = false;
    private Integer stock;


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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //open the input screen and retrieve relevant info via uri
        Intent i = getIntent();
        mBookUri = i.getData();


        //assign the data variables to the text input fields
        mNameEditText = (EditText) findViewById(R.id.ET_title);
        mAuthorEditText = (EditText) findViewById(R.id.ET_author);
        mSupplierEditText = (EditText) findViewById(R.id.ET_Supplier);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.ET_Supplier_phone);
        mPriceEditText = (EditText) findViewById(R.id.ET_price);
        mStockEditText = (EditText) findViewById(R.id.ET_stock_qty);


        //find and assign button IDs
        Button stockIncrease = (Button) findViewById(R.id.btn_stock_up);
        Button stockDecrease = (Button) findViewById(R.id.btn_stock_down);
        Button saveButton = (Button) findViewById(R.id.btn_save);
        Button cancelButton = (Button) findViewById(R.id.btn_cancel);


        //set the title based on whether adding or updating, depending on the method used to get to input screen
        if (mBookUri == null) {
            setTitle(R.string.title_add_book);
            // deleteButton.setVisibility(View.GONE);
            stockIncrease.setVisibility(View.GONE);
            stockDecrease.setVisibility(View.GONE);

        } else {
            //use the loader to populate the text fields
            setTitle(R.string.title_edit_book);
            getLoaderManager().initLoader(mBookLoader, null, this);
        }

        //set click listener to increase stock button
        View.OnClickListener stockUp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stock < 999) {
                    stock += 1;
                    mStockEditText.setText(Integer.toString(stock));
                    mEdited = true;
                }
                //stop the user entering more than 3 digits
                else if (stock == 999) {
                    Toast.makeText(InputActivity.this, R.string.toast_stock_exceeded, Toast.LENGTH_SHORT).show();

                }
            }
        };


        //set click listener to decrease stock button
        View.OnClickListener stockDown = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stock > 0) {
                    stock -= 1;
                    mStockEditText.setText(Integer.toString(stock));
                    mEdited = true;
                }
                //stop the user going below 0
                else if (stock == 0) {
                    Toast.makeText(InputActivity.this, R.string.toast_stock_invalid, Toast.LENGTH_SHORT).show();
                }
            }
        };

        //set click listener to save button
        View.OnClickListener save = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
                finish();

            }
        };

        //set click listener to cancel button
        View.OnClickListener cancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mEdited) {
                    NavUtils.navigateUpFromSameTask(InputActivity.this);
                    return;
                } else launchUnsavedWarning();

            }
        };


        // apply listeners to input fields
        mNameEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);

        // apply listeners to buttons
        stockIncrease.setOnClickListener(stockUp);
        stockDecrease.setOnClickListener(stockDown);
        saveButton.setOnClickListener(save);
        cancelButton.setOnClickListener(cancel);

    }

    private void saveBook() {

        String nameInput = mNameEditText.getText().toString().trim();
        String authorInput = mAuthorEditText.getText().toString().trim();
        String supplierInput = mSupplierEditText.getText().toString().trim();
        String supplierPhoneInput = mSupplierPhoneEditText.getText().toString().trim();
        String priceInput = mPriceEditText.getText().toString().trim();
        Integer stockInput;


        boolean validEntry = false;
        int price = 0;


        if (TextUtils.isEmpty(nameInput)) {
            Toast.makeText(InputActivity.this, R.string.toast_title_required, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(supplierInput)) {
            Toast.makeText(InputActivity.this, R.string.toast_supplier_required, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(priceInput)) {
            Toast.makeText(InputActivity.this, R.string.toast_price_required, Toast.LENGTH_SHORT).show();
        } else {
            price = Integer.parseInt(priceInput);
            validEntry = true;
        }

        //handles error and sets stock to minimum 0 if user gives a null input
        try {
            stockInput = Integer.parseInt(mStockEditText.getText().toString().trim());

        } catch (NumberFormatException e) {
            stockInput = 0;
            Toast.makeText(InputActivity.this, R.string.toast_stock_invalid, Toast.LENGTH_SHORT).show();
        }


        if (validEntry) {


            ContentValues values = new ContentValues();
            //set the new data
            values.put(BookEntry.COLUMN_NAME, nameInput);
            values.put(BookEntry.COLUMN_AUTHOR, authorInput);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierInput);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneInput);
            values.put(BookEntry.COLUMN_PRICE, price);
            values.put(BookEntry.COLUMN_QUANTITY, stockInput);

            if (mBookUri == null) {
                //no uri present so not editing an existing book, insert uri for new book into database
                getContentResolver().insert(BookEntry.CONTENT_URI, values);
            } else {
                //check if any change was recorded
                int updates = getContentResolver().update(mBookUri, values, null, null);
                if (updates == 0) {
                    // No rows were updated
                    Toast.makeText(this, R.string.toast_not_updated,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful
                    Toast.makeText(this, R.string.toast_details_updated, Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


    private void warnUnsavedChanges(
            DialogInterface.OnClickListener discardButtonClickListener) {
        //create alert to warn user of unsaved changes and set click listeners on options
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogue_unsaved_warning);
        builder.setPositiveButton(R.string.dialogue_unsaved_positive, discardButtonClickListener);
        builder.setNegativeButton(R.string.dialogue_unsaved_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user selected to continue editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void launchUnsavedWarning() {
        // warn the user there are unsaved changes and ask if to proceed
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // quit editing and discard any changes
                        finish();
                    }
                };

        // start the warning dialogue
        warnUnsavedChanges(discardButtonClickListener);

    }

    @Override
    public void onBackPressed() {
        // If no changes are detected, proceed with going back
        if (!mEdited) {
            super.onBackPressed();
        } else launchUnsavedWarning();
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
            //use substring method to drop the square bracket[] to enable the data be retrieved
            int supplierColumn = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME.substring(1, 14));
            int supplierPhoneColumn = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE.substring(1, 22));
            int stockColumn = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

            String name = cursor.getString(nameColumn);
            String author = cursor.getString(authorColumn);
            int price = cursor.getInt(priceColumn);
            String supplier = cursor.getString(supplierColumn);
            String supplierPhone = cursor.getString(supplierPhoneColumn);
            stock = cursor.getInt(stockColumn);


            mNameEditText.setText(name);
            mAuthorEditText.setText(author);
            mPriceEditText.setText(Integer.toString(price));
            mSupplierEditText.setText(supplier);
            mSupplierPhoneEditText.setText(supplierPhone);
            mStockEditText.setText(Integer.toString(stock));

        }

    }

    @Override
    //reset all values to null
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mStockEditText.setText("");

    }
}
