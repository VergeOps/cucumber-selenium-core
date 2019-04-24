package com.automation.selenium.cucumber.core.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class CSVReader {
	
	private String file;
	
	public CSVReader(String file) {
		this.file = file;
	}
	
	public Object[][] readFile() {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		FileReader reader = null;
		BufferedReader br = null;
		
		try {
			reader = new FileReader(file);
			br = new BufferedReader(reader); 
			
			int count = 0;
			
			String s;
			while((s = br.readLine()) != null) {
				if (count != 0) {
					String[] strings = s.split(",");
					ArrayList<String> str = new ArrayList<String>();
					String helper = "";
					for (int i = 0; i < strings.length; i++) {
						if (strings[i].contains("\"") && helper.equals("")) {
							helper = strings[i].replace("\"", "");
						} else if (helper.equals("")) {
							str.add(strings[i]);
						} else if (!helper.equals("") && !strings[i].contains("\"")){
							helper = helper + "," + strings[i];
						} else if (!helper.equals("") && strings[i].contains("\"")) {
							helper = helper + "," + strings[i].replace("\"", "");
							str.add(helper);
							helper = "";
						}
					}
					list.add(str);
				}
				count++;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Object[][] data = new Object[list.size()][list.get(0).size()];
		for (int i = 0; i < list.size(); i++){
			ArrayList<String> subList = list.get(i);
			Object[] inner = new Object[subList.size()];
			for (int j = 0; j < subList.size(); j++) {
				inner[j] = subList.get(j);
			}
			data[i] = inner;
		}
		return data;
	}

}
