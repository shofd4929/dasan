package com.example.springbatch.batch;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.item.ItemReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelRowReader implements ItemReader<Row> {

    private final String filePath;
    private Iterator<Row> rowCursor;

    public ExcelRowReader(String filePath) throws IOException {

        this.filePath = filePath;
        initialize();
    }

    private void initialize() throws IOException {

        FileInputStream fileInputStream = new FileInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        this.rowCursor = sheet.iterator();


        if (rowCursor.hasNext()) {
            rowCursor.next();
        }
    }

    @Override
    public Row read() {

        if (rowCursor != null && rowCursor.hasNext()) {
            return rowCursor.next();
        } else {
            return null;
        }
    }
}
