package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/msg/addMessage",method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName")String toName,
                             @RequestParam("content")String content){

        try{
            Message message=new Message();

            if(hostHolder.getUser()==null){
                return WendaUtil.getJSONstring(999,"未登录");
            }else{
                message.setFromId(hostHolder.getUser().getId());
            }
            User user=userService.selectByName(toName);
            if(user==null){
                return WendaUtil.getJSONstring(1,"用户不存在");
            }else{
                message.setToId(user.getId());
            }
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setHasRead(0);
            messageService.addMessage(message);
            return WendaUtil.getJSONstring(0);
        }catch (Exception e){
            logger.error("发送信息失败"+e.getMessage());
            return WendaUtil.getJSONstring(1,"失败");
        }
    }

    @RequestMapping(value = {"/msg/list"},method = {RequestMethod.GET})
    public String getConversationList(Model model){

        if(hostHolder.getUser()==null){
            return "redirect:/reglogin";
        }
        try {
            int localUserId = hostHolder.getUser().getId();
            List<Message> messageList = messageService.getMessageList(localUserId, 0, 10);
            List<ViewObject> conversations = new ArrayList<>();
            for (Message message : messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                vo.set("user", userService.getUser(targetId));
                vo.set("unread", messageService.getConversationUnreadCount(targetId, message.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        }catch (Exception e){
            logger.error("获取消息失败"+e.getMessage());
        }

        return "letter";

    }

    @RequestMapping(value = {"/msg/detail"},method = {RequestMethod.GET})
    public String getConversationDetail(Model model,
                                        @RequestParam("conversationId")String conversationId){

        try{
            List<Message> messageList=messageService.getMessageDetail(conversationId,0,10);
            List<ViewObject> messages=new ArrayList<>();
            for(Message message:messageList){
                ViewObject vo=new ViewObject();
                vo.set("message",message);
                vo.set("user",userService.getUser(message.getFromId()));
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e){
            logger.error("获取详情失败"+e.getMessage());
        }

        return "letterDetail";

    }

}
