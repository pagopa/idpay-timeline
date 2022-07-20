package it.gov.pagopa.timeline.controller;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.timeline.constants.TimelineConstants;
import it.gov.pagopa.timeline.dto.DetailOperationDTO;
import it.gov.pagopa.timeline.dto.ErrorDTO;
import it.gov.pagopa.timeline.dto.QueueOperationDTO;
import it.gov.pagopa.timeline.dto.TimelineDTO;
import it.gov.pagopa.timeline.exception.TimelineException;
import it.gov.pagopa.timeline.service.TimelineService;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(
    value = {TimelineController.class},
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TimelineControllerTest {

  private static final String BASE_URL = "http://localhost:8080/idpay/timeline/";
  private static final String USER_ID = "TEST_USER_ID";
  private static final String INITIATIVE_ID = "TEST_INITIATIVE_ID";
  private static final String OPERATION_ID = "TEST_OPERATION_ID";
  private static final String OPERATION_TYPE = "PAID_REFUND";
  private static final int PAGE = 0;
  private static final int SIZE = 3;
  private static final int SIZE_KO = 11;
  private static final DetailOperationDTO DETAIL_OPERATION_DTO = new DetailOperationDTO();

  private static final QueueOperationDTO PUT_OPERATION_DTO = new QueueOperationDTO(
      USER_ID, INITIATIVE_ID, OPERATION_TYPE, "", "", "", "", "", "", "", "", "");
  private static final QueueOperationDTO PUT_OPERATION_DTO_EMPTY = new QueueOperationDTO(
      "", INITIATIVE_ID, OPERATION_TYPE, "", "", "", "", "", "", "", "", "");

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
            new TimelineException(HttpStatus.NOT_FOUND.value(), "Cannot find the requested operation!"))
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

    assertEquals(HttpStatus.NOT_FOUND.value(), error.getCode());
    assertEquals("Cannot find the requested operation!", error.getMessage());
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

    assertEquals(HttpStatus.BAD_REQUEST.value(), error.getCode());
    assertTrue(error.getMessage().contains(TimelineConstants.ERROR_MANDATORY_FIELD));
  }

  @Test
  void getTimeline_ok() throws Exception {

    Mockito.when(
            timelineServiceMock.getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, PAGE, SIZE))
        .thenReturn(new TimelineDTO("", new ArrayList<>()));

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
            new TimelineException(HttpStatus.NOT_FOUND.value(),
                "No operations have been made on this initiative!"))
        .when(timelineServiceMock)
        .getTimeline(INITIATIVE_ID, USER_ID, OPERATION_TYPE, PAGE, SIZE);

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

    assertEquals(HttpStatus.NOT_FOUND.value(), error.getCode());
    assertEquals("No operations have been made on this initiative!", error.getMessage());
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

    assertEquals(HttpStatus.BAD_REQUEST.value(), error.getCode());
    assertTrue(error.getMessage().equals("Parameter [size] must be less than or equal to 10"));
  }
}
