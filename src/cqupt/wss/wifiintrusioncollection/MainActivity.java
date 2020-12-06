package cqupt.wss.wifiintrusioncollection;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cqupt.wss.tool.ClientThread;
import cqupt.wss.tool.TvThread;

@SuppressLint({ "SdCardPath", "HandlerLeak" })
public class MainActivity extends Activity  implements OnClickListener{
	TextView tv;
	final String filePath="/sdcard/Test/";
	TvThread thread = null;
	Handler mHandler;
	String wifiNames;
	String ip;
	ArrayList<String> selected_aps;
	private ClientThread clientThread;
	private final static int REQUESTCODE = 1; // 返回的结果码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button_start = (Button) findViewById(R.id.start);
		Button button_eval = (Button) findViewById(R.id.eval);
		tv = (TextView) findViewById(R.id.textview);
		//生成文件夹
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdir();
		}
		
	
		mHandler = new Handler(){  
			public void handleMessage(Message msg) {  
				super.handleMessage(msg);  
				String resPerSec = msg.obj.toString();
				int id = msg.arg1;
				if(id%30==0){
					tv.setText(resPerSec);
				}else if(id==-1){
					tv.setText("警告！出现异常，静默状态强度全为0");
				}else{
					tv.append(resPerSec);
				}
				// TODO
				clientThread = new ClientThread(resPerSec,ip);
        		new Thread(clientThread).start();
			}

		};
		selected_aps = new ArrayList<String>();
		getWifiNames();
		button_start.setOnClickListener(this);
		button_eval.setOnClickListener(this);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            if (requestCode == REQUESTCODE) {
                selected_aps = data.getStringArrayListExtra("selected_aps");
                ip = data.getStringExtra("ip");
            }
        }
    }

	private void getWifiNames() {
		wifiNames = filePath + "wifinames.txt";
		File txtfile = new File(wifiNames);
		try {
			if (!txtfile.exists()) {
				txtfile.createNewFile();
			}
		} catch (Exception e) {
			System.err.println("error:"+e);
		}
		thread = new TvThread(this,txtfile,mHandler,selected_aps);
		thread.getWifiNames(txtfile);
		thread.stopThread();
	}

	@SuppressLint({ "SimpleDateFormat", "ShowToast" })
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.start:
			if(selected_aps.size()<=0){
				Toast.makeText(this, "未选择ap", 0).show();
				return;
			}
			Toast.makeText(this, selected_aps.toString(), 0).show();
			tv.setText("");
			String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String fileName = filePath + time+".txt";
			File txtfile = new File(fileName);
			try {
				if (!txtfile.exists()) {
					txtfile.createNewFile();
					Toast.makeText(this, filePath + fileName+"创建", 0).show();
				}
			} catch (Exception e) {
				System.err.println("error:"+e);
			}
			thread = new TvThread(this,txtfile,mHandler,selected_aps);
			thread.start();
			break;
		case R.id.eval:
			Intent intent = new Intent(MainActivity.this, SettingActivity.class);
			intent.putExtra("wifiNames", wifiNames);
			startActivityForResult(intent, REQUESTCODE);
			break;
		}
	}
}
