package com.github.wanjune.yuu.util;

import com.github.wanjune.yuu.exception.KafkaException;
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

  private final KafkaTemplate<String, String> kafkaTemplate;

  public KafkaUtil(final KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  /**
   * Kafka-启动(恢复)监听容器
   *
   * @param listenerRegistry    监听节点注册器(Spring通过自注解等实现)
   * @param listenerContainerId 监听容器ID
   */
  @SuppressWarnings("ALL")
  public static void startupListenerContainer(final KafkaListenerEndpointRegistry listenerRegistry, final String listenerContainerId) {
    try {
      MessageListenerContainer messageListenerContainer = listenerRegistry.getListenerContainer(listenerContainerId);
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
   * @param listenerRegistry    监听节点注册器(Spring通过自注解等实现)
   * @param listenerContainerId 监听容器ID
   */
  @SuppressWarnings("ALL")
  public static void shutdownListenerContainer(final KafkaListenerEndpointRegistry listenerRegistry, final String listenerContainerId) {
    try {
      MessageListenerContainer messageListenerContainer = listenerRegistry.getListenerContainer(listenerContainerId);
      messageListenerContainer.pause();
    } catch (Exception ex) {
      throw new KafkaException(String.format("关闭监听容器[%s]失败", listenerContainerId), ex);
    }
  }

  /**
   * 发送消息(字符串或对象)
   *
   * @param topic   消息Topic
   * @param message 消息Message(字符串或对象)
   */
  public <T> void send(final String topic, final T message) {
    try {
      if (message instanceof String) {
        kafkaTemplate.send(topic, String.valueOf(message));
      } else {
        kafkaTemplate.send(topic, JsonUtil.writeValueAsString(message));
      }
    } catch (Exception ex) {
      throw new KafkaException(String.format("发送消息[topic=%s]失败", topic), ex);
    }
  }

}
