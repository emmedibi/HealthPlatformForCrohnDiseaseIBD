package com.springboot.myhealthplatform.board.service;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.User;
import com.springboot.myhealthplatform.board.bean.Message;
import com.springboot.myhealthplatform.board.repository.MessageRepository;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.Optional;
/**
 * Classe che comunica con le classi Repository per estrarre o registrare dati provenienti dai Controller.
 * Service dedicato alla gestione dei messaggi (Message class).
 */
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Ritorna tutti i messaggi inviati o ricevuti allo user indicato
     * @param userId
     * @return
     */
    public List<Message> findAllByUserId(Long userId){
        return messageRepository.findAllBySenderIdOrRecipientId(userId, userId);
    }

    /**
     * Salva il messaggio nel database.
     * @param message oggetto che contiene i dati da inserire nel database.
     * @return l'oggetto che contiene i dati inseriti nel database.
     */
    public Message save(Message message){
        return messageRepository.save(message);
    }

    /**
     * Ritorna tutti i messaggi ancora non letti per uno specifico destinatario.
     * @param userId identificatore dell'utente destinatario.
     * @return se presente, la lista dei messaggi che corrispondono ai criteri di ricerca.
     */
    public List<Message> findAllNotReadMessagesByUserId(Long userId){
        return messageRepository.findAllByRecipientIdAndReadMessageIsFalse(userId);
    }

    /**
     * Ritorna tutti i messaggi ancora non letti per uno specifico mittente.
     * @param userId identificatore dell'utente mittente.
     * @return se presente, la lista dei messaggi che corrispondono ai criteri di ricerca.
     */
    public List<Message> findAllNotReadMessageSendByUserId(Long userId){
        return messageRepository.findAllBySenderIdAndReadMessageIsFalse(userId);
    }

    /** -- NOT USED --
     * Ritorna un oggetto di classe Message che ha id uguale a quello passato in input.
     * @param messageId identificatore di un messaggio.
     * @return se presente, il messaggio che corrisponde ai criteri di ricerca.
     * @throws Exception viene lanciata se il messaggio non è presente nel database.
     */
    public Optional<Message> getMessage(int messageId) throws Exception{
        Optional<Message> message = messageRepository.findById(messageId);
        if(message.isPresent()) {
            return message;
        } else throw new Exception();
    }

    /**
     * Recupera un messaggio in base al suo id.
     * @param messageId identificatore di un messaggio.
     * @return se presente, il messaggio che corrisponde ai criteri di ricerca
     */
    public Message getMessageValue(int messageId){
        return messageRepository.findById(messageId).orElseThrow(() -> new NullPointerException("Message not found with messageId " + messageId));
    }


    /**
     * Viene recuperato il messaggio tramite il suo id e viene verificato che l'id del medico
     * corrisponda al destinatario del messaggio.
     * @param messageId identificativo del messaggio
     * @param doctor utente che sta cercando di modificare lo stato del messaggio.
     * @throws Exception se il messaggio non ha come destinatario il medico passato in input, allora
     * viene lanciata una eccezione e l'operazione viene annullata.
     */
    public void setReadMessageTrue(int messageId, Doctor doctor) throws Exception{
        Optional<Message> optionalMessage = this.getMessage(messageId);
        // Se il messaggio è presente, nella funzione lambda, verifichiamo che il destinatario
        // sia effettivamente il medico loggato
        // Se ok, procedo a modificare l'attributo readMessage e salvo.
        optionalMessage.ifPresent(message -> {
            try {
                if (message.getRecipient().getId() == doctor.getUser().getId()) {
                    message.setReadMessage(true);
                    this.save(message);
                }
            } catch(Exception e) {
                throw e;
            }
        });
    }

    /**
     * Recupera il messaggio corrispondente all'id in input e ne modifica l'attributo
     * readMessage in true.
     * @param messageId identificatore del messaggio
     * @throws Exception lancia un a eccezione se viene riscontrato qualche errore.
     */
    public void setReadMessageTrueFromNurse(int messageId) throws Exception {
        Optional<Message> optionalMessage = this.getMessage(messageId);
        // Se il messaggio è presente, nella funzione lambda
        // procedo a modificare l'attributo readMessage e salvo.
        optionalMessage.ifPresent(message -> {
            try{
                message.setReadMessage(true);
                this.save(message);
            } catch(Exception e){
                throw e;
            }
        });
        optionalMessage.orElseThrow( () -> new NullPointerException("Nessun messaggio trovato con questo id"));
    }

    /**
     * Recupera il messaggio che corrispone all'id del messaggio e l'id dell'utente indicati in
     * input
     * @param messageId identificatore del messaggio
     * @param user utente loggato.
     * @return viene restituito il messaggio che soddisfa i criteri di ricerca
     * @throws NullPointerException viene lanciato se non viene trovato il messaggio che
     * deve soddisfare i criteri di ricerca.
     */
    public Message checkRecipientMessage(int messageId, User user) throws NullPointerException{
        Message message;
        try {
            message = messageRepository.findByIdAndRecipientId(messageId, user.getId());
            if(message == null){
                throw new NullPointerException("Messaggio non recuperato per questi criteri di ricerca");
            }
        } catch (NullPointerException e ){
            throw e;
        }
        return message;
    }


}
