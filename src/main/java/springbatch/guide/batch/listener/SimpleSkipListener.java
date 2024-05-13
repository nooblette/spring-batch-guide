package springbatch.guide.batch.listener;

import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.User;

@Slf4j
@Component
public class SimpleSkipListener implements SkipListener<User, String> {

	@Override
	public void onSkipInProcess(User item, Throwable t) {
		log.info(">> onSkipInProcess, item:{}", item);
		log.info(">> error message : {}", t.getMessage());
	}
}
