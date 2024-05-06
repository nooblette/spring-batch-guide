package springbatch.guide.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ChildStep1Tasklet implements Tasklet {
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info(">> this is childStep1 tasklet");

		StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
		String name = stepExecution.getJobExecution().getJobParameters().getString("name");
		log.info(">> job parameter from parentStep2 in childStep: " + name);

		String run = stepExecution.getJobExecution().getJobParameters().getString("run");
		log.info(">> job parameter from parentStep2 in childStep: " + run);

		Thread.sleep(10000);
		return RepeatStatus.FINISHED;
	}
}
