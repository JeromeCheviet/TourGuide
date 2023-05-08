package tourGuide.configuration;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;
import tourGuide.service.GpsUtilServiceImpl;
import tourGuide.service.RewardsServiceImpl;

/**
 * Class to configure object to a bean.
 */
@Configuration
public class TourGuideModule {

    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }

    @Bean
    public GpsUtilServiceImpl getGpsUtilService() {
        return new GpsUtilServiceImpl(getGpsUtil());
    }

    @Bean
    public RewardsServiceImpl getRewardsService() {
        return new RewardsServiceImpl(getGpsUtilService(), getRewardCentral());
    }

    @Bean
    public RewardCentral getRewardCentral() {
        return new RewardCentral();
    }

}
