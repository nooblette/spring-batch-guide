package springbatch.guide.batch.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Component
public class ParameterDecider implements JobExecutionDecider {
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		String exitStatus = (String) jobExecution.getExecutionContext().get("run");
		if("true".equals(exitStatus)){
			return new FlowExecutionStatus("LAUNCH");
		}

		return new FlowExecutionStatus("NONE");
	}
}
