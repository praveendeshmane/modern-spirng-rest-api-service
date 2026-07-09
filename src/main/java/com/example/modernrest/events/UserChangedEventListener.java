package com.example.modernrest.events;

import com.example.modernrest.starter.audit.AuditTrailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserChangedEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserChangedEventListener.class);
    private final AuditTrailService auditTrailService;

    public UserChangedEventListener(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserChanged(UserChangedEvent event) {
        String message = "User event action=" + event.action() + " userId=" + event.userId() + " username=" + event.username();
        log.info(message);
        auditTrailService.record(message);
    }
}
