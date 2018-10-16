package com.futao.springmvcdemo.controller.business;

import com.alibaba.fastjson.JSONObject;
import com.futao.springmvcdemo.annotation.IllegalValueCheck;
import com.futao.springmvcdemo.annotation.interceptor.LoginUser;
import com.futao.springmvcdemo.foundation.configuration.ApplicationContext;
import com.futao.springmvcdemo.model.entity.PageResultList;
import com.futao.springmvcdemo.model.entity.SingleValueResult;
import com.futao.springmvcdemo.model.entity.User;
import com.futao.springmvcdemo.model.entity.constvar.ErrorMessage;
import com.futao.springmvcdemo.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author futao
 * Created on 2018/9/19-15:05.
 */
@RequestMapping(path = "User", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Validated
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private ApplicationContext applicationContext;

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param age      年龄
     * @param mobile   手机号
     * @param email    邮箱
     * @param address  地址
     * @return
     */
    @PostMapping("register")
    public JSONObject register(
            /*使用@RequestBody注解需要保证该对象有默认的空的构造函数
             * 是流的形式读取，那么流读了一次就没有了
             * */
            @RequestParam("username")
            @Size(min = 3, max = 8, message = ErrorMessage.USERNAME_LEN_ILLEGAL)
                    String username,
            @RequestParam("age")
                    String age,
            @Size(max = 11, message = ErrorMessage.MOBILE_LEN_ILLEGAL)
            @RequestParam("mobile")
                    String mobile,
            @RequestParam("email")
            @Email(message = ErrorMessage.EMAIL_ILLEGAL)
                    String email,
            @NotBlank
            @IllegalValueCheck(forbidden = "LOL")
            @RequestParam("address")
                    String address
    ) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", "注册失败");
        if (userService.register(username, age, mobile, email, address)) {
            jsonObject.put("result", "注册成功");
        }
        return jsonObject;
    }

    /**
     * 登陆接口
     *
     * @param mobile
     * @param request
     * @return
     */
    @PostMapping(path = "login")
    public User login(
            @RequestParam("mobile") String mobile,
            @RequestParam("password") String password,
            HttpServletRequest request
    ) {
        return userService.login(mobile, password, request);
    }

    /**
     * 获取当前的登陆的用户信息，其实是从threadLocal中获取
     *
     * @return
     */
    @LoginUser
    @GetMapping(path = "my")
    public JSONObject my() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("当前的登陆的用户是:", userService.currentUser());
        return jsonObject;
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    @GetMapping("list")
    public PageResultList<User> list(
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int pageSize
    ) {
        return new PageResultList<>(userService.list(mobile, pageNum, pageSize, orderBy), 1, 11);
    }


    @GetMapping("get")
    public User get() {
        User user = new User();
        user.setUsername("NiuBist");
        user.setAge("18");
        user.setEmail("12312");
        user.setMobile("12312321312");
        return user;
    }

    /**
     * 添加评论
     *
     * @param content
     * @return
     */
    @PostMapping(path = "addReview")
    public SingleValueResult addReview(
            @RequestParam("content")
            @IllegalValueCheck(forbidden = "亚索")
                    String content) {
        System.out.println("已经被执行");
        SingleValueResult result = new SingleValueResult(content);
        return result;
    }
}
