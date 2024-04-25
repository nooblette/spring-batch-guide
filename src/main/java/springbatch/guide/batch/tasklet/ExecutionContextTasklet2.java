package springbatch.guide.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExecutionContextTasklet2 implements Tasklet {
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// jobExecutionContext
		ExecutionContext jobExecutionContext = chunkContext.getStepContext()
			.getStepExecution()
			.getJobExecution()
			.getExecutionContext();

		// stepExecutionContext
		ExecutionContext stepExecutionContext = chunkContext.getStepContext()
			.getStepExecution()
			.getExecutionContext();

		// ExecutionContextTasklet1에서 ExecutionContext 공간에 저장한 데이터 조회
		// Job 내의 Step들은 JobExecutionContext 데이터 공유 가능
		log.info("ExecutionContextTasklet2 JobName: {}", jobExecutionContext.get("jobName"));
		log.info("ExecutionContextTasklet2 StepName: {}", stepExecutionContext.get("stepName"));

		return RepeatStatus.FINISHED;
	}
}
