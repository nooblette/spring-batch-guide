package springbatch.guide.batch.incrementer;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.stereotype.Component;

/**
 * JobParametersIncrementer 인터페이스
 * - Spring Batch Job은 기본적으로 Job Class Name과 Job Parameter의 조합을 바탕으로 하나의 Job 인스턴스를 식별
 * - 따라서 Job Class Name만으로는 (수행에 성공한) 배치 Job을 여러번 수행할 수 없다. (JobInstanceAlreadyCompleteException 예외 발생)
 * - 이 때, JobParameter를 직접 변경하지 않고 동일한 Job 클래스를 여러 번 수행하기 위해 JobParametersIncrementer 인터페이스를 직접 구현하여 사용할 수 있다.
 * */
@Component
public class CustomJobIncrementer implements JobParametersIncrementer {
	@Override
	// getNext() : JobParmeters에서 필요한 값을 증가시켜 다음 job을 실행할 때 Job을 식별하기 위해 사용할 JobParameters 오브젝트를 반환
	public JobParameters getNext(JobParameters parameters) {
		String now = LocalDateTime.now(Clock.systemDefaultZone()).toString();
		return new JobParametersBuilder()
			.addString("run.id", now)
			.toJobParameters();
	}
}
