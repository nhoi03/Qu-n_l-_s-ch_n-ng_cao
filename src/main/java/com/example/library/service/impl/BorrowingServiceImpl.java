package com.example.library.service.impl;

import com.example.library.dto.form.BorrowingCreateForm;
import com.example.library.dto.form.BorrowingItemForm;
import com.example.library.dto.form.ReturnForm;
import com.example.library.dto.form.ReturnItemForm;
import com.example.library.entity.Book;
import com.example.library.entity.Borrowing;
import com.example.library.entity.BorrowingDetail;
import com.example.library.entity.Member;
import com.example.library.entity.enums.BookStatus;
import com.example.library.entity.enums.BorrowingStatus;
import com.example.library.entity.enums.MemberStatus;
import com.example.library.exception.BusinessRuleException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingDetailRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.BorrowingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class BorrowingServiceImpl implements BorrowingService {

    public static final long DAILY_FINE_PER_BOOK = 5000L;
    public static final int DEFAULT_BORROW_DAYS = 14;

    private final BorrowingRepository borrowingRepository;
    private final BorrowingDetailRepository borrowingDetailRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository,
                                BorrowingDetailRepository borrowingDetailRepository,
                                MemberRepository memberRepository,
                                BookRepository bookRepository) {
        this.borrowingRepository = borrowingRepository;
        this.borrowingDetailRepository = borrowingDetailRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Borrowing> searchBorrowings(String keyword, BorrowingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowDate", "id"));
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return borrowingRepository.searchBorrowings(cleanKeyword, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Borrowing getBorrowingById(Long id) {
        return borrowingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + id));
    }

    @Override
    public Borrowing createBorrowing(BorrowingCreateForm form) {
        Member member = memberRepository.findById(form.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với ID: " + form.getMemberId()));

        if (member.getStatus() == MemberStatus.BLOCKED) {
            throw new BusinessRuleException("Thành viên '" + member.getFullName() + "' đang bị KHÓA, không thể mượn sách");
        }

        if (form.getItems() == null || form.getItems().isEmpty()) {
            throw new BusinessRuleException("Phiếu mượn phải có ít nhất một cuốn sách");
        }

        // Aggregate quantities by bookId
        Map<Long, Integer> bookQuantityMap = new LinkedHashMap<>();
        for (BorrowingItemForm item : form.getItems()) {
            if (item.getBookId() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                bookQuantityMap.merge(item.getBookId(), item.getQuantity(), Integer::sum);
            }
        }

        if (bookQuantityMap.isEmpty()) {
            throw new BusinessRuleException("Vui lòng chọn ít nhất một cuốn sách hợp lệ với số lượng lớn hơn 0");
        }

        LocalDate borrowDate = form.getBorrowDate() != null ? form.getBorrowDate() : LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(DEFAULT_BORROW_DAYS);

        Borrowing borrowing = new Borrowing();
        borrowing.setMember(member);
        borrowing.setBorrowDate(borrowDate);
        borrowing.setDueDate(dueDate);
        borrowing.setStatus(BorrowingStatus.BORROWED);
        borrowing.setTotalFine(BigDecimal.ZERO);

        for (Map.Entry<Long, Integer> entry : bookQuantityMap.entrySet()) {
            Long bookId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + bookId));

            if (book.getStatus() != BookStatus.ACTIVE) {
                throw new BusinessRuleException("Sách '" + book.getTitle() + "' hiện không ở trạng thái hoạt động (Status: " + book.getStatus() + ")");
            }

            if (book.getAvailableQuantity() < requestedQuantity) {
                throw new BusinessRuleException("Sách '" + book.getTitle() + "' không đủ số lượng để mượn (Còn: " +
                        book.getAvailableQuantity() + ", Yêu cầu: " + requestedQuantity + ")");
            }

            // Deduct available quantity
            book.setAvailableQuantity(book.getAvailableQuantity() - requestedQuantity);
            bookRepository.save(book);

            BorrowingDetail detail = new BorrowingDetail();
            detail.setBook(book);
            detail.setQuantity(requestedQuantity);
            detail.setReturnedQuantity(0);
            detail.setFineAmount(BigDecimal.ZERO);

            borrowing.addDetail(detail);
        }

        return borrowingRepository.save(borrowing);
    }

    @Override
    public Borrowing returnBooks(Long borrowingId, ReturnForm form) {
        Borrowing borrowing = getBorrowingById(borrowingId);
        if (borrowing.getStatus() == BorrowingStatus.RETURNED) {
            throw new BusinessRuleException("Phiếu mượn này đã được trả hoàn tất trước đó");
        }

        LocalDate returnDate = form.getReturnDate() != null ? form.getReturnDate() : LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(borrowing.getDueDate(), returnDate);
        boolean isOverdue = daysLate > 0;

        int totalReturnedInThisAction = 0;

        for (ReturnItemForm itemForm : form.getItems()) {
            if (itemForm.getDetailId() == null) continue;

            BorrowingDetail detail = borrowing.getDetails().stream()
                    .filter(d -> d.getId().equals(itemForm.getDetailId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng chi tiết phiếu mượn ID: " + itemForm.getDetailId()));

            int returnQty = itemForm.getReturnQuantity() != null ? itemForm.getReturnQuantity() : 0;
            if (returnQty < 0) {
                throw new BusinessRuleException("Số lượng trả không thể là số âm");
            }

            int remaining = detail.getRemainingQuantity();
            if (returnQty > remaining) {
                throw new BusinessRuleException("Số lượng trả (" + returnQty + ") vượt quá số lượng chưa trả (" + remaining + ") của sách: " + detail.getBook().getTitle());
            }

            if (returnQty > 0) {
                totalReturnedInThisAction += returnQty;

                // Return to inventory
                Book book = detail.getBook();
                int newAvailable = book.getAvailableQuantity() + returnQty;
                if (newAvailable > book.getTotalQuantity()) {
                    newAvailable = book.getTotalQuantity();
                }
                book.setAvailableQuantity(newAvailable);
                bookRepository.save(book);

                // Update detail
                detail.setReturnedQuantity(detail.getReturnedQuantity() + returnQty);

                if (isOverdue) {
                    BigDecimal fine = BigDecimal.valueOf(daysLate * returnQty * DAILY_FINE_PER_BOOK);
                    detail.setFineAmount(detail.getFineAmount().add(fine));
                }
            }
        }

        if (totalReturnedInThisAction == 0) {
            throw new BusinessRuleException("Vui lòng nhập số lượng sách cần trả lớn hơn 0");
        }

        // Recalculate total fine
        BigDecimal totalFine = borrowing.getDetails().stream()
                .map(BorrowingDetail::getFineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        borrowing.setTotalFine(totalFine);

        // Check if all books in borrowing are returned
        boolean allReturned = borrowing.getDetails().stream()
                .allMatch(d -> Objects.equals(d.getReturnedQuantity(), d.getQuantity()));

        if (allReturned) {
            borrowing.setStatus(BorrowingStatus.RETURNED);
            borrowing.setReturnedDate(returnDate);
        }

        return borrowingRepository.save(borrowing);
    }

    @Override
    public Borrowing returnAllBooks(Long borrowingId, LocalDate returnDate) {
        Borrowing borrowing = getBorrowingById(borrowingId);
        ReturnForm form = prepareReturnForm(borrowingId);
        if (returnDate != null) {
            form.setReturnDate(returnDate);
        }
        for (ReturnItemForm item : form.getItems()) {
            item.setReturnQuantity(item.getRemainingQuantity());
        }
        return returnBooks(borrowingId, form);
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnForm prepareReturnForm(Long borrowingId) {
        Borrowing borrowing = getBorrowingById(borrowingId);
        ReturnForm form = new ReturnForm();
        form.setBorrowingId(borrowing.getId());
        form.setMemberFullName(borrowing.getMember().getFullName());
        form.setMemberEmail(borrowing.getMember().getEmail());
        form.setBorrowDate(borrowing.getBorrowDate());
        form.setDueDate(borrowing.getDueDate());
        form.setReturnDate(LocalDate.now());

        List<ReturnItemForm> itemForms = new ArrayList<>();
        for (BorrowingDetail detail : borrowing.getDetails()) {
            ReturnItemForm item = new ReturnItemForm();
            item.setDetailId(detail.getId());
            item.setBookId(detail.getBook().getId());
            item.setBookTitle(detail.getBook().getTitle());
            item.setBookIsbn(detail.getBook().getIsbn());
            item.setQuantity(detail.getQuantity());
            item.setReturnedQuantity(detail.getReturnedQuantity());
            item.setRemainingQuantity(detail.getRemainingQuantity());
            item.setReturnQuantity(detail.getRemainingQuantity());
            itemForms.add(item);
        }
        form.setItems(itemForms);
        return form;
    }
}
