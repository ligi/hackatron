package org.cbase.hackatron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class HackatronActivity extends Activity {
    

	public static final int PORT = 4225;
	
	private class ServerTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(PORT);
				Log.d("hackatron", "socket created ");
				while (!isCancelled()) {
					final Socket s = serverSocket.accept();
					Log.d("hackatron", "client connected: " + s.getRemoteSocketAddress());
					UserTask task = new UserTask();
					task.execute(s);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (serverSocket != null) {
					try {
						serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			return null;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			finish();
		}
		
	}
	
	private class UserTask extends AsyncTask<Socket, String, Void> {

		private String username = "user";
		private boolean ready = false;
		
		@Override
		protected Void doInBackground(Socket... params) {
			Socket s = params[0];
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
				
				String line;
				while ((line = reader.readLine()) != null) {
					onProgressUpdate(line);
				}
				
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			
			final String l = values[0];
			if (l.startsWith("join ")) {
				username = l.substring(4);
				if (l.length() > 20) {
					cancel(true);
				}
				Log.d("hackatron", "got username: " + username);
				
			} else if (l.equals("1")) {
				Log.d("hackatron", username + " goes left");
				tv.player_left(0);
			} else if (l.equals("2")) {
				tv.player_right(0);
				Log.d("hackatron", username + " goes right");
			} else if (l.equals("3")) {
				ready = true;
				Log.d("hackatron", username + " is ready");
			}
		}
		
		public boolean isReady() {
			return ready;
		}
		
		public void setReady(boolean ready) {
			this.ready = ready;
		}
		
		public String getUsername() {
			return username;
		}
		
	}
	
	private final ServerTask serverTask = new ServerTask();
	
	private TronView tv;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        serverTask.execute();
        
        tv=new TronView(this);
        tv.setFocusable(true);
        setContentView(tv);
       
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		
		serverTask.cancel(true);
	}

}