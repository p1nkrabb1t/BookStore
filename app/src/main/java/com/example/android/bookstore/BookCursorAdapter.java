package com.example.android.bookstore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract;
import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {


    public BookCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {


        //find the relevant views
        TextView titleTextView = (TextView) view.findViewById(R.id.tv_title);
        TextView authorTextView = (TextView) view.findViewById(R.id.tv_author);
        TextView priceTextView = (TextView) view.findViewById(R.id.tv_price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.tv_quantity);


        //find the columns containing relevant info
        int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QUANTITY);

        //read the data from the cursor for the current item
        String titleData = cursor.getString(titleColumnIndex);
        String authorData = cursor.getString(authorColumnIndex);
        Integer priceData = cursor.getInt(priceColumnIndex);
        final Integer quantityData = cursor.getInt(quantityColumnIndex);

        //feed the data collected into the relevant views
        titleTextView.setText(titleData);
        authorTextView.setText(authorData);
        priceTextView.setText("RRP £" + priceData);

        //display relevant stock message depending if item is in stock or not
        if (quantityData == null || quantityData == 0) {
            quantityTextView.setText("OUT OF STOCK");
        } else {
            quantityTextView.setText("In Stock: " + quantityData);
        }

        //Find sale button and set click listener
        Button saleButton = (Button) view.findViewById(R.id.btn_sale);

        final int position = cursor.getPosition();
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the quantity data at the relevant position in the adapter
                cursor.moveToPosition(position);
                Integer quantityData = cursor.getInt(quantityColumnIndex);

                //check first there is a positive number of books
                if (quantityData == null || quantityData == 0) {
                    Toast.makeText(view.getContext(), view.getContext()
                            .getString(R.string.toast_book_sold_out), Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    //reduce quantity by 1 for each time the sale button is pressed
                    quantityData = quantityData - 1;

                    int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
                    String[] id = {String.valueOf(cursor.getInt(idColumnIndex))};
                    String selection = BookEntry._ID + "=?";
                    ContentValues values = new ContentValues();


                    //set the new quantity back into the database
                    values.put(BookEntry.COLUMN_QUANTITY, quantityData);
                    context.getContentResolver().update(BookEntry.CONTENT_URI, values, selection, id);
                    cursor.close();

                    //display message to advise user that request was successful
                    Toast.makeText(view.getContext(), view.getContext()
                            .getString(R.string.toast_book_sold), Toast.LENGTH_SHORT).show();
                }






            }

        });


    }
}
