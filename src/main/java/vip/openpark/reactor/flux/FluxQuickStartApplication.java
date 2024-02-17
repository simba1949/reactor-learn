package vip.openpark.reactor.flux;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Flux 快速入门
 *
 * @author anthony
 * @version 2024/2/17 12:30
 */
public class FluxQuickStartApplication {
	public static void main(String[] args) throws InterruptedException {
		// 创建一个 Flux 对象
		Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		// 订阅
		flux.subscribe(System.out::println);
		
		System.out.println("-----------------------");
		
		// Flux 从 0 开始，每 1 秒发射一个数字
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
		interval.subscribe(System.out::println);
		Thread.sleep(10000);
	}
}