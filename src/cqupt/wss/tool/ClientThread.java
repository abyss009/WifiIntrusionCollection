package cqupt.wss.tool;


import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 客户端的线程
 */
public class ClientThread implements Runnable {
    private Socket s;
    private String ip;
    private final int Port = 10046;
    // 定义向UI线程发送消息的Handler对象
    private String res;
    // 定义接收UI线程的消息的Handler对象
    public Handler revHandler;
    // 该线程所处理的Socket所对应的输入流
    BufferedReader br = null;
    OutputStream os = null;

    public ClientThread(String str,String ip) {
    	this.ip = ip;
        this.res = str;
//        Log.i("tag", "构造方法"+res);
    }

    public void run() {
        try {
        	Log.i("tag", "开始try方法,发送到"+ip);
            s = new Socket(ip, Port);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = s.getOutputStream();
                    if (res != null) {
                        try {
                            os.write((res + "\r\n").getBytes("utf-8"));
                            Log.i("tag", "执行发送");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
        } catch (SocketTimeoutException e1) {
            System.out.println("网络连接超时！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

