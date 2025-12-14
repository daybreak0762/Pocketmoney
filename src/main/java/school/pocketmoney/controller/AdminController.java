package school.pocketmoney.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import school.pocketmoney.domain.Company;
import school.pocketmoney.domain.Hint;
import school.pocketmoney.dto.CompanyRequestDto;
import school.pocketmoney.dto.HintRequestDto;
import school.pocketmoney.repository.CompanyRepository;
import school.pocketmoney.service.CompanyService;
import school.pocketmoney.service.HintService;
import school.pocketmoney.service.MemberService;
import school.pocketmoney.domain.Member;

import java.util.List;

@Controller
@RequestMapping("/admin") // 📌 모든 관리자 기능의 기본 경로 설정
@RequiredArgsConstructor
public class AdminController {

    private final HintService hintService;
    private final MemberService memberService;
    private final CompanyService companyService;

    // --- 🔑 관리자 기능: 힌트 추가 ---
    @GetMapping("/hint/add")
    public String addHintForm(Model model, HttpSession session) {
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            // 세션에 ID가 없으면 (로그인되지 않았거나 세션 만료) 로그인 페이지로 리다이렉트
            return "redirect:/";
        }

        // 📌 추가: 모든 기업 목록을 Model에 담아 View로 전달
        List<Company> companies = companyService.findAllCompanies();
        model.addAttribute("companies", companies); // View에서 반복문으로 사용

        model.addAttribute("loggedInUserId", loggedInUserId);
        model.addAttribute("hintRequestDto", new HintRequestDto());

        return "admin/hint/addHint";
    }

    @PostMapping("/hint/add")
    public String addHintForm(HttpSession session, @ModelAttribute HintRequestDto dto, RedirectAttributes redirectAttributes) {
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

    // --- 🔑 관리자 기능: 전체 회원 목록 조회 및 관리 ---
    @GetMapping("/members")
    public String memberList(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되어 처리할 수 없습니다.");
            return "redirect:/"; // 로그인 페이지로 리다이렉트
        }

        try {
            // MemberService를 통해 모든 회원 목록을 조회
            List<Member> members = memberService.findAllMembers();
            model.addAttribute("members", members);

            // "admin/member/list.html" 템플릿으로 이동
            return "admin/member/memberList";

        } catch (Exception e) {
            // 예외 발생 시 대시보드로 이동하며 에러 메시지 전달
            redirectAttributes.addFlashAttribute("errorMessage", "회원 목록을 불러오는 데 실패했습니다.");
            return "redirect:/admin/dashboard";
        }
    }

    // --- 🔑 관리자 기능: 회원 접속 차단/해제 (Ban 기능) ---
    @PostMapping("/members/ban/{memberId}")
    public String toggleBanStatus(
            @PathVariable("memberId") String targetMemberId,
            @RequestParam("status") boolean isBanned, // true: 차단, false: 해제
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String adminId = (String) session.getAttribute("loggedInUserId");

        if (adminId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되어 처리할 수 없습니다.");
            return "redirect:/";
        }

        try {
            // 관리자 본인이 자신의 계정을 차단하는 것을 방지
            if (adminId.equals(targetMemberId)) {
                throw new IllegalStateException("자신의 계정을 차단할 수 없습니다.");
            }

            // MemberService를 호출하여 차단 상태를 변경
            memberService.updateBanStatus(targetMemberId, isBanned);

            String action = isBanned ? "차단" : "차단 해제";
            redirectAttributes.addFlashAttribute("successMessage",
                    "'" + targetMemberId + "' 님의 접속이 성공적으로 " + action + "되었습니다.");

            // 목록 페이지로 리다이렉트하여 변경된 결과를 확인
            return "redirect:/admin/members";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/members";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "회원 차단 상태 변경에 실패했습니다.");
            return "redirect:/admin/members";
        }
    }

    // 📌 새로 추가될 기업 등록 폼 메서드
    @GetMapping("/company/add")
    public String addCompanyForm(Model model, HttpSession session) {
        String adminId = (String) session.getAttribute("loggedInUserId");

        if (adminId == null) {
            return "redirect:/"; // 세션 만료 시 로그인 페이지로
        }

        // 권한 확인 로직은 Service에서 처리하지만, 폼 접근 자체를 제한할 수도 있습니다.
        // 여기서는 폼에 필요한 DTO 객체를 모델에 담아 전달합니다.
        model.addAttribute("companyRequestDto", new CompanyRequestDto());

        // templates/admin/company/addCompany.html 반환을 가정
        return "admin/company/addCompany";
    }


    // 📌 새로 추가될 기업 등록 처리 메서드
    @PostMapping("/company/add")
    public String addCompany(HttpSession session, @ModelAttribute CompanyRequestDto dto, RedirectAttributes redirectAttributes) {

        String adminId = (String) session.getAttribute("loggedInUserId");

        if (adminId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되어 처리할 수 없습니다.");
            return "redirect:/";
        }

        try {
            // Service 계층 호출: 관리자 권한 확인 및 기업 저장
            companyService.registerCompany(adminId, dto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "기업 '" + dto.getCoName() + "'이 성공적으로 등록되었습니다. (자동 번호 할당)");

            // 등록 성공 후 폼 페이지로 리다이렉트
            return "redirect:/admin/company/add";

        } catch (IllegalStateException e) {
            // 관리자 권한 없음 예외 처리
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/company/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "기업 등록 중 알 수 없는 오류가 발생했습니다.");
            return "redirect:/admin/company/add";
        }
    }
}