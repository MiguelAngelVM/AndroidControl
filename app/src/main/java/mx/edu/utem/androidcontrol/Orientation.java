package mx.edu.utem.androidcontrol;

/**
 * Created by asdf on 27/09/15.
 */
public class Orientation {

    public double getX() {

        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getAmax() {
        return amax;
    }

    public void setAmax(double amax) {
        this.amax = amax;
    }

    public double x=0,y=0,z=0,a=0,amax=0;

    public boolean getDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public boolean destroy = false;
}
