package coco.ide.member.service;

import coco.ide.member.domain.Member;
import coco.ide.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Member saveMember(Member member) {
        // 수정 예정
        return memberRepository.save(member);
    }


}