package love.simbot.example.listener;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;

@Beans
public class MyPrivateListener {

    @OnPrivate
    public void fudu(PrivateMsg msg, Sender sender){

        sender.sendPrivateMsg(msg,msg.getMsgContent());
    }
}
