package springbatch.guide.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import springbatch.guide.batch.dto.User;
import springbatch.guide.batch.execption.SimpleSkipException;

@Component
public class SimpleProcessor implements ItemProcessor<User, String> {
	@Override
	public String process(User item) throws SimpleSkipException {
		// Skip이 발생하더라도 Reader의 쿼리가 다시 동작하지않고, 기존에 조회해온 내역을 대상으로 첫 원소부터 다시 processing을 한다(read listener로 로그를 확인해본다)
		if(item.getUserId() % 2 != 0){
			throw new SimpleSkipException("홀수입니다. user id : " + item.getUserId() + ", name : " + item.getName());
			// return "홀수입니다. user id : " + item.getUserId() + ", name : " + item.getName();
		}

		return "짝수입니다. user id : " + item.getUserId() + ", name : " + item.getName();
	}
}
