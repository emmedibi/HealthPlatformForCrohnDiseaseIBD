package com.springboot.myhealthplatform.board.controller;

import com.springboot.myhealthplatform.board.bean.BloodTestReport;
import com.springboot.myhealthplatform.board.model.FileInfo;
import com.springboot.myhealthplatform.board.service.BloodTestReportService;
import com.springboot.myhealthplatform.board.service.PDFReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Classe Controller che richiede i dati relativi alle classi BloodTestReport e PDFReport e li
 * invia al frontEnd.
 */
@Controller
public class ReportsController {
    @Autowired
    private BloodTestReportService bloodTestReportService;
    @Autowired
    private PDFReportService pdfReportService;

    /**
     * Recupera tutti i documenti di tipo PDFReport e di tipo BloodTestReport per uno specifico paziente
     * richiesto tramite identificativo dal medico.
     * @param model modello da utilizzare per riportare le informazioni su Thymeleaf
     * @param patientId identificativo del paziente di cui si vuole recuperare i documenti presenti nella
     *                  sua cartella.
     * @param user utente loggato - deve essere un medico
     * @return pagina html dove vengono mostrati tutti i documenti recuperati.
     */
    @GetMapping("doctor/reports/reportsPerPatient/{id}")
    public String getPatientFiles(Model model, @PathVariable("id") int patientId, Principal user) {
        // L'oggetto che ritorna dal metodo getAllFiles è uno Stream di oggetti di classe PDFReport.
        // Recupero lo Stream di oggetti di tipo PDFReport e, per permettere il download successivo
        // dei file, costruisco l'URL a cui scaricare il documento e lo inserisco come attributo
        // String dell'oggeto di classe FileInfo assieme alla descrizione del documento
        // stesso.
        List<FileInfo> fileInfos = pdfReportService.getAllFiles(patientId).map(pdfFile -> {
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

        model.addAttribute("files", fileInfos);

        // Recupera i documenti relativi agli esami del sangue.
        List<BloodTestReport> bloodTestReports = bloodTestReportService.getAllBloodTestReports(patientId);
        model.addAttribute("bloodTestReports", bloodTestReports);
        model.addAttribute("patientId", patientId);

        return "/doctor/reports/patientReportsBoard.html";
    }
}
