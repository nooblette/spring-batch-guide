package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
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
import springbatch.guide.batch.dto.User;
import springbatch.guide.batch.listener.SimpleChunkListener;
import springbatch.guide.batch.listener.SimpleItemProcessorListener;
import springbatch.guide.batch.listener.SimpleItemReaderListener;
import springbatch.guide.batch.listener.SimpleItemWriterListener;
import springbatch.guide.batch.listener.SimpleJobExecutionListener;
import springbatch.guide.batch.listener.SimpleStepExecutionListener;
import springbatch.guide.batch.processor.SimpleProcessor;
import springbatch.guide.batch.reader.SimpleReader;
import springbatch.guide.batch.writer.SimpleWriter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleListenerJobConfiguration {

	private static final String JOB_NAME = "simpleListenerJob";
	private static final String STEP_NAME = "simpleListenerStep";

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final SimpleReader simpleReader;
	private final SimpleProcessor simpleProcessor;
	private final SimpleWriter simpleWriter;
	private final SimpleJobExecutionListener simpleJobExecutionListener;
	private final SimpleChunkListener simpleChunkListener;
	private final SimpleStepExecutionListener simpleStepExecutionListener;
	private final SimpleItemReaderListener simpleItemReaderListener;
	private final SimpleItemWriterListener simpleItemWriterListener;
	private final SimpleItemProcessorListener simpleItemProcessorListener;


	@Bean(name = JOB_NAME)
	public Job simpleListenerJob() {
		return new JobBuilder(JOB_NAME, jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(simpleJobExecutionListener)
			.start(simpleListenerStep1())
			.next(simpleListenerStep2())
			.build();
	}

	@Bean(name = STEP_NAME + 1)
	@JobScope
	public Step simpleListenerStep1() {
		return new StepBuilder(STEP_NAME, jobRepository)
			.listener(simpleStepExecutionListener)
			.tasklet(((stepContribution, chunkContext) -> {
				log.info(">> this is simpleListenerStep1()");
				return RepeatStatus.FINISHED;
			}), transactionManager)
			.build();
	}

	@Bean(name = STEP_NAME + 2)
	@JobScope
	public Step simpleListenerStep2() {
		return new StepBuilder(STEP_NAME + 2, jobRepository)
			.<User, String>chunk(10, transactionManager)
			.reader(simpleReader.reader())
			.processor(simpleProcessor)
			.writer(simpleWriter)
			.listener(simpleItemReaderListener)
			.listener(simpleItemProcessorListener)
			.listener(simpleItemWriterListener)
			.listener(simpleChunkListener)
			.listener(simpleStepExecutionListener)
			.build();
	}
}