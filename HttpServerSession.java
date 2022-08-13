import java.io.*;
import java.net.*;
import java.util.*;

class HttpServerSession extends Thread
{
    private HttpServer hs;
    private Socket s;
    private BufferedReader read;
    private BufferedOutputStream write;
    HttpServerRequest req;
    
    public HttpServerSession(HttpServer cs, Socket s)
    {
	this.s = s;
	this.hs = cs;
    }
    
    private boolean println(BufferedOutputStream bos, String s)
    {
	String news = s + "\r\n";
	byte[] array = news.getBytes();
	try {
	    bos.write(array, 0, array.length);
	    bos.flush();
	} catch(IOException e) {
	    return false;
	}
	return true;
    }
    
    
    /* entry point into the HTTP Server Session */
    public void run()
    {
	try {
	    
	    
	    read = new BufferedReader(
				      new InputStreamReader(s.getInputStream()));
	    write = new BufferedOutputStream(s.getOutputStream());
	    req = new HttpServerRequest();                                                                                
	    
	    while(true) {
		req.process(in);//Process handles Null, so pass in without checking
		
		if(req.isDone()){
		    
		    //Return info requested
		    if(req.getFile() == null) { print404(); break;}
		   
		    int successNum = 0;
		    successNum = sendFile(req);
		    if(successNum == 404) print404();
		    if(successNum == 200) write.flush();
		  
		    break;
		}
		
	    }
	    
	    
	    hs.remove(this);
	    
	    /* close the socket */
	    s.close();
	    
	} catch(Exception e) {
	    System.err.println("Exception: " + e);
	}
    }
    
    private int sendFile(HttpServerRequest req){
	try{
	    FileInputStream fis = new FileInputStream((req.getHost() == null ? "localhost:"+ hs.port : req.getHost())+ "/" + req.getFile());
	    
	    //File exists, so 200 OK
	    println(write, "HTTP/1.1 200 OK");
	    println(write, "");
	    byte[] buf = new byte[1024];
	    int rc;
	    
	    while((rc = fis.read(buf)) != -1){
		write.write(buf,0,rc);
	    }
	    
	    System.out.println("The requested file WAS found");

	    return 200;
	}
	catch(FileNotFoundException ex){
	    return 404;
	}
	catch(IOException ex){
	    System.err.println("Exception: " + ex);
	    return 404;
	}
    }
    
    private void print404(){
	System.out.println("The requested file was NOT found");//Doing here catches 'unparsable header' issues
	try{
	    write = new BufferedOutputStream(s.getOutputStream());//Delete anything waiting in buffer
	    println(write, "HTTP/1.1 404 FileNotFound");
	    println(write, "");
	    println(write, "<html><h1>404</h1><p>File Not Found</p></html>");
	}
	catch(Exception e){
	    System.err.println("Exception: " + e);
	}
    }
}
