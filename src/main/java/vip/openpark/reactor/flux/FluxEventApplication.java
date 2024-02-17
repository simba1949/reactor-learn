package vip.openpark.reactor.flux;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.time.Duration;

/**
 * @author anthony
 * @version 2024/2/17 13:36
 */
@Slf4j
public class FluxEventApplication {
	public static void main(String[] args) throws InterruptedException {
		// 正常操作
		// normal();
		// 操作流出现了异常
		error();
		
		Thread.sleep(20000);
	}
	
	/**
	 * Flux 正常操作
	 */
	public static void normal() {
		Flux<Integer> flux =
			Flux.range(0, 20)
				// 过滤偶数
				.filter(i -> i % 2 == 0)
				// 每个元素延迟一秒
				.delayElements(Duration.ofSeconds(1))
				// 当Flux开始时触发
				.doOnSubscribe(s -> log.info("doOnSubscribe"))
				// 流请求获取数据时触发
				.doOnRequest(n -> log.info("doOnRequest {}", n))
				// 当流产生数据时触发
				.doOnNext(s -> log.info("doOnNext {}", s))
				// 当Flux成功完成时触发
				.doOnComplete(() -> log.info("doOnComplete"))
				// 当流产生错误时触发
				.doOnError(e -> log.error("doOnError", e))
				// 当流撤销时触发
				.doOnCancel(() -> log.info("doOnCancel"));
		
		// 订阅，流只有订阅时，才会触发完成事件
		flux.subscribe(new BaseSubscriber<Integer>() {
			@Override
			protected void hookOnSubscribe(Subscription subscription) {
				log.info("hookOnSubscribe");
				// 请求数据
				subscription.request(1);
			}
			
			@Override
			protected void hookOnNext(Integer value) {
				log.info("hookOnNext {}", value);
				if (value >= 10) {
					// 取消订阅
					cancel();
				} else {
					// 请求数据
					request(1);
				}
			}
			
			@Override
			protected void hookOnComplete() {
				log.info("hookOnComplete");
			}
			
			@Override
			protected void hookOnError(Throwable throwable) {
				log.error("hookOnError", throwable);
			}
			
			@Override
			protected void hookOnCancel() {
				log.info("hookOnCancel");
			}
			
			@Override
			protected void hookFinally(SignalType type) {
				log.info("hookFinally {}", type);
			}
		});
	}
	
	/**
	 * Flux 异常操作
	 */
	public static void error() {
		Flux<Integer> flux =
			Flux.range(0, 20)
				// 过滤偶数
				.filter(i -> i % 2 == 0)
				// 每个元素延迟一秒
				.delayElements(Duration.ofSeconds(1))
				// 当Flux开始时触发
				.doOnSubscribe(s -> log.info("doOnSubscribe"))
				// 流请求获取数据时触发
				.doOnRequest(n -> log.info("doOnRequest {}", n))
				// 当流产生数据时触发
				.doOnNext(s -> log.info("doOnNext {}", s))
				// 当Flux成功完成时触发
				.doOnComplete(() -> log.info("doOnComplete"))
				// 当流产生错误时触发
				.doOnError(e -> log.error("doOnError", e))
				// 当流撤销时触发
				.doOnCancel(() -> log.info("doOnCancel"));
		
		// 订阅，流只有订阅时，才会触发完成事件
		flux.subscribe(new BaseSubscriber<Integer>() {
			@Override
			protected void hookOnSubscribe(Subscription subscription) {
				log.info("hookOnSubscribe");
				// 请求数据
				subscription.request(1);
			}
			
			@Override
			protected void hookOnNext(Integer value) {
				log.info("hookOnNext {}", value);
				if (value >= 8) {
					throw new RuntimeException("test");
				} else {
					// 请求数据
					request(1);
				}
			}
			
			@Override
			protected void hookOnComplete() {
				log.info("hookOnComplete");
			}
			
			@Override
			protected void hookOnError(Throwable throwable) {
				log.error("hookOnError", throwable);
			}
			
			@Override
			protected void hookOnCancel() {
				log.info("hookOnCancel");
			}
			
			@Override
			protected void hookFinally(SignalType type) {
				// 流出现异常后，会触发该方法
				log.info("hookFinally {}", type);
			}
		});
	}
}