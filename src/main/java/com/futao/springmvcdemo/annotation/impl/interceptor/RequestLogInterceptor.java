package com.futao.springmvcdemo.annotation.impl.interceptor;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.futao.springmvcdemo.utils.RequestUtils.getCookies;
import static com.futao.springmvcdemo.utils.RequestUtils.getSessionParameters;

/**
 * @author futao
 * Created on 2018/9/20-12:12.
 * 请求controller记录日志，以及接口请求时间记录
 */
@Component
public class RequestLogInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);

    /**
     * 统计接口请求次数请求
     */
    private ConcurrentHashMap<String, AtomicInteger> apiRequestStatistic = new ConcurrentHashMap();

    /**
     * controller执行之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        logger.info("请求(id={})开始：开始时间：{}", uuid, startTime);
        request.setAttribute("startTime", startTime);
        request.setAttribute("uuid", uuid);
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            RestController restController = method.getDeclaringClass().getAnnotation(RestController.class);
            if (ObjectUtils.allNotNull(restController)) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n")
                        .append("From: ").append(request.getRemoteHost()).append("|").append(request.getRemoteAddr()).append("|").append(request.getRemotePort())
                        .append("\n")
                        .append("请求方式: ").append(request.getMethod())
                        .append("\n")
                        .append("请求地址: ").append(request.getRequestURL())
                        .append("\n")
                        .append("请求sessions: ").append(getSessionParameters(request.getSession(false)))
                        .append("\n")
                        .append("请求参数：").append(queryParameters(request))
                        .append("\n")
                        .append("请求cookies: ").append(getCookies(request.getCookies()));
                logger.info(String.valueOf(sb));
            }
            String methodName = method.getDeclaringClass() + ".< " + method.getName() + " >";
            AtomicInteger atomicInteger = apiRequestStatistic.get(methodName);
            if (atomicInteger == null) {
                apiRequestStatistic.put(methodName, new AtomicInteger(1));
            } else {
                apiRequestStatistic.put(methodName, new AtomicInteger(atomicInteger.incrementAndGet()));
            }
        }
        return true;
    }

    /**
     * 获取queryString中的数据
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private String queryString(HttpServletRequest request) throws UnsupportedEncodingException {
        String qStr = request.getQueryString();
        return qStr == null ? "" : URLDecoder.decode(qStr, "UTF-8");
    }

    /**
     * 获取getParameter中的数据
     * 使用getParameter代替getQueryString的原因是后者只能拿到url中的参数，对于放在body中的参数是拿不到的
     * 虽然GET和POST方法都可以将参数放在url中，但是POST放在body中的时候，getQueryString拿不到数据
     *
     * @param request
     * @return
     */
    public static String queryParameters(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        JSONObject jsonObject = new JSONObject();
        map.forEach((k, v) -> jsonObject.put(k, Arrays.toString(v)));
        return jsonObject.toJSONString();
    }

    /**
     * 视图渲染之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("请求(id={})结束：本次请求所消耗的时间（毫秒）：{}", request.getAttribute("uuid"), ((System.currentTimeMillis() - (Long.valueOf(request.getAttribute("startTime").toString())))));
    }

    public ConcurrentHashMap<String, AtomicInteger> getApiRequestStatistic() {
        return apiRequestStatistic;
    }
}
