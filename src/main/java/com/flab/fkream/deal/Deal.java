package com.flab.fkream.deal;

import com.flab.fkream.item.Item;
import com.flab.fkream.user.User;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Deal {

    private Long id;
    @NotNull
    private Item item;
    @NotNull
    private KindOfDeal kindOfDeal;
    @NotNull
    private Long userId;
    @NotNull
    private int price;
    @NotNull
    private String size;
    @NotNull
    private LocalDate period;
    @AssertTrue
    private boolean utilizationPolicy;
    @AssertTrue
    private boolean salesCondition;
    private Status status;

    @Setter
    private Long otherId;

    private LocalDateTime createdAt;


    public void setKindOfDealToSale(){
        kindOfDeal = KindOfDeal.SALE;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setKindOfDealToPurchase() {
        kindOfDeal = KindOfDeal.PURCHASE;
    }

    public void setCreatedAtToNow(){
        createdAt = LocalDateTime.now();
    }
}
