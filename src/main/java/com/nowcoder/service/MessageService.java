package com.nowcoder.service;


import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDAO.addMessage(message)>0?message.getId():0;
    }

    public List<Message> getMessageDetail(String conversationId,int offset,int limit){
        return messageDAO.getMessageDetail(conversationId,offset,limit);
    }

    public List<Message> getMessageList(int userId,int offset,int limit){
        return messageDAO.getMessageList(userId,offset,limit);
    }

    public int getConversationUnreadCount(int userId,String convcersationId){
        return messageDAO.getConversationUnreadCount(userId,convcersationId);
    }

}
