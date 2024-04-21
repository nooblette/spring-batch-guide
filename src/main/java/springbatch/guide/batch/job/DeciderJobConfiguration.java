package springbatch.guide.batch.job;

import java.util.Random;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
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
public class DeciderJobConfiguration {

	@Bean
	public Job deciderJob(JobRepository jobRepository,
						  @Qualifier("startStep") Step startstep,
						  @Qualifier("evenStep") Step evenStep,
						  @Qualifier("oddStep") Step oddStep,
						  JobExecutionDecider jobExecutionDecider){
		return new JobBuilder("deciderJob", jobRepository)
			.start(startstep)
			.next(jobExecutionDecider)
			.from(jobExecutionDecider)
				.on("ODD")
				.to(oddStep)
			.from(jobExecutionDecider)
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


	@Bean
	public JobExecutionDecider jobExecutionDecider(){
		return new OddDecider();
	}

	public static class OddDecider implements JobExecutionDecider {
		@Override
		public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
			Random random = new Random();

			int randomNumber = random.nextInt(50) + 1;
			log.info("랜덤숫자 : {}", randomNumber);

			if(randomNumber % 2 == 0){
				return new FlowExecutionStatus("EVEN");
			} else {
				return new FlowExecutionStatus("ODD");

			}
		}
	}
}
