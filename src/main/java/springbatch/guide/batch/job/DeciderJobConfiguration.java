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
import springbatch.guide.batch.decider.OddDecider;

@Slf4j
@Configuration
public class DeciderJobConfiguration {

	@Bean
	public Job deciderJob(JobRepository jobRepository,
						  @Qualifier("startStep") Step startstep,
						  @Qualifier("evenStep") Step evenStep,
						  @Qualifier("oddStep") Step oddStep,
						  OddDecider oddDecider){
		return new JobBuilder("deciderJob", jobRepository)
			.start(startstep)
			.next(oddDecider)
			.from(oddDecider)
				.on("ODD")
				.to(oddStep)
			.from(oddDecider)
				.on("EVEN")
				.to(evenStep)
			.end()
			.build();
	}

	@Bean
	public Step startStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("startStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				log.info(">>>>> Start!");
				return RepeatStatus.FINISHED;
			}, platformTransactionManager)
			.build();
	}

	@Bean
	public Step evenStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("evenStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				log.info(">>>>> 짝수입니다");
				return RepeatStatus.FINISHED;
			}, platformTransactionManager)
			.build();
	}

	@Bean
	public Step oddStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("oddStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				log.info(">>>>> 홀수입니다");
				return RepeatStatus.FINISHED;
			}, platformTransactionManager)
			.build();
	}
}
