package coco.ide.ideapp.exception;

public class DuplicateNameException extends RuntimeException{
    public DuplicateNameException() {
        super("이름이 중복됩니다.");
    }
}
