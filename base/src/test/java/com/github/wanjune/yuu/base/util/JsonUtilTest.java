package com.github.wanjune.yuu.base.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.wanjune.yuu.base.model.MessageModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class JsonUtilTest {

  @Test
  void writeValueAsString() throws Exception {
    Assertions.assertEquals(JsonUtil.writeValueAsString(MapUtil.of("message", "Jackson测试3")), "{\"message\":\"Jackson测试3\"}");
    Assertions.assertEquals(JsonUtil.writeValueAsString(MapUtil.of("code", 1103, "message", "Jackson测试3")), "{\"code\":1103,\"message\":\"Jackson测试3\"}");
  }

  @Test
  void getType() throws Exception {
    MessageModel message = JsonUtil.getType("{\"code\":1103,\"message\":\"Jackson测试3\"}", new TypeReference<MessageModel>() {
    });
    Assertions.assertEquals(message.getCode(), 1103);
    Assertions.assertEquals(message.getMessage(), "Jackson测试3");
  }

  @Test
  void getMap() throws Exception {
    Map<String, Object> message = JsonUtil.getMap("{\"code\":1103,\"message\":\"Jackson测试3\"}");
    Assertions.assertEquals(message.get("code"), 1103);
    Assertions.assertEquals(message.get("message"), "Jackson测试3");
  }

  @Test
  void getMapList() throws Exception {
    List<Map<String, Object>> messageList = JsonUtil.getMapList("[{\"code\":1103,\"message\":\"Jackson测试3\"},{\"code\":1104,\"message\":\"Jackson测试4\"}]");

    for (Map<String, Object> message : messageList) {
      if (message.get("code").equals(1103)) {
        Assertions.assertEquals(message.get("message"), "Jackson测试3");
      } else {
        Assertions.assertEquals(message.get("message"), "Jackson测试4");
      }
    }
  }

  @Test
  void isJsonString() {
    Assertions.assertTrue(JsonUtil.isJsonString("{\"code\":1103,\"message\":\"Jackson测试3\"}"));
    Assertions.assertFalse(JsonUtil.isJsonString("{\"code:1103,\"message\":\"Jackson测试3\"}"));
  }

  @Test
  void isJsonArray() {
    Assertions.assertTrue(JsonUtil.isJsonArray("[{\"code\":1103,\"message\":\"Jackson测试3\"},{\"code\":1104,\"message\":\"Jackson测试4\"}]"));
    Assertions.assertFalse(JsonUtil.isJsonArray("[{\"code\":1103,\"message\":\"Jackson测试3},{\"code\":1104,\"message\":\"Jackson测试4\"}]"));
  }

  //@Test
  //void getCsvDataList() throws JsonProcessingException {
  //  List<Map<String, Object>> messageList = ListUtil.asList(MapUtil.of("code", 1103, "message", "Jackson测试3"), MapUtil.of("code", 1104, "message", "Jackson测试4"));
  //  List<Map<String, String>> dataList = JsonUtil.getCsvDataList(messageList, ListUtil.asList("code", "message"));
  //
  //  for (Map<String, String> cData : dataList) {
  //    if (cData.get("code").equals("1103")) {
  //      Assertions.assertEquals(cData.get("message"), "Jackson测试3");
  //    } else {
  //      Assertions.assertEquals(cData.get("message"), "Jackson测试4");
  //    }
  //  }
  //}
  //
  //@Test
  //void getCsvDataListUnicode() throws JsonProcessingException {
  //  List<Map<String, Object>> messageList = ListUtil.asList(MapUtil.of("code", 1103, "message", "Jackson测试3\uD83D\uDE09"), MapUtil.of("code", 1104, "message", "Jackson测试4\uD83D\uDE03"));
  //  List<Map<String, String>> dataList = JsonUtil.getCsvDataList(messageList, ListUtil.asList("code", "message"), ListUtil.asList("message"));
  //
  //  for (Map<String, String> cData : dataList) {
  //    if (cData.get("code").equals("1103")) {
  //      Assertions.assertEquals(cData.get("message"), "Jackson测试3");
  //    } else {
  //      Assertions.assertEquals(cData.get("message"), "Jackson测试4");
  //    }
  //  }
  //}
}
