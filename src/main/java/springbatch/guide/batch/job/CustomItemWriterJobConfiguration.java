package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
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
import springbatch.guide.batch.dto.Pay;
import springbatch.guide.batch.dto.Pay2;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomItemWriterJobConfiguration {
	private static final int CHUNK_SIZE = 2;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public Job customItemWriterJob() {
		return new JobBuilder("customItemWriterJob", jobRepository)
			.start(customItemWriterStep())
			.build();
	}

	@Bean
	public Step customItemWriterStep(){
		return new StepBuilder("customItemWriterStep", jobRepository)
			.<Pay, Pay2>chunk(CHUNK_SIZE, platformTransactionManager)
			.reader(customItemWriterReader())
			.processor(customItemWriterProcessor())
			.writer(customItemWriter())
			.build();
	}

	@Bean
	public ItemReader<Pay> customItemWriterReader(){
		return new JpaPagingItemReaderBuilder<Pay>()
			.name("jpaPagingItemReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(CHUNK_SIZE)
			.queryString("SELECT p FROM Pay p")
			.build();
	}

	@Bean
	public ItemProcessor<Pay, Pay2> customItemWriterProcessor() {
		return pay -> new Pay2(pay.getAmount(), pay.getTxName(), pay.getTxDateTime());
	}

	@Bean
	public ItemWriter<Pay2> customItemWriter(){
		// CustomItemWriter 구현 : ItemWriter 인터페이스의 write() 메서드를 오버라이딩한다.
		return new ItemWriter<Pay2>() {
			@Override
			public void write(Chunk<? extends Pay2> chunk) throws Exception {
				for (Pay2 pay2 : chunk) {
					System.out.println("pay2 = " + pay2);
				}
			};
		};

		// 람다식으로 작성(java 8 이상)
		// return pay2List -> {
		// 	for (Pay2 pay2 : pay2List) {
		// 		System.out.println("pay2 = " + pay2);
		// 	}
		// };
	}
}
