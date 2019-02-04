package com.example.android.bookstore;

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
    public void bindView(View view, Context context, Cursor cursor) {

        //find the relevant views
        TextView titleTextView = (TextView) view.findViewById(R.id.tv_title);
        TextView authorTextView = (TextView) view.findViewById(R.id.tv_author);
        TextView priceTextView = (TextView) view.findViewById(R.id.tv_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.tv_quantity);
        Button saleButton = (Button) view.findViewById(R.id.btn_sale);

        //find the columns containing relevant info
        int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QUANTITY);

        //read the data from the cursor for the current item
        String titleData = cursor.getString(titleColumnIndex);
        String authorData = cursor.getString(authorColumnIndex);
        Integer priceData = cursor.getInt(priceColumnIndex);
        Integer quantityData = cursor.getInt(quantityColumnIndex);


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

        //set click listener to sale button
        View.OnClickListener sale = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "test", Toast.LENGTH_SHORT).show();

            }
        };
        saleButton.setOnClickListener(sale);
    }
}
