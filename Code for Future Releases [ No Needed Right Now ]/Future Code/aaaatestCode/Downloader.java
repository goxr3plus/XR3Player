package aaaatestCode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import tools.InfoTool;

public class Downloader {

    private static class ProgressListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    // e.getSource() gives you the object of
	    // DownloadCountingOutputStream
	    // because you set it in the overriden method, afterWrite().
	    System.out.println("Downloaded bytes : " + ((DownloadProgressListener) e.getSource()).getByteCount());
	}
    }

    /**
     * Main Method
     * 
     * @param args
     */
    public static void main(String[] args) {

	System.out.println("Extension is :"
		+ InfoTool.getFileExtension("http://i12.photobucket.com/albums/a206/zxc6/1_zps3e6rjofn.jpg"));

	String result = null;
	try {
	    result = java.net.URLDecoder.decode("https://t2.kn3.net/taringa/1/6/4/4/2/2/STEELMAX/176x132_22A.jpg",
		    "UTF-8");
	    System.out.println(result);
	} catch (UnsupportedEncodingException ex) {
	    // TODO Auto-generated catch block
	    ex.printStackTrace();
	}

	URL dl = null;
	File fl = null;
	String x = null;
	OutputStream os = null;
	InputStream is = null;
	ProgressListener progressListener = new ProgressListener();
	try {
	    fl = new File(System.getProperty("user.home").replace("\\", "/") + "/Desktop/image.zip");
	    dl = new URL(java.net.URLDecoder
		    .decode("https://sourceforge.net/projects/xr3player/files/XR3Player%20Update%2043.zip/download", "UTF-8"));
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
	    System.out.println("Content Length:" + connection.getHeaderField("Content-Length"));
	    System.out.println("Content Length with different way:" + connection.getContentType());

	    System.out.println("\n");

	    // begin transfer by writing to dcount, not os.
	    IOUtils.copy(is, dcount);

	} catch (Exception e) {
	    System.out.println(e);
	} finally {
	    IOUtils.closeQuietly(os);
	    IOUtils.closeQuietly(is);
	}
    }
}