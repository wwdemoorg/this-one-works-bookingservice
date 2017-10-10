/**
 * 
 */
package com.acmeair.faultTolerance;

import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

import com.acmeair.client.CustomerClient;

/**
 * @author jagraj
 *
 */

@ApplicationScoped
public class CustomerClientFTConnectionBean {

	  protected Logger logger =  Logger.getLogger(CustomerClientFTConnectionBean.class.getName());
	
	  @Inject
	  private CustomerClient customerClient;
	  
	  // TODO: Do we really need all of these?
	  @Bulkhead(value = 50, waitingTaskQueue = 300)
	  @Retry(maxRetries=6,delayUnit=ChronoUnit.SECONDS,delay=10,durationUnit=ChronoUnit.MINUTES,maxDuration=5)
	  @Fallback(StringFallbackHandler.class)
	  @CircuitBreaker(delay=10,delayUnit = ChronoUnit.SECONDS, requestVolumeThreshold = 3, failureRatio = 1.0)
	  @Timeout(value = 30, unit = ChronoUnit.SECONDS)
	  public String connect(String userId, String miles) throws ConnectException, TimeoutException,CircuitBreakerOpenException,InterruptedException{
		  int executionCounter = 1;
		      logger.info("CustomerClientFTConnectionBean.connect()  called: ");		  
		    
		  try {
		    logger.info(
		        "Delay Duration: " + "10 seconds" + " CustomerClientFTConnectionBean.connect() Service called, execution " + executionCounter);
			  
		    executionCounter++;
		    
		    return customerClient.updateTotalMiles(userId, miles);
			   
		  } catch(Exception e){
			  e.printStackTrace();
			  return null;
			 
		  }
		  finally {
		    logger.info("CustomerClientFTConnectionBean.connect() complete ");
		    executionCounter=0;
		  }
	  }
}
