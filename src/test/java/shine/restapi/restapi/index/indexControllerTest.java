package shine.restapi.restapi.index;

import org.junit.jupiter.api.Test;
import shine.restapi.restapi.common.BaseControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class indexControllerTest extends BaseControllerTest {

    @Test
    public void indexTest() throws Exception {
        // given
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists());

        // when

        // then
    }
}
