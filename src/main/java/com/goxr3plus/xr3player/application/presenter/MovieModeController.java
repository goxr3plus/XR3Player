package main.java.com.goxr3plus.xr3player.application.presenter;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;

public class MovieModeController extends BorderPane {
	
	//-----------------------------------------------------
	
	@FXML
	private StackPane stack1;
	
	@FXML
	private ImageView imageView1;
	
	@FXML
	private Label label1;
	
	@FXML
	private StackPane stack2;
	
	@FXML
	private ImageView imageView2;
	
	@FXML
	private Label label2;
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public MovieModeController() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "MoviesMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Called as soon as .fxml is initialised
	 */
	@FXML
	private void initialize() {
		
		stack1.boundsInLocalProperty().addListener(l -> {
			
			double width = stack1.getWidth();
			double height = stack1.getHeight();
			
			double size = 0;
			if (width > height)
				size = height;
			else
				size = width;
			
			imageView1.setFitWidth(size);
			imageView1.setFitHeight(size);
			imageView1.maxWidth(size);
			imageView1.maxHeight(size);
			
			imageView2.setFitWidth(size);
			imageView2.setFitHeight(size);
			imageView2.maxWidth(size);
			imageView2.maxHeight(size);
		});
		
		
//		stack2.boundsInLocalProperty().addListener(l -> {
//			
//			double width = stack2.getWidth();
//			double height = stack2.getHeight();
//			
//			double size = 0;
//			if (width > height)
//				size = height;
//			else
//				size = width;
//			
//			imageView2.setFitWidth(size);
//			imageView2.setFitHeight(size);
//			imageView2.maxWidth(size);
//			imageView2.maxHeight(size);
//		});
		
	}
	
}
