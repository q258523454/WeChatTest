package controller;

import entity.TextMessage;
import org.apache.log4j.Logger;
import util.CheckUtil;
import util.MessageUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WXServlet extends HttpServlet {

    private Logger logger = Logger.getLogger(WXServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        PrintWriter out = response.getWriter();
        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
            //如果校验成功，将得到的随机字符串原路返回
            out.print(echostr);
        }

    }

    // 超过5秒, 微信服务器不会处理
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();    //获取开始时间
        response.setCharacterEncoding("UTF-8");         // 避免中文乱码
        PrintWriter out = response.getWriter();
        String str = "";
        try {
            Map<String, String> map = MessageUtil.xmlToMap(request);
            // 取元素
            String ToUserName = map.get("ToUserName");
            String FromUserName = map.get("FromUserName");
            String CreateTime = map.get("CreateTime");
            String MsgType = map.get("MsgType");
            String Content = map.get("Content");
            String MsgId = map.get("MsgId");
            String MediaId = map.get("MediaId");

            if (MsgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                TextMessage message = new TextMessage();
                message.setFromUserName(ToUserName);
                message.setToUserName(FromUserName);
                message.setMsgType("text");
                message.setCreateTime(simpleDateFormat.format(new Date()));
                message.setMsgId(MsgId);
                message.setContent("Hello World!");
                str = MessageUtil.textMsgToXml(message);
            } else if (MsgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {//判断是否为文本消息类型
                if (Content.equals("1")) {
                    str = MessageUtil.initText(ToUserName, FromUserName, MsgId,"A");
                } else if (Content.equals("2")) {
                    str = MessageUtil.initText(ToUserName, FromUserName, MsgId,"B");
                } else if (Content.equals("3")) {
                    str = MessageUtil.initText(ToUserName, FromUserName, MsgId,MessageUtil.menuText());
                } else if(Content.equals("4")){
                    str = MessageUtil.initNewsMessage(ToUserName, FromUserName,MsgId);
                }else {
                    str = MessageUtil.initText(ToUserName, FromUserName, MsgId,"没让你选的就别瞎嘚瑟！！！");
                }
            } else if (MsgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {    //判断是否为事件类型
                String eventType = map.get("Event");
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    str = MessageUtil.initText(ToUserName, FromUserName, MsgId, MessageUtil.menuText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
        logger.info(("程序运行时间：" + (endTime - startTime) + "ms"));    //输出程序运行时间
        out.print(str);
        out.close();
    }


}
