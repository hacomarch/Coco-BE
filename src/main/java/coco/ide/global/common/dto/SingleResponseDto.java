package coco.ide.global.common.dto;

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
