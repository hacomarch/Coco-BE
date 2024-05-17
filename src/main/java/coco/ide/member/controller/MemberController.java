package coco.ide.member.controller;

import coco.ide.global.common.dto.SingleResponseDto;
import coco.ide.global.validation.CustomEmail;
import coco.ide.member.dto.*;
import coco.ide.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 인증번호 이메일 보내기
    @PostMapping("/emails/verification-requests")
    public ResponseEntity<Void> sendMessage(@RequestParam("email") @Valid @CustomEmail String email) {
        memberService.sendCodeToEmail(email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 인증번호 확인
    @GetMapping("/emails/verifications")
    public ResponseEntity<SingleResponseDto<EmailVerificationResult>> verificationEmail(@RequestParam("email") @Valid @CustomEmail String email,
                                                                                        @RequestParam("code") String authCode) {
        EmailVerificationResult response = memberService.verifyCode(email, authCode);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<MemberDto> register(@RequestBody MemberRegistrationDto memberRegistrationDto) {
        MemberDto savedMember = memberService.saveMember(memberRegistrationDto);
        if (savedMember != null && savedMember.getMemberId() != null) {
            return ResponseEntity.ok(savedMember);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberDto> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        MemberDto memberDto = memberService.login(loginDto);
        if (memberDto != null) {
            HttpSession session = request.getSession(true); // 세션 생성
            session.setAttribute("memberId", memberDto.getMemberId()); // 세션에 사용자 ID 저장
            return ResponseEntity.ok(memberDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }

    // 회원정보 수정 전 비밀번호 확인
//    @PostMapping("/verify-password")
//    public ResponseEntity<SingleResponseDto<String>> verifyPassword(@RequestBody VerifyPasswordDto verifyPasswordDto, HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            Long memberId = (Long) session.getAttribute("memberId");
//            if (memberId != null) {
//                boolean isVerified = memberService.verifyPassword(memberId, verifyPasswordDto.getPassword());
//                if (isVerified) {
//                    return ResponseEntity.ok(new SingleResponseDto<>("Password verified successfully"));
//                } else {
//                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                            .body(new SingleResponseDto<>("Incorrect password"));
//                }
//            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(new SingleResponseDto<>("User not authenticated"));
//    }

    @PostMapping("/verify-password")
    public ResponseEntity<SingleResponseDto<String>> verifyPassword(@RequestBody VerifyPasswordDto verifyPasswordDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long memberId = (Long) session.getAttribute("memberId");
            if (memberId != null) {
                boolean isVerified = memberService.verifyPassword(memberId, verifyPasswordDto.getPassword());
                if (isVerified) {
                    return ResponseEntity.ok(new SingleResponseDto<>("Password verified successfully"));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new SingleResponseDto<>("Incorrect password"));
                }
            } else {
                log.info("memberId is null");
            }
        } else {
            log.info("Session is null");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new SingleResponseDto<>("User not authenticated"));
    }


    // 회원정보 수정
    @PutMapping("/profile")
    public ResponseEntity<MemberDto> updateProfile(@RequestBody @Valid MemberUpdateDto updateDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long memberId = (Long) session.getAttribute("memberId");
            if (memberId != null) {
                MemberDto updateMember = memberService.updateMemberProfile(memberId, updateDto);
                return ResponseEntity.ok(updateMember);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // 마이 페이지
    @GetMapping("/myPage")
    public ResponseEntity<MemberDto> myPage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long memberId = (Long) session.getAttribute("memberId");
            if (memberId != null) {
                MemberDto memberDto = memberService.getMemberById(memberId);
                return ResponseEntity.ok(memberDto);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}