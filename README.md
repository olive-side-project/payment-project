# [ Payment 프로젝트 ] <br><br>

<div align=center><h1>📚 기술스택 </h1></div><br>
<div align=center> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
    <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"> 
  <img src="https://img.shields.io/badge/aws_lightsail-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white"> 
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
</div>

<br><br><br>

<div align=center><h1>💻 구현 상세 설명 </h1></div><br>

### 1. TOSS 결제 API 구현

- 확장성을 위해 추상클래스와 인터페이스를 사용하고 <br> 
- 동시성 처리를 위해 Redis를 활용하여 결제 요청의 상태 관리와 잠금 설정을 수행했습니다. <br><br>


### 2. 로그인 기능
- JWT로 발행한 토큰을 사용하였습니다. <br>
- Redis를 활용해 세션 정보를 저장하고 만료 시간을 설정함으로써
  - 동시성 처리와 신속한 세션 관리의 장점을 확보했습니다. <br>
- 인증 필터와 API 권한 인터셉터를 통해 요청 시 인증 및 권한 체크를 수행하여 보안을 강화했습니다. <br><br>


### 3. 멀티모듈 구조
- 공통 기능은 common 모듈
- 결제 관련 로직은 payment 모듈
- 사용자 관리 기능을 user 모듈에 나누어 구현함으로써 코드의 재사용성과 유지 보수성을 높였습니다. <br><br>


### 4. 로깅 및 모니터링
- 인터셉터와 필터를 활용하여 요청 및 응답 로그를 기록하였습니다. <br>
- MDC를 활용하여 각 쓰레드별로 로그를 남겨
  - 동시 실행 시에도 로그 추적이 가능하게 하여 디버깅 효율성을 높였습니다. <br>
- 로그백을 멀티프로파일로 설정하여, 환경에 따라 로그 수준을 조정하고
  - 필요한 정보만 효과적으로 수집하도록 하였습니다. <br><br>

<br><br>

<div align=center><h1>✍ 관련 블로깅 </h1></div><br>

[AWS Lightsail로 HTTPS 서버를 구축해보다.](https://domean.tistory.com/327)
  

