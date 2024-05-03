package springbatch.guide.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RepeatTasklet implements Tasklet {
	private static final int MAX = 3;
	private int iteration = 0; // 반복횟수 제어

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		if(iteration < MAX){
			log.info(">> iteration : {}, RepeatStatus.CONTINUABLE 반환", iteration);
			iteration++;
			return RepeatStatus.CONTINUABLE;
		}

		log.info(">> iteration : {}, RepeatStatus.FINISHED 반환", iteration);
		return RepeatStatus.FINISHED;
	}
}
