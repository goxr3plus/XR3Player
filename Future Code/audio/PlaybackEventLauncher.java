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
 *
 * @author Besmir Beqiri
 */
public class PlaybackEventLauncher extends Thread {

    private AudioPlayer source;
    private Playback state;
    private long position;
    private Map properties;
    private PlaybackListener listener;

    public PlaybackEventLauncher(AudioPlayer source, Playback state, long position, Map properties, PlaybackListener listener) {
        super();
        this.source = source;
        this.state = state;
        this.position = position;
        this.properties = properties;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (listener != null) {
            switch (state) {
                case BUFFERING:
                    listener.playbackBuffering(new PlaybackEvent(source, state, position, properties));
                    break;
                case OPENED:
                    listener.playbackOpened(new PlaybackEvent(source, state, position, properties));
                    break;
                case EOM:
                    listener.playbackEndOfMedia(new PlaybackEvent(source, state, position, properties));
                    break;
                case PLAYING:
                    listener.playbackPlaying(new PlaybackEvent(source, state, position, properties));
                    break;
                case PAUSED:
                    listener.playbackPaused(new PlaybackEvent(source, state, position, properties));
                    break;
                case STOPPED:
                    listener.playbackStopped(new PlaybackEvent(source, state, position, properties));
                    break;
            }
        }
    }
}
