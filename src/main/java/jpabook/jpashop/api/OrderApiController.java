package jpabook.jpashop.api;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream().map(OrderDto::new).collect(toList());
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream().map(OrderDto::new).collect(toList());
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit){
        // toOne 관계는 페이징에 영향을 주지 않으니 한방쿼리로 가져옴(패치조인)
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        // default_batch_fetch_size 를 이용해서 in query 로 조회
        return orders.stream().map(OrderDto::new).collect(toList());
    }

    // 쿼리 N+1
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit){

        return orderQueryRepository.findOrderQueryDtos();
    }


    // 쿼리 2방
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit){

        return orderQueryRepository.findAllByDto_optimization();
    }

    // 쿼리 2방
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit){

        return orderQueryRepository.findAllByDto_flat();
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------
    // -- static class
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // value 는 괜찮음
        private List<OrderItemDto> orderItems;
        // private List<OrderItem> orderItems; << entity 외부로 노출하지 않기!! 속에있는 것도!!

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // Lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Lazy 초기화
            order.getOrderItems().forEach(o -> o.getItem().getName());
            orderItems = order.getOrderItems().stream()
                              .map(item -> new OrderItemDto(item.getItem().getName(), item.getOrderPrice(), item.getCount()))
                              .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private final String itemName;
        private final int orderPrice;
        private final int count;

        public OrderItemDto(String itemName, int orderPrice, int count) {
            this.itemName = itemName;
            this.orderPrice = orderPrice;
            this.count = count;
        }
    }
}
