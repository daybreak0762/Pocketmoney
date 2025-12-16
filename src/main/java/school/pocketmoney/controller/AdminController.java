package school.pocketmoney.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import school.pocketmoney.domain.Company;
import school.pocketmoney.domain.Hint;
import school.pocketmoney.domain.Member;
import school.pocketmoney.dto.CompanyRequestDto;
import school.pocketmoney.dto.HintRequestDto;
import school.pocketmoney.dto.StockRequestDto;
import school.pocketmoney.service.CompanyService;
import school.pocketmoney.service.HintService;
import school.pocketmoney.service.MemberService;
import school.pocketmoney.service.StockService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HintService hintService;
    private final MemberService memberService;
    private final CompanyService companyService;
    private final StockService stockService;

    // ==========================================================
    // 📌 관리자 접근 체크 공통 로직
    // ==========================================================
    private String checkAdminSession(HttpSession session, RedirectAttributes redirectAttributes) {
        String loggedInUserId = (String) session.getAttribute("loggedInUserId");

        // 1. 세션에 ID가 없는 경우 (미로그인 상태)
        if (loggedInUserId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/";
        }

        // 2. ID로 Member 엔티티 조회
        Optional<Member> memberOptional = memberService.findByMemberId(loggedInUserId);

        // 3. 엔티티 존재 여부 및 관리자 권한 확인
        if (memberOptional.isEmpty()) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("errorMessage", "잘못된 사용자 정보입니다. 다시 로그인해주세요.");
            return "redirect:/";
        }

        Member member = memberOptional.get();

        // 📌 관리자 권한 체크: 'ad' 필드가 null이거나 false인 경우
        if (member.getAd() == null || !member.getAd()) {
            redirectAttributes.addFlashAttribute("errorMessage", "관리자 권한이 없습니다.");
            return "redirect:/main";
        }

        // 4. 관리자 확인 완료
        return null;
    }

    // ==========================================================
    // 🔑 관리자 기능: 대시보드
    // ==========================================================
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, RedirectAttributes rttr) {
        String redirectUrl = checkAdminSession(session, rttr);
        if (redirectUrl != null) {
            return redirectUrl;
        }
        return "admin/dashboard";
    }

    // ==========================================================
    // 🔑 관리자 기능: 힌트 추가 폼
    // ==========================================================
    @GetMapping("/hint/add")
    public String addHintForm(Model model, HttpSession session, RedirectAttributes rttr) {
        String redirectUrl = checkAdminSession(session, rttr);
        if (redirectUrl != null) {
            return redirectUrl;
        }

        // --- 관리자 권한 확인 후 로직 ---
        List<Company> companies = companyService.findAllCompanies();
        model.addAttribute("companies", companies);
        model.addAttribute("hintRequestDto", new HintRequestDto());

        return "admin/hint/addHint";
    }

    // ==========================================================
    // 🔑 관리자 기능: 힌트 추가 처리
    // ==========================================================
    @PostMapping("/hint/add")
    public String addHint(HttpSession session, @ModelAttribute HintRequestDto dto, RedirectAttributes redirectAttributes) {
        String adminId = (String) session.getAttribute("loggedInUserId"); // 세션에서 ID 가져오기

        // 📌 POST 메서드에서는 Session 값만으로 체크 (성능상 이점)
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
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/hint/add";
        }
    }

    // ==========================================================
    // 🔑 관리자 기능: 전체 회원 목록 조회 및 관리
    // ==========================================================
    @GetMapping("/members")
    public String memberList(Model model, HttpSession session, RedirectAttributes rttr) {
        String redirectUrl = checkAdminSession(session, rttr);
        if (redirectUrl != null) {
            return redirectUrl;
        }

        try {
            List<Member> members = memberService.findAllMembers();
            model.addAttribute("members", members);
            return "admin/member/memberList";

        } catch (Exception e) {
            rttr.addFlashAttribute("errorMessage", "회원 목록을 불러오는 데 실패했습니다.");
            return "redirect:/admin/dashboard";
        }
    }

    // ==========================================================
    // 🔑 관리자 기능: 회원 접속 차단/해제 (Ban 기능)
    // ==========================================================
    @PostMapping("/members/ban/{memberId}")
    public String toggleBanStatus(
            @PathVariable("memberId") String targetMemberId,
            @RequestParam("status") boolean isBanned,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String adminId = (String) session.getAttribute("loggedInUserId");

        // 📌 POST 메서드에서는 Session 값만으로 체크
        if (adminId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되어 처리할 수 없습니다.");
            return "redirect:/";
        }

        try {
            if (adminId.equals(targetMemberId)) {
                throw new IllegalStateException("자신의 계정을 차단할 수 없습니다.");
            }

            memberService.updateBanStatus(targetMemberId, isBanned);

            String action = isBanned ? "차단" : "차단 해제";
            redirectAttributes.addFlashAttribute("successMessage",
                    "'" + targetMemberId + "' 님의 접속이 성공적으로 " + action + "되었습니다.");

            return "redirect:/admin/members";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/members";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "회원 차단 상태 변경에 실패했습니다.");
            return "redirect:/admin/members";
        }
    }

    // ==========================================================
    // 🔑 관리자 기능: 기업 등록 폼
    // ==========================================================
    @GetMapping("/company/add")
    public String addCompanyForm(Model model, HttpSession session, RedirectAttributes rttr) {
        String redirectUrl = checkAdminSession(session, rttr);
        if (redirectUrl != null) {
            return redirectUrl;
        }

        // --- 관리자 권한 확인 후 로직 ---
        model.addAttribute("companyRequestDto", new CompanyRequestDto());

        return "admin/company/addCompany";
    }


    // ==========================================================
    // 🔑 관리자 기능: 기업 등록 처리
    // ==========================================================
    @PostMapping("/company/add")
    public String addCompany(HttpSession session, @ModelAttribute CompanyRequestDto dto, RedirectAttributes redirectAttributes) {

        String adminId = (String) session.getAttribute("loggedInUserId");

        if (adminId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되어 처리할 수 없습니다.");
            return "redirect:/";
        }

        try {
            companyService.registerCompany(adminId, dto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "기업 '" + dto.getCoName() + "'이 성공적으로 등록되었습니다. (자동 번호 할당)");

            return "redirect:/admin/company/add";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/company/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "기업 등록 중 알 수 없는 오류가 발생했습니다.");
            return "redirect:/admin/company/add";
        }
    }

    // ==========================================================
    // 🔑 관리자 기능: 주가 등록 폼 (하나로 통합됨)
    // ==========================================================
    @GetMapping("/stock/add")
    public String addStockForm(Model model, HttpSession session, RedirectAttributes rttr,
                               @RequestParam(required = false) Long coNum) {
        String redirectUrl = checkAdminSession(session, rttr);
        if (redirectUrl != null) {
            return redirectUrl;
        }

        // --- 관리자 권한 확인 후 로직 ---
        LocalDate recommendedDate = null;
        if (coNum != null) {
            try {
                recommendedDate = stockService.getNextRegistrationDate(coNum);
            } catch (IllegalArgumentException e) {
                // coNum 오류 시 무시
            }
        }

        StockRequestDto dto = new StockRequestDto();
        if (coNum != null) {
            dto.setCoNum(coNum); // 기업이 선택되었다면 DTO에 coNum을 미리 설정
        }

        model.addAttribute("stockRequestDto", dto);
        model.addAttribute("companies", companyService.findAllCompanies());
        model.addAttribute("recommendedStockDate", recommendedDate != null ? recommendedDate.toString() : "2015-01-01");

        return "admin/stock/addStock";
    }


    // ==========================================================
    // 🔑 관리자 기능: 주가 등록 처리
    // ==========================================================
    @PostMapping("/stock/add")
    public String addStock(
            HttpSession session,
            @ModelAttribute StockRequestDto dto,
            RedirectAttributes redirectAttributes) {

        String adminId = (String) session.getAttribute("loggedInUserId");

        if (adminId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션이 만료되어 처리할 수 없습니다.");
            return "redirect:/";
        }

        try {
            LocalDate nextDate = stockService.registerQuarterlyStock(dto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "날짜 " + dto.getStockDate() + "의 주가(₩" + dto.getStock() + ")가 성공적으로 등록되었습니다. " +
                            "다음 추천 등록일은 " + nextDate + "입니다.");

            return "redirect:/admin/stock/add?coNum=" + dto.getCoNum();

        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/stock/add?coNum=" + dto.getCoNum();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "주가 등록 중 알 수 없는 오류가 발생했습니다.");
            return "redirect:/admin/stock/add";
        }
    }

}