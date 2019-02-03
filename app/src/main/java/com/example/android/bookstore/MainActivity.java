package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    BookCursorAdapter mAdapter;
    private static final int mBookLoader = 0;


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
        switch (item.getItemId()) {
            case R.id.test_data:
                insertBook();
                return true;

            case R.id.delete_all:
                //before running delete process, first check if there are any entries to delete
                int count = getContentResolver().query(BookEntry.CONTENT_URI, null, null, null, null).getCount();
                //advise user there is nothing to delete and then discontinue with delete
                if (count == 0) {
                    Toast.makeText(this, "THERE ARE NO BOOKS TO DELETE", Toast.LENGTH_SHORT).show();
                }
                // entries are present, proceed with launching delete confirmation dialogue
                else {
                    showDeleteConfirmationDialog();
                }
                return true;


            case R.id.add_book:
                //launch the InputActivity for the user to enter new book details
                Intent i = new Intent(MainActivity.this, InputActivity.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    public void displayBooks() {

        //reference the listView which will display the list of book items
        ListView listView = (ListView) findViewById(R.id.LV_books);

        //empty view will show only if there are no books in database
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        //set click listener on empty view for quick way for the user to add a book
        View.OnClickListener add = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, InputActivity.class);
                startActivity(i);
                finish();
            }
        };
        emptyView.setOnClickListener(add);


        //set the Cursor Adapter to display the list of 'books', each one being a Cursor of data
        mAdapter = new BookCursorAdapter(this, null, 0);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //create new intent to launch the input activity
                Intent i = new Intent(MainActivity.this, InputActivity.class);

                Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                i.setData(uri);
                startActivity(i);
            }
        });

        //start the loader
        getLoaderManager().initLoader(mBookLoader, null, this);

    }

    //insert test data into the database
    public void insertBook() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_NAME, "Apps For Beginners");
        values.put(BookEntry.COLUMN_AUTHOR, "C. oder");
        values.put(BookEntry.COLUMN_PRICE, 5);
        values.put(BookEntry.COLUMN_QUANTITY, 1);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Droid Books");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "00000 000 000");

        //insert via contentResolver instead of directly interacting with database
        getContentResolver().insert(BookEntry.CONTENT_URI, values);

    }

    //delete all books in the table
    private void deleteAllBooks() {
        if (BookEntry.CONTENT_URI != null) {
            getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        }


    }

    //add a confirm deletion check message with options to continue or cancel
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure");
        //option to continue with delete
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllBooks();
            }
        });
        //option to cancel the request
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null,
                null);
    }

    @Override
    //update the adapter with the data from the newly retrieved cursor
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    //reset all values to null
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
