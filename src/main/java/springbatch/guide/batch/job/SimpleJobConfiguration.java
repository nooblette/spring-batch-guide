package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value; // lombok.Value 애노테이션이 아님에 주의
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.beanprinter.BeanListPrinter;

@Slf4j // 로깅을 위함
@RequiredArgsConstructor
@Configuration // @Bean 애노테이션으로 생성된 스프링 빈 객체(simpleJob, simpleStep1)를 스프링 컨테이너에 등록, 싱글톤 패턴을 따르도록 보장
public class SimpleJobConfiguration {
	private final BeanListPrinter beanListPrinter;

	@Bean // simpleJob 객체 스프링 빈 수동 등록
	public Job simpleJob(JobRepository jobRepository, @Qualifier("simpleStep1") Step simpleStep1, @Qualifier("simpleStep2") Step simpleStep2){
		return new JobBuilder("simpleJob", jobRepository)
			.start(simpleStep1)
			.next(simpleStep2)
			.build();
	}

	@Bean // simpleStep1 객체 스프링 빈 수동 등록
	@JobScope // simpleStep1 빈 생성 시점을 스프링 애플리케이션 실행이 아닌 해당 step 생성 메서드가 호출까지 지연(Late Binding)한다. (JobParameter를 유연하게 할당하기 위함)
	// @Value("#{jobParameters[requestDate]}") : 외부로부터 전달받은 job 실행 parameter를 사용한다.
	public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate, JobRepository jobRepository,
							 PlatformTransactionManager platformTransactionManager) {
		return new StepBuilder("simpleStep1", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("start step1, requestDate : {}", requestDate);
				// throw new IllegalArgumentException("simpleStep1 예외 발생");
				log.info("end step1");
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}

	@Bean
	@JobScope // Late Binding
	public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate, JobRepository jobRepository,
							PlatformTransactionManager platformTransactionManager) {
		return new StepBuilder("simpleStep2", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("start step2, requestDate : {}", requestDate);
				log.info("end step2");
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}
}
