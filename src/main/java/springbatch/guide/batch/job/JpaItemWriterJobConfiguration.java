package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.Pay;
import springbatch.guide.batch.dto.Pay2;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaItemWriterJobConfiguration {
	private static final int CHUNK_SIZE = 2;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public Job jpaItemWriterJob() {
		return new JobBuilder("jpaItemWriterJob", jobRepository)
			.start(jpaItemWriterStep())
			.build();
	}

	@Bean
	public Step jpaItemWriterStep(){
		return new StepBuilder("jpaItemWriterStep", jobRepository)
			.<Pay, Pay2>chunk(CHUNK_SIZE, platformTransactionManager)
			.reader(jpaItemWriterReader())
			.processor(jpaItemProcessor())
			.writer(jpaItemWriter())
			.build();
	}

	@Bean
	public ItemReader<Pay> jpaItemWriterReader(){
		return new JpaPagingItemReaderBuilder<Pay>()
			.name("jpaPagingItemReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(CHUNK_SIZE)
			.queryString("SELECT p FROM Pay p WHERE amount >= 2000")
			.build();
	}

	@Bean
	public ItemProcessor<Pay, Pay2> jpaItemProcessor() {
		return pay -> new Pay2(pay.getAmount(), pay.getTxName(), pay.getTxDateTime());
	}

	@Bean
	public ItemWriter<Pay2> jpaItemWriter(){
		// return payList -> {
		// 	for (Pay2 pay2 : payList) {
		// 		log.info("pay={}", pay2);
		// 	}
		// };
		JpaItemWriter<Pay2> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		return jpaItemWriter;
	}
}
