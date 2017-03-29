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

import java.util.Map;

/**
 * @author Besmir Beqiri
 */
public class PlaybackEvent {

    private AudioPlayer source;
    private Playback state;
    private long position;
    private Map properties;

    public PlaybackEvent(AudioPlayer source, Playback state, long position, Map properties) {
        this.source = source;
        this.state = state;
        this.position = position;
        this.properties = properties;
    }

    public AudioPlayer getSource() {
        return source;
    }

    public void setSource(AudioPlayer source) {
        this.source = source;
    }

    public Playback getState() {
        return state;
    }

    public void setState(Playback state) {
        this.state = state;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }
}
