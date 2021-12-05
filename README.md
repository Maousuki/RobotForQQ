# simbot Demo - Mirai

这是[simple-robot](https://github.com/ForteScarlet/simpler-robot) 框架使用[mirai组件](https://github.com/ForteScarlet/simpler-robot/tree/dev/component/component-mirai) 对接[Mirai](https://github.com/mamoe/mirai) 的Demo项目。

## 需要做的
### fork/clone
fork或者clone此项目到你的本地，并使用IDE工具打开并构建它。

### 修改配置文件
打开文件 [yourBot.bot](src/main/resources/simbot-bots/yourBot.bot) 并修改其中的code项为你测试用的QQ账号，password项为你的测试QQ账号密码，例如：
```yaml
code=123456
password=123456
```

### 保证安静
将你的bot放在一些测试用的群而不是一些大型群。

### 阅读
- [listener](src/main/java/love/simbot/example/listener) 包下为一些监听函数示例。阅读它们的注释，并可以试着修改它们。

### 运行
执行[SimbotExampleApplication](src/main/java/love/simbot/example/SimbotExampleApplication.java) 中的main方法。

### 验证
如果你是第一次使用此框架，且出现了诸如需要“滑动验证”等相关错误，你可以尝试先使用一次 [simbot-mirai-login-solver-selenium-helperPack](https://github.com/simple-robot/simbot-mirai-login-solver-selenium-helperPack) 来使腾讯记住你的设备信息。

以及，记得关闭账号中与“设备锁”、“安全保护”等相关内容。

### 功能

- 查询城市天气  命令：%天气预报+城市名
- 玩骰子  命令：%玩骰子
- 今天吃什么 命令：%Bot今天吃什么
- 显示群信息  命令：%显示群信息
- 显示发送人信息  命令：%发送人信息
- 显示热榜 ，目前支持微博、微信、百度、新浪、历史上的今天、哔哩哔哩、抖音  命令：%热榜+APP名
- 点歌（源自网易云） 命令： %点歌+歌名（返回为搜索结果的第一首歌曲，为了增加准确率建议歌名后加上歌手名)
- 随机生成Asoul二创图片  命令：%asoul
- 订阅B站UP主直播提醒  命令：%添加直播订阅+UP主B站主页URL地址
- 订阅B站UP主动态提醒  命令：%添加动态订阅+UP主B站主页URL地址
- 删除B站UP主直播/动态提醒  命令：%删除直播/动态订阅+UP主名字
- 查询订阅列表  命令：%查询订阅列表
- 开启直播提醒  命令：%开始监听直播（只需在任意一个部署了机器人的群里发送一次该命令即可，发送后所有部署了机器人的群都会开始监听直播）
- 开启动态提醒  命令：%开始监听动态（只需在任意一个部署了机器人的群里发送一次该命令即可，发送后所有部署了机器人的群都会开始监听动态）

### 协助

如果你有一个好的示例点子，你可以通过[github pr](https://github.com/simple-robot/simbot-mirai-demo/pulls) 来协助此demo项目的更新。

### 友情链接

- [simpler-robot: simple-robot](https://github.com/Maousuki/simpler-robot)（感谢提供的Robot框架）
- [猫猫码，一个可爱的通用特殊码，CQ码的精神延续](https://github.com/ForteScarlet/CatCode)
- [A-SOUL提醒小助手 IDEA版](https://github.com/cnsky1103/A-SOUL-Reminder)（感谢提供绕过SSL验证方法）
- [A-SOUL Fans Art - 一个魂的二创 ](https://asoul.cloud/)（感谢提供的随机图片api）
- [NeteaseCloudMusicApi: 网易云音乐 Node.js API](https://github.com/Maousuki/NeteaseCloudMusicApi)（感谢提供的众多网易云api）

