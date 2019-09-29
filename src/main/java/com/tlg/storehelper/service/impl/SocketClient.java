package com.tlg.storehelper.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient extends Thread {

    //定义一个Socket对象
    Socket socket = null;
    String toSend = "";

    //写操作
    OutputStream os = null;
    PrintWriter pw = null;

    public SocketClient(String host, int port, String toSend) {
        this.toSend = toSend;
        try {
            //需要服务器的IP地址和端口号，才能获得正确的Socket对象
            socket = new Socket(host, port);
        } catch (ConnectException e) {
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }

    }

    @Override
    public void run() {
        //客户端一连接就写数据给服务器
        new sendMessThread().start();
        super.run();
//        try {
//            sleep(2000);
//            // 读Socket里面的数据
//            InputStream is = socket.getInputStream();
//            byte[] buf = new byte[1024];
//            int len = 0;
//            while ((len = is.read(buf)) != -1) {
//                System.out.println(new String(buf, 0, len));
//            }
//            socket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    //往Socket里面写数据，需要新开一个线程
    class sendMessThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                os = socket.getOutputStream();
                pw = new PrintWriter(os);
                pw.println(toSend);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.shutdownOutput();
                os.close();     //关闭socket
                //socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //函数入口
    public static void main(String[] args) {
        //需要服务器的正确的IP地址和端口号
        SocketClient socketClient = new SocketClient("127.0.0.1", 1234, "test");
        socketClient.start();
    }

}
