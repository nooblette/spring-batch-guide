package springbatch.guide.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleStepExecutionListener implements StepExecutionListener {
	@Override
	public void beforeStep(StepExecution stepExecution) {
		String stepName = stepExecution.getStepName();
		Long jobInstanceId = stepExecution.getJobExecution().getJobId();
		Long stepId = stepExecution.getId();
		log.info(">> {} start, jobInstanceId : {}, stepId : {}", stepName, jobInstanceId, stepId);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		String stepName = stepExecution.getStepName();
		Long jobInstanceId = stepExecution.getJobExecution().getJobId();
		Long stepId = stepExecution.getId();
		ExitStatus exitStatus = stepExecution.getExitStatus();
		log.info(">> {} end, jobInstanceId : {}, stepId : {}, exitStatus.getExitCode = {}", stepName, jobInstanceId, stepId, exitStatus.getExitCode());

		return stepExecution.getExitStatus();
	}
}
