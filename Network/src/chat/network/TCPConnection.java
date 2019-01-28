package chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.EventListener;


public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener eventListener; //интерфейс для событий
    private final BufferedReader in;
    private final BufferedWriter out;








    public TCPConnection(TCPConnectionListener eventListener, String ipAdress, int port)throws  IOException{ // конструктор создает сокет
          this(eventListener, new Socket(ipAdress,port));
    }


    public TCPConnection(TCPConnectionListener eventListener,Socket socket)throws IOException {   //конструктор(кто то снаружи уже создаст сокет)
           this.eventListener = eventListener;
           this.socket = socket;
           in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
           out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

           rxThread = new Thread(new Runnable() {
               @Override
               public void run() {
                   try {
                       eventListener.onConnectionReady(TCPConnection.this); // Для того чтобы передать класс не анонимный(реализующий интерфейс раннбл
                      while(!rxThread.isInterrupted()){
                          eventListener.onReceiveString(TCPConnection.this, in.readLine());
                      }

                   } catch(IOException e){
                       eventListener.onException(TCPConnection.this,e);
                   }finally {
                       disconnect();
                   }
               }
           });
           rxThread.start();
    }






    public synchronized void sendString(String value)
    {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
            disconnect();
        }
    }
    public synchronized void disconnect()
    {
           rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
        }
    }
    @Override
    public String toString(){
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort(); //для логов кто подключился кто отключился
    }
}
