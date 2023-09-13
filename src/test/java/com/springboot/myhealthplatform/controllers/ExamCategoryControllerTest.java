package com.springboot.myhealthplatform.controllers;

import com.springboot.myhealthplatform.board.bean.ExamCategory;
import com.springboot.myhealthplatform.board.controller.ExamCategoryController;
import com.springboot.myhealthplatform.board.service.ExamCategoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExamCategoryControllerTest.class)
@AutoConfigureMockMvc
@WithMockUser(username="admin",roles="ADMIN")
public class ExamCategoryControllerTest {

    @MockBean
    private ExamCategoryService examCategoryService;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        // Senza il costruttore fatto in questo modo, examCategoryService è null
        ExamCategoryController controller = new ExamCategoryController(examCategoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void it_should_save_exam_category() throws Exception{
        ExamCategory examCategory = new ExamCategory();
        examCategory.setCategoryTitle("test");

        when(examCategoryService.save(examCategory)).thenReturn(examCategory);

        this.mockMvc
                .perform(post("/admin/examCategories/addCategory")
                        .param("categoryTitle", examCategory.getCategoryTitle()))
                .andExpect(status().isOk())
                .andDo(print());


    }

}
