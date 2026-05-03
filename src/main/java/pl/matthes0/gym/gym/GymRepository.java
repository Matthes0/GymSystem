package pl.matthes0.gym.gym;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.matthes0.gym.gym.dtos.RevenueReportDto;

import java.util.List;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {

    @Query( "SELECT new pl.matthes0.gym.gym.dtos.RevenueReportDto(" +
            "g.name, " +
            "new pl.matthes0.gym.membershipplan.Price(SUM(p.monthlyPrice.amount), p.monthlyPrice.currency))" +
            "FROM Gym g " +
            "JOIN g.membershipPlans p " +
            "JOIN p.members m " +
            "WHERE m.status = pl.matthes0.gym.member.Status.ACTIVE " +
            "GROUP BY g.name, p.monthlyPrice.currency")
    List<RevenueReportDto> getRevenueReport();
}
