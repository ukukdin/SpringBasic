package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;

public class OrderServiceImpl  implements OrderService{
    /**
     * 인터페이스에만 의존하도록 설계를 변경했다.
     *
     * */
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

   /*
    인터페이스에만 의존하도록 다시 설계를 하면서 이전 코드를 주석 처리했습니다.
    private  final DiscountPoilicy discountPolicy = new FixDiscountPolicy();
    private  final DiscountPoilicy discountPolicy = new RateDiscountPolicy();*/

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice){
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId,itemName,itemPrice,discountPrice);
    }

}
