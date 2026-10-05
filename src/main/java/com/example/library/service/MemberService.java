package com.example.library.service;

import com.example.library.dto.form.MemberForm;
import com.example.library.entity.Member;
import com.example.library.entity.enums.MemberStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MemberService {
    Page<Member> searchMembers(String keyword, MemberStatus status, int page, int size, String sortBy, String sortDir);
    List<Member> getActiveMembers();
    Member getMemberById(Long id);
    Member createMember(MemberForm form);
    Member updateMember(Long id, MemberForm form);
    void toggleMemberStatus(Long id);
}
