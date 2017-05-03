/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2010 Besmir Beqiri
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package xtrememp.player.audio;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This class implements custom exception for the player.
 * 
 * @author Besmir Beqiri
 */
public class PlayerException extends Exception {
    
    private Throwable cause = null;
    
    public PlayerException() {
        super();
    }
    
    public PlayerException(String msg) {
        super(msg);
    }
    
    public PlayerException(Throwable cause) {
        super();
        this.cause = cause;
    }
    
    public PlayerException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return cause;
    }
    
    @Override
    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        } else if (cause != null) {
            return cause.toString();
        } else {
            return null;
        }
    }
    
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }
    
    @Override
    public void printStackTrace(PrintStream out) {
        synchronized (out) {
            PrintWriter pw = new PrintWriter(out, false);
            printStackTrace(pw);
            pw.flush();
        }
    }
    
    @Override
    public void printStackTrace(PrintWriter out) {
        if (cause != null) cause.printStackTrace(out);
    }
}
