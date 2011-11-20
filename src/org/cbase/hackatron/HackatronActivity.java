package org.cbase.hackatron;

import android.app.Activity;
import android.os.Bundle;

public class HackatronActivity extends Activity {

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TronView tv=new TronView(this);
        tv.setFocusable(true);
        setContentView(tv);
       
    }
}