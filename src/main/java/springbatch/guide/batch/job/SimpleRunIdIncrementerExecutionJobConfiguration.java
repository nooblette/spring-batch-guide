package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleRunIdIncrementerExecutionJobConfiguration {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;

	@Bean
	public Job simpleRunIdIncrementerExecutionJob(){
		return new JobBuilder("simpleRunIdIncrementerExecutionJob", jobRepository)
			.start(simpleRunIdIncrementerExecutionStep1())
			.incrementer(new RunIdIncrementer())
			.build();
	}

	@Bean
	public Step simpleRunIdIncrementerExecutionStep1() {
		log.info("register simpleRunIdIncrementerExecutionStep1 bean");

		return new StepBuilder("simpleRunIdIncrementerExecutionStep1", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				log.info("call simpleRunIdIncrementerExecutionStep1");
				return RepeatStatus.FINISHED;
			}, platformTransactionManager)
			.build();
	}
}
