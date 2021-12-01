package love.simbot.example.listener.GroupListeners;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;

import java.util.Random;

@Beans
public class Eat {

    @OnGroup
    @Filter(value = "Bot今天吃什么",matchType = MatchType.EQUALS)
    public void WhereToEat(GroupMsg msg, Sender sender){
        String[] location = new String[]{"云一","云二","星北一","星北二","星南一","星南二","星南三","外卖"};
        int num = location.length;
        Random random = new Random();
        int index = random.nextInt(num);
        System.out.println(index);
        sender.sendGroupMsg(msg,location[index]);
    }
}
