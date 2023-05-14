package tourGuide.controller;

import gpsUtil.GpsUtil;
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
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.user.User;
import tourGuide.service.GpsUtilServiceImpl;
import tourGuide.service.RewardsServiceImpl;
import tourGuide.service.TourGuideServiceImpl;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    public void updateUserPreferences() throws Exception {
        GpsUtilServiceImpl gpsUtilServiceImpl = new GpsUtilServiceImpl(new GpsUtil());
        RewardsServiceImpl rewardsServiceImpl = new RewardsServiceImpl(gpsUtilServiceImpl, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(1);
        TourGuideServiceImpl tourGuideServiceImpl = new TourGuideServiceImpl(gpsUtilServiceImpl, rewardsServiceImpl);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        String userName = "jon";

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
                .content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
