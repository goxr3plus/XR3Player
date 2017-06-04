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

import java.util.LinkedList;
import java.util.Queue;
import javazoom.jl.decoder.JavaLayerException;

/**
 *
 * @author pbackx
 */
public class EnergyOutputAudioDevice extends BaseOutputAudioDevice {
    private int averageLength = 1024; // number of samples over which the average is calculated
    private Queue<Short> instantBuffer = new LinkedList<Short>();

    public EnergyOutputAudioDevice(SampleProcessor processor) {
        super(processor);
    }

    @Override
    protected void outputImpl(short[] samples, int offs, int len) throws JavaLayerException {
        for(int i=0; i<len; i++)
            instantBuffer.offer(samples[i]);

        while(instantBuffer.size()>averageLength*channels)
        {
            long energy = 0;
            for(int i=0; i<averageLength*channels; i++)
                energy += Math.pow(instantBuffer.poll(), 2);

            if(processor != null)
                processor.process(new long[] { energy });
        }
    }

    public int getAverageLength() {
        return averageLength;
    }

    public void setAverageLength(int averageLength) {
        this.averageLength = averageLength;
    }
}
