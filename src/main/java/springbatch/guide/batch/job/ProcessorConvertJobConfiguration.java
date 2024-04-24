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
public class ProcessorConvertJobConfiguration {
	public static final String JOB_NAME = "ProcessorConvertBatch";
	public static final String BEAN_PREFIX = JOB_NAME + "_";
	private static final int CHUNK_SIZE = 2;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final EntityManagerFactory entityManagerFactory;

	@Bean(JOB_NAME)
	public Job processorConvertJob() {
		return new JobBuilder("processorConvertJob", jobRepository)
			.start(processorConvertStep())
			.build();
	}

	@Bean(BEAN_PREFIX + "step")
	public Step processorConvertStep() {
		return new StepBuilder("processorConvertStep", jobRepository)
			.<Teacher, String>chunk(CHUNK_SIZE, platformTransactionManager)
			.reader(processorConvertReader())
			.processor(processorConvertProcessor())
			.writer(processorConvertWriter())
			.build();
	}

	@Bean
	public ItemReader<Teacher> processorConvertReader(){
		return new JpaPagingItemReaderBuilder<Teacher>()
			.name(BEAN_PREFIX + "reader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(CHUNK_SIZE)
			.queryString("select t from Teacher t")
			.build();
	}

	@Bean
	public ItemProcessor<Teacher, String> processorConvertProcessor(){
		// Teacher -> String 변환
		return Teacher::getName;
	}

	@Bean
	public ItemWriter<String> processorConvertWriter(){
		return nameList -> {
			for (String name : nameList) {
				log.info("teacher name={}", name);
			}
		};
	}
}
