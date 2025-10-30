package school.pocketmoney.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.pocketmoney.domain.Member;
import school.pocketmoney.service.MemberService;

import java.util.Map;


@Controller
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public String join(@RequestParam Map<String, String> requestMap, RedirectAttributes redirectAttributes) {
        try {
            String memberId = requestMap.get("memberId");
            String name = requestMap.get("name");
            String email = requestMap.get("email");
            String pw = requestMap.get("pw");

            // Builder를 사용하여 Entity 생성
            Member newMember = Member.builder()
                    .memberId(memberId)
                    .name(name)
                    .email(email)
                    .pw(pw)
                    .build();

            memberService.join(newMember);

            // 성공 시 로그인 페이지로 리다이렉트하며 성공 메시지 전달
            redirectAttributes.addFlashAttribute("successMessage", memberId + "님, 회원가입이 완료되었습니다! 로그인해 주세요.");
            return "redirect:/"; // "/"는 ViewController의 loginPage()로 연결
        } catch (IllegalStateException e) {
            // 실패 시 회원가입 페이지로 돌아가며 오류 메시지 전달
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/join";
        } catch (Exception e) {
            // 기타 서버 오류
            redirectAttributes.addFlashAttribute("errorMessage", "서버 오류: 회원가입 실패");
            return "redirect:/join";
        }
    }



    // 로그인
    @PostMapping("/login")
    public String login(@RequestParam String memberId, @RequestParam String pw, Model model) {

        Member member = memberService.login(memberId, pw); // MemberService 호출

        if (member != null) {
            // 로그인 성공 시 메인 페이지("/main")로 리다이렉트
            return "redirect:/main";
        } else {
            // 로그인 실패 시 templates/members/login.html 파일을 렌더링
            model.addAttribute("errorMessage", "로그인 실패: 아이디 또는 비밀번호를 확인하세요.");
            return "members/login"; // 템플릿 파일 경로 수정
        }
    }
}