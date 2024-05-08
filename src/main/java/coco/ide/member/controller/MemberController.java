package coco.ide.member.controller;

import coco.ide.global.common.SingleResponseDto;
import coco.ide.global.validation.CustomEmail;
import coco.ide.member.dto.EmailVerificationResult;
import coco.ide.member.dto.LoginDto;
import coco.ide.member.dto.MemberDto;
import coco.ide.member.dto.MemberRegistrationDto;
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

    @PostMapping("/register")
    public ResponseEntity<MemberDto> register(@RequestBody MemberRegistrationDto memberRegistrationDto) {
        MemberDto savedMember = memberService.saveMember(memberRegistrationDto);
        if (savedMember != null && savedMember.getMemberId() != null) {
            return ResponseEntity.ok(savedMember);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<MemberDto> login(@RequestBody LoginDto loginDto) {
        MemberDto memberDto = memberService.login(loginDto);
        if (memberDto != null) {
            return ResponseEntity.ok(memberDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/emails/verification-requests")
    public ResponseEntity<Void> sendMessage(@RequestParam("email") @Valid @CustomEmail String email) {
        memberService.sendCodeToEmail(email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/emails/verifications")
    public ResponseEntity<SingleResponseDto<EmailVerificationResult>> verificationEmail(@RequestParam("email") @Valid @CustomEmail String email,
                                            @RequestParam("code") String authCode) {
        EmailVerificationResult response = memberService.verifiedCode(email, authCode);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }
}
