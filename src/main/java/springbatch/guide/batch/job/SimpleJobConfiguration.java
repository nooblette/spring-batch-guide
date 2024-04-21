package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value; // lombok.Value 애노테이션이 아님에 주의
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅을 위함
@Configuration // SimpleJobConfiguration 클래스를 스프링 빈으로 등록
public class SimpleJobConfiguration {

	@Bean
	public Job simpleJob(JobRepository jobRepository, Step simpleStep1){
		// System.out.println("SimpleJobConfiguration.simpleJob");
		// beanListPrinter.printBeanList();
		return new JobBuilder("simpleJob", jobRepository)
			.start(simpleStep1)
			.build();
	}

	@Bean
	@JobScope
	// @Value("#{jobParameters[requestDate]}") : 외부로부터 전달받은 job 실행 parameter를 사용한다.
	public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate, JobRepository jobRepository,
							 PlatformTransactionManager platformTransactionManager) {
		// System.out.println("SimpleJobConfiguration.simpleStep1");
		// beanListPrinter.printBeanList();
		return new StepBuilder("simpleStep1", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info("start step1, requestDate : {}", requestDate);
				log.info("end step1");
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}
}
