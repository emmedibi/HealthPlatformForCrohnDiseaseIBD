package com.springboot.myhealthplatform;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Crea una mappatura diretta tra il nome della view e il suo URL, tramite ViewControllerRegistry (qui,
 * riportato come registry).
 */
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/myErrorPage").setViewName("myErrorPage");
        registry.addViewController("/admin/adminPage").setViewName("adminPage");
        registry.addViewController("/registration").setViewName("registration");
        registry.addViewController("/doctorRegistrationPage").setViewName("doctorRegistrationPage");
        registry.addViewController("/admin/successRegistration").setViewName("successRegistration");
        registry.addViewController("/doctor/doctorHomePage").setViewName("doctorHomePage");
        registry.addViewController("/doctor/doctorAgenda").setViewName("doctorAgenda");
        registry.addViewController("/doctor/yourPatients").setViewName("yourPatients");
        registry.addViewController("/doctor/yourPatientsMessages").setViewName("yourPatientsMessages");
        registry.addViewController("/perform_logout").setViewName("perform_logout");
        registry.addViewController("/reports/uploadForm").setViewName("uploadForm");
        registry.addViewController("/reports/files").setViewName("files");
        registry.addViewController("/diary/diaryEntries").setViewName("diaryEntries");
        registry.addViewController("/admin/examCategories/examCategoryForm").setViewName("examCategoryForm");
        registry.addViewController("agenda/appointmentList").setViewName("appointmentList");
        registry.addViewController("/message/NewMessageForm").setViewName("NewMessageForm");
        registry.addViewController("doctor/message/doctorMessageBoard.html").setViewName("doctorMessageBoard");
        registry.addViewController("doctor/message/messageInfo.html").setViewName("messageInfo");
        registry.addViewController("nurse/message/messageInfo.html").setViewName("messageInfo");
        registry.addViewController("nurse/message/notReadPatientMessages.html").setViewName("notReadPatientMessages");
        registry.addViewController("nurse/patientDiary/patientDiaryEntriesList.html").setViewName("patientDiaryEntriesList");
        registry.addViewController("doctor/message/NotReadMessagesPerPatient.html").setViewName("NotReadMessagesPerPatient");
        registry.addViewController("doctor/patientDiary/patientDiaryEntriesBoard.html").setViewName("patientDiaryEntriesBoard");
        registry.addViewController("doctor/reports/patientReportsBoard.html").setViewName("patientReportsBoard");
        registry.addViewController("doctor/activityIndex/activityIndexForm.html").setViewName("activityIndexForm");
        registry.addViewController("doctor/activityIndex/AIForm.html").setViewName("AIFormDisplayErrors");
        registry.addViewController("doctor/activityIndex/AIFormDisplayErrors.html").setViewName("AIForm");
        registry.addViewController("doctor/activityIndex/indexValueResult.html").setViewName("indexValueResult");
        registry.addViewController("/diary/diaryEntryInfoMessage").setViewName("diaryEntryInfoMessage");
        registry.addViewController("/patient/showADiaryEntry.html").setViewName("showADiaryEntry");
        registry.addViewController("doctor/doctorInfo.html").setViewName("doctorInfo");
    }

}
