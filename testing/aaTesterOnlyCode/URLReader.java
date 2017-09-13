package aaTesterOnlyCode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

public class URLReader {
	public static void main(String[] args) throws Exception {
		
		//System.out.println(Cipher.getMaxAllowedKeyLength("AES"));
		
		//Create HttpURLConnection 
		
		//https://sourceforge.net/projects/xr3player/files/stats/json?start_date=2015-10-29&end_date=2307-11-04
		
		HttpURLConnection httpcon = (HttpURLConnection) new URL("https://api.github.com/repos/goxr3plus/XR3Player/releases").openConnection();
		httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
		
		//Read line by line
		String responseSB = in.lines().collect(Collectors.joining());
		in.close();
		
		//Get SourceForge Downloads 
		//URL: "https://img.shields.io/sourceforge/dt/xr3player.svg"
		
		//System.out.println(line.split("<text x=\"98.5\" y=\"14\">")[1].split("/total")[0]);
		
		//Get Git Hub Downloads of XR3Player
		
		//URL: https://api.github.com/repos/goxr3plus/XR3Player/releases
		
		//getTagName_Of_Each_Release(json).forEach(l -> System.out.println(l));
		//System.out.println("Total Releases: " + countReleases(json));
		
		//		
		//int total = getDownloadCount_Of_Each_Release(json).sum();
		//		
		//		System.out.println("\nTotal Downloads: " + total);
		
		//JSON Array [ROOT]
		JsonArray jsonRoot = (JsonArray) Jsoner.deserialize(responseSB);
		jsonRoot.forEach(item -> {
			//--
			String url = ( (JsonObject) item ).get("url").toString();
			
			//--
			String tagName = ( (JsonObject) item ).get("tag_name").toString();
			
			//--
			String[] downloads = { "" };
			( (JsonArray) ( (JsonObject) item ).get("assets") ).forEach(item2 -> downloads[0] = ( (JsonObject) item2 ).get("download_count").toString());
			
			//--
			String[] size = { "" };
			( (JsonArray) ( (JsonObject) item ).get("assets") ).forEach(item2 -> size[0] = ( (JsonObject) item2 ).get("size").toString());
			
			//--
			String[] createdAt = { "" };
			( (JsonArray) ( (JsonObject) item ).get("assets") ).forEach(item2 -> createdAt[0] = ( (JsonObject) item2 ).get("created_at").toString());
			
			//--
			String[] publicedAt = { "" };
			( (JsonArray) ( (JsonObject) item ).get("assets") ).forEach(item2 -> publicedAt[0] = ( (JsonObject) item2 ).get("created_at").toString());
			
			System.out.println(Arrays.asList(url, tagName, downloads[0], size[0], createdAt[0], publicedAt[0]));
		});
		
	}
	
	/**
	 * An List containing the tag name of each repository release
	 * 
	 * @param json
	 *            The JSON return by `https://api.github.com/repos/{username}/{repository}/releases` as a string
	 * @return An List containing the tag name of each repository release
	 */
	public static List<String> getAllReleases(String json) {
		return Arrays.stream(json.split("\"tag_name\":")).map(l -> l.split(",")[0]).collect(Collectors.toList());
	}
	
	/**
	 * Total number of repository releases
	 * 
	 * @param json
	 *            The JSON return by `https://api.github.com/repos/{username}/{repository}/releases` as a string
	 * @return Total number of repository releases
	 */
	public static long countReleases(String json) {
		return Arrays.stream(json.split("\"tag_name\":")).skip(1).map(l -> l.split(",")[0]).count();
	}
	
	/**
	 * An IntStrean containing the download count for each repository release
	 * 
	 * @param json
	 *            The JSON return by `https://api.github.com/repos/{username}/{repository}/releases` as a string
	 * @return An IntStrean containing the download count for each repository release
	 */
	public static IntStream getDownloadCount_Of_Each_Release(String json) {
		return Arrays.stream(json.split("\"download_count\":")).skip(1).map(l -> l.split(",")[0]).mapToInt(Integer::parseInt);
	}
	
	/**
	 * An IntStrean containing the download size for each repository release
	 * 
	 * @param json
	 *            The JSON return by `https://api.github.com/repos/{username}/{repository}/releases` as a string
	 * @return An IntStrean containing the download size for each repository release
	 */
	public static IntStream getSize_Of_Each_Release(String json) {
		return Arrays.stream(json.split("\"size\":")).skip(1).map(l -> l.split(",")[0]).mapToInt(Integer::parseInt);
	}
	
	/**
	 * An List containing the release date of each repository release
	 * 
	 * @param json
	 *            The JSON return by `https://api.github.com/repos/{username}/{repository}/releases` as a string
	 * @return An List containing the release date of each repository release
	 */
	public static List<String> getReleaseDate_Of_Each_Release(String json) {
		return Arrays.stream(json.split("\"published_at\":")).skip(1).map(l -> l.split(",")[0].replaceAll("\"", "")).collect(Collectors.toList());
	}
	
	/**
	 * An List containing the created date of each repository release
	 * 
	 * @param json
	 *            The JSON return by `https://api.github.com/repos/{username}/{repository}/releases` as a string
	 * @return An List containing the created date of each repository release
	 */
	public static List<String> getCreateDate_Of_Each_Release(String json) {
		return Arrays.stream(json.split("\"created_at\":")).skip(1).map(l -> l.split(",")[0].replaceAll("\"", "")).collect(Collectors.toList());
	}
	
	/**
	 * An List containing the tag name of each repository release
	 * 
	 * @param json
	 *            The JSON return by `https://api.github.com/repos/{username}/{repository}/releases` as a string
	 * @return An List containing the tag name of each repository release
	 */
	public static List<String> getTagName_Of_Each_Release(String json) {
		return Arrays.stream(json.split("\"tag_name\":")).skip(1).map(l -> l.split(",")[0].replaceAll("\"", "")).collect(Collectors.toList());
	}
}
