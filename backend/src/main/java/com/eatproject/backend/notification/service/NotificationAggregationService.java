package com.eatproject.backend.notification.service;

import com.eatproject.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationAggregationService {

    private final NotificationRepository repository;
    private final NotificationService notificationService;

    private static final List<Integer> THRESHOLDS =
            List.of(10, 50, 100, 500, 1000);

    public void aggregate(String email, String type, Long postId) {

        int count = repository.countByTargetEmailAndTypeAndRefPostId(email, type, postId);
        int next = count + 1;

        for (int t : THRESHOLDS) {
            if (next == t) {
                notificationService.create(
                        email,
                        type,
                        postId,
                        null,
                        type + " " + t + "개 발생"
                );
                break;
            }
        }
    }
}