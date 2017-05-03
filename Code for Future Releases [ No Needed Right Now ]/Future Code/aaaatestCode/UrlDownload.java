package aaaatestCode;

import java.io.*;
import java.net.*;

import org.apache.commons.io.IOUtils;

public class UrlDownload { 
    final static int size = 1024 * 4;

    public static void fileUrl(String fAddress, String localFileName, String destinationDir) {
	OutputStream outStream = null;
	URLConnection uCon = null;

	InputStream is = null;
	try {
	    URL Url;
	    byte[] buf;
	    int ByteRead, ByteWritten = 0;
	    Url = new URL(fAddress);
	    new File(destinationDir).mkdir();
	    new File(destinationDir + "\\" + localFileName).createNewFile();
	    outStream = new BufferedOutputStream(new FileOutputStream(destinationDir + "\\" + localFileName));


            IOUtils.copy(is, outStream);
	    
//	    uCon = Url.openConnection();
//	    is = uCon.getInputStream();
//	    buf = new byte[size];
//	    while ((ByteRead = is.read(buf)) != -1) {
//		outStream.write(buf, 0, ByteRead);
//		ByteWritten += ByteRead;
//		System.out.println("Running....");
//	    }
	    System.out.println("Downloaded Successfully.");
	    System.out.println("File name:\"" + localFileName + "\"\nNo ofbytes :" + ByteWritten);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		is.close();
		outStream.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public static void fileDownload(String fAddress, String destinationDir) {

	int slashIndex = fAddress.lastIndexOf('/');
	int periodIndex = fAddress.lastIndexOf('.');

	String fileName = fAddress.substring(slashIndex + 1);

	if (periodIndex >= 1 && slashIndex >= 0 && slashIndex < fAddress.length() - 1) {
	    fileUrl(fAddress, fileName, destinationDir);
	} else {
	    System.err.println("path or file name.");
	}
    }

    public static void main(String[] args) {

	fileDownload("https://sourceforge.net/projects/xr3player/files/XR3Player Update 43.zip","C:/Download");
	
	// if (args.length == 2) {
	// for (int i = 1; i < args.length; i++) {
	// fileDownload(args[i], args[0]);
	// }
	// } else {
	//
	// }
    }
}