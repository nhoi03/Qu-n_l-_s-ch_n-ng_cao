package com.example.library.service.impl;

import com.example.library.dto.form.MemberForm;
import com.example.library.entity.Member;
import com.example.library.entity.enums.MemberStatus;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.MemberRepository;
import com.example.library.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Member> searchMembers(String keyword, MemberStatus status, int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy.trim() : "createdAt";
        if (!List.of("id", "fullName", "email", "phone", "createdAt", "status").contains(sortProperty)) {
            sortProperty = "id";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        return memberRepository.searchMembers(cleanKeyword, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> getActiveMembers() {
        return memberRepository.findByStatusOrderByFullNameAsc(MemberStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với ID: " + id));
    }

    @Override
    public Member createMember(MemberForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (memberRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email '" + email + "' đã tồn tại trong hệ thống");
        }

        Member member = new Member();
        member.setFullName(form.getFullName().trim());
        member.setEmail(email);
        member.setPhone(form.getPhone() != null ? form.getPhone().trim() : null);
        member.setStatus(form.getStatus() != null ? form.getStatus() : MemberStatus.ACTIVE);

        return memberRepository.save(member);
    }

    @Override
    public Member updateMember(Long id, MemberForm form) {
        Member member = getMemberById(id);
        String email = form.getEmail().trim().toLowerCase();

        if (memberRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Email '" + email + "' đã được sử dụng bởi thành viên khác");
        }

        member.setFullName(form.getFullName().trim());
        member.setEmail(email);
        member.setPhone(form.getPhone() != null ? form.getPhone().trim() : null);
        if (form.getStatus() != null) {
            member.setStatus(form.getStatus());
        }

        return memberRepository.save(member);
    }

    @Override
    public void toggleMemberStatus(Long id) {
        Member member = getMemberById(id);
        if (member.getStatus() == MemberStatus.ACTIVE) {
            member.setStatus(MemberStatus.BLOCKED);
        } else {
            member.setStatus(MemberStatus.ACTIVE);
        }
        memberRepository.save(member);
    }
}
