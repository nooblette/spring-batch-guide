package springbatch.guide.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExecutionContextTasklet3 implements Tasklet {
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Object name = chunkContext.getStepContext()
			.getStepExecution()
			.getJobExecution()
			.getExecutionContext()
			.get("name");

		// JobExecutionContext에 name을 key로 갖는 공유데이터는 존재하지 않을 경우
		if(name == null){
			// name을 key로 갖는 공유데이터를 JobExecutionContext에 넣어준다(key = name, value = user1)
			chunkContext.getStepContext()
				.getStepExecution()
				.getJobExecution()
				.getExecutionContext()
				.put("name", "user1");

			throw  new RuntimeException("step has failed"); // 이후 Step 실행 x
		}

		return RepeatStatus.FINISHED;
	}
}
