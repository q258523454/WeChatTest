package entity;

import java.util.List;

/**
 * Created by
 *
 * @author :   zhangjian
 * @date :   2018-03-20
 */

public class NewsMessage extends baseMessage {
    private int ArticleCount; // 图文消息条数
    private List<News> Articles;

    public int getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(int articleCount) {
        ArticleCount = articleCount;
    }

    public List<News> getArticles() {
        return Articles;
    }

    public void setArticles(List<News> articles) {
        Articles = articles;
    }
}
