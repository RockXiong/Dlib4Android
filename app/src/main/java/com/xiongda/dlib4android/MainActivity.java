package com.xiongda.dlib4android;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("dlib");
    }

    public static final int REQUESTCODE_PICK = 0X002;
    private Button btnOpenImage;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        mImageView = (ImageView) findViewById(R.id.iv_image);
        //        tv.setText(stringFromJNI());
    }

    public void onClickOpenImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUESTCODE_PICK);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native int[] stringFromJNI(int[] pixels, int height, int width);

    private int[] face_detection(Bitmap origin_image) {
        float scale = 240.f / Math.max(origin_image.getHeight(), origin_image.getWidth());
        int width = (int) (origin_image.getWidth() * scale);
        int height = (int) (origin_image.getHeight() * scale);
        Bitmap resize_image = Bitmap.createScaledBitmap(origin_image, width, height, false);

        // 保存所有像素的数组,图片宽x高
        int[] pixels = new int[width * height];

        resize_image.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] rect = stringFromJNI(pixels, height, width);
        int[] result_rect = new int[4];
        result_rect[0] = (int) (rect[0] / scale);
        result_rect[1] = (int) (rect[1] / scale);
        result_rect[2] = (int) (rect[2] / scale);
        result_rect[3] = (int) (rect[3] / scale);
        result_rect[2] = result_rect[2] + result_rect[0];
        result_rect[3] = result_rect[3] + result_rect[1];
        return result_rect;
    }

    private void drawResult(Bitmap scaled) {
        int[] rect = face_detection(scaled);
        Canvas canvas = new Canvas(scaled.copy(Bitmap.Config.ARGB_8888,true));
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3.0f);
        canvas.drawLine(rect[0], rect[1], rect[2], rect[1], paint);//up
        canvas.drawLine(rect[0], rect[1], rect[0], rect[3], paint);//left
        canvas.drawLine(rect[0], rect[3], rect[2], rect[3], paint);//down
        canvas.drawLine(rect[2], rect[1], rect[2], rect[3], paint);//right
        mImageView.setImageBitmap(scaled);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_PICK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                drawResult(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver()
                                   .query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
