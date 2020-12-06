package cqupt.wss.wifiintrusioncollection;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import cqupt.wss.tool.AptxtUtil;

public class SettingActivity extends Activity implements OnClickListener {

	ListView lv;
	ArrayList<String> list;
	AptxtUtil aptxt;
	ArrayList<String> selected_aps;
	EditText et;
	@SuppressLint({ "ShowToast", "SdCardPath" })
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		Intent intent = getIntent();
		String wifiNames = intent.getStringExtra("wifiNames");
		Button button_done = (Button) findViewById(R.id.done);
		button_done.setOnClickListener(this);
		lv = (ListView) findViewById(R.id.lv);
		et = (EditText) findViewById(R.id.ip);
		
		list = new ArrayList<String>();
		aptxt = new AptxtUtil();
		try {
			list = aptxt.getApList(wifiNames);//从txt中获取所有的ap名
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, list);
        lv.setAdapter(adapter);//将所有ap名显示到listview中
	}
	
	public void getSelectAps() {//获取选中的ap名到字符串数组中
        // long[] authorsId = radioButtonList.getCheckItemIds();
        long[] apsId = getListSelectededItemIds(lv);
        selected_aps = new ArrayList<String>();// String[apsId.length];
        String message = null;
        if (apsId.length > 0) {
            for (int i = 0; i < apsId.length; i++) {
            	//selected_aps[i] = list.get((int)apsId[i]);
            	selected_aps.add(list.get((int)apsId[i]));
            	message = selected_aps.toString();
            }
        } else {
            message = "请至少选择一个AP！";
        }
        Toast.makeText(SettingActivity.this, message, Toast.LENGTH_LONG).show();
    }
	
	 // 避免使用getCheckItemIds()方法
    public long[] getListSelectededItemIds(ListView listView) {
         
        long[] ids = new long[listView.getCount()];//getCount()即获取到ListView所包含的item总个数
        //定义用户选中Item的总个数
        int checkedTotal = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            //如果这个Item是被选中的
            if (listView.isItemChecked(i)) {
                ids[checkedTotal++] = i;
            }
        }
 
        if (checkedTotal < listView.getCount()) {
            //定义选中的Item的ID数组
            final long[] selectedIds = new long[checkedTotal];
            //数组复制 ids
            System.arraycopy(ids, 0, selectedIds, 0, checkedTotal);
            return selectedIds;
        } else {
            //用户将所有的Item都选了
            return ids;
        }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.done:
			getSelectAps();
            Intent intent = new Intent();
            // 获取选取ap
            intent.putStringArrayListExtra("selected_aps", selected_aps); 
            intent.putExtra("ip", et.getText().toString());
            setResult(2, intent);
            finish(); //结束当前的activity的生命周期
			break;
		}
	}
}
