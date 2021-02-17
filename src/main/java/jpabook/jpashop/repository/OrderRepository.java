package jpabook.jpashop.repository;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

/**
 * repository 는 순수 entity를 조회하는데 사용
 */
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

    public List<Order> findAllByString(OrderSearch orderSearch) { //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                                    .setMaxResults(1000); //최대 1000건

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);

        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    // 컬렉션 패치 조인 (일대다)
    // 컬렉션 패치 조인은 1개만 사용. 컬렉션 둘 이상에 패치조인을 사용하면 데이터 정합성이 틀어질 수 있음.
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" + // XToOne 관계는 row 수를 증가시키지 않으니 fetch 조인 사용 가능
                        " join fetch  o.delivery d" +
                        " join fetch o.orderItems oi" + // 컬렌션은 지연로딩을 조회 해야함...
                        " join fetch oi.item i", Order.class
        )//.setFirstResult(1).setMaxResults(100)// 일대다 fetch join 에서 hibernate가 경고를 내고 메모리에 다 들고온다음 페이징함
                 .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
                ).setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
