package vip.openpark.reactor.operate;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;

/**
 * @author anthony
 * @version 2024/2/18 10:24
 */
@Slf4j
public class TimeoutAndRetryApplication {
	public static void main(String[] args) throws InterruptedException {
		// timeout();
		retry();
		
		Thread.sleep(10000);
	}
	
	public static void timeout() {
		Flux.just(1, 2, 3)
			.delayElements(Duration.ofSeconds(3))
			.log()
			.timeout(Duration.ofSeconds(2))
			.subscribe();
	}
	
	public static void retry() {
		Flux.just(1, 2, 3)
			.delayElements(Duration.ofSeconds(3))
			.log()
			.timeout(Duration.ofSeconds(2))
			.retry(3) // 重试3次，把流从头到尾重新请求
			.subscribe();
	}
}