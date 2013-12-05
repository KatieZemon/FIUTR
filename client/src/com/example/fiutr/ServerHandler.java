package com.example.fiutr;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import android.os.AsyncTask;

public class ServerHandler {

	private String addNetwork = "ADD NETWORK ";
	private String SERVER_IP = "131.151.90.161";
	private int SERVER_PORT = 50000;
	
	
	public ServerHandler() 
	{}
	
	public void addNetwork(Network networkToAdd)
	{
		NetworkTask netTask = new NetworkTask();
		netTask.execute(networkToAdd);
	}
		
	public class NetworkTask extends AsyncTask<Network,byte[],Boolean>
	{
		Socket socket;
		OutputStream nos;
		
		@Override
		protected Boolean doInBackground(Network... networks)
		{
			try
			{
				SocketAddress sockAddr = new InetSocketAddress(SERVER_IP, SERVER_PORT);
				socket = new Socket();
				socket.connect(sockAddr, 5000);
				if(socket.isConnected() && networks.length > 0)
				{
					nos = socket.getOutputStream();
					socket.setSoTimeout(3000);
					PrintStream strm = new PrintStream(nos);
					System.out.println(addNetwork+networks[0].getName()+" "+networks[0].signalLevel+" "+networks[0].getLocation().latitude+" "+networks[0].getLocation().longitude+"\r\n");
					strm.print(addNetwork+networks[0].getName()+" "+networks[0].signalLevel+" "+networks[0].getLocation().latitude+" "+networks[0].getLocation().longitude+"\r\n");
					return true;
				}
				return false;
			}
			catch(SocketTimeoutException timeout)
			{
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
	}
}
