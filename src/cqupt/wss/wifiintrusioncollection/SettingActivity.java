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
			list = aptxt.getApList(wifiNames);//��txt�л�ȡ���е�ap��
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, list);
        lv.setAdapter(adapter);//������ap����ʾ��listview��
	}
	
	public void getSelectAps() {//��ȡѡ�е�ap�����ַ���������
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
            message = "������ѡ��һ��AP��";
        }
        Toast.makeText(SettingActivity.this, message, Toast.LENGTH_LONG).show();
    }
	
	 // ����ʹ��getCheckItemIds()����
    public long[] getListSelectededItemIds(ListView listView) {
         
        long[] ids = new long[listView.getCount()];//getCount()����ȡ��ListView��������item�ܸ���
        //�����û�ѡ��Item���ܸ���
        int checkedTotal = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            //������Item�Ǳ�ѡ�е�
            if (listView.isItemChecked(i)) {
                ids[checkedTotal++] = i;
            }
        }
 
        if (checkedTotal < listView.getCount()) {
            //����ѡ�е�Item��ID����
            final long[] selectedIds = new long[checkedTotal];
            //���鸴�� ids
            System.arraycopy(ids, 0, selectedIds, 0, checkedTotal);
            return selectedIds;
        } else {
            //�û������е�Item��ѡ��
            return ids;
        }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.done:
			getSelectAps();
            Intent intent = new Intent();
            // ��ȡѡȡap
            intent.putStringArrayListExtra("selected_aps", selected_aps); 
            intent.putExtra("ip", et.getText().toString());
            setResult(2, intent);
            finish(); //������ǰ��activity����������
			break;
		}
	}
}
