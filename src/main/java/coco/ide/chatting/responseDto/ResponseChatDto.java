package coco.ide.chatting.responseDto;

import coco.ide.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import org.w3c.dom.Text;

import java.sql.Blob;
import java.time.LocalDateTime;

@Getter
public class ResponseChatDto {

    private String message;
    private Member member;
    private LocalDateTime createdAt;

    @Builder
    public ResponseChatDto(String message, Member member, LocalDateTime createdAt) {
        this.message = message;
        this.member = member;
        this.createdAt = createdAt;
    }
}
