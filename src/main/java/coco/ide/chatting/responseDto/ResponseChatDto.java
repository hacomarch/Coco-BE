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
    private LocalDateTime createdAt;

    @Builder
    public ResponseChatDto(String message, Long memberId, LocalDateTime createdAt) {
        this.message = message;
        this.memberId = memberId;
        this.createdAt = createdAt;
    }
}
