package coco.ide.member.service;

import coco.ide.global.common.BusinessLogicException;
import coco.ide.global.common.ExceptionCode;
import coco.ide.global.common.RedisService;
import coco.ide.member.domain.Member;
import coco.ide.member.dto.EmailVerificationResult;
import coco.ide.member.dto.LoginDto;
import coco.ide.member.dto.MemberDto;
import coco.ide.member.dto.MemberRegistrationDto;
import coco.ide.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private static final String AUTH_CODE_PREFIX = "AUTH_CODE:";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final MailService mailService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    @Override
    public MemberDto saveMember(MemberRegistrationDto memberDto) {
        String hashedPassword = passwordEncoder.encode(memberDto.getPassword());
        Member member = new Member(memberDto.getEmail(), memberDto.getNickname(), hashedPassword);
        Member savedMember = memberRepository.save(member);
        return new MemberDto(savedMember.getMemberId(), savedMember.getEmail(), savedMember.getNickname());
    }

    @Override
    public MemberDto login(LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail());
        if (member != null && passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            return new MemberDto(member.getMemberId(), member.getEmail(), member.getNickname());
        } else {
            return null;
        }
    }

    @Override
    public void sendCodeToEmail(String toEmail) {
        this.checkDuplicatedEmail(toEmail);
        String title = "코코 IDE 이메일 인증 번호 발송";
        String authCode = this.createCode();
        mailService.sendEmail(toEmail, title, authCode);
        redisService.setValues(AUTH_CODE_PREFIX + toEmail,
                authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    private void checkDuplicatedEmail(String email) {
        Optional<Member> member = Optional.ofNullable(memberRepository.findByEmail(email));
        if (member.isPresent()) {
            log.debug("MemberServiceImpl.checkDuplicatedEmail exception occur email: {}", email);
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("MemberServiceImpl.createCode() exception occur");
            throw new BusinessLogicException(ExceptionCode.NO_SUCH_ALGORITHM);
        }
    }

    public EmailVerificationResult verifiedCode(String email, String authCode) {
        this.checkDuplicatedEmail(email);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);

        return EmailVerificationResult.of(authResult);
    }
}
