package coco.ide.member.service;

import coco.ide.member.dto.EmailVerificationResult;
import coco.ide.member.dto.LoginDto;
import coco.ide.member.dto.MemberDto;
import coco.ide.member.dto.MemberRegistrationDto;

public interface MemberService {
    MemberDto saveMember(MemberRegistrationDto memberDto);
    MemberDto login(LoginDto loginDto);
    void sendCodeToEmail(String email);
    EmailVerificationResult verifiedCode(String email, String authCode);
}
