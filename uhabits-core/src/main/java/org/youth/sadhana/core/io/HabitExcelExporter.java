package org.youth.sadhana.core.io;

import android.support.annotation.NonNull;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.youth.sadhana.core.Config;
import org.youth.sadhana.core.models.Checkmark;
import org.youth.sadhana.core.models.Habit;
import org.youth.sadhana.core.models.HabitList;
import org.youth.sadhana.core.models.Timestamp;
import org.youth.sadhana.core.preferences.Preferences;
import org.youth.sadhana.core.utils.DateFormats;
import org.youth.sadhana.core.utils.ExcelStyleManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kamlesh on 16-09-2017.
 */

public class HabitExcelExporter {


    private List<Habit> selectedHabits;

    private List<String> generateDirs;

    private List<String> generateFilenames;

    private String exportDirName;
    /**
     * Delimiter used in a CSV file.
     */
    private final String DELIMITER = ",";

    private final Preferences preferences;
    Timestamp fromDate;
    Timestamp toDate;
    String fileName;

    //create workbook
    Workbook wb = new HSSFWorkbook();
    ExcelStyleManager excelStyleManager = null;
    String exportedFileName = "";
    String exportedAbsoluteFileName = "";

    SimpleDateFormat sdf = new SimpleDateFormat("MMM_yyyy");

    @NonNull
    private final HabitList allHabits;

    public HabitExcelExporter(@NonNull HabitList allHabits,
                               @NonNull List<Habit> selectedHabits,
                               @NonNull File dir,
                               Preferences preferences,
                              Timestamp fromDate,
                              Timestamp toDate,
                              String fileName) {
        this.allHabits = allHabits;
        this.selectedHabits = selectedHabits;
        this.exportDirName = dir.getAbsolutePath() + "/";

        generateDirs = new LinkedList<>();
        generateFilenames = new LinkedList<>();
        this.preferences = preferences;
        excelStyleManager = new ExcelStyleManager();
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fileName = fileName;
    }


    public String exportToExcel() throws IOException {
        if(fileName != null && !fileName.trim().isEmpty())
            exportedFileName = fileName;
        String month = "";
        Date fromDateJava = null;
        if(fromDate != null) {
            fromDateJava = fromDate.toJavaDate();
            month = sdf.format(fromDateJava);
        }
        exportedFileName = exportedFileName + "_" + month + ".xls";
        exportedAbsoluteFileName = exportDirName + exportedFileName;;
        writeMonthlyReport(month);
        return exportedAbsoluteFileName;
    }
    public void writeMonthlyReport(String month) throws IOException {

        Sheet sheet = wb.createSheet(month);
        int rowNum = 0;
        writeMonthNameHeader(rowNum++,sheet,month);
        writeDatesSadhanaHeader(rowNum++,sheet);
        Timestamp oldest = fromDate;
        Timestamp newest = toDate;

        List<int[]> checkmarks = new ArrayList<>();
        for (Habit h : selectedHabits) {
            checkmarks.add(h.getCheckmarks().getValues(oldest, newest));
        }

        List<List<Checkmark>> checkmarksList = new ArrayList<>();
        for (Habit h : selectedHabits) {
            checkmarksList.add(h.getCheckmarks().getCheckmarks(oldest, newest));
        }

        List<List<String>> rows = new ArrayList<List<String>>();

        int days = oldest.daysUntil(newest);
        SimpleDateFormat dateFormat = DateFormats.getCSVDateFormat();
        int[] totals = new int[selectedHabits.size()];
        for (int i = 0; i <= days; i++)
        {
            List<String> row = new ArrayList<String>();
            Date day = oldest.plus(i).toJavaDate();
            String date = dateFormat.format(day);
            row.add(date);
            for(int j = 0; j < selectedHabits.size(); j++)
            {
                Checkmark checkmark = checkmarksList.get(j).get(days-i);
                int value = checkmark.getValue();
                if(selectedHabits.get(j).isNumerical()) {
                    if(value >= 1000) {
                        value = value/1000;
                    }
                    if(value  > 0)
                        row.add(String.valueOf(value));
                    else
                        row.add("");
                    totals[j] = totals[j] + value;
                    //Seva Remark
                    /*if(selectedHabits.get(j).getName().equalsIgnoreCase(Config.SEVA_NAME)) {
                        String remark = checkmark.getRemark();
                        row.add(remark);
                    }*/
                }
                else {
                    if(value  > 0) {
                        row.add("Y");
                        totals[j] = totals[j] + 1;
                    } else {
                        row.add("");
                    }
                }
            }
            rows.add(row);
        }

        for(List<String> rowData: rows) {
            Row row = sheet.createRow(rowNum++);
            int column = 0;
            for (String columnData : rowData) {
                Cell cellHeader = row.createCell(column++);
                cellHeader.setCellStyle(excelStyleManager.getContentCellStyle(wb));
                cellHeader.setCellValue(columnData);
            }
        }
        //sheet.setColumnWidth(Config.SEVA_REMARK_COLUMN, 4500);
        writeTotal(Config.total_RowNum,sheet,totals);
        writeIntoFile(wb);
    }

    private void writeTotal(int rowNum,Sheet sheet,int[] totals) {
        Row row = sheet.createRow(rowNum);
        int column = 0;
        Cell cell = row.createCell(column++);
        cell.setCellValue("Total");
        cell.setCellStyle(excelStyleManager.getHeaderCellStyle(wb));

        for(int total : totals) {
            Cell cellTotal = row.createCell(column++);
            cellTotal.setCellValue(total);
            cellTotal.setCellStyle(excelStyleManager.getHeaderCellStyle(wb));
        }
    }
    private void writeMonthNameHeader(int rowNum,Sheet sheet,String month) throws IOException
    {
        Row row = sheet.createRow(rowNum);

        List<String> headerDatas = new ArrayList<String>();
        headerDatas.add("Month: "+month);
        //headerDatas.add(month);
        headerDatas.add("Center :"+ preferences.getMBACenter());
        //headerDatas.add(preferences.getMBACenter());
        headerDatas.add("Name: "+ preferences.getFirstName() + " " + preferences.getLastName());
        headerDatas.add("Mahahatma Id: "+ preferences.getMahatmaId());
        //headerDatas.add(preferences.getFirstName() + " " + preferences.getLastName());
        //writeHeaderData(row,headerDatas);
        int column = 0;
        for(String headerData : headerDatas) {
            CellRangeAddress cellRangeAddress = new CellRangeAddress(rowNum, rowNum, column, column + 1);
            sheet.addMergedRegion(cellRangeAddress);
            Cell cellHeader = row.createCell(column);
            cellHeader.setCellStyle(excelStyleManager.getHeaderCellStyle(wb));
            cellHeader.setCellValue(headerData);
            column = column + 2;
        }
    }


    private void writeDatesSadhanaHeader(int rowNum,Sheet sheet) throws IOException
    {
        Row row = sheet.createRow(rowNum);
        List<String> headerDatas = new ArrayList<String>();
        headerDatas.add("Date");
        for (Habit h : selectedHabits) {
            headerDatas.add(h.getName());
        }
        //headerDatas.add("Seva Remark");
        writeHeaderData(row,headerDatas);
    }

    private void writeHeaderData(Row row,List<String> headerDatas) {
        int column = 0;
        for(String headerData : headerDatas) {
            Cell cellHeader = row.createCell(column++);
            cellHeader.setCellStyle(excelStyleManager.getHeaderCellStyle(wb));
            cellHeader.setCellValue(headerData);
        }
    }


    /**
     * Write into an excel file
     * @param wb
     */
    private void writeIntoFile(Workbook wb) throws IOException {
        boolean isFileOperationSuccessful = true;

        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(exportedAbsoluteFileName);
            wb.write(fileOut);
        } catch (IOException e) {
            isFileOperationSuccessful = false;
            throw e;
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.flush();
                    fileOut.close();
                } catch (IOException e) {
                    isFileOperationSuccessful = false;
                }
            }
        }

        //if file is successfully created and closed
        //open file, otherwise display error
        if (isFileOperationSuccessful) {
            //openExcelFile();
        } else {
            //excelFileError(getString(R.string.excelError));
        }
    }

    /**
     * Excel related error
     * @param errorMessage String
     *//*
    private void excelFileError(String errorMessage) {
        Snackbar.make(list, errorMessage,
                Snackbar.LENGTH_LONG).show();
    }*/


}
