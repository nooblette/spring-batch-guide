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
public class ExecutionContextTasklet1 implements Tasklet {
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

		String jobName = chunkContext.getStepContext()
			.getStepExecution()
			.getJobExecution()
			.getJobInstance()
			.getJobName();

		String stepName = chunkContext.getStepContext()
			.getStepExecution()
			.getStepName();

		log.info("ExecutionContextTasklet1 JobName: {}", jobExecutionContext.get("jobName"));
		log.info("ExecutionContextTasklet1 StepName: {}", stepExecutionContext.get("stepName"));

		// Job ExecutionContext에 데이터가 존재하지 않는 경우 저장
		if(jobExecutionContext.get("jobName") == null){
			jobExecutionContext.put("jobName", jobName);
		}

		// Step ExecutionContext에 데이터가 존재하지 않는 경우 저장
		if (stepExecutionContext.get("stepName") == null) {
			stepExecutionContext.put("stepName", stepName);
		}

		log.info("ExecutionContextTasklet1 JobName: {}", jobExecutionContext.get("jobName"));
		log.info("ExecutionContextTasklet1 StepName: {}", stepExecutionContext.get("stepName"));

		return RepeatStatus.FINISHED;
	}
}
