package cqupt.wss.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AptxtUtil {
	public ArrayList<String> getApList(String filename) throws IOException{
		int n = 3;//随机读取n行的ap
		File file = new File(filename);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		ArrayList<String> list = new ArrayList<String>();
		String s = "";
		while ((s = br.readLine()) != null) {
			list.add(s);
		}
		if(list.size()<3)
			n=list.size();
		br.close();
		fr.close();
		Set<String> aps=new HashSet<String>();
		for(int i=0;i<n;i++){
			int temp = (int) (Math.random() * (list.size()));
			String[] ss = list.get(temp).split("##");
			for(int j=0;j<ss.length;j++){
				String temp_ap = ss[j].split(",")[0].trim();
				if(temp_ap.length()!=0)
					aps.add(temp_ap);
			}
		}
		ArrayList<String> result = new ArrayList<String>(aps);
		return result;
		
	}
	public static void write2txt(String content,File file) throws IOException{
		RandomAccessFile raf = new RandomAccessFile(file, "rwd");
		raf.seek(file.length());
		raf.write(content.getBytes());
		raf.close();
	}
}
