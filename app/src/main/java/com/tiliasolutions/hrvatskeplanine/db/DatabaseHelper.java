package com.tiliasolutions.hrvatskeplanine.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.tiliasolutions.hrvatskeplanine.MainActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by etomlip on 18.9.2015..
 */
public class DatabaseHelper extends SQLiteOpenHelper implements Serializable{
    //DatabaseHelper singleton instances
    private static DatabaseHelper mInstance = null;
    private Context mCxt;

    //array to store VRHOVI data
    private static ArrayList<HashMap> vrhoviHashMapData = new ArrayList<HashMap>();

    //array to store PUTOVI data
    private static ArrayList<HashMap> putoviHashMapData = new ArrayList<HashMap>();;

    private static ArrayList<HashMap> vrhoviData;
    private static ArrayList<HashMap> putoviData;



    //DATABASE details
    private static final String DATABASE_NAME = "HrvatskePlanine.db"; //database name
    private static final int DATABASE_VERSION = 1;              //database version
    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    //string to crete VRHOVI table
    private static final String CREATE_VRHOVI_TABLE =
            "CREATE TABLE " +
            VrhoviEntry.TABLE_VRHOVI_NAME + " (" +
            VrhoviEntry._ID + " INTEGER PRIMARY KEY," +
            VrhoviEntry.TABLE_VRHOVI_VRH + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_PLANINA + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_VISINA + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_LAT + DOUBLE_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_LON + DOUBLE_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_INFO + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_PRILAZ + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_ZIG + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_KT + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_LINK + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_REGIJA + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_VIDIK + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_ZEMLJOVID + TEXT_TYPE + COMMA_SEP +
            VrhoviEntry.TABLE_VRHOVI_NAPOMENA + TEXT_TYPE +

            " )";

    //string to create PUtovi table
    private static final String CREATE_PUTOVI_TABLE =
            "CREATE TABLE " +
                    PutoviEntry.TABLE_PUTOVI_NAME + " (" +
                    PutoviEntry._ID + " INTEGER PRIMARY KEY," +
                    PutoviEntry.TABLE_PUTOVI_NAZIV + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_PLANINA + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_POCETAK + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_KRAJ + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_VRIJEME_HODA+ DOUBLE_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_DULJINA + DOUBLE_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_VISINSKA_RAZLIKA + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_OPIS + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_OZNAKA + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_DRUSTVO + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_NAPOMENA + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_GPX + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_IMAGE + TEXT_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_LAT_START + DOUBLE_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_LAT_END + DOUBLE_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_ELEVATION_START + DOUBLE_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_LON_START + DOUBLE_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_LON_END + DOUBLE_TYPE + COMMA_SEP +
                    PutoviEntry.TABLE_PUTOVI_ELEVATION_END + DOUBLE_TYPE +
                    " )";



    //constructor is private, so that it can not be created outside
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCxt=context;
        //TODO this.mCxt=context.getApplicationContext();
    }


    //getting/creating singleton for database helper
    public static DatabaseHelper getInstance(Context ctx) {
         if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        //create Vrhovi table
        db.execSQL(CREATE_VRHOVI_TABLE);
        //create PUTOVI table
        db.execSQL(CREATE_PUTOVI_TABLE);

        //TODO get Context
        //populateVrhoviTable(mCxt, db);



        //TODO populating vrhoviTable
        //iterateThruHashMapAndpupulateVrhoviTable(vrhoviHashMapData, db);
        //TODO populate PUTOVI table
        //iterateThruHashMapAndPopulatePutoviTable(putoviHashMapData, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    //populate any hashMap
    public void populateHahMap(ArrayList<HashMap> a, InputStream fPath){
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(fPath);
            a = (ArrayList<HashMap>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //iterate thru Vrhovi HashMap and populate vrhoviTable
    public void iterateThruHashMapAndpupulateVrhoviTable(ArrayList<HashMap> a, SQLiteDatabase db){
        for(HashMap<String, Object> h : a){
            StringBuilder sb = new StringBuilder();
            ArrayList<String> tempPrilaz = (ArrayList<String>) h.get("PRILAZ");

            for(String t : tempPrilaz){
                sb.append(t+"\n");
            }
            String vrhoviPrilaz = sb.toString();

            insertVrh(db,
                    (String) h.get("VRH"),
                    (String) h.get("PLANINA"),
                    null,
                    (String) h.get("VISINA"),
                    convertToDouble((String) h.get("LAT")),
                    convertToDouble((String) h.get("LON")),
                    (String) h.get("INFO"),
                    (String) h.get("LINK"),
                    (String) h.get("VIDIK"),
                    (String) h.get("ZIG"),
                    vrhoviPrilaz,
                    (String) h.get("ZEMLJOVID"),
                    (String) h.get("KT"),
                    (String) h.get("NAPOMENA")
                    );
        }
    }



    public void populateVrhoviTable(Context ctx, SQLiteDatabase db){
        int getIdentifier = ctx.getResources().getIdentifier("vrhovi_data", "raw", ctx
                .getPackageName());

        try {
            openSerFile(ctx.getResources().openRawResource(getIdentifier), "v");
            vrhoviData = getVrhoviData();
            iterateThruHashMapAndpupulateVrhoviTable(vrhoviData, db);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    //iterate thru Putovi HashMap and populate putoviTable
    public void iterateThruHashMapAndPopulatePutoviTable(ArrayList<HashMap> a, SQLiteDatabase db){
        for(HashMap<String, String> h : a){

            insertPut(db,
                    (String) h.get("NAZIV"),
                    (String) h.get("PLANINA"),
                    (String) h.get("POCETAK"),
                    (String) h.get("KRAJ"),
                    (String) h.get("VRIJEME_HODA"),
                    (String) h.get("DULJINA"),
                    (String) h.get("VISINSKA_RAZLIKA"),
                    (String) h.get("OPIS"),
                    (String) h.get("OZNAKA"),
                    (String) h.get("DRUSTVO"),
                    (String) h.get("NAPOMENA"),
                    (String) h.get("GPX"),
                    (String) h.get("IMAGE"),
                    convertToDouble((String) h.get("LAT_START")),
                    convertToDouble((String) h.get("LAT_END")),
                    convertToDouble((String) h.get("ELEVATION_START")),
                    convertToDouble((String) h.get("LON_START")),
                    convertToDouble((String) h.get("LON_END")),
                    convertToDouble((String) h.get("ELEVATION_END"))
            );
        }
    }




    private Double convertToDouble(String s){
        Double data = 0.0;
        try{
            data=Double.parseDouble(s);
        }catch(NullPointerException npe){
        }
        return data;
    }



    //open file - used im mainActivity
    public void openVrhoviFile(InputStream fPath) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(fPath);

        try {
            vrhoviData = (ArrayList<HashMap>) ois.readObject();
        } catch (OptionalDataException e) {
            if (!e.eof) throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ois.close();
        }
    }


    public void openSerFile(InputStream fPath, String whichFile) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(fPath);

        try {
            switch(whichFile){
                case "p":putoviData = (ArrayList<HashMap>) ois.readObject();
                    break;
                case "v":vrhoviData = (ArrayList<HashMap>) ois.readObject();
                    break;

            }
        } catch (OptionalDataException e) {
            if (!e.eof) throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ois.close();
        }
    }




    public ArrayList<HashMap> getVrhoviData(){
        return vrhoviData;
    }

    public ArrayList<HashMap> getPutoviData(){
        return putoviData;
    }


//zasto mi ovo treba???
    public void getAllVrhoviDataFromTable(SQLiteDatabase db){
        ArrayList<Object> data = new ArrayList<Object>();
        String[] projection = {
                VrhoviEntry._ID,
                VrhoviEntry.TABLE_VRHOVI_VRH,
                VrhoviEntry.TABLE_VRHOVI_PLANINA,
                VrhoviEntry.TABLE_VRHOVI_LAT,
                VrhoviEntry.TABLE_VRHOVI_LON,
                VrhoviEntry.TABLE_VRHOVI_VISINA
        };

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor c = db.query(
                VrhoviEntry.TABLE_VRHOVI_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        c.moveToFirst();
        do{
            ArrayList<Object> temp = new ArrayList<Object>();
            temp.add(c.getString(c.getColumnIndex(VrhoviEntry._ID)));
            temp.add(c.getString(c.getColumnIndex(VrhoviEntry.TABLE_VRHOVI_VRH)));
            temp.add(c.getString(c.getColumnIndex(VrhoviEntry.TABLE_VRHOVI_PLANINA)));
            temp.add(c.getString(c.getColumnIndex(VrhoviEntry.TABLE_VRHOVI_LAT)));
            temp.add(c.getString(c.getColumnIndex(VrhoviEntry.TABLE_VRHOVI_LON)));
            temp.add(c.getString(c.getColumnIndex(VrhoviEntry.TABLE_VRHOVI_VISINA)));
            data.add(temp);
        }while(c.moveToNext());



    }



    //populating vrhoviTable one VRH at the time
    public boolean insertVrh  (SQLiteDatabase db, String vrh, String planina,
                               String regija, String visina, Double lat,
                               Double lon, String info, String link,
                               String vidik, String zig, String prilaz,
                               String zemljovid, String kt, String napomena)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_VRH, vrh);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_PLANINA, planina);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_REGIJA, regija);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_VISINA, visina);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_LAT, lat);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_LON, lon);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_INFO, info);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_LINK, link);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_VIDIK, vidik);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_ZIG, zig);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_PRILAZ, prilaz);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_ZEMLJOVID, zemljovid);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_KT, kt);
        contentValues.put(VrhoviEntry.TABLE_VRHOVI_NAPOMENA, napomena);

        db.insert(VrhoviEntry.TABLE_VRHOVI_NAME, null, contentValues);
        return true;
    }



    //populating putoviTable one PUT at the time
    public boolean insertPut  (SQLiteDatabase db, String naziv, String planina, String pocetak,
                               String kraj, String vrijemeHoda, String duljina,
                               String visinskaRazlika, String opis, String oznaka,
                               String drustvo, String napomena, String gpx,
                               String image, Double latStart, Double latEnd,
                               Double eleStart, Double lonStart, Double lonEnd,
                               Double eleEnd) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(PutoviEntry.TABLE_PUTOVI_PLANINA, planina);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_NAZIV, naziv);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_POCETAK, pocetak);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_KRAJ, kraj);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_VRIJEME_HODA, vrijemeHoda);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_DULJINA, duljina);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_VISINSKA_RAZLIKA, visinskaRazlika);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_OPIS, opis);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_OZNAKA, oznaka);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_DRUSTVO, drustvo);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_NAPOMENA, napomena);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_GPX, gpx);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_IMAGE, image);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_LAT_START, latStart);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_LAT_END, latEnd);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_ELEVATION_START, eleStart);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_LON_START, lonStart);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_LON_END, lonEnd);
        contentValues.put(PutoviEntry.TABLE_PUTOVI_ELEVATION_END, eleEnd);

        db.insert(PutoviEntry.TABLE_PUTOVI_NAME, null, contentValues);
        return true;
    }


    //implementation of VRHOVI table
    public static abstract class VrhoviEntry implements BaseColumns {
        //ID is part of BAseColumns
        //private static final String TABLE_VRHOVI_ID ="_id";
        public static final String TABLE_VRHOVI_NAME = "vrhoviTable";
        public static final String TABLE_VRHOVI_VRH ="VRH";
        public static final String TABLE_VRHOVI_PLANINA ="PLANINA";
        public static final String TABLE_VRHOVI_REGIJA ="REGIJA";
        public static final String TABLE_VRHOVI_VISINA ="VISINA";
        public static final String TABLE_VRHOVI_LAT ="LAT";
        public static final String TABLE_VRHOVI_LON ="LON";
        public static final String TABLE_VRHOVI_INFO ="INFO";
        public static final String TABLE_VRHOVI_LINK ="LINK";
        public static final String TABLE_VRHOVI_VIDIK ="VIDIK";
        public static final String TABLE_VRHOVI_ZIG ="ZIG";
        public static final String TABLE_VRHOVI_PRILAZ ="PRILAZ";
        public static final String TABLE_VRHOVI_ZEMLJOVID ="ZEMLJOVID";
        public static final String TABLE_VRHOVI_KT ="KT";
        public static final String TABLE_VRHOVI_NAPOMENA ="NAPOMENA";
    }



    //implementation of PUTOVI table
    public static abstract class PutoviEntry implements BaseColumns {
        //ID is part of BAseColumns
        //private static final String TABLE_PUTOVI_ID ="_id";
        public static final String TABLE_PUTOVI_NAME = "putoviTable";
        public static final String TABLE_PUTOVI_NAZIV = "NAZIV";
        public static final String TABLE_PUTOVI_PLANINA = "PLANINA";
        public static final String TABLE_PUTOVI_POCETAK = "POCETAK";
        public static final String TABLE_PUTOVI_KRAJ = "KRAJ";
        public static final String TABLE_PUTOVI_VRIJEME_HODA = "VRIJEME_HODA";
        public static final String TABLE_PUTOVI_DULJINA = "DULJINA";
        public static final String TABLE_PUTOVI_VISINSKA_RAZLIKA = "VISINSKA_RAZLIKA";
        public static final String TABLE_PUTOVI_OPIS = "OPIS";
        public static final String TABLE_PUTOVI_OZNAKA = "OZNAKA";
        public static final String TABLE_PUTOVI_DRUSTVO = "DRUSTVO";
        public static final String TABLE_PUTOVI_NAPOMENA = "NAPOMENA";
        public static final String TABLE_PUTOVI_GPX = "GPX";
        public static final String TABLE_PUTOVI_IMAGE = "IMAGE";
        public static final String TABLE_PUTOVI_LAT_START = "LAT_START";
        public static final String TABLE_PUTOVI_LAT_END = "LAT_END";
        public static final String TABLE_PUTOVI_ELEVATION_START = "ELEVATION_START";
        public static final String TABLE_PUTOVI_LON_START = "LON_START";
        public static final String TABLE_PUTOVI_LON_END = "LON_END";
        public static final String TABLE_PUTOVI_ELEVATION_END = "ELEVATION_END";






    }




    public Cursor getData(String mId, String tableName, SQLiteDatabase db) {
            String selectQuery = "SELECT  * FROM " + tableName + " WHERE _id = " + mId +" LIMIT 1";
            Cursor c = db.rawQuery(selectQuery, null);
            String[] data = null;
            if (c.moveToFirst()) {
                String pln = c.getString(c.getColumnIndex("PLANINA"));
                Log.i("***getDAtaonclick*** ",pln);
            }
            return c;
    }

}


