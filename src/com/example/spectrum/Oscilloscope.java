package com.example.spectrum;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


import android.app.Activity;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.view.View;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;



public class Oscilloscope extends Activity {
	DrawView drawView;
	
	public BluetoothSocket socket;
	public InputStream mmInputStream;
    //private OutputStream mmOutputStream;
    public int readBufferPosition = 0;
    //public static byte data[] = new byte [1024];
    public static float dataIntegers [] = new float[1000];//Global Variable for Stream of bytes from Arduino sensor 
 
    
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //Force Landscape
	    super.onCreate(savedInstanceState);
	    socket = MainActivity.socket;
	    final byte delimiter = 65;
        final byte[] readBuffer = new byte[16348];
        final Handler handler = new Handler();
	    drawView = new DrawView(this);
	    drawView.setBackgroundColor(Color.BLACK);
	    setContentView(drawView);
	    
	    try {
			mmInputStream = socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
		final Thread workerThread = new Thread(new Runnable()
	    {
			
	        public void run()
	        {
	        	
	           boolean stopWorker = false;
			while(!Thread.currentThread().isInterrupted() && !stopWorker)
	           {
				int bytesAvailable = 0;
				try {
					bytesAvailable = mmInputStream.available();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            if(bytesAvailable > 0)
	            {
	                byte[] packetBytes = new byte[bytesAvailable];
	                try {
						mmInputStream.read(packetBytes);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                for(int i=0;i<bytesAvailable;i++)
	                {
	                    byte b = packetBytes[i];
	                    if(b == delimiter)
	                    {
	                        byte[] encodedBytes = new byte[readBufferPosition];
	                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
	                        try {
								String data =  new String(encodedBytes, "US-ASCII");
								data = data.trim();
								data = data.replaceAll("\\s+", ""); 
								final String dataArray [] = data.split("z");
								float dataIntegers [] = new float[1000];
								for (int j=0; j<(dataArray.length);++j){
									/*if (Float.valueOf(dataArray[j]) != (float)(Float.valueOf(dataArray[j])))
											{
										      dataIntegers[j] = 0;
											}
									else {*/
									try{
									dataIntegers[j] = Float.valueOf(dataArray[j]);
									}
									
									catch (NumberFormatException e){
										/*stopWorker = true;
										
										try {
										    TimeUnit.MILLISECONDS.sleep(1000);
										} catch (InterruptedException e1) {
										    //Handle exception
										}
										
										stopWorker = false;
										
										//dataIntegers[j]=0;*/
										
									}
									}
									
								setMydata(dataIntegers);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                        
	                       
	                        readBufferPosition = 0;
	                        
	                        handler.post(new Runnable()
	                        {
	                            public void run()
	                            {
	                                
	                            }

	                        });
	                    }
	                    else
	                    {
	                        readBuffer[readBufferPosition++] = b;
	                    }
	                }
	            }
				}  
	        }
	        });
		
		workerThread.start();
		
		}
	
		   
	       public void setMydata(float[] value){
	            Oscilloscope.dataIntegers=value.clone();
		
	}
	

	public class DrawView extends View {
		
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Paint text = new Paint();
        public DrawView(Context context) {
            super(context);
            //setContentView(R.layout.oscilloscope);
            
            /*if (!isInEditMode()) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }*/
            int lime = context.getResources().getColor(com.example.spectrum.R.color.lime);
            paint.setColor(Color.BLUE);
            paint2.setColor(lime);
            text.setColor(Color.WHITE);
            text.setTextSize(22); 
        }
        @Override
        public void onDraw(Canvas canvas) {
        	
             super.onDraw(canvas);
             float x = 0;
             //canvas.drawLine(0,400,480,400,paint);
             //canvas.drawLine(240,0,240,800,paint);
             //for (x = 438; x>42; x-=4){
            	 //canvas.drawLines(packetBytes, paint2);
                 //canvas.drawLine(x, dataIntegers[(int) ((438-x)/4)], x-4, dataIntegers[(int) ((438-x)/4+1)], paint2);
             canvas.drawLine(0,300,800,300,paint);
             canvas.drawLine(0,302,800,302,paint);
             canvas.drawLine(0,50,800,50,paint);
             canvas.drawText("5v", 2, 50, text);
             for (x = 780; x>20; x-=4){
                 canvas.drawLine(x, dataIntegers[(int) ((780-x)/4)], x-4, dataIntegers[(int) ((780-x)/4+1)], paint2);
             
             }
                invalidate();
        } 
        
	}
	

}
