package springbatch.guide.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import springbatch.guide.batch.dto.User;

@Component
public class SimpleProcessor implements ItemProcessor<User, String> {
	@Override
	public String process(User item) {
		if(item.getUserId() % 2 != 0){
			return "홀수입니다. user id : " + item.getUserId() + ", name : " + item.getName();
		}

		return "짝수입니다. user id : " + item.getUserId() + ", name : " + item.getName();
	}
}
