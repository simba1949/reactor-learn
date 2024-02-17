package vip.openpark.reactor.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * 调度器工具类：{@link Schedulers}
 * {@link Schedulers#immediate()} 无执行上下文，当前线程执行所有操作
 * {@link Schedulers#single()} 使用固定的单线程调度器
 * {@link Schedulers#boundedElastic()} 有界、可伸缩的调度器；线程池中有 10*CPU 核心个线程，队列默认是 10万，keepalive 为 1min
 * {@link Schedulers#newBoundedElastic(int, int, String)} 自定义有界、可伸缩的调度器；
 * {@link Schedulers#parallel()} 并行调度器
 *
 * @author anthony
 * @since 2024/2/17 20:55
 */
@Slf4j
public class SchedulerApplication {
	public static void main(String[] args) throws InterruptedException {
		Flux.range(1, 5)
			// 改变发布者所在线程池：在哪个线程池把这个流的数据和操作执行了
			// 只要不指定线程池，默认发布者用的线程池就是订阅者的线程池
			.publishOn(Schedulers.single())
			.log()
			// 改变订阅者所在线程池：在哪个线程池订阅这个流的数据和操作执行了
			.subscribeOn(Schedulers.boundedElastic())
			.subscribe(ele -> log.info("{}", ele));
		
		Thread.sleep(2000L);
	}
}