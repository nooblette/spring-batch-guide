package springbatch.guide.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleChunkListener implements ChunkListener {
	@Override
	public void beforeChunk(ChunkContext context) {
		String stepName = context.getStepContext().getStepName();
		String stepExecutionId = context.getStepContext().getId();
		log.info(">> beforeChunk stepName:{}, stepExecutionId:{}", stepName, stepExecutionId);
	}

	@Override
	public void afterChunk(ChunkContext context) {
		String stepName = context.getStepContext().getStepName();
		String stepExecutionId = context.getStepContext().getId();
		String exitStatus = context.getStepContext().getStepExecution().getExitStatus().getExitCode();
		log.info(">> afterChunk stepName:{}, stepExecutionId:{}, exitStatus: {}", stepName, stepExecutionId, exitStatus);
	}

	@Override
	public void afterChunkError(ChunkContext context) {
		String stepExecutionId = context.getStepContext().getId();
		String exitStatus = context.getStepContext().getStepExecution().getExitStatus().getExitCode();
		log.info(">> afterChunkError stepExecutionId:{}, exitStatus: {}", stepExecutionId, exitStatus);
	}
}
