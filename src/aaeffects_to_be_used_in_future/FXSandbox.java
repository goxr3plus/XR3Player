/*
 * 
 */
package aaeffects_to_be_used_in_future;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class FXSandbox.
 */
public class FXSandbox extends Application {

	/** The Constant STAR_COUNT. */
	private static final int STAR_COUNT = 5000;

	/** The nodes. */
	private final Rectangle[] nodes = new Rectangle[STAR_COUNT];
	
	/** The angles. */
	private final double[] angles = new double[STAR_COUNT];
	
	/** The start. */
	private final long[] start = new long[STAR_COUNT];

	/** The random. */
	private final Random random = new Random();

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage primaryStage) {
		for (int i = 0; i < STAR_COUNT; i++) {
			nodes[i] = new Rectangle(2, 2, Color.RED);
			angles[i] = 2.0 * Math.PI * random.nextDouble();
			start[i] = random.nextInt(2000000000);
		}
		final Scene scene = new Scene(new Group(nodes), 800, 600, Color.BLACK);
		primaryStage.setScene(scene);

		primaryStage.show();

		new AnimationTimer() {
			@Override
			public void handle(long now) {

				/*
				 * if(zg==true){ ++counter; z+=0.01; System.out.println(z);
				 * if(counter==100) zg=false; }else if(zg==false){ --counter;
				 * z-=0.01; if(counter==0) zg=true; }
				 */

				// z=Math.random();

				// System.out.println(f);
				// if(f>-1.2) f-=0.01;
				// if(f<-1.2) f+=0.009;
				// s-=0.0111111111;
				// z+=0.01;

				f = 1;
				s = 1;

				final double width = z * primaryStage.getWidth();
				final double height = 0.5 * primaryStage.getHeight();
				final double radius = Math.sqrt(2) * Math.max(width, height);
				for (int i = 0; i < STAR_COUNT; i++) {
					if (i % 1000 == 0)
						nodes[i].setFill(Color.rgb(random(), random(), random()));
					final Node node = nodes[i];
					final double angle = angles[i];
					final long t = (now - start[i]) % 2000000000;
					final double d = t * radius / 2000000000.0;
					node.setTranslateX(Math.cos(angle) * d * f + width);
					node.setTranslateY(Math.sin(angle) * d * s + height);
				}
			}
		}.start();
	}

	/** The z. */
	double z = 0.5;
	
	/** The f. */
	double f = 0.5;
	
	/** The s. */
	double s = 0.6;
	
	/** The counter. */
	double counter = 0;

	/** The zg. */
	public boolean zg = true;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Random.
	 *
	 * @return the int
	 */
	public int random() {
		return (int) (Math.random() * 255);

	}

}