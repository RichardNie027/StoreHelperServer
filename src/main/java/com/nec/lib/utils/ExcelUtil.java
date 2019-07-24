package com.nec.lib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    public static WritableFont arial14font = null;

    public static WritableCellFormat arial14format = null;
    public static WritableFont arial10font = null;
    public static WritableCellFormat arial10format = null;
    public static WritableFont arial12font = null;
    public static WritableCellFormat arial12format = null;

    public final static String UTF8_ENCODING = "UTF-8";
    public final static String GBK_ENCODING = "GBK";


    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    public static boolean format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);//对齐格式
            arial12format.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN); //设置边框

        } catch (WriteException e) {
            System.out.println("初始化EXCEL格式失败。" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 初始化Excel
     * @param fileName
     * @param colName
     */
    public static boolean initExcel(String fileName, String sheetName, String[] colName) {
        boolean success = format();
        if(!success)
            return success;
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            //创建标题栏
            sheet.addCell((WritableCell) new Label(0, 0, fileName,arial14format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            sheet.setRowView(0,340); //设置行高

            workbook.write();
        } catch (Exception e) {
            System.out.println("初始化EXCEL失败。" + e.getMessage());
            success = false;
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    System.out.println("初始化EXCEL失败。" + e.getMessage());
                }
            }
        }
        return success;
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean writeObjListToExcel(List<T> objList,String fileName, String[] orderedFieldNames) {
        boolean success = true;
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(new File(fileName));
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(new File(fileName),workbook);
                WritableSheet sheet = writebook.getSheet(0);

//				sheet.mergeCells(0,1,0,objList.size()); //合并单元格
//				sheet.mergeCells()

                for (int j = 0; j < objList.size(); j++) {
                    ArrayList list = CastUtil.cast(objList.get(j), orderedFieldNames);
                    for (int i = 0; i < list.size(); i++) {
                        Object item = list.get(i);
                        if(item instanceof String)
                            sheet.addCell(new Label(i, j + 1, (String)item, arial12format));
                        else if(item instanceof Boolean)
                            sheet.addCell(new jxl.write.Boolean(i, j + 1, (boolean)item, arial12format));
                        else if(item instanceof Number)
                            sheet.addCell(new jxl.write.Number(i, j + 1, ((Number) item).doubleValue(), arial12format));
                        else
                            sheet.addCell(new Label(i, j + 1, item.toString(), arial12format));
                        if (item.toString().length() <= 5){
                            sheet.setColumnView(i, item.toString().length()+8); //设置列宽
                        }else {
                            sheet.setColumnView(i, item.toString().length()+5); //设置列宽
                        }
                    }
                    sheet.setRowView(j+1,350); //设置行高
                }

                writebook.write();

                String fullpath = fileName.substring(0, fileName.lastIndexOf("/"));
                String finalPath = fullpath.substring(fullpath.lastIndexOf("/"));
                System.out.println("导出成功，文件目录" + finalPath);
            } catch (Exception e) {
                System.out.println("导出失败。" + e.getMessage());
                success = false;
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        System.out.println("导出失败。" + e.getMessage());
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        System.out.println("导出失败。" + e.getMessage());
                        success = false;
                    }
                }
            }
        }
        return success;
    }
}