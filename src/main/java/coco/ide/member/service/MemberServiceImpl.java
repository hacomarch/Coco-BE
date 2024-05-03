package coco.ide.member.service;

import coco.ide.member.domain.Member;
import coco.ide.member.dto.LoginDto;
import coco.ide.member.dto.MemberDto;
import coco.ide.member.dto.MemberRegistrationDto;
import coco.ide.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
}
