# SSG-USMS-business-service

> 무인 매장 관리 시스템 비즈니스 서비스

## 프로젝트 소개

SSG-USMS-business-service는 무인 매장을 안전하고 효율적으로 운영하기 위한 통합 관리 플랫폼 CCTV 기반 실시간 모니터링, AI 사고 감지, 다채널 알림 시스템을 통해 무인 매장의 보안과 운영 관리를 지원합니다.

## 주요 기능

- **회원 관리 및 JWT 인증**: 안전한 토큰 기반 인증 시스템과 역할 기반 접근 제어
- **다중 인증 시스템**: SMS/Email 기반 본인 인증
- **매장 관리**: 매장 등록, 사업자 등록증 관리, 승인 워크플로우
- **CCTV 관리**: CCTV 등록, 스트림 키 생성, 연결 상태 모니터링
- **비디오 스트리밍**: 실시간 CCTV 스트리밍 및 녹화 영상 재생
- **사고 감지 및 알림**: 7가지 이상 행동 패턴 감지 (출입, 싸움, 절도, 파손 등)
- **다채널 알림 시스템**: Firebase Push, Email, SMS를 통한 실시간 알림
- **지역별 경고 시스템**: 스케줄링 기반 지역 사고 통계 분석 및 경고

## 담당 기능
- **CI/CD**: 자동화된 빌드 및 배포 파이프라인
- **회원 서비스**
  - 회원가입 및 로그인
  - 역할 기반 접근 제어 (Admin, Store Owner)
- **인증 서비스**
  - SMS 인증 (Nurigo SDK)
  - Email 인증 (SMTP)
  - Redis 기반 인증 코드 관리
- **알림 서비스**
  - Firebase Cloud Messaging (FCM) 앱 푸시 알림
  - 비동기 알림 처리 (ThreadPoolTaskExecutor)
  - 디바이스 토큰 관리

## 기술 스택

### Backend
- **Java 11**
- **Spring Boot 2.7.18**
- **Spring Data JPA** + **Hibernate**
- **Spring Security**
- **Spring Session** (Redis 기반 세션 관리)
- **Spring Cache** (Redis 캐싱)

### Database
- **MariaDB** (운영 DB)
- **H2 Database** (테스트 DB)
- **Redis** (캐싱, 세션 관리, 스트림 키 추적)

### Infrastructure
- **AWS S3** (이미지 및 비디오 파일 저장)
- **Firebase Cloud Messaging** (푸시 알림)
- **Nurigo SDK** (SMS 인증)
- **Spring Mail** (Email 발송)

### Testing
- **JUnit 5** + **Spring Boot Test**
- **Embedded Redis** (테스트용 Redis)
- **S3Mock** (테스트용 S3)

## 아키텍처
- **계층형 아키텍처** (Controller → Service → Repository)
- **역할 기반 접근 제어** (RBAC)
- **비동기 이벤트 처리** (알림, 스케줄링)

![SSGproject](https://github.com/user-attachments/assets/5fda79b3-27bd-4f12-8870-cbbaec89eab3)

## 해결한 부분

### 1️⃣ 다중 인스턴스상 세션 유실 문제

**문제:** 다중 인스턴스 상에서 사용자의 로그인 세션이 유실되는 문제가 발생했다.

**원인:** 스프링 시큐리티는 기본적으로 서버 내부 메모리에 (시큐리티 컨텍스트홀더) 저장한다 따라서 동일 컴퓨터 내에 두가지 서버를 구동했을때 하나의 서버에 로그인을 완료하였을때 다른 서버로 요청이 가게 되면 세션이 유실되는 문제가 발생한다

**해결:**

- 세션을 유지하는 방식이 필요했다. 첫번째로는 세션을 저장한 서버로 요청을 보내는 Sticky Session 방식
- 두번째로는 외부 세션 저장소에 세션을 저장하고 이를 다른 서버들이 공유하는 방식을 사용해야했다.
- Sticky Session 방식을 사용한다면 한개의 서버로 트래픽이 몰릴 가능성이 있기 때문에 세션 저장소를 사용하였다.
- 팀원과의 협의 하에 Redis를 세션 저장소로 사용하기로 합의하였다. 추후에 본인인증 시에도 Redis를 사용 및 캐싱기능에도 이를 도입하기 위해서였다.
- 스프링에서도 이를 편리하게 지원해주는 Spring Session을 사용하여 문제를 해결했다.

**핵심 코드:**

의존성을 추가하고

```gradle
implementation 'org.springframework.session:spring-session-data-redis'
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

다음과 같이 `@EnableRedisHttpSession`을 통해 활성화 해줬다.

```java
@Configuration
@EnableRedisHttpSession
public class RedisConfiguration {...}
```

기존 HttpSession 처리 과정을 무시하고 SessionRepositoryFilter라는 필터를 대신 등록함으로써 요청이 들어오면 Redis에서 세션을 가져오고, 로그인 정보도 Redis에 자동으로 저장할 수 있도록 해주기 때문에 시큐리티 쪽의 설정은 필요없었다.

**결과:** 다중 인스턴스 상에서 세션 유실 문제를 해결할 수 있었다.

**아쉬웠던점:** 세션을 사용한 이유에는 사용자의 로그인 정보를 관리자 측에서 편하게 관리하기 위해서였다. 하지만 JWT방식에도 Refresh토큰 및 블랙리스트 기법을 활용하면 동일한 효과를 볼 수 있었지 않았을까? 생각이든다.

---

### 2️⃣ 다중 디바이스 푸시 알림 처리 지연 문제

**문제:** 사고 감지 시 여러 사용자의 모든 디바이스에 푸시 알림을 전송할 때 순차 처리로 인한 지연 발생

**원인:** 동기 방식으로 FCM API를 호출하여 디바이스 수에 비례하여 처리시간이 증가하게 되었다.

**해결:**

- 사용자의 디바이스에 알림을 전송하는 절차는 응답을 기다릴 필요가 없다고 생각했다.
- 따라서 동기식 처리가 아닌 비동기식으로 알림을 전송한 후 성공 여부나 응답을 기다릴 필요 없이 해당 응답을 반환하면 되겠다고 생각했다.
- 그래서 스레드풀을 정의하고 `@Async` 어노테이션을 사용해 해당 로직을 비동기 처리 방식으로 전환하여 해결하였다.

**핵심 코드:**

```java
@Async("threadPoolTaskExecutor")
public void send(String targetToken, String title, String message) {
    Message fcmMessage = Message.builder()
        .setToken(targetToken)
        .setNotification(Notification.builder()
            .setTitle(title)
            .setBody(message)
            .build())
        .build();

    FirebaseMessaging.getInstance().send(fcmMessage);
}
```

**결과:** 알림 전송이 메인 로직을 블로킹하지 않아 API 응답 속도가 개선

---

### 3️⃣ 알림 수단 추가시 불필요한 코드 변경

**문제:** 알림 수단이 추가되었을때 불필요하게 서비스측에서 코드를 추가해줘야 했다.

예를 들어 다음과 같이 FirebaseNotificationService가 추가되었을때 저렇게 추가하고 또 다른 알림 서비스가 들어오면 또 추가해주는 방식

```java
@RequiredArgsConstructor
public class AccidentService {
    private final FirebaseNotificationService firebaseService;
    private final SmsNotificationService smsService;
    ...
}
```

**해결:**

- Spring에서는 인터페이스 타입으로 빈을 주입받을 수 있다.
- 이를 기반으로 전략패턴을 적용해 알림 인터페이스를 만들고 추후에 추가되는 알림 서비스들이 이를 구현하도록 만들었다.

**핵심 코드:**

```java
@RequiredArgsConstructor
public class AccidentService {
    private final List<NotificationService> notificationService;
    ...
}
```

추후에 알림 서비스가 새로 추가되어도 해당 코드를 변경하지 않아도 되게 되었다. email 서비스와 같은 개별 알림 서비스가 필요하다면 HashMap 또는 빈 이름 생성 규칙에 따라 변수명을 정해주면 되었다.

**결과:** 코드의 확장성이 증가하였다

---

### 4️⃣ Spring Validation을 이용한 검증 시 불필요한 중복

**문제:** Spring Validation을 이용한 검증 시에 여러 요청 객체들에 `@Pattern("정규표현식")`과 같은 중복된 로직들이 많았고, 이게 어떤 타입을 검증하는 로직인지 구분이 힘들었다.

```java
@Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
        message = "전화번호 형식이 올바르지 않습니다.")
private final String phoneNumber;
```

**해결:**

- Spring Validation은 우리가 만든 커스텀 어노테이션을 만들어 검증을 진행할 수 있도록 지원해준다는 사실을 알게 되었다.
- 때문에 다음과 같은 어노테이션을 정의하고 그에 맞는 Validator 클래스를 만들었고 이를 요청 객체에 `@PhoneNumber` 형식으로 적용해줬다.

**핵심 코드:**

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {

    String message() default "전화번호 형식이 일치하지않습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (!isValidPhoneNumber(phoneNumber)) {
            return false;
        }
        return true;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONENUMBER_PATTERN);
    }
}
```

이전 코드:

```java
@Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
        message = "전화번호 형식이 올바르지 않습니다.")
private final String phoneNumber;
```

개선 후:

```java
@PhoneNumber
private final String phoneNumber;
```

**결과:** 코드의 가독성이 높아졌고 재사용성이 증가했다

---


## 프로젝트 구조

```
src/main/java/com/ssg/usms/business/
├── user/                      # 사용자 관리
│   ├── controller/           # 회원가입, 로그인 API
│   ├── service/              # 사용자 비즈니스 로직
│   ├── repository/           # 사용자 데이터 접근
│   └── dto/                  # 사용자 DTO
├── security/                  # 보안 및 인증
│   ├── login/                # 커스텀 로그인 필터 및 핸들러
│   └── logout/               # 로그아웃 처리
├── Identification/            # 본인 인증
│   ├── service/              # SMS/Email 인증 코드 발송/검증
│   └── repository/           # Redis 기반 인증 코드 저장소
├── notification/              # 알림 시스템
│   ├── service/              # Firebase, Email, SMS 알림 서비스
│   └── dto/                  # FCM 메시지 DTO
├── device/                    # 디바이스 관리
│   ├── service/              # FCM 토큰 등록/해제
│   └── repository/           # 디바이스 데이터 접근
├── store/                     # 매장 관리
│   ├── controller/           # 매장 CRUD API
│   ├── service/              # 매장 비즈니스 로직
│   ├── repository/           # 매장 데이터 접근
│   └── dto/                  # 매장 DTO
├── cctv/                      # CCTV 관리
│   ├── controller/           # CCTV CRUD API
│   ├── service/              # 스트림 키 생성 및 관리
│   └── repository/           # CCTV 데이터 접근
├── video/                     # 비디오 스트리밍
│   ├── controller/           # 실시간/녹화 영상 API
│   ├── service/              # 비디오 URL 리디렉션 및 파일 조회
│   └── repository/           # S3 비디오 저장소, Redis 스트림 키 저장소
├── accident/                  # 사고 감지
│   ├── controller/           # 사고 기록 및 조회 API
│   ├── service/              # 사고 데이터 처리
│   └── repository/           # 사고 데이터 접근
├── warning/                   # 경고 시스템
│   ├── service/              # 지역별 사고 통계 및 경고 생성
│   └── repository/           # 지역별 사고 조회
└── config/                    # 전역 설정
    ├── SecurityConfig         # Spring Security 설정
    ├── RedisConfiguration     # Redis 연결 및 캐시 설정
    ├── AwsConfiguration       # AWS S3 설정
    └── AsyncConfiguration     # 비동기 스레드 풀 설정
```
