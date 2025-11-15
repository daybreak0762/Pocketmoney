package school.pocketmoney.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.pocketmoney.domain.Hint;
import school.pocketmoney.dto.HintRequestDto;
import school.pocketmoney.service.HintService;
import school.pocketmoney.service.MemberService;

@Controller
@RequestMapping("/admin") // 📌 모든 관리자 기능의 기본 경로 설정
@RequiredArgsConstructor
public class AdminController {

    private final HintService hintService;
    private final MemberService memberService;

    // --- 🔑 관리자 기능: 힌트 추가 ---
    @GetMapping("/hint/add")
    public String addHintForm(Model model, HttpSession session) {
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            // 세션에 ID가 없으면 (로그인되지 않았거나 세션 만료) 로그인 페이지로 리다이렉트
            return "redirect:/";
        }

        model.addAttribute("loggedInUserId", loggedInUserId);
        model.addAttribute("hintRequestDto", new HintRequestDto());

        return "admin/hint/addForm";
    }

    @PostMapping("/hint/add")
    public String addHint(HttpSession session, @ModelAttribute HintRequestDto dto, RedirectAttributes redirectAttributes) {
        String adminId = (String) session.getAttribute("loggedInUserId"); // 세션에서 ID 가져오기

        if (adminId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되어 처리할 수 없습니다.");
            return "redirect:/";
        }

        try {
            Hint savedHint = hintService.addHint(adminId, dto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "힌트가 성공적으로 추가되었습니다. (번호: " + savedHint.getHtNum() + ")");
            return "redirect:/admin/hint/add";

        } catch (IllegalStateException | IllegalArgumentException e) {
            // 권한 없음 또는 날짜/ID 형식 오류 처리
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            // 폼 페이지로 리다이렉트
            return "redirect:/admin/hint/add";
        }
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}