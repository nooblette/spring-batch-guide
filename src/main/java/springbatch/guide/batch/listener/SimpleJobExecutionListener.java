package springbatch.guide.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobScope
public class SimpleJobExecutionListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		Long jobExecutionId = jobExecution.getId();
		String jobName = jobExecution.getJobInstance().getJobName();
		long jobInstanceId = jobExecution.getJobInstance().getInstanceId();

		log.info(">> {} 실행, jobExecutionId: {}, jobInstanceId: {}", jobName, jobExecutionId, jobInstanceId);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();
		BatchStatus status = jobExecution.getStatus();
		ExitStatus exitStatus = jobExecution.getExitStatus();

		log.info(">> {} 종료, BatchStatus: {}, ExitStatus: {}", jobName, status.name(), exitStatus.getExitCode());
	}
}
