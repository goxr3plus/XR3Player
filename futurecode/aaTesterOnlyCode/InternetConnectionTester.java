/**
 * 
 */
package aaTesterOnlyCode;

import application.tools.InfoTool;

/**
 * @author GOXR3PLUS
 *
 */
public class InternetConnectionTester {

    /**
     * 
     */
    public InternetConnectionTester() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	Thread thread = new Thread(()->{
	    while(true) {
		
		try {
		    Thread.sleep(50);
		} catch (InterruptedException ex) {
		    // TODO Auto-generated catch block
		    ex.printStackTrace();
		}
		System.out.println("Internet Connection is available : "+ InfoTool.isReachableUsingSocket("www.google.com",80));
	    }
	});
	
	thread.setDaemon(false);
	thread.start();

    }

}
