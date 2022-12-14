package com.github.wanjune.yuu.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.wanjune.yuu.exception.YuuException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
@Slf4j
public class CsvUtil {

  // 行分隔符
  private static final String LINE_SEPARATOR = "\n";
  // 列分隔符
  private static final char COLUMN_SEPARATOR = '\001';
  // 元素分隔符
  private static final String ELEMENT_SEPARATOR = Character.toString('\002');

  /**
   * 数据存储至CSV
   * <p>CSV文件已存在 -> 追加;CSV文件不存在 -> 创建</p>
   *
   * @param csvPath           CSV文件存储路径
   * @param csvColumnList     CSV列名列表
   * @param dataList          数据列表
   * @param unicodeColumnList 需要清理Unicode列的列表([emoji]等;null->不清理)
   */
  public static void data2Csv(final String csvPath,
                              final List<String> csvColumnList,
                              final List<Map<String, Object>> dataList,
                              final List<String> unicodeColumnList) {
    // CSV文件
    File csvFile = FileUtil.create(csvPath);
    boolean isCsvExists = csvFile.exists();

    try {
      // CSV对象
      CsvSchema csvSchema = CsvSchema.builder().addColumns(csvColumnList, CsvSchema.ColumnType.STRING)
          .build()
          .withoutQuoteChar()
          .withUseHeader(!isCsvExists)
          .withLineSeparator(LINE_SEPARATOR)
          .withColumnSeparator(COLUMN_SEPARATOR)
          .withArrayElementSeparator(ELEMENT_SEPARATOR);

      // 保存数据到文件中
      new CsvMapper().configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, false)
          .writerFor(JsonUtil.TYPE_REF_LIST_MAP)
          .with(csvSchema)
          .writeValue(new FileOutputStream(csvFile, isCsvExists), getCsvDataList(csvColumnList, dataList, unicodeColumnList));

      log.info(String.format("数据存储至CSV(%s)[%s]成功", !isCsvExists ? "创建" : "追加", csvPath));
    } catch (Exception ex) {
      throw new YuuException(String.format("数据存储至CSV[%s]失败", csvPath), ex);
    }
  }

  /**
   * EXCEL数据保存至CSV
   * <p>CSV文件已存在 -> 追加;CSV文件不存在 -> 创建</p>
   *
   * @param excelPath         EXCEL文件路径
   * @param sheetNo           EXCEL的SHEET序号(0开始)[不设置时需要设置为null,与sheetName二选一]
   * @param sheetName         EXCEL的SHEET名称[不设置时需要设置为null,与sheetNo二选一]
   * @param headRowNo         EXCEL的标题行号(标题行之后,开始读取数据)
   * @param cellAutoTrim      EXCEL的单元格数据是否去除前后空格
   * @param batchRowCnt       EXCEL读取时每批量处理数据行数(影响系统资源-内存)
   * @param csvPath           CSV文件存储路径
   * @param csvColumnList     CSV列名列表
   * @param unicodeColumnList 需要清理Unicode列的列表([emoji]等;null->不清理)
   */
  public static void excel2Csv(final String excelPath,
                               final Integer sheetNo,
                               final String sheetName,
                               final Integer headRowNo,
                               final boolean cellAutoTrim,
                               final int batchRowCnt,
                               final String csvPath,
                               final List<String> csvColumnList,
                               final List<String> unicodeColumnList) {

    // CSV文件是否已经存在
    boolean isCsvExists = FileUtil.isExists(csvPath);

    try {
      // 读取EXCEL数据并保存至CSV
      EasyExcel.read(excelPath, new ReadListener<Map<Integer, String>>() {
        // EXCEL临时数据缓存
        private List<Map<String, Object>> cacheRowDataList = ListUtils.newArrayListWithExpectedSize(batchRowCnt);

        @SneakyThrows
        @Override
        public void invoke(Map<Integer, String> rowDataMap, AnalysisContext context) {
          // 每行解析的数据(按照索引[0~]对应列的Map类型) -> 按照自定义格式转换并存储至EXCEL临时数据缓存
          cacheRowDataList.add(getRowData(csvColumnList, rowDataMap));
          // 如果EXCEL临时数据缓存已满 -> EXCEL临时数据缓存 存储至CSV并清空
          if (cacheRowDataList.size() >= batchRowCnt) {
            data2Csv(csvPath, csvColumnList, cacheRowDataList, unicodeColumnList);
            cacheRowDataList = ListUtils.newArrayListWithExpectedSize(batchRowCnt);
          }
        }

        @SneakyThrows
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
          // 所有行解析完成后,如EXCEL临时数据缓存不为空 -> 存储至CSV
          if (ListUtil.notEmpty(cacheRowDataList)) data2Csv(csvPath, csvColumnList, cacheRowDataList, unicodeColumnList);
        }

        // 自定义格式转换
        private Map<String, Object> getRowData(List<String> columnList, Map<Integer, String> rowDataMap) {
          Map<String, Object> rowData = new HashMap<>();
          for (int i = 0; i < columnList.size(); i++) {
            rowData.put(columnList.get(i), rowDataMap.get(i));
          }
          return rowData;
        }
      }).sheet(sheetNo, sheetName).autoTrim(cellAutoTrim).headRowNumber(headRowNo).useScientificFormat(false).doRead();

      log.info(String.format("EXCEL[%s]数据保存至CSV(%s)[%s]成功", excelPath, !isCsvExists ? "创建" : "追加", csvPath));
    } catch (Exception ex) {
      throw new YuuException(String.format("EXCEL[%s]数据保存至CSV[%s]失败", excelPath, csvPath), ex);
    }
  }

  /**
   * 获取CSV的存储数据列表
   *
   * @param csvColumnList     CSV列名列表
   * @param dataList          数据列表
   * @param unicodeColumnList 需要清理Unicode列的列表([emoji]等;null->不清理)
   * @return CSV的存储数据列表
   */
  private static List<Map<String, String>> getCsvDataList(final List<String> csvColumnList,
                                                          final List<Map<String, Object>> dataList,
                                                          final List<String> unicodeColumnList) {
    List<Map<String, String>> stdCsvDataList = null;
    if (ListUtil.notEmpty(dataList) && ListUtil.notEmpty(csvColumnList)) {
      stdCsvDataList = new ArrayList<>(dataList.size());
      for (Map<String, Object> data : dataList) {
        stdCsvDataList.add(getCsvData(csvColumnList, data, unicodeColumnList));
      }
    }
    return stdCsvDataList;
  }

  /**
   * 获取CSV的存储数据
   *
   * @param csvColumnList     CSV列名列表
   * @param data              数据
   * @param unicodeColumnList 需要清理Unicode列的列表([emoji]等;null->不清理)
   * @return CSV的存储数据
   */
  private static Map<String, String> getCsvData(final List<String> csvColumnList,
                                                final Map<String, Object> data,
                                                final List<String> unicodeColumnList) {
    Map<String, String> stdCsvData = new HashMap<>(csvColumnList.size());
    for (String column : csvColumnList) {
      if (StringUtil.isContains(column, unicodeColumnList, true) && data.get(column) != null) {
        stdCsvData.put(column, JsonUtil.writeValueAsString(StringUtil.cleanUnicode(data.get(column).toString())));
      } else if (data.get(column) != null) {
        stdCsvData.put(column, JsonUtil.writeValueAsString(data.get(column)));
      } else {
        stdCsvData.put(column, StringUtil.EMPTY);
      }
    }
    return stdCsvData;
  }

}
