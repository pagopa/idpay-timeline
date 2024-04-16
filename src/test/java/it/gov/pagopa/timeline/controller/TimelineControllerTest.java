package it.gov.pagopa.timeline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.common.web.dto.ErrorDTO;
import it.gov.pagopa.common.web.exception.ServiceException;
import it.gov.pagopa.timeline.configuration.ServiceExceptionConfig;
import it.gov.pagopa.timeline.configuration.TimelineErrorManagerConfig;
import it.gov.pagopa.timeline.constants.TimelineConstants;
import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.exception.custom.RefundsNotFoundException;
import it.gov.pagopa.timeline.exception.custom.TimelineDetailNotFoundException;
import it.gov.pagopa.timeline.exception.custom.UserNotFoundException;
import it.gov.pagopa.timeline.service.TimelineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(
    value = {TimelineController.class, ServiceExceptionConfig.class, TimelineErrorManagerConfig.class},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TimelineControllerTest {

  private static final String BASE_URL = "http://localhost:8080/idpay/timeline/";
  private static final String USER_ID = "TEST_USER_ID";
  private static final String INITIATIVE_ID = "TEST_INITIATIVE_ID";
  private static final String OPERATION_ID = "TEST_OPERATION_ID";
  private static final String OPERATION_TYPE = "PAID_REFUND";
  private static final String EVENT_ID = "EVENT_ID";
  private static final String INSTRUMENT_ID = "INSTRUMENT_ID";
  private static final String MASKED_PAN = "MASKED_PAN";
  private static final String BRAND_LOGO = "BAND_LOGO";
  private static final int PAGE = 0;
  private static final int PAGE_KO = -3;
  private static final int SIZE = 3;
  private static final int SIZE_KO = 11;
  private static final String STATUS = "COMPLETED_OK";
  private static final String REFUND_TYPE = "ORDINARY";
  private static final LocalDate START_DATE = LocalDate.now();
  private static final LocalDate END_DATE = LocalDate.now().plusDays(2);
  private static final LocalDate TRANSFER_DATE = LocalDate.now();
  private static final LocalDate NOTIFICATION_DATE = LocalDate.now();
  private static final String BUSINESS_NAME = "BUSINESS_NAME";
  private static final DetailOperationDTO DETAIL_OPERATION_DTO = DetailOperationDTO.builder()
          .build();

  private static final QueueOperationDTO PUT_OPERATION_DTO = new QueueOperationDTO(
          USER_ID, INITIATIVE_ID, OPERATION_TYPE, "", EVENT_ID, BRAND_LOGO, BRAND_LOGO, MASKED_PAN,
          INSTRUMENT_ID, "", "", "", "", "", null, null,
          0L, 0L, 0L, "", "", STATUS,
          REFUND_TYPE, START_DATE, END_DATE, TRANSFER_DATE, NOTIFICATION_DATE, BUSINESS_NAME);
  private static final QueueOperationDTO PUT_OPERATION_DTO_EMPTY = new QueueOperationDTO(
          "", INITIATIVE_ID, OPERATION_TYPE, "", EVENT_ID, BRAND_LOGO, BRAND_LOGO, MASKED_PAN,
          INSTRUMENT_ID, "", "", "", "", "", null, null,
          0L, 0L, 0L, "", "", STATUS,
          REFUND_TYPE, START_DATE, END_DATE, TRANSFER_DATE, NOTIFICATION_DATE, BUSINESS_NAME);



  @MockBean
  TimelineService timelineServiceMock;

  @Autowired
  protected MockMvc mvc;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  void getTimelineDetail_ok() throws Exception {

    Mockito.when(timelineServiceMock.getTimelineDetail(INITIATIVE_ID, OPERATION_ID, USER_ID))
            .thenReturn(DETAIL_OPERATION_DTO);

    mvc.perform(
                    MockMvcRequestBuilders.get(BASE_URL + INITIATIVE_ID + "/" + OPERATION_ID + "/" + USER_ID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
  }

  @Test
  void getTimelineDetail_not_found() throws Exception {

    Mockito.doThrow(
                    new TimelineDetailNotFoundException("Cannot find the detail of timeline on initiative [%s]".formatted(INITIATIVE_ID)))
            .when(timelineServiceMock)
            .getTimelineDetail(INITIATIVE_ID, OPERATION_ID, USER_ID);

    MvcResult res =
            mvc.perform(
                            MockMvcRequestBuilders.get(
                                            BASE_URL + INITIATIVE_ID + "/" + OPERATION_ID + "/" + USER_ID)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();

    ErrorDTO error = objectMapper.readValue(res.getResponse().getContentAsString(), ErrorDTO.class);

    assertEquals(TimelineConstants.ExceptionCode.TIMELINE_DETAIL_NOT_FOUND, error.getCode());
    assertEquals("Cannot find the detail of timeline on initiative [%s]".formatted(INITIATIVE_ID), error.getMessage());
  }

  @Test
  void addOperation_ok() throws Exception {

    Mockito.doNothing().when(timelineServiceMock).sendToQueue(PUT_OPERATION_DTO);

    mvc.perform(
                    MockMvcRequestBuilders.put(BASE_URL)
                            .content(objectMapper.writeValueAsString(PUT_OPERATION_DTO))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
  }

  @Test
  void addOperation_ko_empty_fields() throws Exception {

    MvcResult res = mvc.perform(
                    MockMvcRequestBuilders.put(BASE_URL)
                            .content(objectMapper.writeValueAsString(PUT_OPERATION_DTO_EMPTY))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();

    ErrorDTO error = objectMapper.readValue(res.getResponse().getContentAsString(), ErrorDTO.class);

    assertEquals(TimelineConstants.ExceptionCode.TIMELINE_INVALID_REQUEST, error.getCode());
    assertTrue(error.getMessage().contains(TimelineConstants.ERROR_MANDATORY_FIELD));
  }

  @Test
  void getTimeline_ok() throws Exception {

    mvc.perform(
                    MockMvcRequestBuilders.get(BASE_URL + INITIATIVE_ID + "/" + USER_ID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .param("operationType", OPERATION_TYPE)
                            .param("page", String.valueOf(PAGE))
                            .param("size", String.valueOf(SIZE))
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
  }

  @Test
  void getTimeline_not_found() throws Exception {

    Mockito.doThrow(
                    new UserNotFoundException("Timeline for the current user and initiative [%s] was not found".formatted(INITIATIVE_ID)))
            .when(timelineServiceMock)
            .getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, PAGE, SIZE, null, null);

    MvcResult res =
            mvc.perform(
                            MockMvcRequestBuilders.get(
                                            BASE_URL + INITIATIVE_ID + "/" + USER_ID)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .param("operationType", OPERATION_TYPE)
                                    .param("page", String.valueOf(PAGE))
                                    .param("size", String.valueOf(SIZE))
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();

    ErrorDTO error = objectMapper.readValue(res.getResponse().getContentAsString(), ErrorDTO.class);

    assertEquals(TimelineConstants.ExceptionCode.TIMELINE_USER_NOT_FOUND, error.getCode());
    assertEquals("Timeline for the current user and initiative [%s] was not found".formatted(INITIATIVE_ID), error.getMessage());
  }

  @Test
  void getTimeline_ko_max_size() throws Exception {

    MvcResult res =
            mvc.perform(
                            MockMvcRequestBuilders.get(
                                            BASE_URL + INITIATIVE_ID + "/" + USER_ID)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .param("operationType", OPERATION_TYPE)
                                    .param("page", String.valueOf(PAGE))
                                    .param("size", String.valueOf(SIZE_KO))
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn();

    ErrorDTO error = objectMapper.readValue(res.getResponse().getContentAsString(), ErrorDTO.class);

    assertEquals(TimelineConstants.ExceptionCode.TIMELINE_INVALID_REQUEST, error.getCode());
    assertTrue(error.getMessage().equals("Parameter [size] must be less than or equal to 10"));
  }

  @Test
  void getTimeline_ko_min_page() throws Exception {

    MvcResult res =
        mvc.perform(
                MockMvcRequestBuilders.get(
                        BASE_URL + INITIATIVE_ID + "/" + USER_ID)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("operationType", OPERATION_TYPE)
                    .param("page", String.valueOf(PAGE_KO))
                    .param("size", String.valueOf(SIZE))
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();

    ErrorDTO error = objectMapper.readValue(res.getResponse().getContentAsString(), ErrorDTO.class);

    assertEquals(TimelineConstants.ExceptionCode.TIMELINE_INVALID_REQUEST, error.getCode());
    assertTrue(error.getMessage().equals("Parameter [page] must be more than or equal to 0"));
  }

  @Test
  void getRefunds_ok() throws Exception {

    mvc.perform(
                    MockMvcRequestBuilders.get(BASE_URL + INITIATIVE_ID + "/" + USER_ID + "/refunds")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
  }

  @Test
  void getRefunds_not_found() throws Exception {

    Mockito.doThrow(
                    new RefundsNotFoundException("No refunds have been rewarded for the current user and initiative [%s]".formatted(INITIATIVE_ID)))
            .when(timelineServiceMock)
            .getRefunds(INITIATIVE_ID, USER_ID);

    MvcResult res =
            mvc.perform(
                            MockMvcRequestBuilders.get(
                                            BASE_URL + INITIATIVE_ID + "/" + USER_ID + "/refunds")
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn();

    ErrorDTO error = objectMapper.readValue(res.getResponse().getContentAsString(), ErrorDTO.class);

    assertEquals(TimelineConstants.ExceptionCode.TIMELINE_REFUNDS_NOT_FOUND, error.getCode());
    assertEquals("No refunds have been rewarded for the current user and initiative [%s]".formatted(INITIATIVE_ID), error.getMessage());
  }

  @Test
  void getRefunds_genericServiceException() throws Exception {
    //given
    doThrow(new ServiceException("DUMMY_EXCEPTION_CODE", "DUMMY_EXCEPTION_MESSAGE", null))
            .when(timelineServiceMock)
            .getRefunds(INITIATIVE_ID, USER_ID);

    // when
    MvcResult res = mvc.perform(MockMvcRequestBuilders.get(
                            BASE_URL + INITIATIVE_ID + "/" + USER_ID + "/refunds")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andReturn();

    //then
    ErrorDTO error = objectMapper.readValue(res.getResponse().getContentAsString(), ErrorDTO.class);
    assertEquals("DUMMY_EXCEPTION_CODE", error.getCode());
    assertEquals("DUMMY_EXCEPTION_MESSAGE", error.getMessage());

  }
}
