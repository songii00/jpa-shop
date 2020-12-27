package jpabook.jpashop.domain;

import javax.persistence.Embeddable;

import lombok.Getter;

@Embeddable
@Getter
public enum DeliveryStatus {
    READY, COMP;

    public boolean isComp() {
        return this == COMP;
    }
}
