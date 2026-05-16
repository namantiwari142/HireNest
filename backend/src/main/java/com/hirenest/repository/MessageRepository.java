package com.hirenest.repository;

import com.hirenest.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :userId AND m.receiver.id = :partnerId) OR " +
           "(m.sender.id = :partnerId AND m.receiver.id = :userId) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("userId") Long userId, @Param("partnerId") Long partnerId);

    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END " +
           "FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Long> findConversationPartnerIds(@Param("userId") Long userId);
}
