package coco.ide.member.service;

import coco.ide.member.dto.*;

public interface MemberService {
    MemberDto saveMember(MemberRegistrationDto memberDto);
    MemberDto login(LoginDto loginDto);
    void sendCodeToEmail(String email);
    EmailVerificationResult verifyCode(String email, String authCode);
    MemberDto updateMemberProfile(Long memberId, MemberUpdateDto updateDto);
}
