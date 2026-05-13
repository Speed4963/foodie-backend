import com.eatproject.backend.notification.event.ActionEvent;
import com.eatproject.backend.notification.service.NotificationAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventListener {

    private final NotificationAggregationService aggregationService;

    @EventListener(condition =
            "#event.type == T(com.eatproject.backend.notification.enums.EventType).LIKE")
    public void handle(ActionEvent event) {

        aggregationService.aggregate(
                event.getTargetEmail(),
                "LIKE",
                event.getPostId()
        );
    }
}