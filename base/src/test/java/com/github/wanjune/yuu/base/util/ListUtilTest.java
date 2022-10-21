package com.github.wanjune.yuu.base.util;

import com.github.wanjune.yuu.base.model.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class ListUtilTest {

  @Test
  void size() {
    Assertions.assertEquals(ListUtil.size(null), 0);
    Assertions.assertEquals(ListUtil.size(new ArrayList<String>()), 0);
    Assertions.assertEquals(ListUtil.size(ListUtil.asList("1", "2")), 2);
  }

  @Test
  void equal() {
    List<MessageModel> list1 = ListUtil.asList(new MessageModel(1101, "测试消息01"), new MessageModel(1102, "测试消息02"));
    List<MessageModel> list2 = new ArrayList<>();
    list2.add(new MessageModel(1102, "测试消息02"));
    list2.add(new MessageModel(1101, "测试消息01"));

    Assertions.assertTrue(ListUtil.equal(list1, list2));

    list2.add(new MessageModel(1103, "测试消息03"));
    Assertions.assertFalse(ListUtil.equal(list1, list2));
  }

  @Test
  void asList() {
    ListUtil.asList(new MessageModel(1101, "测试消息01"), new MessageModel(1102, "测试消息02")).forEach(e -> {
      Assertions.assertEquals(e.getMessage(), e.getCode() == 1101 ? "测试消息01" : "测试消息02");
    });
  }

  @Test
  void asListString() {
    List<String> list1 = ListUtil.asList("[ 5, 7,8,4, 1,3 ]");
    list1.forEach(e -> {
      log.info(String.format("索引[%s] -> \t[%s]", list1.indexOf(e), e));
    });
    Assertions.assertEquals(list1.size(), 6);

    List<String> list2 = ListUtil.asList("5,7,  8,4,1,3, 2 ");
    list2.forEach(e -> {
      log.info(String.format("索引[%s] -> \t[%s]", list2.indexOf(e), e));
    });
    Assertions.assertEquals(list2.size(), 7);
  }

  @Test
  void isEmpty() {
    Assertions.assertTrue(ListUtil.isEmpty(null));
    Assertions.assertTrue(ListUtil.isEmpty(new ArrayList<>()));
    Assertions.assertFalse(ListUtil.isEmpty(ListUtil.asList("1", "a")));
  }

  @Test
  void nonEmpty() {
    Assertions.assertFalse(ListUtil.notEmpty(null));
    Assertions.assertFalse(ListUtil.notEmpty(new ArrayList<>()));
    Assertions.assertTrue(ListUtil.notEmpty(ListUtil.asList("1", "a")));
  }

  @Test
  void getSingleton() {
    Assertions.assertEquals(ListUtil.getSingleton(ListUtil.asList("测试001")), "测试001");
    Assertions.assertEquals(ListUtil.getSingleton(ListUtil.asList("测试001", "测试001")), ListUtil.asList("测试001", "测试001"));
  }

  @Test
  void sort() {
    List<Integer> list1 = ListUtil.asList(5, 7, 8, 4, 1, 3);
    ListUtil.sort(list1);

    Assertions.assertEquals(list1.get(0).intValue(), 1);
    Assertions.assertEquals(list1.get(5).intValue(), 8);

    List<String> list2 = ListUtil.asList("[5, 7,8,4, 1,3 ]");
    ListUtil.sort(list2);

    Assertions.assertEquals(list2.get(0), "1");
    Assertions.assertEquals(list2.get(5), "8");
  }

  @Test
  void copy() {
    List<Integer> list1 = ListUtil.asList(5, 7, 8, 4, 1, 3);
    List<Integer> list2 = new ArrayList<>();
    ListUtil.copy(list2, list1);

    Assertions.assertEquals(list1.get(0).intValue(), 5);
    Assertions.assertEquals(list1.get(5).intValue(), 3);

    List<MessageModel> list3 = ListUtil.asList(new MessageModel(1101, "测试消息01"), new MessageModel(1102, "测试消息02"));
    List<MessageModel> list4 = new ArrayList<>();
    ListUtil.copy(list4, list3);

    Assertions.assertEquals(list4.get(0).getCode(), 1101);
    Assertions.assertEquals(list4.get(1).getMessage(), "测试消息02");
  }

  @Test
  void partition() {
    List<MessageModel> list0 = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      list0.add(new MessageModel(101 + i, "测试消息" + (101 + i)));
    }

    List<List<MessageModel>> listp1 = ListUtil.partition(list0, 20);
    Assertions.assertEquals(listp1.get(4).size(), 20);
    Assertions.assertEquals(listp1.get(4).get(19).getMessage(), "测试消息200");
  }
}
