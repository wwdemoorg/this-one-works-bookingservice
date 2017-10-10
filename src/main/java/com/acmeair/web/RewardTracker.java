package com.acmeair.web;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.acmeair.faultTolerance.ConnectException;
import com.acmeair.faultTolerance.CustomerClientFTConnectionBean;
import com.acmeair.faultTolerance.FlightClientFTConnectionBean;



@ApplicationScoped
public class RewardTracker {


  @Inject
  private CustomerClientFTConnectionBean customerClientFtConectionBean;
  
  @Inject
  private FlightClientFTConnectionBean flightClientFtConnectioBean;
  
  /* microprofile-1.1 */
  @Inject 
  @ConfigProperty(name = "TRACK_REWARD_MILES", defaultValue = "true") 
  private Boolean trackRewardMiles;

  @PostConstruct
  private void initialize() {
    System.out.println("TRACK_REWARD_MILES: " + trackRewardMiles);
  }
    
  public boolean trackRewardMiles() {
    return trackRewardMiles;
  }
  
  /**
   * Update rewards.
   */
  //Make this asynchrnous so the client gets the booking confirmed message faster.
  //This can be done in the background
  @Asynchronous
  public Future<String> updateRewardMiles(String userid, String flightSegId, boolean add) {
    
    if (trackRewardMiles) {
      
      String miles = null;     
      try {
        miles = flightClientFtConnectioBean.connect(userid, flightSegId, add);
      } catch (Exception e) {
        e.printStackTrace();
        return CompletableFuture.completedFuture(null);
      }    
      
      String totalMiles = null;
      if (miles != null && !miles.equals("Failed")) {
        try {
          totalMiles = customerClientFtConectionBean.connect(userid, miles);
        } catch (Exception e) {
          e.printStackTrace();
          return CompletableFuture.completedFuture(null);
        }
      } else {
        System.out.println("FlightSevice Call Failed: Updating Reward Miles Failed for " + userid + ", flightSegment " + flightSegId);
        return CompletableFuture.completedFuture(null);
      }  
      if (totalMiles != null && !totalMiles.equals("Failed")) {
        return CompletableFuture.completedFuture(totalMiles);
      } else {
        System.out.println("CustomerSevice Call Failed: Updating Reward Miles Failed for " + userid + ", flightSegment " + flightSegId);
        return CompletableFuture.completedFuture(null);
      }  
    }
    
    return CompletableFuture.completedFuture(null);
  }
}
