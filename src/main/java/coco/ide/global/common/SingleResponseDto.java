package coco.ide.global.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SingleResponseDto<T> {
    private T data;

    public SingleResponseDto(T data) {
        this.data = data;
    }

}
