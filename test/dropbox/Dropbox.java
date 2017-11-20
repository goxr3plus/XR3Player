package dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderContinueErrorException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadUploader;
import com.dropbox.core.v2.users.FullAccount;

import main.java.com.goxr3plus.xr3player.application.tools.InfoTool;
import main.java.com.goxr3plus.xr3player.remote.dropbox.io.ProgressInputStream;
import main.java.com.goxr3plus.xr3player.remote.dropbox.io.ProgressOutputStream;

/**
 * This class controls Dropbox connections with XR3Player
 * 
 * @author GOXR3PLUS
 *
 */
public class Dropbox {
	
	private static String ACCESS_TOKEN;
	
	public static void main(String args[]) throws DbxException , IOException {
		
		// Create Dropbox client
		DbxRequestConfig config = new DbxRequestConfig("XR3Player");
		
		//Get access token from dropbox-app-tokens.json		
		ACCESS_TOKEN = new String(Files.readAllBytes(Paths.get("dropbox-app-tokens.json"))).split("\"access_token\" : ")[1].replace("\"", "").replace("}", "").trim();
		
		//Create the Client
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
		//System.out.println(client.files().getTemporaryLink("/musica/troyboi - medusa dayz.mp3").getLink());
		
		// Get current account info
		FullAccount account = client.users().getCurrentAccount();
		System.out.println("User Name: " + account.getName().getDisplayName());
		
		String path = "";
		TreeMap<String,Metadata> children = new TreeMap<>();
		ListFolderResult result = client.files().listFolder("");
		List<Metadata> list = result.getEntries();
		ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("/"));
		
		//Try to upload file on DropBox
		
		//List all current User Account Files
		listAllFiles(client, path, children, arrayList);
		
	}
	
	public static void listAllFiles(DbxClientV2 client , String path , SortedMap<String,Metadata> children , List<String> arrayList) {
		try {
			ListFolderResult result = null;
			try {
				result = client.files().listFolder(path);
			} catch (ListFolderErrorException ex) {
				ex.printStackTrace();
			}
			
			while (true) {
				int i = 0;
				for (Metadata md : result.getEntries()) {
					if (md instanceof DeletedMetadata) { //Deleted
						children.remove(md.getPathLower());
					} else if (md instanceof FolderMetadata) { //Folder
						String folder = md.getPathLower();
						String parent = new File(md.getPathLower()).getParent();
						children.put(folder, md);
						
						boolean subFileOfCurrentFolder = path.equals(parent.replace("\\", "/"));
						System.out.println( ( subFileOfCurrentFolder ? "" : "\n" ) + "Folder ->" + folder);
						
						//Call for this folder
						listAllFiles(client, folder, children, arrayList);
						
						//Add folder to arraylist
						arrayList.add(folder);
					} else if (md instanceof FileMetadata) { //File
						String file = md.getPathLower();
						String parent = new File(md.getPathLower()).getParent();
						children.put(file, md);
						
						boolean subFileOfCurrentFolder = path.equals(parent.replace("\\", "/"));
						System.out.println( ( subFileOfCurrentFolder ? "" : "\n" ) + "File->" + file + " Media Info: " + InfoTool.isAudioSupported(file));
						//System.out.println("1." + path + " 2." + parent.replace("\\", "/")+"=Equals ? "+subFileOfCurrentFolder);
						
						//Try to download the File
						//downloadFile(client, file, "downloads/" + md.getName());
						
						//Add file to array
						arrayList.add(file);
					}
					i++;
				}
				
				if (!result.getHasMore())
					break;
				
				try {
					result = client.files().listFolderContinue(result.getCursor());
					System.out.println("Entered result next");
				} catch (ListFolderContinueErrorException ex) {
					ex.printStackTrace();
				}
			}
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Download Dropbox File to Local Computer
	 * 
	 * @param client
	 *            Current connected client
	 * @param dropBoxFilePath
	 *            The file path on the Dropbox cloud server -> [/foldername/something.txt]
	 * @param localFileAbsolutePath
	 *            The absolute file path of the File on the Local File System
	 * @throws DbxException
	 * @throws DownloadErrorException
	 * @throws IOException
	 */
	public static void downloadFile(DbxClientV2 client , String dropBoxFilePath , String localFileAbsolutePath) throws DownloadErrorException , DbxException , IOException {
		//Create DbxDownloader
		DbxDownloader<FileMetadata> dl = client.files().download(dropBoxFilePath);
		
		//FileOutputStream
		FileOutputStream fOut = new FileOutputStream(localFileAbsolutePath);
		System.out.println("Downloading .... " + dropBoxFilePath);
		
		//Add a progress Listener
		dl.download(new ProgressOutputStream(fOut, dl.getResult().getSize(), (long completed , long totalSize) -> {
			System.out.println( ( completed * 100 ) / totalSize + " %");
		}));
		
		//Fast way...
		//client.files().downloadBuilder(file).download(new FileOutputStream("downloads/" + md.getName()))
		
	}
	
	/**
	 * Upload Local File to Dropbox
	 * 
	 * @param client
	 *            Current connected client
	 * @param dropBoxFilePath
	 *            The file path on the Dropbox cloud server -> [/foldername/something.txt]
	 * @param localFileAbsolutePath
	 *            The absolute file path of the File on the Local File System
	 * @throws DbxException
	 * @throws IOException
	 */
	public static void uploadFile(DbxClientV2 client , String dropBoxFilePath , String localFileAbsolutePath) throws DbxException , IOException {
		//Create UploadUploader
		UploadUploader uploader = client.files().upload(dropBoxFilePath);
		
		long size = 0;
		FileInputStream fileInput;
		
		fileInput = new FileInputStream(localFileAbsolutePath);
		size = fileInput.getChannel().size();
		
		System.out.println("Size : " + InfoTool.getFileSizeEdited(size));
		
		//Try to upload the File
		uploader.uploadAndFinish(new ProgressInputStream(fileInput, size, (long completed , long totalSize) -> {
			System.out.println( ( completed * 100 ) / totalSize + " %");
		}));
		
	}
}
