package entity;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-20
 */

public class baseMessage {
    private String ToUserName;      // 开发者微信号
    private String FromUserName;    // 发送方帐号（一个OpenID）
    private String CreateTime;      // 消息创建时间 （整型）
    private String MsgType;         // text
    private String MsgId;           // 消息id，64位整型

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }
}

