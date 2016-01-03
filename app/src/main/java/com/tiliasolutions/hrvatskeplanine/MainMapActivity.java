package com.tiliasolutions.hrvatskeplanine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tiliasolutions.hrvatskeplanine.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class MainMapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<Object> vrhoviTableData; //for opening serialized file
    private HashMap<Marker, Integer> vrhoviHashMap = new HashMap<>(); //for marker
    // oninfoclicker
    private HashMap<Marker, Integer> putoviHashMap = new HashMap<>(); //for marker
    private Marker endMarker; //marker to show end point of trail



    private String vrhoviSerFile="vrhovi_data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        db=dbHelper.getReadableDatabase();
        setUpMapIfNeeded();

        //getCoordinates();

        if(getDataFromIntent().equals("vrhovi")){
            populateMapFromVrhoviTable();
        }else{
            populateMapFromPutoviTable();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //set zoom and center of map
        mMap.animateCamera(CameraUpdateFactory.
                newLatLngZoom(new LatLng(44.912801, 17.0656558), 6.0f));
        mMap.setMyLocationEnabled(true);


    }


    //reads database and populate markers.
    public void populateMapFromVrhoviTable(){
        Cursor c = db.rawQuery("select * from vrhoviTable", null);  //read whole database

        if (c.moveToFirst()){
            do{
                Marker tempMarker;
                    Double lat = c.getDouble(c.getColumnIndex("LAT"));
                    Double lon = c.getDouble(c.getColumnIndex("LON"));
                    Integer vrhId = c.getInt(c.getColumnIndex("_id"));
                tempMarker  = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .title(c.getString(c.getColumnIndex("PLANINA"))+" - "+
                                c.getString(c.getColumnIndex("VRH"))));
                vrhoviHashMap.put(tempMarker, vrhId);   //hashMap K-V = Marker-_ID
            }while(c.moveToNext());
        }
        c.close();

        //sets OnInfoWindowClickListener and reads _id from hashmap vrhoviHashMap
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker mrk) {
                //gets data form vrhoviTable for particular marker by _ID.
                String tempId = vrhoviHashMap.get(mrk).toString().trim();
                Cursor tempCursor = dbHelper.getData(
                        tempId,
                        DatabaseHelper.VrhoviEntry.TABLE_VRHOVI_NAME,
                        db);
                String planina = tempCursor.getString(tempCursor.getColumnIndex(
                        DatabaseHelper.VrhoviEntry.TABLE_VRHOVI_PLANINA));
                String visina = tempCursor.getString(tempCursor.getColumnIndex(
                        DatabaseHelper.VrhoviEntry.TABLE_VRHOVI_VISINA));
                String vrh = tempCursor.getString(tempCursor.getColumnIndex(
                        DatabaseHelper.VrhoviEntry.TABLE_VRHOVI_VRH));
                String prilaz = tempCursor.getString(tempCursor.getColumnIndex(
                        DatabaseHelper.VrhoviEntry.TABLE_VRHOVI_PRILAZ));
                String vidik = tempCursor.getString(tempCursor.getColumnIndex(
                        DatabaseHelper.VrhoviEntry.TABLE_VRHOVI_VIDIK));
                String zig = tempCursor.getString(tempCursor.getColumnIndex(
                        DatabaseHelper.VrhoviEntry.TABLE_VRHOVI_ZIG));


                //alert dialog for OnInfoWindowClickListener
                AlertDialog aDialog = new AlertDialog.Builder(MainMapActivity.this).create();
                aDialog.setTitle(planina + " - " + vrh);
                aDialog.setMessage(Html.fromHtml(
                        "<h3>" + visina + "m</h3></br></br>" +
                                "<h3>PRILAZ</h3></br><p>" + prilaz + "</p></br></br>" +
                                "<h3>VIDIK</h3></br><p>" + vidik + "</p>" +
                                "<h3>ŽIG</h3></br><p>" + zig + "</p>"));
                aDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                aDialog.show();
            }
        });
    }




    //PUTOVI MAP
    private void populateMapFromPutoviTable(){
        Cursor c = db.rawQuery("select * from putoviTable", null);  //read whole database

        if (c.moveToFirst()){
            do{
                Marker tempMarker;
                Double lat = c.getDouble(c.getColumnIndex("LAT_START"));
                Double lon = c.getDouble(c.getColumnIndex("LON_START"));
                String title = c.getString(c.getColumnIndex("NAZIV"));
                Integer vrhId = c.getInt(c.getColumnIndex("_id"));
                tempMarker  = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .title(title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                putoviHashMap.put(tempMarker, vrhId);   //hashMap K-V = Marker-_ID
            }while(c.moveToNext());
        }
        c.close();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {

                if (putoviHashMap.containsKey(m)) {


                    String tempId = putoviHashMap.get(m).toString().trim();
                    Cursor tempCursor = dbHelper.getData(
                            tempId,
                            DatabaseHelper.PutoviEntry.TABLE_PUTOVI_NAME,
                            db);
                    Double lat = tempCursor.getDouble(tempCursor.getColumnIndex(
                            DatabaseHelper.PutoviEntry.TABLE_PUTOVI_LAT_END));
                    Double lon = tempCursor.getDouble(tempCursor.getColumnIndex(
                            DatabaseHelper.PutoviEntry.TABLE_PUTOVI_LON_END));
                    showEndMarker(lat, lon);
                }
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker mrk) {
                String tempId = putoviHashMap.get(mrk).toString().trim();
                Cursor tempCursor = dbHelper.getData(
                        tempId,
                        DatabaseHelper.PutoviEntry.TABLE_PUTOVI_NAME,
                        db);

                String naziv = tempCursor.getString(tempCursor.getColumnIndex
                        (DatabaseHelper.PutoviEntry.TABLE_PUTOVI_NAZIV));
                String vrijeme_hoda = tempCursor.getString(tempCursor.getColumnIndex
                        (DatabaseHelper.PutoviEntry.TABLE_PUTOVI_VRIJEME_HODA));
                String duljina = tempCursor.getString(tempCursor.getColumnIndex
                        (DatabaseHelper.PutoviEntry.TABLE_PUTOVI_DULJINA));
                String opis = tempCursor.getString(tempCursor.getColumnIndex
                        (DatabaseHelper.PutoviEntry.TABLE_PUTOVI_OPIS));
                String visinska_razlika = tempCursor.getString(tempCursor.getColumnIndex
                        (DatabaseHelper.PutoviEntry.TABLE_PUTOVI_VISINSKA_RAZLIKA));


                //alert dialog for OnInfoWindowClickListener
                AlertDialog aDialog = new AlertDialog.Builder(MainMapActivity.this).create();
                aDialog.setTitle(naziv);
                aDialog.setMessage(Html.fromHtml(
                        "<h3>" + "Vrijeme Hoda: " + vrijeme_hoda + "h (" + duljina + "km) " +
                                "</h3></br></br>" +
                                "<h3>" + "Visinska razlika: " + visinska_razlika + "m" +
                                "</h3></br></br>" +
                                "<h3>OPIS</h3></br><p>" + opis + "</p></br></br>" +
                                "<h3>VISINSKA RAZLIKA</h3></br><p>" + visinska_razlika + "</p>"));
                aDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                aDialog.show();

            }
        });


    }

    private String getDataFromIntent(){
        Intent i = getIntent();
        return i.getStringExtra("CHOOSE_MAP");
    }

    private void showEndMarker(Double lat, Double lon) {
        if (endMarker != null) {
            endMarker.remove();

        }
        endMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }


}
