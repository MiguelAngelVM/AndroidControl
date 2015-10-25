package mx.edu.utem.androidcontrol;


import android.os.AsyncTask;

/**
 * Created by asdf on 24/09/15.
 */


public class MiAssyncTask extends AsyncTask<Void, Void, Void> {


    MainActivity main;

    public static final String TAG = "Acelerometro";
    boolean arriba = true, abajo = true, derecha = true ,izquierda = true;




    @Override
    protected  Void doInBackground(Void... arg0) {
        while (MainActivity.getOrientation().destroy) {
            try {
                Thread.sleep(100);

            }catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            publishProgress();

        }

        return null;
    }
    @Override
    protected void onProgressUpdate(Void... progress)
    {

        if (MainActivity.getOrientation().getZ()>=5  && arriba==true && abajo == true)
        {
            arriba = false;

            main.getmConnectedThread().write("1");
        }
        else if (MainActivity.getOrientation().getZ()<=4 &&
                MainActivity.getOrientation().getZ()>= 1 &&
                arriba == false)
        {
            arriba = true;
            main.getmConnectedThread().write("5");
        }
        if(MainActivity.getOrientation().getY()>3 && derecha == true && izquierda == true)
        {
            derecha = false;
            main.getmConnectedThread().write("4");
        }
        else if (MainActivity.getOrientation().getY() <= 2 &&
                MainActivity.getOrientation().getY() >= -2 &&
                derecha == false)
        {
            derecha = true;
            main.getmConnectedThread().write("8");
        }
        if(MainActivity.getOrientation().getY()<-3 && derecha == true && izquierda == true)
        {
           izquierda = false;
           main.getmConnectedThread().write("3");


        }else if (MainActivity.getOrientation().getY() >= -2 &&
                MainActivity.getOrientation().getY() <= 2 &&
                izquierda == false)
        {
            izquierda = true;
            main.getmConnectedThread().write("7");
        }
        if(MainActivity.getOrientation().getZ()<-2 && abajo == true && arriba == true )
        {
            abajo = false;
            main.getmConnectedThread().write("2");

        }else if (MainActivity.getOrientation().getZ()>-2 &&
                MainActivity.getOrientation().getZ() <= 4 &&
                abajo == false)
        {
            abajo = true;
            main.getmConnectedThread().write("6");
        }
    }
}





