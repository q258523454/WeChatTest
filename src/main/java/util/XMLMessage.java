package util;

import com.thoughtworks.xstream.XStream;
import entity.TextMessage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-19
 */

public class XMLMessage {

    // xml to message
    public Map<String, String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException {
        Map<String, String> map = new HashMap<String, String>();
        // SAXReader对象创建
        SAXReader saxReader = new SAXReader();
        InputStream inputStream = request.getInputStream(); // 获取输入流
        Document document = saxReader.read(inputStream);    // 读取输入到SAXReader
        Element rootElement = document.getRootElement();    // 获取XML文档根元素
        List<Element> list = rootElement.elements();        // 获取所有子节点
        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        inputStream.close();
        return map;
    }

    // message to xml
    public static String objectToXml(TextMessage textMessage) {
        XStream xStream = new XStream();
        xStream.alias("xml", textMessage.getClass());
        return xStream.toXML(textMessage);
    }
}
