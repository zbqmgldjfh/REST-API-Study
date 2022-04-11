package shine.restapi.restapi.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("REST API Study")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        Event event = new Event();
        event.setName("Spring REST API");
        event.setDescription("Spring");

        assertThat(event.getName()).isEqualTo("Spring REST API");
        assertThat(event.getDescription()).isEqualTo("Spring");
    }
}
