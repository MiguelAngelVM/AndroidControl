package mx.edu.utem.androidcontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by asdf on 25/10/15.
 */
public class Rotation{
   static MainActivity main;
    public static void viewRotation(int numRotation)
    {
        main.imViewAndroid.setImageBitmap(rotateImage(BitmapFactory.decodeResource(main.getResources(), R.drawable.qwdqwd), numRotation));

    }
    public static Bitmap rotateImage(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
