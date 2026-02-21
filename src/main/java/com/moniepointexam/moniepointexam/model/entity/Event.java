package com.moniepointexam.moniepointexam.model.entity;

import com.moniepointexam.moniepointexam.model.enums.Channel;
import com.moniepointexam.moniepointexam.model.enums.MerchantTier;
import com.moniepointexam.moniepointexam.model.enums.Product;
import com.moniepointexam.moniepointexam.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue
    @Column(name = "event_id", updatable = false, nullable = false)
    private UUID eventId;

    @Column(name = "merchant_id", nullable = false, length = 20)
    private String merchantId;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;   //this is for the eventtimestampp

    @Enumerated(EnumType.STRING)
    @Column(name = "product", nullable = false, length = 20)
    private Product product;       //this is for the product enum

    @Column(name = "event_type", nullable = false, length = 30)
    private String eventType;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status;// Status enum

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 10)
    private Channel channel;        // Status Channel

    @Column(name = "region", length = 50)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(name = "merchant_tier", length = 10)
    private MerchantTier merchantTier;
}