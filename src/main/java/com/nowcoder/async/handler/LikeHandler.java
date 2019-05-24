package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;


    @Override
    public void doHandler(EventModel model) {
        Message message=new Message();
        message.setToId(model.getEntityOwnerId());
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setHasRead(0);
        User user=userService.getUser(model.getActorId());
        message.setCreatedDate(new Date());
        message.setContent("用户"+user.getName()+"赞了你的评论,http://127.0.0.1:8080/question"+model.getExts("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.LIKE);
    }
}
