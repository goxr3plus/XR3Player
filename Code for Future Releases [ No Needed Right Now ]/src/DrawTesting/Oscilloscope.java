package DrawTesting;

public class Oscilloscope {

    public static void main(String[] args) {
        StdDraw.setXscale(-1, +1);
        StdDraw.setYscale(-1, +1);
        StdDraw.enableDoubleBuffering();

        double a    = 1;//Double.parseDouble(args[0]);    // amplitudes
        double b    = 1;//Double.parseDouble(args[1]);
        double wX   = 5;//Double.parseDouble(args[2]);    // angular frequencies
        double wY   = 3;//Double.parseDouble(args[3]);
        double phiX = 30;//Double.parseDouble(args[4]);    // phase factors
        double phiY = 45;//Double.parseDouble(args[5]);

        // convert from degrees to radians
        phiY = Math.toRadians(phiX);
        phiY = Math.toRadians(phiY);


        for (double t = 0.0; t < 10; t += 0.0001) {
            double x = a * Math.sin(wX * t + phiX);
            double y = b * Math.sin(wY * t + phiY);
            StdDraw.point(x, y);
            StdDraw.show();
            //StdDraw.pause(1);
        }
    }
   
}