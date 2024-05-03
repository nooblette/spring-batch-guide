package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.incrementer.CustomJobIncrementer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleCustomJobIncrementerExecutionJobConfiguration {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;

	@Bean
	public Job simpleCustomJobIncrementerExecutionJob(){
		return new JobBuilder("simpleCustomJobIncrementerExecutionJob", jobRepository)
			.start(simpleCustomJobIncrementerExecutionStep1())
			.incrementer(
				// incrementer()는 JobParametersIncrementer 인터페이스를 매개변수로 받고, 이 인터페이스의 getNext() 메서드가 다음 Job 실행시 사용될 JobParameter 오브젝트를 반환한다.
				// 만일 incrementer 설정 없이 배치를 실행한 후에 incrementer를 설정한다면 Job을 식별할 수 없어 실행되지 않는다. (이전 배치에서 다음 Job 실행시 사용할 JobParameter 오브젝트를 생성하지 않았기 때문)
				new CustomJobIncrementer() // getNext() 메서드를 구현한다, getNext()에 구현한 로직대로 JobParmaters에서 필요한 값을 증가시켜 다음 Job 실행시 사용될 JobParameters 오브젝트를 얻는다.
			)
			.build();
	}

	@Bean
	public Step simpleCustomJobIncrementerExecutionStep1() {
		log.info("register simpleCustomJobIncrementerExecutionStep1 bean");

		return new StepBuilder("simpleCustomJobIncrementerExecutionStep1", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				log.info("call simpleCustomJobIncrementerExecutionStep1");
				return RepeatStatus.FINISHED;
			}, platformTransactionManager)
			.build();
	}
}
