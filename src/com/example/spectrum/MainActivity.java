package com.example.spectrum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;



import android.support.v7.app.ActionBarActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MainActivity extends ActionBarActivity {
	
	public TextView msg;
	private BluetoothAdapter BA;
	public Set<BluetoothDevice>pairedDevices;
	private ListView lv;
	private Button listButton;
	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static BluetoothSocket socket;
    final Runnable mLoadingData = new Runnable() {public void run() {
		Toast.makeText(getApplicationContext(),"Connected!!!!",Toast.LENGTH_SHORT).show();// In your case, show your custom dialog
	}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView msgTextView = (TextView) findViewById(R.id.msg);
        msgTextView.setText(R.string.name);
        
        Button onButton = (Button) findViewById(R.id.onButton);
        Button offButton = (Button) findViewById(R.id.offButton);
        listButton = (Button) findViewById(R.id.listButton);
        Button inStreamButton = (Button) findViewById(R.id.inStreamButton);
        Button Oscilloscope = (Button) findViewById(R.id.Oscilloscope);
        Button SpectrumAnalyzer = (Button) findViewById(R.id.SpectrumAnalyzer);
        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView1);
        //final Handler mHandler = new Handler();
 
               
        //Turn on Bluetooth button
        onButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if (!BA.isEnabled()) {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 0);
                    Toast.makeText(getApplicationContext(),"Turned on" 
                    ,Toast.LENGTH_SHORT).show();
                 }
                 else{
                    Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_SHORT).show();
                    }
                }
            });
        
        
        //Turn Off Bluetoooth button
        offButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            	if (BA.isEnabled()) {
            	BA.disable();
                Toast.makeText(getApplicationContext(),"Turned off" ,Toast.LENGTH_SHORT).show();
            	}
                else{
                	Toast.makeText(getApplicationContext(),"Bluetooth is already off", Toast.LENGTH_SHORT).show();
                	}
                }
            });
        
        
        //List Paired Devices from Array
        listButton.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		if (!BA.isEnabled()) {
                    Toast.makeText(getApplicationContext(),"Bluetooth is off" ,Toast.LENGTH_SHORT).show();
        		}
        		else{
        	      pairedDevices = BA.getBondedDevices();
        	      ArrayList list = new ArrayList();
        	      for(BluetoothDevice bt_device : pairedDevices)
        	         list.add(bt_device.getName() +":  "+ bt_device.getAddress());
        	      
        	      //Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();
        	      final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,R.layout.list,list);
        	      lv.setAdapter(adapter);
        		}
        
        	}
        });
        
        
        //Start ACtivity to Show Arduino Bluetooth Data STream
        inStreamButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	Intent intent = new Intent(MainActivity.this, DataStream.class);
        	    startActivity(intent);
                // Do something in response to button click
                //mConnectedThread = new ConnectedThread(socket);
                //mConnectedThread.start();
            
                }
            });
        
        
        
        //Start ACtivity to Show Arduino Oscilloscope
        Oscilloscope.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	Intent intent = new Intent(MainActivity.this, Oscilloscope.class);
        	    startActivity(intent);
                // Do something in response to button click
                //mConnectedThread = new ConnectedThread(socket);
                //mConnectedThread.start();
            
                }
            });
        
        //Start Activity to Show Arduino Spectrum Analyzer
        SpectrumAnalyzer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	Intent intent = new Intent(MainActivity.this, Analyzer.class);
        	    startActivity(intent);
                // Do something in response to button click
                //mConnectedThread = new ConnectedThread(socket);
                //mConnectedThread.start();
            
                }
            });
        
        
        
        //Select paired device to connect
        lv.setOnItemClickListener(new OnItemClickListener()
        {
           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                 long arg3) 
           {
                 //String value = (String)adapter.getItemAtPosition(position);
                 //Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT).show();
                 // assuming string and if you want to get the value on click of list item
                 // do what you intend to do on click of listview row
        	   
        	   if (BA.isDiscovering()){
        		   BA.cancelDiscovery();
               }
        	   
        	   final String info = ((TextView) arg1).getText().toString();
        	   String address = info.substring(info.length()-17);
        	   //connect the device when item is click
        	   final BluetoothDevice connect_device = BA.getRemoteDevice(address);
        	   final Thread connectDevice = new Thread() {
   	       
        		   @Override
        		   public void run(){
        			   try {
        				   socket = connect_device.createRfcommSocketToServiceRecord(MY_UUID);
        				   socket.connect();
        				   } catch (IOException e) {
        					   // TODO Auto-generated catch block
        					   e.printStackTrace();
        					   };
        					   }
        		   };
        		   connectDevice.start();
        		   Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
        		      		
           }});
           
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
