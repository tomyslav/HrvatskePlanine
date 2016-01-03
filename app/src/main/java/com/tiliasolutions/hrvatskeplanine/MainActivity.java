package com.tiliasolutions.hrvatskeplanine;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tiliasolutions.hrvatskeplanine.db.DatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<HashMap> vrhoviTableData;
    private ArrayList<HashMap> putoviTableData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = DatabaseHelper.getInstance(this);
        db = dbHelper.getReadableDatabase();

        //TODO this shoul be in dbHelper
        populateVrhoviTable(db);
        populatePutoviTable(db);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startListActivity(View view){
        Intent in = new Intent(MainActivity.this, RegionListActivity.class);
        startActivity(in);
    }


    public void startMapActivity(View view){
        Intent i = new Intent(MainActivity.this, MainMapActivity.class);
        switch(view.getId()){
            case R.id.vrhoviMapButton:
                i.putExtra("CHOOSE_MAP",this.findViewById(R.id.vrhoviMapButton).getTag()
                        .toString());
                break;
            case R.id.putoviMapButton:
                i.putExtra("CHOOSE_MAP",this.findViewById(R.id.putoviMapButton).getTag().toString());
                break;
            default:
                i.putExtra("CHOOSE_MAP",this.findViewById(R.id.vrhoviMapButton).getTag().toString());
                break;
        }
        startActivity(i);
    }



    public void populateVrhoviTable(SQLiteDatabase db){
        int getIdentifier = this.getResources().getIdentifier("vrhovi_data", "raw", this
                .getPackageName());

        try {
            dbHelper.openSerFile(this.getResources().openRawResource(getIdentifier), "v");
            vrhoviTableData = dbHelper.getVrhoviData();
            dbHelper.iterateThruHashMapAndpupulateVrhoviTable(vrhoviTableData,db);
        } catch (IOException e) {
            e.printStackTrace();
        }

           }



    public void populatePutoviTable(SQLiteDatabase db){
        int getIdentifier = this.getResources().getIdentifier("putovi_data", "raw", this
                .getPackageName());

        try {
            dbHelper.openSerFile(this.getResources().openRawResource(getIdentifier), "p");
            putoviTableData = dbHelper.getPutoviData();
            dbHelper.iterateThruHashMapAndPopulatePutoviTable(putoviTableData, db);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
