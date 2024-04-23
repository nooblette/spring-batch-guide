package springbatch.guide.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.SimpleChunkDto;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleChunkJobConfiguration {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	@Bean
	public Job simpleChunkJob(){
		System.out.println("SimpleChunkJobConfiguration.simpleChunkJob");
		return new JobBuilder("simpleChunkJob", jobRepository)
			.start(simpleChunkStep())
			.build();
	}

	@Bean
	public Step simpleChunkStep(){
		System.out.println("SimpleChunkJobConfiguration.simpleChunkStep");
		return new StepBuilder("simpleChunkStep", jobRepository)
			.<SimpleChunkDto, String>chunk(10, platformTransactionManager)
			.reader(flatFileReader())
			.processor(simpleChunkProcessor())
			.writer(simpleChunkWriter())
			.build();
	}

	@Bean
	public ItemReader<SimpleChunkDto> flatFileReader(){
		System.out.println("SimpleChunkJobConfiguration.flatFileReader");
		return new FlatFileItemReaderBuilder<SimpleChunkDto>()
			.name("flatFileReader")
			.resource(new ClassPathResource("simpleChunkTest.txt"))
			.targetType(SimpleChunkDto.class)
			.lineMapper(lineMapper())
			.build();
	}

	@Bean
	public LineMapper<SimpleChunkDto> lineMapper(){
		System.out.println("SimpleChunkJobConfiguration.lineMapper");
		DefaultLineMapper<SimpleChunkDto> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames("name");

		BeanWrapperFieldSetMapper<SimpleChunkDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(SimpleChunkDto.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

	@Bean
	public ItemProcessor<SimpleChunkDto, String> simpleChunkProcessor(){
		System.out.println("SimpleChunkJobConfiguration.simpleChunkProcessor");
		return simpleChunkDto -> {
			System.out.println(">> start processing : simpleChunkDto.getName() = " + simpleChunkDto.getName());
			return String.format("%s", simpleChunkDto.getName());
		};
	}

	@Bean
	public ItemWriter<String> simpleChunkWriter(){
		System.out.println("SimpleChunkJobConfiguration.simpleChunkWriter");
		return items -> {
			System.out.println(">> start writing : " + String.join(", ", items));
		};

	}
}
