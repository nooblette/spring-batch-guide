package springbatch.guide.batch.job;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.decider.ParameterDecider;
import springbatch.guide.batch.tasklet.ParentStep1Tasklet;

@Slf4j
@Configuration
public class ParentJobConfiguration {
	private static final String JOB_NAME = "parentJob";
	private static final String STEP_NAME = "parentStep";
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final ParameterDecider parameterDecider;
	private final ParentStep1Tasklet parentStep1Tasklet;
	private final JobLauncher jobLauncher;
	private final Job childJob;

	public ParentJobConfiguration(JobRepository jobRepository,
								  PlatformTransactionManager platformTransactionManager,
								  ParameterDecider parameterDecider,
								  ParentStep1Tasklet parentStep1Tasklet,
								  JobLauncher jobLauncher,
								  @Qualifier("childJob") Job childJob) {
		this.jobRepository = jobRepository;
		this.platformTransactionManager = platformTransactionManager;
		this.parameterDecider = parameterDecider;
		this.parentStep1Tasklet = parentStep1Tasklet;
		this.jobLauncher = jobLauncher;
		this.childJob = childJob;
	}

	@Bean(name = JOB_NAME)
	public Job parentJob(){
		return new JobBuilder(JOB_NAME, jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(parentStep1())
			.next(parameterDecider)
			.from(parameterDecider)
				.on("LAUNCH")
				.to(parentStep2())
			.from(parameterDecider)
				.on("*")
				.end()
			.end()
			.build();
	}

	@Bean(name = STEP_NAME + 1)
	@JobScope
	public Step parentStep1(){
		return new StepBuilder(STEP_NAME + 1, jobRepository)
			.tasklet(parentStep1Tasklet, platformTransactionManager)
			.build();
	}

	@Bean(name = STEP_NAME + 2)
	@JobScope
	public Step parentStep2() {
		return new StepBuilder(STEP_NAME + 2, jobRepository)
			.job(childJob)
			.launcher(jobLauncher)
			.parametersExtractor(jobParametersExtractor())
			.listener(stepExecutionListener())
			.build();
	}

	private DefaultJobParametersExtractor jobParametersExtractor() {
		DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
		extractor.setKeys(new String[]{"name", "run"});
		return extractor;
	}

	private StepExecutionListener stepExecutionListener(){
		return new StepExecutionListener() {
			@Override
			public void beforeStep(StepExecution stepExecution) {
				stepExecution.getExecutionContext().putString("name", "user1");

				// jobExecutionContext의 값을 job Parameter로 childJob에게 전달
				String run = (String) stepExecution.getJobExecution().getExecutionContext().get("run");
				stepExecution.getExecutionContext().putString("run", run);
			}

			@Override
			public ExitStatus afterStep(StepExecution stepExecution) {
				return null;
			}
		};
	}
}
