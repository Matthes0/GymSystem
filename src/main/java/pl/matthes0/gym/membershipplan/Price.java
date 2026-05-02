package pl.matthes0.gym.membershipplan;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Price {

    @Column(nullable = false, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(nullable = false)
    private Currency currency;
}
