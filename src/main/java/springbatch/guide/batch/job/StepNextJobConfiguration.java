package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class StepNextJobConfiguration {
	@Bean
	public Job stepNextJob(JobRepository jobRepository, @Qualifier("step1") Step step1, @Qualifier("step2") Step step2, @Qualifier("step3") Step step3){
		return new JobBuilder("stepNextJob", jobRepository)
			.start(step1)
			.next(step2)
			.next(step3)
			.build();
	}

	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("step1", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("call step1");
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}

	@Bean
	public Step step2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("step2", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("call step2");
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}

	@Bean
	public Step step3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("step3", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("call step3");
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}
}
