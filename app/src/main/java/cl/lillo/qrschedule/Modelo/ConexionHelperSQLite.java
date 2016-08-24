package cl.lillo.qrschedule.Modelo;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Alexi on 29/06/2016.
 */
public class ConexionHelperSQLite extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = ".db";
    private static final int DATABASE_VERSION = 1;

    public ConexionHelperSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
