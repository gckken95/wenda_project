package com.nowcoder.controller;


import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = {"/addcomment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId")int questionId,
                             @RequestParam("content")String content){
        try {
            Comment comment = new Comment();
            if (hostHolder.getUser() == null) {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            } else {
                comment.setUserId(hostHolder.getUser().getId());
            }
            comment.setContent(content);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            int count=commentService.getCommentCount(comment.getEntityType(),comment.getId());
            questionService.updateCommentCount(comment.getEntityId(),count);
        }catch (Exception e){
            logger.error("添加评论失败"+e.getMessage());
        }
        return "redirect:/question/"+questionId;
    }

}
