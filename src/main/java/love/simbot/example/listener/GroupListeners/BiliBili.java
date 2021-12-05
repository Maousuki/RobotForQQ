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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import data.Sqlite;

@Beans
public class BiliBili {

    public Connection con() {
        return new Sqlite().connect();
    }

    Connection sqlCon = con();

    /**
     * 直播通知
     *
     * @param msg
     * @param sender
     * @throws InterruptedException
     */
    @OnGroup
    @Filter(value = "%开始监听直播", matchType = MatchType.EQUALS)
    public void live(GroupMsg msg, Sender sender) throws InterruptedException {

        sender.sendGroupMsg(msg, "已启动哔哩哔哩直播监听");

        while (true) {
            String sql = "select * from Live";
            try {
                PreparedStatement pstSearch = sqlCon.prepareStatement(sql);
                ResultSet rs = pstSearch.executeQuery();
                if (rs.isClosed()) {
                    sender.sendGroupMsg(msg, "当前暂未订阅UP主！");
                    break;
                } else {
                    while (rs.next()) {
                        long GroupID = rs.getLong("GroupID");
                        Integer UpID = rs.getInt("UpID");
                        String name = rs.getString("Name");

                        StringBuffer result = new StringBuffer();
                        URL url = new URL("https://api.bilibili.com/x/space/acc/info?mid=" + UpID.toString());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        String line;
                        while ((line = in.readLine()) != null) {
                            result.append(line);
                        }
                        in.close();
//                        System.out.println(result.toString());

                        JSONObject res = JSONObject.parseObject(result.toString());
                        JSONObject data = res.getJSONObject("data");
                        JSONObject live_room = data.getJSONObject("live_room");
                        Integer liveStatus = live_room.getInteger("liveStatus");

                        String sqlFlag = "select Flag from Live where GroupID = " + GroupID + " and UpID = " + UpID;
                        PreparedStatement pstFlag = sqlCon.prepareStatement(sqlFlag);
                        ResultSet rsFlag = pstFlag.executeQuery();
                        int flag = rsFlag.getInt("Flag");

                        if (liveStatus == 1 && flag == 0) {
                            String title = live_room.getString("title");
                            String liveURL = live_room.getString("url");
                            String content = name + " 开播啦！" + "\r\n";
                            content = content + title + "\r\n";
                            content = content + liveURL;

                            ++flag;

                            String flagUD = "update Live set Flag = " + flag + " where GroupID = " + GroupID + " and UpID = " + UpID;
                            PreparedStatement pstUD = sqlCon.prepareStatement(flagUD);
                            pstUD.executeUpdate();

                            sender.sendGroupMsg(GroupID, content);
                        }

                        if (liveStatus == 0) {
                            String flagRe = "update Live set Flag = 0 where GroupID = " + GroupID + " and UpID = " + UpID;
                            PreparedStatement pstRe = sqlCon.prepareStatement(flagRe);
                            pstRe.executeUpdate();
                        }

                        Thread.sleep(3000);

                    }

                    Thread.sleep(120000);

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
    @Filter(value = "%开始监听动态", matchType = MatchType.EQUALS)
    public void dynamic(GroupMsg msg, Sender sender) throws InterruptedException {

        sender.sendGroupMsg(msg, "已启动哔哩哔哩动态监听");

//        String GroupID = msg.getGroupInfo().getGroupCode();

        String sqlRe = "select * from Dynamic";
        try {
            PreparedStatement pstRe = sqlCon.prepareStatement(sqlRe);
            ResultSet rsRe = pstRe.executeQuery();
            if (rsRe.isClosed()){
                sender.sendGroupMsg(msg, "当前暂未订阅UP主！");
            }else {
                while (rsRe.next()){
                    String sqlFlag = "update Dynamic set Flag = 0 ";
                    PreparedStatement pstFlag = sqlCon.prepareStatement(sqlFlag);
                    pstFlag.executeUpdate();

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }   //每次开机时，更新数据库Flag=0，防止发送上次关机前保留的动态


        while (true) {

            String sqlSearch = "select * from Dynamic";
            try {
                PreparedStatement pstSearch = sqlCon.prepareStatement(sqlSearch);
                ResultSet rs = pstSearch.executeQuery();
                if (rs.isClosed()) {
                    sender.sendGroupMsg(msg, "当前暂未订阅UP主！");
                    break;
                } else {
                    while (rs.next()) {
                        long GroupID = rs.getLong("GroupID");
                        Integer UpID = rs.getInt("UpID");
                        String name = rs.getString("Name");

                        StringBuffer result = new StringBuffer();

                        URL url = new URL("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?host_uid=" + UpID.toString());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        String line;
                        while ((line = in.readLine()) != null) {
                            result.append(line);
                        }
                        in.close();
//                        System.out.println(result.toString());

                        JSONObject res = JSONObject.parseObject(result.toString());
                        JSONObject data = res.getJSONObject("data");
                        JSONArray cards = data.getJSONArray("cards");
                        JSONObject obj = cards.getJSONObject(0);
                        String card = obj.getString("card");
                        String tmp = card;
                        tmp = tmp.replaceAll("[^\\u4e00-\\u9fa5]", ""); //只需要返回值里的中文部分来判重，字符部分每次都可能不一样，不好判重

                        String sql = "select Flag from Dynamic where GroupID = " + GroupID + " and UpID = " + UpID;
                        PreparedStatement pst = sqlCon.prepareStatement(sql);
                        ResultSet rsFlag = pst.executeQuery();
                        int flag = rsFlag.getInt("Flag");


                        if (flag == 0) {    //跳过第一次循环
                            String sqlFlag = "update Dynamic set Flag = 1 where GroupID = " + GroupID + " and UpID = " + UpID;
                            String sqlPre = "update Dynamic set PreMsg = " + "\"" + tmp + "\"" + " where GroupID = " + GroupID + " and UpID = " + UpID;
                            PreparedStatement pstFlag = sqlCon.prepareStatement(sqlFlag);
                            pstFlag.executeUpdate();
                            PreparedStatement pstPre = sqlCon.prepareStatement(sqlPre);
                            pstPre.executeUpdate();
                            continue;
                        }

                        String sqlMsg = "select PreMsg from Dynamic where GroupID = " + GroupID + " and UpID = " + UpID;
                        PreparedStatement pstMsg = sqlCon.prepareStatement(sqlMsg);
                        ResultSet rsMsg = pstMsg.executeQuery();
                        String pre = rsMsg.getString("PreMsg");

                        if (tmp.equals(pre)) {
                            continue;
                        } else {
                            JSONObject cardRes = JSONObject.parseObject(card);
                            if (cardRes.getJSONObject("item") == null) {
                                //发视频动态
                                String title = cardRes.getString("title");
                                String short_link = cardRes.getString("short_link");
                                sender.sendGroupMsg(GroupID, name + " 发视频啦！" + "\r\n" + title + "\r\n" + short_link);
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
                                    sender.sendGroupMsg(GroupID, name + "\r\n" + comment + "\r\n" + "转发自 " + uname + "\r\n" + title + "\r\n" + short_link);
                                } else {
                                    JSONObject item = origin.getJSONObject("item");
                                    if (item.getString("content") != null) {
                                        //转发纯文本
                                        String content = item.getString("content");
                                        sender.sendGroupMsg(GroupID, name + "\r\n" + comment + "\r\n" + "转发自 " + uname + "\r\n" + content);
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
                                        sender.sendGroupMsg(GroupID, content.toString());
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
                                    sender.sendGroupMsg(GroupID, name + " 发动态啦！" + "\r\n" + description + "\r\n" + cat);
                                } else {
                                    //发纯文本动态
                                    String content = item.getString("content");
                                    sender.sendGroupMsg(GroupID, name + " 发动态啦！" + "\r\n" + content);
                                }
                            }
                        }

                        String sqlUpdate = "update Dynamic set PreMsg = " + "\"" + tmp + "\"" + " where GroupID = " + GroupID + " and UpID = " + UpID;
                        PreparedStatement pstUpdate = sqlCon.prepareStatement(sqlUpdate);
                        pstUpdate.executeUpdate();
                        Thread.sleep(3000);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
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
    @Filter(value = "%添加直播订阅", matchType = MatchType.STARTS_WITH)
    public void addLive(GroupMsg msg, Sender sender) {
        String text = msg.getText();
        String[] split = text.split("/");
        String liveID = split[3];
        String GroupID = msg.getGroupInfo().getGroupCode();
        int UpID = 0;
        try {
            UpID = Integer.parseInt(liveID);
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
//            System.out.println(result.toString());

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

                    String sqlSearch = "select * from Live where GroupID = " + GroupID + " and UpID = " + liveID;
                    String sql = "insert into Live values (" + GroupID + "," + liveID + "," + "\"" + name + "\"" + ",0" +")";

                    PreparedStatement pstSearch = sqlCon.prepareStatement(sqlSearch);
                    ResultSet rs = pstSearch.executeQuery();
                    if (rs.next()) {
                        sender.sendGroupMsg(msg, "该用户您已订阅！");
                    } else {
                        PreparedStatement pst = sqlCon.prepareStatement(sql);
                        pst.executeUpdate();
                        sender.sendGroupMsg(msg, "成功订阅 " + name + " 的直播通知！");
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 添加动态订阅
     *
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "%添加动态订阅", matchType = MatchType.STARTS_WITH)
    public void addDynamic(GroupMsg msg, Sender sender) {
        String text = msg.getText();
        String[] split = text.split("/");
        String dynamicID = split[3];
        String GroupID = msg.getGroupInfo().getGroupCode();
        int UpID = 0;
        try {
            UpID = Integer.parseInt(dynamicID);
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
//            System.out.println(result.toString());

            JSONObject res = JSONObject.parseObject(result.toString());
            Integer code = res.getInteger("code");
            if (code == -404) {
                sender.sendGroupMsg(msg, "该用户不存在！");
            } else if (code == -400) {
                sender.sendGroupMsg(msg, "请输入含有正确id的URL！");
            } else {
                JSONObject data = res.getJSONObject("data");
                String name = data.getString("name");

                String sqlSearch = "select * from Dynamic where GroupID = " + GroupID + " and UpID = " + dynamicID;
                String sql = "insert into Dynamic values (" + GroupID + "," + dynamicID + "," + "\"" + name + "\"" + ",0" + ",null" + ")";

                PreparedStatement pstSearch = sqlCon.prepareStatement(sqlSearch);
                ResultSet rs = pstSearch.executeQuery();
                if (rs.next()) {
                    sender.sendGroupMsg(msg, "该用户您已订阅！");
                } else {
                    PreparedStatement pst = sqlCon.prepareStatement(sql);
                    pst.executeUpdate();
                    sender.sendGroupMsg(msg, "成功订阅 " + name + " 的动态通知！");
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /**
     * 查询订阅列表
     *
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "%查询订阅列表", matchType = MatchType.EQUALS)
    public void search(GroupMsg msg, Sender sender) {

        String GroupID = msg.getGroupInfo().getGroupCode();

        StringBuffer live = new StringBuffer();
        StringBuffer dynamic = new StringBuffer();

        live.append("当前直播订阅列表：" + "\r\n");
        dynamic.append("当前动态订阅列表：" + "\r\n");
        int liveCount = 1;
        int dynamicCount = 1;

        String sqlDynamic = "select UpID,Name from Dynamic where GroupID = " + GroupID;
        String sqlLive = "select UpID,Name from Live where GroupID = " + GroupID;
        try {
            PreparedStatement pstDynamic = sqlCon.prepareStatement(sqlDynamic);
            PreparedStatement pstLive = sqlCon.prepareStatement(sqlLive);
            ResultSet rsDynamic = pstDynamic.executeQuery();
            ResultSet rsLive = pstLive.executeQuery();
            while (rsDynamic.next()) {
                dynamic.append(dynamicCount + "." + rsDynamic.getString("Name") + "\r\n");
                ++dynamicCount;
            }
            while (rsLive.next()) {
                live.append(liveCount + "." + rsLive.getString("Name") + "\r\n");
                ++liveCount;
            }
            live.append("\r\n");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
    @Filter(value = "%删除直播订阅", matchType = MatchType.STARTS_WITH)
    public void liveDelete(GroupMsg msg, Sender sender) {
        String text = msg.getText();
        text = text.replace(" ", "");    //确保有无空格都能识别
        String name = text.substring(7, text.length());
        String GroupID = msg.getGroupInfo().getGroupCode();

        String sqlSearch = "select * from Live where GroupID = " + GroupID + " and name = " + "\"" + name + "\"";
        String sql = "delete from Live where GroupID = " + GroupID + " and name = " + "\"" + name + "\"";
        try {
            PreparedStatement pstSearch = sqlCon.prepareStatement(sqlSearch);
            ResultSet rs = pstSearch.executeQuery();
            if (rs.isClosed()) {
                sender.sendGroupMsg(msg, "该用户不在订阅列表中！");
            } else {
                PreparedStatement pst = sqlCon.prepareStatement(sql);
                pst.executeUpdate();
                sender.sendGroupMsg(msg, "删除成功！");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * 删除动态订阅
     *
     * @param msg
     * @param sender
     */
    @OnGroup
    @Filter(value = "%删除动态订阅", matchType = MatchType.STARTS_WITH)
    public void dynamicDelete(GroupMsg msg, Sender sender) {
        String text = msg.getText();
        text = text.replace(" ", "");    //确保有无空格都能识别
        String name = text.substring(7, text.length());
        String GroupID = msg.getGroupInfo().getGroupCode();

        String sqlSearch = "select * from Dynamic where GroupID = " + GroupID + " and name = " + "\"" + name + "\"";
        String sql = "delete from Dynamic where GroupID = " + GroupID + " and name = " + "\"" + name + "\"";
        try {
            PreparedStatement pstSearch = sqlCon.prepareStatement(sqlSearch);
            ResultSet rs = pstSearch.executeQuery();
            if (rs.isClosed()) {
                sender.sendGroupMsg(msg, "该用户不在订阅列表中！");
            } else {
                PreparedStatement pst = sqlCon.prepareStatement(sql);
                pst.executeUpdate();
                sender.sendGroupMsg(msg, "删除成功！");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
