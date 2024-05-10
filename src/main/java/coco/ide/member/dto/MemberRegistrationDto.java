package coco.ide.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberRegistrationDto {
    private String email;
    private String nickname;
    private String password;

    public MemberRegistrationDto() {}

    public MemberRegistrationDto(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }
}
