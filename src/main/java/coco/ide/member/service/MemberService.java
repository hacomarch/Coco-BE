package coco.ide.member.service;

import coco.ide.member.dto.*;

public interface MemberService {
    MemberDto saveMember(MemberRegistrationDto memberDto);
    MemberDto login(LoginDto loginDto);
    MemberDto updateMemberProfile(Long memberId, MemberUpdateDto updateDto);
    MemberDto getMemberById(Long memberId);
    void sendCodeToEmail(String email);
    EmailVerificationResult verifyCode(String email, String authCode);
    boolean verifyPassword(Long memberId, String password);

}