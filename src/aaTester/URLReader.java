package aaTester;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

public class URLReader {
	public static void main(String[] args) throws Exception {
		
		//System.out.println(Cipher.getMaxAllowedKeyLength("AES"));
		
		//Create HttpURLConnection 
		
		//https://sourceforge.net/projects/xr3player/files/stats/json?start_date=2015-10-29&end_date=2307-11-04
		
		HttpURLConnection httpcon = (HttpURLConnection) new URL("https://api.github.com/repos/goxr3plus/XR3Player/releases").openConnection();
		httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
		
		//Read line by line
		String line = "" , inputLine;
		while ( ( inputLine = in.readLine() ) != null) {
			line += "\n" + inputLine;
			//System.out.println(inputLine);
		}
		in.close();
		
		//Get SourceForge Downloads 
		//URL: "https://img.shields.io/sourceforge/dt/xr3player.svg"
		
		//System.out.println(line.split("<text x=\"98.5\" y=\"14\">")[1].split("/total")[0]);
		
		//Get Git Hub Downloads of XR3Player
		
		//URL: https://api.github.com/repos/goxr3plus/XR3Player/releases
		
				Arrays.stream(line.split("\"download_count\":")).skip(1).map(l -> l.split(",")[0]).forEach(l -> System.out.println(l));
		//		
		//		int total = Arrays.stream(line.split("\"download_count\":")).skip(1).mapToInt(l -> Integer.parseInt(l.split(",")[0])).sum();
		//		
		//		System.out.println("\nTotal Downloads: " + total);
		
	}
	
}
