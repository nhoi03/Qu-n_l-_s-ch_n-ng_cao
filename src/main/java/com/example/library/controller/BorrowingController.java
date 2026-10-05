package com.example.library.controller;

import com.example.library.dto.form.BorrowingCreateForm;
import com.example.library.dto.form.BorrowingItemForm;
import com.example.library.dto.form.ReturnForm;
import com.example.library.entity.Borrowing;
import com.example.library.entity.enums.BorrowingStatus;
import com.example.library.exception.BusinessRuleException;
import com.example.library.service.BookService;
import com.example.library.service.BorrowingService;
import com.example.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final MemberService memberService;
    private final BookService bookService;

    public BorrowingController(BorrowingService borrowingService,
                               MemberService memberService,
                               BookService bookService) {
        this.borrowingService = borrowingService;
        this.memberService = memberService;
        this.bookService = bookService;
    }

    @GetMapping
    public String listBorrowings(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) BorrowingStatus status,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        Page<Borrowing> borrowingPage = borrowingService.searchBorrowings(keyword, status, page, size);

        model.addAttribute("borrowings", borrowingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", borrowingPage.getTotalPages());
        model.addAttribute("totalElements", borrowingPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("statuses", BorrowingStatus.values());
        model.addAttribute("today", LocalDate.now());

        return "borrowings/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        BorrowingCreateForm form = new BorrowingCreateForm();
        model.addAttribute("borrowingForm", form);
        model.addAttribute("members", memberService.getActiveMembers());
        model.addAttribute("books", bookService.getAvailableBooksForBorrowing());
        return "borrowings/form";
    }

    @PostMapping
    public String createBorrowing(@Valid @ModelAttribute("borrowingForm") BorrowingCreateForm form,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("members", memberService.getActiveMembers());
            model.addAttribute("books", bookService.getAvailableBooksForBorrowing());
            return "borrowings/form";
        }

        try {
            Borrowing borrowing = borrowingService.createBorrowing(form);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo phiếu mượn #" + borrowing.getId() + " thành công!");
            return "redirect:/borrowings/" + borrowing.getId();
        } catch (BusinessRuleException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("members", memberService.getActiveMembers());
            model.addAttribute("books", bookService.getAvailableBooksForBorrowing());
            return "borrowings/form";
        }
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        Borrowing borrowing = borrowingService.getBorrowingById(id);
        model.addAttribute("borrowing", borrowing);
        model.addAttribute("today", LocalDate.now());
        return "borrowings/detail";
    }

    @GetMapping("/{id}/return")
    public String showReturnForm(@PathVariable Long id, Model model) {
        ReturnForm form = borrowingService.prepareReturnForm(id);
        model.addAttribute("returnForm", form);
        return "borrowings/return";
    }

    @PostMapping("/{id}/return")
    public String processReturn(@PathVariable Long id,
                                @Valid @ModelAttribute("returnForm") ReturnForm form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "borrowings/return";
        }

        try {
            Borrowing borrowing = borrowingService.returnBooks(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Xử lý trả sách cho phiếu #" + borrowing.getId() + " thành công!");
            return "redirect:/borrowings/" + borrowing.getId();
        } catch (BusinessRuleException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "borrowings/return";
        }
    }

    @PostMapping("/{id}/return-all")
    public String returnAll(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Borrowing borrowing = borrowingService.returnAllBooks(id, LocalDate.now());
            redirectAttributes.addFlashAttribute("successMessage", "Đã trả toàn bộ sách trong phiếu #" + borrowing.getId() + "!");
            return "redirect:/borrowings/" + borrowing.getId();
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/borrowings/" + id;
        }
    }
}
