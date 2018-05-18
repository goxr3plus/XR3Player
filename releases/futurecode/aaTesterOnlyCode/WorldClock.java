package aaTesterOnlyCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WorldClock {
	
	public static final Map<String,String> countriesMap = new HashMap<>();
	
	public static void main(String[] args) {
		
		try {
			Document doc = Jsoup.connect("https://www.timeanddate.com/worldclock/full.html").get();
			
			doc.getElementsByClass("zebra fw tb-wc").get(0).getElementsByTag("tbody").get(0).getElementsByTag("tr").forEach(row -> {
				
				Elements countriesWithCapitals = row.getElementsByTag("a");
				Elements countriesCapitalsHours = row.getElementsByClass("rbi");
				
				//Let's run it for each country
				for (int i = 0; i < countriesWithCapitals.size(); i++) {
					String country = countriesWithCapitals.get(i).attr("href").split("/")[2];
					String capital = countriesWithCapitals.get(i).text();
					String dateTime = countriesCapitalsHours.get(i).text();
					System.out.println("Country : " + country + " , City : " + capital + " , Hour : " + dateTime + " ");
					
					countriesMap.put(country, dateTime);
				}
			});
			
			//Let's check our map
			System.out.println(countriesMap.size());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
