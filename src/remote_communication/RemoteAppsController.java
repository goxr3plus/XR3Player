/*
 * 
 */
package remote_communication;

import java.io.BufferedReader;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import application.Main;
import javafx.application.Platform;


/**
 * Communicates with the external speech recognizer.
 *
 * @author GOXR3PLUS
 */
public class RemoteAppsController {

    /** The result. */
    // More General
    private String result;

    /** The process. */
    // About Process
    private Process process;

    /** The buffered reader. */
    private BufferedReader bufferedReader;

    /** The locked player. */
    // Locks
    private int lockedPlayer = -1;

    /** The logger. */
    // Logger
    private Logger logger;

    /** The speech window. */
    // SpeechWindow
    private SpeechWindow speechWindow;

    /** The j channel communication. */
    // JChannelCommunication
    JChannelCommunication jChannelCommunication;

    /**
     * Constructor.
     */
    public RemoteAppsController() {

	logger = Logger.getLogger(getClass().getName());
	speechWindow = new SpeechWindow();
	jChannelCommunication = new JChannelCommunication("XR3Player");

    }

    /**
     * Starts the Speech Reader.
     */
    public void startSpeechRecognizer() {

	Main.sideBar.getSpeechProgressIndicator().setVisible(true);
	speechWindow.show();

	// Start an external Thread which
	// is communicating with the SpeechRecognizer application
	new Thread(() -> {
	    try {
		ProcessBuilder builder = new ProcessBuilder("java", "-jar", System.getProperty("user.home")
			+ File.separator + "Desktop" + File.separator + "XR3SpeechRecognizer.jar", "SPEECH");

		process = builder.start();

		ProcessBuilder builder2 = new ProcessBuilder("java", "-jar",
			System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "XR3Voice.jar",
			"VOICE");

		builder2.start();

		// process = new
		// ProcessBuilder(System.getProperty("connectedUser.home")
		// + File.separator
		// + "Desktop" + File.separator +
		// "speechRecognizer.exe").start();

		// builder.redirectErrorStream(true);
		// process = builder.start();
		// bufferedReader = new BufferedReader(new
		// InputStreamReader(process.getInputStream()));
		//
		// // Continuously Read Output
		// String line;
		// while (process.isAlive())
		// while ( ( line = bufferedReader.readLine() ) != null &&
		// !line.isEmpty()) {
		// speechWindow.appendText(line);
		// result = line;
		// checkResult(line);
		// }

	    } catch (Exception ex) {
		logger.log(Level.INFO, "", ex);
	    } finally {
		Platform.runLater(() -> {
		    Main.sideBar.getSpeechProgressIndicator().setVisible(false);
		});
	    }
	    // finally {
	    // // Stop SpeechRecognizer
	    // stopSpeechRec(false);
	    //
	    // // log the exit of the Thread
	    // logger.info("SpeechReader Stopped! Process is alive:" +
	    // process.isAlive() + " >Exit Value:"
	    // + process.exitValue());
	    // }
	});// .start();

    }

    /**
     * Stops the SpeechReader.
     *
     * @param stopTTS
     *            the stop TTS
     */
    public void stopSpeechRec(boolean stopTTS) {
	// stopTTS?
	// tts.stopSpeaking(stopTTS);
	if (process != null && process.isAlive())
	    process.destroy();

	//
	if (process != null)
	    Platform.runLater(() -> {
		Main.sideBar.getSpeechToggleButton().setSelected(false);
		Main.sideBar.getSpeechProgressIndicator().setVisible(false);
	    });

    }

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							     Speech Recognizer Results
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * Checks the result for any valid commands.
     *
     * @param result
     *            the result
     */
    private void checkResult(String result) {
	this.result = result;
	if (result.contains("<unk>"))
	    return;

	System.out.println("Checking result");

	// --------------------- Commands ------------------------------------
	if (result.contains("hey deck")) {
	    if (result.contains("lock")) {
		if (result.contains("deck zero"))
		    speechCommandForPlayer(0, "Deck Zero Locked!");
		else if (result.contains("deck one"))
		    speechCommandForPlayer(1, "Deck One Locked!");
		else if (result.contains("deck two"))
		    speechCommandForPlayer(2, "Deck Two Locked!");

		// A deck has been locked || // No deck is locked
	    } else if (lockedPlayer != -1 || lockedPlayer == -1)
		speechCommandForPlayer(lockedPlayer, null);

	    return;
	} else {
	    jChannelCommunication.sendMessage(new Message(null, "XR3Voice<> What?"));
	}

	// ---------------------------Information-------------------------------------
	if ("Loading...".equals(result)) {
	    // tts.speak("Loading Speech Recognizer ...", 2.0f, false, false);
	} else if ("Start Speaking...".equals(result)) {
	    // tts.speak("You can start to speak...", 2.0f, false, true);
	    Platform.runLater(() -> Main.sideBar.getSpeechProgressIndicator().setVisible(false));
	} else if ("Microphone not available...".equals(result)) {
	    // tts.speak("see you later", 2.0f, false, true);
	} else if ("hey see you later".equals(result)) {
	    // tts.speak("i love you too", 2.0f, false, true);
	    stopSpeechRec(false);
	}

    }

    /**
     * All the speech commands for players.
     *
     * @param key
     *            the key
     * @param saySomething
     *            the say something
     */
    private void speechCommandForPlayer(int key, String saySomething) {
	// Check if no deck is locked
	if (key == -1) {
	    jChannelCommunication.sendMessage(new Message(null, "XR3Voice<> No deck is locked"));
	    return;
	} else {
	    lockedPlayer = key;
	    jChannelCommunication.sendMessage(new Message(null, "XR3Voice<>" + saySomething));
	}

	if (result.contains("pause")) {
	    Main.xPlayersList.getXPlayer(key).pause();
	    // playSound()
	} else if (result.contains("resume")) {
	    Main.xPlayersList.getXPlayer(key).resume();
	    // playSound()
	} else if (result.contains("stop")) {
	    Main.xPlayersList.getXPlayer(key).stop();
	    // playSound()
	} else if (result.contains("restart")) {
	    Platform.runLater(Main.xPlayersList.getXPlayerController(key)::replaySong);
	    // playSound()
	} else if (result.contains("maximum volume")) {
	    Main.xPlayersList.getXPlayerController(key).maximizeVolume();
	    // playSound()
	} else if (result.contains("minimum volume")) {
	    Main.xPlayersList.getXPlayerController(key).minimizeVolume();
	    // playSound()
	} else if (result.contains("increase volume by")) { // increase volume
	    if (result.contains("five")) {
		Main.xPlayersList.getXPlayerController(key).adjustVolume(5);
		// playSound()
	    } else if (result.contains("ten")) {
		Main.xPlayersList.getXPlayerController(key).adjustVolume(10);
		// playSound()
	    } else if (result.contains("twenty")) {
		Main.xPlayersList.getXPlayerController(key).adjustVolume(20);
		// playSound()
	    } else if (result.contains("thirty")) {
		Main.xPlayersList.getXPlayerController(key).adjustVolume(30);
		// playSound()
	    }
	} else if (result.contains("low volume by")) { // low volume
	    if (result.contains("five")) {
		Main.xPlayersList.getXPlayerController(key).adjustVolume(-5);
		// playSound()
	    } else if (result.contains("ten")) {
		Main.xPlayersList.getXPlayerController(key).adjustVolume(-10);
		// playSound()
	    } else if (result.contains("twenty")) {
		Main.xPlayersList.getXPlayerController(key).adjustVolume(-20);
		// playSound()
	    } else if (result.contains("thirty")) {
		Main.xPlayersList.getXPlayerController(key).adjustVolume(-30);
		// playSound()
	    }
	} else if (result.contains("set volume")) { // set volume
	    volumeLevel(key);
	}
    }

    /**
     * Volume level.
     *
     * @param key
     *            the key
     */
    private void volumeLevel(int key) {
	if (result.contains("volume zero")) {
	    Main.xPlayersList.getXPlayerController(key).setVolume(0);
	    // playSound()
	} else if (result.contains("five")) {
	    Main.xPlayersList.getXPlayerController(key).setVolume(5);
	    // playSound()
	} else if (result.contains("ten")) {
	    Main.xPlayersList.getXPlayerController(key).setVolume(10);
	    // playSound()
	} else if (result.contains("twenty")) {
	    Main.xPlayersList.getXPlayerController(key).setVolume(20);
	    // playSound()
	} else if (result.contains("thirty")) {
	    Main.xPlayersList.getXPlayerController(key).setVolume(30);
	    // playSound()
	} else if (result.contains("fourty")) {
	    Main.xPlayersList.getXPlayerController(key).setVolume(40);
	    // playSound()
	} else if (result.contains("fifty")) {
	    Main.xPlayersList.getXPlayerController(key).setVolume(50);
	    // playSound()
	}
    }

    // } else if (result.contains("open face book")) {
    // tts.speak("I am opening facebook page.");
    // try {
    // Runtime.getRuntime().exec("cmd /c start firefox www.facebook.com");
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // } else if (result.contains("open mail")) {
    // tts.speak("I am opening mail", 2.0f, true, false);
    // try {
    // Runtime.getRuntime().exec("cmd /c start firefox www.gmail.com");
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // } else if (result.contains("good bye")) {
    // tts.speak("Good Bye");
    // // stopSpeechReader(false);

    /*-----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     * 
     * 
     * 							     JChannelCommunication
     * 
     * -----------------------------------------------------------------------
     * 
     * -----------------------------------------------------------------------
     */

    /**
     * The Class JChannelCommunication.
     *
     * @author GOXR3PLUS
     */
    public class JChannelCommunication extends ReceiverAdapter {

	/** The channel. */
	// JChannel
	JChannel channel;

	/** The logger. */
	// Logger
	Logger logger;

	/** The channel name. */
	// ChannelName
	String channelName;

	/**
	 * Constructor.
	 *
	 * @param channelName
	 *            the channel name
	 */
	public JChannelCommunication(String channelName) {

	    // initialize
	    this.channelName = channelName;
	    logger = Logger.getLogger(getClass().getName());

	    try {
		// Initialize the channel
		channel = new JChannel();

		// Set the receiver
		channel.setName(channelName);
		channel.setReceiver(this);

		// Joins the cluster
		channel.connect("XR3PlayerCluster");

	    } catch (IllegalStateException ex) {
		logger.log(Level.SEVERE, channelName + " failed to start a channel![ The channel is closed]", ex);
	    } catch (Exception ex) {
		logger.log(Level.SEVERE,
			channelName + " failed to start a channel![ The protocol stack cannot be started]", ex);
	    }
	}

	/* (non-Javadoc)
	 * @see org.jgroups.ReceiverAdapter#viewAccepted(org.jgroups.View)
	 */
	@Override
	public void viewAccepted(View newView) {

	    System.out.println(newView);

	}

	/* (non-Javadoc)
	 * @see org.jgroups.ReceiverAdapter#receive(org.jgroups.Message)
	 */
	@Override
	public void receive(Message msg) {

	    String message = msg.getObject();
	    System.err.println(msg.getSrc() + ": " + message);

	    // Check the received message
	    if (message.startsWith("XR3Player<>")) {
		String result = message.replaceAll("XR3Player<>", "");
		// sendMessage(new Message(null, "XR3Voice<>" + result));
		checkResult(result);
	    }
	}

	/**
	 * Send a message to the Cluster.
	 *
	 * @param message
	 *            the message
	 */
	public void sendMessage(Message message) {
	    try {
		channel.send(message);
	    } catch (Exception ex) {
		logger.log(Level.SEVERE, channelName + " failed to send Message!", ex);
	    }
	}
    }

}
