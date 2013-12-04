package com.example.fiutr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class StartActivity extends Activity {

	private final String SERVER_IP = "131.151.90.161";
	private final int SERVER_PORT = 50000;
	private TextView serverStatus;
	private boolean isConnected = false;
	private String filePath = "";
	private String fileName = "gpsMaps.txt";
	private FileOutputStream writer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("In on create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		filePath = getFilesDir().toString()+"/gpsmaps.txt";
		System.out.println("Starting thread");
		ClientThread thingy = new ClientThread();
		Thread clientThread = new Thread(thingy);
		clientThread.start();
		thingy.kill();
		System.out.println("Starting main activity");
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		return;
	}
	
	public class ClientThread implements Runnable 
	{
		volatile boolean isRunning = true;
		
		public void run()
		{
			System.out.println("In runnable!");
			while(isRunning)
			{
				Socket socket = null;
				try
				{
					System.out.println("Attempting to open socket");
					InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
					serverStatus.setText("Connecting to server!\n");
					socket = new Socket(serverAddr, SERVER_PORT);
					isConnected = true;
					while(isConnected)
					{
						try
						{
							serverStatus.append("Requesting file from server...\n");
							PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
							out.println("GET NETWORKS\r\n");
							serverStatus.append("Send request to server!\n");
							out.close();
							// Getting data
							BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String message = "";
							int charsRead = 0;
							serverStatus.append("Waiting for data from server...\n");
							char[] buffer = new char[2048];
							while((charsRead = inStream.read(buffer)) != -1)
							{
								message += new String(buffer).substring(0, charsRead);
							}
							//finished reading, now to write
							try
							{
								serverStatus.append("Writing data from server\n");
								writer = openFileOutput(fileName, Context.MODE_APPEND);
								writer.write(message.getBytes());
								writer.close();
							}
							catch(FileNotFoundException e)
							{
								System.err.println("StartActivity creating a file!\n");
								BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
								writer.close();
								run();
							}
							catch(Exception f)
							{
								System.err.println("StartActivity had an error: "+f.getMessage());
								socket.close();
								isConnected = false;
							}					
						}
						catch(Exception j)
						{
							System.err.println("StartActivity had an error: "+j.getMessage());
							socket.close();
							isConnected = false;
						}
					}
				}
				catch(Exception e)
				{
					System.err.println("StartActivity had an error: "+e.getMessage());
					try {
						socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					isConnected = false;
				}
			}
		return;
		}
		
	public void kill()
	{
		isRunning = false;
	}
	}

}
