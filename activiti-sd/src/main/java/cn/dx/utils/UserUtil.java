package cn.dx.utils;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * 用户工具类
 *
 * @author
 */
public class UserUtil
{
    
    public static final String USER = "user";
    
    /**
     * 设置用户到session
     *
     * @param session
     * @param user
     */
    public static void saveUserToSession(HttpSession session, Map<String,Object> user)
    {
        session.setAttribute(USER, user);
    }
    
    /**
     * 从Session获取当前用户信息
     *
     * @param session
     * @return
     */
    public static Map<String,Object> getUserFromSession(HttpSession session)
    {
        Object attribute = session.getAttribute(USER);
        return attribute == null ? null : (Map<String,Object>)attribute;
    }
    
}
