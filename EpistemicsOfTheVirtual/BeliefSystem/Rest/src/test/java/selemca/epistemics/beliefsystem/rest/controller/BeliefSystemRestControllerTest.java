package selemca.epistemics.beliefsystem.rest.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import selemca.epistemics.beliefsystem.rest.config.TestRestConfig;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;

import java.lang.invoke.MethodHandles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static selemca.epistemics.beliefsystem.rest.controller.BeliefSystemRestController.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestRestConfig.class)
public class BeliefSystemRestControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ConceptRepository conceptRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        Concept concept = new Concept("beep", 0.01);
        conceptRepository.save(concept);
    }

    @Test
    public void test() throws Exception {
        mockMvc.perform(get(URL_PREFIX + SERVLET_CONCEPT).param(PARAM_CONCEPT_ID, "beep"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(result -> LOG.debug("MVC result: {}", result))
            .andExpect(content().json("[{\"name\":\"beep\",\"truthValue\":0.01}]"))
        ;
    }
}
