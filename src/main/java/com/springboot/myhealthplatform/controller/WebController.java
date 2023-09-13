package com.springboot.myhealthplatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller riferito alla realizzazione delle pagine del frontEnd
 */
@Controller
public class WebController {

    // Pagine generiche - accessibili a tutti //
    @RequestMapping("/login")
    public String login() {
        return "login.html";
    }
    @RequestMapping("/myErrorPage")
    public String myErrorPage() { return "myErrorPage.html";}
    @GetMapping("/index")
    public String goToIndex() {
        return "index.html";
    }
    @RequestMapping("/logout")
    public String logout() {
        return "perform_logout.html";
    }
    // Pagine dedicate all'utente ADMIN //
    @GetMapping("/admin/examCategories/examCategoryForm.html")
    public String examCategoryForm() {
        return "/admin/examCategories/examCategoryForm.html";
    }
    @GetMapping("/admin/adminPage.html")
    public String goToAdminPage() {
        return "/admin/adminPage.html";
    }
    @GetMapping("/admin/registration.html")
    public String registrationPage() {
        return "/admin/registration.html";
    }
    @GetMapping("/admin/doctorRegistrationPage.html")
    public String doctorRegistrationPage() {
        return "/admin/doctorRegistrationPage.html";
    }
    @GetMapping("/admin/patientRegistrationPage.html")
    public String patientRegistrationPage() {
        return "/admin/patientRegistrationPage.html";
    }
    @GetMapping("/admin/successRegistration.html")
    public String goToSuccessRegistrationPage() {
        return "/admin/successRegistration.html";
    }

    // Pagine dedicate al MEDICO //

    @GetMapping("/doctor/message/doctorMessageBoard.html")
    public String doctorMessageBoard(){return "/doctor/message/doctorMessageBoard.html";}
    @GetMapping("/doctor/doctorInfo.html")
    public String doctorInfo(){return "/doctor/doctorInfo.html";}
    @GetMapping("/doctor/message/messageInfo.html")
    public String doctorMessageInfo(){return "/doctor/message/messageInfo.html";}
    @GetMapping("/doctor/message/NotReadMessagesPerPatient.html")
    public String showNotReadMessagesPerPatient(){return "/doctor/message/NotReadMessagesPerPatient.html";}
    @GetMapping("/doctor/patientDiary/patientDiaryEntriesBoard.html")
    public String showPatientDiaryEntriesToDoctor(){return "/doctor/patientDiary/patientDiaryEntriesBoard.html";}
    @GetMapping("/doctor/reports/patientReportsBoard.html")
    public String showPatientPDFFiles() {return "/doctor/reports/patientReportsBoard.html";}
    @GetMapping("/doctor/activityIndex/activityIndexForm.html")
    public String activityIndexCalculatorForm(){return "/doctor/activityIndex/activityIndexForm.html";}
    @GetMapping("/doctor/activityIndex/AIForm.html")
    public String AICalculatorForm(){return "/doctor/activityIndex/AIForm.html";}
    @GetMapping("/doctor/activityIndex/AIFormDisplayErrors.html")
    public String AICalculatorFormDisplayErrors(){return "/doctor/activityIndex/AIFormDisplayErrors.html";}
    @GetMapping("/doctor/activityIndex/indexValueResult.html")
    public String showActivityIndexResult(){return "/doctor/activityIndex/indexValueResult.html";}
    @GetMapping("/doctor/doctorHomePage.html")
    public String goToDoctorHomePage() {
        return "/doctor/doctorHomePage.html";
    }
    @GetMapping("/doctor/yourPatients.html")
    public String goToYourPatientsPage() {
        return "/doctor/yourPatients.html";
    }

    // Pagine dedicate agli INFERMIERI //

    @GetMapping("/nurse/message/messageInfo.html")
    public String nurseMessageInfo(){return "/nurse/message/messageInfo.html";}
    @GetMapping("/nurse/message/notReadPatientMessages.html")
    public String nurseMessageBoard(){return "/nurse/message/notReadPatientMessages.html";}
    @GetMapping("/nurse/patientDiary/patientDiaryEntriesList.html")
    public String showPatientDiaryEntries(){return "/nurse/patientDiary/patientDiaryEntriesList.html";}

    // Directory MESSAGE //

    @GetMapping("/message/NewMessageForm.html")
    public String newMessageForm(){return "/message/NewMessageForm.html";}
    @GetMapping("/message/patientMessageBoard.html")
    public String patientMessageBoard(){return "/message/patientMessageBoard.html";}

    // Directory AGENDA //

    @GetMapping("/agenda/appointmentList.html")
    public String appointmentList() {
        return "/agenda/appointmentList.html";
    }
    @GetMapping("doctor/doctorAgenda/doctorAppointmentForm.html")
    public String doctorAppointmentForm() {
        return "doctor/agenda/doctorAppointmentForm.html";
    }
    @GetMapping("doctor/doctorAgenda/doctorAppointmentList.html")
    public String doctorAppointmentList() {
        return "doctor/agenda/doctorAppointmentList.html";
    }

    // Directory REPORTS //

    @GetMapping("/reports/uploadForm.html")
    public String uploadForm() {
        return "/reports/uploadForm.html";
    }
    @GetMapping("/reports/files.html")
    public String showAllFiles() {
        return "/reports/files.html";
    }
    @GetMapping("/reports/bloodExamForm.html")
    public String bloodExamForm() {
        return "/reports/bloodExamForm.html";
    }

    // Directory DIARY //
    @GetMapping("/diary/diaryEntryInfoMessage.html")
    public String draftDiaryEntryMessagePage(){ return "/diary/diaryEntryInfoMessage.html";}
    @GetMapping("/diary/diaryEntryForm.html")
    public String diaryEntryForm() {
        return "/diary/diaryEntryForm.html";
    }
    @GetMapping("/diary/diaryEntries.html")
    public String diaryEntries() {
        return "/diary/diaryEntries.html";
    }
    @GetMapping("/diary/publishedInfoMessage.html")
    public String pushingMessage() {
        return "/diary/publishedInfoMessage.html";
    }

    // Directory PATIENT //

    @GetMapping("/patient/showADiaryEntry.html")
    public String showADiaryEntry() { return "/patient/showADiaryEntry.html";}


}
