<div align=center><h1>SpringBasic</h1></div>
 <img src="https://img.shields.io/badge/spring-6DB33F?style=flat&logo=Spring&logoColor=white"/>
 
> Springboot examples for beginner

<h2>본 코드는 김영한의 스프링 강좌를 베이스로 합니다.:smile: </h2> 

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
