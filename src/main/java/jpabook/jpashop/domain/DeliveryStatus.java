package jpabook.jpashop.domain;

import javax.persistence.Embeddable;

@Embeddable
public enum DeliveryStatus {
    READY, COMP
}
