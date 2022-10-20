package com.github.wanjune.yuu.base.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.wanjune.yuu.base.exception.YuuException;
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
   * @param strCsvFilePath CSV文件存储路径
   * @param csvHeaderList  CSV列名列表
   * @param csvDataList    CSV数据列表
   * @param unicodeColumns 需要清理Unicode的列([moji文字]等,无需要设null)
   */
  public static void data2Csv(final String strCsvFilePath,
                              final List<String> csvHeaderList,
                              final List<Map<String, Object>> csvDataList,
                              final List<String> unicodeColumns) {
    // CSV文件
    File csvFile = FileUtil.create(strCsvFilePath);
    boolean isCsvExists = csvFile.exists();

    try {
      // CSV对象
      CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().addColumns(csvHeaderList, CsvSchema.ColumnType.STRING);
      CsvSchema csvSchema = csvSchemaBuilder.build().withoutQuoteChar().withUseHeader(!csvFile.exists())
          .withLineSeparator(LINE_SEPARATOR).withColumnSeparator(COLUMN_SEPARATOR).withArrayElementSeparator(ELEMENT_SEPARATOR);

      // 保存数据到文件中
      CsvMapper csvMapper = new CsvMapper().configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, false);
      csvMapper.writerFor(new TypeReference<List<Map<String, String>>>() {
      }).with(csvSchema).writeValue(new FileOutputStream(csvFile, csvFile.exists()), getCsvDataList(csvDataList, csvHeaderList, unicodeColumns));

      log.info(String.format("数据存储至CSV(%s)[%s]成功", !isCsvExists ? "创建" : "追加", strCsvFilePath));
    } catch (Exception ex) {
      throw new YuuException(String.format("数据存储至CSV[%s]失败", strCsvFilePath), ex);
    }
  }

  /**
   * EXCEL数据保存至CSV
   * <p>CSV文件已存在 -> 追加;CSV文件不存在 -> 创建</p>
   *
   * @param filePath       EXCEL文件路径
   * @param sheetNo        EXCEL的SHEET序号(0开始)[不设置时需要设置为null,与sheetName二选一]
   * @param sheetName      EXCEL的SHEET名称[不设置时需要设置为null,与sheetNo二选一]
   * @param headRowNo      EXCEL的标题行号(标题行之后,开始读取数据)
   * @param cellAutoTrim   EXCEL的单元格数据是否去除前后空格
   * @param batchRowCnt    EXCEL读取时每批量处理数据行数(影响系统资源-内存)
   * @param csvFilePath    CSV文件路径
   * @param csvHeaderList  CSV文件头
   * @param unicodeColumns 需要清理Unicode的列([moji文字]等,无需要设null)
   */
  public static void excel2Csv(final String filePath,
                               final Integer sheetNo,
                               final String sheetName,
                               final Integer headRowNo,
                               final boolean cellAutoTrim,
                               final int batchRowCnt,
                               final String csvFilePath,
                               final List<String> csvHeaderList,
                               final List<String> unicodeColumns) {

    // CSV文件是否已经存在
    boolean isCsvExists = FileUtil.isExists(csvFilePath);

    try {
      // 读取EXCEL数据并保存至CSV
      EasyExcel.read(filePath, new ReadListener<Map<Integer, String>>() {
        // EXCEL临时数据缓存
        private List<Map<String, Object>> cacheDataList = ListUtils.newArrayListWithExpectedSize(batchRowCnt);

        @SneakyThrows
        @Override
        public void invoke(Map<Integer, String> rowDataMap, AnalysisContext context) {
          // 每行解析的数据(按照索引[0~]对应列的Map类型) -> 按照自定义格式转换并存储至EXCEL临时数据缓存
          cacheDataList.add(getRowData(csvHeaderList, rowDataMap));
          // 如果EXCEL临时数据缓存已满 -> EXCEL临时数据缓存 存储至CSV并清空
          if (cacheDataList.size() >= batchRowCnt) {
            data2Csv(csvFilePath, csvHeaderList, cacheDataList, unicodeColumns);
            cacheDataList = ListUtils.newArrayListWithExpectedSize(batchRowCnt);
          }
        }

        @SneakyThrows
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
          // 所有行解析完成后,如EXCEL临时数据缓存不为空 -> 存储至CSV
          if (ListUtil.nonEmpty(cacheDataList)) data2Csv(csvFilePath, csvHeaderList, cacheDataList, unicodeColumns);
        }

        // 自定义格式转换
        private Map<String, Object> getRowData(List<String> headerList, Map<Integer, String> rowDataMap) {
          Map<String, Object> rowData = new HashMap<>();
          for (int i = 0; i < headerList.size(); i++) {
            rowData.put(headerList.get(i), rowDataMap.get(i));
          }
          return rowData;
        }
      }).sheet(sheetNo, sheetName).autoTrim(cellAutoTrim).headRowNumber(headRowNo).useScientificFormat(false).doRead();

      log.info(String.format("EXCEL[%s]数据保存至CSV(%s)[%s]成功", filePath, !isCsvExists ? "创建" : "追加", csvFilePath));
    } catch (Exception ex) {
      throw new YuuException(String.format("EXCEL[%s]数据保存至CSV[%s]失败", filePath, csvFilePath), ex);
    }
  }

  /**
   * 获取CSV的存储数据列表
   *
   * @param csvDataList    Json对象数据列表
   * @param csvHeaderList  转换的CSV表头列表
   * @param unicodeColumns 需要清理Unicode的列([moji文字]等,无需要设null)
   * @return CSV的存储数据列表
   * @throws Exception Exception
   */
  private static List<Map<String, String>> getCsvDataList(final List<Map<String, Object>> csvDataList,
                                                          final List<String> csvHeaderList,
                                                          final List<String> unicodeColumns) throws Exception {
    List<Map<String, String>> reCsvDataList = null;
    if (ListUtil.nonEmpty(csvDataList) && ListUtil.nonEmpty(csvHeaderList)) {
      reCsvDataList = new ArrayList<>(csvDataList.size());
      for (Map<String, Object> csvDataItem : csvDataList) {
        reCsvDataList.add(getCsvData(csvDataItem, csvHeaderList, unicodeColumns));
      }
    }
    return reCsvDataList;
  }

  /**
   * 获取CSV的存储数据
   *
   * @param csvData        Json对象数据
   * @param csvHeaderList  转换的CSV表头
   * @param unicodeColumns 需要清理Unicode的列([moji文字]等,无需要设null)
   * @return CSV的存储数据
   * @throws Exception Exception
   */
  private static Map<String, String> getCsvData(final Map<String, Object> csvData,
                                                final List<String> csvHeaderList,
                                                final List<String> unicodeColumns) throws Exception {
    Map<String, String> reCsvData = new HashMap<>(csvHeaderList.size());
    for (String key : csvHeaderList) {
      if (StringUtil.isContains(key, unicodeColumns, true) && csvData.get(key) != null) {
        reCsvData.put(key, JsonUtil.writeValueAsString(StringUtil.cleanUnicode(csvData.get(key).toString())));
      } else if (csvData.get(key) != null) {
        reCsvData.put(key, JsonUtil.writeValueAsString(csvData.get(key)));
      } else {
        reCsvData.put(key, StringUtil.EMPTY);
      }
    }
    return reCsvData;
  }

}
