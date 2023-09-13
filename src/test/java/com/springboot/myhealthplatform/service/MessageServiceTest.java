package com.springboot.myhealthplatform.service;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.board.bean.Message;
import com.springboot.myhealthplatform.board.repository.MessageRepository;
import com.springboot.myhealthplatform.board.service.MessageService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    private AutoCloseable closeable;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    /** -- SUPERATO --
     * Verifica che dato un id, venga restituito il messaggio che corrisponde ai criteri di ricerca.
     * @throws Exception
     */
    @Test
    public void it_should_return_a_message() throws Exception {
        int messageId = 1;
        User userDoctor = new User();
        Doctor doctor = new Doctor();
        doctor.setUser(userDoctor);
        userDoctor.setId(1L);
        Optional<Message> message = Optional.of(new Message());
        message.ifPresent(mex -> {
            mex.setId(messageId);
            mex.setReadMessage(false);
            mex.setRecipient(userDoctor);
        });
        when(messageRepository.findById(ArgumentMatchers.anyInt())).thenReturn(message);
        Optional<Message> messageOptional = messageService.getMessage(messageId);
        assertTrue(messageOptional.isPresent());
    }

    /** --SUPERATO --
     * Verifica che se il medico segna come letto un messaggio, l'attributo ReadMessage del messaggio
     * assuma valore "true".
     * @throws Exception
     */
    @Test
    public void it_should_set_ReadMessage_true() throws Exception {
        int messageId = 1;
        User userDoctor = new User();
        Doctor doctor = new Doctor();
        doctor.setUser(userDoctor);
        userDoctor.setId(1L);
        Optional<Message> message = Optional.of(new Message());
        message.ifPresent(mex -> {
            mex.setId(messageId);
            mex.setReadMessage(false);
            mex.setRecipient(userDoctor);
        });
        when(messageRepository.findById(ArgumentMatchers.anyInt())).thenReturn(message);
        Optional<Message> messageOptional = messageService.getMessage(messageId);
        messageService.setReadMessageTrue(messageId, doctor);
        assertTrue(messageOptional.isPresent());
        Message messageSaved = messageOptional.get();
        assertTrue(messageSaved.isReadMessage()==true);
    }

    /** -- SUPERATO --
     * Verifica che, se un infermiere segna come letto un messaggio, venga modificato l'attributo
     * ReadMessage associandogli il valore True.
     * @throws Exception
     */
    @Test
    public void it_should_set_ReadMessage_true_from_Nurse() throws Exception {
        int messageId = 1;
        User userDoctor = new User();
        Doctor doctor = new Doctor();
        doctor.setUser(userDoctor);
        userDoctor.setId(1L);
        Optional<Message> message = Optional.of(new Message());
        message.ifPresent(mex -> {
            mex.setId(messageId);
            mex.setReadMessage(false);
            mex.setRecipient(userDoctor);
        });
        when(messageRepository.findById(ArgumentMatchers.anyInt())).thenReturn(message);
        Optional<Message> messageOptional = messageService.getMessage(messageId);
        messageService.setReadMessageTrueFromNurse(messageId);
        assertTrue(messageOptional.isPresent());
        Message messageSaved = messageOptional.get();
        assertTrue(messageSaved.isReadMessage()==true);
    }

    /** -- SUPERATO --
     * Verifica che, dato l'id di un messaggio e l'utente che invia il messaggio, restituisce
     * il messaggio corrispondente.
     * @throws Exception
     */
    @Test
    public void it_should_return_message_with_messageId_and_patientId_given() throws Exception {
        Message message = new Message();
        int messageId = 1;
        message.setId(messageId);
        User user = new User();
        user.setId(1L);
        message.setSender(user);
        when(messageRepository.findByIdAndRecipientId(messageId, user.getId())).thenReturn(message);

        Message messageSaved = messageService.checkRecipientMessage(messageId, user);

        assertTrue(messageSaved.getId() == message.getId());

    }

    /** -- SUPERATO --
     * Verifico che, se non viene recuperato nessun messaggio secondo i criteri di ricerca, venga
     * lanciata una eccezione.
     * @throws Exception
     */
    @Test
    public void it_should_return_exception_with_messageId_and_patientId_given_because_message_is_null() throws Exception {
        Message message = new Message();
        int messageId = 1;
        message.setId(messageId);
        User user = new User();
        user.setId(1L);
        message.setSender(user);
        when(messageRepository.findByIdAndRecipientId(messageId, user.getId())).thenReturn(null);

        assertThrows(Exception.class, () -> messageService.checkRecipientMessage(messageId, user));

    }

}
