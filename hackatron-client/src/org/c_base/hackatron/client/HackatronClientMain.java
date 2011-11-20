package org.c_base.hackatron.client;

import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class HackatronClientMain extends Activity implements OnTouchListener {

	private static final String TAG = "client";
	
	public static final String INTENT_EXTRA_USERNAME = "INTENT_EXTRA_USERNAME";
	public static final String INTENT_EXTRA_SERVERADDRESS = "INTENT_EXTRA_SERVERADDRESS";

	public static final int PORT = 4225;

	private String userName;
	private String serverAddress;

	private class ControlTask extends AsyncTask<Void, String, Void> {

		private Socket socket;
		private PrintWriter writer;

		private volatile int nextCommand = -1;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				socket = new Socket(serverAddress, PORT);
				Log.d(TAG, "connection established to " + socket.getRemoteSocketAddress());
				writer = new PrintWriter(socket.getOutputStream());
				writer.write("join " + userName + "\n");
				writer.flush();

				while (!isCancelled()) {
					synchronized (ControlTask.this) {
						try {
							ControlTask.this.wait();
							if (nextCommand != -1) {
								int command = nextCommand;
								nextCommand = -1;
								
								Log.d(TAG, "sending command: " + command);
								writer.write(command + "\n");
								writer.flush();
							}
						} catch (InterruptedException e) {
							if (isCancelled()) {
								writer.close();
								socket.close();
								break;
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				publishProgress(e.toString());
				cancel(true);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			finish();
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			finish();
		}

		public synchronized void sendCommand(int command) {
			Log.d(TAG, "sendCommand("+command+")");
			nextCommand = command;
			notify();
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_LONG).show();
		}

	}

	private final ControlTask task = new ControlTask();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
		findViewById(R.id.left).setOnTouchListener(this);
		findViewById(R.id.right).setOnTouchListener(this);
		
		
		Intent intent = getIntent();
		if (intent == null) {
			Toast.makeText(getApplicationContext(), "No Intent data.", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			userName = intent.getStringExtra(INTENT_EXTRA_USERNAME);
			serverAddress = intent.getStringExtra(INTENT_EXTRA_SERVERADDRESS);

			if (userName.contains("\n") || userName.contains("\r")) {
				Toast.makeText(getApplicationContext(), "Invalid username.", Toast.LENGTH_SHORT).show();
				finish();
			}

			task.execute();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// disconnect and finish
		task.cancel(true);
	}

	public void onClickReady(View v) {
		findViewById(R.id.ready).setVisibility(View.GONE);
		findViewById(R.id.left).setVisibility(View.VISIBLE);
		findViewById(R.id.right).setVisibility(View.VISIBLE);
		task.sendCommand(3);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			final int id = v.getId();
			if (id == R.id.left) {
				task.sendCommand(1);
			} else if (id == R.id.right) {
				task.sendCommand(2);
			}
		}
		
		return false;
	}

}
