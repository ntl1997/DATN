package com.poly.viettutor.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private Double totalAmount;

    private String couponCode;

    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;


    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)

    private List<OrderDetail> orderDetails;
}
