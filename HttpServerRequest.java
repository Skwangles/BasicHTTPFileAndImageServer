import java.util.*;
import java.io.*;

class HttpServerRequest
{
    private String file = null;
    private String host = null;
    private boolean done = false;
    private boolean isFirst = true;

    public boolean isDone() { return done; }
    public String getFile() { return file; }
    public String getHost() { return host; }

    public void process(String in)
    {
	if(done) return;
	if(in == null || in.equals("")) {done=true; return;}
	
	if(isFirst){
	    String[] parts = in.split(" ");
	    //Parse slash
	    if(!parts[0].equals("GET") || !parts[1].startsWith("/") || !parts[2].startsWith("HTTP/") || parts.length != 3) {done = true; return;}//badly formed - thus prevent from being parsed
	    //Deal with / for index.html
		String filename = parts[1].substring(1);//Cut out inital /
		if(filename.equals("") || filename.endsWith("/")) filename += "index.html";

		if(file == null) file = filename; //Can only assign file once per Request

		isFirst = false;//Mark first line being parsed
	}
	else{
	    //Can only assign Host: once - double usages will only accept first
	    if(in.startsWith("Host: ") && host == null) {
		String[] hostnames = in.split(" "); //get text after the 'Host:'
		if(hostnames.length > 1) host = hostnames[1];//Ensuring url exists before assigning
	    }
	}
    }
}
