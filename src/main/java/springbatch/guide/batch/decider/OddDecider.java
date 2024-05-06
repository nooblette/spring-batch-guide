package springbatch.guide.batch.decider;

import java.util.Random;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OddDecider implements JobExecutionDecider {
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		Random random = new Random();

		int randomNumber = random.nextInt(50) + 1;
		log.info("랜덤숫자 : {}", randomNumber);

		if(randomNumber % 2 == 0){
			return new FlowExecutionStatus("EVEN");
		} else {
			return new FlowExecutionStatus("ODD");

		}
	}
}
