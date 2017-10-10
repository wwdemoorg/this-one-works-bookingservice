package com.acmeair.faultTolerance;

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

@Dependent
public class StringFallbackHandler implements FallbackHandler<String> {
	  protected Logger logger =  Logger.getLogger(StringFallbackHandler.class.getName());


	@Override
    public String handle(ExecutionContext context) {
		logger.info("fallback for " + context.getMethod().getName());		
        return "Failed";
    }

}
