package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import springbatch.guide.batch.tasklet.*;

@RequiredArgsConstructor
@Configuration
public class ExecutionContextJobConfiguration {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;

	// ExecutionContext : Spring Batch 프레임워크에서 지원하는 key/value 형식 공유 객체, Job을 실행하면서 필요한 데이터를 저장하는 공간
	// JobExecutionContext : 각 Job의 JobExecution에 저장, 서로 다른 Job 끼리는 JobExecutionContext를 공유할 수 없으나 동일한 Job 내의 서로 다른 Step끼리는 공유할 수 있다.
	// StepExecutionContext : 각 Step의 StepExecution에 저장, Step 사이에서 공유할 수 없다.

	//  JobExecution 및 StepExecution 클래스의 각 구현체를 살펴보면 필드로 ExecutionContext 변수를 선언한 것을 볼 수 있다.
	private final ExecutionContextTasklet1 executionContextTasklet1;
	private final ExecutionContextTasklet2 executionContextTasklet2;
	private final ExecutionContextTasklet3 executionContextTasklet3;
	private final ExecutionContextTasklet4 executionContextTasklet4;

	@Bean
	public Job executionContextJob() {
		return new JobBuilder("executionContextJob", jobRepository)
			.start(executionContextStep1())
			.next(executionContextStep2())
			.next(executionContextStep3())
			.next(executionContextStep4())
			.build();
	}

	@Bean
	public Step executionContextStep1() {
		return new StepBuilder("executionContextStep1", jobRepository)
			.tasklet(executionContextTasklet1, platformTransactionManager)
			.build();
	}

	@Bean
	public Step executionContextStep2() {
		return new StepBuilder("executionContextStep2", jobRepository)
			.tasklet(executionContextTasklet2, platformTransactionManager)
			.build();
	}

	@Bean
	public Step executionContextStep3() {
		return new StepBuilder("executionContextStep3", jobRepository)
			.tasklet(executionContextTasklet3, platformTransactionManager)
			.build();
	}

	@Bean
	public Step executionContextStep4() {
		return new StepBuilder("executionContextStep4", jobRepository)
			.tasklet(executionContextTasklet4, platformTransactionManager)
			.build();
	}
}
