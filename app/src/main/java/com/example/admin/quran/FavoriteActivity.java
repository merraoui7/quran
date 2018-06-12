package com.example.admin.quran;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    SQLiteDatabase sqLiteDatabase ;
    SQL_Lite_DB sql_lite_db;
    RecyclerView recyclerView;
    SurahAdapter adapter;
    List<Surah> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        sql_lite_db =new SQL_Lite_DB(this);
        sqLiteDatabase=sql_lite_db.getWritableDatabase();

        datalist.clear();
        datalist =sql_lite_db.readDataList();

        recyclerView =(RecyclerView) findViewById(R.id.favlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter =new SurahAdapter(datalist,this);
        recyclerView.setAdapter(adapter);

    }
}
