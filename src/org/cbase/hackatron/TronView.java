package org.cbase.hackatron;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class TronView extends View implements Runnable {

	private static final int[] COLORS = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW };
	private static final byte PLAYER_COUNT=4;

	private static int[][] CONTROLS = {
			{KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_CENTER},
			{KeyEvent.KEYCODE_X, KeyEvent.KEYCODE_V, KeyEvent.KEYCODE_C},
			{KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_E},
			{KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_O},
			};

	private Player[] players;

	private int buff_width=0;
	private int buff_height=0;
	private int[][] tron_buff;
	private Bitmap center_logo;
	private final static byte divider=5;

	private boolean running =true;

	private Paint mPlayerPaint;
	
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    return inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	    //    Log.e(LOG_TAG, ex.toString());
	    }
	    return null;
	}
	
	public TronView(Context context) {
		super(context);

		tron_buff=new int[buff_width][buff_height];

		players = new Player[PLAYER_COUNT];
		for (int i = 0; i < players.length; i++) {
			players[i] = new Player();
			players[i].setColor(COLORS[i]);
		}

		new Thread(this).start();
		
		mPlayerPaint=new Paint();
	}

	public static Bitmap relative2View(View view,Bitmap orig, float x_scale_, float y_scale_) {
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();

		float x_scale, y_scale;
		if (y_scale_ != 0f)
			// take the given y scale
			y_scale = (view.getHeight() * y_scale_) / orig.getHeight();
		else
			// take x_scale
			y_scale = (view.getWidth() * x_scale_) / orig.getWidth();

		if (x_scale_ != 0f)
			// take the given x scale
			x_scale = (view.getWidth() * x_scale_) / orig.getWidth();
		else
			// take the given y scale
			x_scale = (view.getHeight() * y_scale_) / orig.getHeight();

		matrix.postScale(x_scale, y_scale);

		return Bitmap.createBitmap(orig, 0, 0, orig.getWidth(),orig.getHeight(), matrix, true);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		center_logo=relative2View(this,BitmapFactory.decodeResource(this.getResources(),R.drawable.logo),0.7f,0.0f);
		buff_width=w/divider;
		buff_height=h/divider;

		tron_buff=new int[buff_width][buff_height];

		init();

		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void kill_player(int player) {
		Log.i("hackatron","killing player" + player);
		for(int x=0;x<buff_width;x++)
			for(int y=0;y<buff_height;y++)
				if (tron_buff[x][y]==player)
					tron_buff[x][y]=-1;

		init_player(player) ;
	}

	private void init_player(int player) {
		if (player < PLAYER_COUNT && player > -1) {
			Player p = getPlayer(player);
			switch (player) {

			case 0:
				p.setMovement(Movement.RIGHT);
				p.setPosition(buff_width / 20,  buff_height / 20);
				break;

			case 1:
				p.setMovement(Movement.DOWN);
				p.setPosition(buff_width - buff_width / 20, buff_height / 20);
				break;

			case 2:
				p.setMovement(Movement.UP);
				p.setPosition( buff_width / 20, buff_height - buff_height / 20);
				break;

			case 3:
				p.setMovement(Movement.LEFT);
				p.setPosition(buff_width - buff_width / 20, buff_height - buff_height / 20);

				break;
			}
		}
	}

	public Player getPlayer(int player) {
		return players[player];
	}

	private void init() {
		for(int x=0;x<buff_width;x++)
			for(int y=0;y<buff_height;y++)
				tron_buff[x][y]=-1;

		for (int act_player=0;act_player<PLAYER_COUNT;act_player++)
			init_player(act_player);
	}
	
	
	public void player_right(int player) {
		getPlayer(player).turnRight();
	}
	
	public void player_left(int player) {
		getPlayer(player).turnLeft();
	}

	public void player_start(int player) {
		getPlayer(player).setActive(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("hackatron","key event");
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			int code = event.getKeyCode();
			for (int p = 0; p < CONTROLS.length; p++) {
				for (int keycode = 0; keycode < CONTROLS[p].length; keycode++) {
					if (code == CONTROLS[p][keycode]) {
						Player player = getPlayer(p);
						switch (keycode) {
							case 0: player.turnRight();break;
							case 1: player.turnLeft();break;
							case 2: player.setActive(true);break;
						}
					}
				}
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);

		mPlayerPaint.setColor(Color.RED);
		
		mPlayerPaint.setTextAlign(Align.LEFT);
		mPlayerPaint.setTextSize(this.getHeight()/30);
		
		canvas.drawText(getPlayer(0).getName(), 100, 100, mPlayerPaint);
		
		mPlayerPaint.setTextAlign(Align.RIGHT);
		mPlayerPaint.setColor(Color.YELLOW);
		
		canvas.drawText(getPlayer(1).getName(), this.getWidth()-100,100, mPlayerPaint);
		
		
		
		canvas.drawBitmap(center_logo, (this.getWidth() - center_logo.getWidth()) / 2, (this.getHeight() - center_logo.getHeight()) / 2, new Paint());
		
		Paint ip_text_paint=new Paint();
		ip_text_paint.setColor(Color.WHITE);
		canvas.drawText("connect to"+this.getLocalIpAddress(), (this.getWidth() - center_logo.getWidth()) / 2, (this.getHeight() - 2*center_logo.getHeight()) / 2, ip_text_paint);
		
		for(int x=0;x<buff_width;x++)
		
			for(int y=0;y<buff_height;y++) {
				int val=tron_buff[x][y];
				if (val!=-1)
					canvas.drawRect(new Rect(x*divider,y*divider,(x+1)*divider,(y+1)*divider),getPlayer(val).getPaint());

			}

		super.onDraw(canvas);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {	}

		this.invalidate();
	}

	@Override
	public void run() {
		while (running) {
			try {
				if (buff_width!=0)
					for (int act_player=0;act_player<PLAYER_COUNT;act_player++) {
						Player player = getPlayer(act_player);

						if (player.isActive()){
							player.getPoint();

							int x = player.getPoint().x;
							int y = player.getPoint().y;
						if (tron_buff[x][y]!=-1)
							kill_player(act_player);

						tron_buff[x][y]=act_player;

						switch (player.getMovement()) {
						case RIGHT:
							if (x<buff_width-1)
									player.moveX(1);
								else
									kill_player(act_player);
								break;

						case LEFT:
							if (x>0)
								player.moveX(-1);
							else
								kill_player(act_player);
							break;


						case DOWN:
							if (y<buff_height-1)
								player.moveY(1);
							else
								kill_player(act_player);
							break;

						case UP:
							if (y>0)
								player.moveY(-1);
							else
								kill_player(act_player);
							break;
						}
					}
					}

				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
	}


}