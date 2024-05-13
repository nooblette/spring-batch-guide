package springbatch.guide.batch.listener;

import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.User;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleItemReaderListener implements ItemReadListener<User> {
	@Override
	public void beforeRead() {
		//log.info(">> beforeRead");
	}

	@Override
	public void afterRead(User item) {
		log.info(">> afterRead, item = {}", item);

	}

	@Override
	public void onReadError(Exception ex) {
	}
}
