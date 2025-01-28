<div align=center><h1>SpringBasic</h1></div>
 <img src="https://img.shields.io/badge/spring-6DB33F?style=flat&logo=Spring&logoColor=white"/>
 
> Springboot examples for beginner

<h2>스프링부트 강좌를 다시 정리하면서 배우기 합니다.:smile: </h2> 

스프링의 진짜 핵심   
  스프링은 자바 언어 기반의 프레임워크입니다.  
  자바 언어의 가장 큰 특징?  
  객체 지향 언어
  스프링은 객체 지향 언어가 가진 강력한 특징을 살려내는 프레임워크 


현 상태 :  주문과 할인 도메인 설계   
  주문과 할인 정책  
  –	회원은 상품을 주문할 수 있다.  
  –	회원 등급에 따라 할인 정책을 적용할 수 있다.  
  –	할인 정책은 모든 VIP 는 1000원을 할인해주는 고정 금액 할인을 적용해달라.(나중에 변경 될 수 있다.)  
  –	할인 정책은 변경 가능성이 높다. 회사의 기본 할인 정책을 아직 정하지 못했고, 오픈 직전까지 고민을 미루고싶다.     
     최악의 경우 할인을 적용하지 않을 수 도 있다. (미확정)  

* 클라이언트   
  1. 주문 생성 - 회원id - 상풍명 - 상품 가격 - 주문 서비스 역할  
  2. 회원 조회 - 회원 저장소 역할   
  3. 할인 적용 - 할인 정책 역할  
  4.	주문 결과 반환

- [1]	주문 생성: 클라이언트는 주문 서비스에 주문 생성을 요청한다.
- [2] 회원 조회: 할인을 위해서는 회원 등급이 필요하다. 그래서 주문 서비스는 회원 저장소에서 회원을 조회한다.
- [3] 할인 적용: 주문 서비스는 회원 등급에 따른 할인 여부를 할인 정책에 위임한다.
- [4] 주문 결과 반환: 주문 서비스는 할인 결과를 포함한 주문 결과를 반환한다.

  
참고: 실제로는 주문 데이터를 db에 저장하겠지만, 예제가 너무 복잡해 질수 잇어서 생략하고 단순히 주문 결과를 반환한다.
역할 과 구현을 분맇해서 자유롭게 구현 객체를 조립할 수 잇게 설계한다. 덕분에 회원 저장소는 물론이고, 할인 정책도 유연하게 변경할 수 있다. 

좋은 객체 지향 설계의 5가지 원칙의 적용 에 대한 글 귀 

순수한 자바 코드만으로 DI를 적용했다. 이제 스프링으로 작성해보자 

AppConfig 에  @configuration  넣어주고 각 메서드에  @bean  으로 넣어주면 알아서 bean 객체로 실행됨 
스프링 컨테이너 
applicationContext를 스프링 컨테이너라 한다. 
기존에는 개발자가 appconfig를 사용해서 직접 객체를 생성하고 DI를 했지만,

<div><h3>이제부터는 스프링 컨테이너를 통해서 사용한다.</h3></div>

스프링 컨테이너는  **@configuration**  이 붙은 appConfig를 설정 정보로 사용한다. 

여기서  **@bean**  이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 

이렇게 스프링 컨테이너에 등록된 객체를 스프링빈이라 한다. 
스프링빈은 **@bean**   분은 메서드의 명을 스프링 빈의 이름으로 사용한다. 

이전에는 개발자가 필요한 객체를 AppConfig를 사용해서 직접 조회했지만, 
이제부터는 스프링 컨테이너를 통해서 필요한 스프링 빈 (객체)를 찾아야 한다. 

**스프링 빈은 applicaitonContext.getBean()메서드를 사용해서 찾을수있다.**

기존에는 개발자가 직접 자바코드로 모든 것을 했다면 이때부터 는 스프링 컨테이너에 객체를 스프링 빈으로 등록하고, 
스프링컨테이너에서 스프링 빈을 찾아서 사용하도록 변경되었다.
<h2>코드가 약간 더 복잡해진 것 같은데, 스프링 컨테이너를 사용하면 어떤 장점이 있을까?</h2>
–	개발을 할때 할게 많다. 스프링컨테이너가 관리해주므로써 기능이 많아진다. 


</br>

> 스레드 풀


* 스레드풀의 적정 숫자
  * 적정 숫자는 어떻게 찾나요?
  * 애플리케이션 로직의 복잡도, cpu, 메모리, IO 리소스 상황에 따라 모두 다르다.
  * 성능 테스트
    * 최대한 실제 서비스와 유사하게 성능 테스트 시도
    * 툴 : 아파치 ab, 제이미터, nGrinder

WAS 의 멀티 쓰레드 지원
- 핵심
  - 멀티 쓰레드에 대한 부분은 WAS가 처리
  - 개발자가 멀티 쓰레드 관련 코드를 신경쓰지 않아도 됨
  - 개발자는 마치 싱글 쓰레드 프로그래밍을 하듯이 편리하게 소스 코드를 개발
  - 멀티 쓰레드 환경이므로 싱글톤 객체(서블릿, 스프링 빈)는 주의해서 사용

# 서버사이드 랜더링, 클라이언트 사이드 렌더링
  - SSR - 서버 사이드 렌더링
    - HTML 최종 결과를 서버에서 만들어서 웹 브라우저에 전달
    - 주로 정적인 화면에 사용
    - 관련기술 : jsp, 타임리프 -> 백엔드 개발자
  - CSR - 클라이언트 사이드 렌더링
    - HTML 결과를 자바스크립ㅇ를 사용해 웹 브라우저에서 동적으로 생성해서 적용
    - 주로 동적인 화면에 사용, 웹 환경을 마치 앱 처럼 필요한 부분부분 변경할 수 있음
    - 구글지도, gmail, 구글 캘린더
    - 관련기술: React, Vue,js -> 웹 프론트엔드 개발자
  - 참고
    - React, veu.js를 CSR+SSR 동시에 지원하는 웹 프레임워크도 있음
    - SSR을 사용하더라도, 자바스크립트를 사용해서 화면 일부를 동적으로 변경 가능

## 스프링 웹 플럭스(WebFlux)
  - 특징
    - 비동기 non-blocking 처리
    - 최소 쓰레드로 최대 성능 - 쓰레드 컨텍스트 스위칭 비용 효율화
    - 함수형 스타일로 개발 - 동시 처리 코드 효율화
    - 서블릿 기술 사용 X
  - 단점
    - 웹 플럭스는 기술적 난이도 매우 높음
    - 아직은 RDB 지원 부족
    - 일반 MVC의 쓰레드 모델로 충분히 빠르다.
    - 실무에서 아직 많이 사용하지는 않음(전체 1% 이하)
    

현재 진형중인 사항 2025년 01-29
김영한 스프링 mvc 1편을 보고있습니다. 
앞으로의 계획은 
스프링 mvc 1,2 편을 2월안으로 다보고 그 후 jpa강의 수강 이후 엘라스틱서치 강의를 마져 보고 오겠습니다.