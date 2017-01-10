/*
 * 
 */
package streamplayer;

import java.io.PrintStream;
import java.io.PrintWriter;

// TODO: Auto-generated Javadoc
/**
 * Special exceptions of StreamPlayer.
 *
 * @author GOXR3PLUS
 */
public class StreamPlayerException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	

	/**
	 * Type of exception.
	 *
	 * @author GOXR3PLUS
	 */
	public enum PlayerException {
		
		/** The gain control not supported. */
		GAIN_CONTROL_NOT_SUPPORTED, 
 /** The pan control not supported. */
 PAN_CONTROL_NOT_SUPPORTED, 
 /** The mute control not supported. */
 MUTE_CONTROL_NOT_SUPPORTED, 
 /** The balance control not supported. */
 BALANCE_CONTROL_NOT_SUPPORTED, 
 /** The wait error. */
 WAIT_ERROR, 
 /** The can not init line. */
 CAN_NOT_INIT_LINE, 
 /** The skip not supported. */
 SKIP_NOT_SUPPORTED;
	}

	/** The cause. */
	private final Throwable cause;

	/**
	 * Constructor.
	 *
	 * @param paramString the param string
	 */
	public StreamPlayerException(PlayerException paramString) {
		super(paramString.toString());
		cause = null;
	}

	/**
	 * Constructor.
	 *
	 * @param paramThrowable the param throwable
	 */
	public StreamPlayerException(Throwable paramThrowable) {
		cause = paramThrowable;
	}

	/**
	 * Constructor.
	 *
	 * @param paramString the param string
	 * @param paramThrowable the param throwable
	 */
	public StreamPlayerException(PlayerException paramString, Throwable paramThrowable) {
		super(paramString.toString());
		cause = paramThrowable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getCause()
	 */
	@Override
	public Throwable getCause() {
		return cause;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {

		if (super.getMessage() != null)
			return super.getMessage();
		else if (cause != null)
			return cause.toString();

		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace()
	 */
	@Override
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 */
	@Override
	public void printStackTrace(PrintStream printStream) {
		synchronized (printStream) {
			PrintWriter localPrintWriter = new PrintWriter(printStream, false);
			printStackTrace(localPrintWriter);
			localPrintWriter.flush();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
	 */
	@Override
	public void printStackTrace(PrintWriter printWriter) {
		if (cause != null)
			cause.printStackTrace(printWriter);

	}
}
