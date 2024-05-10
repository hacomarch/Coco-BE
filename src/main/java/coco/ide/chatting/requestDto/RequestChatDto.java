package coco.ide.chatting.requestDto;

import coco.ide.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import org.w3c.dom.Text;

import java.sql.Blob;

@Getter
public class RequestChatDto {

    private String memberId;
    private String message;

    @Builder
    public RequestChatDto(String memberId, String message) {
        this.memberId = memberId;
        this.message = message;
    }
}
