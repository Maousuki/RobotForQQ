package love.simbot.example.listener;

import com.alibaba.fastjson.JSONObject;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import java.util.Random;

@Beans
public class MyGroupListener {

    @OnGroup
    @Filter(value = "help",matchType = MatchType.EQUALS)
    public void func(GroupMsg msg, Sender sender){
        String func = "当前支持功能有：" + "\r\n" +
                "1.查询城市天气  命令：天气预报 XX(注意空格)" + "\r\n" +
                "2.玩骰子  命令：玩骰子" + "\r\n" +
                "3.今天吃什么 命令：Bot今天吃什么" + "\r\n" +
                "4.显示群信息  命令：显示群信息" + "\r\n" +
                "5.显示发送人信息  命令：发送人信息" + "\r\n" +
                "剩下功能二号再开发";

        sender.sendGroupMsg(msg, func);

    }

    @OnGroup
    @Filter(value = "我是大宽",matchType = MatchType.EQUALS)
    public void dakuan(GroupMsg msg, Sender sender){
        sender.sendGroupMsg(msg,"我是你爹");
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

    @OnGroup
    @Filter(value = "天气预报",matchType = MatchType.STARTS_WITH)
    public void music(GroupMsg msg, Sender sender){
        String text = msg.getText();
        String city = text.substring(5,text.length());
        System.out.println(city);

        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL("https://v2.alapi.cn/api/weather?token=oEwGnsA0egFaMCJL&location="+ URLEncoder.encode(city,"utf-8"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while((line = in.readLine()) != null){
                result.append(line);
            }
            in.close();
            System.out.println(result.toString());

            String context = null;

            JSONObject jsonRes = JSONObject.parseObject(result.toString());
            JSONObject data = jsonRes.getJSONObject("data");
            JSONObject now = data.getJSONObject("now");
            String cond_txt = now.getString("cond_txt"); //天气
            String tmp = now.getString("tmp");//气温
            String hum = now.getString("hum");//相对湿度
            String pres = now.getString("pres");//大气压强
            String vis = now.getString("vis");//能见度 km

            context = "城市：" + city + "\r\n" +
                    "天气：" + cond_txt + "\r\n" +
                    "气温：" + tmp + "\r\n" +
                    "相对湿度：" + hum + "%" + "\r\n" +
                    "大气压强：" + pres + "hPa" + "\r\n" +
                    "能见度：" + vis + "km";

            sender.sendGroupMsg(msg,context);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
