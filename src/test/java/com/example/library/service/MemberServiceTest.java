package com.example.library.service;

import com.example.library.dto.form.MemberForm;
import com.example.library.entity.Member;
import com.example.library.entity.enums.MemberStatus;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.repository.MemberRepository;
import com.example.library.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(1L, "Nguyễn Văn An", "nguyenvanan@gmail.com", "0901234567", MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("Tạo thành viên mới thành công")
    void testCreateMember_Success() {
        MemberForm form = new MemberForm(null, "Trần Thị Bình", "tranbinh@gmail.com", "0912345678", MemberStatus.ACTIVE);
        when(memberRepository.existsByEmailIgnoreCase("tranbinh@gmail.com")).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member created = memberService.createMember(form);

        assertNotNull(created);
        assertEquals("Trần Thị Bình", created.getFullName());
        assertEquals("tranbinh@gmail.com", created.getEmail());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("Tạo thành viên thất bại khi email bị trùng")
    void testCreateMember_DuplicateEmail() {
        MemberForm form = new MemberForm(null, "Nguyễn Văn An", "nguyenvanan@gmail.com", "0901234567", MemberStatus.ACTIVE);
        when(memberRepository.existsByEmailIgnoreCase("nguyenvanan@gmail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> memberService.createMember(form));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("Khóa và mở khóa thành viên")
    void testToggleMemberStatus() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // ACTIVE -> BLOCKED
        memberService.toggleMemberStatus(1L);
        assertEquals(MemberStatus.BLOCKED, member.getStatus());

        // BLOCKED -> ACTIVE
        memberService.toggleMemberStatus(1L);
        assertEquals(MemberStatus.ACTIVE, member.getStatus());

        verify(memberRepository, times(2)).save(member);
    }
}
