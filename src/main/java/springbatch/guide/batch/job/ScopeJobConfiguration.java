package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ScopeJobConfiguration {
	private final SimpleJobTasklet scopeStep2Tasklet;
	private final ApplicationContext applicationContext;

	@Bean
	public Job scopeJob(JobRepository jobRepository,
						@Qualifier("scopeStep1") Step scopeStep1,
						@Qualifier("scopeStep2") Step scopeStep2) {
		log.info(">> this is scopeJob");
		Object simpleJobTasklet = applicationContext.getBean("simpleJobTasklet");
		SimpleJobTasklet simpleJobTasklet1 = (SimpleJobTasklet)simpleJobTasklet;
		simpleJobTasklet1.test();

		return new JobBuilder("scopeJob", jobRepository)
			.start(scopeStep1)
			.next(scopeStep2)
			.build();
	}

	@Bean
	@JobScope // jobParameters를 사용하려면 Step Bean의 Scope는 JobScope(Job 실행 시 빈 생성)이여야 한다.
	public Step scopeStep1(@Value("#{jobParameters[requestDate]}") String requestDate,
						   JobRepository jobRepository,
						   PlatformTransactionManager platformTransactionManager) {
		return new StepBuilder("scopeStep1", jobRepository)
			.tasklet(((contribution, chunkContext) -> {
				log.info(">> this is scopeStep1");
				log.info(">> requestDate= {}", requestDate);
				return RepeatStatus.FINISHED;
			}), platformTransactionManager)
			.build();
	}

	@Bean
	@JobScope
	public Step scopeStep2(@Value("#{jobParameters[requestDate]}") String requestDate,
						   //@Qualifier("scopeStep2Tasklet") Tasklet scopeStep2Tasklet,
						   JobRepository jobRepository,
						   PlatformTransactionManager platformTransactionManager) {
		log.info(">> this is scopeStep2");
		log.info(">> requestDate= {}", requestDate);
		return new StepBuilder("scopeStep2", jobRepository)
			.tasklet(scopeStep2Tasklet, platformTransactionManager)
			.build();
	}

	//@Bean
	//@StepScope
	// public Tasklet scopeStep2Tasklet(@Value("#{jobParameters[requestDate]}") String requestDate,
	// 								 JobRepository jobRepository,
	// 								 PlatformTransactionManager platformTransactionManager) {
	// 	return (contribution, chunkContext) -> {
	// 		log.info(">> this is scopeStep2Tasklet");
	// 		log.info(">> requestDate= {}", requestDate);
	// 		return RepeatStatus.FINISHED;
	// 	};
	// }
}
