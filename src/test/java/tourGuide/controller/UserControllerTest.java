package tourGuide.controller;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tourGuide.helper.InternalTestHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    private void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    public void testUpdateUserPreferences() throws Exception {
        InternalTestHelper internalTestHelper = new InternalTestHelper();
        internalTestHelper.setInternalUserNumber(1);

        String userName = "internalUser0";

        String jsonBody = "{\n" +
                "\"attractionProximity\": 120,\n" +
                "\"currency\": \"EUR\",\n" +
                "\"lowerPricePoint\": 0.0,\n" +
                "\"highPricePoint\": 500.0,\n" +
                "\"tripDuration\": 2,\n" +
                "\"ticketQuantity\": 3,\n" +
                "\"numberOfAdults\": 2,\n" +
                "\"numberOfChildren\": 1\n" +
                "}";

        mvc.perform(put("/users/updatePreferences")
                        .param("userName", userName)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUserPreferences_withEmptyBody() throws Exception {
        InternalTestHelper internalTestHelper = new InternalTestHelper();
        internalTestHelper.setInternalUserNumber(1);

        String userName = "internalUser0";

        String jsonBody = "";

        mvc.perform(put("/users/updatePreferences")
                        .param("userName", userName)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserPreferences_withUnknownUser() throws Exception {
        InternalTestHelper internalTestHelper = new InternalTestHelper();
        internalTestHelper.setInternalUserNumber(1);

        String userName = "john";

        String jsonBody = "{\n" +
                "\"attractionProximity\": 120,\n" +
                "\"currency\": \"EUR\",\n" +
                "\"lowerPricePoint\": 0.0,\n" +
                "\"highPricePoint\": 500.0,\n" +
                "\"tripDuration\": 2,\n" +
                "\"ticketQuantity\": 3,\n" +
                "\"numberOfAdults\": 2,\n" +
                "\"numberOfChildren\": 1\n" +
                "}";

        mvc.perform(put("/users/updatePreferences")
                        .param("userName", userName)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
