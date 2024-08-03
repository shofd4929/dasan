package com.example.springbatch.batch;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelRowReader implements ItemStreamReader<Row> {

    private final String filePath;
    private FileInputStream fileInputStream;
    private Workbook workbook;
    private Iterator<Row> rowCursor;


    public ExcelRowReader(String filePath) throws IOException {

        this.filePath = filePath;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            this.rowCursor = sheet.iterator();


            if (rowCursor.hasNext()) {
                rowCursor.next();
            }
        } catch (IOException e) {
            throw new ItemStreamException(e);
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

    @Override
    public void close() throws ItemStreamException {

        try {
            if (workbook != null) {
                workbook.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (IOException e) {
            throw new ItemStreamException(e);
        }
    }
}
