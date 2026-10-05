package com.example.library.controller;

import com.example.library.dto.form.MemberForm;
import com.example.library.entity.Member;
import com.example.library.entity.enums.MemberStatus;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String listMembers(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) MemberStatus status,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "id") String sortBy,
                              @RequestParam(defaultValue = "desc") String sortDir,
                              Model model) {
        Page<Member> memberPage = memberService.searchMembers(keyword, status, page, size, sortBy, sortDir);

        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberPage.getTotalPages());
        model.addAttribute("totalElements", memberPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("statuses", MemberStatus.values());

        return "members/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        model.addAttribute("statuses", MemberStatus.values());
        model.addAttribute("isEdit", false);
        return "members/form";
    }

    @PostMapping
    public String createMember(@Valid @ModelAttribute("memberForm") MemberForm form,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", MemberStatus.values());
            model.addAttribute("isEdit", false);
            return "members/form";
        }

        try {
            memberService.createMember(form);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm thành viên mới thành công!");
            return "redirect:/members";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            model.addAttribute("statuses", MemberStatus.values());
            model.addAttribute("isEdit", false);
            return "members/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Member member = memberService.getMemberById(id);
        MemberForm form = new MemberForm(
                member.getId(),
                member.getFullName(),
                member.getEmail(),
                member.getPhone(),
                member.getStatus()
        );

        model.addAttribute("memberForm", form);
        model.addAttribute("statuses", MemberStatus.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("memberId", id);
        return "members/form";
    }

    @PostMapping("/{id}")
    public String updateMember(@PathVariable Long id,
                               @Valid @ModelAttribute("memberForm") MemberForm form,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", MemberStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("memberId", id);
            return "members/form";
        }

        try {
            memberService.updateMember(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành viên thành công!");
            return "redirect:/members";
        } catch (DuplicateResourceException ex) {
            bindingResult.rejectValue("email", "duplicate", ex.getMessage());
            model.addAttribute("statuses", MemberStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("memberId", id);
            return "members/form";
        }
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        memberService.toggleMemberStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái thành viên!");
        return "redirect:/members";
    }
}
