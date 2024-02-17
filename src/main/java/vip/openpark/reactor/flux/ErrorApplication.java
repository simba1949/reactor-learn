package vip.openpark.reactor.flux;

/**
 * @author anthony
 * @since 2024/2/17 22:36
 */
public class ErrorApplication {
	public static void main(String[] args) {
		staticDefaultValue();
		fallbackMethod();
		dynamicallyComputeAFallbackValue();
		wrapToABusinessException();
		logAnErrorSpecificMessage();
		tryWithResource();
	}
	
	// Catch and return a static default value.
	public static void staticDefaultValue() {
	}
	
	// Catch and execute an alternative path with a fallback method.
	public static void fallbackMethod() {
	}
	
	// Catch and dynamically compute a fallback value.
	public static void dynamicallyComputeAFallbackValue() {
	}
	
	// Catch, wrap to a BusinessException, and re-throw.
	public static void wrapToABusinessException() {
	}
	
	// Catch, log an error-specific message, and re-throw.
	public static void logAnErrorSpecificMessage() {
	}
	
	// Use the finally block to clean up resources or a Java 7 “try-with-resource” construct.
	public static void tryWithResource() {
	}
}