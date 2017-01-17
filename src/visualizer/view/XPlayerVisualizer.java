/*
 * 
 */
package visualizer.view;

import xplayer.presenter.XPlayerController;


/**
 * This class is updating the panel of the XPlayerUI.
 *
 * @author GOXR3PLUS
 */
public class XPlayerVisualizer extends Visualizer {
	
	/** The x player UI. */
	private XPlayerController xPlayerUI;
	
	/**
	 * Constructor.
	 *
	 * @param width the width
	 * @param height the height
	 * @param xPlayerUI the x player UI
	 */
	public XPlayerVisualizer(int width, int height, XPlayerController xPlayerUI) {
		super("XRPLAYER");
		
		this.xPlayerUI = xPlayerUI;
		this.animationService.passDJDisc(xPlayerUI.disc);
	//	resizeVisualizer(width, height);
		
		setScopeColor(xPlayerUI.disc.getArcColor());
		addMouseListener();
		
	}
	
	
}
