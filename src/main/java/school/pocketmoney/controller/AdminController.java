package school.pocketmoney.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
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
    // 📌 HttpSession을 받아 처리
    public String addHintForm(Model model, HttpSession session) {
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            // 세션에 ID가 없으면 (로그인되지 않았거나 세션 만료) 로그인 페이지로 리다이렉트
            return "redirect:/";
        }

        // 📌 Model에 로그인 ID를 담아 View로 전달
        model.addAttribute("loggedInUserId", loggedInUserId);
        model.addAttribute("hintRequestDto", new HintRequestDto());

        return "admin/hint/addForm";
    }

    /**
     * 📝 힌트 추가 POST 요청: /admin/hint/add
     */
    @PostMapping("/hint/add")
    // 📌 @RequestParam adminId를 제거하고 HttpSession을 사용
    public String addHint(HttpSession session, @ModelAttribute HintRequestDto dto, RedirectAttributes redirectAttributes) {
        String adminId = (String) session.getAttribute("loggedInUserId"); // 세션에서 ID 가져오기

        if (adminId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되어 처리할 수 없습니다.");
            return "redirect:/";
        }

        try {
            // 📌 세션에서 가져온 adminId를 Service에 전달
            Hint savedHint = hintService.addHint(adminId, dto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "힌트가 성공적으로 추가되었습니다. (번호: " + savedHint.getHtNum() + ")");
            return "redirect:/admin/hint/add";

        } catch (IllegalStateException | IllegalArgumentException e) {
            // ... (오류 처리 로직 생략) ...
            return "redirect:/admin/hint/add";
        }
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        // 📌 대시보드 템플릿 경로도 "admin/dashboard"로 수정합니다.
        return "admin/dashboard";
    }
}



