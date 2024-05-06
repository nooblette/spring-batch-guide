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
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.incrementer.CustomJobIncrementer;
import springbatch.guide.batch.tasklet.ChildStep1Tasklet;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChildJobConfiguration {
	private final static String JOB_NAME = "childJob";
	private final static String STEP_NAME = "childStep";
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final ChildStep1Tasklet childStep1Tasklet;

	@Bean(name = JOB_NAME)
	public Job childJob(){
		return new JobBuilder(JOB_NAME, jobRepository)
			.incrementer(new CustomJobIncrementer())
			.start(childStep1())
			.build();
	}

	@Bean(name = STEP_NAME + 1)
	public Step childStep1(){
		return new StepBuilder(STEP_NAME + 1, jobRepository)
			.tasklet(childStep1Tasklet, platformTransactionManager)
			.build();
	}
}
