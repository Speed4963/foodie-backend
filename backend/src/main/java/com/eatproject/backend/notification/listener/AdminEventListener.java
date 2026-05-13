import com.eatproject.backend.notification.event.ActionEvent;
import com.eatproject.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminEventListener {

    private final NotificationService service;

    @EventListener(condition =
            "#event.type == T(com.eatproject.backend.notification.enums.EventType).ACCOUNT_BANNED || " +
                    "#event.type == T(com.eatproject.backend.notification.enums.EventType).BOARD_APPROVED")
    public void handle(ActionEvent event) {

        service.create(
                event.getTargetEmail(),
                event.getType().name(),
                event.getPostId(),
                event.getBoardId(),
                event.getMessage()
        );
    }
}