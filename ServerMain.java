import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


public class ServerMain extends Thread{
	private Socket clientSocket;
	private OutputStream os;
	private DataOutputStream dos;
	private InputStream is;
	private DataInputStream dis;
	
	static ArrayList<ServerMain> threadPool = new ArrayList<ServerMain>();
	static ArrayList<Socket> sockets = new ArrayList<Socket>();
	
	private static final int listenPort = 12345;
	private int threadIndex;
	private int socketIndex;
	
	MySQLManager manager = new MySQLManager(); //Need to give actual connection string
	
	public ServerMain(Socket clientSocket, int threadIndex, int socketIndex) {
		this.threadIndex = threadIndex;
		this.socketIndex = socketIndex;
		try {
			this.clientSocket = clientSocket;
			os = clientSocket.getOutputStream();
			dos = new DataOutputStream(os);
			is = clientSocket.getInputStream();
			dis = new DataInputStream(is);
		} catch (IOException e) {
			System.err.println("Failed to construct new thread");
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		int connectionType = determineConnection();
		try {
			if(connectionType == 10) {
				dos.writeInt(connectionType);
				int status = mobileConnect();
				dos.writeInt(status);
			} else if(connectionType == 20) {
				dos.writeInt(connectionType);
				int status = resturauntConnect();
				dos.writeInt(status);
			} else if(connectionType == -1) {
				dos.writeInt(connectionType);
			}
		} catch(IOException e) {
			System.err.println("Could not write to client");
			e.printStackTrace();
		}
		endConnection();
	}
	
	private void endConnection() {
		try {
			dos.close();
			os.close();
			dis.close();
			is.close();
			clientSocket.close();
			synchronized(threadPool) {
				sockets.remove(socketIndex);
				threadPool.remove(threadIndex);
			}
		} catch (IOException e) {
			System.err.println("Failed to close streams");
			e.printStackTrace();
		}
	}
	
	private int determineConnection() {
		try {
			return dis.readInt();
		} catch (IOException e) {
			System.err.println("Could not read connection type from client.");
			e.printStackTrace();
			return -1;
		}
	}
	
	private int resturauntConnect() {
		try {
			int code = dis.readInt();
			if(code == 10) {	//Only read new data
				 String query = "SELECT * FROM Orders WHERE read=0";
				 ResultSet result = manager.query(query);
				 ResultSetMetaData data = result.getMetaData();
				 int numCol = data.getColumnCount();
				 dos.writeInt(numCol);
				 while(result.next()) {
					 for(int i=0; i<numCol; i++) {
						 dos.writeUTF(result.getString(i));
					 }
				 }
				 dos.writeUTF("done");	//Done writing data
				 return 0;
			} else if(code == 20) {	//Read all data
				 String query = "SELECT * FROM Orders";
				 ResultSet result = manager.query(query);
				 ResultSetMetaData data = result.getMetaData();
				 int numCol = data.getColumnCount();
				 dos.writeInt(numCol);
				 while(result.next()) {
					 for(int i=0; i<numCol; i++) {
						 dos.writeUTF(result.getString(i));
					 }
				 }
				 dos.writeUTF("done");	//Done writing data
				 return 0;
			} else if(code == 30) { //Get Special Offer
				String offer = dis.readUTF();
				String update = "INSERT INTO special_offers VALUES '" + offer +"'";
				manager.update(update);
				return 0;
			}
		} catch(IOException e) {
			System.err.println("Could not read code from resuraunt");
			e.printStackTrace();
			return -10;
		} catch (SQLException e) {
			System.err.println("Error reading from database");
			e.printStackTrace();
			return -20;
		}
		return -1;
	}
	
	private int mobileConnect() {
		int code = 0;
		try {
			code = dis.readInt();
		} catch (IOException e1) {
			System.err.println("Unable to communicate with mobile device");
			e1.printStackTrace();
		}
		if(code == 10) {	//read order from mobile device
			boolean done = false;
			while(!done) {
				try {
					String temp = dis.readUTF();
					ArrayList<String> values = new ArrayList<String>();
					if(temp != "done") {
						values.add(temp);
					} else {
						String update = "";
						update += "INSERT INTO orders VALUES (";
						for(int i=0; i<values.size(); i++) {
							if(i != values.size()-1) 
								update += "'" + values.get(i) + "',";
							else 
								update += "'" + values.get(i) + "'";
						}
						update += ")";
						manager.update(update);
						done = true;
						return 0;
					}
					
				} catch(IOException e) {
					System.err.println("Error reading String!");
					e.printStackTrace();
					return -10;
				} catch (SQLException e) {
					System.err.println("Error updating database!");
					e.printStackTrace();
					return -20;
				}
			}
		} else if(code == 20) {	//send mobile device special offers
			try {
				String query = "SELECT offers FROM special_offers WHERE valid=0";
				ResultSet rs = manager.query(query);
				while(rs.next()) {
					dos.writeUTF(rs.getString("offers"));
				}
				dos.writeUTF("done");
			} catch(SQLException e) {
				System.err.println("Failed to query database");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Failed to write to client");
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	public static void main(String args[]) {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(listenPort);
			while(true) {
				Socket temp = serverSocket.accept();
				synchronized(threadPool) {
					sockets.add(temp);
					ServerMain tempRef = new ServerMain(temp, threadPool.size(), sockets.size()-1);
					threadPool.add(tempRef);
					tempRef.start();
				}
			}
		} catch (IOException e) {
			System.err.println("Server failed");
			e.printStackTrace();
		}
	
	}
	
}
