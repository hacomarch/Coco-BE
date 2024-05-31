package coco.ide.ideapp.exception;

public class FolderMoveException extends RuntimeException{
    public FolderMoveException() {
        super("폴더를 이동할 수 없습니다.");
    }
}
