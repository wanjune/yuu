package com.github.wanjune.yuu.base.util;

import com.github.wanjune.yuu.base.exception.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * Kafka工具类
 *
 * @author wanjune
 * @since 2020-07-27
 */
@Component
public class KafkaUtil {

  private final KafkaTemplate<String, String> stringKafkaTemplate;

  @Autowired
  public KafkaUtil(KafkaTemplate<String, String> stringKafkaTemplate) {
    this.stringKafkaTemplate = stringKafkaTemplate;
  }

  /**
   * Kafka-启动(恢复)监听容器
   *
   * @param kafkaListenerEndpointRegistry 监听节点注册器(Spring通过自注解等实现)
   * @param listenerContainerId           监听容器ID
   */
  @SuppressWarnings("ALL")
  public static void startupListenerContainer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, String listenerContainerId) {
    try {
      MessageListenerContainer messageListenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerContainerId);
      if (!messageListenerContainer.isRunning()) {
        messageListenerContainer.start();
      }
      messageListenerContainer.resume();
    } catch (Exception ex) {
      throw new KafkaException(String.format("启动监听容器[%s]失败", listenerContainerId), ex);
    }
  }

  /**
   * Kafka-关闭(暂停)监听容器
   *
   * @param kafkaListenerEndpointRegistry 监听节点注册器(Spring通过自注解等实现)
   * @param listenerContainerId           监听容器ID
   */
  @SuppressWarnings("ALL")
  public static void shutdownListenerContainer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, String listenerContainerId) {
    try {
      MessageListenerContainer messageListenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerContainerId);
      messageListenerContainer.pause();
    } catch (Exception ex) {
      throw new KafkaException(String.format("关闭监听容器[%s]失败", listenerContainerId), ex);
    }
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
