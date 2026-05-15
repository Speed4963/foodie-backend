import com.eatproject.backend.notification.event.ActionEvent;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ThreadEventListener {

    private final NotificationService service;

    @EventListener(condition =
            "#event.type == T(com.eatproject.backend.notification.enums.EventType).THREAD_LOCKED")
    public void handle(ActionEvent event) {

        service.create(
                event.getTargetEmail(),
                event.getType().name(),
                event.getPostId(),
                event.getBoardId(),
                "스레드가 잠금되었습니다."
        );
    }
}