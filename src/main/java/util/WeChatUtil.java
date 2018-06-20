package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import entity.AccessToken;
import entity.menu.Button;
import entity.menu.ClickButton;
import entity.menu.Menu;
import entity.menu.ViewButton;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-21
 */

public class WeChatUtil {
    // 可以直接访问url:  https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx2717b7180b4856e6&secret=fc73041ce88392e639b1c5b3480cc24a"
    private static final String APPID = "wx46f7532e4f7241ec";                   // 公众号的全局唯一接口调用凭据
    private static final String APPSECRET = "1c34da9f47c89a615ce195b1aaef955a"; // 第三方用户唯一凭证密钥，即appsecret
    // 访问格式
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";  // 请求接口地址
    private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";   // 菜单组装的请求接口地址


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
        HttpPost httpPost = new HttpPost(url);                           // HttpPost将使用post方式发送请求URL
        httpPost.setEntity(new StringEntity(outStr, "UTF-8"));   // 使用setEntity方法，将我们传进来的参数放入请求中
        HttpResponse response = client.execute(httpPost);                // 使用HttpResponse接收client执行httpost的结果
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");//HttpEntity转为字符串类型
        return JSON.parseObject(result);
    }
//    public static JSONObject doPostStr(String url,String outStr) throws ClientProtocolException, IOException {
//        DefaultHttpClient client = new DefaultHttpClient();//获取DefaultHttpClient请求
//        HttpPost httpost = new HttpPost(url); //HttpPost将使用Get方式发送请求URL
//        JSONObject jsonObject = null;
//        httpost.setEntity(new StringEntity(outStr,"UTF-8"));//使用setEntity方法，将我们传进来的参数放入请求中
//        HttpResponse response = client.execute(httpost);//使用HttpResponse接收client执行httpost的结果
//        String result = EntityUtils.toString(response.getEntity(),"UTF-8");//HttpEntity转为字符串类型
//        jsonObject = JSONObject.parseObject(result);//字符串类型转为JSON类型
//        return jsonObject;
//    }
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


    // 组装菜单
    public static Menu initMenu() {
        Menu menu = new Menu();
        ClickButton button11 = new ClickButton();
        button11.setKey("11");
        button11.setName("了解ZHANG");
        button11.setType("click");

        ClickButton button12 = new ClickButton();
        button12.setKey("12");
        button12.setName("加入ZHANG");
        button12.setType("click");

        ViewButton button21 = new ViewButton();
        button21.setName("ZHANG官网");
        button21.setType("view");
        button21.setUrl("http://www.baidu.com");

        ViewButton button22 = new ViewButton();
        button22.setName("ZHANG新闻网");
        button22.setType("view");
        button22.setUrl("http://www.baidu.com");

        ClickButton button31 = new ClickButton();
        button31.setName("BAIDU");
        button31.setType("click");
        button31.setKey("31");

        Button button1 = new Button();
        button1.setName("ZHANG"); //将11/12两个button作为二级菜单封装第一个一级菜单
        button1.setSub_button(new Button[]{button11, button12});

        Button button2 = new Button();
        button2.setName("相关网址"); //将21/22两个button作为二级菜单封装第二个二级菜单
        button2.setSub_button(new Button[]{button21, button22});

        menu.setButton(new Button[]{button1, button2, button31});// 将31Button直接作为一级菜单
        return menu;
    }

    public static String createMenu(String token, String menu) throws ClientProtocolException, IOException {
        String result = "";
        String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = doPostStr(url, menu);
        if (jsonObject != null) {
            result = jsonObject.getString("errcode");
        }
        return result;
    }


    // 测试: 获取Token
    public static void main(String[] args) {
        try {
            AccessToken accessToken = WeChatUtil.getAccessToken();
            System.out.println("accessToken:" + accessToken.getToken());
            System.out.println("time:" + accessToken.getExpiresIn());
//            String path = "/Users/mac/Documents/JavaProjects_git/WeChatTest/src/main/webapp/images/test2.jpg";
//            String mediaId = UpLoadImage.uploadFile(path, accessToken.getToken(), "image");
            String path = "/Users/mac/Documents/JavaProjects_git/WeChatTest/src/main/webapp/images/test3.jpg";
            String mediaId = UpLoadImage.uploadFile(UpLoadImage.TEMP_UPLOAD_MATERIAL_URL,path, accessToken.getToken(), "thumb");
            System.out.println("mediaId:" + mediaId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
