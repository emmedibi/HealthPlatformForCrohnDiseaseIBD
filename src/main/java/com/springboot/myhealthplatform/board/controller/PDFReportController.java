package com.springboot.myhealthplatform.board.controller;

import com.springboot.myhealthplatform.bean.Doctor;
import com.springboot.myhealthplatform.bean.Patient;
import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.bean.PDFReport;
import com.springboot.myhealthplatform.board.model.FileInfo;
import com.springboot.myhealthplatform.board.service.PDFReportService;
import com.springboot.myhealthplatform.service.CustomDoctorDetailService;
import com.springboot.myhealthplatform.service.CustomPatientDetailService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Classe Controller riceve gli user input relativi agli appuntamenti (classe PDFReport),
 * li elabora e invia la richiesta al Service opportuno; allo stesso tempo riceve le informazioni
 * richieste dal Service e le elabora e le invia all'interfaccia utente.
 */
@Controller
public class PDFReportController {

    @Autowired
    private PDFReportService pdfReportService;
    @Autowired
    private CustomPatientDetailService customPatientDetailService;
    @Autowired
    private CustomDoctorDetailService customDoctorDetailService;

    /**
     * Verifica la validità del file in input e salva il documento assieme all'oggetto che rappresenta
     * il paziente che ha caricato il documento.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param file oggetto di tipo MultipartFile che contiene il documento pdf
     * @param user utente loggato a sistema - deve essere un paziente.
     * @return l'indirizzo html della form del documento.
     */
    @PostMapping("/reports/upload")
    public String uploadFile(Model model, @RequestParam("file") MultipartFile file, Principal user){
        String message = "";
        try {
            // Se il file non è presente, lanciare una eccezione.
            if(file.isEmpty()){
                throw new ValidationException("The file is mandatory. Please select one file from your directory");
            }
            // Recupero i dati del paziente loggato e salvo il file.
            Patient patient = customPatientDetailService.findPatientByUserUsername(user.getName());
            pdfReportService.store(file, patient);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            //return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            model.addAttribute("message", message);
        } catch (ValidationException e){
            // Recupero l'eccezione relativa alla validazione.
            message = e.getMessage();
            model.addAttribute("errorMessages", message);
        }   catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            model.addAttribute("message", message);
        }
        return "/reports/uploadForm.html";
    }

    /**
     * Verifica la validità del file in input e salva il documento assieme all'oggetto che rappresenta
     * il paziente e il medico che ha caricato il documento.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param patientId identificativo del paziente di cui stiamo caricando un documento
     * @param file oggetto di tipo MultipartFile che contiene il documento pdf
     * @param user utente loggato a sistema - deve essere un medico.
     * @return la pagina html che raccoglie le informazioni di caricamento con successo del documento.
     */
    @PostMapping("/doctor/reports/upload/{id}")
    public String uploadDoctorExaminationFile(Model model,@PathVariable("id") int patientId, @RequestParam("file") MultipartFile file, Principal user){
        String message = "";

        try {
            // Se il file non è presente, lanciare una eccezione.
            if(file.isEmpty()){
                throw new ValidationException("The file is mandatory. Please select one file from your directory");
            }
            // Recupero i dati del medico e del paziente da inserire nell'oggetto PDFReport che poi
            // Verrà salvato nel database.
            Doctor doctor = customDoctorDetailService.findDoctorByUsername(user.getName());
            Patient patient = customPatientDetailService.findPatientById(patientId);
            pdfReportService.storeByDoctor(file, patient, doctor);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            model.addAttribute("message", message);
        } catch(NullPointerException e){
            message = e.getMessage();
            model.addAttribute("message", message);
        } catch(ValidationException e){
            message = e.getMessage();
            model.addAttribute("message", message);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            model.addAttribute("message", message);
        }
        return "/doctor/doctorInfo.html";
    }

    /**
     * Recupera tutti i file riferiti all'utente loggato.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param user tente loggato a sistema - deve essere un paziente.
     * @return la pagina html dove verranno mostrati tutti i documenti recuperati.
     */
    @GetMapping("/reports/files")
    public String getListFiles(Model model, Principal user) {
        String userUsername = user.getName();
        Patient patient;
        // Verifica ed estrae l'oggetto di classe Patient il cui username corrisponde a quello dell'utente
        // loggato.
        patient = customPatientDetailService.findPatientByUserUsername(userUsername);
        // L'oggetto che ritorna dal metodo getAllFiles è uno Stream di oggetti di classe PDFReport.
        // Recupero lo Stream di oggetti di tipo PDFReport e, per permettere il download successivo
        // dei file, costruisco l'URL a cui scaricare il documento e lo inserisco come attributo
        // String dell'oggeto di classe FileInfo assieme alla descrizione del documento
        // stesso.
        List<FileInfo> fileInfos = pdfReportService.getAllFiles(patient.getId()).map(pdfFile -> {
            // ServletUriComponenetBuilder viene usato per costruire link url in base alla richiesta Http
            // che verrà fatta, che nel nostro caso è la richiesta di download del file.
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/reports/files/")
                    .path(Integer.toString(pdfFile.getId())) // il mio id è un Integer
                    .toUriString();
            // ritorna un oggetto fileInfo costituito dalla descrizione del documento e il suo
            // indirizzo url per il download. Il tutto viene raccolto in una lista (Collector.toList)
            return new FileInfo(pdfFile.getDescription(), fileDownloadUri);
        }).collect(Collectors.toList());
        // la lista di oggetti FileInfo viene inviata al frontEnd per essere visualizzata.
        model.addAttribute("files", fileInfos);
        return "/reports/files";
    }

    /**
     * Richiesta di download di un file da parte del medico.
     * @param id identificativo del file da scaricare.
     * @return il documento scaricato.
     */
    @GetMapping("doctor/reports/files/{id}")
    public ResponseEntity<byte[]> getPatientFile(@PathVariable int id) {
        PDFReport file = pdfReportService.getFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getDescription() + "\"").body(file.getData());
    }

    /**
     * Richiesta di dowload di un file da parte del paziente loggato.
     * @param id identificativo di un suo documento che si vuole scaricare.
     * @return il documento scaricato.
     */
    @GetMapping("/reports/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable int id) {
        PDFReport file = pdfReportService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getDescription() + "\"").body(file.getData());
    }

}
