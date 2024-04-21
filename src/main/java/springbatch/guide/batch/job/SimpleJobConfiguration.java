package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.beanprinter.BeanListPrinter;

@Slf4j // 로깅을 위함
@RequiredArgsConstructor
@Configuration // SimpleJobConfiguration 클래스를 스프링 빈으로 등록
public class SimpleJobConfiguration {
	private final BeanListPrinter beanListPrinter; // 빈(Bean) 목록 출력

	@Bean
	public Job simpleJob(JobRepository jobRepository, Step simpleStep1){
		System.out.println("SimpleJobConfiguration.simpleJob");
		beanListPrinter.printBeanList();
		return new JobBuilder("simpleJob", jobRepository)
			.start(simpleStep1)
			.build();
	}

	@Bean
	public Step simpleStep1(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager platformTransactionManager) {
		System.out.println("SimpleJobConfiguration.simpleStep1");
		beanListPrinter.printBeanList();
		return new StepBuilder("simpleStep1", jobRepository)
			.tasklet(tasklet, platformTransactionManager)
			.build();
	}

	@Bean
	public Tasklet tasklet(){
		System.out.println("SimpleJobConfiguration.tasklet");
		return ((contribution, chunkContext) -> {
			log.info("start step1");
			log.info("end step1");
			return RepeatStatus.FINISHED;
		});
	}
}
