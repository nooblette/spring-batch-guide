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
import springbatch.guide.batch.tasklet.RepeatTasklet;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class RepeatTaskletJobConfiguration {
	private static final String JOB_NAME = "repeatTaskletJob";
	private static final String STEP_NAME = "repeatTaskletStep";
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final RepeatTasklet repeatTasklet;
	private int count = 0;

	@Bean(name = JOB_NAME)
	public Job repeatTaskletJob(){
		return new JobBuilder(JOB_NAME, jobRepository)
			.start(repeatTaskletStep1())
			.next(repeatTaskletStep2())
			.incrementer(new RunIdIncrementer())
			.build();
	}

	@Bean(name = STEP_NAME + 1)
	public Step repeatTaskletStep1(){
		return new StepBuilder(STEP_NAME + 1, jobRepository)
			.tasklet((contribution, chunkContext) -> {
				log.info("call repeatTaskletStep1, count : {}", count++);
				return repeatTasklet.execute(contribution, chunkContext);
			}, platformTransactionManager)
			.build();
	}

	@Bean(name = STEP_NAME + 2)
	public Step repeatTaskletStep2(){
		return new StepBuilder(STEP_NAME + 2, jobRepository)
			.tasklet((contribution, chunkContext) -> {
				log.info("call repeatTaskletStep2");
				return RepeatStatus.FINISHED;
			}, platformTransactionManager)
			.build();
	}
}
