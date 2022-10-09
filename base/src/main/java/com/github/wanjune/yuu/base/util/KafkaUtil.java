package com.github.wanjune.yuu.base.util;

import com.github.wanjune.yuu.base.exception.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUtil {

  private final KafkaTemplate<String, String> stringKafkaTemplate;

  @Autowired
  public KafkaUtil(KafkaTemplate<String, String> stringKafkaTemplate) {
    this.stringKafkaTemplate = stringKafkaTemplate;
  }

  /**
   * 发送消息(字符串或Java对象)
   *
   * @param topic   消息Topic
   * @param message 消息内容(字符串或Java对象)
   */
  public <T> void send(String topic, T message) {
    try {
      if (message instanceof String) {
        stringKafkaTemplate.send(topic, String.valueOf(message));
      } else {
        stringKafkaTemplate.send(topic, JsonUtil.writeValueAsString(message));
      }
    } catch (Exception ex) {
      throw new KafkaException(String.format("发送消息[topic=%s]失败", topic), ex);
    }
  }

}
