package jpabook.jpashop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

import jpabook.jpashop.domain.item.Item;

@Entity
@Getter @Setter
public class OrderItem {

    @GeneratedValue @Id
    @Column(name = "order_item_id")
    private Long id;

    // XXToOne 은 기본 fetch 전략이 EAGER !!!! 전부 LAZY로 변경 해야 함!!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

}
