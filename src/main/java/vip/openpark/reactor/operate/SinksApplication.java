package vip.openpark.reactor.operate;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * @author anthony
 * @version 2024/2/18 10:39
 */
@Slf4j
public class SinksApplication {
	public static void main(String[] args) throws IOException, InterruptedException {
		// unicast();
		// multicast();
		// replay();
		
		// cache();
		
		// block();
		
		// parallelFlux();
		
		context();
		
		System.in.read();
	}
	
	public static void concept() {
		Sinks.one(); // 单值发送器
		Sinks.many(); // 多值发送器
		
		Sinks.many().replay(); // 重放
	}
	
	/**
	 * 1. 一个发送器只能有一个订阅者
	 */
	public static void unicast() throws InterruptedException {
		Sinks.Many<Object> many = Sinks.many().unicast().onBackpressureBuffer();
		
		new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				many.tryEmitNext(i);
			}
		}).start();
		
		// 第一个订阅
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v1 = {}", v))).start();
		// 防止先订阅
		Thread.sleep(1000);
		// 第二个订阅
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v2 = {}", v))).start();
	}
	
	/**
	 * 1. 多个订阅者
	 */
	public static void multicast() {
		Sinks.Many<Object> many = Sinks.many().multicast().onBackpressureBuffer();
		
		new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				many.tryEmitNext(i);
				try {
					// 这里防止先订阅者订阅号消费过快，导致后来的订阅者还没有来得及订阅就被丢弃
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
		// 第一个订阅
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v1 = {}", v))).start();
		// 第二个订阅
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v2 = {}", v))).start();
	}
	
	/**
	 * 1. 重放
	 */
	public static void replay() {
		Sinks.Many<Object> many = Sinks.many().replay().limit(3);
		
		// 发布者每秒发布一个值
		new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				many.tryEmitNext(i);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
		// 第一个订阅者
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v1 = {}", v))).start();
		
		// 第二个订阅五秒后订阅
		new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			many.asFlux().subscribe(v -> log.info("v2 = {}", v));
		}).start();
	}
	
	public static void cache() {
		Flux<Integer> flux =
			Flux.range(1, 10)
				.delayElements(Duration.ofSeconds(1)) //不调缓存默认就是缓存所有
				// 缓存元素个数，默认全部缓存
				.cache(2);
		// 第一个订阅者
		flux.subscribe(v -> log.info("v1 = {}", v));
		// 第二个订阅者
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			flux.subscribe(v -> log.info("v2 = {}", v));
		}).start();
	}
	
	public static void block() {
		List<String> block =
			Flux.range(1, 10)
				.filter(v -> v % 2 == 0)
				.map(v -> "MAP-" + v)
				.collectList()
				// 阻塞，阻塞也是一种订阅：BlockingMonoSubscriber
				.block();
		log.info("block = {}", block);
	}
	
	public static void parallelFlux() {
		Flux.range(1, 1000)
			.buffer(100)
			.log()
			// 8个线程并发
			.parallel(8)
			.runOn(Schedulers.newParallel("线程runOn"))
			.log()
			.flatMap(Flux::fromIterable)
			.sorted(Integer::compare)
			.subscribe(v -> log.info("v = {}", v));
	}
	
	public static void context() {
		Flux.just(1, 2, 3)
			// 必须使用支持 Context API 才能获取上下文
			.transformDeferredContextual((flux, context) -> {
				log.info("flux class is {}, flux = {}", flux.getClass(), flux);
				log.info("context class is {}, context = {}", context.getClass(), context);
				String prefixVal = context.get("PREFIX");
				// 在原有的 flux 基础上给每个元素添加前缀
				return flux.map(v -> prefixVal + v);
			})
			// 这可以传多值可以使用 map
			// ThreadLocal 共享了数据，上游的所有人能看到；Context 由下游传播给上游
			.contextWrite(Context.of("PREFIX", "前缀"))
			.subscribe(v -> log.info("v = {}", v));
	}
	
}