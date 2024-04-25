package springbatch.guide.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExecutionContextTasklet4 implements Tasklet {
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Object name = chunkContext.getStepContext()
			.getStepExecution()
			.getJobExecution()
			.getExecutionContext()
			.get("name");

		log.info("ExecutionContextTasklet4 name: {}", name);
		return RepeatStatus.FINISHED;
	}
}
