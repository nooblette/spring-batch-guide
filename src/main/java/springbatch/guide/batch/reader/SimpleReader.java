package springbatch.guide.batch.reader;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springbatch.guide.batch.dto.User;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleReader {
	private static final int CHUNK_SIZE = 10;
	private final DataSource dataSource; // DataSourceDI

	public JdbcCursorItemReader<User> reader() {
		return new JdbcCursorItemReaderBuilder<User>()
			.fetchSize(CHUNK_SIZE) // 데이터베이스에서 한번에 조회할 데이터 양
			.dataSource(dataSource) // DB에 접근하기 위한 DataSource 객체
			.rowMapper(new BeanPropertyRowMapper<>(User.class)) // BeanPropertyRowMapper : 쿼리 결과를 Java 인스턴스(e.g. Pay.class)에 매핑하기 위한 Mapper
			.sql("SELECT user_id, name FROM user LIMIT 19") // Reader에서 호출할 쿼리문
			.name("jdbcCursorItemReader") // Reader 이름 지정, Spring Batch의 ExecutionContext에 저장(Bean의 이름 아님)
			.build();
	}
}
