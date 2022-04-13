package shine.restapi.restapi.events;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    @DisplayName("기본 가격이 없는 경우 무료다.")
    public void isFreeTest() {
        // given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isTrue();
    }

    @Test
    @DisplayName("기본 가격이 있는 경우 무료가 아니다.")
    public void isBasePriceNotZeroTest() {
        // given
        Event event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    @DisplayName("Max가 있는 경우 무료가 아니다.")
    public void isMaxPriceNotZeroTest() {
        // given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isFalse();
    }

    @ParameterizedTest
    @CsvSource({"0,0,true", "100,0,false", "0,100,false"})
    public void isMaxPriceNotZeroTest(int basePrice, int maxPrice, boolean isFree) {
        // given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    @Test
    @DisplayName("장소가 있으면 오프라인이 맞다")
    public void isOfflineTest() {
        // given
        Event event = Event.builder()
                .location("강남역 D2 스타텁 팩토리")
                .build();

        // when
        event.update();

        // then
        assertThat(event.isOffline()).isTrue();
    }

    @Test
    @DisplayName("장소가 없으면 온라인이 맞다")
    public void isOnlineTest() {
        // given
        Event event = Event.builder()
                .build();

        // when
        event.update();

        // then
        assertThat(event.isOffline()).isFalse();
    }

    @ParameterizedTest
    @CsvSource({"강남,true", ",false", "   ,false"})
    public void isOnlineTest(String location, boolean isOffline) {
        // given
        Event event = Event.builder()
                .location(location)
                .build();

        // when
        event.update();

        // then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }
}
