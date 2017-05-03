package DrawTesting;
public class Globe { 

    public static void main(String[] args) { 
        double alpha = 0.95;//Double.parseDouble(args[0]);
        StdDraw.setYscale(-1, +1);
        StdDraw.setXscale(-1, +1);
        StdDraw.setPenColor(StdDraw.BLUE);

        double x0 = 1, y0 = 0;
        for (double t = 0.0; t <= 20 * 360.0; t += 0.1) {
            double theta = Math.toRadians(t);
            double r = Math.cos(alpha * theta); 
            double x1 = r * Math.cos(theta);
            double y1 = r * Math.sin(theta);
            StdDraw.line(x0, y0, x1, y1);
            x0 = x1;
            y0 = y1;
        }
    }

}


