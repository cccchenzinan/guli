package com.guli.ucenter.controller.admin;

import com.guli.common.vo.R;
import com.guli.ucenter.service.MemberService;
import com.guli.ucenter.util.CookieUtils;
import com.guli.ucenter.vo.LoginInfoVo;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/admin/ucenter/member")
public class MemberAdminController {

    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "今日注册数")
    @GetMapping(value = "count-register/{day}")
    public R registerCount(
            @ApiParam(name = "day", value = "统计日期")
            @PathVariable String day){
        Integer count = memberService.countRegisterByDay(day);
        return R.ok().data("countRegister", count);
    }

    @GetMapping("get-jwt")
    @ResponseBody
    public R getJwt(HttpServletRequest request) {

        String guliJwtToken = CookieUtils.getCookieValue(request, "guli_jwt_token");
        return R.ok().data("guli_jwt_token", guliJwtToken);
    }

    @PostMapping("parse-jwt")
    @ResponseBody
    public R getLoginInfoByJwtToken(@RequestBody String jwtToken){

        Claims claims = com.guli.ucenter.util.JwtUtils.checkJWT(jwtToken);

        String id = (String)claims.get("id");
        String nickname = (String)claims.get("nickname");
        String avatar = (String)claims.get("avatar");

        LoginInfoVo loginInfoVo = new LoginInfoVo();
        loginInfoVo.setId(id);
        loginInfoVo.setNickname(nickname);
        loginInfoVo.setAvatar(avatar);

        return R.ok().data("loginInfo", loginInfoVo);

    }
}
