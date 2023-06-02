# TextReaderSideBarTool

[![API](https://img.shields.io/badge/IC-2020.1%2B-yellow.svg?labelColor=white&style=flat&logo=jetBrains&logoColor=black)](https://www.jetbrains.com)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/m/muedsa/TextReaderSidebarTool?logo=github)](https://github.com/muedsa/TextReaderSidebarTool/commits/master)
[![CodeFactor](https://www.codefactor.io/repository/github/muedsa/textreadersidebartool/badge)](https://www.codefactor.io/repository/github/muedsa/textreadersidertool)
[![GitHub Releases](https://img.shields.io/github/downloads/muedsa/TextReaderSidebarTool/total?logo=github)](https://github.com/muedsa/TextReaderSidebarTool/releases)

![](/record.webp)

## Download And Install

[Releases](https://github.com/muedsa/TextReaderSidebarTool/releases)

Install: [#1](https://github.com/muedsa/TextReaderSidebarTool/issues/1)

## Feature
- [x] 智障分章读
- [x] 搜索标题
- [x] 按行读取 默认ALT+E下一行、ALT+A上一行、ALT+S清空 (现在支持展示在通知、状态栏部件、编辑器背景中)

## Setting
**文本样式设置**
- 选择字体：影响`章节工具窗口`与`按行读取编辑器背景`中的文本字体
- 字体大小：影响`章节工具窗口`与`按行读取编辑器背景`中的文本大小
- 行间距：影响`章节工具窗口`中的文本行间距
- 首行缩进：影响`章节工具窗口`中的文本的首行缩进
- 段落间隔：影响`章节工具窗口`中的文本的段落间隔

**用于解析章节**_在点击`file`前可以先修改这些设置_
- 解析限制：章节的标题如果过长可以适量增加此限制
- 章节前缀：章节的标题前缀
- 章节后缀：章节的标题后缀
- 固定标题：如果存在如 "前言"、"引子" 等标题可以放在此列表中，使用`|`分隔
  *以上设置修改后，清空 "正则设置" ，然后点击 "file" 选择文件*
- 正则设置：匹配章节标题的正则，可自定义正则，`正则设置优先于以上设置，如果为空，会把上面的设置解析为正则`

**按行读取**
- 每行字数：设置按行读取的每行字数
- 展示方式：按行读取的内容展示在哪里 _支持展示在`通知` `状态栏` `编辑器背景`中（新UI由于有新时间线通知管理的工具窗口，通知已不会展示在状态栏左侧）_
- 文本颜色：状态栏、编辑器背景中的文本颜色
- 背景偏移：编辑器背景文本展示在背景的哪里
- 偏移量：编辑器背景中文本的偏移量
