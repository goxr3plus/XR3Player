package DrawTesting;

public class Spirograph {

    public static void main(String[] args) {
        double R = 180;//Double.parseDouble(args[0]);
        double r = 40;//Double.parseDouble(args[1]);
        double a = 30;//Double.parseDouble(args[2]);

        StdDraw.setXscale(-300, +300);
        StdDraw.setYscale(-300, +300);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.enableDoubleBuffering();

        for (double t = 0.0; t < 500; t += 0.01) {
            double x = (R+r) * Math.cos(t) - (r+a) * Math.cos(((R+r)/r)*t);
            double y = (R+r) * Math.sin(t) - (r+a) * Math.sin(((R+r)/r)*t);
            double degrees = -Math.toDegrees((R+r)/r)*t;
            StdDraw.picture(x, y, "earth.gif", degrees);
            // StdDraw.rotate(+Math.toDegrees((R+r)/r)*t);
            StdDraw.show();
            StdDraw.pause(1);
        }

    }
   
}