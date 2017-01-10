/*
 * 
 */
package smartcontroller;


/**
 * Indicates the Genre of Songs.
 *
 * @author GOXR3PLUS
 */
public enum Genre {
	
	/** The librarysong. */
	LIBRARYSONG,
	/** The xplaylistsong. */
	XPLAYLISTSONG,
	/** The topcategorysong. */
	TOPCATEGORYSONG,
	/** The radiostation. */
	RADIOSTATION,
	/** The unknown. */
	UNKNOWN;
	
	/**
	 * Indicates the type of data.
	 *
	 * @author GOXR3PLUS
	 */
	public enum TYPE {
		
		/** The url. */
		URL,
		/** The file. */
		FILE,
		/** The inputstream. */
		INPUTSTREAM,
		/** The unkown. */
		UNKOWN;
	}
	
}
