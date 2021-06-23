package com.example.facerecognize;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facerecognize.R;
import com.microsoft.projectoxford.face.contract.Face;

public class CustomAdapter extends BaseAdapter {

    private Face[] face;
    private Context context;
    private LayoutInflater inflater;
    private Bitmap originalBitmap;

    public CustomAdapter(Face[] face, Context context, Bitmap originalBitmap) {
        this.face = face;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.originalBitmap = originalBitmap;
    }

    @Override
    public int getCount() {
        return face.length;
    }

    @Override
    public Object getItem(int position) {
        return face[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.listview_layout, null);
        }

        TextView txtAge;
        TextView txtGender;
        TextView txtSmile;
        TextView txtFacialHair;
        ImageView imageView;

        txtAge = (TextView) view.findViewById(R.id.txtAge);
        txtGender = (TextView) view.findViewById(R.id.txtGender);
        txtSmile = (TextView) view.findViewById(R.id.txtSmile);
        txtFacialHair = (TextView) view.findViewById(R.id.txtFacialHair);

        imageView = (ImageView) view.findViewById(R.id.imgThumb);

        txtAge.setText("Age: " + face[position].faceAttributes.age);
        txtGender.setText("Gender: " + face[position].faceAttributes.gender);
        txtSmile.setText("Smile: " + face[position].faceAttributes.smile);
        txtFacialHair.setText(String.format("Facial hair: \nmustache: %f \nsideburns: %f \nbeard: %f",
                face[position].faceAttributes.facialHair.moustache,
                face[position].faceAttributes.facialHair.sideburns,
                face[position].faceAttributes.facialHair.beard));

        Bitmap bitmap = ImageHelper.generateThumbnail(originalBitmap,
                face[position].faceRectangle);
        imageView.setImageBitmap(bitmap);

        return view;
    }
}
