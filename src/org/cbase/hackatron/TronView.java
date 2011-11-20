package org.cbase.hackatron;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class TronView extends View implements Runnable{


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

	private Bitmap center_logo;

	public final static byte PLAYER_COUNT=4;

	private final static byte divider=5;

	private int[][] tron_buff;

	private int buff_width=0;
	private int buff_height=0;

	private boolean running =true;

	private Paint logo_paint=new Paint();

	private int[] player_colors = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW };
	private Player[] players;

	public TronView(Context context) {
		super(context);

		tron_buff=new int[buff_width][buff_height];

		players = new Player[PLAYER_COUNT];
		for (int i = 0; i < players.length; i++) {
			players[i] = new Player();
			players[i].setColor(player_colors[i]);
		}

		new Thread(this).start();
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

	private Player getPlayer(int player) {
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

	private void player_active(int player) {
		getPlayer(player).setActive(true);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("hackatron","key event");
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				player_right(0); 
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				player_left(0);
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				player_active(0);
				break;

			case KeyEvent.KEYCODE_X:
				player_right(1);
				break;

			case KeyEvent.KEYCODE_V:
				player_left(1);
				break;

			case KeyEvent.KEYCODE_C:
				player_active(1);
				break;

			case KeyEvent.KEYCODE_W:
				player_right(2);
				break;
			case KeyEvent.KEYCODE_R:
				player_left(2);
				break;
			case KeyEvent.KEYCODE_E:
				player_active(2);
				break;

			case KeyEvent.KEYCODE_I:
				player_right(3);
				break;

			case KeyEvent.KEYCODE_P:
				player_left(3);
				break;

			case KeyEvent.KEYCODE_O:
				player_active(3);
				break;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);


		canvas.drawBitmap(center_logo, (this.getWidth() - center_logo.getWidth()) / 2, (this.getHeight() - center_logo.getHeight()) / 2, new Paint());
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
