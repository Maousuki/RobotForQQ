package love.simbot.example.listener.GroupListeners;

import com.alibaba.fastjson.JSONObject;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.filter.MatchType;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import javax.net.ssl.HostnameVerifier;


@Beans
public class Asoul_pictures {

    @OnGroup
    @Filter(value = "%asoul", matchType = MatchType.EQUALS)
    public void pictures(GroupMsg msg, Sender sender){

        StringBuffer result = new StringBuffer();

        try {
            HttpClient client = getHttpClient();

            HttpGet request = new HttpGet("https://api.asoul.cloud:8000/getRandomPic");
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line;
            while((line = in.readLine()) != null){
                result.append(line);
            }
            in.close();
            System.out.println(result.toString());

            JSONObject res = JSONObject.parseObject(result.toString());
            String img = res.getString("img");
            String url = res.getString("dy_url");
            System.out.println(img);

            String cat = "[CAT:image,file=" + img + ",flash=false]";
            sender.sendGroupMsg(msg,cat + "\r\n" + url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绕过SSL证书
     */
    private static HttpClient getHttpClient() throws Exception {
        // use the TrustSelfSignedStrategy to allow Self Signed Certificates
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        // we can optionally disable hostname verification.
        // if you don't want to further weaken the security, you don't have to include this.
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
        // and allow all hosts verifier.
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

        // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
        HttpClient client = HttpClients
                .custom()
                .setSSLSocketFactory(connectionFactory)
                .build();

        return client;
    }

}
