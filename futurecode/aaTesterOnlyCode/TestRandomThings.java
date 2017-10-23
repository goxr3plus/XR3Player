package aaTesterOnlyCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;

public class TestRandomThings {
	
	public static void main(String[] args) {
		
		Path path = Paths.get("C:\\Users\\GOXR3PLUS\\Desktop\\FOG");
		BasicFileAttributes attr;
		try {
			attr = Files.readAttributes(path, BasicFileAttributes.class);
			
			FileTime s = attr.creationTime();
			
			System.out.println("Creation date: " + attr.creationTime());
			System.out.println("Creation date in  DD-MM-YY Format " + new SimpleDateFormat("dd/MM/yyyy").format(s.toMillis()));
			System.out.println("Creation date HH:MM:SS Format " + new SimpleDateFormat("HH:mm:ss").format(s.toMillis()));
			
		} catch (IOException e) {
			System.out.println("oops error! " + e.getMessage());
		}
	}
}
