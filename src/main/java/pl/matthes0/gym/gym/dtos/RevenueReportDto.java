package pl.matthes0.gym.gym.dtos;

import pl.matthes0.gym.membershipplan.Price;

public record RevenueReportDto(
        String gymName,
        Price revenue) {
}
