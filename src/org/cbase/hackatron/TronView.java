package org.cbase.hackatron;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
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
	
	private final static byte PLAYER_MOVEMENT_NONE=-1;
	private final static byte PLAYER_MOVEMENT_UP=0;
	private final static byte PLAYER_MOVEMENT_DOWN=2;
	private final static byte PLAYER_MOVEMENT_LEFT=3;
	private final static byte PLAYER_MOVEMENT_RIGHT=1;
	
	
	private byte[] act_player_movement;//=PLAYER_MOVEMENT_NONE;
	
	private final static byte divider=5;

	private Point[] player_position;
	private Paint[] player_paint;
	
	private boolean[] player_active;
	
	private int[][] tron_buff;
	
	private int buff_width=0;
	private int buff_height=0;
	
	private boolean running =true;
		
	private Paint logo_paint=new Paint();
	
	public TronView(Context context) {
		super(context);
		
		tron_buff=new int[buff_width][buff_height];
		
		player_position=new Point[PLAYER_COUNT];
		player_paint=new Paint[PLAYER_COUNT];
		player_active=new boolean[PLAYER_COUNT];
		
		player_position[1]=new Point(0,0);
		
		for (int act_player=0;act_player<PLAYER_COUNT;act_player++) {
			player_position[act_player]=new Point(0,0);
			player_paint[act_player]=new Paint();
			player_active[act_player]=false;
		}

		player_paint[0].setColor(Color.RED);
		player_paint[1].setColor(Color.GREEN);
		player_paint[2].setColor(Color.BLUE);
		player_paint[3].setColor(Color.YELLOW);

		
		act_player_movement=new byte[PLAYER_COUNT];
		
	
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
		switch (player) {
		case 0:
			act_player_movement[0]=PLAYER_MOVEMENT_RIGHT;	

			player_position[0].x=buff_width/20;
			player_position[0].y=buff_height/20;
			break;
			
		case 1:
			act_player_movement[1]=PLAYER_MOVEMENT_DOWN;

			player_position[1].x=buff_width-buff_width/20;
			player_position[1].y=buff_height/20;

			break;

		case 2:
			act_player_movement[2]=PLAYER_MOVEMENT_UP;

			player_position[2].x=buff_width/20;
			player_position[2].y=buff_height-buff_height/20;

			break;

		case 3:
			act_player_movement[3]=PLAYER_MOVEMENT_LEFT;

			player_position[3].x=buff_width-buff_width/20;
			player_position[3].y=buff_height-buff_height/20;


			break;

		}
	}
	
	private void init() {
		for(int x=0;x<buff_width;x++)
			for(int y=0;y<buff_height;y++)
				tron_buff[x][y]=-1;

		for (int act_player=0;act_player<PLAYER_COUNT;act_player++) 
			init_player(act_player);


	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("hackatron","key event");
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				act_player_movement[0]=(byte)((act_player_movement[0]+1)%4);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				act_player_movement[0]=(byte)((act_player_movement[0]+4-1)%4);
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				player_active[0]=true;
				break;
			
			case KeyEvent.KEYCODE_X:
				act_player_movement[1]=(byte)((act_player_movement[1]+1)%4);
				break;

			case KeyEvent.KEYCODE_V:
				act_player_movement[1]=(byte)((act_player_movement[1]+4-1)%4);
				break;
			
			case KeyEvent.KEYCODE_C:
				player_active[1]=true;
				break;

			case KeyEvent.KEYCODE_W:
				act_player_movement[2]=(byte)((act_player_movement[2]+1)%4);
				break;

			case KeyEvent.KEYCODE_R:
				act_player_movement[2]=(byte)((act_player_movement[2]+4-1)%4);
				break;
			
			case KeyEvent.KEYCODE_E:
				player_active[2]=true;
				break;
				
			case KeyEvent.KEYCODE_I:
				act_player_movement[3]=(byte)((act_player_movement[3]+1)%4);
				break;

			case KeyEvent.KEYCODE_P:
				act_player_movement[3]=(byte)((act_player_movement[3]+4-1)%4);
				break;
			
			case KeyEvent.KEYCODE_O:
				player_active[3]=true;
				break;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		
		
		canvas.drawBitmap(center_logo, (this.getWidth()-center_logo.getWidth())/2, (this.getHeight()-center_logo.getHeight())/2, new Paint());
		for(int x=0;x<buff_width;x++)
			for(int y=0;y<buff_height;y++) {
				int val=tron_buff[x][y];
				if (val!=-1)
					canvas.drawRect(new Rect(x*divider,y*divider,(x+1)*divider,(y+1)*divider),player_paint[val]);
		
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
					for (int act_player=0;act_player<PLAYER_COUNT;act_player++)
						if (player_active[act_player]){
						
						if (tron_buff[player_position[act_player].x][player_position[act_player].y]!=-1)
							kill_player(act_player);
						
						tron_buff[player_position[act_player].x][player_position[act_player].y]=act_player;
						
						switch (act_player_movement[act_player]) {
						case PLAYER_MOVEMENT_RIGHT:
							if (player_position[act_player].x<buff_width-1)
									player_position[act_player].x++;
								else
									kill_player(act_player);
								break;
							
						case PLAYER_MOVEMENT_LEFT:
							if (player_position[act_player].x>0)
								player_position[act_player].x--;
							else
								kill_player(act_player);
							break;
							

						case PLAYER_MOVEMENT_DOWN:
							if (player_position[act_player].y<buff_height-1)
								player_position[act_player].y++;
							else
								kill_player(act_player);
							break;

						case PLAYER_MOVEMENT_UP:
							if (player_position[act_player].y>0)
								player_position[act_player].y--;
							else
								kill_player(act_player);
							break;
						}
					}
						
							
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	

}
