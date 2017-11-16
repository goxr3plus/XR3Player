package main.java.com.goxr3plus.xr3player.remote.dropbox.io;

import java.io.IOException;
import java.io.InputStream;

/** Used to track progress of InputStream , though it doesn't work correctly
 * @author GOXR3PLUS
 *
 */
@Deprecated
public class ProgressInputStream extends InputStream {
	
	private InputStream wrappedInputStream;
	private long size;
	private long counter;
	private Listener listener;
	
	public ProgressInputStream(InputStream in, long size, Listener listener) {
		wrappedInputStream = in;
		this.size = size;
		this.listener = listener;
	}
	
	@Override
	public int read() throws IOException {
		int retVal = wrappedInputStream.read();
		counter += 1;
		check(counter);
		return retVal;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		int retVal = wrappedInputStream.read(b);
		counter += retVal;
		check(counter);
		return retVal;
	}
	
	@Override
	public int read(byte[] b , int offset , int length) throws IOException {
		int retVal = wrappedInputStream.read(b, offset, length);
		counter += retVal;
		check(retVal);
		return retVal;
	}
	
	private void check(long counter2) {
		if (counter2 != -1) {
			listener.uploadProgress(counter, size);
		}
	}
	
	/**
	 * Interface for classes that want to monitor this input stream
	 */
	public interface Listener {
		void uploadProgress(long completed , long totalSize);
	}
}
