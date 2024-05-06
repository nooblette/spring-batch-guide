package springbatch.guide.batch.tasklet;

import javax.sql.DataSource;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ParentStep1Tasklet implements Tasklet {
	private final DataSource dataSource;

	@Value("#{jobParameters[exitStatus]}")
	private String exitStatusParameter;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info(">> this is parentStep1Tasklet tasklet");
		log.info(">> exitStatusParameter is {}", exitStatusParameter);

		if(isRunChdilJob(exitStatusParameter)){
			log.info(">> put executionContext");
			ExecutionContext executionContext = chunkContext.getStepContext()
				.getStepExecution()
				.getJobExecution()
				.getExecutionContext();
			executionContext.put("run", exitStatusParameter);
		}

		return RepeatStatus.FINISHED;
	}

	private boolean isRunChdilJob(String runYn){
		return "true".equals(runYn);
	}
}
