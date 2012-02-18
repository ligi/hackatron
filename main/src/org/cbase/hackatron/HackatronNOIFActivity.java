package org.cbase.hackatron;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class HackatronNOIFActivity extends HackatronActivity {

	public final static int PAUSE_TIME=3000000; // in ms
	private ProgressBar progress;
	private long pause_start;
	private Handler hndl;
	
	public int getLayout() {
		return R.layout.noif;
	}
	
	

    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        progress=(ProgressBar)findViewById(R.id.progress_bar);
        progress.setMax(PAUSE_TIME);
        
        pause_start=System.currentTimeMillis();
        
        hndl=new Handler();
        
        new Thread(new ProgressUpdaterThread()).start();
	}


	public void done() {
    	finish();
    }
    
    public class ProgressUpdaterThread implements Runnable {
		
		@Override
		public void run() {
			while ((System.currentTimeMillis()-pause_start)<PAUSE_TIME) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {		}
				
				hndl.post(new ProgressUpdater());
			}	
			done();
		}
	}

    public class ProgressUpdater implements Runnable {
		@Override
		public void run() {
			progress.setProgress(progress.getMax()-(int)(System.currentTimeMillis()-pause_start));
		}
	}
}
