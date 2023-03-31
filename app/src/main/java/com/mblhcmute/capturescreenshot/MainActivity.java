package com.mblhcmute.capturescreenshot;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // write permission to access the storage
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // this is the card view whose screenshot
        // we will take in this article
        // get the view using fin view bt id
        CardView cardView = findViewById(R.id.card_View);

        // on click of this button it will capture
        // screenshot and save into gallery
        Button captureButton = findViewById(R.id.btn_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the bitmap of the view using
                // getScreenShotFromView method it is
                // implemented below
                Bitmap bitmap = getScreenShotFromView(cardView);

                // if bitmap is not null then
                // save it to gallery
                if (bitmap != null) {
                    saveMediaToStorage(bitmap);
                }
            }
        });
    }

    private Bitmap getScreenShotFromView(View v) {
        // create a bitmap object
        Bitmap screenshot = null;
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            // Now draw this bitmap on a canvas
            Canvas canvas = new Canvas(screenshot);
            v.draw(canvas);
        } catch (Exception e) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.getMessage());
        }
        // return the bitmap
        return screenshot;
    }

    // this method saves the image to gallery
    private void saveMediaToStorage(Bitmap bitmap) {
        // Generating a file name
        String filename = System.currentTimeMillis() + ".jpg";

        // Output stream
        OutputStream fos = null;

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            ContentResolver resolver = getContentResolver();

            // Content resolver will process the contentvalues
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            // Inserting the contentValues to
            // contentResolver and getting the Uri
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            // Opening an outputstream with the Uri that we got
            try {
                fos = imageUri != null ? resolver.openOutputStream(imageUri) : null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // These for devices running on android < Q
            File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = new File(imagesDir, filename);
            try {
                fos = new FileOutputStream(image);
            } catch (Exception e) {
                Log.e("GFG", "Failed to open FileOutputStream because:" + e.getMessage());
            }
        }

        if (fos != null) {
            try {
                // Finally writing the bitmap to the output stream that we opened
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Toast.makeText(this, "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("GFG", "Failed to write bitmap to FileOutputStream because:" + e.getMessage());
            } finally {
                try {
                    fos.close();
                } catch (Exception e) {
                    Log.e("GFG", "Failed to close FileOutputStream because:" + e.getMessage());
                }
            }
        }
    }
}