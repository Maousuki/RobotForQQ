package love.simbot.example.listener.GroupListeners;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;


@Beans
public class BasicFunc {

    @OnGroup
    @Filter(value = "help",matchType = MatchType.EQUALS)
    public void func(GroupMsg msg, Sender sender){
        String func = "当前支持功能有：" + "\r\n" +
                "1.查询城市天气  命令：天气预报+城市名" + "\r\n" +
                "2.玩骰子  命令：玩骰子" + "\r\n" +
                "3.今天吃什么 命令：Bot今天吃什么" + "\r\n" +
                "4.显示群信息  命令：显示群信息" + "\r\n" +
                "5.显示发送人信息  命令：发送人信息" + "\r\n" +
                "6.显示热榜 目前支持微博、微信、百度、新浪、历史上的今天、哔哩哔哩、抖音  命令：热榜+APP名" + "\r\n" +
                "7.点歌（源自网易云） 命令： 点歌+歌名（返回为搜索结果的第一首歌曲，为了增加准确率建议歌名后加上歌手名)" + "\r\n" +
                "8.随机生成Asoul二创图片 命令：asoul" + "\r\n" +
                "9.订阅B站UP主直播提醒 命令：添加直播订阅+UP主B站主页URL地址" + "\r\n" +
                "10.订阅B站UP主动态提醒 命令：添加动态订阅+UP主B站主页URL地址" + "\r\n" +
                "11.删除B站UP主直播/动态提醒 命令：删除直播/动态订阅+UP主名字" + "\r\n" +
                "12.查询订阅列表 命令：查询订阅列表";

        sender.sendGroupMsg(msg, func);

    }

    @OnGroup
    @Filter(value = "显示群信息",matchType = MatchType.EQUALS)
    public void GroupInfo(GroupMsg msg, Sender sender){
        sender.sendGroupMsg(msg, "群号码为"+ msg.getGroupInfo().getGroupCode());
        sender.sendGroupMsg(msg, "群名为" + msg.getGroupInfo().getGroupName());

    }

    @OnGroup
    @Filter(value = "发送人信息",matchType = MatchType.EQUALS)
    public void SenderInfo(GroupMsg msg, Sender sender){
        sender.sendGroupMsg(msg,"昵称："+ msg.getAccountInfo().getAccountNickname());
        sender.sendGroupMsg(msg,"头像："+ msg.getAccountInfo().getAccountAvatar());
        sender.sendGroupMsg(msg,"QQ号："+ msg.getAccountInfo().getAccountCode());
        sender.sendGroupMsg(msg,"群备注："+ msg.getAccountInfo().getAccountRemark());
        long lastSpeakTime = msg.getAccountInfo().getLastSpeakTime();
    }


    /**
     * 玩骰子
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "玩骰子",matchType = MatchType.EQUALS)
    public void dice(GroupMsg msg, Sender sender){
        //[CAT:dice,value=5]
        //[CAT:dice,random=true]
        String dice = "[CAT:dice,random=true]";
        sender.sendGroupMsg(msg,dice);
    }
    @OnGroup
    @Filter(value = "嗨幕我",matchType = MatchType.EQUALS)
    public void dice6(GroupMsg msg, Sender sender){
        //[CAT:dice,value=5]
        if (msg.getAccountInfo().getAccountCode().equals("869839607")){
            String dice = "[CAT:dice,value=6]";
            sender.sendGroupMsg(msg,dice);
        }else {
            sender.sendGroupMsg(msg,"权限不够，无法嗨幕你");
        }
    }




}
