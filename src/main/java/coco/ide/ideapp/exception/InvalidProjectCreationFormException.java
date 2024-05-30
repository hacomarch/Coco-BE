package coco.ide.ideapp.exception;

public class InvalidProjectCreationFormException extends RuntimeException {
    public InvalidProjectCreationFormException() {
        super("잘못된 프로젝트 생성 양식입니다.");
    }
}
