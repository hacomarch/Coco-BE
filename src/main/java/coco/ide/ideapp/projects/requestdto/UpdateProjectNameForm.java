package coco.ide.ideapp.projects.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UpdateProjectNameForm {
    @JsonProperty("newName")
    private final String  newName = null;
}
