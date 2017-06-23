# Breakdown

分解事情的todo列表



## 数据抽象

多列表项的层级关系，每一项单独打勾

* 里面
  * 里面
    * 里面
      * 里面
  * 再来一个



```
Item
  isChecked() bool
  text() string
  childs() Iterator<Item>
  parent() Item
  add(Item)
  remove(Item)
  check()
```

