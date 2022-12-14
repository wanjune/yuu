package com.github.wanjune.yuu.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.wanjune.yuu.model.MessageModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JsonUtilTest {

  @Test
  void writeValueAsString() {
    Assertions.assertEquals("{\"message\":\"Jackson测试3\"}", JsonUtil.writeValueAsString(MapUtil.of("message", "Jackson测试3")));
    Assertions.assertEquals("{\"code\":1103,\"message\":\"Jackson测试3\"}", JsonUtil.writeValueAsString(MapUtil.of("code", 1103, "message", "Jackson测试3")));
  }

  @Test
  void getType() {
    MessageModel message = JsonUtil.getType("{\"code\":1103,\"message\":\"Jackson测试3\"}", new TypeReference<MessageModel>() {
    });
    Assertions.assertEquals(1103, message.getCode());
    Assertions.assertEquals("Jackson测试3", message.getMessage());
  }

  @Test
  void getMap() {
    Map<String, Object> message = JsonUtil.getMap("{\"code\":1103,\"message\":\"Jackson测试3\"}");
    Assertions.assertEquals(1103, message.get("code"));
    Assertions.assertEquals("Jackson测试3", message.get("message"));
  }

  @Test
  void getMapList() {
    List<Map<String, Object>> messageList = JsonUtil.getMapList("[{\"code\":1103,\"message\":\"Jackson测试3\"},{\"code\":1104,\"message\":\"Jackson测试4\"}]");

    for (Map<String, Object> message : messageList) {
      Assertions.assertEquals(message.get("code").equals(1103) ? "Jackson测试3" : "Jackson测试4", message.get("message"));
    }
  }

  @Test
  void getTypeRef() {
    Map<String, Object> msg1 = JsonUtil.getType("{\"code\":1103,\"message\":\"Jackson测试3\"}", JsonUtil.getTypeRef(new HashMap<String, Object>()));
    Assertions.assertEquals(1103, msg1.get("code"));
    Assertions.assertEquals("Jackson测试3", msg1.get("message"));

    List<Map<String, Object>> messageList = JsonUtil.getType("[{\"code\":1103,\"message\":\"Jackson测试3\"},{\"code\":1104,\"message\":\"Jackson测试4\"}]", JsonUtil.getTypeRef(new ArrayList<Map<String, Object>>()));

    for (Map<String, Object> msg2 : messageList) {
      Assertions.assertEquals(msg2.get("code").equals(1103) ? "Jackson测试3" : "Jackson测试4", msg2.get("message"));
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
}
