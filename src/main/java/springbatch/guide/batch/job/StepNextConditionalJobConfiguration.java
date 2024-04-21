package springbatch.guide.batch.job;

import org.springframework.batch.core.ExitStatus;
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
public class StepNextConditionalJobConfiguration {
	@Bean
	public Job stepNextConditionalJob(JobRepository jobRepository,
									  @Qualifier("conditionalJobStep1") Step conditionalJobStep1,
									  @Qualifier("conditionalJobStep2") Step conditionalJobStep2,
									  @Qualifier("conditionalJobStep3") Step conditionalJobStep3) {
		return new JobBuilder("stepNextConditionalJob", jobRepository)
			.start(conditionalJobStep1) // 1. conditionalJobStep1 실행
				// 1-1. conditionalJobStep1 실패한 경우 -> conditionalJobStep3 진행한다
				.on(ExitStatus.FAILED.getExitCode())
				.to(conditionalJobStep3)
				// 1-2. conditionalJobStep3 결과 상관없이 Flow를 종료한다.
				.on("*")
				.end() // FlowBuilder를 반환
			.from(conditionalJobStep1) // 2. conditionalJobStep1 실행
				// 2-1. FAILED 외의 모든 경우 -> conditionalJobStep2 진행한다.
				.on("*")
				.to(conditionalJobStep2)
				.next(conditionalJobStep3) // 2-2. conditionalJobStep2 정상 종료되면 step3를 진행한다.
				.on("*") // 2-3. conditionalJobStep3 결과 상관없이 flow를 종료한다.
				.end() // FlowBuilder를 반환
			.end()// job 종료, FlowBuilder를 종료
			.build();
	}

	@Bean
	public Step conditionalJobStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("conditionalJobStep1", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("call conditionalJobStep1");
				contribution.setExitStatus(ExitStatus.FAILED); // contribution의 ExitStatus를 보고 flow가 진행된다.
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}

	@Bean
	public Step conditionalJobStep2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("conditionalJobStep2", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("call conditionalJobStep2");
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}

	@Bean
	public Step conditionalJobStep3(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
		return new StepBuilder("conditionalJobStep3", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("call conditionalJobStep3");
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}
}
