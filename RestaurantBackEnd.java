import java.util.*;
import java.io.*;
import java.net.*;

public class RestaurantBackEnd {
	private Socket socket;
	private InetAddress address;
	private int port;
	
	InputStream is;
	DataInputStream dis;
	OutputStream os;
	DataOutputStream dos;
	
	
	public RestaurantBackEnd() {	//Try to connect to localhost by default
		try {
		address = InetAddress.getLocalHost();
		port = 12345;
		socket = new Socket(address, port);
		getStreams();
		int status = connectToServer();
		
		if(status == -1) 
			throw new IOException("Could not connect/communicate with server");
		
		} catch(IOException e) {
			System.err.println("Could not connect to server");
			e.printStackTrace();
		}
	}
	
	public RestaurantBackEnd(InetAddress address, int port) {
		try {
			socket = new Socket(address, port);
			this.address = address;
			this.port = port;
			getStreams();
			int status = connectToServer();
			
			if(status == -1) 
				throw new IOException("Could not connect/communicate with server");
			
		} catch (IOException e) {
			System.err.println("Could not connect to server");
			e.printStackTrace();
		}
	}
	
	public RestaurantBackEnd(String address, int port) {
		try {
			this.address = InetAddress.getByName(address);
			this.port = port;
			socket = new Socket(this.address, port);
			getStreams();
			int status = connectToServer();
			
			if(status == -1) 
				throw new IOException("Could not connect/communicate with server");
			
		} catch(IOException e) {
			System.err.println("Could not connect to server");
			e.printStackTrace();
		}
	}
	
	private void getStreams() {
		try {
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			System.err.println("Could not set up streams");
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			dis.close();
			is.close();
			dos.close();
			os.close();
			socket.close();
		} catch (IOException e) {	//If this happens I'll eat my shoe
			System.err.println("Failed to close streams/socket!");
			e.printStackTrace();
		}
	}
	
	private int connectToServer() {
		try {
			dos.writeInt(20);	//Code to say that this is a restaurant connection
			if(dis.readInt() == 20) {
				return 0;
			} else {
				return -1;
			}
		} catch(IOException e) {
			System.err.println("Could not write to server");
			e.printStackTrace();
		}
		return -1;
	}
	
	public ArrayList<String[]> update(int code) {//10 for new, 20 for all, 40 for old
		try {
			ArrayList<String[]> result = new ArrayList<String[]>();
			
			dos.writeInt(code);	//Ask for update
			int numCol = dis.readInt();
			
			String temp = dis.readUTF();
			while(!(temp.equals("done"))) {
				result.add(new String[numCol]);
				for(int i=0; i<numCol; i++) {
					if(i != 0)
						result.get(result.size()-1)[i] = dis.readUTF();
					else
						result.get(result.size()-1)[i] = temp;
				}
				temp = dis.readUTF();
			}
			int status = dis.readInt();
			if(status >= 0)
				return result;
			else 
				return null;
		}
		 catch (IOException e) {
			System.err.println("Unable to write/read to server");
			e.printStackTrace();
		}
		return null;
	}
	
	public int sendOffer(String offer) {
		try {
			dos.writeInt(30);	//Send special offer code
			
			dos.writeUTF(offer);
			
			return dis.readInt();
		} catch (IOException e) {
			System.err.println("Failed to red/write to server");
			e.printStackTrace();
		}
		return -1;
	}
	//End class
}
