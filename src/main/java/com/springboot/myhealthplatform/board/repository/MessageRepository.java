package com.springboot.myhealthplatform.board.repository;

import com.springboot.myhealthplatform.board.bean.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Metodi utili al recupero e salvataggio dei dati riferiti alla classe Message nel database.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findAllBySenderId(Long senderId);
    List<Message> findAllByRecipientId(Long recipientId);
    List<Message> findAllByRecipientIdAndReadMessageIsTrue(Long recipientId);

    /**
     * Recupera tutti i messaggi in base al destinatario e che non sono ancora stati letti
     * @param recipientId identificativo del destinatario
     * @return lista di messaggi
     */
    List<Message> findAllByRecipientIdAndReadMessageIsFalse(Long recipientId);

    /**
     * Recupera tutti i messaggi in base all'id del mittente e del destinatario.
     * @param senderId identificativo del mittente
     * @param recipientId identificativo del destinatario
     * @return lista di messaggi
     */
    List<Message> findAllBySenderIdOrRecipientId(Long senderId, Long recipientId);
    Message findByIdAndRecipientId(int messageId, Long recipientId);

    /**
     * Recupera tutti i messaggi in base al mittente e che non sono ancora stati letti
     * @param senderId identificativo del mittente
     * @return lista di messaggi
     */
    List<Message> findAllBySenderIdAndReadMessageIsFalse(Long senderId);
}
