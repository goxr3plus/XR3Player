/**
 * 
 */
package aaTesterOnlyCode;

import com.jezhumble.javasysmon.JavaSysMon;

/**
 * @author GOXR3PLUS
 *
 */
public class MemTester {

    /**
     * 
     */
    public MemTester() {

	//JavaSysMon
	JavaSysMon sysMon = new JavaSysMon();
        System.out.println("Total: "+sysMon.physical().getTotalBytes()+" ,Free: "+sysMon.physical().getFreeBytes());
    }
    
    public static void main(String[] args) {
	new MemTester();
    }

}
