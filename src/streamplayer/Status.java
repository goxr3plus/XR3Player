package streamplayer;
/**
     * Status of Stream Player.
     *
     * @author GOXR3PLUS
     */
    public enum Status {

	/** UNKOWN STATUS. */
	UNKNOWN,

	/** In the process of opening the AudioInputStream. */
	OPENING,

	/** AudioInputStream is opened. */
	OPENED,

	/** play event has been fired. */
	PLAYING,

	/** player is stopped. */
	STOPPED,

	/** player is paused. */
	PAUSED,

	/** resume event is fired. */
	RESUMED,

	/** player is in the process of seeking. */
	SEEKING,
	
	/**
	 * The player is buffering
	 */
	BUFFERING,

	/** seek work has been done. */
	SEEKED,

	/** EOM stands for "END OF MEDIA". */
	EOM,

	/** player pan has changed. */
	PAN,

	/** player gain has changed. */
	GAIN;

    }