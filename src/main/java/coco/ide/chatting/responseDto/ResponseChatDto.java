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
    private Boolean isDeleted;
    private LocalDateTime createdAt;

    @Builder
    public ResponseChatDto(String message, Long memberId, Boolean isDeleted, LocalDateTime createdAt) {
        this.message = message;
        this.memberId = memberId;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }
}
