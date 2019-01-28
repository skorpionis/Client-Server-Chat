package chat.client;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame  implements ActionListener, TCPConnectionListener {     //интерфейс который реализует нажатия энтер в самом чате сообщзения

    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;


    public static void main(String[] args) {
         SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run()                   //вся эта конструкция нужна для работы Swing(Jframe) с потоком(функция Swingutil)
            {
                new ClientWindow();
            }
        });
    }



    private final JTextArea log  = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Ariec");
    private final JTextField fieldInput = new JTextField();

    private  TCPConnection connection;
    private ClientWindow (){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);   //чтобы стандартно закрыть окошко крестиком
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);   //окошко по середине стандартно поэтому нулл
        setAlwaysOnTop(true);     //чтобы сверху было окно и не у3биралось под другие окна
        log.setEditable(false);   //возможность редактирования лога
        log.setLineWrap(true); //перенос слов автоматически
        add(log, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this,IP_ADDR, PORT);
        } catch (IOException e) {
            e.printStackTrace();
            PrintMessage("Connection exception: " + e);

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
             String msg = fieldInput.getText();
             if (msg.equals("")) return;
             fieldInput.setText(null);
             connection.sendString(fieldNickName.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        PrintMessage("Connection ready... ");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
          PrintMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        PrintMessage("Connection closed... ");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
           PrintMessage("Connection exception: " + e);
    }

    //метод для того чтобы писать в текстовое поле
     private synchronized void PrintMessage(String msg){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                log.append(msg + "\n");                //msg что это
                log.setCaretPosition(log.getDocument().getLength());    //сдвинуть картеку наверх(автоскролл)
            }
        });


    }
}
