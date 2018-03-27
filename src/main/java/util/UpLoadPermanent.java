package util;

import entity.AccessToken;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-23
 */

public class UpLoadPermanent {
    public static String postFile(String url, String filePath, String title, String introduction) throws Exception {
        File file = new File(filePath);
        if (!file.exists())
            return null;
        String result = null;
        try {

            URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            String boundary = "-----------------------------" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream output = conn.getOutputStream();
            output.write(("--" + boundary + "\r\n").getBytes());
            output.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n", file.getName()).getBytes());
            output.write("Content-Type: video/mp4 \r\n\r\n".getBytes());
            byte[] data = new byte[1024];
            int len = 0;
            FileInputStream input = new FileInputStream(file);
            while ((len = input.read(data)) > -1) {
                output.write(data, 0, len);
            }
            output.write(("--" + boundary + "\r\n").getBytes());
            output.write("Content-Disposition: form-data; name=\"description\";\r\n\r\n".getBytes());
            output.write(String.format("{\"title\":\"%s\", \"introduction\":\"%s\"}", title, introduction).getBytes());
            output.write(("\r\n--" + boundary + "--\r\n\r\n").getBytes());
            output.flush();
            output.close();
            input.close();
            InputStream resp = conn.getInputStream();
            StringBuffer sb = new StringBuffer();
            while ((len = resp.read(data)) > -1)
                sb.append(new String(data, 0, len, "utf-8"));
            resp.close();
            result = sb.toString();
            System.out.println(result);
        } catch (IOException e) {
            System.out.println("postFile数据传输失败");
        } catch (Exception e) {
            System.out.println("postFile，不支持http协议");
        }
        return result;
    }

    private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";

    public static void main(String[] args) {
        try {
            AccessToken accessToken = WeChatUtil.getAccessToken();
            System.out.println("accessToken:" + accessToken.getToken());
            System.out.println("time:" + accessToken.getExpiresIn());
            String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken.getToken()).replace("TYPE", "imge");
            postFile(url, "/Users/mac/Documents/JavaProjects_git/WeChatTest/src/main/webapp/images/test.png", "test", "dfd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
