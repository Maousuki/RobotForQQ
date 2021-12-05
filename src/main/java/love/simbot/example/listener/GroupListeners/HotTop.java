package love.simbot.example.listener.GroupListeners;

import com.alibaba.fastjson.JSONArray;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

@Beans
public class HotTop {
    @OnGroup
    @Filter(value = "%热榜",matchType = MatchType.STARTS_WITH)
    public void hotTop(GroupMsg msg, Sender sender){
        String text = msg.getText();
        text = text.replace(" ","");
        String app = text.substring(3,text.length());
        System.out.println(app);

        if (app.equals("知乎")){
            app = "zhihu";
        }else if (app.equals("微博")){
            app = "weibo";
        }else if (app.equals("微信")){
            app = "weixin";
        }else if (app.equals("百度")){
            app = "baidu";
        }else if (app.equals("新浪")){
            app = "xl";
        }else if (app.equals("历史上的今天")){
            app = "hitory";
        }else if (app.equals("哔哩哔哩")){
            app = "bilibili";
        }else if (app.equals("抖音")){
            app = "douyin";
        }

        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL("https://v2.alapi.cn/api/tophub/get?token=oEwGnsA0egFaMCJL&type=" + URLEncoder.encode(app,"utf-8"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while((line = in.readLine()) != null){
                result.append(line);
            }
            in.close();
//            System.out.println(result.toString());

            String context = "";

            JSONObject jsonRes = JSONObject.parseObject(result.toString());
            JSONObject data = jsonRes.getJSONObject("data");
            String name = data.getString("name");
            String last_update = data.getString("last_update");
            JSONArray list = data.getJSONArray("list");

            context += name + "\r\n" + "上次更新时间：" +  last_update + "\r\n";

            for (int i = 0; i < 10; i++) {
                JSONObject obj = list.getJSONObject(i);
                String title = (String) obj.get("title");
                String link = (String) obj.get("link");
                String other = (String) obj.get("other");
                context += title + "\r\n" + link + "\r\n" + other + "\r\n" + "\r\n";
            }
            System.out.println(context);

            sender.sendGroupMsg(msg, context);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
