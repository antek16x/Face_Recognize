package com.example.facerecognize;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.facerecognize.R;
import com.google.gson.Gson;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

public class MenuActivity extends ListActivity {

    Intent fileIntent, cameraIntent;
    Uri uri;
    Uri imageUri;

    private static FaceServiceClient faceServiceClient =
            new FaceServiceRestClient("df6bf8f8f8b04783a1a2106537fe47bb");

    private enum Action {
        LOAD_PICTURE, TAKE_A_PHOTO, EXIT, UNKNOWN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_menu_item,
                getResources().getStringArray(R.array.menu_items));
        setListAdapter(adapter);
        getListView().setOnItemClickListener(listener);
    }

    public AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedItem = (String) getListView().getItemAtPosition(position);
            String selectedAction = selectedItem.replace(' ', '_').toUpperCase(Locale.getDefault());
            Action action = Action.UNKNOWN;
            try {
                action = Action.valueOf(selectedAction);
            } catch (IllegalArgumentException ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            try {
                switch (action) {
                    case LOAD_PICTURE:
                        loadPicture();
                        break;
                    case TAKE_A_PHOTO:
                        loadCamera();
                        break;
                    case EXIT:
                        MenuActivity.this.finish();
                        break;
                    case UNKNOWN:
                        Toast.makeText(getApplicationContext(), "Unrecognized action '" + selectedAction + "'",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    };

    private void loadPicture() {
        fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        startActivityForResult(fileIntent, 10);
    }

    private void loadCamera() {
        cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = makeUri();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, 20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 10:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        detectAndFrame(bitmap);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                break;

            case 20:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        detectAndFrame(bitmap);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                break;
        }
    }

    private void detectAndFrame(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream((outputStream.toByteArray()));

        AsyncTask<InputStream, String, Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>() {
            private ProgressDialog pd = new ProgressDialog(MenuActivity.this);

            @Override
            public Face[] doInBackground(InputStream... params) {

                publishProgress("Detecting...");
                FaceServiceClient.FaceAttributeType[] faceAttr = new FaceServiceClient.FaceAttributeType[]{
                        FaceServiceClient.FaceAttributeType.Age,
                        FaceServiceClient.FaceAttributeType.Gender,
                        FaceServiceClient.FaceAttributeType.Smile,
                        FaceServiceClient.FaceAttributeType.FacialHair,
                };
                try {
                    Face[] result = faceServiceClient.detect(params[0],
                            true,
                            false,
                            faceAttr);

                    if (result == null) {
                        publishProgress("Detection finished. Nothing detected");
                        return null;
                    }
                    publishProgress(String.format("Detection finished. %d face(s) detected", result.length));
                    return result;

                } catch (Exception e) {
                    publishProgress("Detection failed");
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                pd.show();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                pd.setMessage(values[0]);
            }

            @Override
            protected void onPostExecute(Face[] faces) {
                pd.dismiss();
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                Gson gson = new Gson();
                String data = gson.toJson(faces);
                if (faces == null || faces.length == 0) {
                    Toast.makeText(getApplicationContext(), "No faces detected. You may not have added the API Key or try retaking the picture.", Toast.LENGTH_LONG).show();
                } else {
                    intent.putExtra("list_faces", data);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image", byteArray);
                    startActivity(intent);
                }
            }
        };
        detectTask.execute(inputStream);
    }

    private Uri makeUri() {
        File picture = null;
        Calendar calendar = Calendar.getInstance();
        String fileName = "picture_" + calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.MONTH) +
                "_" + calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.HOUR_OF_DAY) +
                "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND);
        String fileExtension = ".jpg";
        String fullPath = "";
        File catalog = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            picture = File.createTempFile(fileName, fileExtension, catalog);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        Uri uri = FileProvider.getUriForFile(
                this,
                "com.example.android.filesprovider",
                picture);
        return uri;
    }
}