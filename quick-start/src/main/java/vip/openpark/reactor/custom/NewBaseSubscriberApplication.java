package vip.openpark.reactor.custom;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

/**
 * @author anthony
 * @version 2024/2/17 15:44
 */
@Slf4j
public class NewBaseSubscriberApplication {
	public static void main(String[] args) {
		Flux.just("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
			.subscribe(new BaseSubscriber<String>() {
				@Override
				protected void hookOnSubscribe(Subscription subscription) {
					log.info("new BaseSubscriber hookOnSubscribe");
					// 订阅完成后，请求一个元素
					request(1);
				}
				
				@Override
				protected void hookOnNext(String value) {
					log.info("new BaseSubscriber hookOnNext : {}", value);
					// 业务处理完成后，再去请求一个元素
					request(1);
				}
				
				@Override
				protected void hookOnComplete() {
					log.info("new BaseSubscriber hookOnComplete");
				}
				
				@Override
				protected void hookFinally(SignalType type) {
					log.info("new BaseSubscriber hookFinally : {}", type);
				}
			});
	}
}