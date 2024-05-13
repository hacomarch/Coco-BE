package coco.ide.ideapp.files.requestdto;

import lombok.Data;

@Data
public class CommandRequest {
    private String command;
    private String sessionId;
}