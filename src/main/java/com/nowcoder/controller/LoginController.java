package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;


    @RequestMapping(path = {"/reg/"},method = {RequestMethod.GET,RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username")String username,
                      @RequestParam("password")String password,
                      @RequestParam(value = "next",required = false)String next,
                      @RequestParam(value="remeber_me",defaultValue ="false")boolean remeberme,
                      HttpServletResponse response){
        try {
            Map<String, String> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie=new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");

                if(remeberme){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("注册失败"+e.getMessage());
            model.addAttribute("msg","服务器异常");
            return "login";
        }
    }


    @RequestMapping(path = {"/login/"},method = {RequestMethod.GET,RequestMethod.POST})
    public String login(Model model,
                      @RequestParam("username")String username,
                      @RequestParam("password")String password,
                      @RequestParam(value = "next",required = false)String next,
                      @RequestParam(value="remeber_me",defaultValue = "false")boolean remeberme,
                      HttpServletResponse response){
        try {
            Map<String, String> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie=new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");

                if(remeberme){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("登录失败"+e.getMessage());
            return "login";
        }
    }

    //通过在页面埋next点方式将next传递到reg和login中

    @RequestMapping(path = {"/reglogin"},method = {RequestMethod.GET,RequestMethod.POST})
    public String regloginPage(Model model,
                           @RequestParam(value = "next",defaultValue = "",required = false)String next){
        model.addAttribute("next",next);
        return "login";
    }

    @RequestMapping(path = {"/logout"},method = {RequestMethod.GET,RequestMethod.POST})
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
