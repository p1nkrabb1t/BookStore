package com.example.android.bookstore.data;

import android.provider.BaseColumns;

public final class BookContract {
    private BookContract() {

    }

    public static final class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";

        public static final String _ID = "_id";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_AUTHOR = "Author";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_QUANTITY = "Quantity";
        public static final String COLUMN_SUPPLIER_NAME = "SupplierName";
        public static final String COLUMN_SUPPLIER_PHONE = "[Supplier Phone Number]";


    }
}
