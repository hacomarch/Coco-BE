package coco.ide.member.service;

import coco.ide.global.common.exception.BusinessLogicException;
import coco.ide.global.common.exception.ExceptionCode;
import coco.ide.global.common.service.RedisService;
import coco.ide.member.domain.Member;
import coco.ide.member.dto.*;
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

    // Id로 Member 가져오기
    @Override
    public MemberDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return new MemberDto(member.getMemberId(), member.getEmail(), member.getNickname());
    }

    // 회원 저장
    @Override
    public MemberDto saveMember(MemberRegistrationDto memberDto) {
        String hashedPassword = passwordEncoder.encode(memberDto.getPassword());
        Member member = new Member(memberDto.getEmail(), memberDto.getNickname(), hashedPassword);
        Member savedMember = memberRepository.save(member);
        return new MemberDto(savedMember.getMemberId(), savedMember.getEmail(), savedMember.getNickname());
    }

    // 로그인
    @Override
    public MemberDto login(LoginDto loginDto) {
        Optional<Member> optionalMember = memberRepository.findByEmail(loginDto.getEmail());
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
                return new MemberDto(member.getMemberId(), member.getEmail(), member.getNickname());
            }
        }
        throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
    }

    // 이메일 중복 체크
    private void checkDuplicatedEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            log.debug("MemberServiceImpl.checkDuplicatedEmail exception occur email: {}", email);
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    // 이메일로 인증 코드 전송
    @Override
    public void sendCodeToEmail(String toEmail) {
        checkDuplicatedEmail(toEmail);
        String title = "[코코 IDE] 이메일 인증 번호 발송";
        String authCode = createCode();
        String text = String.format(
                """
                COCO IDE 가입을 환영합니다!
                이메일 인증을 위한 인증 번호를 발급하였습니다.
                아래의 인증 번호를 입력하여 주세요.
                
                인증 번호: %s
                인증 번호는 30분동안 유효합니다.
                """,
                authCode
        );
        mailService.sendEmail(toEmail, title, text);
        redisService.setValues(AUTH_CODE_PREFIX + toEmail, authCode, Duration.ofMillis(authCodeExpirationMillis));
    }

    // 인증 코드 생성
    private String createCode() {
        int length = 5;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("MemberServiceImpl.createCode() exception occur");
            throw new BusinessLogicException(ExceptionCode.NO_SUCH_ALGORITHM);
        }
    }

    // 인증 코드 검증
    @Override
    public EmailVerificationResult verifyCode(String email, String authCode) {
        checkDuplicatedEmail(email);
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
        return EmailVerificationResult.of(authResult);
    }

    // 회원 정보 수정 전 비밀번호 확인
    @Override
    public boolean verifyPassword(Long memberId, String password) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return passwordEncoder.matches(password, member.getPassword());
    }

    // 회원 정보 수정
    @Override
    public MemberDto updateMemberProfile(Long memberId, MemberUpdateDto updateDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        // 닉네임 수정
        if (updateDto.getNickname() != null && !updateDto.getNickname().isEmpty()) {
            member.setNickname(updateDto.getNickname());
        }
        // 비밀번호 수정
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(updateDto.getPassword());
            member.setPassword(hashedPassword);
        }
        // 수정된 회원 정보 저장
        Member updatedMember = memberRepository.save(member);
        return new MemberDto(updatedMember.getMemberId(), updatedMember.getEmail(), updatedMember.getNickname());
    }



}