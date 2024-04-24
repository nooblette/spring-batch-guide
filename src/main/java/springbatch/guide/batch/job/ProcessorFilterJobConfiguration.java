package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.Teacher;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorFilterJobConfiguration {
	public static final String JOB_NAME = "ProcessorFilterBatch";
	public static final String BEAN_PREFIX = JOB_NAME + "_";
	private static final int CHUNK_SIZE = 2;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final EntityManagerFactory entityManagerFactory;

	@Bean(JOB_NAME)
	public Job processorFilterJob() {
		return new JobBuilder("processorFilterJob", jobRepository)
			.start(processorFilterStep())
			.build();
	}

	@Bean(BEAN_PREFIX + "step")
	public Step processorFilterStep() {
		return new StepBuilder("processorFilterStep", jobRepository)
			.<Teacher, String>chunk(CHUNK_SIZE, platformTransactionManager)
			.reader(processorFilterReader())
			.processor(processorFilterProcessor())
			.writer(processorFilterWriter())
			.build();
	}

	@Bean
	public ItemReader<Teacher> processorFilterReader(){
		return new JpaPagingItemReaderBuilder<Teacher>()
			.name(BEAN_PREFIX + "reader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(CHUNK_SIZE)
			.queryString("select t from Teacher t")
			.build();
	}

	@Bean
	public ItemProcessor<Teacher, String> processorFilterProcessor(){
		// Teacher id가 홀수인 경우만 필터링
		return teacher -> {
			boolean isIgnoredTarget = teacher.getId() % 2 == 0L;
			if(isIgnoredTarget) {
				log.info("teacher name : {}, isIgnoredTarget", teacher.getName(), isIgnoredTarget);
				return null; // processor가 null을 반환하면 Writer에 전달되지 않는다.
			}

			return teacher.getName();
		};
	}

	@Bean
	public ItemWriter<String> processorFilterWriter(){
		return nameList -> {
			for (String name : nameList) {
				log.info("teacher name={}", name);
			}
		};
	}
}
