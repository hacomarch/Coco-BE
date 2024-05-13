package coco.ide.chatting.requestDto;

import lombok.Builder;
import lombok.Getter;

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
