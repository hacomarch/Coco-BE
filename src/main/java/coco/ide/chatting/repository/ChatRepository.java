package coco.ide.chatting.repository;

import coco.ide.chatting.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(value = "SELECT * FROM chat WHERE message LIKE %?1% AND is_deleted = FALSE", nativeQuery = true)
    List<Chat> findMessagesContaining(String word);
}
