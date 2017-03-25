/*
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

   Also(warning!):
 
  1)You are not allowed to sell this product to third party.
  2)You can't change license and made it like you are the owner,author etc.
  3)All redistributions of source code files must contain all copyright
     notices that are currently in this file, and this list of conditions without
     modification.
 */

package streamplayer;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Special exceptions of StreamPlayer.
 *
 * @author GOXR3PLUS (www.goxr3plus.co.nf)
 * @author http://www.javazoom.net
 */
@SuppressWarnings("serial")
public class StreamPlayerException extends Exception {

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
	/**
	* LINE IS NOT SUPPORTED
	*/
	LINE_NOT_SUPPORTED,
	/** The skip not supported. */
	SKIP_NOT_SUPPORTED;
    }

    /** The cause. */
    private final Throwable cause;

    /**
     * Constructor.
     *
     * @param paramString
     *            String Parameter
     */
    public StreamPlayerException(PlayerException paramString) {
	super(paramString.toString());
	cause = null;
    }

    /**
     * Constructor.
     *
     * @param paramThrowable
     *            the param throwable
     */
    public StreamPlayerException(Throwable paramThrowable) {
	cause = paramThrowable;
    }

    /**
     * Constructor.
     *
     * @param paramString
     *            the param string
     * @param paramThrowable
     *            the param throwable
     */
    public StreamPlayerException(PlayerException paramString, Throwable paramThrowable) {
	super(paramString.toString());
	cause = paramThrowable;
    }

    @Override
    public Throwable getCause() {
	return cause;
    }

    @Override
    public String getMessage() {

	if (super.getMessage() != null)
	    return super.getMessage();
	else if (cause != null)
	    return cause.toString();

	return null;
    }

    @Override
    public void printStackTrace() {
	printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintStream printStream) {
	synchronized (printStream) {
	    PrintWriter localPrintWriter = new PrintWriter(printStream, false);
	    printStackTrace(localPrintWriter);
	    localPrintWriter.flush();
	}
    }

    @Override
    public void printStackTrace(PrintWriter printWriter) {
	if (cause != null)
	    cause.printStackTrace(printWriter);

    }
}
