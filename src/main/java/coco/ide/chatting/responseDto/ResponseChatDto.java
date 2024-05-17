package coco.ide.chatting.responseDto;

import coco.ide.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.sql.Blob;
import java.time.LocalDateTime;

@Getter
public class ResponseChatDto {

    private String message;
    private Long memberId;
    private String nickname;
    private Boolean isDeleted;
    private LocalDateTime createdAt;

    @Builder
    public ResponseChatDto(String message, Long memberId, String nickname, Boolean isDeleted, LocalDateTime createdAt) {
        this.message = message;
        this.memberId = memberId;
        this.nickname = nickname;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }
}
