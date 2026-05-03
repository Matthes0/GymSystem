package pl.matthes0.gym.membershipplan;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotNull(message = "Currency is required")
    private Currency currency;
}
