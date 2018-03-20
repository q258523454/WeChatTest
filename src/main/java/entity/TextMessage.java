package entity;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-19
 */

public class TextMessage extends baseMessage{
    private String Content;         // 回复的消息内容（换行：在content中能够换行，微信客户端就支持换行显示）

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

}
