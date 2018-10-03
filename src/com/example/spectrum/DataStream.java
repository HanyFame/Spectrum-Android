package com.example.spectrum;
import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;


import android.text.method.ScrollingMovementMethod;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;



public class DataStream extends Activity {
	
	private BluetoothSocket socket;
	private InputStream mmInputStream;
    //private OutputStream mmOutputStream;
    public int readBufferPosition = 0;
    
    
    TextView myLabel;
    public ListView lv;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datastream);
        socket = MainActivity.socket;
        final byte delimiter = 65;
        final byte[] readBuffer = new byte[2048];
        final Handler handler = new Handler();
        myLabel = (TextView)findViewById(R.id.label);
        myLabel.setMovementMethod(new ScrollingMovementMethod());
        //mmOutputStream = socket.getOutputStream();
        try {
			mmInputStream = socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
            	
               boolean stopWorker = false;
			while(!Thread.currentThread().isInterrupted() && !stopWorker)
               {
				int bytesAvailable = 0;
				try 
				{
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
                            //final String data = new String(packetBytes, "US-ASCII");
                            //for (int j=0; j<packetBytes.length; j++)
                            //{
                            	
                            final String data =  new String(encodedBytes, "US-ASCII");
                            final String data2 = data.trim();
                            //final JSONArray dataStrings = new JSONArray(data);
                            //final String[] dataStrings = data.split(",");
                            //}
                            readBufferPosition = 0;
                            
                            handler.post(new Runnable()
                            {
                                public void run()
                                {
                                	
                                    myLabel.append(data2 + "\n");
                 
                                    //ArrayList list = new ArrayList();
                          	      /*for( int j=0; j<data.length();j++)
                          	      {
                          	         list.add(data);
                          	      }
                          	      
                          	      final ArrayAdapter adapter = new ArrayAdapter(DataStream.this,R.layout.list,list);
                          	      lv.setAdapter(adapter);*/
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
               
            catch (IOException ex) 
            {
            	
                stopWorker = true;
                }
                    //Do work
               }
            }
            });
        workerThread.start();

        
        }
        
    }

