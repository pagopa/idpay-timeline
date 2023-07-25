package it.gov.pagopa.timeline.utils;

import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class AuditUtilitiesTest {

    private static final Long DELETE_COUNTERS = 1L;
    private static final String INITIATIVE_ID = "TEST_INITIATIVE_ID";


    private MemoryAppender memoryAppender;

    private final AuditUtilities auditUtilities = new AuditUtilities();

    @BeforeEach
    public void setup() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("AUDIT");
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(ch.qos.logback.classic.Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }


    @Test
    void logCreateWallet_ok(){
        auditUtilities.logDeleteOperation(DELETE_COUNTERS, INITIATIVE_ID);

        Assertions.assertEquals(
                ("CEF:0|PagoPa|IDPAY|1.0|7|User interaction|2| event=Timeline dstip=%s msg=Deleted %s operations" +
                        " cs1Label=initiativeId cs1=%s")
                        .formatted(
                                AuditUtilities.SRCIP,
                                DELETE_COUNTERS,
                                INITIATIVE_ID
                        ),
                memoryAppender.getLoggedEvents().get(0).getFormattedMessage()
        );
    }
}
