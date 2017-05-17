package aaTester;

import java.net.*;
import java.io.*;

public class URLReader {
	public static void main(String[] args) throws Exception {
		
		//Create HttpURLConnection 
		HttpURLConnection httpcon = (HttpURLConnection) new URL(
				"https://sourceforge.net/projects/openofficeorg.mirror/files/stats/json?start_date=2014-10-29&end_date=2014-11-04").openConnection();
		httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
		
		//Print everything
		String inputLine;
		while ( ( inputLine = in.readLine() ) != null)
			System.out.println(inputLine);
		in.close();

	}
}
