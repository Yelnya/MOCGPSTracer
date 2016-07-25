package at.project.moc.mocgpstracer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandlerSammlung extends SQLiteOpenHelper
{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "routenManager";

    // Routen table name
    private static final String TABLE_ROUTEN = "routen";

    // Routen Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LATOLD = "latold";
    private static final String KEY_LNGOLD = "lngold";
    private static final String KEY_LATNEW = "latnew";
    private static final String KEY_LNGNEW = "lngnew";
    private static final String KEY_TMSTMPOLD = "tmstmpold";
    private static final String KEY_TMSTMPNEW = "tmstmpnew";
    private static final String KEY_DIST = "distance";
    private static final String KEY_SPEED = "kmh";


    public DatabaseHandlerSammlung(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    //TABLE ROUTEN FÜR DIE AUFZEICHNUNG
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_ROUTEN_TABLE = "CREATE TABLE " + TABLE_ROUTEN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LATOLD + " TEXT," + KEY_LNGOLD + " TEXT,"
                + KEY_LATNEW + " TEXT,"+ KEY_LNGNEW + " TEXT,"
                + KEY_TMSTMPOLD + " TEXT,"+ KEY_TMSTMPNEW + " TEXT,"
                + KEY_DIST + " TEXT,"+ KEY_SPEED + " TEXT"
                + ")";
        db.execSQL(CREATE_ROUTEN_TABLE);


    }

    //TABLE SAMMLUNG FÜR DIE KARTENANZEIGE


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTEN);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new route
    void addRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATOLD, route.get_glatoldString());
        values.put(KEY_LNGOLD, route.get_glngoldString());
        values.put(KEY_LATNEW, route.get_glatnewString());
        values.put(KEY_LNGNEW, route.get_glngnewString());
        values.put(KEY_TMSTMPOLD, route.get_gtmstmpoldString());
        values.put(KEY_TMSTMPNEW, route.get_gtmstmpnewString());
        values.put(KEY_DIST, route.get_gdistString());
        values.put(KEY_SPEED, route.get_gspeedString());

        // Inserting Row
        db.insert(TABLE_ROUTEN, null, values);
        db.close(); // Closing database connection
    }

    // Getting single route
    Route getRoute(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROUTEN, new String[] { KEY_ID,
                        KEY_LATOLD, KEY_LNGOLD,
                        KEY_LATNEW, KEY_LNGNEW,
                        KEY_TMSTMPOLD, KEY_TMSTMPNEW,
                        KEY_DIST, KEY_SPEED }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        //public Route(0 = id, 1 = glatoldString, 2 = glngoldString, 3 = glatnewString, 4 = glngnewString, 5 = gtmstmpoldString, 6 = gtmstmpnewString, 7 = gdistString, 8 = gspeedString)

        Route route = new Route(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getString(8));
        // return route
        return route;
    }

    // Getting All Routes
    public List<Route> getAllRoutes() {
        List<Route> routenList = new ArrayList<Route>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTEN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Route route = new Route();
//                contact.setID(Integer.parseInt(cursor.getString(0)));
//                contact.setName(cursor.getString(1));
//                contact.setPhoneNumber(cursor.getString(2));

                route.setID(Integer.parseInt(cursor.getString(0)));
                route.set_glatoldString(cursor.getString(1));
                route.set_glngoldString(cursor.getString(2));
                route.set_glatnewString(cursor.getString(3));
                route.set_glngnewString(cursor.getString(4));
                route.set_gtmstmpoldString(cursor.getString(5));
                route.set_gtmstmpnewString(cursor.getString(6));
                route.set_gdistString(cursor.getString(7));
                route.set_gspeedString(cursor.getString(8));


                // Adding contact to list
                routenList.add(route);
            } while (cursor.moveToNext());
        }

        // return contact list
        return routenList;
    }

    // Updating single contact
    public int updateRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATOLD, route.get_glatoldString());
        values.put(KEY_LNGOLD, route.get_glngoldString());
        values.put(KEY_LATNEW, route.get_glatnewString());
        values.put(KEY_LNGNEW, route.get_glngnewString());
        values.put(KEY_TMSTMPOLD, route.get_gtmstmpoldString());
        values.put(KEY_TMSTMPNEW, route.get_gtmstmpnewString());
        values.put(KEY_DIST, route.get_gdistString());
        values.put(KEY_SPEED, route.get_gspeedString());

        // updating row
        return db.update(TABLE_ROUTEN, values, KEY_ID + " = ?",
                new String[] { String.valueOf(route.getID()) });
    }

    // Deleting single contact
    public void deleteRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROUTEN, KEY_ID + " = ?",
                new String[] { String.valueOf(route.getID()) });
        db.close();
    }


    // Getting contacts Count
    public int getRoutenCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ROUTEN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void removeAll()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_ROUTEN, null, null);
    }

}