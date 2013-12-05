package com.example.fiutr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {

	private final String SERVER_IP = "131.151.90.161";
	private final int SERVER_PORT = 50000;
	private TextView serverStatus;
	private boolean isConnected = false;
	private String filePath = "";
	private String fileName = "gpsMaps.txt";
	private FileOutputStream writer;
	private String message = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		filePath = getFilesDir().toString()+"/gpsmaps.txt";
		System.out.println("Starting thread");
		NetworkTask newtask = new NetworkTask();
		newtask.execute();
		//System.out.println("Starting main activity");
		//Intent intent = new Intent(this, MainActivity.class);
		//startActivity(intent);
		return;
	}
	
	public class NetworkTask extends AsyncTask<Void,byte[],Boolean>
	{
		Socket socket;
		InputStream nis;
		OutputStream nos;
		
		@Override
		protected Boolean doInBackground(Void... params)
		{
			boolean result = false;
			try
			{
				SocketAddress sockAddr = new InetSocketAddress(SERVER_IP, SERVER_PORT);
				socket = new Socket();
				socket.connect(sockAddr, 5000);
				if(socket.isConnected())
				{
					nis = socket.getInputStream();
					nos = socket.getOutputStream();
					PrintStream strm = new PrintStream(nos);
					//strm.print("ADD NETWORK thingy 44 66 -68\r\n");
					strm.print("GET NETWORKS SUPER SPECIAL\r\n");
					//strm.close();
					byte[] buffer = new byte[4096];
					int read = nis.read(buffer,0,4096);
					while(read != -1)
					{
						byte[] tempData = new byte[read];
						System.arraycopy(buffer,0,tempData,0,read);
						System.out.println(new String(tempData));
						read = nis.read(buffer,0,4096);
					}
				}
				return false;
			}
			catch (Exception e)
			{
				System.err.println("Didnt work...");
				e.printStackTrace();
			}
			finally
			{
				try 
				{
					if(socket.isConnected())
						socket.close();
				} 
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}
		
		
	}
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}
