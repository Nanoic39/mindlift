package cn.dcstd.web.mindlift3.utils;

import cn.dcstd.web.mindlift3.configuration.GlobalConfiguration;
import cn.dcstd.web.mindlift3.entity.UserDO;
import cn.dcstd.web.mindlift3.exception.CustomException;
import cn.dcstd.web.mindlift3.exception.GlobalException;
import cn.dcstd.web.mindlift3.mapper.AuthMapper;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author NaNo1c
 */
@Component
public class TokenUtil {
    private static AuthMapper staticAuthMapper;
    private static GlobalConfiguration staticglobalConfiguration;

    @Resource
    AuthMapper authMapper;

    @Resource
    GlobalConfiguration globalConfiguration;

    @PostConstruct
    public void init() {
        staticAuthMapper = authMapper;
        staticglobalConfiguration = globalConfiguration;
    }

    /**
     * 生成token
     * @param id 用户id
     * @param sign 加密密钥
     * @return token
     */
    public static String createToken(int id, String sign) {
        String uid = String.valueOf(id);
        if(staticglobalConfiguration.getTokenExpireTime() <= 0){
            return JWT.create().withAudience(uid)
                    .withExpiresAt(DateUtil.offsetDay(DateUtil.date(), 30))
                    .sign(Algorithm.HMAC256(sign));
        }
        return JWT.create().withAudience(uid)
                .withExpiresAt(DateUtil.offsetSecond(DateUtil.date(), staticglobalConfiguration.getTokenExpireTime()))
                .sign(Algorithm.HMAC256(sign));
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    public static UserDO getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("Authorization");
            if(StrUtil.isNotBlank(token)){
                String uid = JWT.decode(token).getAudience().get(0);
                return staticAuthMapper.selectUserById(uid);
            }
        } catch (Exception e) {
            throw new CustomException(GlobalException.ERROR_TOKEN);
        }
        throw new CustomException();
    }

}
