package springbatch.guide.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.Pay;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcItemWriterJobConfiguration {
	private static final int CHUNK_SIZE = 2;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final DataSource dataSource; // DataSourceDI

	@Bean
	public Job jdbcItemWriterJob(){
		return new JobBuilder("jdbcItemWriterJob", jobRepository)
			.start(jdbcItemWriterStep())
			.build();
	}

	@Bean
	public Step jdbcItemWriterStep(){
		return new StepBuilder("jdbcItemWriterStep", jobRepository)
			.<Pay, Pay>chunk(CHUNK_SIZE, platformTransactionManager) //<Reader 반환 타입(읽어올 타입), Writer 전달 타입(출력할 타입)>chunk(Chunk 크기)
			.reader(jdbcItemWriterReader())
			.writer(jdbcItemWriter())
			.build();
	}

	@Bean
	public ItemReader<Pay> jdbcItemWriterReader(){
		return new JdbcCursorItemReaderBuilder<Pay>()
			.fetchSize(CHUNK_SIZE) // 데이터베이스에서 한번에 조회할 데이터 양
			.dataSource(dataSource) // DB에 접근하기 위한 DataSource 객체
			.rowMapper(new BeanPropertyRowMapper<>(Pay.class)) // BeanPropertyRowMapper : 쿼리 결과를 Java 인스턴스(e.g. Pay.class)에 매핑하기 위한 Mapper
			.sql("SELECT id, amount, tx_name, tx_date_time FROM pay") // Reader에서 호출할 쿼리문
			.name("jdbcCursorItemReader") // Reader 이름 지정, Spring Batch의 ExecutionContext에 저장(Bean의 이름 아님)
			.build();
	}

	/**
	 * ItemReader에서 넘어온 데이터를 출력하기위한 ItemWriter*/
	@Bean
	public ItemWriter<Pay> jdbcItemWriter(){ // JdbcBatchItemWriter의 제네릭 타입은 Reader에서 넘겨주는 값의 타입과 동일해야한다.
		// beanMapped() 방식으로 작성(자바 객체(e.g. Pay) 기반으로 Insert SQL의 Values를 매핑)
		return new JdbcBatchItemWriterBuilder<Pay>()
			.dataSource(dataSource)
			// :field1, :field2, :field3 : 자바 프로퍼티 접근법(Getter)으로 필드에 매핑되어 값이 할당(columnMapped 방식일 경우 Map의 Key)
			.sql("insert into pay2(amount, tx_name, tx_date_time) values (:amount, :txName, :txDateTime)")
			.beanMapped()
			.build();

		// columnMapped 방식으로 작성하는 경우(Key,Value 기반으로 Insert SQL의 Values를 매핑)
		// return new JdbcBatchItemWriterBuilder<Map<String, Object>>()
		// 	.columnMapped()
		// 	.dataSource(dataSource)
		// 	.sql("insert into pay2(amount, tx_name, tx_date_time) values (:amount, :txName, :txDateTime)")
		// 	.build();
	}
}
