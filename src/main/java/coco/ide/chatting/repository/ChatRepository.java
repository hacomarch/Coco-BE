package coco.ide.chatting.repository;

import coco.ide.chatting.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
