package coco.ide;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsController {

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public void handleOptions() {
        // CORS 설정은 스프링이 자동으로 설정하므로, 여기서는 빈 메소드만 필요합니다.
    }
}