package springbatch.guide.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class User {
	private Long userId;
	private String name;

	@Override
	public String toString() {
		return "User{" +
			"userId=" + userId +
			", name='" + name + '\'' +
			'}';
	}
}
