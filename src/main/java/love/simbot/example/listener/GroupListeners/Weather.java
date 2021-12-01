package love.simbot.example.listener.GroupListeners;

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
public class Weather {
    @OnGroup
    @Filter(value = "天气预报",matchType = MatchType.STARTS_WITH)
    public void Weather(GroupMsg msg, Sender sender){
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
