package springbatch.guide.batch.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.Pay;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcCursorItemReaderJobConfiguration {
	private static final int CHUNK_SIZE = 2;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final DataSource dataSource; // DataSourceDI

	@Bean
	public Job jdbcCursorItemReaderJob(){
		return new JobBuilder("jdbcCursorItemReaderJob", jobRepository)
			.start(jdbcCursorItemReaderStep())
			.build();
	}

	@Bean
	public Step jdbcCursorItemReaderStep(){
		return new StepBuilder("jdbcCursorItemReaderJob", jobRepository)
			.<Pay, Pay>chunk(CHUNK_SIZE, platformTransactionManager) //<Reader 반환 타입(읽어올 타입), Writer 전달 타입(출력할 타입)>chunk(Chunk 크기)
			.reader(jdbcCursorItemReader())
			.writer(jdbcCursorItemWriter())
			.build();
	}

	@Bean
	public ItemReader<Pay> jdbcCursorItemReader(){
		// ItemReader의 가장 큰 장점
		// - 데이터를 Streaming 할 수 있음
		// - 즉 read() 메서드는 데이터를 '하나씩' 가져와 ItemWriter에게 전달하고, 다시 다음 데이터를 가져온다.
		// - 이 원리로 reader & processor & write가 chunk 단위로 수행되며 Chunk Size만큼 주기적으로 Commit 된다. (고성능 배치 처리의 핵심)

		// Paging : 실제 쿼리를 limit, offset 등으로 분할 처리
		// Cursor : 분활 처리 X, 내부적으로 fetchSize 크기만큼 가져온 뒤 read()를 통해 하나씩 조회

		/**
		 * 아래 코드를 JdbcTemplate 방식으로 구현하면 다음과 같다.
		 * */
		// JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		// List<Pay> payList = jdbcTemplate.query("SELECT id, amount, tx_name, tx_date_time FROM pay");

		return new JdbcCursorItemReaderBuilder<Pay>()
			.fetchSize(CHUNK_SIZE) // 데이터베이스에서 한번에 조회할 데이터 양
			.dataSource(dataSource) // DB에 접근하기 위한 DataSource 객체
			.rowMapper(new BeanPropertyRowMapper<>(Pay.class)) // BeanPropertyRowMapper : 쿼리 결과를 Java 인스턴스(e.g. Pay.class)에 매핑하기 위한 Mapper
			.sql("SELECT id, amount, tx_name, tx_date_time FROM pay") // Reader에서 호출할 쿼리문
			.name("jdbcCursorItemReader") // Reader 이름 지정, Spring Batch의 ExecutionContext에 저장(Bean의 이름 아님)
			.build();
	}

	@Bean
	public ItemWriter<Pay> jdbcCursorItemWriter(){
		return payList -> {
			for (Pay pay : payList) {
				log.info("current pay={}", pay);
			}
		};
	}
}
