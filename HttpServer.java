import java.io.*;
import java.net.*;
import java.util.*;


class HttpServer
{
    private ArrayList<HttpServerSession> sessions;
    public int port = 51235;
    
    public void remove(HttpServerSession from)
    {
	synchronized(sessions) {
	    int len = sessions.size();
	    for(int i=0; i<len; i++) {
		HttpServerSession sesh = sessions.get(i);
		if(sesh == from) {
		    sessions.remove(i);
		    break;
		}
	    }
	}
    }
    

    public void start_server()
    {
	try{
	    
	    /* listen on port 51235 for incoming connections */
	    ServerSocket ss = new ServerSocket(port);
	    System.out.println("Listening on " + port);//-------Testing----
	    sessions = new ArrayList<HttpServerSession>();
	    
	    /* loop around, accepting new connections as they arrive */
	    while(true) {
		Socket s = ss.accept();
		System.out.println("Connection received from: " + s.getInetAddress().getHostAddress());
		HttpServerSession sesh =
		    new HttpServerSession(this, s);
		
		synchronized(sessions) {
		    sessions.add(sesh);
		}
		
		/* the start method causes the thread to run */
		sesh.start();
		
	    }
	}
	catch(Exception e)
	    {
		System.err.println(e);
	    }
    }
    
    public static void main(String[] args)
    {
	HttpServer server = new HttpServer();
	server.start_server();
	//Cannot add here 'HttpServer::main() that prints a message when a connection is made' - Instead, this is done in start_server()!
    }
    
}
