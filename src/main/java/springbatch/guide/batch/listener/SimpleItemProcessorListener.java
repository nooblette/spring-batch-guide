package springbatch.guide.batch.listener;

import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.User;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleItemProcessorListener implements ItemProcessListener<User, String> {
	@Override
	public void beforeProcess(User item) {
		log.info(">> beforeProcess item={}", item);

	}

	@Override
	public void afterProcess(User item, String result) {
		log.info(">> afterProcess item={}, result ={}", item, result);
	}

	@Override
	public void onProcessError(User item, Exception e) {

	}
}
