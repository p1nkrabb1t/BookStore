package com.example.android.bookstore;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int mBookLoader = 0;
    private Uri mBookUri;
    private String supplierPhone;
    private int stock;
    private boolean mStockEdited = false;

    //set variable names to text views
    private TextView mName;
    private TextView mAuthor;
    private TextView mSupplier;
    private TextView mSupplierPhone;
    private TextView mPrice;
    private TextView mStock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //open the details screen and retrieve relevant info via uri
        Intent i = getIntent();
        mBookUri = i.getData();


        // check uri is valid and if so set the title and load the data
        if (mBookUri != null) {
            setTitle(R.string.title_book_details);
            //use the loader to populate the text fields
            getLoaderManager().initLoader(mBookLoader, null, this);


        } else {
            Toast.makeText(this, R.string.toast_book_not_found, Toast.LENGTH_SHORT).show();
            return;
        }


        //assign the data variables to the text input fields
        mName = (TextView) findViewById(R.id.tv_title);
        mAuthor = (TextView) findViewById(R.id.tv_author);
        mSupplier = (TextView) findViewById(R.id.tv_Supplier);
        mSupplierPhone = (TextView) findViewById(R.id.tv_supplier_phone);
        mPrice = (TextView) findViewById(R.id.tv_price);
        mStock = (TextView) findViewById(R.id.tv_stock_qty);


        //find and assign button IDs
        ImageButton callButton = (ImageButton) findViewById(R.id.btn_call_supplier);
        Button stockIncrease = (Button) findViewById(R.id.btn_stock_up);
        Button stockDecrease = (Button) findViewById(R.id.btn_stock_down);
        Button editButton = (Button) findViewById(R.id.btn_edit);
        Button deleteButton = (Button) findViewById(R.id.btn_delete);
        Button backButton = (Button) findViewById(R.id.btn_back);

        //set click listener to open the dialer
        View.OnClickListener call = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                //remove any extra spaces and set as phone number data for dialer
                i.setData(Uri.parse("tel:" + supplierPhone.replaceAll("\\s+", "")));
                startActivity(i);
            }
        };
        callButton.setOnClickListener(call);

        //set click listener to increase stock button
        View.OnClickListener stockUp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stock < 999) {
                    stock += 1;
                    mStock.setText(Integer.toString(stock));
                    mStockEdited = true;
                }
                //stop the user entering more than 3 digits
                else if (stock == 999) {
                    Toast.makeText(DetailsActivity.this, R.string.toast_stock_exceeded, Toast.LENGTH_SHORT).show();

                }
            }
        };
        stockIncrease.setOnClickListener(stockUp);


        //set click listener to decrease stock button
        View.OnClickListener stockDown = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stock > 0) {
                    stock -= 1;
                    mStock.setText(Integer.toString(stock));
                    mStockEdited = true;
                }
                //stop the user going below 0
                else if (stock == 0) {
                    Toast.makeText(DetailsActivity.this, R.string.toast_stock_invalid, Toast.LENGTH_SHORT).show();
                }
            }
        };
        stockDecrease.setOnClickListener(stockDown);


        //set click listener to know when user wants to edit details
        View.OnClickListener edit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for changes to stock before leaving page
                if (mStockEdited) {
                    //only run update stock if changes are recorded
                    updateStock();
                }
                Intent i = new Intent(DetailsActivity.this, InputActivity.class);
                i.setData(mBookUri);
                startActivity(i);
            }
        };
        editButton.setOnClickListener(edit);

        //set click listener to delete button
        View.OnClickListener delete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        };
        deleteButton.setOnClickListener(delete);


        //set click listener to allow user to go back
        View.OnClickListener back = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for changes to stock before leaving page
                if (mStockEdited) {
                    //only run update stock if changes are recorded
                    updateStock();
                }
                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
            }
        };
        backButton.setOnClickListener(back);

    }


    @Override
    public void onBackPressed() {
        //check for changes to stock before leaving page
        if (mStockEdited) {
            //only run update stock if changes are recorded
            updateStock();
        }
        // go back to book list
        super.onBackPressed();
    }

    private void updateStock() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_QUANTITY, stock);
        getContentResolver().update(mBookUri, values, null, null);
    }

    //add a confirm deletion check message with options to continue or cancel
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialogue_delete_warning);
        //option to continue with delete
        builder.setPositiveButton(R.string.dialogue_delete_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        //option to cancel the request
        builder.setNegativeButton(R.string.dialogue_delete_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {

        if (mBookUri != null) {
            int deleted = getContentResolver().delete(mBookUri, null, null);

            if (deleted == 0) {
                // If no rows were removed, display message to advise user
                Toast.makeText(this, R.string.toast_delete_fail,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, show delete confirmation message
                Toast.makeText(this, BookEntry.COLUMN_NAME + getString(R.string.toast_delete_success),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
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

        //update the adapter with the data from the cursor
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
            supplierPhone = cursor.getString(supplierPhoneColumn);
            stock = cursor.getInt(stockColumn);

            mName.setText(name);
            mAuthor.setText(author);
            mPrice.setText(getString(R.string.GBP) + Integer.toString(price));
            mSupplier.setText(supplier);
            mSupplierPhone.setText(supplierPhone);
            mStock.setText(Integer.toString(stock));
        }

    }

    @Override
    //reset all values to null
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText("");
        mAuthor.setText("");
        mPrice.setText("");
        mSupplier.setText("");
        mSupplierPhone.setText("");
        mStock.setText("");

    }
}
