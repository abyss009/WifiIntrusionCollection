package cqupt.wss.tool;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

public class TvThread extends Thread{
	private boolean stop = false;
	private WifiManager mWifiManager;
	public File txtfile;
	Handler mHandler;
	private ArrayList<String> selected_aps;//选中ap
	private int m;
	private final int n = 60;//每个ap静默采集个数
	private final int w = 15;
	private int[][] ints_static;
	private int[][] ints_window;
	private int[] ints_perSec;
	private int[] ints_perMea;
	private final String space1 = "##";
	private final String space2 = ",";
	private final int time_rest = 1000;
	Message msg;
	@SuppressLint("SdCardPath")
	public TvThread(Context context,File file,Handler mHandler,ArrayList<String> selected_aps) {
		this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		this.txtfile = file;
		this.mHandler = mHandler;
		this.selected_aps = selected_aps;
		m = selected_aps.size();//选中ap数量
		if(m>0){
			ints_static = new int[n][m];//一列为一个ap的rssi
			ints_window = new int[w][m];
			ints_perSec = new int[m];
			ints_perMea = new int[m];
		}
		new CountDouble();
	}

	public void stopThread() {
		stop = true;
	}

	public void getWifiNames(File txtfile){
		mWifiManager.startScan();
		List<ScanResult> mWifiList=mWifiManager.getScanResults();
		String resPerSec = "";
		for(ScanResult scanResult:mWifiList){
			resPerSec = resPerSec+scanResult.SSID+space2+scanResult.BSSID+space2+scanResult.level+space1;
		}
		// 每次写入时，都换行写
		String strContent = resPerSec + "\r\n";
		try {
			RandomAccessFile raf = new RandomAccessFile(txtfile, "rwd");
			raf.seek(txtfile.length());
			raf.write(strContent.getBytes());
			raf.close();
		} catch (Exception e) {
			System.err.println("TestFile Error on write File:" + e);
		}
	}
	public void run() {
		int i=0;
		boolean ifCountStatic = false;
		while (!stop) {
			mWifiManager.startScan();
			List<ScanResult> mWifiList=mWifiManager.getScanResults();
			String resPerSec = "";
			for(ScanResult scanResult:mWifiList){
				resPerSec = resPerSec+scanResult.SSID+space2+scanResult.BSSID+space2+scanResult.level+space1;
			}

			String strContent = resPerSec + "\r\n";
			msg = new Message();
			msg.arg1 = i;
			
			// 提取select_aps中rssi到ints
			extractApRssi(resPerSec);
			
			if(i<n){
				ints_static[i] = ints_perSec.clone();
				System.out.println(Arrays.deepToString(ints_static));
				ints_window[i%w] = ints_perSec.clone();
				msg.obj = "静默第"+i+"条:"+resPerSec.substring(0,30)+"......\r\n";
				msg.what = 1;
			}else{
				judgeMean();//检查ints_perMea
				judgePerSec();//检查ints_perSec
				System.out.println("judge ints_perSec:"+Arrays.toString(ints_perSec));
				moveWindow();//将ints_perSec输入到ints_window[0]位置
				if(!ifCountStatic){//还没有
//					judgeArray(ints_static);//判断数组为0的情况
					CountDouble.count_static(ints_static);
					ifCountStatic = true;
				}
				int res = CountDouble.count_perSec(ints_window);
				msg.what = 2;
				msg.obj = "检测结果代码为： "+res+ "\r\n";
				if(i==n)
					System.out.println(Arrays.deepToString(ints_static));
				else
					System.out.println(Arrays.deepToString(ints_window));
			}
			// 每次写入时，都换行写
			try {
				AptxtUtil.write2txt(strContent, txtfile);
				mHandler.sendMessage(msg);
				Thread.sleep(time_rest);  
			} catch (Exception e) {
				System.err.println("TestFile Error on write File:" + e);
			}
			i++;
		}
		System.out.println("stopped!!!");
	}


	private void extractApRssi(String resPerSec) {
		String[] per_sec = resPerSec.split(space1);
		for(int j=0;j<per_sec.length;j++){
			String[] per_wifi = per_sec[j].split(space2);
			for(int k=0;k<m;k++){
				if(per_wifi[0].equals(selected_aps.get(k))){
					ints_perSec[k] = Integer.parseInt(per_wifi[2]);
					if(ints_perSec[k]!=0)
						ints_perMea[k] = (ints_perMea[k]+ints_perSec[k])/(ints_perSec[k]/(ints_perSec[k]+1)+1);
				}
			}
		}
		System.out.println("ints_perSec:"+Arrays.toString(ints_perSec));
		System.out.println("ints_perMea:"+Arrays.toString(ints_perMea));
	}

	private void judgeMean() {
		for(int j=0;j<m;j++){
			if(ints_perMea[j]==0){
				msg.arg1 = -1;
				msg.obj = "选的某个ap强度全为0，请退出程序，重新选择";
				mHandler.sendMessage(msg);
				return;
			}
		}
	}
//	private void judgeArray(int[][] ints) {
//		for(int j=0;j<ints.length;j++)
//			for(int k=0;k<m;k++)
//				if(ints[j][k]==0){
//					ints[j][k]=ints_perMea[k];
//				}
//	}

	private void judgePerSec() {
		for(int k=0;k<m;k++)
			if(ints_perSec[k]==0)
				ints_perSec[k]=ints_perMea[k];
	}

	private void moveWindow(){
		for(int j=1;j<w-1;j++)
			ints_window[j] = ints_window[j-1];
		ints_window[0] = ints_perSec;
	}
}
