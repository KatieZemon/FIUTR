package com.example.fiutr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {

	private final String SERVER_IP = "131.151.90.161";
	private final int SERVER_PORT = 50000;
	private String filePath = "";
	private String fileName = "gpsmaps.txt";
	private FileOutputStream writer;
	private String message = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		filePath = getFilesDir().toString()+"/"+fileName;
		System.out.println(filePath);
		System.out.println("Starting thread");
		ProgressDialog temp = new ProgressDialog(this);
		temp.setMessage("Loading...");
		NetworkTask newtask = new NetworkTask(temp);
		newtask.execute();
		return;
	}
	
	public class NetworkTask extends AsyncTask<Void,byte[],Boolean>
	{
		Socket socket;
		InputStream nis;
		OutputStream nos;
		String fileOutputString = "";
		ProgressDialog dialog;
		
		public NetworkTask(ProgressDialog temp)
		{
			this.dialog = temp;
		}
		
		@Override
		public void onPreExecute()
		{
			dialog.show();
		}
		
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
					socket.setSoTimeout(6000);
					PrintStream strm = new PrintStream(nos);
					strm.print("GET NETWORKS SUPER SPECIAL\r\n");
					byte[] buffer = new byte[4096];
					int read = nis.read(buffer,0,4096);
					while(read != -1)
					{
						byte[] tempData = new byte[read];
						System.arraycopy(buffer,0,tempData,0,read);
						fileOutputString+=new String(tempData);
						read = nis.read(buffer,0,4096);
					}
				}
				return false;
			}
			catch(SocketTimeoutException timeout)
			{
				if(fileOutputString == "")
					return false;
				try
				{
					System.out.println(fileOutputString);
					File dataFile = new File(filePath);
					FileOutputStream dataStream = new FileOutputStream(dataFile,false);
					dataStream.write(fileOutputString.getBytes());
					dataStream.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				return true;
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
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			if(result)
			{
				dialog.dismiss();
				System.out.println("Successfully worked.");
				Intent intent = new Intent(StartActivity.this, MainActivity.class);
				startActivity(intent);
				StartActivity.this.finish();
			}
		}
	}
}
