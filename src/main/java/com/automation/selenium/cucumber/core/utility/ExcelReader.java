package com.automation.selenium.cucumber.core.utility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelReader {

    private final List<Map<String, String>> data;
    
    private List<String> columns;

    public ExcelReader(String filePath, String spreadsheetName) throws IOException {
        data = read(Res.get(filePath), spreadsheetName);
    }

    /**
     * Gets the data from the excel sheet in List<Hashtable<String,String>>
     * format
     *
     * @return
     */
    public List<Map<String, String>> data() {
        return data;
    }
    
    public Object[][] convertedData() {
    	Object[][] convert = new Object[data.size()][columns.size()];
		for (int i = 0; i < data.size(); i++){
			Object[] item = new Object[columns.size()];
			Map<String, String> subList = data.get(i);
			for (int j = 0; j < columns.size(); j++) {
				item[j] = subList.get(columns.get(j));
			}
			convert[i] = item;
		}
		
		return convert;
    }

    /**
     * Get all values associated with the specified column throughout the entire
     * sheet
     *
     * @param column
     * @return values
     */
    public List<String> getValues(String column) {
        List<String> columnValues = new ArrayList<String>();
        for (Map<String, String> ht : data()) {
            columnValues.add(ht.get(column));
        }
        return columnValues;
    }

    /**
     * Reads the excel sheet and parses it into the
     * List<Hashtable<String,String> for accessing
     * @throws IOException 
     */
    private List<Map<String, String>> read(URL sheetUrl, String sheetName) throws IOException {
        List<Map<String, String>> contents = new ArrayList<Map<String, String>>();
        HSSFSheet sheet = null;
    	HSSFWorkbook workbook = new HSSFWorkbook(sheetUrl.openStream());
        sheet = workbook.getSheet(sheetName);
        
        Iterator<Row> rows = sheet.iterator();
        columns = new ArrayList<String>();
        
        boolean firstRow = true;
        while (rows.hasNext()) {
            Row row = rows.next();
            Iterator<Cell> cells = row.cellIterator();
            if (firstRow) {
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    columns.add(cell.getStringCellValue());
                }
                firstRow = false;
            } else {
                Map<String, String> rowData = new HashMap<String, String>();
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    rowData.put(columns.get(cell.getColumnIndex()), cell.toString());
                }
                contents.add(rowData);
            }
        }
        
        workbook.close();
        return contents;
    }
}
