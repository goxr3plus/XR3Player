package com.goxr3plus.xr3player.controllers.dropbox;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Used to track progress of OutputStream User
 * 
 * @author GOXR3PLUS
 *
 */
public class ProgressOutputStream extends OutputStream {

	private OutputStream underlying;
	private Listener listener;
	private int completed;
	private long totalSize;

	public ProgressOutputStream(OutputStream underlying, long totalSize, Listener listener) {
		this.underlying = underlying;
		this.listener = listener;
		this.completed = 0;
		this.totalSize = totalSize;
	}

	@Override
	public void write(byte[] data, int off, int len) throws IOException {
		this.underlying.write(data, off, len);
		track(len);
	}

	@Override
	public void write(byte[] data) throws IOException {
		this.underlying.write(data);
		track(data.length);
	}

	@Override
	public void write(int c) {
		try {
			this.underlying.write(c);
			track(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void track(int len) {
		this.completed += len;
		this.listener.progress(this.completed, this.totalSize);
	}

	/**
	 * The total File size
	 * 
	 * @return the totalSize The total File size
	 */
	public long getTotalSize() {
		return totalSize;
	}

	public interface Listener {
		public void progress(long completed, long totalSize);
	}
}
