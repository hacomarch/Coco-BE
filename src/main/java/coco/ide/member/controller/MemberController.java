package coco.ide.member.controller;

import coco.ide.member.domain.Member;
import coco.ide.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    MemberRepository memberRepository;

    @PostMapping("/register")
    public String register(Member member) {
        memberRepository.save(member);
        return ""; //수정
    }
}
