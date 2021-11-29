# TextReaderSideBarTool

Working yet

![](https://github.com/MUedsa/TextReaderSiderTool/blob/master/temp.gif?raw=true)

## Download And Install

[Releases](https://github.com/MUedsa/TextReaderSiderTool/releases)

Install: [#1](https://github.com/MUedsa/TextReaderSiderTool/issues/1)

## Feature
- [x] 智障分章读
- [x] 搜索标题
- [x] 按行读取（以通知的方式显示在工具栏），默认ALT+E下一行、ALT+A上一行、ALT+S清空

## Setting

- 解析限制： 章节的标题如果过长可以适量增加此限制
- 章节前缀： 章节的标题前缀
- 章节后缀： 章节的标题后缀
- 固定标题： 如果存在如 "前言"、"引子" 等标题可以放在此列表中，使用`|`分隔

*以上设置修改后，清空 "正则设置" ，然后点击 "file" 选择文件*

- 正则设置： 匹配章节标题的正则，可自定义正则，如果不存在，会把上面的设置解析为正则
- 每行字数： 设置按行读取的每行字数