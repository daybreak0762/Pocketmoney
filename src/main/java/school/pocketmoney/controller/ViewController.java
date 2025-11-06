package school.pocketmoney.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    
    // 로그인 페이지 이동
    @GetMapping("/")
    public String loginPage() {
        // "members/login"을 반환하여 templates/members/login.html 파일을 찾습니다.
        return "members/login";
    }

    // 회원가입 페이지 이동
    @GetMapping("/api/member/join")
    public String joinPage() {
        // "members/join"을 반환하여 templates/members/join.html 파일을 찾습니다.
        return "members/join";
    }

    
    @GetMapping("/main")
    public String mainPage() {
        return "members/main";
    }
}