/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 2009 http://www.streamhead.com
 */
package aaTesterOnlyCode2;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.player.Player;

/**
 * @author Peter Backx
 */
public class BeatItMain {

    static Logger log = Logger.getLogger("BeatIt");
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
    	
        BPM2SampleProcessor processor = new BPM2SampleProcessor();
        processor.setSampleSize(1024);
        EnergyOutputAudioDevice output = new EnergyOutputAudioDevice(processor);
        output.setAverageLength(1024);
        //Player player = new Player(new FileInputStream(args[0]), output);
        //player.play();
        log.log(Level.INFO, "calculated BPM: " + processor.getBPM());
    }
}
