package springbatch.guide.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleItemWriterListener implements ItemWriteListener<String> {
	@Override
	public void beforeWrite(Chunk<? extends String> items) {
		log.info(">> beforeWrite items: {}", items);

	}

	@Override
	public void afterWrite(Chunk<? extends String> items) {
		log.info(">> afterWrite items: {}", items);
	}

	@Override
	public void onWriteError(Exception exception, Chunk<? extends String> items) {
		log.info(">> onWrite error: {}", exception.getMessage());
		items.forEach(item -> log.info(">> onWrite error: {}", item));
	}
}
