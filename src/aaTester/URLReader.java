package aaTester;

import java.net.*;

import javax.crypto.Cipher;

import java.io.*;
import java.lang.reflect.Field;

public class URLReader {
	public static void main(String[] args) throws Exception {
		
		System.out.println(Cipher.getMaxAllowedKeyLength("AES"));
		
		//Create HttpURLConnection 
		HttpURLConnection httpcon = (HttpURLConnection) new URL(
				"https://sourceforge.net/projects/xr3player/files/stats/json?start_date=2015-10-29&end_date=2307-11-04").openConnection();
		httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
		
		//Print everything
		String inputLine;
		while ( ( inputLine = in.readLine() ) != null)
			System.out.println(inputLine);
		in.close();
		
	}
}
