package springbatch.guide.batch.job;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope // jobParameters를 사용하려면 Tasklet Bean의 Scope는 StepScope(Step 실행 시 Bean 생성)여야한다.
public class SimpleJobTasklet implements Tasklet {

	@Value("#{jobParameters[requestDate]}")
	private String requestDate;

	public SimpleJobTasklet() {
		log.info(">> SimpleJobTasklet 생성");
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info(">> this is SimpleJobTasklet");
		log.info(">> requestDate: {}", requestDate);
		return RepeatStatus.FINISHED;
	}

	public void test(){ // ScopeNotActiveException 확인
		System.out.println("SimpleJobTasklet.test");
	}
}
