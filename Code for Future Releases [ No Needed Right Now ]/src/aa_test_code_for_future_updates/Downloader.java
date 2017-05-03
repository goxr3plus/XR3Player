package aa_test_code_for_future_updates;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;


public class Downloader {

    static long totalBytes;

    private static class prL implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    // e.getSource() gives you the object of
	    // DownloadCountingOutputStream
	    // because you set it in the overriden method, afterWrite().
	    long currentBytes=((DownloadProgressListener) e.getSource()).getByteCount();
	    System.out.println(
		    "Downloaded bytes : " + currentBytes + " Progress="+(currentBytes*100)/totalBytes);
	}
    }

    /**
     * Main Method
     * 
     * @param args
     */
    public static void main(String[] args) {

	URL dl = null;
	File fl = null;
	String x = null;
	OutputStream os = null;
	InputStream is = null;
	prL progressListener = new prL();
	try {
	    fl = new File(System.getProperty("user.home").replace("\\", "/") + "/Desktop/XR3Player.Update.zip");
	    dl = new URL(java.net.URLDecoder.decode(
		    "https://github.com/goxr3plus/XR3Player/releases/download/V3.45/XR3Player.Update.45.zip", "UTF-8"));
	    os = new FileOutputStream(fl);
	    is = dl.openStream();

	    // http://i12.photobucket.com/albums/a206/zxc6/1_zps3e6rjofn.jpg

	    // System.out.println("Extension is
	    // :"+InfoTool.getFileExtension("http://i12.photobucket.com/albums/a206/zxc6/1_zps3e6rjofn.jpg"));

	    DownloadProgressListener dcount = new DownloadProgressListener(os);
	    dcount.setListener(progressListener);

	    URLConnection connection = dl.openConnection();

	    // this line give you the total length of source stream as a String.
	    // you may want to convert to integer and store this value to
	    // calculate percentage of the progression.
	    totalBytes = Long.valueOf(connection.getHeaderField("Content-Length"));
	    System.out.println("Content Length:" + totalBytes);
	    System.out.println("Content Length with different way:" + connection.getContentType());

	    System.out.println("\n");

	    // begin transfer by writing to dcount, not os.
	    IOUtils.copy(is, dcount);
	    
	    System.out.println("after copy");

	} catch (Exception e) {
	    System.out.println(e);
	} finally {
	    IOUtils.closeQuietly(os);
	    IOUtils.closeQuietly(is);
	}
    }
}