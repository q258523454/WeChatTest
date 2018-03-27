package entity.menu;

import com.alibaba.fastjson.JSONObject;
import entity.AccessToken;
import org.apache.http.client.ClientProtocolException;
import util.WeChatUtil;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-23
 */

public class MenuTest {
    public static void main(String[] args) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
        AccessToken token = WeChatUtil.getAccessToken();
        System.out.println("Access_Token为:" + token.getToken());
        System.out.println("有效时间为：" + token.getExpiresIn());
        String menu = JSONObject.toJSONString(WeChatUtil.initMenu());
        String result = WeChatUtil.createMenu(token.getToken(), menu);
        System.out.println(result);
    }
}
