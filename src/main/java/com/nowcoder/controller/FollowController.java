package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProduce;
import com.nowcoder.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    EventProduce eventProduce;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @RequestMapping(value = {"/followUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String followUser(@RequestParam("userId")int userId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONstring(999);
        }

        boolean ret=followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);

        eventProduce.fireEvent(new EventModel().setType(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
                                .setEntityType(EntityType.ENTITY_USER).setEntityId(userId).setEntityOwnerId(userId));

        return WendaUtil.getJSONstring(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(value = {"/unfollowUser"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId")int userId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONstring(999);
        }

        boolean ret=followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);

        eventProduce.fireEvent(new EventModel().setType(EventType.UNFOLLOW).setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_USER).setEntityId(userId).setEntityOwnerId(userId));

        return WendaUtil.getJSONstring(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(value = {"/followQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId")int questionId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONstring(999);
        }

        Question question=questionService.getById(questionId);
        if(question==null){
            return WendaUtil.getJSONstring(1,"问题不存在");
        }

        boolean ret=followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);


        eventProduce.fireEvent(new EventModel().setType(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId).setEntityOwnerId(question.getUserId()));

        Map<String,Object> info=new HashMap<>();
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        info.put("id",hostHolder.getUser().getId());
        info.put("name",hostHolder.getUser().getName());
        info.put("headUrl",hostHolder.getUser().getHeadUrl());


        return WendaUtil.getJSONstring(ret?0:1,info);
    }

    @RequestMapping(value = {"/unfollowQuestion"},method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId")int questionId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONstring(999);
        }

        Question question=questionService.getById(questionId);
        if(question==null){
            return WendaUtil.getJSONstring(1,"问题不存在");
        }

        boolean ret=followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);


        eventProduce.fireEvent(new EventModel().setType(EventType.UNFOLLOW).setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId).setEntityOwnerId(question.getUserId()));

        Map<String,Object> info=new HashMap<>();
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        info.put("id",hostHolder.getUser().getId());


        return WendaUtil.getJSONstring(ret?0:1,info);
    }

    @RequestMapping(value = {"/user/{uid}/followers"})
    public String followers(Model model,@PathVariable("uid")int userId){

        List<Integer> followerIds=followService.getFollowers(EntityType.ENTITY_USER,userId,0,10);
        if(hostHolder.getUser()!=null){
            model.addAttribute("followers",getUserInfo(hostHolder.getUser().getId(),followerIds));
        }else{
            model.addAttribute("followers",getUserInfo(0,followerIds));
        }
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser",userService.getUser(userId));
        return "followers";
    }

    @RequestMapping(value = {"/user/{uid}/followees"})
    public String followees(Model model,@PathVariable("uid")int userId){

        List<Integer> followeeIds=followService.getFollowees(userId,EntityType.ENTITY_USER,0,10);
        if(hostHolder.getUser()!=null){
            model.addAttribute("followees",getUserInfo(hostHolder.getUser().getId(),followeeIds));
        }else{
            model.addAttribute("followees",getUserInfo(0,followeeIds));
        }
        model.addAttribute("followeeCount",followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
        model.addAttribute("curUser",userService.getUser(userId));
        return "followees";
    }

    private List<ViewObject> getUserInfo(int localUserId, List<Integer> followerIds){
        List<ViewObject> vos=new ArrayList<>();
        for(Integer id:followerIds){
            User user=userService.getUser(id);
            if(user==null){
                continue;
            }
            ViewObject vo=new ViewObject();
            vo.set("user",user);
            vo.set("commentCount",commentService.getUserCommentCount(id));
            vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,id));
            vo.set("followeeCount",followService.getFolloweeCount(id,EntityType.ENTITY_USER));
            if(localUserId!=0){
                vo.set("followed",followService.isFollower(localUserId,EntityType.ENTITY_USER,id));
            }else{
                vo.set("followed",false);
            }
            vos.add(vo);
        }
        return vos;
    }


}
