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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Beans
public class BiliBili {

    Map<Integer, String> liveList = new HashMap<>();   //<id,name>
    Map<Integer, String> dynamicList = new HashMap<>();   //<id,name>
    Map<String, String > prelist = new HashMap<>();    //<name,pre>用于监听动态时判重
    Map<String, Integer> flagList = new HashMap<>();    //<name,flag>用于退出第一次循环

    /**
     * 直播通知
     *
     * @param msg
     * @param sender
     * @throws InterruptedException
     */
    @OnGroup
    @Filter(value = "开始监听直播", matchType = MatchType.EQUALS)
    public void live(GroupMsg msg, Sender sender) throws InterruptedException {

        sender.sendGroupMsg(msg, "已启动哔哩哔哩直播监听");

        int count = 0;

        while (true) {

            if (liveList.size() == 0) {
                sender.sendGroupMsg(msg, "暂未订阅UP主！");
                break;
            }

            for (Map.Entry<Integer, String> entry : liveList.entrySet()) {
                Integer id = entry.getKey();
                String name = entry.getValue();


                StringBuffer result = new StringBuffer();
                try {
                    URL url = new URL("https://api.bilibili.com/x/space/acc/info?mid=" + id.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line;
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                    in.close();
                    System.out.println(result.toString());

                    JSONObject res = JSONObject.parseObject(result.toString());
                    JSONObject data = res.getJSONObject("data");
                    JSONObject live_room = data.getJSONObject("live_room");
                    Integer liveStatus = live_room.getInteger("liveStatus");

                    if (liveStatus == 1 && count == 0) {
                        String title = live_room.getString("title");
                        String liveURL = live_room.getString("url");
                        String content = name + " 开播啦！" + "\r\n";
                        content = content + title + "\r\n";
                        content = content + liveURL;

                        sender.sendGroupMsg(msg, content);

                        count++;
                    }

                    if (liveStatus == 0) {
                        count = 0;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Thread.sleep(300000);

            }
        }
    }


    /**
     * 动态更新通知
     *
     * @param msg
     * @param sender
     * @throws InterruptedException
     */
    @OnGroup
    @Filter(value = "开始监听动态", matchType = MatchType.EQUALS)
    public void dynamic(GroupMsg msg, Sender sender) throws InterruptedException {

        sender.sendGroupMsg(msg, "已启动哔哩哔哩动态监听");

        while (true) {

            if (dynamicList.size() == 0) {
                sender.sendGroupMsg(msg, "暂未订阅UP主！");
                break;
            }

            for (Map.Entry<Integer, String> entry : dynamicList.entrySet()) {
                Integer id = entry.getKey();
                String name = entry.getValue();

                StringBuffer result = new StringBuffer();
                try {
                    URL url = new URL("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?host_uid=" + id.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line;
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                    in.close();
                    System.out.println(result.toString());

                    JSONObject res = JSONObject.parseObject(result.toString());
                    JSONObject data = res.getJSONObject("data");
                    JSONArray cards = data.getJSONArray("cards");
                    JSONObject obj = cards.getJSONObject(0);
                    String card = obj.getString("card");
                    String tmp = card;
                    tmp = tmp.replaceAll("[^\\u4e00-\\u9fa5]", ""); //只需要返回值里的中文部分来判重，字符部分每次都可能不一样，不好判重

                    if (flagList.get(name) == 0) {
                        flagList.put(name,1);
                        prelist.put(name,tmp);
                        continue;
                    }

                    if (tmp.equals(prelist.get(name))) {
                        continue;
                    } else {
                        JSONObject cardRes = JSONObject.parseObject(card);
                        if (cardRes.getJSONObject("item") == null) {
                            //发视频动态
                            String title = cardRes.getString("title");
                            String short_link = cardRes.getString("short_link");
                            sender.sendGroupMsg(msg, name + " 发视频啦！" + "\r\n" + title + "\r\n" + short_link);
                        } else if (cardRes.getJSONObject("origin") != null) {
                            JSONObject itemInfo = cardRes.getJSONObject("item");
                            String comment = itemInfo.getString("content"); //获取转发时的转发语
                            //转发视频
                            JSONObject origin = cardRes.getJSONObject("origin");
                            //获取被转发人信息
                            JSONObject origin_user = cardRes.getJSONObject("origin_user");
                            JSONObject info = origin_user.getJSONObject("info");
                            String uname = info.getString("uname");

                            if (origin.getString("title") != null) {
                                String title = origin.getString("title");
                                String short_link = origin.getString("short_link");
                                sender.sendGroupMsg(msg, name + "\r\n" + comment + "\r\n" + "转发自 " + uname + "\r\n" + title + "\r\n" + short_link);
                            } else {
                                JSONObject item = origin.getJSONObject("item");
                                if (item.getString("content") != null) {
                                    //转发纯文本
                                    String content = item.getString("content");
                                    sender.sendGroupMsg(msg, name + "\r\n" + comment + "\r\n" + "转发自 " + uname + "\r\n" + content);
                                } else {
                                    //转发带图片的动态
                                    String description = item.getString("description");
                                    JSONArray pictures = item.getJSONArray("pictures");
                                    ArrayList<String> imgList = new ArrayList<>();  //存储图片URL
                                    for (int i = 0; i < pictures.size(); i++) {
                                        JSONObject object = pictures.getJSONObject(i);
                                        String img_src = object.getString("img_src");
                                        imgList.add(img_src);
                                    }
                                    StringBuffer content = new StringBuffer();
                                    //String cat = "[CAT:image,file=" + img + ",flash=false]"
                                    content.append(name + "\r\n" + comment + "\r\n" + "转发自 " + uname + "\r\n" + description + "\r\n");
                                    for (String src : imgList) {
                                        String cat = "[CAT:image,file=" + src + ",flash=false]";
                                        content.append(cat + "\r\n");
                                    }
                                    sender.sendGroupMsg(msg, content.toString());
                                }
                            }
                        } else {
                            JSONObject item = cardRes.getJSONObject("item");
                            if (item.getString("content") == null) {
                                //发带图片的动态
                                String description = item.getString("description");
                                JSONArray pictures = item.getJSONArray("pictures");
                                JSONObject picture = pictures.getJSONObject(0);
                                String img_src = picture.getString("img_src");
                                String cat = "[CAT:image,file=" + img_src + ",flash=false]";
                                sender.sendGroupMsg(msg, name + " 发动态啦！" + "\r\n" + description + "\r\n" + cat);
                            } else {
                                //发纯文本动态
                                String content = item.getString("content");
                                sender.sendGroupMsg(msg, name + " 发动态啦！" + "\r\n" + content);
                            }
                        }
                    }
                    prelist.put(name,tmp);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Thread.sleep(120000);
        }
    }

    /**
     * 添加直播订阅
     *
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "添加直播订阅", matchType = MatchType.STARTS_WITH)
    public void addLive(GroupMsg msg, Sender sender) {
        String text = msg.getText();
        String[] split = text.split("/");
        String liveID = split[3];
        int id = 0;
        try {
            id = Integer.parseInt(liveID);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL("https://api.bilibili.com/x/space/acc/info?mid=" + liveID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();
            System.out.println(result.toString());

            JSONObject res = JSONObject.parseObject(result.toString());
            JSONObject data = res.getJSONObject("data");
            JSONObject live_room = data.getJSONObject("live_room");
            Integer roomStatus = live_room.getInteger("roomStatus");
            Integer code = res.getInteger("code");

            if (roomStatus == 0) {
                sender.sendGroupMsg(msg, "该用户暂未开通直播间！");
            } else {
                if (code == -404) {
                    sender.sendGroupMsg(msg, "该用户不存在！");
                } else if (code == -400) {
                    sender.sendGroupMsg(msg, "请输入含有正确id的URL！");
                } else {
                    String name = data.getString("name");
                    if (liveList.containsKey(id)) {
                        sender.sendGroupMsg(msg, "该用户您已订阅！");
                    } else {
                        liveList.put(id, name);
                        sender.sendGroupMsg(msg, "成功订阅 " + name + " 的直播通知！");
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加动态订阅
     *
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "添加动态订阅", matchType = MatchType.STARTS_WITH)
    public void addDynamic(GroupMsg msg, Sender sender) {
        String text = msg.getText();
        String[] split = text.split("/");
        String dynamicID = split[3];
        int id = 0;
        try {
            id = Integer.parseInt(dynamicID);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL("https://api.bilibili.com/x/space/acc/info?mid=" + dynamicID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();
            System.out.println(result.toString());

            JSONObject res = JSONObject.parseObject(result.toString());
            Integer code = res.getInteger("code");
            if (code == -404) {
                sender.sendGroupMsg(msg, "该用户不存在！");
            } else if (code == -400) {
                sender.sendGroupMsg(msg, "请输入含有正确id的URL！");
            } else {
                JSONObject data = res.getJSONObject("data");
                String name = data.getString("name");
                if (dynamicList.containsKey(id)) {
                    sender.sendGroupMsg(msg, "该用户您已订阅！");
                } else {
                    dynamicList.put(id, name);
                    prelist.put(name,"");
                    flagList.put(name,0);
                    sender.sendGroupMsg(msg, "成功订阅 " + name + " 的动态通知！");
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询订阅列表
     *
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "查询订阅列表", matchType = MatchType.EQUALS)
    public void search(GroupMsg msg, Sender sender) {
        StringBuffer live = new StringBuffer();
        StringBuffer dynamic = new StringBuffer();

        live.append("当前直播订阅列表：" + "\r\n");
        dynamic.append("当前动态订阅列表：" + "\r\n");
        int liveCount = 1;
        int dynamicCount = 1;

        for (Map.Entry<Integer, String> entry : liveList.entrySet()) {
            String name = entry.getValue();
            live.append(liveCount + "." + name + "\r\n");
            ++liveCount;
        }
        live.append("\r\n");

        for (Map.Entry<Integer, String> entry : dynamicList.entrySet()) {
            String name = entry.getValue();
            dynamic.append(dynamicCount + "." + name + "\r\n");
            ++dynamicCount;
        }

        sender.sendGroupMsg(msg, live.toString() + dynamic.toString());
    }


    /**
     * 删除直播订阅
     *
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "删除直播订阅", matchType = MatchType.STARTS_WITH)
    public void liveDelete(GroupMsg msg, Sender sender) {
        String text = msg.getText();
        text = text.replace(" ", "");    //确保有无空格都能识别
        String name = text.substring(6, text.length());

        Collection<String> names = liveList.values();
        if (names.contains(name)) {
            names.remove(name);
            sender.sendGroupMsg(msg, "删除成功！");
        } else {
            sender.sendGroupMsg(msg, "该用户不在订阅列表中！");
        }
    }

    /**
     * 删除动态订阅
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "删除动态订阅", matchType = MatchType.STARTS_WITH)
    public void dynamicDelete(GroupMsg msg, Sender sender) {
        String text = msg.getText();
        text = text.replace(" ", "");    //确保有无空格都能识别
        String name = text.substring(6, text.length());

        Collection<String> names = dynamicList.values();
        if (names.contains(name)) {
            names.remove(name);
            prelist.remove(name);
            flagList.remove(name);
            sender.sendGroupMsg(msg, "删除成功！");
        } else {
            sender.sendGroupMsg(msg, "该用户不在订阅列表中！");
        }
    }
}
