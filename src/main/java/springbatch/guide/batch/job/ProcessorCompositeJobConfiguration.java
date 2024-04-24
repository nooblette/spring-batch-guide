package springbatch.guide.batch.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.Teacher;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorCompositeJobConfiguration {
	private static final String JOB_NAME = "processorCompositeJob";
	private static final String BEAN_PREFIX = JOB_NAME + "_";
	private static int CHUNK_SIZE = 2;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final EntityManagerFactory entityManagerFactory;

	@Bean(JOB_NAME)
	public Job job(){
		return new JobBuilder("processorCompositeJob", jobRepository)
			.start(processorCompositeStep())
			.build();
	}

	@Bean(BEAN_PREFIX + "step")
	@JobScope
	public Step processorCompositeStep(){
		return new StepBuilder(BEAN_PREFIX + "step", jobRepository)
			.<Teacher, String>chunk(CHUNK_SIZE, platformTransactionManager)
			.reader(processorCompositeItemReader())
			.processor(processorCompositeItemProcessor())
			.writer(processorCompositeStepItemWriter())
			.build();
	}

	@Bean
	public ItemReader<Teacher> processorCompositeItemReader(){
		return new JpaPagingItemReaderBuilder<Teacher>()
			.name(BEAN_PREFIX + "reader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(CHUNK_SIZE)
			.queryString("SELECT t FROM Teacher t")
			.build();
	}

	@Bean // ItemProcessor는 <ItemReader의 반환 타입, ItemWriter의 입력 타입> 으로 제네릭을 선언해야한다.
	public ItemProcessor<Teacher, String> processorCompositeItemProcessor(){
		// delegates 목록에 포함된 모든 ItemProcessor 구현체는 같은 제네릭 타입을 가져한다.(즉 입력 객체와 출력 객체의 타입이 동일해야한다)
		// 제네릭 타입을 활용할 수 있는 ItemProcessor간 체이닝이라면 제네릭으로 선언하는게 더 type safety할 것
		List<ItemProcessor<Object, Object>> delegates = new ArrayList<>(2);
		delegates.add(processor1());
		delegates.add(processor2());

		// CompositeItemProcessor : ItemProcessor간 체이닝을 지원하는 Processor
		CompositeItemProcessor<Teacher, String> itemProcessor = new CompositeItemProcessor<>();
		itemProcessor.setDelegates(delegates); // ItemProcessor(processor1, processor2)간 체이닝

		return itemProcessor;
	}

	public ItemProcessor<Object, Object> processor1(){
		return item -> {
			if(item instanceof Teacher){
				return ((Teacher)item).getName();
			}
			return null;
		};
	}

	public ItemProcessor<Object, Object> processor2(){
		return name -> "안녕하세요 " + name + "입니다.";
	}

	@Bean
	public ItemWriter<String> processorCompositeStepItemWriter(){
		return items -> {
			for (String item : items) {
				log.info("Teacher name={}", item);
			}
		};
	}
}
