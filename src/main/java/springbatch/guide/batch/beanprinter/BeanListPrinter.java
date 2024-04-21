package springbatch.guide.batch.beanprinter;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BeanListPrinter {
	private final ApplicationContext applicationContext;

	public void printBeanList() {
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			if(List.of("simpleJob", "simpleStep1", "tasklet").contains(beanName)){
				System.out.println(beanName);
			}
		}
	}


}