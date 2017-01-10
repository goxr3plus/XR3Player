/*
 * 
 */
package aacode_to_be_used_in_future;


import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.StackPane;
import tools.InfoTool;

// TODO: Auto-generated Javadoc
/**
 * The Class DJTabs.
 */
public class DJTabs extends StackPane{
	

	/** The dj beats. */
	public DJSoundTeam djBeats = new DJSoundTeam(getClass().getResource(InfoTool.sounds+"airhorn.mp3"));
	
	/** The dj scratches. */
	public DJSoundTeam djScratches = new DJSoundTeam(getClass().getResource(InfoTool.sounds+"scratch1.mp3"));
	  
	
	/**
	 * Instantiates a new DJ tabs.
	 *
	 * @param d the d
	 * @param y the y
	 * @param width the width
	 * @param e the e
	 */
	//Constructor
	public DJTabs(double d,int y,int width,double e){
		TabPane tabbedPane = new TabPane();
		
		setLayoutX(d);
		setLayoutY(y);
		setPrefSize(width, e);
		
		// djBeats
		/*djBeats.getChildren().addAll(djBeats.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "airhorn.mp3"), "airhorn", 1),
				djBeats.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "military.mp3"), "siren", 1),
				djBeats.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "par2.mp3"), "paran2", 1),
				djBeats.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "par3.mp3"), "paran3", 1),
				djBeats.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "bassSpoken.mp3"), "BassSpoken", 1),
				djBeats.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "yaka.mp3"), "yaka", 1),
				djBeats.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "hout.mp3"), "hout-hout", 1));

		for (Node comp : djBeats.getChildren())
			djBeats.group.getToggles().add(((DJSoundTeamButton) comp).getRadioButton());
		

		((DJSoundTeamButton) djBeats.getChildren().get(0)).getRadioButton().setSelected(true);*/

		tabbedPane.getTabs().add(new Tab("DJBeats", djBeats.border));

		
		// djScratches
		/*djScratches.getChildren().addAll(djScratches.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "scratch1.mp3"), "scratch1", 2),
				djScratches.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "scratch2.mp3"), "scratch2", 2),
				djScratches.new DJSoundTeamButton(getClass().getResource(InfoTool.sounds + "scratch3.mp3"), "scratch3", 2));

		for (Node comp : djScratches.getChildren())
			djScratches.group.getToggles().add(((DJSoundTeamButton) comp).getRadioButton());

		((DJSoundTeamButton) djScratches.getChildren().get(0)).getRadioButton().setSelected(true);*/

		tabbedPane.getTabs().add(new Tab("DJScratch", djScratches.border));

		
		
		// Finally
		tabbedPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		getChildren().add(tabbedPane);

	}
	
	
	/**
	 * Επιστρέφει την ομάδα με βάση την κατηγορία.
	 *
	 * @param category the category
	 * @return the team
	 */
	public DJSoundTeam getTeam(int category){
		if(category==1)
			return djBeats;
		else if(category==2)
			return djScratches;
		
		return null;
	}

	
  
}
