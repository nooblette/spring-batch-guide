package springbatch.guide.batch.execption;

public class SimpleSkipException extends Exception {
	public SimpleSkipException(String message) {
		super(message);
	}
}
