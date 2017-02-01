package aaaatestCode;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRedirectExample {

  public static void main(String[] args) {

    try {

	String url = "https://images.duckduckgo.com/iu/?u=http%3A%2F%2Fimages2.fanpop.com%2Fimage%2Fphotos%2F8900000%2FFirefox-firefox-8967915-1600-1200.jpg&f=1";

	URL obj = new URL(url);
	HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
	conn.setReadTimeout(5000);
	conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
	conn.addRequestProperty("User-Agent", "Mozilla");
	conn.addRequestProperty("Referer", "google.com");

	System.out.println("Request URL ... " + url);

	boolean redirect = false;

	// normally, 3xx is redirect
	int status = conn.getResponseCode();
	if (status != HttpURLConnection.HTTP_OK) {
		if (status == HttpURLConnection.HTTP_MOVED_TEMP
			|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER)
		redirect = true;
	}

	System.out.println("Response Code ... " + status);

	if (redirect) {

		// get redirect url from "location" header field
		String newUrl = conn.getHeaderField("Location");

		// get the cookie if need, for login
		String cookies = conn.getHeaderField("Set-Cookie");

		// open the new connnection again
		conn = (HttpURLConnection) new URL(newUrl).openConnection();
		conn.setRequestProperty("Cookie", cookies);
		conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		conn.addRequestProperty("User-Agent", "Mozilla");
		conn.addRequestProperty("Referer", "google.com");

		System.out.println("Redirect to URL : " + newUrl);

	}

	BufferedReader in = new BufferedReader(
                              new InputStreamReader(conn.getInputStream()));
	String inputLine;
	StringBuffer html = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
		html.append(inputLine);
	}
	in.close();
	
	System.out.println("Writting to file....");
	File file = new File(System.getProperty("user.home").replace("\\", "/") + "/Desktop/image.jpg");
	file.createNewFile();
	
	try(  PrintWriter out = new PrintWriter( file )  ){
	    out.println( html.toString() );
	    out.flush();
	}
	
	//System.out.println("URL Content... \n" + html.toString());
	System.out.println("Done");

	
    } catch (Exception e) {
	e.printStackTrace();
    }

  }

}