package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import entity.AccessToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-21
 */

public class WeiChatUtil {
    // 可以直接访问url:  https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx2717b7180b4856e6&secret=fc73041ce88392e639b1c5b3480cc24a"
    private static final String APPID = "wx2717b7180b4856e6";                   // 第三方用户唯一凭证
    private static final String APPSECRET = "fc73041ce88392e639b1c5b3480cc24a"; // 第三方用户唯一凭证密钥，即appsecret
    // 访问格式
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";  // 请求接口地址

    /**
     * 编写Get请求的方法。但没有参数传递的时候，可以使用Get请求
     *
     * @param url 需要请求的URL
     * @return 将请求URL后返回的数据，转为JSON格式，并return
     */
    public static JSONObject doGetStr(String url) throws ClientProtocolException, IOException {
        HttpClient client = HttpClientBuilder.create().build(); // 获取DefaultHttpClient请求
        HttpGet httpGet = new HttpGet(url);                     // HttpGet将使用Get方式发送请求URL
        HttpResponse response = client.execute(httpGet);        // 使用HttpResponse接收client执行httpGet的结果
        HttpEntity entity = response.getEntity();               // 从response中获取结果，类型为HttpEntity
        String result = "";
        if (entity != null) {
            result = EntityUtils.toString(entity, "UTF-8"); //HttpEntity转为字符串类型(Json格式)
        }
        return JSON.parseObject(result);
    }

    /**
     * 编写Post请求的方法。当我们需要参数传递的时候，可以使用Post请求
     *
     * @param url    需要请求的URL
     * @param outStr 需要传递的参数
     * @return 将请求URL后返回的数据，转为JSON格式，并return
     */
    public static JSONObject doPostStr(String url, String outStr) throws ClientProtocolException, IOException {
        HttpClient client = HttpClientBuilder.create().build();         // 获取DefaultHttpClient请求
        HttpPost httpost = new HttpPost(url);                           // HttpPost将使用Get方式发送请求URL
        httpost.setEntity(new StringEntity(outStr, "UTF-8"));   // 使用setEntity方法，将我们传进来的参数放入请求中
        HttpResponse response = client.execute(httpost);                // 使用HttpResponse接收client执行httpost的结果
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");//HttpEntity转为字符串类型
        return JSON.parseObject(result);
    }

    /**
     * 获取AccessToken
     *
     * @return 返回拿到的access_token及有效期
     */
    public static AccessToken getAccessToken() throws ClientProtocolException, IOException {
        AccessToken token = new AccessToken();
        String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);//将URL中的两个参数替换掉
        JSONObject jsonObject = doGetStr(url);//使用刚刚写的doGet方法接收结果
        if (jsonObject != null && !jsonObject.isEmpty()) { //如果返回不为空，将返回结果封装进AccessToken实体类
            token.setToken(jsonObject.getString("access_token"));//取出access_token
            token.setExpiresIn(jsonObject.getInteger("expires_in"));//取出access_token的有效期
        }
        return token;
    }

    // 测试: 获取Token
    public static void main(String[] args) {
        try {
            AccessToken accessToken = WeiChatUtil.getAccessToken();
            System.out.println("accessToken:" + accessToken.getToken());
            System.out.println("time:" + accessToken.getExpiresIn());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
