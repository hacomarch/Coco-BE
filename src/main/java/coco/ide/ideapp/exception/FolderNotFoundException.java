package coco.ide.ideapp.exception;

public class FolderNotFoundException extends RuntimeException{
    public FolderNotFoundException() {
        super("폴더를 찾을 수 없습니다.");
    }
}
