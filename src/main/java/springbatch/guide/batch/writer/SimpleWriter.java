package springbatch.guide.batch.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleWriter implements ItemWriter<String> {

	@Override
	public void write(Chunk<? extends String> chunk) {
		log.info(">> this is SimpleWriter.write");

		chunk.forEach(item -> log.info(">> item: {}", item));
	}
}