package springbatch.guide.batch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleChunkDto {
	private String name;

	public SimpleChunkDto(){
		System.out.println("create SimpleChunkDto.SimpleChunkDto");
	}
}
