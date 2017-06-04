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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Peter Backx
 */
public class FileSampleProcessor implements SampleProcessor {

    private Logger log = Logger.getLogger(FileSampleProcessor.class.getName());

    private BufferedWriter out;

    public FileSampleProcessor() throws FileNotFoundException, IOException {
        File output = new File("c:\\test.txt");
        out = new BufferedWriter(new FileWriter(output));
    }

    public void process(long[] sample) {
        try {
            out.append(""+sample[0]+"\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, "error writing to file: ", e);
        }
    }

    public void init(int freq, int channels) {
    }

    public void close() throws IOException {
        out.close();
    }

}
