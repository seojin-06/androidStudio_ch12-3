package com.cookandroid.project12_3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    DatePicker dp;
    EditText edtDiary;
    Button btnWrite;
    String fileName;
    public static String datepicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("간단 일기장");

        dp = (DatePicker) findViewById(R.id.datePicker1);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        btnWrite = (Button) findViewById(R.id.btnWrite);

        myHelper = new myDBHelper(this);

        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        int cMonth = cal.get(Calendar.MONTH);
        int cDay = cal.get(Calendar.DAY_OF_MONTH);

        datepicked = Integer.toString(cYear) + Integer.toString(cMonth + 1) + Integer.toString(cDay);

        dp.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                sqlDB = myHelper.getWritableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT content FROM myDiary WHERE diaryDate =" + datepicked
                        + ";", null);
                if (cursor.getCount() == 0) {
                    edtDiary.setText("");
                    edtDiary.setHint("일기 없음");
                    btnWrite.setText("새로 저장");
                } else {
                    cursor.moveToNext();
                    edtDiary.setText(cursor.getString(0));
                    btnWrite.setText("수정하기");
                }
                cursor.close();
                sqlDB.close();
                btnWrite.setEnabled(true);
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnWrite.getText().equals("수정하기")) {
                    sqlDB = myHelper.getWritableDatabase();
                    sqlDB.execSQL("UPDATE myDiary SET content = '" + edtDiary.getText().toString() +"' WHERE diaryDate = '"
                            + datepicked + "';");
                    sqlDB.close();
                    Toast.makeText(getApplicationContext(), "수정됨", Toast.LENGTH_SHORT).show();
                } else {
                    sqlDB = myHelper.getWritableDatabase();
                    sqlDB.execSQL("INSERT INTO myDiary (diaryDate, content) VALUES ('" + datepicked + "' , '"
                            + edtDiary.getText().toString() +"');");
                    sqlDB.close();
                    Toast.makeText(getApplicationContext(), "저장됨", Toast.LENGTH_SHORT).show();
                    btnWrite.setText("수정하기");
                }
            }
        });
    }

    public class myDBHelper extends SQLiteOpenHelper {

        public myDBHelper(@Nullable Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE myDiary (diaryDate char(10) PRIMARY KEY, content VARCHAR(500));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS myDiary");
            onCreate(db);
        }
    }
}