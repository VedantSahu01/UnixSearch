package service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.vedant.repository.UnixStackRepository;
import org.vedant.repository.UnixWikiRepository;
import org.vedant.service.DataIndexingService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataIndexingServiceTest {

    @Mock
    private UnixStackRepository unixStackRepository;

    @Mock
    private UnixWikiRepository unixWikiRepository;

    @InjectMocks
    private DataIndexingService dataIndexingService;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(dataIndexingService, "destinationDirectory", "/downloads/");
        ReflectionTestUtils.setField(dataIndexingService, "destinationStaxFile", "unix_stackexchange.xml");
        ReflectionTestUtils.setField(dataIndexingService, "destinationWikiFile", "unix_wiki.xml");
        CronJobTest cronJobTest = new CronJobTest();
        cronJobTest.testCheckStackExchangeSuccess();
        cronJobTest.testCheckWikipediaSuccess();
    }

    @Test
    void testUpdateStackData(){

        // Test
        dataIndexingService.updateStackData();

        // Verification
        verify(unixStackRepository, times(0)).saveAll(anyList());
    }

    @Test
    void testUpdateWikiData(){

        // Test
        dataIndexingService.updateWikiData();

        // Verification
        verify(unixWikiRepository, times(0)).saveAll(anyList());
    }
}