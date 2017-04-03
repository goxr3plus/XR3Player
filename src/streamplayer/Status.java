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


/**
 * Status of Stream Player.
 *
 * @author GOXR3PLUS
 */
public enum Status {

    /**
     * INITIALIZING
     */
    INIT,

    /** UNKOWN STATUS. */
    NOT_SPECIFIED,

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