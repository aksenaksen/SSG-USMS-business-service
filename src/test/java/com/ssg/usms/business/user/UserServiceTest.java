package com.ssg.usms.business.user;


import com.ssg.usms.business.user.dto.HttpRequestModifyUserDto;
import com.ssg.usms.business.user.dto.HttpResponseUserDto;
import com.ssg.usms.business.user.dto.SecurityState;
import com.ssg.usms.business.user.exception.NotExistingUserException;
import com.ssg.usms.business.user.repository.UserRepository;
import com.ssg.usms.business.user.repository.UsmsUser;
import com.ssg.usms.business.user.service.UserService;
import com.ssg.usms.business.user.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository repository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private UserService userService;

    @DisplayName("인자로 들어온 token값으로 성공적으로 유저를 찾아서 HttpResponseDto를 리턴")
    @Test
    public void SuccessFindUserByValueCode0(){

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        Claims fakeClaims = Jwts.claims()
                .add("code", "0")
                .add("value", "asdf123@naer.com")
                .build();

        given(jwtUtil.getClaim(any())).willReturn(fakeClaims);

        given(repository.findByEmail(any())).willReturn(user);

        Assertions.assertThat(userService.findUserByValue("").toString()).isEqualTo(dto.toString());
    }

    @DisplayName("인자로 들어온 token값으로 user를 찾을수 없는 경우")
    @Test
    public void FailedFindUserByCode0(){

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        Claims fakeClaims = Jwts.claims()
                .add("code", "0")
                .add("value", "asdf123@naer.com")
                .build();

        given(jwtUtil.getClaim(any())).willReturn(fakeClaims);

        given(repository.findByEmail(any())).willReturn(null);

        assertThrows(NotExistingUserException.class ,() -> userService.findUserByValue("").toString());
    }



    @DisplayName("인자로 들어온 token값으로 성공적으로 유저를 찾아서 HttpResponseDto를 리턴")
    @Test
    public void SuccessFindUserByValue(){

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        Claims fakeClaims = Jwts.claims()
                .add("code", "1")
                .add("value", "010-1234-5124")
                .build();

        given(jwtUtil.getClaim(any())).willReturn(fakeClaims);

        given(repository.findByPhoneNumber(any())).willReturn(user);

        Assertions.assertThat(userService.findUserByValue("").toString()).isEqualTo(dto.toString());
    }

    @DisplayName("인자로 들어온 token값으로 user를 찾을수 없는 경우")
    @Test
    public void FailedFindUserByCode1(){

        UsmsUser user = UsmsUser.builder()
                .username("httpRequestSign")
                .password("hashedpassword123@")
                .personName("hihello")
                .phoneNumber("010-1234-24124")
                .email("asdf123@naer.com")
                .id(1L)
                .build();

        HttpResponseUserDto dto = HttpResponseUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .personName(user.getPersonName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .securityState(user.getSecurityState())
                .build();

        Claims fakeClaims = Jwts.claims()
                .add("code", "1")
                .add("value", "010-1234-5124")
                .build();

        given(jwtUtil.getClaim(any())).willReturn(fakeClaims);

        given(repository.findByPhoneNumber(any())).willReturn(null);

        assertThrows(NotExistingUserException.class ,() -> userService.findUserByValue("").toString());
    }


    @DisplayName("성공적으로 수정기능이 작동한 경우 UsmsUser을 리턴한다.")
    @Test
    public void SuccessModifyUser(){
        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("hihello")
                .email("asdf123@naer.com")
                .phoneNumber("010-1234-2412")
                .password("hashedpassword123@")
                .securityState(SecurityState.BASIC)
                .build();

        UsmsUser User = new UsmsUser();
        User.setId(1L);
        User.setPersonName("tmpName");
        User.setEmail("tmp123@naver.com");
        User.setPhoneNumber("010-1234-4242");
        User.setPassword("tmpPassword");
        User.setSecurityState(SecurityState.BASIC);
        User.setSecondPassword(null);

        UsmsUser newUser = new UsmsUser();
        newUser.setId(1L);
        newUser.setPersonName(dto.getPersonName());
        newUser.setEmail(dto.getEmail());
        newUser.setPhoneNumber(dto.getPhoneNumber());
        newUser.setPassword(dto.getPassword());
        newUser.setSecurityState(dto.getSecurityState());
        newUser.setSecondPassword(User.getSecondPassword());

        given(bCryptPasswordEncoder.encode(any())).willReturn(dto.getPassword());
        given(repository.findById(1L)).willReturn(Optional.of(User));

        Assertions.assertThat(userService.ModifyUser(1L,dto)).isInstanceOf(UsmsUser.class);
        Assertions.assertThat(userService.ModifyUser(1L,dto).toString()).isEqualTo(newUser.toString());
    }

    @DisplayName("ModifyUserDto에 필드값이 다 들어가지 않아도 성공적으로 수정기능이 작동한 경우 UsmsUser을 리턴한다.(이 경우에는 비밀번호를 제외했다)")
    @Test
    public void SuccessModifyUserWithsomeArgforDto(){
        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("hihello")
                .email("asdf123@naer.com")
                .phoneNumber("010-1234-2412")
                .securityState(SecurityState.BASIC)
                .build();

        UsmsUser User = new UsmsUser();
        User.setId(1L);
        User.setPersonName("tmpName");
        User.setEmail("tmp123@naver.com");
        User.setPhoneNumber("010-1234-4242");
        User.setPassword("tmpPassword");
        User.setSecurityState(SecurityState.BASIC);
        User.setSecondPassword(null);

        UsmsUser newUser = new UsmsUser();
        newUser.setId(1L);
        newUser.setPersonName(dto.getPersonName());
        newUser.setEmail(dto.getEmail());
        newUser.setPhoneNumber(dto.getPhoneNumber());
        newUser.setPassword(User.getPassword());
        newUser.setSecurityState(dto.getSecurityState());
        newUser.setSecondPassword(User.getSecondPassword());

        given(repository.findById(1L)).willReturn(Optional.of(User));

        Assertions.assertThat(userService.ModifyUser(1L,dto)).isInstanceOf(UsmsUser.class);
        Assertions.assertThat(userService.ModifyUser(1L,dto).toString()).isEqualTo(newUser.toString());
    }



    @DisplayName("들어온 값으로 유저정보를 찾을수 없는 경우 IllagalArgumentException을 던진다.")
    @Test
    public void FailedModifyUserWithNotAllowedValue(){


        HttpRequestModifyUserDto dto = HttpRequestModifyUserDto.builder()
                .personName("hihello")
                .email("asdf123@naer.com")
                .phoneNumber("010-1234-2412")
                .password("hashedpassword123@")
                .securityState(SecurityState.BASIC)
                .build();

        given(repository.findById(1L)).willReturn(Optional.ofNullable(any()));
        assertThrows(IllegalArgumentException.class , () -> userService.ModifyUser(1L,dto));
    }


}
