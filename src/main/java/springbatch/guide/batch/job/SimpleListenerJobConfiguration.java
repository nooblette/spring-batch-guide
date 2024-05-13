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
import springbatch.guide.batch.execption.SimpleSkipException;
import springbatch.guide.batch.listener.SimpleChunkListener;
import springbatch.guide.batch.listener.SimpleItemProcessorListener;
import springbatch.guide.batch.listener.SimpleItemReaderListener;
import springbatch.guide.batch.listener.SimpleItemWriterListener;
import springbatch.guide.batch.listener.SimpleJobExecutionListener;
import springbatch.guide.batch.listener.SimpleSkipListener;
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
	private final SimpleSkipListener simpleSkipListener;


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
				.faultTolerant() // Skip 기능을 사용하기 위함
				.skip(SimpleSkipException.class) // Skip 대상 예외 클래스 지정
				// skip()에 지정하지 않은 예외가 발생할 경우 ExhaustedRetryException 에외가 발생하며 배치가 실패처리한다.
				// e.g.
				//  - ItemWriter에서 예외가 발생한 경우, ItemProcessor를 다시 수행한 뒤 ItemWriter를 동작하기 전에 이전에 발생한 예외를 확인한다.
				//  - 만약 이전에 발생한 에외가 skip()에 명시한 예외가 아니라면 배치를 중단하고 실패처리한다.
				.skipLimit(6) // 최대 Skip 가능 횟수
				// 6회 이상 SimpleSkipException 예외가 발생할 경우 SkipLimitExceedException이 발생하고 배치가 실패처리 된다.
				.listener(simpleSkipListener) // Skip 발생시 동작할 SkipListener, WriteListener의 afterWrite()까지 호출하고나서 SkipListener의 메서드가 수행된다.
			.listener(simpleItemReaderListener)
			.listener(simpleItemProcessorListener)
			.listener(simpleItemWriterListener)
			.listener(simpleChunkListener)
			.listener(simpleStepExecutionListener)
			.build();
	}
}
