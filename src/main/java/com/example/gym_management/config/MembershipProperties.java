package com.example.gym_management.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gym.membership")
@Getter
@Setter
public class MembershipProperties {

  private int gracePeriodDays = 7;

  private int cancellationDeadlineHours = 1;
}
