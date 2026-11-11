package com.example.baithuchanh3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Products table
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_CATEGORY = "category";
    public static final String COLUMN_PRODUCT_ACTIVE = "active";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_PASSWORD + " TEXT NOT NULL);";
        db.execSQL(createUsersTable);

        // Create products table
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_PRODUCT_ACTIVE + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_DESCRIPTION + " TEXT);";
        db.execSQL(createProductsTable);

        // Seed default user (admin / 123456)
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USERNAME, "admin");
        userValues.put(COLUMN_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, userValues);

        // Seed default products
        insertInitialProduct(db, "Chuột không dây", 150000, 20, "Điện tử", 1, "Chuột không dây bluetooth");
        insertInitialProduct(db, "Bàn phím cơ", 450000, 3, "Điện tử", 1, "Bàn phím cơ switch xanh, có đèn nền");
        insertInitialProduct(db, "Sổ tay A5", 25000, 40, "Văn phòng phẩm", 1, "Sổ tay ghi chép A5");
    }

    private void insertInitialProduct(SQLiteDatabase db, String name, int price, int quantity, String category, int active, String description) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(COLUMN_PRODUCT_CATEGORY, category);
        values.put(COLUMN_PRODUCT_ACTIVE, active);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        db.insert(TABLE_PRODUCTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // 1. checkLogin(username, password)
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // 2. insertUser(username, password)
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // 3. insertProduct(Product p)
    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, p.getName());
        values.put(COLUMN_PRODUCT_PRICE, p.getPrice());
        values.put(COLUMN_PRODUCT_QUANTITY, p.getQuantity());
        values.put(COLUMN_PRODUCT_CATEGORY, p.getCategory());
        values.put(COLUMN_PRODUCT_ACTIVE, p.getActive());
        values.put(COLUMN_PRODUCT_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    // 4. getAllProducts()
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, COLUMN_PRODUCT_ID + " DESC");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                    int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY));
                    int active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));

                    list.add(new Product(id, name, price, quantity, category, active, description));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    // 5. getProductById(int id)
    public Product getProductById(int id) {
        Product product = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_PRODUCT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection, selectionArgs, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY));
                int active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));

                product = new Product(id, name, price, quantity, category, active, description);
            }
            cursor.close();
        }
        return product;
    }

    // 6. updateQuantity(int id, int quantity)
    public boolean updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        String whereClause = COLUMN_PRODUCT_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        int rows = db.update(TABLE_PRODUCTS, values, whereClause, whereArgs);
        return rows > 0;
    }

    // 7. deleteProduct(int id)
    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_PRODUCT_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        int rows = db.delete(TABLE_PRODUCTS, whereClause, whereArgs);
        return rows > 0;
    }

    // 8. getTotalValue()
    public long getTotalValue() {
        long total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_PRODUCT_PRICE + " * " + COLUMN_PRODUCT_QUANTITY + ") FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getLong(0);
            }
            cursor.close();
        }
        return total;
    }

    // 9. countLowStock()
    public int countLowStock() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_QUANTITY + " < 5";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
}
