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

### 协助
如果你有一个好的示例点子，你可以通过[github pr](https://github.com/simple-robot/simbot-mirai-demo/pulls) 来协助此demo项目的更新。

### 友情链接

- [simpler-robot: simple-robot](https://github.com/Maousuki/simpler-robot)（感谢提供的Robot框架）
- [A-SOUL提醒小助手 IDEA版](https://github.com/cnsky1103/A-SOUL-Reminder)（感谢提供绕过SSL验证方法）
- [A-SOUL Fans Art - 一个魂的二创 ](https://asoul.cloud/)（感谢提供的随机图片api）
- [NeteaseCloudMusicApi: 网易云音乐 Node.js API](https://github.com/Maousuki/NeteaseCloudMusicApi)（感谢提供的网易云api）

