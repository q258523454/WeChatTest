package util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import entity.TextMessage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息转化工具类
 *
 * @author sun
 */

public class MessageUtil {

    /**
     * 返回消息类型：文本
     */

    public static final String RESP_MESSAGE_TYPE_TEXT = "text";

    /**
     * 返回消息类型：音乐
     */

    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

    /**
     * 返回消息类型：图文
     */

    public static final String RESP_MESSAGE_TYPE_NEWS = "news";

    /**
     * 请求消息类型：文本
     */

    public static final String REQ_MESSAGE_TYPE_TEXT = "text";

    /**
     * 请求消息类型：图片
     */

    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

    /**
     * 请求消息类型：链接
     */

    public static final String REQ_MESSAGE_TYPE_LINK = "link";

    /**
     * 请求消息类型：地理位置
     */

    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

    /**
     * 请求消息类型：音频
     */

    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

    /**
     * 请求消息类型：推送
     */

    public static final String REQ_MESSAGE_TYPE_EVENT = "event";

    /**
     * 事件类型：subscribe(订阅)
     */

    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

    /**
     * 事件类型：unsubscribe(取消订阅)
     */

    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

    /**
     * 事件类型：CLICK(自己定义菜单点击事件)
     */

    public static final String EVENT_TYPE_CLICK = "CLICK";

    //定义一个私有的静态全局变量来保存该类的唯一实例

    private static MessageUtil messageUtil;

    /// 构造函数必须是私有的

    /// 这样在外部便无法使用 new 来创建该类的实例

    private MessageUtil()

    {

    }

    /// 定义一个全局访问点

    /// 设置为静态方法

    /// 则在类的外部便无需实例化就可以调用该方法

    public static MessageUtil getInstance()

    {

        //这里可以保证只实例化一次

        //即在第一次调用时实例化

        //以后调用便不会再实例化

        if (messageUtil == null)

        {

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

    public Map<String, String> parseXml(HttpServletRequest request) throws Exception {

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

    public String parseMsgXml(HttpServletRequest request) {

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
     * 文本消息对象转换成xml
     *
     * @param textMessage 文本消息对象
     * @return xml
     */

    public String textMessageToXml(TextMessage textMessage) {

        xstream.alias("xml", textMessage.getClass());

        return xstream.toXML(textMessage);

    }

    /**
     * 音乐消息对象转换成xml
     *
     * @param musicMessage 音乐消息对象
     * @return xml
     */

//    public String musicMessageToXml(MusicMessage musicMessage) {
//
//        xstream.alias("xml", musicMessage.getClass());
//
//        return xstream.toXML(musicMessage);
//
//    }

    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage 图文消息对象
     * @return xml
     */

//    public String newsMessageToXml(NewsMessage newsMessage) {
//
//        xstream.alias("xml", newsMessage.getClass());
//
//        xstream.alias("item", new Article().getClass());
//
//        return xstream.toXML(newsMessage);
//
//    }

    /**
     * 扩展xstream，使其支持CDATA块
     */

    private XStream xstream = new XStream(new XppDriver()) {
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

}

