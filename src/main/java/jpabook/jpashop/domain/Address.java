package jpabook.jpashop.domain;


import javax.persistence.Embeddable;

import lombok.Getter;

/**
 * 값 타입은 변경 불가능 하게 설계해야 함
 * Setter를 제거하고 생성자에서 값을 초기화 해서 변경 불가능한 클래스 만들자.
 * JPA 스펙 상 엔티티나 임베디드 타입(@Embeddable)은 자바 기본 생성자를 public, protected로 설정해야 함
 * public 보다 protected로 설정하여 안전하게 설계하자
 */
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() { }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
