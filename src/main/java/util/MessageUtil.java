package util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import entity.News;
import entity.NewsMessage;
import entity.TextMessage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.*;

/**
 * 消息转化工具类
 *
 * @author zhangjian
 */

public class MessageUtil {
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";         // 图文
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
    public static final String REQ_MESSAGE_TYPE_LINK = "link";          // 链接
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";  // 地理位置
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";        // 音频
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";        // 事件类型{subscribe(订阅):unsubscribe(取消订阅)}
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";      // subscribe(订阅)
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";  // unsubscribe(取消订阅)
    public static final String EVENT_TYPE_CLICK = "CLICK";              // CLICK(自己定义菜单点击事件)

    //定义一个私有的静态全局变量来保存该类的唯一实例
    private static MessageUtil messageUtil;

    /// 构造函数必须是私有的
    /// 这样在外部便无法使用 new 来创建该类的实例
    private MessageUtil() {

    }

    /// 单例模式
    public static MessageUtil getInstance() {
        if (messageUtil == null) {
            messageUtil = new MessageUtil();
        }
        return messageUtil;
    }

    /**
     * 解析微信发来的请求（XML）
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        String requestXml = document.asXML();
        String subXml = requestXml.split(">")[0] + ">";
        requestXml = requestXml.substring(subXml.length());
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的全部子节点
        List<Element> elementList = root.elements();
        // 遍历全部子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        map.put("requestXml", requestXml);
        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }

    /**
     * 将请求的数据转化xml
     *
     * @param request
     * @return
     */
    public static String requestMsgToXml(HttpServletRequest request) {
        String responseMsg = null;
        try {
            InputStream is = request.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            responseMsg = new String(jsonBytes, "UTF-8");
            is.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseMsg;
    }

    /**
     * 扩展xstream，使其支持CDATA块
     */
    public static XStream xstream = new XStream(new XppDriver()) {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对全部xml节点的转换都添加CDATA标记
                boolean cdata = true;

                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                    if (name.equals("CreateTime")) {
                        cdata = false;
                    }
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    };


    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */
    public static String textMsgToXml(TextMessage textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }


    /**
     * 音乐消息对象转换成xml
     * @param musicMessage 音乐消息对象
     * @return xml
     */
//    public String musicMessageToXml(MusicMessage musicMessage) {
//
//        xstream.alias("xml", musicMessage.getClass());//
//        return xstream.toXML(musicMessage);//
//    }

    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage 图文消息对象
     * @return xml
     */
    public static String newsMessageToXml(NewsMessage newsMessage) {
        xstream.alias("xml", newsMessage.getClass());        // 将xml的根节点替换成<xml>  默认为NewsMessage的包名
        xstream.alias("item", new News().getClass());     // 每条图文消息的(多条子消息:PictureNews)的包名替换为<item>标签
        return xstream.toXML(newsMessage);
    }

    public static String menuText() {
        StringBuffer sb = new StringBuffer();
        sb.append("欢迎关注公众号，请选择:\n");
        sb.append("1、A\n");
        sb.append("2、B\n");
        sb.append("3、主菜单\n");
        return sb.toString();
    }

    public static String initText(String toUSerName, String fromUserName, String MsgId, String content) {
        TextMessage text = new TextMessage();
        text.setFromUserName(toUSerName);
        text.setToUserName(fromUserName);
        text.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);
        text.setCreateTime(new Date().getTime() + "");
        text.setContent(content);
        text.setMsgId(MsgId);
        return MessageUtil.textMsgToXml(text);
    }

    public static String initNewsMessage(String toUSerName, String fromUserName, String MsgId) {
        NewsMessage newsMessage = new NewsMessage();
        List<News> newsList = new ArrayList<News>();
        // 组件一条图文
        News news = new News();
        news.setTitle("测试图文标题");
        news.setDescription("测试图文描述");
        news.setPicUrl("http://9f797487.ngrok.io/images/test.png"); // 本地图片
        // 百度网上的图片
//        news.setPicUrl("c8bf84f661b81382173af6e756&imgtype=0&src=http%3A%2F%2Fwww.citytalk.tw%2Fbbs%2Fdata%2Fattachment%2Fforum%2F201010%2F15%2F201757hhmpsvme3hmvez2n.jpg");
        news.setUrl("http://www.cuiyongzhi.com");
        newsList.add(news);

        // 组装整个图文消息
        newsMessage.setFromUserName(toUSerName);
        newsMessage.setToUserName(fromUserName);
        newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
        newsMessage.setArticleCount(newsList.size());
        newsMessage.setArticles(newsList);
        newsMessage.setCreateTime(new Date().getTime() + "");
        return MessageUtil.newsMessageToXml(newsMessage);
    }
}

