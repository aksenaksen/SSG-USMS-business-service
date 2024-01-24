package com.ssg.usms.business.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.usms.business.security.login.persistence.RequestLoginDto;
import com.ssg.usms.business.security.login.persistence.ResponseLoginDto;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UsmsUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class LoginIntegrationTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password(encoder.encode("hashedpassword123@"))
                .personName("TestUser")
                .phoneNumber("010-1423-4151")
                .email("test@email.com")
                .build();

        repository.signUp(user);


        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }


    @DisplayName("인증이 완료된 유저로 로그인 시도할 경우 200 상태코드를 리턴한다.")
    @Test
    public void testLoginWithAuthorizedUserInfo() throws Exception {

        log.info(repository.findByUsername("httpRequestSign").toString());

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setUsername("httpRequestSign");
        requestBody.setPassword("hashedpassword123@");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.OK.value());
                });

    }


    @DisplayName("존재하지 않는 이메일로 로그인 시도할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testLoginWithNotExistingEmail() throws Exception {

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setUsername("httpRequestSignasdfaa");
        requestBody.setPassword("hashedpassword123@");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

    }

    @DisplayName("비밀번호가 부정확한 요청으로 로그인 시도할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testLoginWithInvalidPassword() throws Exception {

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setUsername("httpRequestSign");
        requestBody.setPassword("hashedpassword123@123");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

    }








}