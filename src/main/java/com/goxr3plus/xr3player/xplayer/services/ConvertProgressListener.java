package main.java.com.goxr3plus.xr3player.xplayer.services;

import it.sauronsoftware.jave.EncoderProgressListener;
import it.sauronsoftware.jave.MultimediaInfo;

public class ConvertProgressListener implements EncoderProgressListener {
	int current = 1;
	
	public ConvertProgressListener() {
	}
	
	public void message(String m) {
		//      if ((ConverterFrame.this.inputfiles.length > 1) && 
		//        (this.current < ConverterFrame.this.inputfiles.length)) {
		//        ConverterFrame.this.encodingMessageLabel.setText(this.current + "/" + ConverterFrame.this.inputfiles.length);
		//      }
	}
	
	public void progress(int p) {
		
		      double progress = p / 1000.00;
		      System.out.println(progress);
		//      ConverterFrame.this.encodingProgressLabel.setText(progress + "%");
		//      if (p >= 1000) {
		//        if (ConverterFrame.this.inputfiles.length > 1)
		//        {
		//          this.current += 1;
		//          if (this.current > ConverterFrame.this.inputfiles.length)
		//          {
		//            ConverterFrame.this.encodingMessageLabel.setText("Encoding Complete!");
		//            ConverterFrame.this.convertButton.setEnabled(true);
		//          }
		//        }
		//        else if (p == 1001)
		//        {
		//          ConverterFrame.this.encodingMessageLabel.setText("Encoding Failed!");
		//          ConverterFrame.this.convertButton.setEnabled(true);
		//        }
		//        else
		//        {
		//          ConverterFrame.this.encodingMessageLabel.setText("Encoding Complete!");
		//          ConverterFrame.this.convertButton.setEnabled(true);
		//        }
	}
	
	public void sourceInfo(MultimediaInfo m) {
	}
}
