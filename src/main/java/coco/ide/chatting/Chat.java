package coco.ide.chatting;

import coco.ide.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.sql.Blob;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "is_deleted")
    private Boolean isDeleted;;

    @Column
    private LocalDateTime createdAt;

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Builder
    public Chat(String message, Member member, Boolean isDeleted, LocalDateTime createdAt) {
        this.message = message;
        this.member = member;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }
}
