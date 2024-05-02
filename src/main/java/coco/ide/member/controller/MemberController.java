package coco.ide.member.controller;

import coco.ide.member.domain.Member;
import coco.ide.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    MemberService memberService;

    @PostMapping("/register")
    public String register(Member member) {
        memberService.saveMember(member);
        return ""; //수정
    }
}
