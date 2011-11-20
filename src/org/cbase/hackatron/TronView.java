package org.cbase.hackatron;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class TronView extends View implements Runnable{

	
	public final static byte PLAYER_COUNT=1;
	
	private final static byte PLAYER_MOVEMENT_NONE=-1;
	private final static byte PLAYER_MOVEMENT_UP=0;
	private final static byte PLAYER_MOVEMENT_DOWN=2;
	private final static byte PLAYER_MOVEMENT_LEFT=3;
	private final static byte PLAYER_MOVEMENT_RIGHT=1;
	
	
	private byte[] act_player_movement;//=PLAYER_MOVEMENT_NONE;
	
	private final static byte divider=5;

	private Point[] player_position;
	private Paint[] player_paint;
	
	private int[][] tron_buff;
	
	private int buff_width=0;
	private int buff_height=0;
	
	private boolean running =true;
		
	public TronView(Context context) {
		super(context);
		
		tron_buff=new int[buff_width][buff_height];
		
		player_position=new Point[PLAYER_COUNT];
		player_position[0]=new Point(0,0);
		
		
		player_paint=new Paint[PLAYER_COUNT];
		
		player_paint[0]=new Paint();
		player_paint[0].setColor(Color.RED);
		
		act_player_movement=new byte[PLAYER_COUNT];
		
		act_player_movement[0]=PLAYER_MOVEMENT_RIGHT;
		
		new Thread(this).start();
		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		buff_width=w/divider;
		buff_height=h/divider;

		tron_buff=new int[buff_width][buff_height];

			
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void init() {
		for(int x=0;x<buff_width;x++)
			for(int y=0;y<buff_height;y++)
				tron_buff[x][y]=-1;

		
		player_position[0].x=buff_width/20;
		player_position[0].y=buff_width/20;
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
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		for(int x=0;x<buff_width;x++)
			for(int y=0;y<buff_height;y++)
				if (tron_buff[x][y]!=-1)
					canvas.drawRect(new Rect(x*divider,y*divider,(x+1)*divider,(y+1)*divider),player_paint[tron_buff[x][y]]);
		
		
		
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
						
						if (tron_buff[player_position[act_player].x][player_position[act_player].y]!=-1)
							init();
						
						tron_buff[player_position[act_player].x][player_position[act_player].y]=act_player;
						
						switch (act_player_movement[act_player]) {
						case PLAYER_MOVEMENT_RIGHT:
							if (player_position[act_player].x<buff_width-1)
								player_position[act_player].x++;
							else
								init();
							break;
							
						case PLAYER_MOVEMENT_LEFT:
							if (player_position[act_player].x>0)
								player_position[act_player].x--;
							else
								init();
							break;
							

						case PLAYER_MOVEMENT_DOWN:
							if (player_position[act_player].y<buff_height-1)
								player_position[act_player].y++;
							else
								init();
							break;

						case PLAYER_MOVEMENT_UP:
							if (player_position[act_player].y>0)
								player_position[act_player].y--;
							else
								init();
							break;
						}
					}
						
							
				Thread.sleep(20);
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	

}
