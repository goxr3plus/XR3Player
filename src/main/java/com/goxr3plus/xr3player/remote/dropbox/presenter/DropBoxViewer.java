package main.java.com.goxr3plus.xr3player.remote.dropbox.presenter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.java.com.goxr3plus.xr3player.application.presenter.treeview.FileTreeItem;
import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.remote.dropbox.authorization.DropboxAuthenticationBrowser;
import main.java.com.goxr3plus.xr3player.remote.dropbox.services.RefreshService;

public class DropBoxViewer extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private Label topLabel;
	
	@FXML
	private Button refresh;
	
	@FXML
	private Button collapseTree;
	
	@FXML
	private TreeView<String> treeView;
	
	@FXML
	private Label refreshLabel;
	
	@FXML
	private ProgressIndicator progressIndicator;
	
	@FXML
	private VBox loginVBox;
	
	@FXML
	private Button authorizationButton;
	
	@FXML
	private TextField accessTokenTextField;
	
	@FXML
	private Button loginButton;
	
	@FXML
	private VBox errorVBox;
	
	@FXML
	private JFXButton tryAgain;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	// -------------------------------------------------------------
	
	private final RefreshService refreshService = new RefreshService(this);
	
	// -------------------------------------------------------------
	
	private final RemoteFileTreeItem root = new RemoteFileTreeItem("root");
	
	// -------------------------------------------------------------
	
	private final DropboxAuthenticationBrowser authenticationBrowser = new DropboxAuthenticationBrowser();
	
	private String accessToken;
	
	/**
	 * Constructor.
	 */
	public DropBoxViewer() {
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "DropboxViewer.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		
		//TreeView
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		// Mouse Released Event
		treeView.setOnMouseReleased(this::treeViewMouseReleased);
		
		//refreshLabel
		refreshLabel.visibleProperty().bind(refreshService.runningProperty());
		
		//Progress Indicator
		progressIndicator.progressProperty().bind(refreshService.progressProperty());
		
		//collapseTree
		collapseTree.setOnAction(a -> {
			//Trick for CPU based on this question -> https://stackoverflow.com/questions/15490268/manually-expand-collapse-all-treeitems-memory-cost-javafx-2-2
			root.setExpanded(false);
			
			//Set not expanded all the children
			collapseTreeView(root, false);
			
			//Trick for CPU
			root.setExpanded(true);
		});
		
		//refresh
		refresh.setOnAction(a -> recreateTree());
		
		// authorizationButton
		authorizationButton.setOnAction(a -> requestDropBoxAuthorization());
		
	}
	
	/**
	 * Request XR3Player Authorization to have access to his/her DropBox Account
	 */
	public void requestDropBoxAuthorization() {
		
		//Show authentication browser
		authenticationBrowser.showAuthenticationWindow();
		
		//Wait for access token
		authenticationBrowser.accessTokenProperty().addListener((observable , oldValue , newValue) -> {
			if (!newValue.equals("")) {
				accessToken = newValue;
				System.out.println("[" + accessToken + "]");
				authenticationBrowser.getWindow().close();
				
				//loginVBox
				loginVBox.setVisible(false);
				
				//Go Make It
				recreateTree();
			}
		});
	}
	
	/**
	 * Collapses the whole TreeView
	 * 
	 * @param item
	 */
	private void collapseTreeView(TreeItem<String> item , boolean expanded) {
		if (item == null || item.isLeaf())
			return;
		
		item.setExpanded(expanded);
		item.getChildren().forEach(child -> collapseTreeView(child, expanded));
	}
	
	/**
	 * Recreates the TreeView
	 */
	public void recreateTree() {
		
		//Clear all the children
		root.getChildren().clear();
		
		//Start the Service
		refreshService.restart();
	}
	
	/**
	 * Used for TreeView mouse released event
	 * 
	 * @param mouseEvent
	 *            [[SuppressWarningsSpartan]]
	 */
	private void treeViewMouseReleased(MouseEvent mouseEvent) {
		//Get the selected item
		RemoteFileTreeItem source = (RemoteFileTreeItem) treeView.getSelectionModel().getSelectedItem();
		
		// host is not on the game
		if (source == null || source == root) {
			mouseEvent.consume();
			return;
		}
		
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
			
			// source is expanded
			if (!source.isExpanded() && source.getChildren().isEmpty()) {
				
				//Check if the TreeItem has not children yet
				if (source.getChildren().isEmpty()) {
					
				} else {
					// if you want to implement rescanning a
					// directory
					// for
					// changes this would be the place to do it
				}
				
				source.setExpanded(true);
			}
			
		} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			
		}
	}
	
	/**
	 * @return the root of the tree
	 */
	public RemoteFileTreeItem getRoot() {
		return root;
	}
	
	/**
	 * @return the progressIndicator
	 */
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}
	
	/**
	 * @param progressIndicator
	 *            the progressIndicator to set
	 */
	public void setProgressIndicator(ProgressIndicator progressIndicator) {
		this.progressIndicator = progressIndicator;
	}
	
	/**
	 * @return the topLabel
	 */
	public Label getTopLabel() {
		return topLabel;
	}
	
	/**
	 * @return the authenticationBrowser
	 */
	public DropboxAuthenticationBrowser getAuthenticationBrowser() {
		return authenticationBrowser;
	}
	
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * @return the treeView
	 */
	public TreeView<String> getTreeView() {
		return treeView;
	}
	
	/**
	 * @return the refreshLabel
	 */
	public Label getRefreshLabel() {
		return refreshLabel;
	}
	
	/**
	 * @param refreshLabel
	 *            the refreshLabel to set
	 */
	public void setRefreshLabel(Label refreshLabel) {
		this.refreshLabel = refreshLabel;
	}
	
}
