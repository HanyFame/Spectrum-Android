package com.example.spectrum;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Analyzer extends Activity{
DrawView drawView;
	
	public BluetoothSocket socket;
	public InputStream mmInputStream;
    //private OutputStream mmOutputStream;
    public int readBufferPosition = 0;
    //public static byte data[] = new byte [1024];
    public static float dataIntegers [] = new float[600];//Global Variable for Stream of bytes from Arduino sensor 
 
    
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //Force Landscape
	    super.onCreate(savedInstanceState);
	    socket = MainActivity.socket;
	    final byte delimiter = 65;
        final byte[] readBuffer = new byte[8192];
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
								float dataIntegers [] = new float[600];
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
	            Analyzer.dataIntegers=value.clone();
		
	}
	

	public class DrawView extends View {
		
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        Paint text = new Paint();
        public DrawView(Context context) {
            super(context);
            
            /*if (!isInEditMode()) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }*/
            int lime = context.getResources().getColor(com.example.spectrum.R.color.lime);
            paint.setColor(Color.BLUE);
            paint2.setColor(lime);
            text.setColor(Color.WHITE);
            text.setTextSize(18); 
            //paint.setStyle(Paint.Style.FILL);
            //paint.setStrokeCap(Paint.Cap.ROUND);
        }
        @Override
        public void onDraw(Canvas canvas) {
        	
             super.onDraw(canvas);
             float x = 0;
             /*canvas.drawLine(0,400,480,400,paint);
             canvas.drawLine(0,402,480,402,paint);
             canvas.drawText("100Hz", 5, 415, text);
             canvas.drawText("500Hz", 60, 415, text);
             canvas.drawText("1KHz", 115, 415, text);
             canvas.drawText("2KHz", 230, 415, text);
             canvas.drawText("3KHz", 345, 415, text);
             canvas.drawText("4KHz", 440, 415, text);
             canvas.drawLine(12,0,12,400,paint);
             canvas.drawLine(60,0,60,400,paint);
             canvas.drawLine(115,0,115,400,paint);
             canvas.drawLine(230,0,230,400,paint);
             canvas.drawLine(345,0,345,400,paint);
             canvas.drawLine(460,0,460,400,paint);*/
             canvas.drawLine(0,400,800,400,paint);
             canvas.drawLine(0,402,800,402,paint);
             canvas.drawText("100Hz", 18, 418, text);
             canvas.drawText("500Hz", 90, 418, text);
             canvas.drawText("1KHz", 173, 418, text);
             canvas.drawText("2KHz", 345, 418, text);
             canvas.drawText("3KHz", 518, 418, text);
             canvas.drawText("4KHz", 690, 418, text);
             canvas.drawLine(18,0,18,400,paint);
             canvas.drawLine(90,0,90,400,paint);
             canvas.drawLine(173,0,173,400,paint);
             canvas.drawLine(345,0,345,400,paint);
             canvas.drawLine(518,0,518,400,paint);
             canvas.drawLine(690,0,690,400,paint);
             
             for (x = 0; x<720; x+=6){
            	 
                 //canvas.drawLine(x, dataIntegers[(int) (x/4)], x+4, dataIntegers[(int) (x/4+1)], paint2);
            	 canvas.drawLine(x, dataIntegers[(int) (x/6)], x+6, dataIntegers[(int) (x/6+1)], paint2);
             }
                invalidate();
        } 
        
	}
}
