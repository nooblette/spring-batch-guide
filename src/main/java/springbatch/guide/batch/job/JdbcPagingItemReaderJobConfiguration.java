package springbatch.guide.batch.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.database.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.Pay;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcPagingItemReaderJobConfiguration {
	private static final int CHUNK_SIZE = 2;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;
	private final DataSource dataSource;

	@Bean
	public Job jdbcPagingItemReaderJob() throws Exception {
		return new JobBuilder("jdbcPagingItemReaderJob", jobRepository)
			.start(jdbcPagingItemReaderStep())
			.build();
	}

	@Bean
	public Step jdbcPagingItemReaderStep() throws Exception {
		return new StepBuilder("jdbcPagingItemReaderStep", jobRepository)
			.<Pay, Pay>chunk(CHUNK_SIZE, platformTransactionManager) //<Reader 반환 타입(읽어올 타입), Writer 전달 타입(출력할 타입)>chunk(Chunk 크기)
			.reader(jdbcPagingItemReader())
			.writer(jdbcPagingItemWriter())
			.build();
	}

	@Bean
	public ItemReader<Pay> jdbcPagingItemReader() throws Exception {
		Map<String, Object> parameterValues = Map.of("amount", 2000); // 쿼리에 전달할 파라미터

		return new JdbcPagingItemReaderBuilder<Pay>()
			.pageSize(CHUNK_SIZE) // CHUNK_SIZE 크기 만큼 paging (실제 실행 쿼리 : SELECT id, amount, tx_name, tx_date_time FROM pay WHERE amount >= :amount ORDER BY id ASC LIMIT 2)
			.dataSource(dataSource)
			.rowMapper(new BeanPropertyRowMapper<>(Pay.class)) // BeanPropertyRowMapper : 쿼리 결과를 Java 인스턴스(e.g. Pay.class)에 매핑하기 위한 Mapper
			.queryProvider(createPagingQueryProvider())
			.parameterValues(parameterValues)
			.name("jdbcCursorItemReader")
			.build();
	}

	@Bean
	public PagingQueryProvider createPagingQueryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();

		// 각 DataBase 별로 Paging 전략이 다르고, DB에 맞게 이 전략을 선택해야한다. 하지만 개발자가 직접 코드로 설정하는 경우 매번 코드를 수정해야하는 불편함이 있다
		// 예를 들어 로컬 테스트는 H2를 하면서, 개발/운영은 MySQL을 사용한다면 매번 코드를 수정해야한다.
		// Spring Batch 에서는 SqlPagingQueryProviderFactoryBean 객체의 DataSource 설정 값을 보고 데이터베이스별로 적절한 Provider 중 하나를 자동으로 선택한다.
		queryProvider.setDataSource(dataSource);

		queryProvider.setSelectClause("id, amount, tx_name, tx_date_time");
		queryProvider.setFromClause("from pay");
		queryProvider.setWhereClause("where amount >= :amount"); // parameterValues 에서 설정된 amount 값을 바인딩한다.

		Map<String, Order> sortKeys = new HashMap<>(1);
		sortKeys.put("id", Order.ASCENDING);

		queryProvider.setSortKeys(sortKeys);

		return queryProvider.getObject();
	}

	@Bean
	public ItemWriter<Pay> jdbcPagingItemWriter(){
		return payList -> {
			for (Pay pay : payList) {
				log.info("current pay={}", pay);
			}
		};
	}
}
