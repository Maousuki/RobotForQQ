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
public class Music {

    @OnGroup
    @Filter(value = "%点歌",matchType = MatchType.STARTS_WITH)
    public void music(GroupMsg msg, Sender sender){
        String text = msg.getText();
        text = text.replace(" ", "");
        String song = text.substring(3,text.length());
        System.out.println(song);

        StringBuffer result = new StringBuffer();
        try {

            //获取歌曲id
            URL url = new URL("https://v2.alapi.cn/api/music/search?token=oEwGnsA0egFaMCJL&type=1&keyword=" + URLEncoder.encode(song,"utf-8"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while((line = in.readLine()) != null){
                result.append(line);
            }
            in.close();
//            System.out.println(result.toString());

            JSONObject jsonRes = JSONObject.parseObject(result.toString());
            JSONObject data = jsonRes.getJSONObject("data");
            JSONArray songs = data.getJSONArray("songs");
            JSONObject obj = songs.getJSONObject(0);
            Integer id = (Integer) obj.get("id");
            System.out.println(id);


            //获取歌曲封面、歌曲名、歌手名
            URL urlJPG = new URL("https://netease-cloud-music-api-green-two.vercel.app/song/detail?ids=" + id.toString());
            HttpURLConnection conJPG = (HttpURLConnection) urlJPG.openConnection();
            in = new BufferedReader(new InputStreamReader(conJPG.getInputStream()));

            StringBuffer resultJPG = new StringBuffer();
            String lineJPG;
            while((lineJPG = in.readLine()) != null){
                resultJPG.append(lineJPG);
            }
            in.close();
//            System.out.println(resultJPG.toString());

            JSONObject jsonResSong = JSONObject.parseObject(resultJPG.toString());
            JSONArray songInfo = jsonResSong.getJSONArray("songs");
            JSONObject info = songInfo.getJSONObject(0);
            String name = info.getString("name");   //获取歌曲名
            JSONArray ar = info.getJSONArray("ar");
            String singers = "";
            for (int i = 0; i < ar.size(); i++) {
                JSONObject singerInfo = ar.getJSONObject(i);
                String singer = singerInfo.getString("name");
                singers += singer + "/";
            }
            String summary = singers.substring(0, singers.length()-1); //获取歌手名
            JSONObject al = info.getJSONObject("al");
            String picUrl = al.getString("picUrl");



            //获取歌曲url
            URL urlSong = new URL("https://v2.alapi.cn/api/music/url?token=oEwGnsA0egFaMCJL&format=json&id=" + id.toString());
            HttpURLConnection conSong = (HttpURLConnection) urlSong.openConnection();
            in = new BufferedReader(new InputStreamReader(conSong.getInputStream()));

            StringBuffer resultSong = new StringBuffer();
            String lineSong;
            while((lineSong = in.readLine()) != null){
                resultSong.append(lineSong);
            }
            in.close();
//            System.out.println(resultSong.toString());

            JSONObject urlRes = JSONObject.parseObject(resultSong.toString());
            JSONObject dataURL = urlRes.getJSONObject("data");
            String url1 = dataURL.getString("url");


            //[CAT:music,type=neteaseCloud,musicUrl=http://xxxx.mp3]
            String urlm = "https://y.music.163.com/m/song?id=" + id + "&userid=95431381#?thirdfrom=qq";
            String cat = "[CAT:music,type=neteaseCloud,musicUrl=" + url1 + ",pictureUrl=" + picUrl +",jumpUrl=" + urlm + ",title=" + name + ",summary="+ summary + "]";
            sender.sendGroupMsg(msg,cat);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
