package jpabook.jpashop.repository;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    // todo queryDsl 로 변경
    public List<Order> findAll(OrderSearch orderSearch) {
        String jpql = "select o from Order o join o.member m"
                + " where o.status = :status";
        return em.createQuery(jpql
                + " and m.name like :name", Order.class)
                                   .setParameter("status", orderSearch.getOrderStatus())
                                   .setParameter("name", orderSearch.getMemberName())
                                   .setFirstResult(100)
                                   .setMaxResults(1000)
                                   .getResultList();

    }
}
