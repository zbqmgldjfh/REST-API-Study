package shine.restapi.restapi.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shine.restapi.restapi.common.ErrorsResource;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@Validated @RequestBody EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        // binding은 성공

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            log.info("[Controller 거치는] ");
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);
        event.setId(newEvent.getId());
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId()); // 자신에 대한 link
        URI createdUri = selfLinkBuilder.toUri(); // self 링크를 string으로

        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events")); // 이벤트 등록하기 링크
        eventResource.add(selfLinkBuilder.withRel("update-events")); // 업데이트 링크
        eventResource.add(Link.of("/docs/index.html#resource-events-create").withRel("profile")); // 프로필 링크
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = eventRepository.findAll(pageable);
        var pageResources = assembler.toModel(page, e -> new EventResource(e));
        pageResources.add(Link.of("/docs/index.html#resource-events-list").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource resource = new EventResource(event);
        resource.add(Link.of("/docs/index.html#resource-events-get").withRel("profile"));
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @Validated @RequestBody EventDto eventDto,
                                      Errors errors) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            log.info("event가 비버있는 경우");
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            log.info("eventDto가 바인딩이 잘못 된 경우");
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            log.info("event의 로직이 잘못된 경우");
            return badRequest(errors); // 로직에 문제있는 경우
        }
        Event originEvent = optionalEvent.get();
        modelMapper.map(eventDto, originEvent);
        Event updatedEvent = eventRepository.save(originEvent);

        EventResource eventResource = new EventResource(updatedEvent);
        eventResource.add(Link.of("/docs/index.html#resource-events-update").withRel("profile"));
        return ResponseEntity.ok().body(eventResource);
    }


    private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
