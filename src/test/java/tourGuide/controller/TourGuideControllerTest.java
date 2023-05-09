package tourGuide.controller;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tourGuide.helper.InternalTestHelper;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TourGuideControllerTest {
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
    public void testGetIndex() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contentEquals("Greetings from TourGuide!"));
    }

    @Test
    public void testGetLocation() throws Exception {
        InternalTestHelper internalTestHelper = new InternalTestHelper();
        internalTestHelper.setInternalUserNumber(1);

        MvcResult mvcResult = mvc.perform(get("/getLocation")
                .param("userName", "internalUser0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getRequest().getParameter("userName").contentEquals("internalUser0"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("longitude"));
    }

    @Test
    public void testGetNearByAttractions() throws Exception {
        InternalTestHelper internalTestHelper = new InternalTestHelper();
        internalTestHelper.setInternalUserNumber(1);

        MvcResult mvcResult = mvc.perform(get("/getNearbyAttractions")
                .param("userName", "internalUser0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getRequest().getParameter("userName").contentEquals("internalUser0"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("attractionName"));
    }

    @Test
    public void testGetRewards() throws Exception {
        InternalTestHelper internalTestHelper = new InternalTestHelper();
        internalTestHelper.setInternalUserNumber(1);

        MvcResult mvcResult = mvc.perform(get("/getRewards")
                .param("userName", "internalUser0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getRequest().getParameter("userName").contentEquals("internalUser0"));
    }

    @Test
    public void testGetAllCurrentLocations() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/getAllCurrentLocations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("location"));
    }

    @Test
    public void testGetTripDeals() throws Exception {
        InternalTestHelper internalTestHelper = new InternalTestHelper();
        internalTestHelper.setInternalUserNumber(1);

        MvcResult mvcResult = mvc.perform(get("/getTripDeals")
                .param("userName", "internalUser0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(mvcResult.getRequest().getParameter("userName").contentEquals("internalUser0"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("price"));
    }
}
