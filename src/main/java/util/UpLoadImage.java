package util;

import com.alibaba.fastjson.JSONObject;
import entity.AccessToken;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by
 *
 * @author :   zj
 * @date :   2018-03-22
 */

public class UpLoadImage {
    public static final String TEMP_UPLOAD_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
    public static final String PERMANENT_UPLOAD_MATERIAL_URL = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";

    /**
     * 新增临时素材
     *
     * @param filePath    文件路径
     * @param accessToken 公众号接口唯一凭证
     * @param type        消息类型
     * @return
     * @throws Exception
     */
    public static String uploadFile(String URL, String filePath, String accessToken, String type) throws Exception {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在！");
        }

        String url = URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
        URL urlObj = new URL(url);

        // 创建Http连接
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);   // 使用post提交需要设置忽略缓存

        //消息请求头信息
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");    // 必须多两道线
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition:form-data;name=\"media\";filename=\"" + file.getName() + "\";filelength=\"" + file.length() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        // 创建输出流
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        // 获得输出流表头
        out.write(head);

        //文件正文部分
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024 * 1024 * 10]; // 10M
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        //结尾
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
        out.write(foot);
        out.flush();
        out.close();


        // 获取响应
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;
        try {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }

        JSONObject json = JSONObject.parseObject(result);
        System.out.println(json);
        String mediaId = "";
        if (!type.equals("image")) {
            mediaId = json.getString(type + "_media_id");
        } else {
            mediaId = json.getString("media_id");
        }
        return mediaId;
    }

    public static void main(String[] args) {
        try {
            AccessToken accessToken = WeChatUtil.getAccessToken();
            System.out.println("accessToken:" + accessToken.getToken());
            System.out.println("time:" + accessToken.getExpiresIn());
            String url = TEMP_UPLOAD_MATERIAL_URL.replace("ACCESS_TOKEN", accessToken.getToken()).replace("TYPE", "imge");
//            String mediaId = uploadFile(TEMP_UPLOAD_MATERIAL_URL,"/Users/mac/Documents/JavaProjects_git/WeChatTest/src/main/webapp/images/test2.jpg", accessToken.getToken(), "image");
//            System.out.println("临时素材, mediaId=" + mediaId);
            String mediaId2 = uploadFile(PERMANENT_UPLOAD_MATERIAL_URL, "/Users/mac/Documents/JavaProjects_git/WeChatTest/src/main/webapp/images/test3.jpg", accessToken.getToken(), "image");
            System.out.println("永久素材, mediaId2=" + mediaId2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
