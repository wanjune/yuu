package com.github.wanjune.yuu.base.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CsvUtilTest {
  private static final String CSV_PATH = "/tmp/csv/测试_20221026.csv";
  private static final String EXCEL_PATH = "/tmp/excel/测试准备数据_20221026.xlsx";
  private static final List<String> CSV_COLUMN_LIST = ListUtil.asList("column1,column2,column3,column4,column5,column6,column7,column8,column9,column10," +
      "column11,column12,column13,column14,column15,column16,column17,column18,column19,column20," +
      "column21,column22,column23,column24,column25,column26,column27,column28,column29,column30," +
      "column31,column32,column33,column34,column35,column36,column37,column38,column39,column40,column41,column42,column43");

  @Test
  void data2Csv() {
    List<Map<String, Object>> dataList = new ArrayList<>();
    dataList.add(MapUtil.of("column1", "测试数据1", "column2", "name\uD83D\uDE021", "column3", "M", "column4", "1"));
    dataList.add(MapUtil.of("column1", "测试数据2", "column2", "name2\uD83D\uDE02", "column3", "F", "column4", "2"));
    dataList.add(MapUtil.of("column1", "测试数据\uD83D\uDE023", "column2", "\uD83D\uDE00name3", "column3", "F", "column4", "3"));

    // 新建
    FileUtil.delete(FileUtil.getParentPath(CSV_PATH));
    CsvUtil.data2Csv(CSV_PATH, ListUtil.asList("column1,column2,column3,column4"), dataList, null);
    // 追加
    CsvUtil.data2Csv(CSV_PATH, ListUtil.asList("column1", "column2", "column3", "column4"), dataList, ListUtil.asList("column1", "column2"));
  }

  @Test
  void excel2Csv() {
    // 新建
    FileUtil.delete(FileUtil.getParentPath(CSV_PATH));
    CsvUtil.excel2Csv(EXCEL_PATH, null, "测试01", 2, true, 500, CSV_PATH, CSV_COLUMN_LIST, null);
    // 追加
    CsvUtil.excel2Csv(EXCEL_PATH, null, "测试01", 2, true, 500, CSV_PATH, CSV_COLUMN_LIST, ListUtil.asList("column6", "column13"));
  }
}
