package coco.ide.ideapp.exception;

public class InvalidCreationFormException extends RuntimeException {
    public InvalidCreationFormException() {
        super("잘못된 양식입니다.");
    }
}
