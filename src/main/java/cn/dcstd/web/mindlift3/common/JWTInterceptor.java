package cn.dcstd.web.mindlift3.common;

import cn.dcstd.web.mindlift3.entity.UserDO;
import cn.dcstd.web.mindlift3.exception.CustomException;
import cn.dcstd.web.mindlift3.exception.GlobalException;
import cn.dcstd.web.mindlift3.mapper.AuthMapper;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author NaNo1c
 */
public class JWTInterceptor implements HandlerInterceptor {

    private static AuthMapper staticAuthMapper;
    @Resource
    private AuthMapper authMapper;

    @PostConstruct
    private void init(){
        // 初始化用户信息
        staticAuthMapper = authMapper;
    }

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            token = request.getParameter("Authorization");
        }

        // 接口含有绕过token注解
        if(handler instanceof HandlerMethod){
            AuthAccess authAccess = ((HandlerMethod) handler).getMethodAnnotation(AuthAccess.class);
            if(authAccess != null){
                return true;
            }
        }
        // 校验token
        // token为空
        if(StrUtil.isBlank(token)){
            throw new CustomException(GlobalException.ERROR_NOT_LOGIN);
        }
        // 获取token中的用户信息
        String uid;
        try {
             uid = JWT.decode(token).getAudience().getFirst();
        } catch (JWTDecodeException e) {
            throw new CustomException(GlobalException.ERROR_TOKEN);
        }
        // 根据token中的uid查询数据库用户信息
        UserDO db_user = staticAuthMapper.selectUserById(uid);
        if(db_user == null){
            throw new CustomException(GlobalException.ERROR_TOKEN);
        }
        // 用户密码加签名验证token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(db_user.getPassword())).build();
        try {
            //验证token
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new CustomException(GlobalException.ERROR_TOKEN);
        }
        return true;
    }
}
