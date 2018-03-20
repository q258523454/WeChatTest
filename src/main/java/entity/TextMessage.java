package entity;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-19
 */

public class TextMessage extends baseMessage{
    private String Content;         // 文本消息内容

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

}
