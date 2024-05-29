package coco.ide.ideapp.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
        super("유저를 찾을 수 없습니다.");
    }
}
