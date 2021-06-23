package com.example.facerecognize;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.facerecognize.R;
import com.google.gson.Gson;
import com.microsoft.projectoxford.face.contract.Face;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String data = getIntent().getStringExtra("list_faces");
        Gson gson = new Gson();

        Face[] faces = gson.fromJson(data, Face[].class);
        ListView myListView = (ListView) findViewById(R.id.listView);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        int childCount = 0;
        int adultCount = 0;
        
        for (int i = 0; i < faces.length; i++) {
            if (faces[i].faceAttributes.age < 18) {
                childCount++;
            } else {
                adultCount++;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Age information")
                .setMessage(String.format("Number of children in the photo: %d\n" +
                        "Number of adult in the photo: %d", childCount, adultCount))
                .setPositiveButton("See details", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create().show();

        CustomAdapter customAdapter = new CustomAdapter(faces, this, bitmap);
        myListView.setAdapter(customAdapter);
    }
}