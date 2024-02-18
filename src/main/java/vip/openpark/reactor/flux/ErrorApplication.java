package vip.openpark.reactor.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.time.Duration;

/**
 * @author anthony
 * @since 2024/2/17 22:36
 */
@Slf4j
public class ErrorApplication {
	public static void main(String[] args) throws InterruptedException {
		// staticDefaultValue1();
		// staticDefaultValue2();
		// staticDefaultValue3();
		// fallbackMethod();
		// dynamicallyComputeAFallbackValue1();
		// dynamicallyComputeAFallbackValue2();
		// wrapToABusinessException1();
		// wrapToABusinessException2();
		// logAnErrorSpecificMessage();
		// tryWithResource();
		// onErrorContinue();
		// onErrorStop();
		onErrorComplete();
		
		Thread.sleep(10000L);
	}
	
	// Catch and return a static default value.
	public static void staticDefaultValue1() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new RuntimeException("boom");
				}
				return i;
			})
			// 【错误算中断】如果抛出异常，返回默认值，上游发生异常后的后续元素将被忽略（不是被丢弃）
			.onErrorReturn(0)
			.log() // 日志打印：onNext(1)、onNext(0)
			.subscribe();
	}
	
	public static void staticDefaultValue2() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("boom");
				}
				return i;
			})
			// 类似于 catch (Exception e) { return 0; }
			.onErrorReturn(ArithmeticException.class, 0)
			.onErrorReturn(NullPointerException.class, 1)
			.log() // 日志打印：onNext(1)、onNext(0)
			.subscribe();
	}
	
	public static void staticDefaultValue3() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("boom");
				}
				return i;
			})
			.onErrorReturn(error -> error instanceof ArithmeticException, 0)
			.log() // 日志打印：onNext(1)、onNext(0)
			.subscribe();
	}
	
	// Catch and execute an alternative path with a fallback method.
	public static void fallbackMethod() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new RuntimeException("boom");
				}
				return i;
			})
			// 【错误算中断】如果抛出异常，执行 fallbackMethod，返回 fallbackMethod 的返回值，上游发生异常后的后续元素将被忽略（不是被丢弃）
			.onErrorResume(error -> {
				log.error("fallbackMethod error", error);
				return Flux.just(0);
			})
			.log() // 日志打印：onNext(1)、onNext(0)
			.subscribe();
	}
	
	// Catch and dynamically compute a fallback value.
	public static void dynamicallyComputeAFallbackValue1() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("boom");
				}
				return i;
			})
			// 类似于 catch (ArithmeticException e) { return 0; }
			.onErrorResume(
				ArithmeticException.class,
				error -> {
					log.error("fallbackMethod error", error);
					return Flux.just(0);
				})
			.log() // 日志打印：onNext(1)、onNext(0)
			.subscribe();
	}
	
	public static void dynamicallyComputeAFallbackValue2() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("boom");
				}
				return i;
			})
			.onErrorResume(
				error -> error instanceof ArithmeticException,
				error -> {
					log.error("fallbackMethod error", error);
					return Flux.just(0);
				})
			.log() // 日志打印：onNext(1)、onNext(0)
			.subscribe();
	}
	
	// Catch, wrap to a BusinessException, and re-throw.
	public static void wrapToABusinessException1() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("boom");
				}
				return i;
			})
			.onErrorResume(error -> Flux.error(new BusinessException("自定义异常信息")))
			.log() // 日志打印：onNext(1)、onError(java.lang.RuntimeException: boom)
			.subscribe(
				val -> log.info("val = {}", val),
				error -> log.error("error = {}", error.getMessage(), error)
			);
	}
	
	public static void wrapToABusinessException2() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("boom");
				}
				return i;
			})
			.onErrorMap(error -> new BusinessException("自定义异常信息"))
			.log() // 日志打印：onNext(1)、onError(java.lang.RuntimeException: boom)
			.subscribe(
				val -> log.info("val = {}", val),
				error -> log.error("error = {}", error.getMessage(), error)
			);
	}
	
	static class BusinessException extends RuntimeException {
		public BusinessException(String message) {
			super(message);
		}
	}
	
	// Catch, log an error-specific message, and re-throw.
	public static void logAnErrorSpecificMessage() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("自定义异常信息");
				}
				return i;
			})
			// 【类似于异常通知】当发生异常时，回调一个函数，可以做打印日志或者其他操作
			.doOnError(error -> log.error("error = {}", error.getMessage(), error))
			.subscribe();
	}
	
	// Use the finally block to clean up resources or a Java 7 “try-with-resource” construct.
	public static void tryWithResource() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("自定义异常信息");
				}
				return i;
			})
			//
			.doFinally(signalType -> {
				// 可以做一些资源释放、关闭流等操作
				if (signalType == SignalType.ON_ERROR) {
					log.info("doFinally signalType is {}", signalType);
				}
			})
			.subscribe();
	}
	
	public static void onErrorContinue() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("自定义异常信息");
				}
				return i;
			})
			// 【错误后继续】当发生异常时，回调一个函数，可以做打印日志或者其他操作，订阅者无法感知异常
			.onErrorContinue((error, value) -> {
				log.error("发布者 error = {}", error.getMessage(), error);
				log.info("发布者 value = {}", value);
			})
			.log() // onNext(1),onNext(3)
			.subscribe(
				val -> log.info("订阅者 val = {}", val),
				error -> log.error("订阅者 error = {}", error.getMessage(), error)
			);
	}
	
	public static void onErrorStop() {
		Flux.interval(Duration.ofSeconds(1))
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("自定义异常信息");
				}
				return i;
			})
			// 【错误后停止】当发生异常后，停止流的产生，订阅者可以感知异常
			.onErrorStop()
			.log()
			.subscribe(
				val -> log.info("订阅者 val = {}", val),
				error -> log.error("订阅者 error = {}", error.getMessage(), error)
			);
	}
	
	public static void onErrorComplete() {
		Flux.just(1, 2, 3)
			.map(i -> {
				if (i == 2) {
					throw new ArithmeticException("自定义异常信息");
				}
				return i;
			})
			// 将错误结束信号，转换为完成信号，订阅者无法感知异常
			.onErrorComplete()
			.log()
			.subscribe(
				val -> log.info("订阅者 val = {}", val),
				error -> log.error("订阅者 error = {}", error.getMessage(), error)
			);
	}
}