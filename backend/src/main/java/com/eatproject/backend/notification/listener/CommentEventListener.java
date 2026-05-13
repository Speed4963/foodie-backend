import com.eatproject.backend.notification.event.ActionEvent;
import com.eatproject.backend.notification.service.NotificationAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventListener {

    private final NotificationAggregationService aggregationService;

    @EventListener(condition =
            "#event.type == T(com.eatproject.backend.notification.enums.EventType).COMMENT || " +
                    "#event.type == T(com.eatproject.backend.notification.enums.EventType).REPLY || " +
                    "#event.type == T(com.eatproject.backend.notification.enums.EventType).QUOTE")
    public void handle(ActionEvent event) {

        aggregationService.aggregate(
                event.getTargetEmail(),
                "COMMENT",
                event.getPostId()
        );
    }
}