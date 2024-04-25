package springbatch.guide.batch;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

// @ExtendWith(SpringExtension.class) // SpringExtension 클래스가 스프링 컨테이너(ApplicationContext)를 만들고 관리, @SpringBatchTest 어노테이션에 포함되어 생략 가능
@SpringBootTest( // 통합 테스트 실행시 사용할 java 설정 선택
	classes = {
		//JpaPagingItemReaderJobConfiguration.class, // 테스트 대상 Batch job 지정
		TestBatchConfig.class
	}
)
@SpringBatchTest // ApplicationContext에 스프링 배치 테스트에 필요한 유틸 Bean을 등록
/**
 * @SpringBatchTest가 자동으로 등록해주는 Bean은 다음과 같다.
 * 1. JobLauncherTestUtils : 스프링 배치 테스트에 필요한 전반적인 유틸 기능 지원 (통합 테스트에 필요한 Bean)
 * 2. JobRepositoryTestUtils : DB에 생성된 JobExecution을 쉽게 생성/삭제할 수 있도록 지원 (통합 테스트에 필요한 Bean)
 * 3. StepScopeTestExecutionListener : 배치 단위 테스트시 StepScope 생성 (단위 테스트에 필요한 Bean)
 * 	- StepScopeTestExecution 컨텍스트를 통해 JobParameter 등을 단위 테스트에서 DI 받을 수 있다.
 * 4. JobScopeTestExecutionListener : 배치 단위 테스트시 JobScope 생성 (단위 테스트에 필요한 Bean)
 * 	- JobScopeTestExecution 컨텍스트를 통해 JobParameter등을 단위 테스트에서 DI 받을 수 있다.
 */
public class JpaPagingItemReaderJobConfigurationTest {
	// JobLauncherTestUtils : Batch Job을 테스트 환경에서 실행할 Utils 클래스
	// 테스트 코드에서 Job을 실행할 수 있도록 지원한다.
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private ApplicationContext applicationContext;

	@BeforeEach
	public void beforeEach(){
		// @SpringBootTest(classes = {JpaPagingItemReaderJobConfiguration.class... }) 로 지정이 되지 않고 직접 setter를 호출해야 Job을 찾는다. (왜지?)
		Job jpaPagingItemReaderJob = (Job) applicationContext.getBean("jpaPagingItemReaderJob");
		jobLauncherTestUtils.setJob(jpaPagingItemReaderJob);
	}

	@Test
	public void JpaPagingItemReaderJob이_실행되는지_확인한다() throws Exception {
		// when
		JobParameters jobParameters = new JobParametersBuilder()
			.addString("version", "36")
			.toJobParameters();

		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);// 테스트 대상 batch job 실행 및 결과 반환

		// then
		assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED); // 배치 수행 결과 검증
	}
}
