import java.net.*;
import java.io.*;
public class PoolEchoServer extends Thread {
   public final static int defaultPort = 5000;
   ServerSocket theServer;
   static int num_threads = 10;
   public static void main(String[] args) {
      int port = defaultPort;
      try { port = Integer.parseInt(args[0]); }
      catch (Exception e) { }
      if (port <= 0 || port >= 65536) port = defaultPort;
      try {
         ServerSocket ss = new ServerSocket(port);
         System.out.println("Server Socket Start!!");
         for (int i = 0; i < num_threads; i++) {
            System.out.println("Create num_threads " 
                               + i + " Port:" + port);
            PoolEchoServer pes = new PoolEchoServer(ss);
            pes.start();
         }
      }
      catch (IOException e) { System.err.println(e); }
   }
   public PoolEchoServer(ServerSocket ss) { theServer = ss; }
   
   public void run() {
      while (true) {
         try {
            DataOutputStream output;
            DataInputStream input;
            Socket connection = theServer.accept();
            System.out.println("Accept Client!");

            //OutputStream os = s.getOutputStream();
            //InputStream is = s.getInputStream();
            input = new DataInputStream(
            connection.getInputStream() );
            output = new DataOutputStream(
            connection.getOutputStream() );
            //BufferedReader bf = new 
            // BufferedReader(new InputStreamReader(is));

            System.out.println("Client Connected and Start get I/O!!");
            while (true) {
               System.out.println("==> Input from Client: " 
                                  + input.readUTF());
               System.out.println(
                 "Output to Client ==> \"Connection successful\"");
               output.writeUTF( "Connection successful" );
               //os.write(n);
               //os.write("Hello Client!!");
               output.flush();
            } // end while
         } // end try
         catch (IOException e) { }
      } // end while
   } // end run
}
    