package cn.dcstd.web.mindlift3.controller;

import cn.dcstd.web.mindlift3.common.Result;
import cn.dcstd.web.mindlift3.entity.UserInfoDO;
import cn.dcstd.web.mindlift3.entity.UserInfoVO;
import cn.dcstd.web.mindlift3.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author NaNo1c
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;


    /**
     * 编辑头像
     * @param avatarId 头像id
     * @return
     */
    @PostMapping("/edit/avatar")
    public Result editAvatar(@RequestParam("avatarId") int avatarId) {
        userService.editAvatar(avatarId);
        return Result.success();
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/info")
    public Result getUserInfo() {
        return Result.success(userService.getUserInfo());
    }

    /**
     * 编辑用户信息
     * @param userInfoVO
     * @return
     */
    @PostMapping("/edit/info")
    public Result editUserInfo(@RequestBody UserInfoVO userInfoVO) {
        userService.editUserInfo(userInfoVO);
        return Result.success("编辑成功");
    }
}
