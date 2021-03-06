package com.coolpeng.blog.widget;

import com.coolpeng.blog.entity.ForumModule;
import com.coolpeng.blog.entity.ForumPost;
import com.coolpeng.blog.service.ForumModuleService;
import com.coolpeng.blog.utils.ForumUrlUtils;
import com.coolpeng.framework.db.PageResult;
import com.coolpeng.framework.mvc.TmsCurrentRequest;
import com.coolpeng.framework.mvc.jsptag.TmsFunctions;
import com.coolpeng.framework.utils.CollectionUtil;
import com.coolpeng.framework.utils.DateUtil;
import com.coolpeng.framework.utils.ServiceUtils;
import com.coolpeng.framework.utils.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

public class BlogListWidget extends TagSupport {
    private PageResult<ForumPost> pageResult;


    private String getImage1(ForumPost post) {
        if (CollectionUtil.isEmpty(post.getImageList())) {
            post.createTempImageEntity(10);
        }

        if (CollectionUtil.isEmpty(post.getImageList())) {
            return null;
        }

        List<String> imgList = post.getImageList();

        for (String img : imgList) {
            if (img != null) {
                if (img.endsWith(".png") || img.endsWith(".jpg")) {
                    return img;
                }
            }
        }
        return null;
    }

    private void appendPostHtml(StringBuffer sb, ForumPost post, ForumModuleService forumModuleService) {
        String postId = post.getId();
        String image1 = getImage1(post);
        String postUrl = ForumUrlUtils.toPostContentHttpURL(postId);
        ForumModule module = forumModuleService.getForumModule(post.getForumModuleId());
        String createTime = DateUtil.toPrettyString(post.getCreateTime());

        if (StringUtils.isBlank(image1)) {
            int intPostId = NumberUtils.toInt(postId, 1);
            int index = intPostId % 13;
            String ctx = TmsCurrentRequest.getContext();
            String sIndex;
            if (index < 10)
                sIndex = "0" + index;
            else {
                sIndex = "" + index;
            }

            image1 = ctx + "/common/images/default-blog-thumb/" + sIndex + ".jpg";
        } else {
//            image1 = BlogFunctions.toImageW250(image1);
            image1 = TmsFunctions.toImageThumb(image1,250,90);
        }

        String x = "" +
                "<div class=\"blogs\">\n        " +
                "       <figure><img src=\"" + image1 + "\"></figure>\n" +
                "       <ul>\n" +
                "          <h3><a href='" + postUrl + "'>" + post.getPostTitle() +
                "             &nbsp;&nbsp;&nbsp;&nbsp;</a>" +
                "         </h3>\n" +
                "          <p>" + StringUtils.htmlEncode(post.getSummary()) + "</p>\n" +
                "          <p class=\"autor\">\n" +
                "               <span class=\"lm f_l\">\n" +
                "                   <a href=\"/\">" + module.getModuleName() + "</a>\n" +
                "               </span>\n" +
                "               <span class=\"dtime f_l\">" + createTime + "</span>\n" +
                "               <span class=\"viewnum f_r\">浏览（<a>" + post.getViewCount() + "</a>）</span>\n" +
                "               <span class=\"pingl f_r\">评论（<a>" + post.getReplyCount() + "</a>）</span>\n" +
                "           </p>\n" +
                "        </ul>\n" +
                "      </div>\n";

        sb.append(x);
    }

    private String toHtml() {
        List<ForumPost> postList = this.pageResult.getPageData();

        if (!CollectionUtil.isEmpty(postList)) {
            ForumModuleService forumModuleService = (ForumModuleService) ServiceUtils.getBean("forumModuleService");

            StringBuffer sb = new StringBuffer();
            sb.append("<div class='blog-list'>");
            for (ForumPost post : postList) {
                appendPostHtml(sb, post, forumModuleService);
            }
            sb.append("</div>");
            return sb.toString();
        }
        return "<div class='blog-list-empty'><span>没有内容</span></div>";
    }

    public int doStartTag()
            throws JspException {
        JspWriter out = this.pageContext.getOut();
        try {
            String html = toHtml();
            out.print(html);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int doEndTag() throws JspException {
        return 6;
    }

    public PageResult<ForumPost> getPageResult() {
        return this.pageResult;
    }

    public void setPageResult(PageResult<ForumPost> pageResult) {
        this.pageResult = pageResult;
    }
}