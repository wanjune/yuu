package com.github.wanjune.yuu.base.model;

import com.github.wanjune.yuu.base.util.MapUtil;
import com.github.wanjune.yuu.base.util.StringUtil;
import com.github.wanjune.yuu.base.value.Messageable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Messge消息扩展
 *
 * @author wanjune
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("all")
public class MessageModel implements Serializable {

  // 扩展变量名
  private static final String VAR_EXT = "ext";

  // 状态码
  private int code;
  // 消息
  private String message;

  public MessageModel(Messageable message) {
    this.code = message.code();
    this.message = message.message();
  }

  public MessageModel(Messageable message, Object objExt) {
    this.code = message.code();
    if (objExt == null) {
      this.message = message.message();
    } else if (objExt instanceof String) {
      this.message = StringUtil.instance(message.message(), MapUtil.of(VAR_EXT, (String) objExt));
    } else if (objExt instanceof List) {
      this.message = StringUtil.instance(message.message(), MapUtil.of(VAR_EXT, StringUtil.getSplitString((List<String>) objExt)));
    } else if (objExt instanceof Map) {
      this.message = StringUtil.instance(message.message(), (Map<String, Object>) objExt);
    }
  }
}
